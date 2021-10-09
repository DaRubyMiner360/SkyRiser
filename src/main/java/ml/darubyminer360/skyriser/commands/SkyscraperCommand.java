/*
 * SkyRiser, a custom Minecraft skyscraper builder plugin.
 * Copyright (C) 2021 DaRubyMiner360
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ml.darubyminer360.skyriser.commands;

import ml.darubyminer360.skyriser.SkyRiser;
import ml.darubyminer360.skyriser.api.PreSkyscraperBuildEvent;
import ml.darubyminer360.skyriser.api.SkyRiserAPI;
import ml.darubyminer360.skyriser.files.Config;
import ml.darubyminer360.skyriser.files.Palette;
import ml.darubyminer360.skyriser.files.Style;
import ml.darubyminer360.skyriser.utils.CommandUtils;
import ml.darubyminer360.skyriser.utils.WorldEditUtils;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkyscraperCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            ((ConsoleCommandSender) sender).sendMessage(SkyRiser.prefix + ChatColor.RED + "You cannot use this command with the console!");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("usage")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Missing argument: action!" + ChatColor.RESET);
            return true;
        }
        else if (args[0].equalsIgnoreCase("build") && args.length == 1) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Missing argument: style!" + ChatColor.RESET);
            return true;
        }
        else if (args[0].equalsIgnoreCase("build") && args.length == 2) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Missing argument: palette!" + ChatColor.RESET);
            return true;
        }
        else if (args[0].equalsIgnoreCase("build") && args.length == 3) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Missing argument: palette sample skip amount!" + ChatColor.RESET);
            return true;
        }
        else if (args[0].equalsIgnoreCase("build") && args.length == 4) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Missing argument: palette blacklist!" + ChatColor.RESET);
            return true;
        }
        
        if (args[0].equalsIgnoreCase("build")) {
            PreSkyscraperBuildEvent buildEvent = new PreSkyscraperBuildEvent(sender);
            Bukkit.getPluginManager().callEvent(buildEvent);
            if (!buildEvent.isCancelled()) {
                SkyRiser.styleManager.loadStyles();
                SkyRiser.paletteManager.loadPalettes();

                StringBuilder varsString = new StringBuilder();
                for (int i = 0; i < args.length; i++) {
                    if (i > 4) {
                        varsString.append(args[i]).append(" ");
                    };
                }
                List<String> vars = new ArrayList<>();
                Pattern varsRegex = Pattern.compile("\\\"([^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*)\\\"|'([^'\\\\]*(?:\\\\.[^'\\\\]*)*)'|[^\\s]+");
                Matcher varsRegexMatcher = varsRegex.matcher(varsString);
                while (varsRegexMatcher.find()) {
                    if (varsRegexMatcher.group(1) != null) {
                        // Add double-quoted string without the quotes
                        vars.add(varsRegexMatcher.group(1));
                    } else if (varsRegexMatcher.group(2) != null) {
                        // Add single-quoted string without the quotes
                        vars.add(varsRegexMatcher.group(2));
                    } else {
                        // Add unquoted word
                        vars.add(varsRegexMatcher.group());
                    }
                }

                Style style = null;
                for (Map.Entry<String, Style> s : SkyRiser.styleManager.getStyles().entrySet()) {
                    if (s.getKey().equalsIgnoreCase(args[1]) || s.getKey().equalsIgnoreCase(args[1] + ".style") || s.getKey().equalsIgnoreCase(args[1] + ".json") || s.getKey().equalsIgnoreCase(args[1] + ".json5")) {
                        style = s.getValue();
                    }
                }

                // Give error if the given style doesn't exist
                if (style == null) {
                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Given style doesn't exist!");
                    return true;
                }

                if (vars.size() < style.options.size()) {
                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Not enough arguments!");
                    return true;
                }

                Palette palette = null;
                for (Map.Entry<String, Palette> p : SkyRiser.paletteManager.getPalettes().entrySet()) {
                    if (p.getKey().equalsIgnoreCase(args[2]) || p.getKey().equalsIgnoreCase(args[2] + ".palette") || p.getKey().equalsIgnoreCase(args[2] + ".csv")) {
                        palette = p.getValue();
                    }
                }

                // Give error if the given palette doesn't exist
                if (palette == null) {
                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Given palette doesn't exist!");
                    return true;
                }

                int paletteSampleSkipAmount = Integer.parseInt(args[3]);

                // Remove the first {paletteSampleSkipAmount} blocks from the palette
                Palette unchangedPalette = palette;
                for (int i = 0; i < unchangedPalette.materials.size(); i++) {
                    if (i < paletteSampleSkipAmount) {
                        palette.materials.remove(0);
                        palette.materialStrings.remove(0);
                    }
                }

                Palette blacklist = null;
                for (Map.Entry<String, Palette> p : SkyRiser.paletteManager.getPalettes().entrySet()) {
                    if ((p.getKey().equalsIgnoreCase(args[4]) || p.getKey().equalsIgnoreCase(args[4] + ".palette") || p.getKey().equalsIgnoreCase(args[4] + ".csv")) && p.getValue() != unchangedPalette) {
                        blacklist = p.getValue();
                    }
                }

                // Remove overlaps with the blacklist from the palette
                if (blacklist != null) {
                    palette.materials.removeAll(blacklist.materials);
                    palette.materialStrings.removeAll(blacklist.materialStrings);
                }
                else {
                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Missing argument: palette blacklist!" + ChatColor.RESET);
                    return true;
                }

                // Check if there are enough blocks left in the palette
                int highestIndex = 0;
                for (Style.Action a : style.actions) {
                    List<String> s = Arrays.asList(a.action.replaceAll("^.*?\\{", "").split("\\}.*?(\\{|$)"));
                    for (String str : s) {
                        if (str.contains("palette[")) {
                            List<String> strings = Arrays.asList(a.action.replaceAll("^.*?palette\\[", "").split("\\].*?(palette\\[|$)"));
                            for (String str1 : strings) {
                                int i = Integer.parseInt(str1);
                                if (i > highestIndex) {
                                    highestIndex = i;
                                }
                            }
                        }
                    }
                }
                if (palette.materials.size() < highestIndex + 1) {
                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Not enough blocks left in palette!");
                    return true;
                }

                if (sender instanceof Player && SkyRiser.useWorldEdit) {
                    WorldEditUtils.editSession = com.sk89q.worldedit.WorldEdit.getInstance().getEditSessionFactory().getEditSession(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(CommandUtils.getWorld(sender)), -1);
                }
                for (Style.Action action : style.actions) {
                    List<String> s = Arrays.asList(action.action.replaceAll("^.*?\\{", "").split("}.*?(\\{|$)"));
                    for (String str : s) {
                        if (str.contains("palette[")) {
                            String replacement = "";
                            List<String> mats = Arrays.asList(str.replaceAll("^.*?palette\\[", "").split("].*?(palette\\[|$)"));
                            for (String mat : mats) {
                                replacement = palette.materialStrings.get(Integer.parseInt(mat));
                            }
                            action.action = action.action.replaceFirst(Pattern.quote("{" + str + "}"), Matcher.quoteReplacement(replacement));
                        }
                    }

                    List<String> strings = Arrays.asList(action.action.replaceAll("^.*?\\{", "").split("}.*?(\\{|$)"));
                    for (String str : strings) {
                        // Add the options as variables with mXparser
                        Expression expression = new Expression(str);

                        if (sender instanceof Player) {
                            expression.addArguments(new Argument("x_pos = " + ((Player) sender).getLocation().getBlockX()));
                            expression.addArguments(new Argument("y_pos = " + ((Player) sender).getLocation().getBlockY()));
                            expression.addArguments(new Argument("z_pos = " + ((Player) sender).getLocation().getBlockZ()));
                            expression.addArguments(new Argument("exact_x_pos = " + ((Player) sender).getLocation().getX()));
                            expression.addArguments(new Argument("exact_y_pos = " + ((Player) sender).getLocation().getY()));
                            expression.addArguments(new Argument("exact_z_pos = " + ((Player) sender).getLocation().getZ()));

                            expression.addArguments(new Argument("eye_x_pos = " + ((Player) sender).getEyeLocation().getBlockX()));
                            expression.addArguments(new Argument("eye_y_pos = " + ((Player) sender).getEyeLocation().getBlockY()));
                            expression.addArguments(new Argument("eye_z_pos = " + ((Player) sender).getEyeLocation().getBlockZ()));
                            expression.addArguments(new Argument("exact_eye_x_pos = " + ((Player) sender).getEyeLocation().getX()));
                            expression.addArguments(new Argument("exact_eye_y_pos = " + ((Player) sender).getEyeLocation().getY()));
                            expression.addArguments(new Argument("exact_eye_z_pos = " + ((Player) sender).getEyeLocation().getZ()));
                        }
                        else if (sender instanceof BlockCommandSender) {
                            expression.addArguments(new Argument("x_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getBlockX()));
                            expression.addArguments(new Argument("y_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getBlockY()));
                            expression.addArguments(new Argument("z_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getBlockZ()));
                            expression.addArguments(new Argument("exact_x_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getX()));
                            expression.addArguments(new Argument("exact_y_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getY()));
                            expression.addArguments(new Argument("exact_z_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getZ()));

                            expression.addArguments(new Argument("eye_x_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getBlockX()));
                            expression.addArguments(new Argument("eye_y_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getBlockY()));
                            expression.addArguments(new Argument("eye_z_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getBlockZ()));
                            expression.addArguments(new Argument("exact_eye_x_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getX()));
                            expression.addArguments(new Argument("exact_eye_y_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getY()));
                            expression.addArguments(new Argument("exact_eye_z_pos = " + ((BlockCommandSender) sender).getBlock().getLocation().getZ()));
                        }
                        expression.addArguments(new Argument("world_border_size = " + CommandUtils.getWorld(sender).getWorldBorder().getSize()));

                        for (int i = 0; i < style.options.size(); i++) {
                            if (vars.get(i).matches("^[+-]?\\d+$"))
                                expression.addArguments(new Argument(style.options.get(i).name + " = " + vars.get(i)));
                        }
                        double result = expression.calculate();
                        if (!Double.isNaN(result)) {
                            String replacement = String.valueOf(Math.toIntExact(Math.round(result)));
                            action.action = action.action.replaceFirst(Pattern.quote("{" + str + "}"), Matcher.quoteReplacement(replacement));
                        }
                        else {
                            String replacement = str;
                            for (int i = 0; i < style.options.size(); i++) {
                                replacement = replacement.replace(style.options.get(i).name, vars.get(i));
                            }
                            action.action = action.action.replaceFirst(Pattern.quote("{" + str + "}"), Matcher.quoteReplacement(replacement));
                        }
                    }

                    List<String> splitStr = new ArrayList<>();
                    Pattern regex = Pattern.compile("\\\"([^\\\"\\\\]*(?:\\\\.[^\\\"\\\\]*)*)\\\"|'([^'\\\\]*(?:\\\\.[^'\\\\]*)*)'|[^\\s]+");
                    Matcher regexMatcher = regex.matcher(action.action);
                    while (regexMatcher.find()) {
                        if (regexMatcher.group(1) != null) {
                            // Add double-quoted string without the quotes
                            splitStr.add(regexMatcher.group(1));
                        } else if (regexMatcher.group(2) != null) {
                            // Add single-quoted string without the quotes
                            splitStr.add(regexMatcher.group(2));
                        } else {
                            // Add unquoted word
                            splitStr.add(regexMatcher.group());
                        }
                    }

                    // Do the actions in the style
                    if (style.format.equalsIgnoreCase("old") && !Config.get().getBoolean("AllowOldStyleFormat")) {
                        sender.sendMessage(SkyRiser.prefix + ChatColor.YELLOW + "Warning: Style uses old format but it isn't allowed in the config! Attempting to use new format. This probably won't work!");
                    }
                    if (!style.format.equalsIgnoreCase("old") && !style.format.equalsIgnoreCase("new")) {
                        sender.sendMessage(SkyRiser.prefix + ChatColor.YELLOW + "Warning: Invalid format! Defaulting to new. This may not work!");
                    }
                    boolean done = false;
                    for (BiFunction<CommandSender, List<Object>, Boolean> func : SkyRiserAPI.customActions) {
                        if (func.apply(sender, Arrays.asList(style, action, palette, splitStr))) {
                            done = true;
                        }
                    }
                    if (style.format.equalsIgnoreCase("old") && Config.get().getBoolean("AllowOldStyleFormat") && !done) {
                        if (action.action_type.equalsIgnoreCase("command")) {
                            if (sender instanceof Player)
                                ((Player) sender).performCommand(action.action);
                        }
                        else if (action.action_type.equalsIgnoreCase("block")) {
                            Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            if (sender instanceof Player) {
                                if (SkyRiser.useWorldEdit) {
                                    try {
                                        WorldEditUtils.editSession.setBlock(com.sk89q.worldedit.bukkit.BukkitAdapter.asBlockVector(loc), com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(Material.matchMaterial(splitStr.get(3)).createBlockData()));
                                    } catch (Exception ignored) {}
                                }
                                else
                                    loc.getBlock().setType(Material.matchMaterial(splitStr.get(3)));
                            }
                            else
                                loc.getBlock().setType(Material.matchMaterial(splitStr.get(3)));
                        }
                    }
                    else if (!done) {
                        if (action.action_type.equalsIgnoreCase("cube") || action.action_type.equalsIgnoreCase("fill")) {
                            Location startLoc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            Location endLoc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(3)), Integer.parseInt(splitStr.get(4)), Integer.parseInt(splitStr.get(5)));
                            for (int x = startLoc.getBlockX(); x <= endLoc.getBlockX(); x++) {
                                for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                                    for (int z = startLoc.getBlockZ(); z <= endLoc.getBlockZ(); z++) {
                                        Location loc = new Location(CommandUtils.getWorld(sender), x, y, z);
                                        if (sender instanceof Player) {
                                            if (SkyRiser.useWorldEdit) {
                                                try {
                                                    WorldEditUtils.editSession.setBlock(com.sk89q.worldedit.bukkit.BukkitAdapter.asBlockVector(loc), com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(Material.matchMaterial(splitStr.get(6)).createBlockData()));
                                                } catch (Exception ignored) {}
                                            }
                                            else
                                                loc.getBlock().setType(Material.matchMaterial(splitStr.get(6)));
                                        }
                                        else {
                                            loc.getBlock().setType(Material.matchMaterial(splitStr.get(6)));
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.action_type.equalsIgnoreCase("hollowcube")) {
                            Location startLoc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            Location endLoc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(3)), Integer.parseInt(splitStr.get(4)), Integer.parseInt(splitStr.get(5)));
                            for (int x = startLoc.getBlockX(); x <= endLoc.getBlockX(); x++) {
                                for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                                    for (int z = startLoc.getBlockZ(); z <= endLoc.getBlockZ(); z++) {
                                        if (x == startLoc.getBlockX() || x == endLoc.getBlockX() || y == startLoc.getBlockY() || y == endLoc.getBlockY() || z == startLoc.getBlockZ() || z == endLoc.getBlockZ()) {
                                            Location loc = new Location(CommandUtils.getWorld(sender), x, y, z);
                                            if (sender instanceof Player) {
                                                if (SkyRiser.useWorldEdit) {
                                                    try {
                                                        WorldEditUtils.editSession.setBlock(com.sk89q.worldedit.bukkit.BukkitAdapter.asBlockVector(loc), com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(Material.matchMaterial(splitStr.get(6)).createBlockData()));
                                                    } catch (Exception ignored) {}
                                                }
                                                else
                                                    loc.getBlock().setType(Material.matchMaterial(splitStr.get(6)));
                                            }
                                            else
                                                loc.getBlock().setType(Material.matchMaterial(splitStr.get(6)));
                                        }
                                    }
                                }
                            }
                        }
                        else if (action.action_type.equalsIgnoreCase("block")) {
                            Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            if (sender instanceof Player) {
                                if (SkyRiser.useWorldEdit) {
                                    try {
                                        WorldEditUtils.editSession.setBlock(com.sk89q.worldedit.bukkit.BukkitAdapter.asBlockVector(loc), com.sk89q.worldedit.bukkit.BukkitAdapter.adapt(Material.matchMaterial(splitStr.get(3)).createBlockData()));
                                    } catch (Exception ignored) {}
                                }
                                else
                                    loc.getBlock().setType(Material.matchMaterial(splitStr.get(3)));
                            }
                            else
                                loc.getBlock().setType(Material.matchMaterial(splitStr.get(3)));
                        }
                        else if (action.action_type.equalsIgnoreCase("summon") || action.action_type.equalsIgnoreCase("summonmob") || action.action_type.equalsIgnoreCase("summonentity") || action.action_type.equalsIgnoreCase("spawnmob") || action.action_type.equalsIgnoreCase("spawnentity")) {
                            World world = CommandUtils.getWorld(sender);
                            Location loc = new Location(world, Integer.parseInt(splitStr.get(2)), Integer.parseInt(splitStr.get(3)), Integer.parseInt(splitStr.get(4)));
                            for (int i = 0; i < Integer.parseInt(splitStr.get(1)); i++) {
                                world.spawnEntity(loc, EntityType.fromName(splitStr.get(0)));
                            }
                        }
                        else if (action.action_type.equalsIgnoreCase("worldborder")) {
                            if (splitStr.size() == 0) {
                                sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Missing arguments for the world border");
                                return true;
                            }
                            if (splitStr.get(0).equalsIgnoreCase("center")) {
                                if (splitStr.size() < 3) {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Missing world border x and/or z values!");
                                    return true;
                                }
                                if (sender instanceof Player) {
                                    for (World world : Bukkit.getWorlds()) {
                                        world.getWorldBorder().setCenter(CommandUtils.getLocation(((Player) sender).getLocation(), splitStr.get(1), "0", splitStr.get(2)));
                                    }
                                    return true;
                                }
                                else {
                                    double x;
                                    double z;
                                    try {
                                        x = Double.parseDouble(splitStr.get(1));
                                        z = Double.parseDouble(splitStr.get(2));
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border center because of an invalid number format.");
                                        return true;
                                    }
                                    for (World world : Bukkit.getWorlds()) {
                                        world.getWorldBorder().setCenter(x, z);
                                    }
                                    return true;
                                }
                            }
                            else if (splitStr.get(0).equalsIgnoreCase("set")) {
                                if (splitStr.size() < 2) {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Missing world border size!");
                                    return true;
                                }
                                double size;
                                try {
                                    size = Double.parseDouble(splitStr.get(1));
                                } catch (NumberFormatException ex) {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border size because of an invalid number format.");
                                    return true;
                                }
                                int time = 0;
                                if (splitStr.size() > 2) {
                                    try {
                                        time = Integer.parseInt(splitStr.get(2));
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border size because of an invalid number format.");
                                        return true;
                                    }
                                }
                                if (time < 0) {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border size because the time must be positive.");
                                    return true;
                                }
                                for (World world : Bukkit.getWorlds()) {
                                    world.getWorldBorder().setSize(size, time);
                                }
                            }
                            else if (splitStr.get(0).equalsIgnoreCase("add")) {
                                if (splitStr.size() < 2) {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot add to world border size because of missing arguments.");
                                    return true;
                                }
                                double size;
                                try {
                                    size = Double.parseDouble(splitStr.get(1));
                                } catch (NumberFormatException ex) {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border size because of an invalid number format.");
                                    return true;
                                }
                                int time = 0;
                                if (splitStr.size() > 2) {
                                    try {
                                        time = Integer.parseInt(splitStr.get(2));
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border size because of an invalid number format.");
                                        return true;
                                    }
                                }
                                if (time < 0) {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border size because the time must be positive.");
                                    return true;
                                }
                                for (World world : Bukkit.getWorlds()) {
                                    world.getWorldBorder().setSize(size + world.getWorldBorder().getSize(), time);
                                }
                            } else if (splitStr.get(0).equalsIgnoreCase("damage")) {
                                if (splitStr.size() < 2) {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border damage because of missing arguments.");
                                    return true;
                                }
                                if (splitStr.get(1).equalsIgnoreCase("buffer")) {
                                    if (splitStr.size() < 3) {
                                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border damage because of missing arguments.");
                                        return true;
                                    }
                                    double buffer;
                                    try {
                                        buffer = Double.parseDouble(splitStr.get(2));
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(SkyRiser.prefix + 
                                                ChatColor.RED + "Cannot set damage buffer: invalid number format.");
                                        return true;
                                    }
                                    if (buffer < 0) {
                                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED
                                                + "Cannot set damage buffer: damage buffer must be positive.");
                                        return true;
                                    }
                                    for (World world : Bukkit.getWorlds()) {
                                        world.getWorldBorder().setDamageBuffer(buffer);
                                    }
                                    sender.sendMessage(SkyRiser.prefix + "Set border's damage buffer to " + buffer + " blocks.");
                                    return true;
                                } else if (splitStr.get(1).equalsIgnoreCase("amount")) {
                                    if (splitStr.size() < 3) {
                                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border damage because of missing arguments.");
                                        return true;
                                    }
                                    double damage;
                                    try {
                                        damage = Double.parseDouble(splitStr.get(2));
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(SkyRiser.prefix + 
                                                ChatColor.RED + "Cannot set damage amount: invalid number format.");
                                        return true;
                                    }
                                    if (damage < 0) {
                                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED
                                                + "Cannot set damage amount: damage amount must be positive.");
                                        return true;
                                    }
                                    for (World world : Bukkit.getWorlds()) {
                                        world.getWorldBorder().setDamageAmount(damage);
                                    }
                                    return true;
                                } else {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border damage because of invalid arguments.");
                                    return true;
                                }
                            } else if (splitStr.get(0).equalsIgnoreCase("warning")) {
                                if (splitStr.size() < 2) {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border warning info because of missing arguments.");
                                    return true;
                                }
                                if (splitStr.get(1).equalsIgnoreCase("time")) {
                                    if (splitStr.size() < 3) {
                                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border warning time because of missing arguments.");
                                        return true;
                                    }
                                    int time;
                                    try {
                                        time = Integer.parseInt(splitStr.get(2));
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(SkyRiser.prefix + 
                                                ChatColor.RED + "Cannot set warning time: invalid number format.");
                                        return true;
                                    }
                                    if (time < 0) {
                                        sender.sendMessage(SkyRiser.prefix + 
                                                ChatColor.RED + "Cannot set warning time: time must be positive.");
                                        return true;
                                    }
                                    for (World world : Bukkit.getWorlds()) {
                                        world.getWorldBorder().setWarningTime(time);
                                    }
                                    return true;
                                } else if (splitStr.get(1).equalsIgnoreCase("distance")) {
                                    if (splitStr.size() < 3) {
                                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border warning distance because of missing arguments.");
                                        return true;
                                    }
                                    int blocks;
                                    try {
                                        blocks = Integer.parseInt(splitStr.get(2));
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(SkyRiser.prefix + 
                                                ChatColor.RED + "Cannot set warning distance: invalid number format.");
                                        return true;
                                    }
                                    if (blocks < 0) {
                                        sender.sendMessage(SkyRiser.prefix + 
                                                ChatColor.RED + "Cannot set warning distance: distance must be positive.");
                                        return true;
                                    }
                                    for (World world : Bukkit.getWorlds()) {
                                        world.getWorldBorder().setWarningDistance(blocks);
                                    }
                                    return true;
                                } else {
                                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot set world border warning info because of invalid arguments.");
                                    return true;
                                }
                            } else {
                                sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Style Error: Cannot modify world border because of missing or invalid arguments.");
                                return true;
                            }
                        }
                        else if (action.action_type.equalsIgnoreCase("holo") || action.action_type.equalsIgnoreCase("hologram")) {
                            Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)), Integer.parseInt(splitStr.get(3)));

                            if (SkyRiser.useHolographicDisplays) {
                                com.gmail.filoghost.holographicdisplays.object.NamedHologram hologram = new com.gmail.filoghost.holographicdisplays.object.NamedHologram(loc, splitStr.get(0));
                                // TODO: Set the name

                                String linesStr = splitStr.get(4).substring(1, splitStr.get(4).length() - 1);
                                List<String> lines = new ArrayList<String>(Arrays.asList(linesStr.replace(", ", ",").split(",")));

                                for (String line : lines) {
                                    hologram.appendTextLine(line.substring(1, line.length() - 1));
                                }
                                com.gmail.filoghost.holographicdisplays.object.NamedHologramManager.addHologram(hologram);
                                hologram.refreshAll();

                                com.gmail.filoghost.holographicdisplays.disk.HologramDatabase.saveHologram(hologram);
                                com.gmail.filoghost.holographicdisplays.disk.HologramDatabase.trySaveToDisk();
                            }
                            else {
                                sender.sendMessage(SkyRiser.prefix + ChatColor.YELLOW + "Warning: This style requires a hologram plugin to fully work! Install Holographic Displays!" + ChatColor.RESET);
                            }
                        }
                    }
                }
                if (sender instanceof Player && SkyRiser.useWorldEdit) {
                    com.sk89q.worldedit.WorldEdit.getInstance().getSessionManager().get(com.sk89q.worldedit.bukkit.BukkitAdapter.adapt((Player) sender)).remember(WorldEditUtils.editSession);
                    WorldEditUtils.editSession.close();
                    WorldEditUtils.Reset();
                }
            }
        }

        return true;
    }
}
