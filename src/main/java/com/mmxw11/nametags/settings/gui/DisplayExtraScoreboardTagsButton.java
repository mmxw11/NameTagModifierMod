package com.mmxw11.nametags.settings.gui;

import net.minecraft.util.EnumChatFormatting;

public class DisplayExtraScoreboardTagsButton extends AbstractGUIButton {

    public DisplayExtraScoreboardTagsButton(int order, int width, int height) {
        super(order, (width / 2) - 205, (height / 2) - 10, "Display e-scoreboard tags");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            boolean value = !modSettings.IsDisplayEScoreboardTags();
            modSettings.toggleDisplayEScoreboardTags(value);
        }
    }

    @Override
    public void onDrawButton() {
        super.displayString = EnumChatFormatting.YELLOW + "Display e-scoreboard tags: "
                + (modSettings.IsDisplayEScoreboardTags() ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled");
    }
}