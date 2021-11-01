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
import ml.darubyminer360.skyriser.files.Palette;
import ml.darubyminer360.skyriser.files.Style;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class SkyTemplateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(SkyRiser.prefix + ChatColor.RED + "Missing argument: template type!" + ChatColor.RESET);
            return true;
        } else if (args.length == 1) {
            player.sendMessage(SkyRiser.prefix + ChatColor.RED + "Missing argument: action!" + ChatColor.RESET);
            return true;
        }

        if (args[0].equalsIgnoreCase("list")) {
            if (args[1].equalsIgnoreCase("style")) {
                SkyRiser.styleManager.loadStyles();
                for (Map.Entry<String, Style> s : SkyRiser.styleManager.getStyles().entrySet()) {
                    player.sendMessage(s.getKey());
                }
            } else if (args[1].equalsIgnoreCase("palette")) {
                SkyRiser.paletteManager.loadPalettes();
                for (Map.Entry<String, Palette> p : SkyRiser.paletteManager.getPalettes().entrySet()) {
                    player.sendMessage(p.getKey());
                }
            }
        }

        return true;
    }
}
