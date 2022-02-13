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

package ml.darubyminer360.skyriser.listeners;

import ml.darubyminer360.skyriser.SkyRiser;
import ml.darubyminer360.skyriser.files.Config;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateCheckerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
//        if (Config.get().getBoolean("SendAvailableUpdateMessage")) {
//            SkyRiser.updateChecker.getVersion(version -> {
//                if (!SkyRiser.instance.getDescription().getVersion().equalsIgnoreCase(version)) {
//                    event.getPlayer().sendMessage("There is a new update available for SkyRiser! Download it at: https://api.spigotmc.org/legacy/update.php?resource=" + SkyRiser.updateChecker.resourceId);
//                }
//            });
//        }
    }
}
