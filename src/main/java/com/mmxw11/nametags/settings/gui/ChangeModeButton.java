package com.mmxw11.nametags.settings.gui;

import com.mmxw11.nametags.NameTagMode;

import net.minecraft.util.EnumChatFormatting;

public class ChangeModeButton extends AbstractGUIButton {

    public ChangeModeButton(int order, int width, int height) {
        super(order, (width / 2) + 5, (height / 2) - 60, "NameTagMode");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            NameTagMode mode = modSettings.getNameTagMode();
            if (mode == null) {
                mode = NameTagMode.HIDE;
            } else {
                mode = mode.nextEnum();
            }
            nhandler.setNameTagMode(mode);
        }
    }

    @Override
    public void onDrawButton() {
        NameTagMode mode = modSettings.getNameTagMode();
        super.displayString = EnumChatFormatting.YELLOW + "NameTagMode: " +
                EnumChatFormatting.GRAY + (mode == null ? "NOT_SET" : mode.getName());
    }
}