package com.mmxw11.nametags.settings.gui;

import net.minecraft.util.text.TextFormatting;

public class ARemoveTeamTagsButton extends AbstractGUIButton {

    public ARemoveTeamTagsButton(int order, int width, int height) {
        super(order, (width / 2) - 100, (height / 2) + 15, "Auto remove team tags");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            boolean value = !modSettings.isAutoRemoveTeamTags();
            modSettings.toggleAutoTeamTagsRemoval(value);
        }
    }

    @Override
    public void onDrawButton() {
        super.displayString = TextFormatting.YELLOW + "Auto remove team tags: "
                + (modSettings.isAutoRemoveTeamTags() ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled");
    }
}