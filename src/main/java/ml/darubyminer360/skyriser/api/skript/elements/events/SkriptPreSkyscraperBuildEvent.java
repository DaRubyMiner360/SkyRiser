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

package ml.darubyminer360.skyriser.api.skript.elements.events;

import ch.njol.skript.expressions.ExprCommandSender;
import ml.darubyminer360.skyriser.api.PreSkyscraperBuildEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Checker;

public class SkriptPreSkyscraperBuildEvent extends SkriptEvent {
    static {
        Skript.registerEvent("Pre Skyscraper Build", SkriptPreSkyscraperBuildEvent.class, PreSkyscraperBuildEvent.class, "(pre|before) [%-commandsender%] skyscraper [buil(d|t|ding)]");
    }

    Literal<CommandSender> sender;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, ParseResult parseResult) {
        sender = (Literal<CommandSender>) args[1];
        return true;
    }

    @Override
    public boolean check(Event e) {
        if (sender != null) {
            CommandSender s = ((PreSkyscraperBuildEvent) e).getSender();
            return sender.check(e, data -> data.getClass().isInstance(s));
        }
        return true;
    }

    @Override
    public String toString(Event e, boolean debug) {
        return "Pre Skyscraper Build event " + sender.toString(e, debug);
    }

}
