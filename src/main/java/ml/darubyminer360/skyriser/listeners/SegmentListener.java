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

package ml.darubyminer360.skyriser.listeners;

import ml.darubyminer360.skyriser.SkyRiser;
import ml.darubyminer360.skyriser.utils.Builder;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.LinkedHashMap;

public class SegmentListener implements Listener {
    public LinkedHashMap<String, Integer> playerLayers = new LinkedHashMap<>();

    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (SkyRiser.instance.playerBuilders.containsKey(event.getPlayer().getName())) {
            if (!playerLayers.containsKey(event.getPlayer().getName()))
                playerLayers.put(event.getPlayer().getName(), 1);
            Builder builder = SkyRiser.instance.playerBuilders.get(event.getPlayer().getName());
            builder.buildLayer(playerLayers.get(event.getPlayer().getName()));
            playerLayers.replace(event.getPlayer().getName(), playerLayers.get(event.getPlayer().getName()), playerLayers.get(event.getPlayer().getName()) + 1);

            if (SkyRiser.instance.playerBuilders.get(event.getPlayer().getName()).getLargest() - SkyRiser.instance.playerBuilders.get(event.getPlayer().getName()).getSmallest() <= playerLayers.get(event.getPlayer().getName()) - 1)
                playerLayers.replace(event.getPlayer().getName(), playerLayers.get(event.getPlayer().getName()), 1);
        }
    }

    public void onPlayerLeave(PlayerQuitEvent event) {
        playerLayers.remove(event.getPlayer().getName());
        SkyRiser.instance.removePlayerBuilder(event.getPlayer().getName());
    }
}
