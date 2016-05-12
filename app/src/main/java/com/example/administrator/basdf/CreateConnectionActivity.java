package com.example.administrator.basdf;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.os.Bundle;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016-05-04.
 */
public class CreateConnectionActivity extends Activity {

    private WifiP2pManager manager;
    private Channel channel;
    private IntentFilter wifiP2pIntentFilter;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private ListView peerDeviceListView;

    /*
    CreatConnectionActivity 클래스 호출시 최초 실행하는 Method 이며
    시스템 서비스로부터 정보를 얻어와 WifiP2pManager를 초기화 시킨다.
    그리고 플랫폼에서 이 Activity에서 필요한 정보를 걸러내어줄 IntentFilter를 초기화하고
    action 을 추가한다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_connection);

        wifiP2pIntentFilter = new IntentFilter();
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        wifiP2pIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        manager.discoverPeers(channel, null);

        peerDeviceListView = (ListView)findViewById(R.id.peer_list);

    }


    /*
    OnStart 이후에 실행 되는 Method 이며
    플랫폼이 보내주는 Broadcast를 잡아주는 BroadcastReceiver를 초기화하고
    이 Activity에 등록한다.
     */
    @Override
    protected void onResume() {
        super.onResume();
        wifiBroadcastReceiver = new WifiBroadcastReceiver(manager, channel, this);
        this.registerReceiver(wifiBroadcastReceiver, wifiP2pIntentFilter);
    }

    /*
    홈키 등을 눌렀을 경우 불려오는  Method  이며
    일시적으로 BroadcastReceiver 등록을 해제해 줘서
    Focus 를 갖고있지 않을 경우 Broadcast를 잡지 않게 한다.
     */
    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(wifiBroadcastReceiver);
    }


    /*
    이 Activity에 등록된 BroadCastReceiver 에서
     WIFI_P2P_PEERS_CHANGED_ACTION 이 잡혔을 경우
     ListView를 새로 그려줘서 사용자가 연결할 수 있는 기기를
     표시하여 주는 Method이다.
     */
    protected void displayPeers(final WifiP2pDeviceList wifiP2pDeviceList) {
        final ArrayList<String> peerDeviceNameList = new ArrayList();
        for(WifiP2pDevice device : wifiP2pDeviceList.getDeviceList()) {
            peerDeviceNameList.add(device.deviceName);
        }
        ArrayAdapter peerNameData = new ArrayAdapter(this, android.R.layout.simple_list_item_1,peerDeviceNameList);
        peerDeviceListView.setAdapter(peerNameData);
        Toast.makeText(this, "피어 상태 변경 감지",Toast.LENGTH_SHORT).show();

        peerDeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                for(WifiP2pDevice device :  wifiP2pDeviceList.getDeviceList() ) {
                    if(device.deviceName.equals(peerDeviceNameList.get(position))) {
                        connectPeer(device);

                    }
                }

            }
        });
    }

    public void connectPeer(WifiP2pDevice device){
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        manager.connect(channel, config, null);

        setResult(Constants.ACTIVATE_WIFIP2P);
        finish();
    }
}
