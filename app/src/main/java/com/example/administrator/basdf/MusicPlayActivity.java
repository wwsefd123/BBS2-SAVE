package com.example.administrator.basdf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2016-05-04.
 */
public class MusicPlayActivity extends Activity {

    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    int port = 7950;
    WifiP2pInfo wifiP2pInfo;
    Intent clientServiceIntent;
    Intent serverServiceIntent;

    boolean serverThreadActive;
    boolean transferActive;
    File fileToSend;
    File downloadTarget;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);


        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, this.getMainLooper(), null);
        manager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                wifiP2pInfo = info;
            }
        });

        transferActive = false;
        serverThreadActive = false;
        //이거 WifiP2pInfo 제대로 넘겨주나 확인해볼것 필수임... 제대로 안넘어가서 안되는것일수도있음
        //Toast.makeText(this, wifiP2pInfo.groupOwnerAddress.getHostAddress(),Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.FILE_REQUEST :
                if(resultCode == RESULT_OK) {
                    fileToSend = (File) data.getExtras().get("file");
                    downloadTarget = (File) data.getExtras().get("file");
                    Toast.makeText(this, "file이 지정되었다." ,Toast.LENGTH_LONG).show();
                }
                break;
            case Constants.MUSIC_REQUEST :
                if(resultCode == RESULT_OK){
                    fileToSend = (File) data.getExtras().get("file");
                    downloadTarget = (File) data.getExtras().get("file");
                    Toast.makeText(this, "음악파일이 지정되었다." ,Toast.LENGTH_LONG).show();
                    //sendFile(findViewById(R.id.sendfle));
                }
                break;
        }
    }

    void selectFile(View view) {
        Intent intent = new Intent(this, FileBrowser.class);
        startActivityForResult(intent, Constants.FILE_REQUEST);
    }

    void selectMusic(View view) {
        Intent intent = new Intent(this, MusicActivity.class);
        startActivityForResult(intent, Constants.MUSIC_REQUEST);
    }



    public void sendFile(View view) {

        //Only try to send file if there isn't already a transfer active
        if(!transferActive)
        {

                //Launch client service
                clientServiceIntent = new Intent(this, ClientService.class);
                clientServiceIntent.putExtra("fileToSend", fileToSend);
                clientServiceIntent.putExtra("port", Integer.valueOf(port));
                clientServiceIntent.putExtra("filename",fileToSend.getName());
                //clientServiceIntent.putExtra("targetDevice", targetDevice);
                clientServiceIntent.putExtra("wifiInfo",wifiP2pInfo);
                clientServiceIntent.putExtra("clientResult", new ResultReceiver(null) {
                    @Override
                    protected void onReceiveResult(int resultCode, final Bundle resultData) {

                        if(resultCode == port )
                        {
                            if (resultData == null) {
                                //Client service has shut down, the transfer may or may not have been successful. Refer to message
                                transferActive = false;
                            }
                            else
                            {
                                final TextView client_status_text = (TextView) findViewById(R.id.file_transfer_status);

                                client_status_text.post(new Runnable() {
                                    public void run() {
                                        client_status_text.setText((String)resultData.get("message"));
                                    }
                                });
                            }
                        }

                    }
                });

                transferActive = true;
                startService(clientServiceIntent);



                //end

        }
    }

    public void startServer(View view) {

        //If server is already listening on port or transfering data, do not attempt to start server service
        if(!serverThreadActive)
        {
            //Create new thread, open socket, wait for connection, and transfer file

            serverServiceIntent = new Intent(this, ServerService.class);
            serverServiceIntent.putExtra("saveLocation", downloadTarget);
            serverServiceIntent.putExtra("port", Integer.valueOf(port));

            serverServiceIntent.putExtra("serverResult", new ResultReceiver(null) {
                @Override
                protected void onReceiveResult(int resultCode, final Bundle resultData) {

                    if(resultCode == port )
                    {
                        if (resultData == null) {
                            //Server service has shut down. Download may or may not have completed properly.
                            serverThreadActive = false;


                        }
                    }

                }
            });

            serverThreadActive = true;
            startService(serverServiceIntent);

            //Set status to running

        }
    }

    void checkConnetion () {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo ethernet = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo vpn = connectivityManager.getActiveNetworkInfo();
        if(wifi.isAvailable()) {
            WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int address = wifiInfo.getIpAddress();
            Toast.makeText(this, " 와이파이 주소 " +  android.text.format.Formatter.formatIpAddress(address), Toast.LENGTH_LONG);
            
        }
    }


}
