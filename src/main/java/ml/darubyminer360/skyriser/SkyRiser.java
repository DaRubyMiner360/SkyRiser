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

import ml.darubyminer360.skyriser.commands.*;
import ml.darubyminer360.skyriser.files.*;

import ml.darubyminer360.skyriser.listeners.UpdateCheckerListener;
import ml.darubyminer360.skyriser.listeners.SegmentListener;
import ml.darubyminer360.skyriser.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;

public final class SkyRiser extends JavaPlugin {
    public static SkyRiser instance;

    public static StyleManager styleManager;
    public static PaletteManager paletteManager;

    public static UpdateChecker updateChecker;

    public HashMap<String, Builder> playerBuilders = new HashMap<>();
    public HashMap<String, List<Builder>> playerHistories = new HashMap<>();

    File undoSave;

    public static final String prefix = ChatColor.WHITE + "[" + ChatColor.BLUE + "Sky" + ChatColor.GREEN + "Riser" + ChatColor.WHITE + "] " + ChatColor.RESET;

    public static boolean useWorldEdit = false;
    public static boolean useHolographicDisplays = false;
    public static boolean useParticleLib = false;
    public static boolean useSkript = false;

    public static SkriptAddon addon;

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

        Bukkit.getPluginManager().registerEvents(new UpdateCheckerListener(), this);
        Bukkit.getPluginManager().registerEvents(new SegmentListener(), this);

        undoSave = new File(getDataFolder(), "history.dat");
        deserializeUndos(undoSave);

        styleManager = new StyleManager();
        paletteManager = new PaletteManager();

        /*if (Config.get().getBoolean("SendAvailableUpdateMessage")) {
            updateChecker = new UpdateChecker(this, 12345);
            updateChecker.getVersion(version -> {
                if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                    getLogger().info("There is not a new update available.");
                }
                else {
                    getLogger().info("There is a new update available.");
                }
            });
        }*/

        useHolographicDisplays = Bukkit.getServer().getPluginManager().isPluginEnabled("HolographicDisplays");
        useParticleLib = Bukkit.getServer().getPluginManager().isPluginEnabled("ParticleLib");
        useSkript = Bukkit.getServer().getPluginManager().isPluginEnabled("Skript");

        if (useSkript) {
            addon = Skript.registerAddon(this);
            try {
                addon.loadClasses("ml.darubyminer360.skyriser", "api", "skript", "elements");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        serializeUndos(undoSave);
        for (String s : playerBuilders.keySet()) {
            removePlayerBuilder(s);
        }
    }

    public boolean addPlayerBuilder(String playerName, Builder builder) {
        if (playerBuilders.containsKey(playerName)) {
            return false;
        }
        playerBuilders.put(playerName, builder);
        addPlayerHistory(playerName, builder);
        return true;
    }

    public boolean removePlayerBuilder(String playerName) {
        if (!playerBuilders.containsKey(playerName)) {
            return false;
        }
        playerBuilders.remove(playerName);
        return true;
    }

    public void addPlayerHistory(String playerName, Builder builder) {
        if (playerHistories.containsKey(playerName))
            playerHistories.get(playerName).add(builder);
        else
            playerHistories.put(playerName, List.of(builder));
    }

    public boolean undo(String playerName, int amount) {
        if (!playerHistories.containsKey(playerName) || amount > playerHistories.get(playerName).size())
            return false;

        for (int i = 0; i < amount; i++) {
            playerHistories.get(playerName).get(playerHistories.get(playerName).size() - 1).undo();
            playerHistories.get(playerName).remove(playerHistories.get(playerName).size() - 1);
        }
        playerBuilders.remove(playerName);
        return true;
    }

    public boolean undo(String playerName) {
        return undo(playerName, 1);
    }

    public void serializeUndos(File file) {
        ObjectOutputStream oos = null;
        try {
            file.createNewFile();

            oos = new ObjectOutputStream(new FileOutputStream(file));
            oos.writeObject(playerHistories);
        } catch (IOException ex) {
            getLogger().warning(prefix + "Unable to save history file.");
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void deserializeUndos(File file) {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
            playerHistories = (HashMap<String, List<Builder>>) ois.readObject();
            ois.close();
        } catch (Exception ex) {
            if (!(ex instanceof FileNotFoundException))
                getLogger().warning(prefix + "Unable to load history file.");
            playerHistories = new HashMap<>();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException ignored) {
                }
            }
        }
    }
}
