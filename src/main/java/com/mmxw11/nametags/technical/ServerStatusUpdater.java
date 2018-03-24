package com.mmxw11.nametags.technical;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Level;

import com.google.common.util.concurrent.ListenableFuture;
import com.mmxw11.nametags.NameTagMod;

import net.minecraft.client.Minecraft;

public class ServerStatusUpdater implements Runnable {

    private NameTagHandler nhandler;

    public ServerStatusUpdater(NameTagHandler nhandler) {
        this.nhandler = nhandler;
    }

    @Override
    public void run() {
        if (!nhandler.getModSettings().isRemovePlayerTagsOnLeave()) {
            nhandler.stopTask();
            return;
        }
        Minecraft minecraft = Minecraft.getMinecraft();
        ListenableFuture<List<String>> future = minecraft.addScheduledTask(() -> minecraft.theWorld.playerEntities
                .stream().map(e -> e.getName().toLowerCase()).collect(Collectors.toList()));
        try {
            List<String> names = future.get(5, TimeUnit.SECONDS);
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
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            NameTagMod.getInstance().getLogger().log(Level.FATAL, "Failed to update world players! Scheduled task took too long!", e);
        }
    }
}