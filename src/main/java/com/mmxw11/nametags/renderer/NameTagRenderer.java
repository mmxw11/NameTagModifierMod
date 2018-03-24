package com.mmxw11.nametags.renderer;

import org.lwjgl.opengl.GL11;

import com.mmxw11.nametags.technical.NameDataProfile;
import com.mmxw11.nametags.technical.NameTagHandler;
import com.mmxw11.nametags.util.ChatHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;

public class NameTagRenderer {

    private NameTagHandler nhandler;

    public NameTagRenderer(NameTagHandler nhandler) {
        this.nhandler = nhandler;
    }

    public void renderPlayerEntityTag(RendererLivingEntity<EntityLivingBase> renderer, EntityPlayer ep, NameDataProfile nprofile, double x, double y, double z) {
        String plate = "";
        NetHandlerPlayClient nhpclient = Minecraft.getMinecraft().getNetHandler();
        NetworkPlayerInfo info = nhpclient.getPlayerInfo(ep.getUniqueID());
        if (info != null) {
            boolean aremove = nhandler.getModSettings().isAutoRemoveTeamTags();
            ScorePlayerTeam team = info.getPlayerTeam();
            String cprefix = nprofile.getPrefix();
            String csuffix = nprofile.getSuffix();
            if (cprefix != null) {
                plate = ChatHelper.translateAlternateColorCodes('&', cprefix);
            } else if (team != null && !aremove) {
                plate = team.getColorPrefix();
            }
            plate += nprofile.getName();
            if (csuffix != null) {
                plate += ChatHelper.translateAlternateColorCodes('&', csuffix);
            } else if (team != null && !aremove) {
                plate += team.getColorSuffix();
            }
        }
        renderPlayerEntityTag(renderer, ep, plate, x, y, z);
    }

    private void renderPlayerEntityTag(RendererLivingEntity<EntityLivingBase> renderer, EntityPlayer ep, String str, double x, double y, double z) {
        RenderManager renderManager = renderer.getRenderManager();
        if (!canRenderName(renderManager, ep)) {
            return;
        }
        double d0 = ep.getDistanceSqToEntity(renderManager.livingPlayer);
        double f = ep.isSneaking() ? RendererLivingEntity.NAME_TAG_RANGE_SNEAK : RendererLivingEntity.NAME_TAG_RANGE;
        if (d0 < (f * f)) {
            GlStateManager.alphaFunc(516, 0.1F);
            if (!ep.isSneaking()) {
                renderOffsetLivingLabel(renderer, ep, str, x, y - (ep.isChild() ? (double) (ep.height / 2.0F) : 0.0D), z, 0.02666667F, d0);
            } else {
                renderSnLivingLabel(renderManager, ep, str, x, y, z);
            }
        }
    }

    private void renderOffsetLivingLabel(RendererLivingEntity<EntityLivingBase> renderer, EntityPlayer ep, String str, double x, double y, double z, float p1, double p2) {
        if (nhandler.getModSettings().IsDisplayEScoreboardTags()) {
            if (p2 < 100.0D) {
                Scoreboard scoreboard = ep.getWorldScoreboard();
                ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(2);
                if (scoreobjective != null) {
                    Score score = scoreboard.getValueFromObjective(ep.getName(), scoreobjective);
                    renderLivingLabel(renderer, score.getScorePoints() + " " + scoreobjective.getDisplayName(), ep, x, y, z, RendererLivingEntity.NAME_TAG_RANGE);
                    y += (double) ((float) renderer.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * p1);
                }
            }
        }
        renderLivingLabel(renderer, str, ep, x, y, z, RendererLivingEntity.NAME_TAG_RANGE);
    }

    private void renderLivingLabel(RendererLivingEntity<EntityLivingBase> renderer, String str, EntityPlayer ep, double x, double y, double z, double maxDistance) {
        RenderManager renderManager = renderer.getRenderManager();
        double d0 = ep.getDistanceSqToEntity(renderManager.livingPlayer);
        if (d0 <= (maxDistance * maxDistance)) {
            FontRenderer fontrenderer = renderManager.getFontRenderer();
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) x + 0.0F, (float) y + ep.height + 0.5F, (float) z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f1, -f1, f1);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            int i = 0;
            int j = fontrenderer.getStringWidth(str) / 2;
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldrenderer.pos((double) (-j - 1), (double) (-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double) (-j - 1), (double) (8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double) (j + 1), (double) (8 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            worldrenderer.pos((double) (j + 1), (double) (-1 + i), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, 553648127);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            fontrenderer.drawString(str, -fontrenderer.getStringWidth(str) / 2, i, -1);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
        }
    }

    private void renderSnLivingLabel(RenderManager renderManager, EntityPlayer ep, String str, double x, double y, double z) {
        FontRenderer frenderer = renderManager.getFontRenderer();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y + ep.height + 0.5F - (ep.isChild() ? ep.height / 2.0F : 0.0F), (float) z);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
        GlStateManager.translate(0.0F, 9.374999F, 0.0F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        int i = frenderer.getStringWidth(str) / 2;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos((double) (-i - 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double) (-i - 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double) (i + 1), 8.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        worldrenderer.pos((double) (i + 1), -1.0D, 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        frenderer.drawString(str, -frenderer.getStringWidth(str) / 2, 0, 553648127);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }

    private boolean canRenderName(RenderManager renderManager, EntityPlayer entity) {
        EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
        if (entity instanceof EntityPlayer && entity != entityPlayerSP) {
            Team team = entity.getTeam();
            Team team1 = entityPlayerSP.getTeam();
            if (team != null) {
                Team.EnumVisible team$enumvisible = team.getNameTagVisibility();
                switch (team$enumvisible) {
                    case ALWAYS:
                        return true;
                    case NEVER:
                        return false;
                    case HIDE_FOR_OTHER_TEAMS:
                        return team1 == null || team.isSameTeam(team1);
                    case HIDE_FOR_OWN_TEAM:
                        return team1 == null || !team.isSameTeam(team1);
                    default:
                        return true;
                }
            }
        }
        return Minecraft.isGuiEnabled() && entity != renderManager.livingPlayer && !entity.isInvisibleToPlayer(entityPlayerSP) && entity.riddenByEntity == null;
    }
}