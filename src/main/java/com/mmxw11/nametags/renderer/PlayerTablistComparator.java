package com.mmxw11.nametags.renderer;

import java.util.Comparator;

import com.google.common.collect.ComparisonChain;

import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerTablistComparator implements Comparator<NetworkPlayerInfo> {

    @Override
    public int compare(NetworkPlayerInfo info1, NetworkPlayerInfo info2) {
        ScorePlayerTeam scoreplayerteam = info1.getPlayerTeam();
        ScorePlayerTeam scoreplayerteam1 = info2.getPlayerTeam();
        return ComparisonChain.start().compareTrueFirst(info1.getGameType() != WorldSettings.GameType.SPECTATOR,
                info2.getGameType() != WorldSettings.GameType.SPECTATOR).compare(scoreplayerteam != null
                        ? scoreplayerteam.getRegisteredName() : "", scoreplayerteam1 != null ? scoreplayerteam1.getRegisteredName() : "")
                .compare(info1.getGameProfile().getName(), info2.getGameProfile().getName()).result();
    }
}