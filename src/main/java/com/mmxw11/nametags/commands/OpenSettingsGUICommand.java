package com.mmxw11.nametags.commands;

import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.settings.gui.SettingsGUI;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class OpenSettingsGUICommand extends CommandBase {

    @Override
    public boolean checkPermission(MinecraftServer mserver, ICommandSender sender) {
        return true;
    }

    @Override
    public String getName() {
        return "ntagsettings";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/ntagsettings";
    }

    @Override
    public void execute(MinecraftServer mserver, ICommandSender sender, String[] args) throws CommandException {
        NameTagModClient.getInstance().getSExecutorService().submit(() -> {
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
            }
            SettingsGUI gui = new SettingsGUI();
            Minecraft.getMinecraft().displayGuiScreen(gui);
        });
    }
}