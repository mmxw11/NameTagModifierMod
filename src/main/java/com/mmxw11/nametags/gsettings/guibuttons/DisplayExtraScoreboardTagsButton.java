package com.mmxw11.nametags.gsettings.guibuttons;

import com.mmxw11.nametags.NameTagMod;
import com.mmxw11.nametags.technical.files.ModSettingsProfile;

import net.minecraft.util.EnumChatFormatting;

public class DisplayExtraScoreboardTagsButton extends AbstractGuiButton {

    public DisplayExtraScoreboardTagsButton(int order, int width, int height) {
        super(order, (width / 2) - 205, (height / 2) - 10, "Display e-scoreboard tags");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
            boolean value = !modSettings.IsDisplayEScoreboardTags();
            modSettings.toggleDisplayEScoreboardTags(value);
        }
    }

    @Override
    public void onDrawButton() {
        ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
        super.displayString = EnumChatFormatting.YELLOW + "Display e-scoreboard tags: " + (modSettings.IsDisplayEScoreboardTags()
                ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
    }
}