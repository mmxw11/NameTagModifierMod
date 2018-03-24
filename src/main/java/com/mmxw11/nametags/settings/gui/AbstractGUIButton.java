package com.mmxw11.nametags.settings.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public abstract class AbstractGUIButton extends GuiButton {

    public AbstractGUIButton(int p_i1020_1_, int p_i1020_2_, int p_i1020_3_, String p_i1020_4_) {
        super(p_i1020_1_, p_i1020_2_, p_i1020_3_, 200, 20, p_i1020_4_);
    }

    @Override
    public void drawButton(Minecraft p_drawButton_1_, int p_drawButton_2_, int p_drawButton_3_) {
        super.drawButton(p_drawButton_1_, p_drawButton_2_, p_drawButton_3_);
        onDrawButton();
    }

    @Override
    public boolean mousePressed(Minecraft p_mousePressed_1_, int p_mousePressed_2_, int p_mousePressed_3_) {
        boolean success = super.mousePressed(p_mousePressed_1_, p_mousePressed_2_, p_mousePressed_3_);
        onMousePressed(success);
        return success;
    }

    public abstract void onMousePressed(boolean success);

    public abstract void onDrawButton();
}