package com.mmxw11.nametags.settings.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.NameTagMode;
import com.mmxw11.nametags.settings.ModSettingsProfile;
import com.mmxw11.nametags.technical.NameTagHandler;
import com.mmxw11.nametags.util.ChatHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SettingsGUI extends GuiScreen {

    private NameTagHandler nhandler;
    private ModSettingsProfile modSettings;
    private List<AbstractGUIButton> tbuttons;

    public SettingsGUI() {
        NameTagModClient mod = NameTagModClient.getInstance();
        this.nhandler = mod.getNHandler();
        this.modSettings = mod.getModSettings();
        this.tbuttons = new ArrayList<>();
    }

    @Override
    public void initGui() {
        buttonList.clear();
        tbuttons.clear();
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();
        buttonList.add(new ModToggleButton(1, width, height));
        buttonList.add(new ChangeOnTablistButton(2, width, height));
        buttonList.add(new ChangeModeButton(4, width, height));
        buttonList.add(new ChangeInChatButton(5, width, height));
        buttonList.add(new RPlayerTagOnLeaveButton(6, width, height));
        buttonList.add(new ResetAllCTagsButton(8, width, height));
        tbuttons.add(new DisplayExtraScoreboardTagsButton(3, width, height));
        tbuttons.add(new ARemoveTeamTagsButton(7, width, height));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            drawScreen0(mouseX, mouseY, partialTicks);
        } catch (Exception e) {
            ChatHelper.sendMessageToPlayer("&cCannot open settings gui?! " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawScreen0(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode == NameTagMode.EDIT) {
            tbuttons.forEach(b -> b.drawButton(mc, mouseX, mouseY, partialTicks));
        }
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        String name = TextFormatting.RED + "" + TextFormatting.BOLD + NameTagModClient.NAME + " Mod V-" + NameTagModClient.VERSION + " Settings";
        drawCenteredString(fontRenderer, name, res.getScaledWidth() / 2, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawToolTips(mouseX, mouseY, res);
    }

    @Override
    protected void mouseClicked(int p_mouseClicked_1_, int p_mouseClicked_2_, int p_mouseClicked_3_) throws IOException {
        super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_);
        if (p_mouseClicked_3_ != 0) {
            return;
        }
        tbuttons.forEach(b -> {
            if (b.mousePressed(mc, p_mouseClicked_1_, p_mouseClicked_2_)) {
                b.playPressSound(mc.getSoundHandler());
                try {
                    actionPerformed(b);
                } catch (IOException e) {
                }
            }
        });
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        try {
            NameTagModClient.getInstance().getFileManager().saveSettingsFile(modSettings);
            ChatHelper.sendMessageToPlayer("&aSettings successfully saved.", true);
        } catch (IOException e) {
            ChatHelper.sendMessageToPlayer(TextFormatting.RED + "Unable to save config fle! Check console for details: " + e.getMessage());
            e.printStackTrace();
        }
        if (modSettings.isRemovePlayerTagsOnLeave()) {
            nhandler.startTask();
        } else {
            nhandler.stopTask();
        }
    }

    private void drawToolTips(int mouseX, int mouseY, ScaledResolution res) {
        int boxX = (width - 410) / 2;
        int boxY = (height - 166) / 2 + 25;
        int defaultX = 199;
        int defaultY = 18;
        if (mouseX > boxX && mouseX < boxX + defaultX && mouseY > boxY && mouseY < boxY + defaultY) {
            List<String> list = new ArrayList<>();
            list.add(TextFormatting.YELLOW + NameTagModClient.NAME + ": "
                    + (modSettings.isEnabled() ? TextFormatting.GREEN + "Enabled" : TextFormatting.RED + "Disabled"));
            list.add(TextFormatting.WHITE + "Click to enabled or disabled the mod.");
            drawHoveringText(list, mouseX, mouseY, fontRenderer);
        } else if (mouseX > boxX && mouseX < boxX + defaultX && mouseY > (boxY + 25) && mouseY < (boxY + 25) + defaultY) {
            List<String> list = new ArrayList<>();
            list.add(TextFormatting.YELLOW + "Change on tablist: " + (modSettings.isChangeOnTablist()
                    ? TextFormatting.GREEN + "Enabled"
                    : TextFormatting.RED + "Disabled"));
            list.add(TextFormatting.WHITE + "Replaces names on tablist when");
            list.add(TextFormatting.WHITE + "NameTagMode is set to EDIT");
            list.add(TextFormatting.WHITE + "or adds " + TextFormatting.GRAY + "[H]" + TextFormatting.WHITE + " tag to them if");
            list.add(TextFormatting.WHITE + "NameTagMode set to HIDE.");
            list.add("");
            list.add(TextFormatting.RED + "WARNING: This might now work with");
            list.add(TextFormatting.RED + "other mods which change how");
            list.add(TextFormatting.RED + "the tablist works.");
            drawHoveringText(list, mouseX, mouseY, fontRenderer);
        } else if (mouseX > boxX && mouseX < boxX + defaultX && mouseY > (boxY + 50) && mouseY < (boxY + 50) + defaultY) {
            NameTagMode mode = modSettings.getNameTagMode();
            if (mode == NameTagMode.EDIT) {
                List<String> list = new ArrayList<>();
                list.add(TextFormatting.YELLOW + "Display extra scoreboard tags: " + (modSettings.IsDisplayEScoreboardTags()
                        ? TextFormatting.GREEN + "Enabled"
                        : TextFormatting.RED + "Disabled"));
                list.add(TextFormatting.WHITE + "Click to toggle the visibility of other");
                list.add(TextFormatting.WHITE + "scoreboard tags above/under players' names.");
                list.add(TextFormatting.WHITE + "For instance sometimes there might be");
                list.add(TextFormatting.WHITE + "a health tag under players' names.");
                drawHoveringText(list, mouseX, mouseY, fontRenderer);
            }
        } else if (mouseX > (boxX + 211) && mouseX < (boxX + 211) + defaultX && mouseY > boxY && mouseY < boxY + defaultY) {
            List<String> list = new ArrayList<>();
            NameTagMode mode = modSettings.getNameTagMode();
            list.add(TextFormatting.YELLOW + "NameTagMode: " +
                    TextFormatting.GRAY + (mode == null ? "NOT_SET" : mode.getName()));
            list.add(TextFormatting.WHITE + "Click to change NameTagMode.");
            drawHoveringText(list, mouseX, mouseY, fontRenderer);
        } else if (mouseX > (boxX + 211) && mouseX < (boxX + 211) + defaultX && mouseY > (boxY + 25) && mouseY < (boxY + 25) + defaultY) {
            List<String> list = new ArrayList<>();
            list.add(TextFormatting.YELLOW + "Change in chat: " + (modSettings.isChangeInChat()
                    ? TextFormatting.GREEN + "Enabled"
                    : TextFormatting.RED + "Disabled"));
            list.add(TextFormatting.WHITE + "Replaces names in chat when");
            list.add(TextFormatting.WHITE + "NameTagMode is set to EDIT");
            list.add(TextFormatting.WHITE + "or adds " + TextFormatting.GRAY + "[H]" + TextFormatting.WHITE + " tag to them if");
            list.add(TextFormatting.WHITE + "NameTagMode set to HIDE.");
            drawHoveringText(list, mouseX, mouseY, fontRenderer);
        } else if (mouseX > (boxX + 211) && mouseX < (boxX + 211) + defaultX && mouseY > (boxY + 50) && mouseY < (boxY + 50) + defaultY) {
            List<String> list = new ArrayList<>();
            list.add(TextFormatting.YELLOW + "Remove player tags on leave: " + (modSettings.isRemovePlayerTagsOnLeave()
                    ? TextFormatting.GREEN + "Enabled"
                    : TextFormatting.RED + "Disabled"));
            list.add(TextFormatting.WHITE + "Click to toggle if you'd like to");
            list.add(TextFormatting.WHITE + "automatically remove saved players'");
            list.add(TextFormatting.WHITE + "custom tags when they leave the world/server.");
            list.add(TextFormatting.WHITE + "");
            list.add(TextFormatting.RED + "This is recommended to keep enabled");
            list.add(TextFormatting.RED + "if you have a lot of custom tags your game");
            list.add(TextFormatting.RED + "may get laggy after a while.");
            drawHoveringText(list, mouseX, mouseY, fontRenderer);
        } else if (mouseX > (boxX + 105) && mouseX < (boxX + 105) + defaultX && mouseY > (boxY + 75) && mouseY < (boxY + 75) + defaultY) {
            NameTagMode mode = modSettings.getNameTagMode();
            if (mode == NameTagMode.EDIT) {
                List<String> list = new ArrayList<>();
                list.add(TextFormatting.YELLOW + "Auto remove team tags: " + (modSettings.isAutoRemoveTeamTags()
                        ? TextFormatting.GREEN + "Enabled"
                        : TextFormatting.RED + "Disabled"));
                list.add(TextFormatting.WHITE + "Click to toggle if team prefixes");
                list.add(TextFormatting.WHITE + "and suffixes should be removed");
                list.add(TextFormatting.WHITE + "from players' names by default.");
                drawHoveringText(list, mouseX, mouseY, fontRenderer);
            }
        } else if (mouseX > (boxX + 105) && mouseX < (boxX + 105) + defaultX && mouseY > (boxY + 100) && mouseY < (boxY + 100) + defaultY) {
            List<String> list = new ArrayList<>();
            list.add(TextFormatting.RED + "Reset all custom tags");
            list.add(TextFormatting.WHITE + "Click to reset all saved cutom tags.");
            list.add("");
            int tags = nhandler.getTotalCustomTagsAmount();
            if (tags == 0) {
                list.add(TextFormatting.GREEN + "No saved custom tags were found.");
            } else {
                list.add(TextFormatting.WHITE + "There are currently " + TextFormatting.YELLOW + tags +
                        TextFormatting.WHITE + " saved custom tag" + (tags == 1 ? "" : "s") + ".");
            }
            drawHoveringText(list, mouseX, mouseY, fontRenderer);
        }
    }
}