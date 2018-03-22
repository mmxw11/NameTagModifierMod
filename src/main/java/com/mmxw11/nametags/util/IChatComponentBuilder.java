package com.mmxw11.nametags.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class IChatComponentBuilder {

    private List<IChatComponent> components;
    private IChatComponent ccComponent;
    private ChatStyle cstyle;

    public IChatComponentBuilder() {
        this.components = new ArrayList<>();
        this.ccComponent = new ChatComponentText("");
        this.cstyle = new ChatStyle();
    }

    public List<IChatComponent> buildIChatComponents(String msg) {
        return buildIChatComponents(msg, false);
    }

    private List<IChatComponent> buildIChatComponents(String msg, boolean keepNewlines) {
        components.add(ccComponent);
        if (msg == null) {
            return components;
        }
        Matcher matcher = ChatHelper.CHAT_INC_PATTERN.matcher(msg);
        String match = null;
        int currentIndex = 0;
        while (matcher.find()) {
            int groupId = 0;
            while ((match = matcher.group(++groupId)) == null);
            appendNewComponent(msg, currentIndex, matcher.start(groupId));
            switch (groupId) {
                case 1:
                    EnumChatFormatting format = ChatHelper.colorFormatMap.get(match.toLowerCase().charAt(1));
                    if (format == EnumChatFormatting.RESET) {
                        this.cstyle = new ChatStyle();
                    } else if (format == EnumChatFormatting.OBFUSCATED || format == EnumChatFormatting.BOLD || format == EnumChatFormatting.STRIKETHROUGH
                            || format == EnumChatFormatting.UNDERLINE || format == EnumChatFormatting.ITALIC) {
                        switch (format) {
                            case BOLD:
                                cstyle.setBold(true);
                                break;
                            case ITALIC:
                                cstyle.setItalic(true);
                                break;
                            case STRIKETHROUGH:
                                cstyle.setStrikethrough(true);
                                break;
                            case UNDERLINE:
                                cstyle.setUnderlined(true);
                                break;
                            case OBFUSCATED:
                                cstyle.setObfuscated(true);
                                break;
                            default:
                                throw new AssertionError("Unexpected message format");
                        }
                    } else {
                        // Color resets formatting
                        this.cstyle = new ChatStyle();
                        cstyle.setColor(format);
                    }
                    break;
                case 2:
                    if (keepNewlines) {
                        this.ccComponent.appendSibling(new ChatComponentText("\n"));
                    } else {
                        this.ccComponent = null;
                    }
                    break;
                case 3:
                    if (!(match.startsWith("http://") || match.startsWith("https://"))) {
                        match = "http://" + match;
                    }
                    cstyle.setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, match));
                    appendNewComponent(msg, currentIndex, matcher.end(groupId));
                    cstyle.setChatClickEvent(null);
            }
            currentIndex = matcher.end(groupId);
        }
        if (currentIndex < msg.length()) {
            appendNewComponent(msg, currentIndex, msg.length());
        }
        return components;
    }

    private void appendNewComponent(String msg, int currentIndex, int index) {
        if (index <= currentIndex) {
            return;
        }
        IChatComponent addition = new ChatComponentText(msg.substring(currentIndex, index)).setChatStyle(cstyle);
        currentIndex = index;
        this.cstyle = cstyle.createShallowCopy();
        if (ccComponent == null) {
            this.ccComponent = new ChatComponentText("");
            components.add(ccComponent);
        }
        this.ccComponent.appendSibling(addition);
    }
}