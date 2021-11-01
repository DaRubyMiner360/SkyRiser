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

package ml.darubyminer360.skyriser.utils;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BlockUtils {
    public static List<String> split(String orig, char delimiter) {
        List<String> splitted = new ArrayList<>();
        int nextingLevel = 0;
        StringBuilder result = new StringBuilder();
        for (char c : orig.toCharArray()) {
            if (c == delimiter && nextingLevel == 0) {
                splitted.add(result.toString());
                result.setLength(0);
            } else {
                if (c == '[')
                    nextingLevel++;
                else if (c == ']')
                    nextingLevel--;
                result.append(c);
            }
        }
        splitted.add(result.toString());
        return splitted;
    }

    public static List<String> split(String orig) {
        return split(orig, ',');
    }

    public static Vector getRotationDirection(Location origin, int amount) {
        Location rotation = origin.clone();
        rotation.setPitch(0);
        rotation.setYaw(rotation.getYaw() - amount);

        return rotation.getDirection();
    }

    /**
     * Get the cardinal compass direction of a player.
     */
    public static String getCardinalDirection(Location location) {
        float yaw = location.getYaw();
        if (yaw < 0) {
            yaw += 360;
        }
        if (yaw >= 315 || yaw < 45) {
            return "South";
        } else if (yaw < 135) {
            return "West";
        } else if (yaw < 225) {
            return "North";
        } else if (yaw < 315) {
            return "East";
        }
        return "North";
    }

    /**
     *
     * @param centerBlock Define the center of the sphere
     * @param radius Radius of your sphere
     * @param hollow If your sphere should be hollow (you only get the blocks outside) just put in "true" here
     * @return Returns the locations of the blocks in the sphere
     *
     */
    public static List<Location> generateSphere(Location centerBlock, int radius, boolean hollow) {
        List<Location> circleBlocks = new ArrayList<>();

        int bx = centerBlock.getBlockX();
        int by = centerBlock.getBlockY();
        int bz = centerBlock.getBlockZ();

        for (int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx - x) * (bx - x) + ((bz - z) * (bz - z)) + ((by - y) * (by - y)));
                    if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1))))
                        circleBlocks.add(new Location(centerBlock.getWorld(), x, y, z));
                }
            }
        }

        return circleBlocks;
    }
}
