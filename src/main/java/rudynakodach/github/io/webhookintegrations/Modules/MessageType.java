/*
 * WebhookIntegrations
 * Copyright (C) 2023 rudynakodach
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package rudynakodach.github.io.webhookintegrations.Modules;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public enum MessageType {
    SERVER_START("onServerStart"),
    SERVER_STOP("onServerStop"),
    PLAYER_JOIN("onPlayerJoin"),
    PLAYER_QUIT("onPlayerQuit"),
    PLAYER_KICK("onPlayerKicked"),
    PLAYER_DEATH_NPC("onPlayerDeath.playerKilledByNPC"),
    PLAYER_DEATH_KILLED("onPlayerDeath.playerKilledByPlayer"),
    PLAYER_CHAT("onPlayerChat"),
    PLAYER_ADVANCEMENT("onPlayerAdvancement"),
    PLAYER_COUNT_CHANGED("onPlayerCountChange");


    private String t;
    MessageType(String type) {
        this.t = type;
    }

    public String toString() {
        return t;
    }

    public static List<String> getAllMessageTypes() {
        List<String> messageTypes = new ArrayList<>();
        try {
            for (Field field : MessageType.class.getDeclaredFields()) {
                if (field.getType() == String.class) {
                    field.setAccessible(true);
                    messageTypes.add((String) field.get(null));
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return messageTypes;
    }
}
