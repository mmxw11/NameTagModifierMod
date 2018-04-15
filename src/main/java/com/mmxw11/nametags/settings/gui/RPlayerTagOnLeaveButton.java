package com.mmxw11.nametags.settings.gui;

import net.minecraft.util.EnumChatFormatting;

public class RPlayerTagOnLeaveButton extends AbstractGUIButton {

    public RPlayerTagOnLeaveButton(int order, int width, int height) {
        super(order, (width / 2) + 5, (height / 2) - 10, "Remove player tags on leave");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            boolean value = !modSettings.isRemovePlayerTagsOnLeave();
            modSettings.togglePlayerTagsRemovalOnLeave(value);
        }
    }

    @Override
    public void onDrawButton() {
        super.displayString = EnumChatFormatting.YELLOW + "Remove player tags on leave: "
                + (modSettings.isRemovePlayerTagsOnLeave() ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
    }
}