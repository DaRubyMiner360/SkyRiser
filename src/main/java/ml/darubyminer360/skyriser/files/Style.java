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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Style {
    public List<Action> actions = new ArrayList<>();
    public List<Option> options = new ArrayList<>();

    public boolean rotate = true;

    public String toString() {
        return "Style [ actions: " + actions + ", options: " + options + ", rotate: " + rotate + " ]";
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Style other  = (Style) obj;
            return actions.equals(other.actions) && options.equals(other.options) && rotate == other.rotate;
        } catch (Exception e) {
            return false;
        }
    }

    public static class Action {
        public String action_type;
        public String action;

        public String toString() {
            return "Action [ action_type: " + action_type + ", action: " + action + " ]";
        }

        @Override
        public boolean equals(Object obj) {
            try {
                Action other  = (Action) obj;
                return action_type.equals(other.action_type) && action.equals(other.action);
            } catch (Exception e) {
                return false;
            }
        }
    }

    public static class Option {
        public String name;
        public String description;

        public String toString() {
            return "Option [ name: " + name + ", description: " + description + " ]";
        }

        @Override
        public boolean equals(Object obj) {
            try {
                Option other  = (Option) obj;
                return name.equals(other.name) && description.equals(other.description);
            } catch (Exception e) {
                return false;
            }
        }
    }
}
