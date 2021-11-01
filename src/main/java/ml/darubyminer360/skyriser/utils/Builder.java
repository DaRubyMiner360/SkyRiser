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
import org.bukkit.block.data.BlockData;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.Map;

public class Builder {
    public LinkedHashMap<Location, BlockData> allOriginalBlocks = new LinkedHashMap<>();
    public LinkedHashMap<Location, BlockData> allBuiltBlocks = new LinkedHashMap<>();
    public LinkedHashMap<Location, BlockData> blocks = new LinkedHashMap<>();
    protected boolean shouldRotate;
    protected CommandSender sender;

    public Builder(CommandSender sender, boolean shouldRotate) {
        this.sender = sender;
        this.shouldRotate = shouldRotate;
    }

    public void addBlock(Location location, BlockData blockData) {
        blocks.put(location, blockData);
    }

    public void build() {
        String senderRotation;
        Location origin;
        if (sender instanceof Player) {
            senderRotation = BlockUtils.getCardinalDirection(((Player) sender).getLocation());
            origin = ((Player) sender).getEyeLocation();
        } else {
            senderRotation = BlockUtils.getCardinalDirection(((BlockCommandSender) sender).getBlock().getLocation());
            origin = ((BlockCommandSender) sender).getBlock().getLocation();
        }
        Vector direction = origin.getDirection();

        Location center = origin.add(direction);

        LinkedHashMap<Location, BlockData> blocks = new LinkedHashMap<>(this.blocks);
        int smallest = 0;
        int largest = 0;
        int amount = 0;

        for (Map.Entry<Location, BlockData> block : blocks.entrySet()) {
            Location location = block.getKey();

            switch (senderRotation) {
                case "West" -> {
                    // West (Negative X, 90 Degrees)
                    amount = 90;
                    if (location.getBlockX() > smallest)
                        smallest = location.getBlockX();
                    if (location.getBlockX() < largest)
                        largest = location.getBlockX();
                }
                case "North" -> {
                    // North (Negative Z, 180 Degrees)
                    amount = 180;
                    if (location.getBlockZ() > smallest)
                        smallest = location.getBlockZ();
                    if (location.getBlockZ() < largest)
                        largest = location.getBlockZ();
                }
                case "East" -> {
                    // East (Positive X, 270 Degrees)
                    amount = 270;
                    if (location.getBlockX() < smallest)
                        smallest = location.getBlockX();
                    if (location.getBlockX() > largest)
                        largest = location.getBlockX();
                }
                case "South" -> {
                    // South (Positive Z, 0 Degrees)
                    amount = 0;
                    if (location.getBlockZ() < smallest)
                        smallest = location.getBlockZ();
                    if (location.getBlockZ() > largest)
                        largest = location.getBlockZ();
                }
            }
        }

        for (Map.Entry<Location, BlockData> block : blocks.entrySet()) {
            Location location = block.getKey();

            if (shouldRotate) {
//                Vector rotation = BlockUtils.getRotationDirection(origin, amount);
//
//                Vector initialRotation = rotation.clone().multiply((largest - smallest) / 4);
//                location = center.clone().add(location).add(initialRotation).subtract(0, (largest - smallest) / 2, 0);
//                rotation.multiply(-1);
            }

            allOriginalBlocks.put(location, location.getBlock().getBlockData());
            location.getBlock().setType(block.getValue().getMaterial(), false);
            location.getBlock().setBlockData(block.getValue(), false);
            allBuiltBlocks.put(location, location.getBlock().getBlockData());
        }
    }

