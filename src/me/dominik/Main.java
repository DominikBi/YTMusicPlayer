package me.dominik;

import javax.print.DocFlavor;
import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Scanner;

public class Main {

    SourceDataLine speaker;
    Socket server;
    InputStream inputStream;
    boolean running = true;
    URL url;
    AudioFormat audioFormat = new AudioFormat(8000.F,16,1,true,false);
    DataLine.Info datalineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

    {
        try {
            url = new URL("https://www.youtube.com/watch?v=PH8MIVuLz1g");
        } catch (MalformedURLException e) {
            System.err.println("Wrong Url");
        }
    }

    int port = url.getPort();

    String serverName = url.getHost();

    public static void main(String[] args) throws LineUnavailableException, IOException, UnsupportedAudioFileException {

        Main main = new Main();
        main.init();
        main.start();

    }
    public void init() throws LineUnavailableException {


        speaker = (SourceDataLine) AudioSystem.getLine(datalineInfo);
        speaker.open(audioFormat);


    }
    public void start() throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        System.out.println(url.toString());
        SourceDataLine line = null;
        AudioInputStream ain = AudioSystem.getAudioInputStream(url);


        try {


            if(!AudioSystem.isLineSupported(datalineInfo)){
                AudioFormat pcm =  new AudioFormat(audioFormat.getSampleRate(),16,audioFormat.getChannels(),true,false);
                ain = AudioSystem.getAudioInputStream(pcm, ain);
                audioFormat = ain.getFormat();
                datalineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            }
            line = (SourceDataLine) AudioSystem.getLine(datalineInfo);
            line.open(audioFormat);
            int framesize = audioFormat.getFrameSize();
            byte[] buffer = new byte[1024*4*framesize];
            int numbytes = 0;
            boolean startet = false;

            for(;;) {
                int bytesread = ain.read(buffer, numbytes, buffer.length - numbytes);
                if (bytesread == -1)
                    break;
                numbytes += bytesread;

                if (!startet) {
                    line.start();
                    startet = true;
                }
            }
                int bytesToWrite = (numbytes/framesize) * framesize;
                line.write(buffer,0,bytesToWrite);
                int remaning = numbytes - bytesToWrite;
                if(remaning > 0){
                    System.arraycopy(buffer,bytesToWrite,buffer,0,remaning);
                    numbytes = remaning;
                }
            speaker.write(buffer,0,buffer.length);
            speaker.start();
            line.drain();
            } finally {
            if(line != null) {
                line.close();
            }
            if(ain != null){
                ain.close();
            }

            }

    }

    }

