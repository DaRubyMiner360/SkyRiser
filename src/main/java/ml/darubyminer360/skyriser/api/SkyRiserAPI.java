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

package ml.darubyminer360.skyriser.api;

import ml.darubyminer360.skyriser.SkyRiser;
import ml.darubyminer360.skyriser.files.Palette;
import ml.darubyminer360.skyriser.files.Style;
import ml.darubyminer360.skyriser.utils.Function6;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class SkyRiserAPI {
    static List<Function6<CommandSender, Style, Style.Action, Palette, List<String>, Boolean>> customActions = new ArrayList<>();


    public static boolean addAction(Function6<CommandSender, Style, Style.Action, Palette, List<String>, Boolean> action) { return customActions.add(action); }

    public static boolean removeAction(Function6<CommandSender, Style, Style.Action, Palette, List<String>, Boolean> action) { return customActions.remove(action); }

    public static List<Function6<CommandSender, Style, Style.Action, Palette, List<String>, Boolean>> getActions() { return customActions; }

    public static boolean undo(String playerName, int amount) {
        return SkyRiser.instance.undo(playerName, amount);
    }

    public static boolean undo(String playerName) {
        return SkyRiser.instance.undo(playerName);
    }

    public static boolean redo(String playerName, int amount) { return SkyRiser.instance.redo(playerName, amount); }

    public static boolean redo(String playerName) {
        return SkyRiser.instance.redo(playerName);
    }
}
