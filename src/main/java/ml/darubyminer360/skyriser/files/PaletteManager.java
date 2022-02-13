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


package ml.darubyminer360.skyriser.files;

import ml.darubyminer360.skyriser.SkyRiser;
import ml.darubyminer360.skyriser.utils.BlockUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;

public class PaletteManager {
    File path = new File(SkyRiser.instance.getDataFolder() + "/palettes");
    HashMap<String, Palette> palettesArray = new HashMap<>();

    public char delimiter = ',';

    public PaletteManager() {
        onEnable();
    }

    public HashMap<String, Palette> getPalettes() {
        return palettesArray;
    }

    public void onEnable() {
        try {
            if (path.mkdirs())
                SkyRiser.instance.saveResource("palettes/example.palette", false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (SkyRiser.instance.getServer().getOnlinePlayers().size() == 0) {
            return;
        }
        loadPalettes();
    }

    public void loadPalettes() {
        palettesArray.clear();
        for (File file : path.listFiles()) {
            try {
                if (file.isFile() && file.canRead() && (file.getName().endsWith(".palette") || file.getName().endsWith(".csv"))) {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);

                    Palette palette = new Palette();

                    String row;
                    List<String> cells;
                    while ((row = br.readLine()) != null) {
                        cells = BlockUtils.split(row, delimiter);
                        for (String cell : cells) {
                            palette.blockDatas.add(Bukkit.createBlockData(cell));
                            palette.blockDataStrings.add(cell);
                        }
                    }
                    br.close();

                    palettesArray.put(file.getName(), palette);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

