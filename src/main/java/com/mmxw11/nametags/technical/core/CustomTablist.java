package com.mmxw11.nametags.technical.core;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.collect.Ordering;
import com.mmxw11.nametags.NameTagMode;
import com.mmxw11.nametags.technical.NameDataProfile;
import com.mmxw11.nametags.technical.NameTagHandler;
import com.mmxw11.nametags.util.ChatHelper;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class CustomTablist extends Gui {

    private NameTagHandler nhandler;
    private Minecraft minecraft;
    private final Ordering<NetworkPlayerInfo> order;

    public CustomTablist(NameTagHandler nhandler) {
        this.nhandler = nhandler;
        this.minecraft = Minecraft.getMinecraft();
        TablistPlayerComparator comparator = new TablistPlayerComparator();
        this.order = Ordering.from(comparator);
    }

    public void renderPlayerlist() {
        ScaledResolution res = new ScaledResolution(minecraft);
        int width = res.getScaledWidth();
        World world = minecraft.theWorld;
        Scoreboard sboard = world.getScoreboard();
        ScoreObjective sobjective = sboard.getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient nhpclient = minecraft.getNetHandler();
        List<NetworkPlayerInfo> nplayers = order.<NetworkPlayerInfo>sortedCopy(nhpclient.getPlayerInfoMap());
        int i = 0;
        int j = 0;
        for (NetworkPlayerInfo info : nplayers) {
            int k = minecraft.fontRendererObj.getStringWidth(getPlayerName(info));
            i = Math.max(i, k);
            if (sobjective != null && sobjective.getRenderType() != IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                k = minecraft.fontRendererObj.getStringWidth(" " + sboard.getValueFromObjective(info.getGameProfile().getName(), sobjective).getScorePoints());
                j = Math.max(j, k);
            }
        }
        nplayers = nplayers.subList(0, Math.min(nplayers.size(), 80));
        int l3 = nplayers.size();
        int i4 = l3;
        int j4;
        for (j4 = 1; i4 > 20; i4 = (l3 + j4 - 1) / j4) {
            ++j4;
        }
        boolean flag = minecraft.isIntegratedServerRunning() || nhpclient.getNetworkManager().getIsencrypted();
        int l;
        if (sobjective != null) {
            if (sobjective.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
                l = 90;
            } else {
                l = j;
            }
        } else {
            l = 0;
        }
        int i1 = Math.min(j4 * ((flag ? 9 : 0) + i + l + 13), width - 50) / j4;
        int j1 = width / 2 - (i1 * j4 + (j4 - 1) * 5) / 2;
        int k1 = 10;
        int l1 = i1 * j4 + (j4 - 1) * 5;
        List<String> list1 = null;
        List<String> list2 = null;
        IChatComponent footer = (IChatComponent) getPlayerListField("field_175255_h", "footer");
        IChatComponent header = (IChatComponent) getPlayerListField("field_175256_i", "header");
        if (header != null) {
            list1 = minecraft.fontRendererObj.listFormattedStringToWidth(header.getFormattedText(), width - 50);
            for (String s : list1) {
                l1 = Math.max(l1, minecraft.fontRendererObj.getStringWidth(s));
            }
        }
        if (footer != null) {
            list2 = minecraft.fontRendererObj.listFormattedStringToWidth(footer.getFormattedText(), width - 50);
            for (String s2 : list2) {
                l1 = Math.max(l1, minecraft.fontRendererObj.getStringWidth(s2));
            }
        }
        if (list1 != null) {
            drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list1.size() * minecraft.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);
            for (String s3 : list1) {
                int i2 = minecraft.fontRendererObj.getStringWidth(s3);
                minecraft.fontRendererObj.drawStringWithShadow(s3, width / 2 - i2 / 2, k1, -1);
                k1 += minecraft.fontRendererObj.FONT_HEIGHT;
            }
            ++k1;
        }
        drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + i4 * 9, Integer.MIN_VALUE);
        for (int k4 = 0; k4 < l3; ++k4) {
            int l4 = k4 / i4;
            int i5 = k4 % i4;
            int j2 = j1 + l4 * i1 + l4 * 5;
            int k2 = k1 + i5 * 9;
            drawRect(j2, k2, j2 + i1, k2 + 8, 553648127);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            if (k4 < nplayers.size()) {
                NetworkPlayerInfo networkplayerinfo1 = nplayers.get(k4);
                String s1 = this.getPlayerName(networkplayerinfo1);
                GameProfile gameprofile = networkplayerinfo1.getGameProfile();
                if (flag) {
                    EntityPlayer entityplayer = world.getPlayerEntityByUUID(gameprofile.getId());
                    boolean flag1 = entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.CAPE)
                            && (gameprofile.getName().equals("Dinnerbone") || gameprofile.getName().equals("Grumm"));
                    minecraft.getTextureManager().bindTexture(networkplayerinfo1.getLocationSkin());
                    int l2 = 8 + (flag1 ? 8 : 0);
                    int i3 = 8 * (flag1 ? -1 : 1);
                    Gui.drawScaledCustomSizeModalRect(j2, k2, 8.0F, l2, 8, i3, 8, 8, 64.0F, 64.0F);
                    if (entityplayer != null && entityplayer.isWearing(EnumPlayerModelParts.HAT)) {
                        int j3 = 8 + (flag1 ? 8 : 0);
                        int k3 = 8 * (flag1 ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(j2, k2, 40.0F, j3, 8, k3, 8, 8, 64.0F, 64.0F);
                    }
                    j2 += 9;
                }
                if (networkplayerinfo1.getGameType() == WorldSettings.GameType.SPECTATOR) {
                    s1 = EnumChatFormatting.ITALIC + s1;
                    minecraft.fontRendererObj.drawStringWithShadow(s1, j2, k2, -1862270977);
                } else {
                    minecraft.fontRendererObj.drawStringWithShadow(s1, j2, k2, -1);
                }
                if (sobjective != null && networkplayerinfo1.getGameType() != WorldSettings.GameType.SPECTATOR) {
                    int k5 = j2 + i + 1;
                    int l5 = k5 + l;
                    if (l5 - k5 > 5) {
                        drawScoreboardValues(sobjective, k2, gameprofile.getName(), k5, l5, networkplayerinfo1);
                    }
                }
                drawPing(i1, j2 - (flag ? 9 : 0), k2, networkplayerinfo1);
            }
        }
        if (list2 != null) {
            k1 = k1 + i4 * 9 + 1;
            drawRect(width / 2 - l1 / 2 - 1, k1 - 1, width / 2 + l1 / 2 + 1, k1 + list2.size() * minecraft.fontRendererObj.FONT_HEIGHT, Integer.MIN_VALUE);
            for (String s4 : list2) {
                int j5 = minecraft.fontRendererObj.getStringWidth(s4);
                minecraft.fontRendererObj.drawStringWithShadow(s4, width / 2 - j5 / 2, k1, -1);
                k1 += minecraft.fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    private void drawPing(int p_175245_1_, int p_175245_2_, int p_175245_3_, NetworkPlayerInfo info) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(icons);
        int i = 0;
        int j = 0;
        if (info.getResponseTime() < 0) {
            j = 5;
        } else if (info.getResponseTime() < 150) {
            j = 0;
        } else if (info.getResponseTime() < 300) {
            j = 1;
        } else if (info.getResponseTime() < 600) {
            j = 2;
        } else if (info.getResponseTime() < 1000) {
            j = 3;
        } else {
            j = 4;
        }
        this.zLevel += 100.0F;
        this.drawTexturedModalRect(p_175245_2_ + p_175245_1_ - 11, p_175245_3_, 0 + i * 10, 176 + j * 8, 10, 8);
        this.zLevel -= 100.0F;
    }

    private void drawScoreboardValues(ScoreObjective p_175247_1_, int p_175247_2_, String p_175247_3_, int p_175247_4_, int p_175247_5_, NetworkPlayerInfo p_175247_6_) {
        long lastTimeOpened = 0;
        Object lastTime = getPlayerListField("field_175253_j", "lastTimeOpened");
        if (lastTime != null) {
            lastTimeOpened = (long) lastTime;
        }
        int i = p_175247_1_.getScoreboard().getValueFromObjective(p_175247_3_, p_175247_1_).getScorePoints();
        if (p_175247_1_.getRenderType() == IScoreObjectiveCriteria.EnumRenderType.HEARTS) {
            minecraft.getTextureManager().bindTexture(icons);
            if (lastTimeOpened == p_175247_6_.func_178855_p()) {
                if (i < p_175247_6_.func_178835_l()) {
                    p_175247_6_.func_178846_a(Minecraft.getSystemTime());
                    p_175247_6_.func_178844_b(minecraft.ingameGUI.getUpdateCounter() + 20);
                } else if (i > p_175247_6_.func_178835_l()) {
                    p_175247_6_.func_178846_a(Minecraft.getSystemTime());
                    p_175247_6_.func_178844_b(minecraft.ingameGUI.getUpdateCounter() + 10);
                }
            }
            if (Minecraft.getSystemTime() - p_175247_6_.func_178847_n() > 1000L || lastTimeOpened != p_175247_6_.func_178855_p()) {
                p_175247_6_.func_178836_b(i);
                p_175247_6_.func_178857_c(i);
                p_175247_6_.func_178846_a(Minecraft.getSystemTime());
            }
            p_175247_6_.func_178843_c(lastTimeOpened);
            p_175247_6_.func_178836_b(i);
            int j = MathHelper.ceiling_float_int(Math.max(i, p_175247_6_.func_178860_m()) / 2.0F);
            int k = Math.max(MathHelper.ceiling_float_int(i / 2), Math.max(MathHelper.ceiling_float_int(p_175247_6_.func_178860_m() / 2), 10));
            boolean flag = p_175247_6_.func_178858_o() > minecraft.ingameGUI.getUpdateCounter()
                    && (p_175247_6_.func_178858_o() - minecraft.ingameGUI.getUpdateCounter()) / 3L % 2L == 1L;
            if (j > 0) {
                float f = Math.min((float) (p_175247_5_ - p_175247_4_ - 4) / (float) k, 9.0F);
                if (f > 3.0F) {
                    for (int l = j; l < k; ++l) {
                        drawTexturedModalRect(p_175247_4_ + l * f, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                    }
                    for (int j1 = 0; j1 < j; ++j1) {
                        drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, flag ? 25 : 16, 0, 9, 9);
                        if (flag) {
                            if (j1 * 2 + 1 < p_175247_6_.func_178860_m()) {
                                drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, 70, 0, 9, 9);
                            }
                            if (j1 * 2 + 1 == p_175247_6_.func_178860_m()) {
                                drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, 79, 0, 9, 9);
                            }
                        }
                        if (j1 * 2 + 1 < i) {
                            drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, j1 >= 10 ? 160 : 52, 0, 9, 9);
                        }
                        if (j1 * 2 + 1 == i) {
                            drawTexturedModalRect(p_175247_4_ + j1 * f, p_175247_2_, j1 >= 10 ? 169 : 61, 0, 9, 9);
                        }
                    }
                } else {
                    float f1 = MathHelper.clamp_float(i / 20.0F, 0.0F, 1.0F);
                    int i1 = (int) ((1.0F - f1) * 255.0F) << 16 | (int) (f1 * 255.0F) << 8;
                    String s = "" + i / 2.0F;
                    if (p_175247_5_ - minecraft.fontRendererObj.getStringWidth(s + "hp") >= p_175247_4_) {
                        s = s + "hp";
                    }
                    minecraft.fontRendererObj.drawStringWithShadow(s, (p_175247_5_ + p_175247_4_) / 2 - minecraft.fontRendererObj.getStringWidth(s) / 2, p_175247_2_, i1);
                }
            }
        } else {
            String s1 = EnumChatFormatting.YELLOW + "" + i;
            minecraft.fontRendererObj.drawStringWithShadow(s1, p_175247_5_ - minecraft.fontRendererObj.getStringWidth(s1), p_175247_2_, 16777215);
        }
    }

    private Object getPlayerListField(String nameObf, String nameDeObf) {
        GuiPlayerTabOverlay guiClass = minecraft.ingameGUI.getTabList();
        try {
            String name = fieldExists(nameObf, guiClass) ? nameObf : nameDeObf;
            Field field = guiClass.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(guiClass);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            return null;
        }
    }

    private boolean fieldExists(String name, GuiPlayerTabOverlay guiClass) {
        try {
            guiClass.getClass().getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return false;
        }
        return true;
    }

    private String getPlayerName(NetworkPlayerInfo info) {
        GameProfile profile = info.getGameProfile();
        ScorePlayerTeam team = info.getPlayerTeam();
        String fname = info.getDisplayName() != null ? info.getDisplayName().getFormattedText() : ScorePlayerTeam.formatPlayerName(team, profile.getName());
        String nfname = StringUtils.stripControlCodes(fname);
        if (nfname.isEmpty() || nfname.contains("[NPC]")) {
            return fname;
        }
        NameDataProfile nprofile = nhandler.getCustomTag(profile.getName());
        if (nprofile == null) {
            return fname;
        }
        NameTagMode mode = nhandler.getModSettings().getNameTagMode();
        if (mode == NameTagMode.HIDE) {
            return ChatHelper.translateAlternateColorCodes('&', "&7[H]&f " + fname);
        } else if (mode == NameTagMode.EDIT) {
            String renderName;
            String cprefix = nprofile.getPrefix();
            String csuffix = nprofile.getSuffix();
            if (team == null) {
                renderName = nprofile.getName();
                if (cprefix != null) {
                    renderName = ChatHelper.translateAlternateColorCodes('&', cprefix) + renderName;
                }
                if (csuffix != null) {
                    renderName += ChatHelper.translateAlternateColorCodes('&', csuffix);
                }
            } else {
                boolean aremove = nhandler.getModSettings().isAutoRemoveTeamTags();
                if (aremove || cprefix != null || csuffix != null) {
                    renderName = nprofile.getName();
                    if (cprefix != null) {
                        renderName = ChatHelper.translateAlternateColorCodes('&', cprefix) + renderName;
                    } else if (!aremove) {
                        renderName = team.getColorPrefix() + renderName;
                    }
                    if (csuffix != null) {
                        renderName += ChatHelper.translateAlternateColorCodes('&', csuffix);
                    } else if (!aremove) {
                        renderName += team.getColorSuffix();
                    }
                } else {
                    renderName = team.formatString(nprofile.getName());
                }
            }
            return renderName;
        }
        return fname;
    }
}