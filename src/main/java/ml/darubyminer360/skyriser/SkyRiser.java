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

package ml.darubyminer360.skyriser;


import ml.darubyminer360.skyriser.api.SkyRiserAPI;
import ml.darubyminer360.skyriser.commands.*;
import ml.darubyminer360.skyriser.files.*;

import ml.darubyminer360.skyriser.listeners.PlayerJoinListener;
import ml.darubyminer360.skyriser.utils.UpdateChecker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public final class SkyRiser extends JavaPlugin {
    public static SkyRiser instance;

    public static StyleManager styleManager;
    public static PaletteManager paletteManager;

    public static UpdateChecker updateChecker;

    public static String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "Sky" + ChatColor.GREEN + "Riser" + ChatColor.WHITE + "] " + ChatColor.RESET;

    public static boolean useWorldEdit = false;
    public static boolean useHolographicDisplays = false;

    @Override
    public void onEnable() {
        instance = this;

        // Setup config
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        Config.setup();
//        Config.get().addDefault("SendAvailableUpdateMessage", true);
        Config.get().addDefault("AlwaysAddExampleStyles", false);
        Config.get().addDefault("AllowOldStyleFormat", true);
        Config.get().options().copyDefaults(true);
        Config.save();

        getCommand("skyreload").setExecutor(new ReloadCommand());
        getCommand("skyscraper").setExecutor(new SkyscraperCommand());
        getCommand("skyscraper").setTabCompleter(new SkyscraperCommandTabCompletion());
        getCommand("skytemplate").setExecutor(new SkyTemplateCommand());
        getCommand("skytemplate").setTabCompleter(new SkyTemplateCommandTabCompletion());

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);

        styleManager = new StyleManager();
        paletteManager = new PaletteManager();

        /*if (Config.get().getBoolean("SendAvailableUpdateMessage")) {
            updateChecker = new UpdateChecker(this, 12345);
            updateChecker.getVersion(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    getLogger().info("There is not a new update available.");
                } else {
                    getLogger().info("There is a new update available.");
                }
            });
        }*/

        useWorldEdit = Bukkit.getServer().getPluginManager().isPluginEnabled("WorldEdit");
        useHolographicDisplays = Bukkit.getServer().getPluginManager().isPluginEnabled("HolographicDisplays");


//        BiFunction<CommandSender, List<Object>, Boolean> func = (sender, template) -> {
//            Style style = (Style) template.get(0);
//            Style.Action action = (Style.Action) template.get(1);
//            Palette palette = (Palette) template.get(2);
//            List<String> args = (List<String>) template.get(3);
//            if (action.action_type.equalsIgnoreCase("api")) {
//                sender.sendMessage("API Test is Working! " + args.get(0));
//                return true;
//            }
//            return false;
//        };
//        SkyRiserAPI.customActions.add(func);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
