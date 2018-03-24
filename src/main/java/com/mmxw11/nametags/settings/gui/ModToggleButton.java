package com.mmxw11.nametags.settings.gui;

import com.mmxw11.nametags.NameTagMod;
import com.mmxw11.nametags.settings.ModSettingsProfile;
import com.mmxw11.nametags.util.ChatHelper;

import net.minecraft.util.EnumChatFormatting;

public class ModToggleButton extends AbstractGUIButton {

    public ModToggleButton(int order, int width, int height) {
        super(order, (width / 2) - 205, (height / 2) - 60, "Toggle Mod");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
            boolean value = !modSettings.isEnabled();
            modSettings.toggleMod(value);
            ChatHelper.sendMessageToPlayer(value ? "&aTags enabled." : "&cTags disabled.");
        }
    }

    @Override
    public void onDrawButton() {
        ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
        super.displayString = EnumChatFormatting.YELLOW + NameTagMod.NAME + ": " + (modSettings.isEnabled()
                ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
    }
}