package com.mmxw11.nametags;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class ServerCheckerListeners {

    private NameTagMod mod;
    private boolean ctHypixel;
    private boolean connectedToServer;
    private final String hydomain;
    private String cdomain;
    private String lastDomain;
    private List<String> ips;

    public ServerCheckerListeners(NameTagMod mod) {
        this.mod = mod;
        this.ctHypixel = false;
        this.connectedToServer = false;
        this.hydomain = "hypixel.net";
        this.cdomain = "";
        this.lastDomain = "";
        this.ips = new ArrayList<>();
        registerOtherIps();
    }

    private void registerOtherIps() {
        ips.add("99.198.123.178");
        ips.add("209.222.115.14");
    }

    public void register() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onClientConnnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent e) {
        if (e.isLocal) {
            this.cdomain = "";
            return;
        }
        this.connectedToServer = true;
        mod.getNHandler().startPlayerUpdater();
        String host = FMLClientHandler.instance().getClient().getCurrentServerData().serverIP;
        if (!cdomain.isEmpty()) {
            this.lastDomain = cdomain;
        }
        this.cdomain = host;
        if (host.toLowerCase().endsWith(hydomain) || ips.contains(host)) {
            this.ctHypixel = true;
            mod.getLogger().info("Joined the Hypixel Network.");
        }
    }

    @SubscribeEvent
    public void onClientDisconnectionFomServer(FMLNetworkEvent.ClientDisconnectionFromServerEvent e) {
        this.connectedToServer = false;
        mod.getNHandler().stopPlayerUpdater();
        mod.getMListeners().resetCurrentServer();
        if (ctHypixel) {
            this.ctHypixel = false;
            mod.getLogger().info("Disconnected from the Hypixel Network.");
        }
    }

    public boolean isConnectedToHypixel() {
        return ctHypixel;
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