package com.mmxw11.nametags.technical;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.NameTagMode;
import com.mmxw11.nametags.settings.FileManager;
import com.mmxw11.nametags.settings.ModSettingsProfile;
import com.mmxw11.nametags.util.ChatHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;

public class NameTagHandler {

    private ModSettingsProfile modSettings;
    private List<String> randomNames;
    private ScheduledFuture<?> sfuture;
    private Random random;
    private Map<String, NameDataProfile> customTags;

    public NameTagHandler() {
        this.random = new Random();
        this.customTags = new ConcurrentHashMap<>();
    }

    public void loadSettings(ModSettingsProfile modSettings, FileManager fmanager) throws IOException {
        this.modSettings = modSettings;
        this.randomNames = fmanager.readRandomNames();
    }

    public void startTask() {
        if (sfuture != null) {
            return;
        }
        if (modSettings.isRemovePlayerTagsOnLeave()) {
            ServerStatusUpdater pupdater = new ServerStatusUpdater(this);
            ScheduledExecutorService exService = NameTagModClient.getInstance().getSExecutorService();
            this.sfuture = exService.scheduleAtFixedRate(pupdater, 5, 20, TimeUnit.SECONDS);
        }
    }

    public void stopTask() {
        if (sfuture == null) {
            return;
        }
        sfuture.cancel(true);
        this.sfuture = null;
    }

    public void setNameTagMode(NameTagMode mode) {
        modSettings.setNameTagMode(mode);
        if (mode == NameTagMode.EDIT) {
            for (Iterator<NameDataProfile> it = customTags.values().iterator(); it.hasNext();) {
                NameDataProfile value = it.next();
                if (value.getName() == null) {
                    it.remove();
                }
            }
        }
    }

    public boolean setCustomName(String target, String customName, boolean overrideExisting) {
        NameDataProfile nprofile = customTags.get(target.toLowerCase());
        if (!overrideExisting && nprofile != null) {
            return false;
        }
        if (nprofile != null) {
            nprofile.setName(customName);
        } else {
            nprofile = new NameDataProfile(target, customName);
            customTags.put(target.toLowerCase(), nprofile);
        }
        return true;
    }

    public int setCustomNameToAllPlayers(String customName) {
        NameTagMode mode = modSettings.getNameTagMode();
        NetHandlerPlayClient nhpclient = Minecraft.getMinecraft().getNetHandler();
        int counter = 0;
        for (NetworkPlayerInfo info : nhpclient.getPlayerInfoMap()) {
            String tname = info.getGameProfile().getName();
            String nname = null;
            if (mode == NameTagMode.EDIT) {
                if (customName != null && customName.equalsIgnoreCase("randomname")) {
                    nname = generateRandomName();
                } else {
                    nname = customName;
                }
            }
            if (setCustomName(tname, nname, false)) {
                counter++;
            }
        }
        return counter;
    }

    public int setCustomNamePrefix(String customName, String prefix, boolean overrideExisting) {
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode != NameTagMode.EDIT) {
            return -1;
        }
        int counter = 0;
        for (NameDataProfile nprofile : customTags.values()) {
            if (!nprofile.getName().equalsIgnoreCase(customName)) {
                continue;
            }
            if (!overrideExisting && nprofile.getPrefix() != null) {
                continue;
            }
            nprofile.setPrefix(prefix);
            counter++;
        }
        return counter;
    }

    public int setCustomNamePrefixToAllPlayers(String prefix) {
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode != NameTagMode.EDIT) {
            return -1;
        }
        int counter = 0;
        for (NameDataProfile nprofile : customTags.values()) {
            if (nprofile.getPrefix() != null) {
                continue;
            }
            nprofile.setPrefix(prefix);
            counter++;
        }
        return counter;
    }

    public int setCustomNameSuffix(String customName, String suffix, boolean overrideExisting) {
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode != NameTagMode.EDIT) {
            return -1;
        }
        int counter = 0;
        for (NameDataProfile nprofile : customTags.values()) {
            if (!nprofile.getName().equalsIgnoreCase(customName)) {
                continue;
            }
            if (!overrideExisting && nprofile.getSuffix() != null) {
                continue;
            }
            nprofile.setSuffix(suffix);
            counter++;
        }
        return counter;
    }

    public int setCustomNameSuffixToAllPlayers(String suffix) {
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode != NameTagMode.EDIT) {
            return -1;
        }
        int counter = 0;
        for (NameDataProfile nprofile : customTags.values()) {
            if (nprofile.getSuffix() != null) {
                continue;
            }
            nprofile.setSuffix(suffix);
            counter++;
        }
        return counter;
    }

    public int removeCustomNameTags(String name) {
        int counter = 0;
        for (Iterator<NameDataProfile> it = customTags.values().iterator(); it.hasNext();) {
            NameDataProfile nprofile = it.next();
            String cname = nprofile.getName();
            if (nprofile.getRealName().equalsIgnoreCase(name) || (cname != null && cname.equalsIgnoreCase(name))) {
                it.remove();
                counter++;
            }
        }
        return counter;
    }

    public void removeAllCustomNameTags(boolean onNewWorld) {
        if (customTags.isEmpty()) {
            if (!onNewWorld) {
                ChatHelper.sendMessageToPlayer("&cNo saved tags were found.");
            }
            return;
        }
        int size = customTags.size();
        if (!onNewWorld) {
            customTags.clear();
            ChatHelper.sendMessageToPlayer("&eRemoved a total of&7 " + size + " &ecustom tag" + (size == 1 ? "" : "s") + " from name(s).");
        } else {
            customTags.entrySet().removeIf(entry -> {
                String pname = Minecraft.getMinecraft().thePlayer.getName();
                if (entry.getKey().equalsIgnoreCase(pname)) {
                    return false;
                }
                return true;
            });
            int count = size - customTags.size();
            if (count > 0) {
                ChatHelper.sendMessageToPlayer("&eNew world detected: automatically removed a total of&7 "
                        + count + " &ecustom tag" + (count == 1 ? "" : "s") + " from name(s).");
            }
        }
    }

    public String generateRandomName() {
        int number = random.nextInt(randomNames.size());
        return randomNames.get(number);
    }

    public NameDataProfile getCustomTag(String target) {
        return customTags.get(target.toLowerCase());
    }

    public Collection<NameDataProfile> getAllCustomTags() {
        return customTags.values();
    }

    public int getTotalCustomTagsAmount() {
        return customTags.size();
    }

    public List<NameDataProfile> getCustomTagHolders(String customName) {
        NameTagMode mode = modSettings.getNameTagMode();
        if (mode != NameTagMode.EDIT) {
            return null;
        }
        List<NameDataProfile> list = customTags.values().stream().filter(k -> k.getName().equalsIgnoreCase(customName))
                .collect(Collectors.toList());
        return list;
    }
}