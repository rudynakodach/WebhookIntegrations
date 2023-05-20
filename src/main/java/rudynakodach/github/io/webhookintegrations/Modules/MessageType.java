package rudynakodach.github.io.webhookintegrations.Modules;


public enum MessageType {
    SERVER_START("onServerStart"),
    SERVER_STOP("onServerStop"),
    PLAYER_JOIN("onPlayerJoin"),
    PLAYER_QUIT("onPlayerQuit"),
    PLAYER_KICK("onPlayerKicked"),
    PLAYER_DEATH_NPC("onPlayerDeath.playerKilledByNPC"),
    PLAYER_DEATH_KILLED("noPlayerDeath.playerKilledByPlayer"),
    PLAYER_CHAT("onPlayerChat"),
    PLAYER_ADVANCEMENT("onPlayerAdvancement");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
