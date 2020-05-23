package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class RespawnListener implements Listener {

    private Arena arena;

    public RespawnListener(Arena arena) {
        this.arena = arena;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {

        if (Toolkit.inArena(e.getPlayer())) {

            e.setRespawnLocation(arena.getRandomSpawn(e.getPlayer()));

        }

    }

}