    public void buildLayer(int layer) {
        String senderRotation;
        Location origin;
        if (sender instanceof Player) {
            senderRotation = BlockUtils.getCardinalDirection(((Player) sender).getLocation());
            origin = ((Player) sender).getEyeLocation();
        } else {
            senderRotation = BlockUtils.getCardinalDirection(((BlockCommandSender) sender).getBlock().getLocation());
            origin = ((BlockCommandSender) sender).getBlock().getLocation();
        }
        Vector direction = origin.getDirection();

        Location center = origin.add(direction);

        LinkedHashMap<Location, BlockData> blocks = new LinkedHashMap<>(this.blocks);
        layer -= 1;
        int smallest = 0;
        int largest = 0;
        int amount = 0;

        for (Map.Entry<Location, BlockData> block : blocks.entrySet()) {
            Location location = block.getKey();

            switch (senderRotation) {
                case "West" -> {
                    // West (Negative X, 90 Degrees)
                    amount = 90;
                    if (location.getBlockX() > smallest)
                        smallest = location.getBlockX();
                    if (location.getBlockX() < largest)
                        largest = location.getBlockX();
                }
                case "North" -> {
                    // North (Negative Z, 180 Degrees)
                    amount = 180;
                    if (location.getBlockZ() > smallest)
                        smallest = location.getBlockZ();
                    if (location.getBlockZ() < largest)
                        largest = location.getBlockZ();
                }
                case "East" -> {
                    // East (Positive X, 270 Degrees)
                    amount = 270;
                    if (location.getBlockX() < smallest)
                        smallest = location.getBlockX();
                    if (location.getBlockX() > largest)
                        largest = location.getBlockX();
                }
                case "South" -> {
                    // South (Positive Z, 0 Degrees)
                    amount = 0;
                    if (location.getBlockZ() < smallest)
                        smallest = location.getBlockZ();
                    if (location.getBlockZ() > largest)
                        largest = location.getBlockZ();
                }
            }
        }

        layer += smallest;

        for (Map.Entry<Location, BlockData> block : blocks.entrySet()) {
            Location location = block.getKey();

            if (shouldRotate) {
//                Vector rotation = BlockUtils.getRotationDirection(origin, amount);
//
//                Vector initialRotation = rotation.clone().multiply((largest - smallest) / 4);
//                location = center.clone().add(location).add(initialRotation).subtract(0, (largest - smallest) / 2, 0);
//                rotation.multiply(-1);
            }

            switch (senderRotation) {
                case "West" -> {
                    // West (Negative X, 90 Degrees)
                    sender.sendMessage(String.valueOf(location.getBlockX()));
                    sender.sendMessage(String.valueOf(-layer));
                    if (location.getBlockX() == layer) {
                        sender.sendMessage("AA");
                        allOriginalBlocks.put(location, location.getBlock().getBlockData());
                        location.getBlock().setType(block.getValue().getMaterial(), false);
                        location.getBlock().setBlockData(block.getValue(), false);
                        allBuiltBlocks.put(location, location.getBlock().getBlockData());
                    } else
                        sender.sendMessage("AAA");
                }
                case "North" -> {
                    // North (Negative Z, 180 Degrees)
                    if (location.getBlockZ() == layer) {
                        sender.sendMessage("AB");
                        allOriginalBlocks.put(location, location.getBlock().getBlockData());
                        location.getBlock().setType(block.getValue().getMaterial(), false);
                        location.getBlock().setBlockData(block.getValue(), false);
                        allBuiltBlocks.put(location, location.getBlock().getBlockData());
                    } else
                        sender.sendMessage("AAB");
                }
                case "East" -> {
                    // East (Positive X, 270 Degrees)
                    if (location.getBlockX() == layer) {
                        sender.sendMessage("AC");
                        allOriginalBlocks.put(location, location.getBlock().getBlockData());
                        location.getBlock().setType(block.getValue().getMaterial(), false);
                        location.getBlock().setBlockData(block.getValue(), false);
                        allBuiltBlocks.put(location, location.getBlock().getBlockData());
                    } else
                        sender.sendMessage("AAC");
                }
                case "South" -> {
                    // South (Positive Z, 0 Degrees)
                    if (location.getBlockZ() == layer) {
                        sender.sendMessage("AD");
                        allOriginalBlocks.put(location, location.getBlock().getBlockData());
                        location.getBlock().setType(block.getValue().getMaterial(), false);
                        location.getBlock().setBlockData(block.getValue(), false);
                        allBuiltBlocks.put(location, location.getBlock().getBlockData());
                    } else
                        sender.sendMessage("AAD");
                }
            }
        }
    }

