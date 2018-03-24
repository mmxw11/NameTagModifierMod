package com.mmxw11.nametags.commands;

import com.mmxw11.nametags.NameTagMod;
import com.mmxw11.nametags.settings.gui.SettingsGui;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class OpenSettingsCommand extends CommandBase {

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "ntagsettings";
    }

    @Override
    public String getCommandUsage(ICommandSender paramICommandSender) {
        return "/ntagsettings";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        NameTagMod.getInstance().getSExecutorService().submit(() -> {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
            SettingsGui gui = new SettingsGui();
            Minecraft.getMinecraft().displayGuiScreen(gui);
        });
    }
}