package com.mmxw11.nametags.settings.gui;

import com.mmxw11.nametags.NameTagMod;
import com.mmxw11.nametags.NameTagMode;
import com.mmxw11.nametags.settings.ModSettingsProfile;
import com.mmxw11.nametags.technical.NameTagHandler;

import net.minecraft.util.EnumChatFormatting;

public class ChangeModeButton extends AbstractGUIButton {

    public ChangeModeButton(int order, int width, int height) {
        super(order, (width / 2) + 5, (height / 2) - 60, "NameTagMode");
    }

    @Override
    public void onMousePressed(boolean success) {
        if (success) {
            NameTagHandler nhandler = NameTagMod.getInstance().getNHandler();
            NameTagMode mode = nhandler.getModSettings().getNameTagMode();
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
        ModSettingsProfile modSettings = NameTagMod.getInstance().getNHandler().getModSettings();
        NameTagMode mode = modSettings.getNameTagMode();
        super.displayString = EnumChatFormatting.YELLOW + "NameTagMode: " +
                EnumChatFormatting.GRAY + (mode == null ? "NOT_SET" : mode.getName());
    }
}