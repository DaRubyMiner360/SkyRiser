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

import ml.darubyminer360.skyriser.SkyRiser;
import ml.darubyminer360.skyriser.files.Palette;
import ml.darubyminer360.skyriser.files.Style;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkyscraperCommandTabCompletion implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> valid = new ArrayList<>();

            if (args[0].startsWith("b"))
                valid.add("build");
            else if (args[0].startsWith("s"))
                valid.add("stop");
            else if (args[0].startsWith("u")) {
                if (args[0].startsWith("un"))
                    valid.add("undo");
                else if (args[0].startsWith("us"))
                    valid.add("usage");
                else if (args[0].equals("u")) {
                    valid.add("undo");
                    valid.add("usage");
                }
            } else if (args[0].startsWith("r"))
                    valid.add("redo");
            else if (args[0].equals("")) {
                valid.add("build");
                valid.add("stop");
                valid.add("undo");
                valid.add("redo");
                valid.add("usage");
            }

            return valid;
        } else if (args[0].equalsIgnoreCase("build")) {
            if (args.length == 2) {
                SkyRiser.styleManager.loadStyles();

                List<String> valid = new ArrayList<>();

                for (Map.Entry<String, Style> s : SkyRiser.styleManager.getStyles().entrySet()) {
                    valid.add(s.getKey());
                }

                return valid;
            } else if (args.length == 3) {
                SkyRiser.paletteManager.loadPalettes();

                List<String> valid = new ArrayList<>();

                for (Map.Entry<String, Palette> palette : SkyRiser.paletteManager.getPalettes().entrySet()) {
                    valid.add(palette.getKey());
                }

                return valid;
            } else if (args.length == 4) {
                List<String> valid = new ArrayList<>();

                valid.add("0");
                valid.add("1");
                valid.add("2");
                valid.add("3");
                valid.add("4");
                valid.add("5");

                return valid;
            } else if (args.length == 5) {
                SkyRiser.paletteManager.loadPalettes();

                List<String> valid = new ArrayList<>();

                for (Map.Entry<String, Palette> palette : SkyRiser.paletteManager.getPalettes().entrySet()) {
                    valid.add(palette.getKey());
                }

                return valid;
            }
        } else if (args[0].equalsIgnoreCase("stop")) {
            if (args.length == 2) {
                List<String> valid = new ArrayList<>();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    valid.add(player.getName());
                }

                return valid;
            }
        } else if (args[0].equalsIgnoreCase("undo")) {
            if (args.length == 2) {
                List<String> valid = new ArrayList<>();

                valid.add("0");
                valid.add("1");
                valid.add("2");
                valid.add("3");
                valid.add("4");
                valid.add("5");

                return valid;
            } else if (args.length == 3) {
                List<String> valid = new ArrayList<>();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    valid.add(player.getName());
                }

                return valid;
            }
        } else if (args[0].equalsIgnoreCase("redo")) {
            if (args.length == 2) {
                List<String> valid = new ArrayList<>();

                valid.add("0");
                valid.add("1");
                valid.add("2");
                valid.add("3");
                valid.add("4");
                valid.add("5");

                return valid;
            } else if (args.length == 3) {
                List<String> valid = new ArrayList<>();

                for (Player player : Bukkit.getOnlinePlayers()) {
                    valid.add(player.getName());
                }

                return valid;
            }
        }

        return null;
    }
}
