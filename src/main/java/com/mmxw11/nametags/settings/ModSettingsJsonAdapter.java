package com.mmxw11.nametags.settings;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mmxw11.nametags.NameTagMode;

public class ModSettingsJsonAdapter implements JsonSerializer<ModSettingsProfile>, JsonDeserializer<ModSettingsProfile> {

    @Override
    public JsonElement serialize(ModSettingsProfile src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jobject = new JsonObject();
        jobject.addProperty("enabled", src.isEnabled());
        NameTagMode mode = src.getNameTagMode();
        jobject.addProperty("mode", mode == null ? "NOT_SET" : mode.getName());
        jobject.addProperty("displayEScoreboardTags", src.IsDisplayEScoreboardTags());
        jobject.addProperty("rplayerTagsOnLeave", src.isRemovePlayerTagsOnLeave());
        jobject.addProperty("changeOnTablist", src.isChangeOnTablist());
        jobject.addProperty("changeInChat", src.isChangeInChat());
        jobject.addProperty("autoRemoveTeamTags", src.isAutoRemoveTeamTags());
        return jobject;
    }

    @Override
    public ModSettingsProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jobject = json.getAsJsonObject();
        ModSettingsProfile settings = new ModSettingsProfile();
        settings.toggleMod(jobject.get("enabled").getAsBoolean());
        String modeName = jobject.get("mode").getAsString();
        NameTagMode mode = NameTagMode.getByName(modeName);
        settings.setNameTagMode(mode);
        settings.toggleDisplayEScoreboardTags(jobject.get("displayEScoreboardTags").getAsBoolean());
        settings.togglePlayerTagsRemovalOnLeave(jobject.get("rplayerTagsOnLeave").getAsBoolean());
        settings.toggleChangeOnTablist(jobject.get("changeOnTablist").getAsBoolean());
        settings.toggleChangeInChat(jobject.get("changeInChat").getAsBoolean());
        settings.toggleAutoTeamTagsRemoval(jobject.get("autoRemoveTeamTags").getAsBoolean());
        return settings;
    }
}