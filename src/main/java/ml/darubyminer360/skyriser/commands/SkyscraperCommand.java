/*
 * SkyRiser, a custom Minecraft skyscraper builder plugin.
 * Copyright (C) 2021-2022 DaRubyMiner360
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

import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;
import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.NamedHologramManager;
import ml.darubyminer360.skyriser.SkyRiser;
import ml.darubyminer360.skyriser.api.PreSkyscraperBuildEvent;
import ml.darubyminer360.skyriser.api.SkyRiserAPI;
import ml.darubyminer360.skyriser.files.Palette;
import ml.darubyminer360.skyriser.files.Style;
import ml.darubyminer360.skyriser.utils.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;

import io.github.bananapuncher714.nbteditor.NBTEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SkyscraperCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player && !sender.hasPermission("skyriser.skyscraper")) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: You do not have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Missing argument: action!");
            return true;
        }

        if (args[0].equalsIgnoreCase("usage")) {
            return false;
        } else if (args[0].equalsIgnoreCase("stop")) {
            Player target;
            if (args.length > 1)
                target = Bukkit.getPlayer(args[1]);
            else if (sender instanceof Player)
                target = (Player) sender;
            else {
                sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: You can't do this from the console or a command block!");
                return true;
            }

            if (SkyRiser.instance.removePlayerBuilder(target.getName())) {
                String message = SkyRiser.prefix + ChatColor.GREEN + "Stopped building";
                if (target != sender)
                    message += " for " + target.getDisplayName();
                message += ".";
                sender.sendMessage(message);
            } else {
                String message = SkyRiser.prefix + ChatColor.RED + " ";
                if (target == sender)
                    message += "You aren't";
                else
                    message += target.getDisplayName() + " isn't";
                message += " currently building.";
                sender.sendMessage(message);
            }
            return true;
        } else if (args[0].equalsIgnoreCase("undo")) {
            int amount = 1;
            Player target;
            if (args.length > 1)
                amount = Integer.parseInt(args[1]);
            if (args.length > 2)
                target = Bukkit.getPlayer(args[2]);
            else if (sender instanceof Player)
                target = (Player) sender;
            else {
                sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: You can't do this from the console or a command block!");
                return true;
            }

            for (int i = 0; i < amount; i++) {
                if (!SkyRiser.instance.undo(sender.getName())) {
                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Can't undo.");
                    String message = SkyRiser.prefix + ChatColor.GREEN + "Undid " + i + " action";
                    if (i != 1)
                        message += "s";
                    if (target != sender)
                        message += " for " + target.getDisplayName();
                    message += ".";
                    sender.sendMessage(message);
                    return true;
                }
            }
            String message = SkyRiser.prefix + ChatColor.GREEN + "Undid " + amount + " action";
            if (amount != 1)
                message += "s";
            if (target != sender)
                message += " for " + target.getDisplayName();
            message += ".";
            sender.sendMessage(message);
            return true;
        } else if (args[0].equalsIgnoreCase("redo")) {
            int amount = 1;
            Player target;
            if (args.length > 1)
                amount = Integer.parseInt(args[1]);
            if (args.length > 2)
                target = Bukkit.getPlayer(args[2]);
            else if (sender instanceof Player)
                target = (Player) sender;
            else {
                sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: You can't do this from the console or a command block!");
                return true;
            }

            for (int i = 0; i < amount; i++) {
                if (!SkyRiser.instance.redo(sender.getName())) {
                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Can't redo.");
                    String message = SkyRiser.prefix + ChatColor.GREEN + "Redid " + i + " action";
                    if (i != 1)
                        message += "s";
                    if (target != sender)
                        message += " for " + target.getDisplayName();
                    message += ".";
                    sender.sendMessage(message);
                    return true;
                }
            }
            String message = SkyRiser.prefix + ChatColor.GREEN + "Redid " + amount + " action";
            if (amount != 1)
                message += "s";
            if (target != sender)
                message += " for " + target.getDisplayName();
            message += ".";
            sender.sendMessage(message);
            return true;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: You can't do this from the console!");
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Missing argument: style!");
            return true;
        } else if (args.length == 2) {
            sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Missing argument: palette!");
            return true;
        }
        
        if (args[0].equalsIgnoreCase("build")) {
            PreSkyscraperBuildEvent preBuildEvent = new PreSkyscraperBuildEvent(sender);
            Bukkit.getPluginManager().callEvent(preBuildEvent);
            if (!preBuildEvent.isCancelled()) {
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

                if (args.length > 3) {
                    int paletteSampleSkipAmount = Integer.parseInt(args[3]);

                    // Remove the first {paletteSampleSkipAmount} blocks from the palette
                    Palette unchangedPalette = palette;
                    for (int i = 0; i < unchangedPalette.blockDatas.size(); i++) {
                        if (i < paletteSampleSkipAmount) {
                            palette.blockDatas.remove(0);
                            palette.blockDataStrings.remove(0);
                        }
                    }


                    if (args.length > 4) {
                        Palette blacklist = null;
                        for (Map.Entry<String, Palette> p : SkyRiser.paletteManager.getPalettes().entrySet()) {
                            if ((p.getKey().equalsIgnoreCase(args[4]) || p.getKey().equalsIgnoreCase(args[4] + ".palette") || p.getKey().equalsIgnoreCase(args[4] + ".csv")) && p.getValue() != unchangedPalette) {
                                blacklist = p.getValue();
                            }
                        }

                        // Remove overlaps with the blacklist from the palette
                        if (blacklist != null) {
                            palette.blockDatas.removeAll(blacklist.blockDatas);
                            palette.blockDataStrings.removeAll(blacklist.blockDataStrings);
                        }
                    }
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
                if (palette.blockDatas.size() < highestIndex + 1) {
                    sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: Not enough blocks left in palette!");
                    return true;
                }

                BlockBuilder blockBuilder = new BlockBuilder(sender, style.rotate);
                BlockBuilder segmentBlockBuilder = new BlockBuilder(sender, style.rotate);
                EntityBuilder entityBuilder = new EntityBuilder(sender, style.rotate);
                EntityBuilder segmentEntityBuilder = new EntityBuilder(sender, style.rotate);
                for (Style.Action action : style.actions) {
                    if (SkyRiser.usePlaceholderAPI && sender instanceof Player) {
                        action.action = me.clip.placeholderapi.PlaceholderAPI.setPlaceholders((Player) sender, action.action);
                    }

                    List<String> s = Arrays.asList(action.action.replaceAll("^.*?\\{", "").split("}.*?(\\{|$)"));
                    for (String str : s) {
                        if (str.contains("palette[")) {
                            String replacement = "";
                            List<String> mats = Arrays.asList(str.replaceAll("^.*?palette\\[", "").split("].*?(palette\\[|$)"));
                            for (String mat : mats) {
                                replacement = palette.blockDataStrings.get(Integer.parseInt(mat));
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
                        } else if (sender instanceof BlockCommandSender) {
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
                        } else {
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
                    boolean done = false;
                    for (Function6<CommandSender, Style, Style.Action, Palette, List<String>, Boolean> func : SkyRiserAPI.getActions()) {
                        if (func.apply(sender, style, action, palette, splitStr)) {
                            done = true;
                        }
                    }
                    if (!done) {
                        if (action.action_type.equalsIgnoreCase("cube") || action.action_type.equalsIgnoreCase("fill")) {
                            Location startLoc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            Location endLoc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(3)), Integer.parseInt(splitStr.get(4)), Integer.parseInt(splitStr.get(5)));
                            boolean hollow = false;
                            if (splitStr.size() > 7)
                                hollow = Boolean.parseBoolean(splitStr.get(7));
                            for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                                for (int x = startLoc.getBlockX(); x <= endLoc.getBlockX(); x++) {
                                    for (int z = startLoc.getBlockZ(); z <= endLoc.getBlockZ(); z++) {
                                        if (!hollow || (x == startLoc.getBlockX() || x == endLoc.getBlockX() || y == startLoc.getBlockY() || y == endLoc.getBlockY() || z == startLoc.getBlockZ() || z == endLoc.getBlockZ())) {
                                            Location loc = new Location(CommandUtils.getWorld(sender), x, y, z);
                                            blockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(6)));
                                        }
                                    }
                                }
                            }
                        } else if (action.action_type.equalsIgnoreCase("segmentcube") || action.action_type.equalsIgnoreCase("segcube") || action.action_type.equalsIgnoreCase("scube") || action.action_type.equalsIgnoreCase("segmentfill") || action.action_type.equalsIgnoreCase("segfill") || action.action_type.equalsIgnoreCase("sfill")) {
                            Location startLoc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            Location endLoc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(3)), Integer.parseInt(splitStr.get(4)), Integer.parseInt(splitStr.get(5)));
                            boolean hollow = false;
                            if (splitStr.size() > 7)
                                hollow = Boolean.parseBoolean(splitStr.get(7));
                            for (int y = startLoc.getBlockY(); y <= endLoc.getBlockY(); y++) {
                                for (int x = startLoc.getBlockX(); x <= endLoc.getBlockX(); x++) {
                                    for (int z = startLoc.getBlockZ(); z <= endLoc.getBlockZ(); z++) {
                                        if (!hollow || (x == startLoc.getBlockX() || x == endLoc.getBlockX() || y == startLoc.getBlockY() || y == endLoc.getBlockY() || z == startLoc.getBlockZ() || z == endLoc.getBlockZ())) {
                                            Location loc = new Location(CommandUtils.getWorld(sender), x, y, z);
                                            segmentBlockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(6)));
                                        }
                                    }
                                }
                            }
                        } else if (action.action_type.equalsIgnoreCase("block")) {
                            Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            blockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(3)));
                        } else if (action.action_type.equalsIgnoreCase("segmentblock") || action.action_type.equalsIgnoreCase("segblock") || action.action_type.equalsIgnoreCase("sblock")) {
                            Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            segmentBlockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(3)));
                        } else if (action.action_type.equalsIgnoreCase("circle")) {
                            Location center = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            int radius = Integer.parseInt(splitStr.get(4));
                            CircleGenerator.Plane plane = CircleGenerator.Plane.valueOf(splitStr.get(5).toUpperCase());
                            boolean hollow = false;
                            boolean ignoreEnclosed = false;
                            boolean allowBurrs = false;
                            if (splitStr.size() > 6)
                                hollow = Boolean.parseBoolean(splitStr.get(6));
                            if (splitStr.size() > 7)
                                ignoreEnclosed = Boolean.parseBoolean(splitStr.get(7));
                            if (splitStr.size() > 8)
                                allowBurrs = Boolean.parseBoolean(splitStr.get(8));
                            for (Location loc : CircleGenerator.generateCircle(center, radius, plane, hollow, ignoreEnclosed, allowBurrs)) {
                                blockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(3)));
                            }
                        } else if (action.action_type.equalsIgnoreCase("segmentcircle") || action.action_type.equalsIgnoreCase("segcircle") || action.action_type.equalsIgnoreCase("scircle")) {
                            Location center = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                            int radius = Integer.parseInt(splitStr.get(4));
                            CircleGenerator.Plane plane = CircleGenerator.Plane.valueOf(splitStr.get(5).toUpperCase());
                            boolean hollow = false;
                            boolean ignoreEnclosed = false;
                            boolean allowBurrs = false;
                            if (splitStr.size() > 8)
                                allowBurrs = Boolean.parseBoolean(splitStr.get(8));
                            else if (splitStr.size() > 7)
                                ignoreEnclosed = Boolean.parseBoolean(splitStr.get(7));
                            else if (splitStr.size() > 6)
                                hollow = Boolean.parseBoolean(splitStr.get(6));
                            for (Location loc : CircleGenerator.generateCircle(center, radius, plane, hollow, ignoreEnclosed, allowBurrs)) {
                                segmentBlockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(3)));
                            }
                        }
                       /*else if (action.action_type.equalsIgnoreCase("sphere")) {
                           Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                           // TODO: Add spheres
//                            blockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(3)));
                       }
                       else if (action.action_type.equalsIgnoreCase("segmentsphere") || action.action_type.equalsIgnoreCase("segsphere") || action.action_type.equalsIgnoreCase("ssphere")) {
                           Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                           // TODO: Add segment spheres
//                            segmentBlockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(3)));
                       }
                       else if (action.action_type.equalsIgnoreCase("hollowsphere")) {
                           Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                           // TODO: Add hollow spheres
//                            blockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(3)));
                       }
                       else if (action.action_type.equalsIgnoreCase("segmenthollowsphere") || action.action_type.equalsIgnoreCase("seghollowsphere") || action.action_type.equalsIgnoreCase("shollowsphere")) {
                           Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(0)), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)));
                           // TODO: Add segment hollow spheres
//                            segmentBlockBuilder.addBlock(loc, Bukkit.createBlockData(splitStr.get(3)));
                       }*/ else if (action.action_type.equalsIgnoreCase("summon") || action.action_type.equalsIgnoreCase("summonmob") || action.action_type.equalsIgnoreCase("summonentity") || action.action_type.equalsIgnoreCase("spawnmob") || action.action_type.equalsIgnoreCase("spawnentity")) {
                            World world = CommandUtils.getWorld(sender);
                            EntityType type = EntityType.fromName(splitStr.get(0));
                            NBTEditor.NBTCompound nbt = NBTEditor.NBTCompound.fromJson("{}");
                            Location loc = new Location(world, 0, 0, 0);
                            int amount = 1;
                            if (splitStr.size() == 6) {
                                // ENTITY_TYPE NBT X Y Z AMOUNT
                                nbt = NBTEditor.NBTCompound.fromJson(splitStr.get(1));
                                loc = new Location(world, Integer.parseInt(splitStr.get(2)), Integer.parseInt(splitStr.get(3)), Integer.parseInt(splitStr.get(4)));
                                amount = Integer.parseInt(splitStr.get(2));
                            } else if (splitStr.size() == 5) {
                                if (splitStr.get(1).matches("-?\\d+(\\.\\d+)?")) {
                                    // ENTITY_TYPE X Y Z AMOUNT
                                    loc = new Location(world, Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)), Integer.parseInt(splitStr.get(3)));
                                    amount = Integer.parseInt(splitStr.get(4));
                                } else {
                                    // ENTITY_TYPE NBT X Y Z
                                    nbt = NBTEditor.NBTCompound.fromJson(splitStr.get(1));
                                    loc = new Location(world, Integer.parseInt(splitStr.get(2)), Integer.parseInt(splitStr.get(3)), Integer.parseInt(splitStr.get(4)));
                                    amount = 1;
                                }
                            } else if (splitStr.size() == 4) {
                                // ENTITY_TYPE X Y Z
                                loc = new Location(world, Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)), Integer.parseInt(splitStr.get(3)));
                                amount = 1;
                            }

                            for (int i = 0; i < amount; i++) {
                                entityBuilder.addEntity(loc, type, nbt);
                                // world.spawnEntity(loc, type);
                            }
                        } else if (action.action_type.equalsIgnoreCase("worldborder")) {
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
                                } else {
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
                            } else if (splitStr.get(0).equalsIgnoreCase("set")) {
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
                            } else if (splitStr.get(0).equalsIgnoreCase("add")) {
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
                        } else if (action.action_type.equalsIgnoreCase("holo") || action.action_type.equalsIgnoreCase("hologram")) {
                            Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)), Integer.parseInt(splitStr.get(3)));

                            if (SkyRiser.useHolographicDisplays) {
                                NamedHologram hologram = new NamedHologram(loc, splitStr.get(0));
                                // TODO: Set the name

                                String linesStr = splitStr.get(4).substring(1, splitStr.get(4).length() - 1);
                                List<String> lines = new ArrayList<>(Arrays.asList(linesStr.replace(", ", ",").split(",")));

                                for (String line : lines) {
                                    hologram.appendTextLine(line.substring(1, line.length() - 1));
                                }
                                NamedHologramManager.addHologram(hologram);
                                hologram.refreshAll();

                                HologramDatabase.saveHologram(hologram);
                                HologramDatabase.trySaveToDisk();
                            }
                            else
                                sender.sendMessage(SkyRiser.prefix + ChatColor.YELLOW + "Warning: This style requires a hologram plugin to fully work! Install Holographic Displays!");
                        } else if (action.action_type.equalsIgnoreCase("particle")) {
                            Location loc = new Location(CommandUtils.getWorld(sender), Integer.parseInt(splitStr.get(1)), Integer.parseInt(splitStr.get(2)), Integer.parseInt(splitStr.get(3)));

                            if (SkyRiser.useParticleLib) {
                                // TODO: Add this
                            }
                            else
                                sender.sendMessage(SkyRiser.prefix + ChatColor.YELLOW + "Warning: This style requires ParticleLib to fully work!");
                        }
                    }
                }
                if (sender instanceof Player && (segmentBlockBuilder.blocks.size() > 0 || segmentEntityBuilder.entityDatas.size() > 0)) {
                    boolean success = true;
                    if (segmentBlockBuilder.blocks.size() > 0)
                        success = SkyRiser.instance.addPlayerBuilder(sender.getName(), segmentBlockBuilder);
                    if (segmentEntityBuilder.entityDatas.size() > 0)
                        success = success && SkyRiser.instance.addPlayerBuilder(sender.getName(), segmentEntityBuilder);

                    if (success)
                        sender.sendMessage(SkyRiser.prefix + ChatColor.GREEN + "Started building something, stop with " + ChatColor.DARK_GREEN + "/skyscraper stop" + ChatColor.GREEN + ".");
                    else
                        sender.sendMessage(SkyRiser.prefix + ChatColor.RED + "Error: You are already building something, stop with " + ChatColor.DARK_RED + "/skyscraper stop" + ChatColor.RED + ".");
                }
                if (blockBuilder.blocks.size() > 0) {
                    SkyRiser.instance.addPlayerHistory(sender.getName(), blockBuilder);
                    blockBuilder.build();
                }
                if (entityBuilder.entityDatas.size() > 0) {
                    SkyRiser.instance.addPlayerHistory(sender.getName(), entityBuilder);
                    entityBuilder.build();
                }
            }
        }

        return true;
    }
}
