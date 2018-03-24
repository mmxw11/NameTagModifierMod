package com.mmxw11.nametags.settings.gui;

import com.mmxw11.nametags.NameTagMod;
import com.mmxw11.nametags.settings.ModSettingsProfile;

import net.minecraft.util.EnumChatFormatting;

public class ChangeOnTablistButton extends AbstractGUIButton {

    public ChangeOnTablistButton(int order, int width, int height) {
        super(order, (width / 2) - 205, (height / 2) - 35, "Change on tablist");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
            boolean value = !modSettings.isChangeOnTablist();
            modSettings.toggleChangeOnTablist(value);
        }
    }

    @Override
    public void onDrawButton() {
        ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
        super.displayString = EnumChatFormatting.YELLOW + "Change on tablist: " + (modSettings.isChangeOnTablist()
                ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
    }
}