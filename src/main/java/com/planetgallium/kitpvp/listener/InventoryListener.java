package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {

    private FileConfiguration config;

    public InventoryListener(Game plugin) {
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onKitsItem(InventoryMoveItemEvent e) {

        Player p = (Player) e.getSource().getHolder();

        if (Toolkit.inArena(p)) {

            ItemStack item = Toolkit.getMainHandItem(p);
            ItemMeta meta = item.getItemMeta();

            // Ugly code stolen from ItemListener#onInteract()

            if (item.getType() == XMaterial.matchXMaterial(config.getString("Items.Kits.Item")).get().parseMaterial()) {

                if (meta.getDisplayName().equals(Config.getS("Items.Kits.Name"))) {

                    e.setCancelled(true);

                }

            }

        }

    }

}
