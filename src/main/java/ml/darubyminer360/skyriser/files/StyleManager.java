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

package ml.darubyminer360.skyriser.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ml.darubyminer360.skyriser.SkyRiser;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

public class StyleManager {
    File path = new File(SkyRiser.instance.getDataFolder() + "/styles");
    protected HashMap<String, Style> stylesArray = new HashMap<>();

    public StyleManager() { onEnable(); }

    public HashMap<String, Style> getStyles() {
        return stylesArray;
    }

    public void onEnable() {
        try {
            if (path.mkdirs() || Config.get().getBoolean("AlwaysAddExampleStyles")) {
                SkyRiser.instance.saveResource("styles/example.style", false);
                SkyRiser.instance.saveResource("styles/hologramexample.style", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (SkyRiser.instance.getServer().getOnlinePlayers().size() == 0) {
            return;
        }
        loadStyles();
    }

    public void loadStyles() {
        stylesArray.clear();
        for (File file : path.listFiles()) {
            try {
                if (file.isFile() && file.canRead() && (file.getName().endsWith(".style") || file.getName().endsWith(".json") || file.getName().endsWith(".json5"))) {
                    StringBuilder jsonString = new StringBuilder();

                    Scanner reader = new Scanner(file);
                    while (reader.hasNextLine()) {
                        jsonString.append(reader.nextLine()).append("\n");
                    }

                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();

                    Gson gson = builder.create();
                    Style style = gson.fromJson(jsonString.toString(), Style.class);

                    stylesArray.put(file.getName(), style);

                    reader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
