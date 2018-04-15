package com.mmxw11.nametags.commands;

import org.apache.commons.lang3.StringUtils;

import com.mmxw11.nametags.NameTagModClient;
import com.mmxw11.nametags.NameTagMode;
import com.mmxw11.nametags.technical.NameTagHandler;
import com.mmxw11.nametags.util.ChatHelper;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class NameTagEditCommand extends CommandBase {

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public String getCommandName() {
        return "ntag";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/ntag <set/setprefix/setsuffix/remove> <name/all> [name/randomname/prefix/suffix]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        NameTagModClient mod = NameTagModClient.getInstance();
        NameTagHandler nhandler = mod.getNHandler();
        NameTagMode mode = mod.getModSettings().getNameTagMode();
        if (mode == null) {
            ChatHelper.sendMessageToPlayer("&cYou can't use this command because you haven't set NameTagMode yet!");
            return;
        }
        if (args.length < 2) {
            ChatHelper.sendMessageToPlayer("&cUsage: " + getCommandUsage(sender));
            return;
        }
        String action = args[0];
        String target = args[1];
        if (action.equalsIgnoreCase("set")) {
            if (args.length < 3 && mode == NameTagMode.EDIT) {
                ChatHelper.sendMessageToPlayer("&cUsage: " + getCommandUsage(sender));
                return;
            }
            String cname = null;
            if (mode == NameTagMode.EDIT) {
                cname = args[2];
                if (cname.length() > 16) {
                    ChatHelper.sendMessageToPlayer("&cMinecraft only supports names up to 16 characters!");
                    return;
                }
            }
            if (target.equalsIgnoreCase("all")) {
                int count = nhandler.setCustomNameToAllPlayers(cname);
                if (mode == NameTagMode.EDIT) {
                    ChatHelper.sendMessageToPlayer("&eSet custom names for &7" + count + " &eplayers.");
                } else if (mode == NameTagMode.HIDE) {
                    ChatHelper.sendMessageToPlayer("&eHide a total of &7" + count + " &enames.");
                }
            } else {
                String nname = null;
                if (mode == NameTagMode.EDIT) {
                    if (cname.equalsIgnoreCase("randomname")) {
                        nname = nhandler.generateRandomName();
                    } else {
                        nname = cname;
                    }
                }
                nhandler.setCustomName(target, nname, true);
                ChatHelper.sendMessageToPlayer("&eSet the custom name of " + (nname == null ? "'HIDE'" : nname) + " for the name of " + target + ".");
            }
        } else if (action.equalsIgnoreCase("setprefix")) {
            if (mode != NameTagMode.EDIT) {
                ChatHelper.sendMessageToPlayer("&cYou must have NameTagMode set to EDIT to use this command!");
                return;
            }
            if (args.length < 3) {
                ChatHelper.sendMessageToPlayer("&cUsage: " + getCommandUsage(sender));
                return;
            }
            String prefix = args[2];
            int spaceCount = StringUtils.countMatches(prefix, "/s");
            if ((prefix.replaceAll("/s", "").length() + spaceCount) > 16) {
                ChatHelper.sendMessageToPlayer("&cMinecraft only supports prefixes up to 16 characters!");
                return;
            }
            prefix = prefix.replaceAll("/s", " ");
            if (target.equalsIgnoreCase("all")) {
                int count = nhandler.setCustomNamePrefixToAllPlayers(prefix);
                ChatHelper.sendMessageToPlayer("&eSet the prefix of " + ChatHelper.translateAlternateColorCodes('&', prefix.trim()) + " &efor &7" + count + " &eplayers.");
            } else {
                int count = nhandler.setCustomNamePrefix(target, prefix, true);
                if (count == 0) {
                    ChatHelper.sendMessageToPlayer("&cCan't find custom tag(s) by the name of " + target + "!");
                } else {
                    ChatHelper.sendMessageToPlayer("&eSet the prefix of " + ChatHelper.translateAlternateColorCodes('&', prefix.trim())
                            + " &efor the custom name of " + target + ".");
                }
            }
        } else if (action.equalsIgnoreCase("setsuffix")) {
            if (mode != NameTagMode.EDIT) {
                ChatHelper.sendMessageToPlayer("&cYou must have NameTagMode set to EDIT to use this command!");
                return;
            }
            if (args.length < 3) {
                ChatHelper.sendMessageToPlayer("&cUsage: " + getCommandUsage(sender));
                return;
            }
            String suffix = args[2];
            int spaceCount = StringUtils.countMatches(suffix, "/s");
            if ((suffix.replaceAll("/s", "").length() + spaceCount) > 16) {
                ChatHelper.sendMessageToPlayer("&cMinecraft only supports suffixes up to 16 characters!");
                return;
            }
            suffix = suffix.replaceAll("/s", " ");
            if (target.equalsIgnoreCase("all")) {
                int count = nhandler.setCustomNameSuffixToAllPlayers(suffix);
                ChatHelper.sendMessageToPlayer("&eSet the suffix of " + ChatHelper.translateAlternateColorCodes('&', suffix.trim()) + " &efor &7" + count + " &eplayers.");
            } else {
                int count = nhandler.setCustomNameSuffix(target, suffix, true);
                if (count == 0) {
                    ChatHelper.sendMessageToPlayer("&cCan't find custom tag(s) by the name of " + target + "!");
                } else {
                    ChatHelper.sendMessageToPlayer("&eSet the suffix of " + ChatHelper.translateAlternateColorCodes('&', suffix.trim())
                            + " &efor the name of " + target + ".");
                }
            }
        } else if (action.equalsIgnoreCase("remove")) {
            if (target.equalsIgnoreCase("all")) {
                nhandler.removeAllCustomNameTags(false);
            } else {
                int count = nhandler.removeCustomNameTags(target);
                if (count == 0) {
                    ChatHelper.sendMessageToPlayer("&Can't find target(s) by the name of " + target + "!");
                } else {
                    ChatHelper.sendMessageToPlayer("&eRemoved a total of&7 " + count + " &ecustom tag" + (count == 1 ? "" : "s") + " from name(s).");
                }
            }
        } else {
            ChatHelper.sendMessageToPlayer("&cUnknown action! Available actions: SET, SETPREFIX, SETSUFFIX, REMOVE");
        }
    }
}