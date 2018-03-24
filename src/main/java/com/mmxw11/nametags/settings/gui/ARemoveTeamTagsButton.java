package com.mmxw11.nametags.settings.gui;

import com.mmxw11.nametags.NameTagMod;
import com.mmxw11.nametags.settings.ModSettingsProfile;

import net.minecraft.util.EnumChatFormatting;

public class ARemoveTeamTagsButton extends AbstractGUIButton {

    public ARemoveTeamTagsButton(int order, int width, int height) {
        super(order, (width / 2) - 100, (height / 2) + 15, "Auto remove team tags");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
            boolean value = !modSettings.isAutoRemoveTeamTags();
            modSettings.toggleAutoTeamTagsRemoval(value);
        }
    }

    @Override
    public void onDrawButton() {
        ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
        super.displayString = EnumChatFormatting.YELLOW + "Auto remove team tags: " + (modSettings.isAutoRemoveTeamTags()
                ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
    }
}