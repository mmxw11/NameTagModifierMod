package com.mmxw11.nametags.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

public class ITetxComponentBuilder {

    private List<ITextComponent> components;
    private ITextComponent itcomp;
    private Style cstyle;

    public ITetxComponentBuilder() {
        this.components = new ArrayList<>();
        this.itcomp = new TextComponentString("");
        this.cstyle = new Style();
    }

    public List<ITextComponent> buildITextComponents(String msg) {
        return buildITextComponents(msg, false);
    }

    private List<ITextComponent> buildITextComponents(String msg, boolean keepNewlines) {
        components.add(itcomp);
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
                    TextFormatting format = ChatHelper.colorFormatMap.get(match.toLowerCase().charAt(1));
                    if (format == TextFormatting.RESET) {
                        this.cstyle = new Style();
                    } else if (format == TextFormatting.OBFUSCATED || format == TextFormatting.BOLD || format == TextFormatting.STRIKETHROUGH
                            || format == TextFormatting.UNDERLINE || format == TextFormatting.ITALIC) {
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
                        this.cstyle = new Style();
                        cstyle.setColor(format);
                    }
                    break;
                case 2:
                    if (keepNewlines) {
                        itcomp.appendSibling(new TextComponentString("\n"));
                    } else {
                        this.itcomp = null;
                    }
                    break;
                case 3:
                    if (!(match.startsWith("http://") || match.startsWith("https://"))) {
                        match = "http://" + match;
                    }
                    cstyle.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, match));
                    appendNewComponent(msg, currentIndex, matcher.end(groupId));
                    cstyle.setClickEvent(null);
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
        ITextComponent addition = new TextComponentString(msg.substring(currentIndex, index)).setStyle(cstyle);
        currentIndex = index;
        this.cstyle = cstyle.createShallowCopy();
        if (itcomp == null) {
            this.itcomp = new TextComponentString("");
            components.add(itcomp);
        }
        itcomp.appendSibling(addition);
    }
}