    public int getLargest() {
        double senderRotation;
        if (sender instanceof Player)
            senderRotation = (((Player) sender).getLocation().getYaw() - 90.0F) % 360.0F;
        else
            senderRotation = (((BlockCommandSender) sender).getBlock().getLocation().getYaw() - 90.0F) % 360.0F;

        int largest = 0;

        for (Map.Entry<Location, BlockData> block : blocks.entrySet()) {
            Location location = block.getKey();

            if (((0.0D <= senderRotation) && (senderRotation < 45.0D))) {
                // West (Negative X, 90 Degrees)
                if (location.getBlockX() < largest)
                    largest = location.getBlockX();
            } else if ((45.0D <= senderRotation) && (senderRotation < 135.0D)) {
                // North (Negative Z, 180 Degrees)
                if (location.getBlockZ() < largest)
                    largest = location.getBlockZ();
            } else if ((135.0D <= senderRotation) && (senderRotation < 225.0D)) {
                // East (Positive X, 270 Degrees)
                if (location.getBlockX() > largest)
                    largest = location.getBlockX();
            } else if ((225.0D <= senderRotation) && (senderRotation < 315.0D)) {
                // South (Positive Z, 0 Degrees)
                if (location.getBlockZ() > largest)
                    largest = location.getBlockZ();
            } else if ((315.0D <= senderRotation) && (senderRotation < 360.0D)) {
                // West (Negative X, 90 Degrees)
                if (location.getBlockX() < largest)
                    largest = location.getBlockX();
            }
        }
        return largest;
    }

    public int getSmallest() {
        double senderRotation;
        if (sender instanceof Player)
            senderRotation = (((Player) sender).getLocation().getYaw() - 90.0F) % 360.0F;
        else
            senderRotation = (((BlockCommandSender) sender).getBlock().getLocation().getYaw() - 90.0F) % 360.0F;

        int smallest = 0;

        for (Map.Entry<Location, BlockData> block : blocks.entrySet()) {
            Location location = block.getKey();

            if (((0.0D <= senderRotation) && (senderRotation < 45.0D))) {
                // West (Negative X, 90 Degrees)
                if (location.getBlockX() > smallest)
                    smallest = location.getBlockX();
            } else if ((45.0D <= senderRotation) && (senderRotation < 135.0D)) {
                // North (Negative Z, 180 Degrees)
                if (location.getBlockZ() > smallest)
                    smallest = location.getBlockZ();
            } else if ((135.0D <= senderRotation) && (senderRotation < 225.0D)) {
                // East (Positive X, 270 Degrees)
                if (location.getBlockX() < smallest)
                    smallest = location.getBlockX();
            } else if ((225.0D <= senderRotation) && (senderRotation < 315.0D)) {
                // South (Positive Z, 0 Degrees)
                if (location.getBlockZ() < smallest)
                    smallest = location.getBlockZ();
            } else if ((315.0D <= senderRotation) && (senderRotation < 360.0D)) {
                // West (Negative X, 90 Degrees)
                if (location.getBlockX() > smallest)
                    smallest = location.getBlockX();
            }
        }
        return smallest;
    }

    public void undo() {
        for (Map.Entry<Location, BlockData> block : allOriginalBlocks.entrySet()) {
            block.getKey().getBlock().setType(block.getValue().getMaterial(), false);
            block.getKey().getBlock().setBlockData(block.getValue(), false);
        }
    }

    public void redo() {
        for (Map.Entry<Location, BlockData> block : allBuiltBlocks.entrySet()) {
            block.getKey().getBlock().setType(block.getValue().getMaterial(), false);
            block.getKey().getBlock().setBlockData(block.getValue(), false);
        }
    }
}
