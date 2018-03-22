package com.mmxw11.nametags.technical;

import org.lwjgl.input.Keyboard;

import com.mmxw11.nametags.NameTagMod;
import com.mmxw11.nametags.technical.files.ModSettingsProfile;
import com.mmxw11.nametags.util.ChatHelper;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyHandler {

    private static final String KEY_CATEGORY = NameTagMod.NAME + " Mod";
    private final KeyBinding toggleButton;
    private NameTagHandler nhandler;

    public KeyHandler(NameTagMod mod) {
        this.toggleButton = new KeyBinding("Toggle the mod", Keyboard.KEY_N, KEY_CATEGORY);
        this.nhandler = mod.getNHandler();
    }

    public void register() {
        ClientRegistry.registerKeyBinding(toggleButton);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (toggleButton.isPressed()) {
            ModSettingsProfile settings = nhandler.getModSettings();
            boolean value = !settings.isEnabled();
            settings.toggleMod(value);
            ChatHelper.sendMessageToPlayer(value ? "&aTags enabled." : "&cTags disabled.");
        }
    }
}