package com.mmxw11.nametags.technical;

import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.NameTagMode;
import com.mmxw11.nametags.renderer.CustomTablist;
import com.mmxw11.nametags.renderer.NameTagRenderer;
import com.mmxw11.nametags.settings.ModSettingsProfile;
import com.mmxw11.nametags.util.ChatHelper;
import com.mmxw11.nametags.util.ITetxComponentBuilder;
import com.mmxw11.nametags.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
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

    public ModListeners(NameTagModClient mod) {
        this.nhandler = mod.getNHandler();
        this.modSettings = mod.getModSettings();
        this.nelisteners = mod.getNeListeners();
        this.ctablist = new CustomTablist(mod);
        this.renderer = new NameTagRenderer(mod);
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
    public void onRenderLiving(RenderLivingEvent.Specials.Pre<EntityLivingBase> e) {
        if (!(e.getEntity() instanceof EntityPlayer)) {
            return;
        }
        if (!modSettings.isEnabled()) {
            return;
        }
        EntityPlayer ep = (EntityPlayer) e.getEntity();
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
        e.setCanceled(true);
        if (mode == NameTagMode.EDIT) {
            renderer.renderPlayerEntityTag(e.getRenderer(), ep, nprofile, e.getX(), e.getY(), e.getZ());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlay(RenderGameOverlayEvent e) {
        if (e.getType() != RenderGameOverlayEvent.ElementType.PLAYER_LIST) {
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
    public void onClientChatReceived(ClientChatReceivedEvent e) {
        if (e.getType() != ChatType.CHAT || e.isCanceled()) {
            return;
        }
        if (!modSettings.isEnabled() || !modSettings.isChangeInChat()) {
            return;
        }
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode == null) {
            return;
        }
        ITextComponent itcompmsg = e.getMessage();
        Pair<String, Integer> sdata = ChatHelper.getPossibleChatMsgSender(itcompmsg);
        String sender = sdata.getKey();
        if (sender == null) { // no player sender or not found.
            return;
        }
        NameDataProfile nprofile = nhandler.getCustomTag(sender);
        if (nprofile == null) {
            return;
        }
        if (mode == NameTagMode.HIDE) {
            ITextComponent hicomp = new TextComponentString(ChatHelper.translateAlternateColorCodes('&', "&7[H] "));
            itcompmsg.getSiblings().add(0, hicomp);
        } else {
            String fmessage = itcompmsg.getFormattedText();
            String src = fmessage.substring(0, sdata.getValue());
            fmessage = nprofile.getName() + fmessage.substring(sdata.getValue() + sender.length());
            String cprefix = nprofile.getPrefix();
            final boolean tags = cprefix != null || modSettings.isAutoRemoveTeamTags();
            if (!tags) {
                fmessage = src + fmessage;
            } else {
                src = src.replaceAll(ChatHelper.COLOR_CHAR + "r", "");
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
                        src = src.replace(prefix, ChatHelper.COLOR_CHAR + "r");
                    }
                    fmessage = src + fmessage;
                }
            }
            e.setCanceled(true);
            ITetxComponentBuilder builder = new ITetxComponentBuilder();
            for (ITextComponent itcomp : builder.buildITextComponents(fmessage)) {
                Minecraft.getMinecraft().player.sendStatusMessage(itcomp, false);
            }
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent e) {
        EntityPlayer ep = Minecraft.getMinecraft().player;
        if (ep == null) {
            return;
        }
        if (!e.getEntity().getUniqueID().equals(ep.getUniqueID())) {
            return;
        }
        World eworld = e.getWorld();
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