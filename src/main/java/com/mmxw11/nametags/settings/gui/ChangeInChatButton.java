package com.mmxw11.nametags.settings.gui;

import com.mmxw11.nametags.NameTagMod;
import com.mmxw11.nametags.settings.ModSettingsProfile;

import net.minecraft.util.EnumChatFormatting;

public class ChangeInChatButton extends AbstractGUIButton {

    public ChangeInChatButton(int order, int width, int height) {
        super(order, (width / 2) + 5, (height / 2) - 35, "Change in chat");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
            boolean value = !modSettings.isChangeInChat();
            modSettings.toggleChangeInChat(value);
        }
    }

    @Override
    public void onDrawButton() {
        ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
        super.displayString = EnumChatFormatting.YELLOW + "Change in chat: " + (modSettings.isChangeInChat()
                ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
    }
}