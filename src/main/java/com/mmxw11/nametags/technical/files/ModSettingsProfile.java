package com.mmxw11.nametags.technical.files;

import com.mmxw11.nametags.NameTagMode;

public class ModSettingsProfile {

    private boolean enabled;
    private NameTagMode mode;
    private boolean displayEScoreboardTags;
    private boolean rplayerTagsOnLeave;
    private boolean changeOnTablist;
    private boolean changeInChat;
    private boolean autoRemoveTeamTags;

    public void toggleMod(boolean value) {
        this.enabled = value;
    }

    public void setNameTagMode(NameTagMode mode) {
        this.mode = mode;
    }

    public void toggleDisplayEScoreboardTags(boolean value) {
        this.displayEScoreboardTags = value;
    }

    public void togglePlayerTagsRemovalOnLeave(boolean value) {
        this.rplayerTagsOnLeave = value;
    }

    public void toggleChangeOnTablist(boolean value) {
        this.changeOnTablist = value;
    }

    public void toggleChangeInChat(boolean value) {
        this.changeInChat = value;
    }

    public void toggleAutoTeamTagsRemoval(boolean value) {
        this.autoRemoveTeamTags = value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public NameTagMode getNameTagMode() {
        return mode;
    }

    public boolean IsDisplayEScoreboardTags() {
        return displayEScoreboardTags;
    }

    public boolean isRemovePlayerTagsOnLeave() {
        return rplayerTagsOnLeave;
    }

    public boolean isChangeOnTablist() {
        return changeOnTablist;
    }

    public boolean isChangeInChat() {
        return changeInChat;
    }

    public boolean isAutoRemoveTeamTags() {
        return autoRemoveTeamTags;
    }
}