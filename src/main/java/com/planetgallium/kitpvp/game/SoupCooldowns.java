package com.planetgallium.kitpvp.game;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SoupCooldowns {

    public static final int COOLDOWN_MINS = 5;
    private static final long COOLDOWN_MILLIS = 1000 * 60 * COOLDOWN_MINS;
    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();

    public static boolean isOnCooldown(Player p) {

        Long lastUse = COOLDOWNS.get(p.getUniqueId());
        return lastUse != null && System.currentTimeMillis() - lastUse < COOLDOWN_MILLIS;

    }

    public static void add(Player p) {

        COOLDOWNS.put(p.getUniqueId(), System.currentTimeMillis());

    }

    public static void reset(Player p) {

        COOLDOWNS.remove(p.getUniqueId());

    }

}
