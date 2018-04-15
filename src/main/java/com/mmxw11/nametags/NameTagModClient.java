package com.mmxw11.nametags;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.JsonParseException;
import com.mmxw11.nametags.commands.NameTagEditCommand;
import com.mmxw11.nametags.commands.OpenSettingsGUICommand;
import com.mmxw11.nametags.settings.FileManager;
import com.mmxw11.nametags.settings.ModSettingsProfile;
import com.mmxw11.nametags.technical.KeyHandler;
import com.mmxw11.nametags.technical.ModListeners;
import com.mmxw11.nametags.technical.NameTagHandler;
import com.mmxw11.nametags.technical.NetworkEventListeners;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = NameTagModClient.MODID, name = NameTagModClient.NAME, version = NameTagModClient.VERSION, clientSideOnly = true,
        acceptedMinecraftVersions = "[1.12.2]")
public class NameTagModClient {

    public static final String MODID = "ntmodifier";
    public static final String NAME = "NameTagModifier";
    public static final String VERSION = "1.2.0";
    public static final String PREFIX = "&f[&cNameTagModifierMod&f] ";
    private static NameTagModClient instance;
    private Logger logger;
    private ScheduledExecutorService exService;
    private FileManager fmanager;
    private ModSettingsProfile modSettings;
    private NetworkEventListeners nelisteners;
    private NameTagHandler nhandler;
    private ModListeners mlisteners;

    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        instance = this;
        this.logger = LogManager.getLogger(NAME);
    }

    @EventHandler
    public void init(FMLInitializationEvent e) {
        ThreadFactoryBuilder builder = new ThreadFactoryBuilder();
        builder.setNameFormat(NAME + " Thread-%d");
        this.exService = Executors.newScheduledThreadPool(2, builder.build());
        addExServiceShutdownHook();
        this.fmanager = new FileManager();
        try {
            fmanager.createFiles();
            this.modSettings = fmanager.loadSettings();
        } catch (IOException | JsonParseException ex) {
            logger.log(Level.FATAL, "Failed to load " + NAME + " " + VERSION + "!", ex);
            return;
        }
        this.nelisteners = new NetworkEventListeners(this);
        nelisteners.register();
        this.nhandler = new NameTagHandler();
        try {
            nhandler.loadSettings(modSettings, fmanager);
        } catch (IOException ex) {
            logger.log(Level.FATAL, "Failed to load " + NAME + " " + VERSION + "!", ex);
            return;
        }
        KeyHandler keyHandler = new KeyHandler(this);
        keyHandler.register();
        ClientCommandHandler.instance.registerCommand(new NameTagEditCommand());
        ClientCommandHandler.instance.registerCommand(new OpenSettingsGUICommand());
        this.mlisteners = new ModListeners(this);
        mlisteners.register();
        logger.info(NAME + " " + VERSION + " has been loaded!");
    }

    private void addExServiceShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                exService.shutdownNow();
            }
        });
    }

    public static NameTagModClient getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public ScheduledExecutorService getSExecutorService() {
        return exService;
    }

    public FileManager getFileManager() {
        return fmanager;
    }

    public ModSettingsProfile getModSettings() {
        return modSettings;
    }

    public NetworkEventListeners getNeListeners() {
        return nelisteners;
    }

    public NameTagHandler getNHandler() {
        return nhandler;
    }

    public ModListeners getMListeners() {
        return mlisteners;
    }
}