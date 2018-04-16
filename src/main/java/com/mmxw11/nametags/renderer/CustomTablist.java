package com.mmxw11.nametags.renderer;

import java.lang.reflect.Field;
import java.util.List;

import com.google.common.collect.Ordering;
import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.NameTagMode;
import com.mmxw11.nametags.technical.NameDataProfile;
import com.mmxw11.nametags.util.ChatHelper;
import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.IScoreCriteria.EnumRenderType;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public class CustomTablist extends Gui {

    private NameTagModClient mod;
    private Minecraft mc;
    private final Ordering<NetworkPlayerInfo> order;

    public CustomTablist(NameTagModClient mod) {
        this.mod = mod;
        this.mc = Minecraft.getMinecraft();
        PlayerTablistComparator comparator = new PlayerTablistComparator();
        this.order = Ordering.from(comparator);
    }

    public void renderPlayerlist() {
        ScaledResolution res = new ScaledResolution(mc);
        int width = res.getScaledWidth();
        World world = mc.world;
        Scoreboard sboard = world.getScoreboard();
        ScoreObjective sobjective = sboard.getObjectiveInDisplaySlot(0);
        NetHandlerPlayClient nhpclient = mc.getConnection();
        List<NetworkPlayerInfo> nplayers = order.<NetworkPlayerInfo>sortedCopy(nhpclient.getPlayerInfoMap());
        int lvt_6_1_ = 0;
        int lvt_7_1_ = 0;
        FontRenderer frenderer = mc.fontRenderer;
        int lvt_10_2_;
        for (NetworkPlayerInfo ninfo : nplayers) {
            lvt_10_2_ = frenderer.getStringWidth(getPlayerName(ninfo));
            lvt_6_1_ = Math.max(lvt_6_1_, lvt_10_2_);
            if (sobjective != null && sobjective.getRenderType() != EnumRenderType.HEARTS) {
                lvt_10_2_ = frenderer.getStringWidth(" " + sboard
                        .getOrCreateScore(ninfo.getGameProfile().getName(), sobjective).getScorePoints());
                lvt_7_1_ = Math.max(lvt_7_1_, lvt_10_2_);
            }
        }
        nplayers = nplayers.subList(0, Math.min(nplayers.size(), 80));
        int arg31 = nplayers.size();
        int arg32 = arg31;
        for (lvt_10_2_ = 1; arg32 > 20; arg32 = (arg31 + lvt_10_2_ - 1) / lvt_10_2_) {
            ++lvt_10_2_;
        }
        boolean lvt_11_1_ = mc.isIntegratedServerRunning() || nhpclient.getNetworkManager().isEncrypted();
        int lvt_12_3_;
        if (sobjective != null) {
            if (sobjective.getRenderType() == EnumRenderType.HEARTS) {
                lvt_12_3_ = 90;
            } else {
                lvt_12_3_ = lvt_7_1_;
            }
        } else {
            lvt_12_3_ = 0;
        }
        int lvt_13_1_ = Math.min(lvt_10_2_ * ((lvt_11_1_ ? 9 : 0) + lvt_6_1_ + lvt_12_3_ + 13),
                width - 50) / lvt_10_2_;
        int lvt_14_1_ = width / 2 - (lvt_13_1_ * lvt_10_2_ + (lvt_10_2_ - 1) * 5) / 2;
        int lvt_15_1_ = 10;
        int lvt_16_1_ = lvt_13_1_ * lvt_10_2_ + (lvt_10_2_ - 1) * 5;
        List<String> list1 = null;
        List<String> list2 = null;
        ITextComponent footer = (ITextComponent) getPlayerListField("field_175255_h", "footer");
        ITextComponent header = (ITextComponent) getPlayerListField("field_175256_i", "header");
        if (header != null) {
            list1 = frenderer.listFormattedStringToWidth(header.getFormattedText(), width - 50);
            for (String value : list1) {
                lvt_16_1_ = Math.max(lvt_16_1_, frenderer.getStringWidth(value));
            }
        }
        if (footer != null) {
            list2 = frenderer.listFormattedStringToWidth(footer.getFormattedText(), width - 50);
            for (String value : list1) {
                lvt_16_1_ = Math.max(lvt_16_1_, frenderer.getStringWidth(value));
            }
        }
        if (list1 != null) {
            drawRect(width / 2 - lvt_16_1_ / 2 - 1, lvt_15_1_ - 1, width / 2 + lvt_16_1_ / 2 + 1,
                    lvt_15_1_ + list1.size() * frenderer.FONT_HEIGHT, Integer.MIN_VALUE);
            for (String s : list1) {
                int swidth = frenderer.getStringWidth(s);
                frenderer.drawStringWithShadow(s, (float) (width / 2 - swidth / 2), (float) lvt_15_1_, -1);
                lvt_15_1_ += frenderer.FONT_HEIGHT;
            }
            ++lvt_15_1_;
        }
        drawRect(width / 2 - lvt_16_1_ / 2 - 1, lvt_15_1_ - 1, width / 2 + lvt_16_1_ / 2 + 1, lvt_15_1_ + arg32 * 9, Integer.MIN_VALUE);
        for (int arg35 = 0; arg35 < arg31; ++arg35) {
            int arg36 = arg35 / arg32;
            int swidth = arg35 % arg32;
            int lvt_22_1_ = lvt_14_1_ + arg36 * lvt_13_1_ + arg36 * 5;
            int lvt_23_1_ = lvt_15_1_ + swidth * 9;
            drawRect(lvt_22_1_, lvt_23_1_, lvt_22_1_ + lvt_13_1_, lvt_23_1_ + 8, 553648127);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
                    SourceFactor.ONE, DestFactor.ZERO);
            if (arg35 < nplayers.size()) {
                NetworkPlayerInfo ninfo2 = nplayers.get(arg35);
                GameProfile lvt_25_1_ = ninfo2.getGameProfile();
                int lvt_28_2_;
                if (lvt_11_1_) {
                    EntityPlayer lvt_26_2_ = world.getPlayerEntityByUUID(lvt_25_1_.getId());
                    boolean lvt_27_2_ = lvt_26_2_ != null && lvt_26_2_.isWearing(EnumPlayerModelParts.CAPE)
                            && ("Dinnerbone".equals(lvt_25_1_.getName()) || "Grumm".equals(lvt_25_1_.getName()));
                    mc.getTextureManager().bindTexture(ninfo2.getLocationSkin());
                    lvt_28_2_ = 8 + (lvt_27_2_ ? 8 : 0);
                    int lvt_29_1_ = 8 * (lvt_27_2_ ? -1 : 1);
                    Gui.drawScaledCustomSizeModalRect(lvt_22_1_, lvt_23_1_, 8.0F, (float) lvt_28_2_, 8, lvt_29_1_, 8, 8,
                            64.0F, 64.0F);
                    if (lvt_26_2_ != null && lvt_26_2_.isWearing(EnumPlayerModelParts.HAT)) {
                        int lvt_30_1_ = 8 + (lvt_27_2_ ? 8 : 0);
                        int lvt_31_1_ = 8 * (lvt_27_2_ ? -1 : 1);
                        Gui.drawScaledCustomSizeModalRect(lvt_22_1_, lvt_23_1_, 40.0F, (float) lvt_30_1_, 8, lvt_31_1_,
                                8, 8, 64.0F, 64.0F);
                    }
                    lvt_22_1_ += 9;
                }
                String arg37 = getPlayerName(ninfo2);
                if (ninfo2.getGameType() == GameType.SPECTATOR) {
                    frenderer.drawStringWithShadow(TextFormatting.ITALIC + arg37, (float) lvt_22_1_,
                            (float) lvt_23_1_, -1862270977);
                } else {
                    frenderer.drawStringWithShadow(arg37, (float) lvt_22_1_, (float) lvt_23_1_, -1);
                }
                if (sobjective != null && ninfo2.getGameType() != GameType.SPECTATOR) {
                    int arg38 = lvt_22_1_ + lvt_6_1_ + 1;
                    lvt_28_2_ = arg38 + lvt_12_3_;
                    if (lvt_28_2_ - arg38 > 5) {
                        drawScoreboardValues(sobjective, lvt_23_1_, lvt_25_1_.getName(), arg38, lvt_28_2_, ninfo2);
                    }
                }
                drawPing(lvt_13_1_, lvt_22_1_ - (lvt_11_1_ ? 9 : 0), lvt_23_1_, ninfo2);
            }
        }
        if (list2 != null) {
            lvt_15_1_ += arg32 * 9 + 1;
            drawRect(width / 2 - lvt_16_1_ / 2 - 1, lvt_15_1_ - 1,
                    width / 2 + lvt_16_1_ / 2 + 1,
                    lvt_15_1_ + list2.size() * frenderer.FONT_HEIGHT, Integer.MIN_VALUE);
            for (String s : list2) {
                int swidth = frenderer.getStringWidth(s);
                frenderer.drawStringWithShadow(s, (float) (width / 2 - swidth / 2), (float) lvt_15_1_, -1);
                lvt_15_1_ += frenderer.FONT_HEIGHT;
            }
        }
    }

    private void drawPing(int p_drawPing_1_, int p_drawPing_2_, int p_drawPing_3_, NetworkPlayerInfo info) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(ICONS);
        byte lvt_6_6_;
        if (info.getResponseTime() < 0) {
            lvt_6_6_ = 5;
        } else if (info.getResponseTime() < 150) {
            lvt_6_6_ = 0;
        } else if (info.getResponseTime() < 300) {
            lvt_6_6_ = 1;
        } else if (info.getResponseTime() < 600) {
            lvt_6_6_ = 2;
        } else if (info.getResponseTime() < 1000) {
            lvt_6_6_ = 3;
        } else {
            lvt_6_6_ = 4;
        }
        this.zLevel += 100.0F;
        drawTexturedModalRect(p_drawPing_2_ + p_drawPing_1_ - 11, p_drawPing_3_, 0, 176 + lvt_6_6_ * 8, 10, 8);
        this.zLevel -= 100.0F;
    }

    private void drawScoreboardValues(ScoreObjective p_drawScoreboardValues_1_, int p_drawScoreboardValues_2_,
            String p_drawScoreboardValues_3_, int p_drawScoreboardValues_4_, int p_drawScoreboardValues_5_,
            NetworkPlayerInfo p_drawScoreboardValues_6_) {
        long lastTimeOpened = (long) getPlayerListField("field_175253_j", "lastTimeOpened");
        int lvt_7_1_ = p_drawScoreboardValues_1_.getScoreboard()
                .getOrCreateScore(p_drawScoreboardValues_3_, p_drawScoreboardValues_1_).getScorePoints();
        GuiIngame igGui = mc.ingameGUI;
        if (p_drawScoreboardValues_1_.getRenderType() == EnumRenderType.HEARTS) {
            mc.getTextureManager().bindTexture(ICONS);
            if (lastTimeOpened == p_drawScoreboardValues_6_.getRenderVisibilityId()) {
                if (lvt_7_1_ < p_drawScoreboardValues_6_.getLastHealth()) {
                    p_drawScoreboardValues_6_.setLastHealthTime(Minecraft.getSystemTime());
                    p_drawScoreboardValues_6_.setHealthBlinkTime((long) (igGui.getUpdateCounter() + 20));
                } else if (lvt_7_1_ > p_drawScoreboardValues_6_.getLastHealth()) {
                    p_drawScoreboardValues_6_.setLastHealthTime(Minecraft.getSystemTime());
                    p_drawScoreboardValues_6_.setHealthBlinkTime((long) (igGui.getUpdateCounter() + 10));
                }
            }
            if (Minecraft.getSystemTime() - p_drawScoreboardValues_6_.getLastHealthTime() > 1000L
                    || lastTimeOpened != p_drawScoreboardValues_6_.getRenderVisibilityId()) {
                p_drawScoreboardValues_6_.setLastHealth(lvt_7_1_);
                p_drawScoreboardValues_6_.setDisplayHealth(lvt_7_1_);
                p_drawScoreboardValues_6_.setLastHealthTime(Minecraft.getSystemTime());
            }
            p_drawScoreboardValues_6_.setRenderVisibilityId(lastTimeOpened);
            p_drawScoreboardValues_6_.setLastHealth(lvt_7_1_);
            int lvt_8_2_ = MathHelper.ceil((float) Math.max(lvt_7_1_, p_drawScoreboardValues_6_.getDisplayHealth()) / 2.0F);
            int lvt_9_1_ = Math.max(MathHelper.ceil((float) (lvt_7_1_ / 2)),
                    Math.max(MathHelper.ceil((float) (p_drawScoreboardValues_6_.getDisplayHealth() / 2)), 10));
            boolean lvt_10_1_ = p_drawScoreboardValues_6_
                    .getHealthBlinkTime() > (long) igGui.getUpdateCounter()
                    && (p_drawScoreboardValues_6_.getHealthBlinkTime() - (long) igGui.getUpdateCounter()) / 3L
                            % 2L == 1L;
            if (lvt_8_2_ > 0) {
                float lvt_11_1_ = Math.min(
                        (float) (p_drawScoreboardValues_5_ - p_drawScoreboardValues_4_ - 4) / (float) lvt_9_1_, 9.0F);
                if (lvt_11_1_ > 3.0F) {
                    int lvt_12_3_;
                    for (lvt_12_3_ = lvt_8_2_; lvt_12_3_ < lvt_9_1_; ++lvt_12_3_) {
                        drawTexturedModalRect((float) p_drawScoreboardValues_4_ + (float) lvt_12_3_ * lvt_11_1_,
                                (float) p_drawScoreboardValues_2_, lvt_10_1_ ? 25 : 16, 0, 9, 9);
                    }
                    for (lvt_12_3_ = 0; lvt_12_3_ < lvt_8_2_; ++lvt_12_3_) {
                        drawTexturedModalRect((float) p_drawScoreboardValues_4_ + (float) lvt_12_3_ * lvt_11_1_,
                                (float) p_drawScoreboardValues_2_, lvt_10_1_ ? 25 : 16, 0, 9, 9);
                        if (lvt_10_1_) {
                            if (lvt_12_3_ * 2 + 1 < p_drawScoreboardValues_6_.getDisplayHealth()) {
                                drawTexturedModalRect((float) p_drawScoreboardValues_4_ + (float) lvt_12_3_ * lvt_11_1_,
                                        (float) p_drawScoreboardValues_2_, 70, 0, 9, 9);
                            }
                            if (lvt_12_3_ * 2 + 1 == p_drawScoreboardValues_6_.getDisplayHealth()) {
                                drawTexturedModalRect((float) p_drawScoreboardValues_4_ + (float) lvt_12_3_ * lvt_11_1_,
                                        (float) p_drawScoreboardValues_2_, 79, 0, 9, 9);
                            }
                        }
                        if (lvt_12_3_ * 2 + 1 < lvt_7_1_) {
                            drawTexturedModalRect((float) p_drawScoreboardValues_4_ + (float) lvt_12_3_ * lvt_11_1_,
                                    (float) p_drawScoreboardValues_2_, lvt_12_3_ >= 10 ? 160 : 52, 0, 9, 9);
                        }
                        if (lvt_12_3_ * 2 + 1 == lvt_7_1_) {
                            drawTexturedModalRect((float) p_drawScoreboardValues_4_ + (float) lvt_12_3_ * lvt_11_1_,
                                    (float) p_drawScoreboardValues_2_, lvt_12_3_ >= 10 ? 169 : 61, 0, 9, 9);
                        }
                    }
                } else {
                    float arg15 = MathHelper.clamp((float) lvt_7_1_ / 20.0F, 0.0F, 1.0F);
                    int lvt_13_1_ = (int) ((1.0F - arg15) * 255.0F) << 16 | (int) (arg15 * 255.0F) << 8;
                    String lvt_14_1_ = "" + (float) lvt_7_1_ / 2.0F;
                    if (p_drawScoreboardValues_5_
                            - mc.fontRenderer.getStringWidth(lvt_14_1_ + "hp") >= p_drawScoreboardValues_4_) {
                        lvt_14_1_ = lvt_14_1_ + "hp";
                    }
                    mc.fontRenderer.drawStringWithShadow(lvt_14_1_,
                            (float) ((p_drawScoreboardValues_5_ + p_drawScoreboardValues_4_) / 2
                                    - mc.fontRenderer.getStringWidth(lvt_14_1_) / 2),
                            (float) p_drawScoreboardValues_2_, lvt_13_1_);
                }
            }
        } else {
            String arg14 = TextFormatting.YELLOW + "" + lvt_7_1_;
            mc.fontRenderer.drawStringWithShadow(arg14,
                    (float) (p_drawScoreboardValues_5_ - mc.fontRenderer.getStringWidth(arg14)),
                    (float) p_drawScoreboardValues_2_, 16777215);
        }
    }

    private Object getPlayerListField(String nameObf, String nameDeObf) {
        GuiPlayerTabOverlay guiClass = mc.ingameGUI.getTabList();
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
        NameDataProfile nprofile = mod.getNHandler().getCustomTag(profile.getName());
        if (nprofile == null) {
            return fname;
        }
        NameTagMode mode = mod.getModSettings().getNameTagMode();
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
                boolean aremove = mod.getModSettings().isAutoRemoveTeamTags();
                if (aremove || cprefix != null || csuffix != null) {
                    renderName = nprofile.getName();
                    if (cprefix != null) {
                        renderName = ChatHelper.translateAlternateColorCodes('&', cprefix) + renderName;
                    } else if (!aremove) {
                        renderName = team.getPrefix() + renderName;
                    }
                    if (csuffix != null) {
                        renderName += ChatHelper.translateAlternateColorCodes('&', csuffix);
                    } else if (!aremove) {
                        renderName += team.getSuffix();
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