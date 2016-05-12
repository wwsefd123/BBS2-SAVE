package com.example.administrator.basdf;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Button btnMCONNECT,btnOCONNECT,btnSELECT;
    TextView device_show;
    ImageButton btnWIFI,btnNFC;
    WifiManager wifiMgr;
    private final IntentFilter intentFilter = new IntentFilter();
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;


    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;

/*
    public void connetPeer(WifiP2pDevice device){
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        manager.connect(channel, config, null);
    }
*/
    /*
    public void displayPeers(final WifiP2pDeviceList peers){
        ArrayList<String> peerStringArrayList = new ArrayList<>();
        for(WifiP2pDevice device : peers.getDeviceList()){
            device_show.append(device.deviceName);
            connetPeer(device);
        }
    }
*/
    public void openFileBrowser(View view) {

        Intent clientStartIntent = new Intent(this, FileBrowser.class);
        startActivityForResult(clientStartIntent, Constants.FILE_REQUEST);
    }

    public void createConnection(View view) {
        Intent createConnectionIntent = new Intent(this, CreateConnectionActivity.class);
        startActivityForResult(createConnectionIntent, Constants.CREATE_CONNECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.FILE_REQUEST :
                if(resultCode == RESULT_OK) {
                    File file = (File) data.getExtras().get("file");
                    device_show.setText(file.getAbsolutePath());
                }
                break;
            case Constants.CREATE_CONNECTION :
                if(resultCode == Constants.ACTIVATE_WIFIP2P) {
                    Intent musicPlayIntent = new Intent(this, MusicPlayActivity.class);
                    startActivityForResult(musicPlayIntent, Constants.MUSIC_PLAY);
                    Toast.makeText(this, "연결이 되었습니다", Toast.LENGTH_SHORT).show();
                }
        }
    }
/*
    public void searchPeers(View view) {
        manager.discoverPeers(channel, null);
    }
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnWIFI = (ImageButton)findViewById(R.id.Btn_WIFI_DIRECT);
        btnNFC = (ImageButton)findViewById(R.id.Btn_NFC);
        btnMCONNECT = (Button)findViewById(R.id.Btn_MCONNECT);

        device_show = (TextView)findViewById(R.id.device_show);

        wifiMgr  = (WifiManager)getSystemService(Context.WIFI_SERVICE);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);




        btnWIFI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!wifiMgr.isWifiEnabled()) {
                    wifiMgr.setWifiEnabled(true);
                    btnWIFI.setBackgroundColor(Color.GREEN);
                }
                else{
                    wifiMgr.setWifiEnabled(false);
                }
            }

        });

        btnNFC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();

    }
/*
    // BroadcastReceiver 등록을 취소한다.

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }




}


