package net.kunmc.lab.configlib.util;


import com.mojang.authlib.GameProfile;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.UUID;

public final class UUIDUtil {
    private UUIDUtil() {
    }

    public static String getNameOrUuid(UUID uuid) {
        GameProfile gameProfile = ServerLifecycleHooks.getCurrentServer()
                                                      .getPlayerProfileCache()
                                                      .getProfileByUUID(uuid);
        if (gameProfile == null) {
            return uuid.toString();
        }
        if (gameProfile.getName() == null) {
            return uuid.toString();
        }
        return gameProfile.getName();
    }
}
