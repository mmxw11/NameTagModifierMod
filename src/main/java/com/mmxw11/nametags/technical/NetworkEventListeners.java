package com.mmxw11.nametags.technical;

import com.mmxw11.nametags.NameTagModClient;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class NetworkEventListeners {

    private NameTagModClient mod;
    private boolean connectedToServer;
    private String cdomain;
    private String lastDomain;

    public NetworkEventListeners(NameTagModClient mod) {
        this.mod = mod;
        this.connectedToServer = false;
        this.cdomain = "";
        this.lastDomain = "";
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientConnnected(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if (e.isLocal) {
            this.cdomain = "";
            return;
        }
        this.connectedToServer = true;
        String host = FMLClientHandler.instance().getClient().getCurrentServerData().serverIP;
        if (!cdomain.isEmpty()) {
            this.lastDomain = cdomain;
        }
        this.cdomain = host;
        mod.getNHandler().startTask();
    }

    @SubscribeEvent
    public void onClientDisconnection(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        this.connectedToServer = false;
        mod.getNHandler().stopTask();
        mod.getMListeners().resetCurrentServer();
    }

    public boolean isConnectedToServer() {
        return connectedToServer;
    }

    public String getCurrentDomain() {
        return cdomain;
    }

    public String getLastDomain() {
        return lastDomain;
    }
}