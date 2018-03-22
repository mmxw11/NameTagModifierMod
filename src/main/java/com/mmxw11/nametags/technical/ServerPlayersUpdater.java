package com.mmxw11.nametags.technical;

import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ServerPlayersUpdater implements Runnable {

    private NameTagHandler nhandler;

    public ServerPlayersUpdater(NameTagHandler nhandler) {
        this.nhandler = nhandler;
    }

    @Override
    public void run() {
        if (!nhandler.getModSettings().isRemovePlayerTagsOnLeave()) {
            nhandler.stopPlayerUpdater();
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        World world = minecraft.theWorld;
        List<String> names = world.playerEntities.stream().map(e -> e.getName().toLowerCase()).collect(Collectors.toList());
        nhandler.getAllCustomTags().removeIf(t -> {
            if (!names.contains(t.getRealName().toLowerCase())) {
                if ((System.currentTimeMillis() - t.getLastSeenTime()) >= 40000) {
                    return true;
                }
            } else {
                t.resetLastSeenTime();
            }
            return false;
        });
    }
}