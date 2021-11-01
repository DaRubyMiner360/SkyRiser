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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;

/**
 * Borrowed methods from GlowstoneMC
 */
public class CommandUtils {
    static double getDouble(String d, boolean shift) {
        boolean literal = d.split("\\.").length != 1;
        if (shift && !literal) {
            d += ".5";
        }
        return Double.valueOf(d);
    }

    static World getDefaultWorld() {
        return Bukkit.getServer().getWorlds().get(0);
    }

    /**
     * Returns the world that the given command sender is referring to when not specifying one.
     *
     * @param sender a command sender
     * @return the command sender's world if the sender is a block or entity, or the default world
     *     otherwise
     */
    public static World getWorld(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return getDefaultWorld();
        } else if (sender instanceof Entity) {
            return ((Entity) sender).getWorld();
        } else if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getWorld();
        }
        return getDefaultWorld();
    }

    /**
     * Gets the location that is "~ ~ ~" for a command sender.
     *
     * @param sender a command sender
     * @return the sender's location if the sender is a block or entity, or the default world's
     *     coordinate origin otherwise.
     */
    public static Location getLocation(CommandSender sender) {
        if (sender instanceof Entity) {
            return ((Entity) sender).getLocation();
        } else if (sender instanceof BlockCommandSender) {
            return ((BlockCommandSender) sender).getBlock().getLocation();
        }
        return new Location(getDefaultWorld(), 0, 0, 0);
    }

    /**
     * Parses coordinates that may be absolute or relative.
     *
     * @param sender the command sender
     * @param x      the x coordinate specifier
     * @param y      the y coordinate specifier
     * @param z      the z coordinate specifier
     * @return the coordinates
     */
    public static Location getLocation(CommandSender sender, String x, String y, String z) {
        Location currentLocation;
        if (x.startsWith("~") || y.startsWith("~") || z
                .startsWith("~")) { // The coordinates are relative
            currentLocation = getLocation(sender);
        } else { // Otherwise, the current location can be set to 0/0/0 (since it's absolute)
            currentLocation = new Location(getWorld(sender), 0, 0, 0);
        }
        return getLocation(currentLocation, x, y, z);
    }

    /**
     * <p>Gets the relative location based on the given axis values (x/y/z) based on tilde
     * notation.</p>
     *
     * <p>For instance, using axis values of ~10 ~ ~15 will return the location with the offset of
     * the given rotation values.
     *
     * @param location  the initial location
     * @param relativeX the relative x-axis (if there is no tilde [~], then the literal
     *                  value is used)
     * @param relativeY the relative y-axis (if there is no tilde [~], then the literal
     *                  value is used)
     * @param relativeZ the relative z-axis (if there is no tilde [~], then the literal
     *                  value is used)
     * @return the relative location
     */
    public static Location getLocation(Location location, String relativeX, String relativeY,
                                       String relativeZ) {
        double x;
        double y;
        double z;
        if (relativeX.startsWith("~")) {
            double diff = 0;
            if (relativeX.length() > 1) {
                diff = getDouble(relativeX.substring(1), true);
            }
            x = location.getX() + diff;
        } else {
            x = getDouble(relativeX, true);
        }
        if (relativeY.startsWith("~")) {
            double diff = 0;
            if (relativeY.length() > 1) {
                diff = getDouble(relativeY.substring(1), false);
            }
            y = location.getY() + diff;
        } else {
            y = getDouble(relativeY, false);
        }
        if (relativeZ.startsWith("~")) {
            double diff = 0;
            if (relativeZ.length() > 1) {
                diff = getDouble(relativeZ.substring(1), true);
            }
            z = location.getZ() + diff;
        } else {
            z = getDouble(relativeZ, true);
        }
        return new Location(location.getWorld(), x, y, z);
    }
}
