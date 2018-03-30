package com.mmxw11.nametags;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonParseException;
import com.mmxw11.nametags.commands.NameTagEditCommand;
import com.mmxw11.nametags.commands.OpenSettingsCommand;
import com.mmxw11.nametags.technical.KeyHandler;
import com.mmxw11.nametags.technical.ModListeners;
import com.mmxw11.nametags.technical.NameTagHandler;
import com.mmxw11.nametags.technical.NetworkEventListeners;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = NameTagMod.MODID, name = NameTagMod.NAME, version = NameTagMod.VERSION, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class NameTagMod {

    public static final String MODID = "ntmodifier";
    public static final String NAME = "NameTagModifier";
    public static final String VERSION = "1.1.1";
    public static final String PREFIX = "&f[&cNameTagModifierMod&f] ";
    private static NameTagMod instance;
    private Logger logger;
    private NameTagHandler nhandler;
    private ScheduledExecutorService scheduledExService;
    private NetworkEventListeners nelisteners;
    private ModListeners mlisteners;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        instance = this;
        this.logger = LogManager.getLogger(NAME);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        this.nhandler = new NameTagHandler();
        try {
            nhandler.setupFileManager();
        } catch (JsonParseException | IOException ex) {
            logger.log(Level.FATAL, "Failed to load " + NAME + " " + VERSION + "!", ex);
            return;
        }
        this.scheduledExService = Executors.newScheduledThreadPool(2);
        KeyHandler keyHandler = new KeyHandler(this);
        keyHandler.register();
        ClientCommandHandler.instance.registerCommand(new NameTagEditCommand());
        ClientCommandHandler.instance.registerCommand(new OpenSettingsCommand());
        this.nelisteners = new NetworkEventListeners(this);
        nelisteners.register();
        this.mlisteners = new ModListeners(nhandler, nelisteners);
        mlisteners.register();
        Runtime.getRuntime().addShutdownHook(new Thread() { // add shutdownhook for the exService

            @Override
            public void run() {
                scheduledExService.shutdownNow();
            }
        });
        logger.info(NAME + " " + VERSION + " successfully loaded!");
    }

    public static NameTagMod getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public ScheduledExecutorService getSExecutorService() {
        return scheduledExService;
    }

    public NameTagHandler getNHandler() {
        return nhandler;
    }

    public NetworkEventListeners getNEListeners() {
        return nelisteners;
    }

    public ModListeners getMListeners() {
        return mlisteners;
    }
}