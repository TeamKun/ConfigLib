package net.kunmc.lab.configlib.processor.smoke;

import net.kunmc.lab.configlib.annotation.ConfigNullable;
import net.kunmc.lab.configlib.annotation.Description;
import net.kunmc.lab.configlib.annotation.Masked;
import net.kunmc.lab.configlib.annotation.Range;

final class ProcessorSmokeConfig {
    @Description("Maximum number of players.")
    @Range(min = 1, max = 100)
    int maxPlayers = 20;

    @ConfigNullable
    String adminContact = null;

    @Masked
    String token = "secret";

    ArenaSettings arena = new ArenaSettings();

    static final class ArenaSettings {
        @Description("Maximum number of arenas.")
        @Range(min = 1, max = 50)
        int maxArenas = 5;
    }
}
