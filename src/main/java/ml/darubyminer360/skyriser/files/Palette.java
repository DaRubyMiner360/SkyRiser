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

import org.bukkit.block.data.BlockData;

import java.util.ArrayList;
import java.util.List;

public class Palette {
    public List<String> blockDataStrings = new ArrayList<>();
    public List<BlockData> blockDatas = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        try {
            Palette other  = (Palette) obj;
            return blockDatas.equals(other.blockDatas) && blockDataStrings.equals(other.blockDataStrings);
        } catch (Exception e) {
            return false;
        }
    }
}
