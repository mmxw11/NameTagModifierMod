package com.mmxw11.nametags;

import com.mmxw11.nametags.render.CustomTablist;
import com.mmxw11.nametags.render.NameTagRenderer;
import com.mmxw11.nametags.settings.ModSettingsProfile;
import com.mmxw11.nametags.technical.NameDataProfile;
import com.mmxw11.nametags.technical.NameTagHandler;
import com.mmxw11.nametags.util.ChatHelper;
import com.mmxw11.nametags.util.IChatComponentBuilder;
import com.mmxw11.nametags.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModListeners {

    private NameTagHandler nhandler;
    private ModSettingsProfile modSettings;
    private NetworkEventListeners nelisteners;
    private CustomTablist ctablist;
    private NameTagRenderer renderer;
    private World currentWorld;
    private boolean newServerJoin;
    private int multipChunkCacheCounter;

    public ModListeners(NameTagHandler nhandler, NetworkEventListeners nelisteners) {
        this.nhandler = nhandler;
        this.modSettings = nhandler.getModSettings();
        this.nelisteners = nelisteners;
        this.ctablist = new CustomTablist(nhandler);
        this.renderer = new NameTagRenderer(nhandler);
    }

    public void register() {
        resetCurrentServer();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void resetCurrentServer() {
        this.currentWorld = null;
        this.newServerJoin = true;
        this.multipChunkCacheCounter = 0;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderLivingEvent(RenderLivingEvent.Specials.Pre<EntityLivingBase> e) {
        if (!(e.entity instanceof EntityPlayer)) {
            return;
        }
        if (!modSettings.isEnabled()) {
            return;
        }
        EntityPlayer ep = (EntityPlayer) e.entity;
        String dname = ep.getDisplayName().getSiblings().get(0).getUnformattedText();
        if (dname.startsWith("[NPC]") || dname.isEmpty()) {
            return;
        }
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode == null) {
            return;
        }
        NameDataProfile nprofile = nhandler.getCustomTag(ep.getName());
        if (nprofile == null) {
            return;
        }
        if (mode == NameTagMode.HIDE) {
            e.setCanceled(true);
        } else if (mode == NameTagMode.EDIT) {
            e.setCanceled(true);
            renderer.renderPlayerEntityTag(e.renderer, ep, nprofile, e.x, e.y, e.z);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderGameOverlayEvent(RenderGameOverlayEvent e) {
        if (e.type != RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
            return;
        }
        if (e.getResult() == Event.Result.DENY) {
            return;
        }
        if (!modSettings.isEnabled() || !modSettings.isChangeOnTablist()) {
            return;
        }
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode == null) {
            return;
        }
        e.setCanceled(true);
        ctablist.renderPlayerlist();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientChatReceivedEvent(ClientChatReceivedEvent e) {
        if (e.type != 0 || e.isCanceled()) {
            return;
        }
        if (!modSettings.isEnabled() || !modSettings.isChangeInChat()) {
            return;
        }
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode == null) {
            return;
        }
        Pair<String, Integer> sdata = ChatHelper.getPossibleChatMsgSender(e.message);
        String sender = sdata.getKey();
        if (sender == null) { // no player sender or not found.
            return;
        }
        NameDataProfile nprofile = nhandler.getCustomTag(sender);
        if (nprofile == null) {
            return;
        }
        if (mode == NameTagMode.HIDE) {
            IChatComponent hicomp = new ChatComponentText(ChatHelper.translateAlternateColorCodes('&', "&7[H] "));
            e.message.getSiblings().add(0, hicomp);
        } else {
            String fmessage = e.message.getFormattedText();
            String src = fmessage.substring(0, sdata.getValue());
            fmessage = nprofile.getName() + fmessage.substring(sdata.getValue() + sender.length());
            String cprefix = nprofile.getPrefix();
            final boolean tags = cprefix != null || modSettings.isAutoRemoveTeamTags();
            if (!tags) {
                fmessage = src + fmessage;
            } else {
                src = src.replaceAll(ChatHelper.MC_COLOR_CHAR + "r", "");
                String prefix = ChatHelper.getPossibleChatMsgSenderPrefix(sender, src);
                if (prefix == null) {
                    fmessage = src + fmessage;
                } else {
                    if (cprefix != null) {
                        if (!StringUtils.stripControlCodes(prefix).isEmpty()) {
                            prefix += " ";
                        }
                        src = src.replace(prefix, ChatHelper.translateAlternateColorCodes('&', cprefix));
                    } else {
                        if (!StringUtils.stripControlCodes(prefix).isEmpty()) {
                            prefix += " ";
                        }
                        src = src.replace(prefix, ChatHelper.MC_COLOR_CHAR + "r");
                    }
                    fmessage = src + fmessage;
                }
            }
            e.setCanceled(true);
            IChatComponentBuilder builder = new IChatComponentBuilder();
            for (IChatComponent icomp : builder.buildIChatComponents(fmessage)) {
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(icomp);
            }
        }
    }

    @SubscribeEvent
    public void RenderWorld(EntityJoinWorldEvent e) {
        EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
        if (ep == null) {
            return;
        }
        if (!e.entity.getUniqueID().equals(ep.getUniqueID())) {
            return;
        }
        World eworld = e.world;
        if (eworld != ep.getEntityWorld()) {
            return;
        }
        if (currentWorld == null || eworld != currentWorld) {
            this.currentWorld = eworld;
            if (newServerJoin) {
                if (modSettings.isEnabled()) {
                    if (modSettings.getNameTagMode() == null) {
                        ChatHelper.sendMessageToPlayer("&cYou haven't set NameTagMode yet! Open the settings gui using the command of /ntagsettings");
                    } else {
                        String cdomain = nelisteners.getCurrentDomain();
                        String ldomain = nelisteners.getLastDomain();
                        if (!cdomain.isEmpty() && !ldomain.isEmpty() && !cdomain.equals(ldomain)) {
                            if (modSettings.isRemovePlayerTagsOnLeave()) {
                                nhandler.removeAllCustomNameTags(true);
                            }
                        }
                    }
                }
                this.newServerJoin = false;
            } else {
                if (nelisteners.isConnectedToServer()) {
                    if (++multipChunkCacheCounter < 2) {
                        return;
                    }
                }
                this.multipChunkCacheCounter = 0;
                if (modSettings.isEnabled() && modSettings.isRemovePlayerTagsOnLeave()) {
                    nhandler.removeAllCustomNameTags(true);
                }
            }
        }
    }
}