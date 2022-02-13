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

package ml.darubyminer360.skyriser.utils;

import org.bukkit.command.CommandSender;

public abstract class Builder {
    protected boolean shouldRotate;
    protected CommandSender sender;

    public Builder(CommandSender sender, boolean shouldRotate) {
        this.sender = sender;
        this.shouldRotate = shouldRotate;
    }

    public abstract void build();
    public abstract void buildLayer(int layer);

    public abstract int getLargest();
    public abstract int getSmallest();

    public abstract void undo();
    public abstract void redo();
}
