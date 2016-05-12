package com.example.administrator.basdf;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016-05-10.
 */
public class ClientService extends IntentService {


    private boolean serviceEnabled;

    private int port;
    private File fileToSend;
    private String filename;
    private ResultReceiver clientResult;
    private WifiP2pDevice targetDevice;
    private WifiP2pInfo wifiInfo;

    public ClientService() {
        super("ClientService");
        serviceEnabled = true;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Toast.makeText(this, "보내는부분시작" ,Toast.LENGTH_LONG).show();
        port = ((Integer) intent.getExtras().get("port")).intValue();
        fileToSend = (File) intent.getExtras().get("fileToSend");
        clientResult = (ResultReceiver) intent.getExtras().get("clientResult");
        filename = (String) intent.getExtras().get("filename");
        //targetDevice = (WifiP2pDevice) intent.getExtras().get("targetDevice");
        wifiInfo = (WifiP2pInfo) intent.getExtras().get("wifiInfo");

       // Toast.makeText(this, String.valueOf(wifiInfo.isGroupOwner) ,Toast.LENGTH_LONG).show();
        if(!wifiInfo.isGroupOwner)
        {
            //targetDevice.
            //signalActivity(wifiInfo.isGroupOwner + " Transfering file " + fileToSend.getName() + " to " + wifiInfo.groupOwnerAddress.toString()  + " on TCP Port: " + port );

            try {
                InetAddress targetIP = InetAddress.getByName("192.168.49.1");

            Socket clientSocket = null;
            OutputStream os = null;
            DataOutputStream dos = null;

            try {


                clientSocket =new Socket(targetIP, port);
                os = clientSocket.getOutputStream();
                PrintWriter pw = new PrintWriter(os);
                dos = new DataOutputStream(clientSocket.getOutputStream());


                InputStream is = clientSocket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                signalActivity("About to start handshake");



                //Client-Server handshake
				/*
				pw.println(fileToSend.getName());



				String inputData = "";

				pw.println("wdft_client_hello");

				inputData = br.readLine();

				if(!inputData.equals("wdft_server_hello"))
				{
					throw new IOException("Invalid WDFT protocol message");

				}


				pw.println(fileToSend.getName());

				if(!inputData.equals("wdft_server_ready"))
				{
					throw new IOException("Invalid WDFT protocol message");

				}

				*/

                //Handshake complete, start file transfer



                byte[] buffer = new byte[4096];

                FileInputStream fis = new FileInputStream(fileToSend);
                BufferedInputStream bis = new BufferedInputStream(fis);
                // long BytesToSend = fileToSend.length();
                dos.writeUTF(filename);
                while(true)
                {



                    int bytesRead = bis.read(buffer, 0, buffer.length);

                    if(bytesRead == -1)
                    {
                        break;
                    }

                    //BytesToSend = BytesToSend - bytesRead;
                    os.write(buffer,0, bytesRead);
                    os.flush();
                }



                fis.close();
                bis.close();

                br.close();
                isr.close();
                is.close();

                pw.close();
                os.close();


                clientSocket.close();

                signalActivity("File Transfer Complete, sent file: " + fileToSend.getName());


            } catch (IOException e) {
                signalActivity(e.getMessage());
            }
            catch(Exception e)
            {
                signalActivity(e.getMessage());

            }
            }catch (UnknownHostException e) {

            }

        }
        else
        {
            signalActivity("This device is a group owner, therefore the IP address of the " +
                    "target device cannot be determined. File transfer cannot continue");
        }


        clientResult.send(port, null);
    }


    public void signalActivity(String message)
    {
        Bundle b = new Bundle();
        b.putString("message", message);
        clientResult.send(port, b);
    }


    public void onDestroy()
    {
        serviceEnabled = false;

        //Signal that the service was stopped
        //serverResult.send(port, new Bundle());

        stopSelf();
    }

}
