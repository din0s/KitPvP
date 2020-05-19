package com.planetgallium.kitpvp.command;

import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class RepairCommand implements CommandExecutor {

    private Arena arena;
    private Resources resources;

    public RepairCommand(Arena arena, Resources resources) {
        this.arena = arena;
        this.resources = resources;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("repair")) {

            if (sender instanceof Player) {

                Player p = (Player) sender;

                if (Toolkit.inArena(p)) {

                    if (arena.getKits().hasKit(p.getName())) {

                        for (int i = 0; i < 36; i++) {
                            ItemStack item = p.getInventory().getItem(i);
                            if (item != null) {
                                // https://www.spigotmc.org/threads/1-14-how-to-set-item-durability.390411/
                                ItemMeta meta = item.getItemMeta();
                                if (meta instanceof Damageable) {
                                    ((Damageable) meta).setDamage(0);
                                    item.setItemMeta(meta);
                                }
                            }
                        }

                    } else {

                        sender.sendMessage(resources.getMessages().getString("Messages.Error.NoKit"));

                    }

                } else {

                    sender.sendMessage(resources.getMessages().getString("Messages.Error.Arena"));

                }

                return true;

            }

        }

        return false;

    }

}
