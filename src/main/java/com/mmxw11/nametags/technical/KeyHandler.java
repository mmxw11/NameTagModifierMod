package com.mmxw11.nametags.technical;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.settings.ModSettingsProfile;
import com.mmxw11.nametags.util.ChatHelper;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeyHandler {

    private static final String KEY_CATEGORY = NameTagModClient.NAME + " Mod";
    private final KeyBinding toggleButton;
    private NameTagModClient mod;

    public KeyHandler(NameTagModClient mod) {
        this.toggleButton = new KeyBinding("Toggle the mod", Keyboard.KEY_N, KEY_CATEGORY);
        this.mod = mod;
    }

    public void register() {
        ClientRegistry.registerKeyBinding(toggleButton);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent e) {
        if (toggleButton.isPressed()) {
            ModSettingsProfile modSettings = mod.getModSettings();
            boolean value = !modSettings.isEnabled();
            modSettings.toggleMod(value);
            try {
                mod.getFileManager().saveSettingsFile(modSettings);
                ChatHelper.sendMessageToPlayer(value ? "&aTags enabled." : "&cTags disabled.");
            } catch (IOException ex) {
                ChatHelper.sendMessageToPlayer(EnumChatFormatting.RED + "Unable to save config fle! Check console for details: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}