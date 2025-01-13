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

public final class MessageType {
    public static final String SERVER_START = "onServerStart";
    public static final String SERVER_STOP = "onServerStop";
    public static final String PLAYER_JOIN = "onPlayerJoin";
    public static final String PLAYER_QUIT = "onPlayerQuit";
    public static final String PLAYER_KICK = "onPlayerKicked";
    public static final String PLAYER_DEATH_NPC = "onPlayerDeath.playerKilledByNPC";
    public static final String PLAYER_DEATH_KILLED = "onPlayerDeath.playerKilledByPlayer";
    public static final String PLAYER_CHAT = "onPlayerChat";
    public static final String PLAYER_ADVANCEMENT = "onPlayerAdvancement";

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
