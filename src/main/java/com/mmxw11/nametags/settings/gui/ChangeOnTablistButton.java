package com.mmxw11.nametags.settings.gui;

import net.minecraft.util.text.TextFormatting;

public class ChangeOnTablistButton extends AbstractGUIButton {

    public ChangeOnTablistButton(int order, int width, int height) {
        super(order, (width / 2) - 205, (height / 2) - 35, "Change on tablist");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            boolean value = !modSettings.isChangeOnTablist();
            modSettings.toggleChangeOnTablist(value);
        }
    }

    @Override
    public void onDrawButton() {
        super.displayString = TextFormatting.YELLOW + "Change on tablist: "
                + (modSettings.isChangeOnTablist() ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled");
    }
}