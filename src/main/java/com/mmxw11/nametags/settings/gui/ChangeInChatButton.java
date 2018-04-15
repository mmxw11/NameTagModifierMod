package com.mmxw11.nametags.settings.gui;

import net.minecraft.util.text.TextFormatting;

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
        super.displayString = TextFormatting.YELLOW + "Change in chat: "
                + (modSettings.isChangeInChat() ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled");
    }
}