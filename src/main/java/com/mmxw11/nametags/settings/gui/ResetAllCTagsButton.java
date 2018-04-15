package com.mmxw11.nametags.settings.gui;

import net.minecraft.util.text.TextFormatting;

public class ResetAllCTagsButton extends AbstractGUIButton {

    public ResetAllCTagsButton(int order, int width, int height) {
        super(order, (width / 2) - 100, (height / 2) + 40, TextFormatting.RED + "Reset all custom tags");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            nhandler.removeAllCustomNameTags(false);
        }
    }

    @Override
    public void onDrawButton() {}
}