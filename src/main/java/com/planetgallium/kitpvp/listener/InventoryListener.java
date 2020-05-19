package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.util.Config;
import com.planetgallium.kitpvp.util.XMaterial;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {

    private FileConfiguration config;

    public InventoryListener(Game plugin) {
        this.config = plugin.getConfig();
    }

    private void checkAndCancel(Cancellable e, ItemStack item) {

        ItemMeta meta = item.getItemMeta();

        // Ugly code stolen from ItemListener#onInteract()
        if (item.getType() == XMaterial.matchXMaterial(config.getString("Items.Kits.Item")).get().parseMaterial()) {

            if (meta.getDisplayName().equals(Config.getS("Items.Kits.Name"))) {

                e.setCancelled(true);

            }

        }

    }


    @EventHandler
    public void onKitsClick(InventoryClickEvent e) {

        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof Player) { // I don't know what else it could be but ye

            ItemStack item = e.getCurrentItem();

            if (item != null) {

                checkAndCancel(e, item);

            }

        }

    }

    @EventHandler
    public void onKitsDrop(PlayerDropItemEvent e) {

//        ItemStack item = e.getItemDrop().getItemStack();
//        checkAndCancel(e, item);

    }

}
