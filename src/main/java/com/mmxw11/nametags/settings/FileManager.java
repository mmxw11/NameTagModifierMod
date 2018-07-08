package com.mmxw11.nametags.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mmxw11.nametags.NameTagModClient;

public class FileManager {

    private final String dataFolder;
    private String settingsFile;
    private String namesFile;
    private Gson gson;

    public FileManager() {
        this.dataFolder = "NameTagModifier";
        this.settingsFile = "settings.json";
        this.namesFile = "random-names.csv";
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
            NameTagModClient.getInstance().getLogger().log(Level.FATAL, "Malformed " + settingsFile + " file regenerating...", e);
            return generateSettingsFile();
        }
    }

    public List<String> readRandomNames() throws IOException {
        List<String> names = new ArrayList<>();
        try (InputStream input = getJarResource(namesFile);
                BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
            reader.lines().forEach(l -> {
                String[] values = l.split(",");
                if (values.length == 0) {
                    l = l.replaceAll("\uFEFF", ""); // remove BOM if present.
                    if (!names.contains(l)) {
                        names.add(l);
                    }
                } else {
                    for (String value : values) {
                        value = value.replaceAll("\uFEFF", ""); // remove BOM if present.
                        if (!names.contains(value)) {
                            names.add(value);
                        }
                    }
                }
            });
            return names;
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

    public InputStream getJarResource(String filename) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        filename = filename.replaceAll("\\\\", "/");
        URL url = getClass().getClassLoader().getResource(filename);
        if (url == null) {
            throw new FileNotFoundException("File '" + filename + "' not found.");
        }
        URLConnection conn = url.openConnection();
        conn.setUseCaches(false);
        return conn.getInputStream();
    }

    private File getRootFolder() {
        return new File("mods", dataFolder);
    }
}