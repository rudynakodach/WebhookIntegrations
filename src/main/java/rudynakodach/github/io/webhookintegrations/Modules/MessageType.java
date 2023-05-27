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

public class MessageType {
    public static String SERVER_START = "onServerStart";
    public static String SERVER_STOP = "onServerStop";
    public static String PLAYER_JOIN = "onPlayerJoin";
    public static String PLAYER_QUIT = "onPlayerQuit";
    public static String PLAYER_KICK = "onPlayerKicked";
    public static String PLAYER_DEATH_NPC = "onPlayerDeath.playerKilledByNPC";
    public static String PLAYER_DEATH_KILLED = "noPlayerDeath.playerKilledByPlayer";
    public static String PLAYER_CHAT = "onPlayerChat";
    public static String PLAYER_ADVANCEMENT = "onPlayerAdvancement";
}
