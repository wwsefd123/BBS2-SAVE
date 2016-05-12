package com.example.administrator.basdf;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;


/**
 * Created by Administrator on 2016-05-03.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver{

        private WifiP2pManager wifiP2pManager;
        private Channel channel;
        private CreateConnectionActivity activity;

        public WifiBroadcastReceiver(WifiP2pManager manager,Channel channel,CreateConnectionActivity activity) {
            super();
            this.activity = activity;
            this.channel = channel;
            this.wifiP2pManager = manager;
        }

        @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            wifiP2pManager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    activity.displayPeers(peers);

                }
            });
        }
    }
}

