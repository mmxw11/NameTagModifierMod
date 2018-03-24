package com.mmxw11.nametags.settings.gui;

import com.mmxw11.nametags.NameTagMod;
import com.mmxw11.nametags.settings.ModSettingsProfile;

import net.minecraft.util.EnumChatFormatting;

public class RPlayerTagOnLeaveButton extends AbstractGUIButton {

    public RPlayerTagOnLeaveButton(int order, int width, int height) {
        super(order, (width / 2) + 5, (height / 2) - 10, "Remove player tags on leave");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
            boolean value = !modSettings.isRemovePlayerTagsOnLeave();
            modSettings.togglePlayerTagsRemovalOnLeave(value);
        }
    }

    @Override
    public void onDrawButton() {
        ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
        super.displayString = EnumChatFormatting.YELLOW + "Remove player tags on leave: " + (modSettings.isRemovePlayerTagsOnLeave()
                ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
    }
}