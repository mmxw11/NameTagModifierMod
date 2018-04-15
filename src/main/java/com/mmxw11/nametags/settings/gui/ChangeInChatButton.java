package com.mmxw11.nametags.settings.gui;

import net.minecraft.util.EnumChatFormatting;

public class ChangeInChatButton extends AbstractGUIButton {

    public ChangeInChatButton(int order, int width, int height) {
        super(order, (width / 2) + 5, (height / 2) - 35, "Change in chat");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            boolean value = !modSettings.isChangeInChat();
            modSettings.toggleChangeInChat(value);
        }
    }

    @Override
    public void onDrawButton() {
        super.displayString = EnumChatFormatting.YELLOW + "Change in chat: "
                + (modSettings.isChangeInChat() ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
    }
}