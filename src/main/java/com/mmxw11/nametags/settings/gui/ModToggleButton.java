package com.mmxw11.nametags.settings.gui;

import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.util.ChatHelper;

import net.minecraft.util.text.TextFormatting;

public class ModToggleButton extends AbstractGUIButton {

    public ModToggleButton(int order, int width, int height) {
        super(order, (width / 2) - 205, (height / 2) - 60, "Toggle Mod");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            boolean value = !modSettings.isEnabled();
            modSettings.toggleMod(value);
            ChatHelper.sendMessageToPlayer(value ? "&aTags enabled." : "&cTags disabled.", false);
        }
    }

    @Override
    public void onDrawButton() {
        super.displayString = TextFormatting.YELLOW + NameTagModClient.NAME + ": "
                + (modSettings.isEnabled() ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled");
    }
}