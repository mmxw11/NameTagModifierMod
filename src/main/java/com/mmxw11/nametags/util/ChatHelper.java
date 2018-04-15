package com.mmxw11.nametags.util;

import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.technical.NameDataProfile;
import com.mmxw11.nametags.technical.NameTagHandler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ChatHelper {

    public static final char COLOR_CHAR = '\u00A7';
    public static final Map<Character, EnumChatFormatting> colorFormatMap;
    public static final Pattern CHAT_INC_PATTERN;
    private static final Pattern STRIP_COLOR_PATTERN;
    static {
        Builder<Character, EnumChatFormatting> builder = ImmutableMap.builder();
        for (EnumChatFormatting color : EnumChatFormatting.values()) {
            builder.put(Character.toLowerCase(color.toString().charAt(1)), color);
        }
        colorFormatMap = builder.build();
        CHAT_INC_PATTERN = Pattern.compile("(" + COLOR_CHAR + "[0-9a-fk-or])|(\\n)|((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]"
                + "{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + COLOR_CHAR + " \\n]|$))))", Pattern.CASE_INSENSITIVE);
        STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");
    }

    public static Pair<String, Integer> getPossibleChatMsgSender(IChatComponent iccomp) {
        final String ftext = iccomp.getFormattedText();
        final String ltext = ftext.toLowerCase();
        String sender = null;
        int startIndex = -1;
        NameTagHandler nhandler = NameTagModClient.getInstance().getNHandler();
        for (NameDataProfile nprofile : nhandler.getAllCustomTags()) {
            String name = nprofile.getRealName().toLowerCase();
            int index = ltext.indexOf(name);
            if (index != -1) {
                if ((startIndex == -1 || index < startIndex)) {
                    startIndex = index;
                    sender = ftext.substring(startIndex, startIndex + name.length());
                }
            }
        }
        Pair<String, Integer> pair = new Pair<>(sender, startIndex);
        return pair;
    }

    public static String getPossibleChatMsgSenderPrefix(String sender, String src) {
        NetHandlerPlayClient nhpclient = Minecraft.getMinecraft().getNetHandler();
        NetworkPlayerInfo info = nhpclient.getPlayerInfo(sender);
        String prefix = null;
        src = src.replaceAll(COLOR_CHAR + "r", "").trim();
        int findex = src.indexOf(" ");
        int lindex = src.lastIndexOf(" ");
        if (findex != -1 && lindex != -1 && findex != lindex) {
            src = src.substring(findex).trim();
        }
        if (info != null) {
            ScorePlayerTeam team = info.getPlayerTeam();
            if (team != null) {
                String cprefix = team.getColorPrefix();
                cprefix = cprefix.replaceAll(COLOR_CHAR + "r", "").trim();
                if (!cprefix.isEmpty()) {
                    if (src.contains(cprefix)) {
                        prefix = cprefix;
                    }
                }
            }
        } else {
            for (NetworkPlayerInfo i : nhpclient.getPlayerInfoMap()) {
                if (i.getPlayerTeam() == null) {
                    continue;
                }
                String cprefix = i.getPlayerTeam().getColorPrefix();
                cprefix = cprefix.replaceAll(COLOR_CHAR + "r", "").trim();
                if (cprefix.isEmpty()) {
                    continue;
                }
                if (src.contains(cprefix)) {
                    prefix = cprefix;
                    break;
                }
            }
        }
        return prefix;
    }

    public static void sendMessageToPlayer(String msg) {
        EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
        if (ep == null) {
            return;
        }
        msg = translateAlternateColorCodes('&', NameTagModClient.PREFIX + msg);
        ChatComponentText ccomponent = new ChatComponentText(msg);
        ep.addChatComponentMessage(ccomponent);
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String stripColor(String input, boolean translateColorCodes) {
        if (input == null) {
            return null;
        }
        if (translateColorCodes) {
            input = translateAlternateColorCodes('&', input);
        }
        return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }
}