package com.mmxw11.nametags.renderer;

import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.technical.NameDataProfile;
import com.mmxw11.nametags.util.ChatHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Team.EnumVisible;

public class NameTagRenderer {

    private NameTagModClient mod;

    public NameTagRenderer(NameTagModClient mod) {
        this.mod = mod;
    }

    public void renderPlayerEntityTag(RenderLivingBase<EntityLivingBase> rlbase, EntityPlayer ep, NameDataProfile nprofile, double x, double y, double z) {
        String plate = "";
        NetHandlerPlayClient nhpclient = Minecraft.getMinecraft().getConnection();
        NetworkPlayerInfo info = nhpclient.getPlayerInfo(ep.getUniqueID());
        if (info != null) {
            boolean aremove = mod.getModSettings().isAutoRemoveTeamTags();
            ScorePlayerTeam team = info.getPlayerTeam();
            String cprefix = nprofile.getPrefix();
            String csuffix = nprofile.getSuffix();
            if (cprefix != null) {
                plate = ChatHelper.translateAlternateColorCodes('&', cprefix);
            } else if (team != null && !aremove) {
                plate = team.getPrefix();
            }
            plate += nprofile.getName();
            if (csuffix != null) {
                plate += ChatHelper.translateAlternateColorCodes('&', csuffix);
            } else if (team != null && !aremove) {
                plate += team.getSuffix();
            }
        }
        renderPlayerEntityTag(rlbase, ep, plate, x, y, z);
    }

    private void renderPlayerEntityTag(RenderLivingBase<EntityLivingBase> rlbase, EntityPlayer ep, String str, double x, double y, double z) {
        RenderManager renderManager = rlbase.getRenderManager();
        if (!canRenderName(renderManager, ep)) {
            return;
        }
        double distance = ep.getDistanceSq(renderManager.renderViewEntity);
        double f = RenderLivingBase.NAME_TAG_RANGE_SNEAK;
        if (distance < (f * f)) {
            GlStateManager.alphaFunc(516, 0.1F);
            boolean tview = renderManager.options.thirdPersonView == 2;
            float height = ep.height + 0.5F;
            int fixedHeight = "deadmau5".equals(str) ? -10 : 0;
            render(rlbase, ep, str, x, y, z, height, fixedHeight, tview, distance);
        }
    }

    private void render(RenderLivingBase<EntityLivingBase> rlbase, EntityPlayer ep, String str, double x, double y, double z, float height, int fixedHeight, boolean tview,
            double distance) {
        RenderManager renderManager = rlbase.getRenderManager();
        if (mod.getModSettings().IsDisplayEScoreboardTags()) {
            y += renderScoreboardObjects(renderManager, ep, x, y, z, height, fixedHeight, tview, distance);
        }
        EntityRenderer.drawNameplate(renderManager.getFontRenderer(), str, (float) x, (float) y + height, (float) z, fixedHeight, renderManager.playerViewY,
                renderManager.playerViewX, tview, ep.isSneaking());
    }

    private double renderScoreboardObjects(RenderManager renderManager, EntityPlayer ep, double x, double y, double z, float height, int fixedHeight, boolean tview,
            double distance) {
        if (ep.isSneaking()) {
            return 0;
        }
        if (distance < 100.0D) {
            Scoreboard scoreboard = ep.getWorldScoreboard();
            ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
            if (scoreobjective != null) {
                Score score = scoreboard.getOrCreateScore(ep.getName(), scoreobjective);
                String str = score.getScorePoints() + " " + scoreobjective.getDisplayName();
                EntityRenderer.drawNameplate(renderManager.getFontRenderer(), str, (float) x, (float) y + height, (float) z, fixedHeight, renderManager.playerViewY,
                        renderManager.playerViewX, tview, false);
                return renderManager.getFontRenderer().FONT_HEIGHT * 1.15F * 0.02666667F;
            }
        }
        return 0;
    }

    private boolean canRenderName(RenderManager renderManager, EntityPlayer entity) {
        EntityPlayerSP ep = Minecraft.getMinecraft().player;
        boolean flag = !entity.isInvisibleToPlayer(ep);
        if (entity != ep) {
            Team team = entity.getTeam();
            Team team1 = ep.getTeam();
            if (team != null) {
                EnumVisible team$enumvisible = team.getNameTagVisibility();
                switch (team$enumvisible) {
                    case ALWAYS:
                        return flag;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null ? flag : !team.isSameTeam(team1) && flag;
                    default:
                        return true;
                }
            }
        }
        return Minecraft.isGuiEnabled() && entity != renderManager.renderViewEntity && !entity.isInvisibleToPlayer(ep) && !entity.isBeingRidden();
    }
}