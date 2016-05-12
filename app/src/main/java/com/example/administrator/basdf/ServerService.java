package com.example.administrator.basdf;

import android.app.IntentService;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Administrator on 2016-05-10.
 */
public class ServerService extends IntentService {
    MediaPlayer mp;
    private boolean serviceEnabled;

    private int port;
    private File saveLocation;
    private ResultReceiver serverResult;

    public ServerService() {
        super("ServerService");
        serviceEnabled = true;


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {



        port = ((Integer) intent.getExtras().get("port")).intValue();
        saveLocation = (File) intent.getExtras().get("saveLocation");
        serverResult = (ResultReceiver) intent.getExtras().get("serverResult");


        //signalActivity("Starting to download");


        String fileName = "";

        ServerSocket welcomeSocket = null;
        Socket socket = null;

        try {



            welcomeSocket = new ServerSocket(port);

            while(true && serviceEnabled)
            {

                //Listen for incoming connections on specified port
                //Block thread until someone connects
                socket = welcomeSocket.accept();

                //signalActivity("TCP Connection Established: " + socket.toString() + " Starting file transfer");


                DataInputStream dis = new DataInputStream(socket.getInputStream());
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                OutputStream os = socket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);


                String inputData = "";



                signalActivity("About to start handshake");
                //Client-Server handshake

				/*
				String test = "Y";
				test = test + br.readLine() + test;


				signalActivity(test);
				 */

				/*
				inputData = br.readLine();

				if(!inputData.equals("wdft_client_hello"))
				{
					throw new IOException("Invalid WDFT protocol message");

				}

				pw.println("wdft_server_hello");


				inputData = br.readLine();


				if(inputData == null)
				{
					throw new IOException("File name was null");

				}


				fileName = inputData;

				pw.println("wdft_server_ready");

				*/

                //signalActivity("Handshake complete, getting file: " + fileName);
                String fName = dis.readUTF();
                //String savedAs = "WDFL_File_" + System.currentTimeMillis();
                File file = new File(saveLocation, fName);

                byte[] buffer = new byte[4096];
                int bytesRead;

                FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos);

                while(true)
                {
                    bytesRead = is.read(buffer, 0, buffer.length);
                    if(bytesRead == -1)
                    {
                        break;
                    }
                    bos.write(buffer, 0, bytesRead);
                    bos.flush();

                }


			    /*
			    fos.close();
			    bos.close();

			    br.close();
			    isr.close();
			    is.close();

			    pw.close();
			    os.close();

			    socket.close();
			    */

                bos.close();
                socket.close();


                mp = MediaPlayer.create(getApplicationContext(), Uri.parse(file.getAbsolutePath()));
                mp.setLooping(true);
                mp.start();

                signalActivity("File Transfer Complete, saved as: " + fName);
                //Start writing to file

            }


        } catch (IOException e) {
            signalActivity(e.getMessage());


        }
        catch(Exception e)
        {
            signalActivity(e.getMessage());

        }

        //Signal that operation is complete
        serverResult.send(port, null);





    }


    public void signalActivity(String message)
    {
        Bundle b = new Bundle();
        b.putString("message", message);
        serverResult.send(port, b);
    }


    public void onDestroy()
    {
        serviceEnabled = false;

        //Signal that the service was stopped
        //serverResult.send(port, new Bundle());

        stopSelf();
    }

}
