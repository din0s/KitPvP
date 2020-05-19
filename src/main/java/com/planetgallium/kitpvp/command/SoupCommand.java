package com.planetgallium.kitpvp.command;

import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.game.SoupCooldowns;
import com.planetgallium.kitpvp.util.Resources;
import com.planetgallium.kitpvp.util.Toolkit;
import com.planetgallium.kitpvp.util.XMaterial;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SoupCommand implements CommandExecutor {

    private Economy econ;
    private Arena arena;
    private Resources resources;

    private static final int BASE_COST = 20;
    private static final int EXTRA_COST = 10;

    public SoupCommand(Economy econ, Arena arena, Resources resources) {
        this.econ = econ;
        this.arena = arena;
        this.resources = resources;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        if (command.getName().equalsIgnoreCase("soup")) {

            if (sender instanceof Player) {

                Player p = (Player) sender;

                if (Toolkit.inArena(p)) {

                    if (SoupCooldowns.isOnCooldown(p)) {

                        sender.sendMessage(resources.getMessages().getString("Messages.Error.SoupCooldown").replace("%mins%", String.valueOf(SoupCooldowns.COOLDOWN_MINS)));

                    } else {

                        if (arena.getKits().hasKit(p.getName())) {

                            int count = 0;
                            for (int i = 0; i < 36; i++) {
                                // Code stolen from SoupListener#onDamage()
                                if (p.getInventory().getItem(i) == null) {
                                    count++;
                                }
                            }

                            if (count != 0) {

                                int soups = arena.getStats().getSoups(p.getUniqueId());
                                int cost = BASE_COST + soups * EXTRA_COST;

                                if (econ.has(p, cost)) {

                                    ItemStack soup = new ItemStack(XMaterial.MUSHROOM_STEW.parseItem());

                                    for (int i = 0; i < count; i++) {
                                        p.getInventory().addItem(soup);
                                    }

                                    econ.withdrawPlayer(p, cost);
                                    arena.getStats().addSoup(p.getUniqueId());
                                    SoupCooldowns.add(p);
                                    sender.sendMessage(resources.getMessages().getString("Messages.Commands.Soup").replace("%amount%", String.valueOf(cost)));

                                } else {

                                    sender.sendMessage(resources.getMessages().getString("Messages.Error.Balance").replace("%amount%", String.valueOf(cost)));

                                }

                            } else {

                                sender.sendMessage(resources.getMessages().getString("Messages.Error.FullInv"));

                            }

                        } else {

                            sender.sendMessage(resources.getMessages().getString("Messages.Error.NoKit"));

                        }

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
