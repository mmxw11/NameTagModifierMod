package com.mmxw11.nametags.technical.files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mmxw11.nametags.NameTagMod;

public class FileManager {

    private final String dataFolder;
    private String settingsFile;
    private Gson gson;

    public FileManager() {
        this.dataFolder = "NameTagModifier";
        this.settingsFile = "settings.json";
        GsonBuilder gbuilder = new GsonBuilder();
        gbuilder.setPrettyPrinting()
                .registerTypeAdapter(ModSettingsProfile.class, new ModSettingsJsonAdapter());
        this.gson = gbuilder.create();
    }

    public void createFiles() throws IOException {
        File file = getRootFolder();
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public ModSettingsProfile loadSettings() throws IOException, JsonParseException {
        File file = new File(getRootFolder(), settingsFile);
        if (!file.exists()) {
            return generateSettingsFile();
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return gson.fromJson(reader, ModSettingsProfile.class);
        } catch (Exception e) {
            NameTagMod.getInstance().getLogger().log(Level.FATAL, "Malformed " + settingsFile + " file regenerating...", e);
            return generateSettingsFile();
        }
    }

    public void saveSettingsFile(ModSettingsProfile modSettings) throws IOException {
        File file = new File(getRootFolder(), settingsFile);
        if (file.exists()) {
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                // just ignore...
            }
        }
        try (Writer writter = new BufferedWriter(new FileWriter(file))) {
            gson.toJson(modSettings, writter);
        }
    }

    private ModSettingsProfile generateSettingsFile() throws IOException, JsonParseException {
        ModSettingsProfile modSettings = new ModSettingsProfile();
        modSettings.toggleMod(true);
        modSettings.toggleDisplayEScoreboardTags(true);
        modSettings.togglePlayerTagsRemovalOnLeave(true);
        saveSettingsFile(modSettings);
        return modSettings;
    }

    private File getRootFolder() {
        return new File("mods", dataFolder);
    }
}