package com.planetgallium.kitpvp.command;

import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
                            if (item != null && item.getDurability() > 0) {
                                item.setDurability((short) 0);
                            }
                        }

                        for (ItemStack item : p.getInventory().getArmorContents()) {
                            if (item != null && item.getDurability() > 0) {
                                item.setDurability((short) 0);
                            }
                        }

                        p.updateInventory();
                        sender.sendMessage(resources.getMessages().getString("Messages.Commands.Repair"));

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
