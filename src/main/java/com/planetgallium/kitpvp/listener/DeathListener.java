package com.planetgallium.kitpvp.listener;

import com.planetgallium.kitpvp.Game;
import com.planetgallium.kitpvp.game.Arena;
import com.planetgallium.kitpvp.util.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class DeathListener implements Listener {
	
	private Title title = new Title();
	private Arena arena;
	private FileConfiguration config;
	private Resources resources;
	private Economy econ;
	private Random r;

	private static final int MIN_PAYOUT = 3;
	private static final int MAX_PAYOUT = 5;
	private static final int PAYOUT_GAP = MAX_PAYOUT - MIN_PAYOUT;
	
	public DeathListener(Game plugin, Arena arena, Resources resources) {
		this.arena = arena;
		this.config = plugin.getConfig();
		this.resources = resources;
		this.econ = plugin.getEconomy();
		this.r = new Random();
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		
		if (Toolkit.inArena(e.getEntity())) {

			Player victim = e.getEntity();
			e.setDeathMessage("");

			if (Config.getB("Arena.PreventDeathDrops")) {
				e.getDrops().clear();
			}

			setDeathMessage(victim, e);
			respawnPlayer(victim);

			arena.getStats().addDeath(victim.getUniqueId());
			arena.getStats().resetSoups(victim.getUniqueId());
			arena.getLevels().removeExperience(victim, resources.getLevels().getInt("Levels.General.Experience.Death"));

			victim.getWorld().playEffect(victim.getLocation().add(0.0D, 1.0D, 0.0D), Effect.STEP_SOUND, 152);

			Toolkit.runCommands("Death", victim, "%victim%", victim.getName());

			if (Config.getB("Death.Sound.Enabled")) {
				broadcast(victim.getWorld(), XSound.matchXSound(Config.getS("Death.Sound.Sound")).get().parseSound(), 1, (int) Config.getI("Death.Sound.Pitch"));
			}

		}
	
	}

	private void respawnPlayer(Player victim) {
		
		if (Config.getB("Arena.FancyDeath")) {

			Location deathLocation = victim.getLocation();

			new BukkitRunnable() {

				@Override
				public void run() {

					victim.spigot().respawn();
					victim.setGameMode(GameMode.SPECTATOR);
					victim.teleport(deathLocation);

				}

			}.runTaskLater(Game.getInstance(), 1L);

			arena.removePlayer(victim);
			
			new BukkitRunnable() {
				
				int time = Config.getI("Death.Title.Time");
				
				@Override
				public void run() {
					
					if (time != 0) {

						title.sendTitle(victim, Config.getS("Death.Title.Title"), Config.getS("Death.Title.Subtitle").replace("%seconds%", String.valueOf(time)), 0, 20, 20);
						victim.playSound(victim.getLocation(), XSound.UI_BUTTON_CLICK.parseSound(), 1, 1);
						time--;
						
					} else {
						
						if (Config.getB("Arena.ClearInventoryOnRespawn")) {
							victim.getInventory().clear();
							victim.getInventory().setArmorContents(null);
						}
						
						arena.addPlayer(victim);
						
						victim.sendMessage(Config.getS("Death.Title.Message"));
						victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
						victim.playSound(victim.getLocation(), XSound.ENTITY_EXPERIENCE_ORB_PICKUP.parseSound(), 1, 1);
						
						Toolkit.runCommands("Respawn", victim);
						
						cancel();
						
					}
					
				}
				
			}.runTaskTimer(Game.getInstance(), 0L, 20L);
			
		} else {
			
			arena.removePlayer(victim);
			
			if (Config.getB("Arena.ClearInventoryOnRespawn")) {
				victim.getInventory().clear();
				victim.getInventory().setArmorContents(null);
			}
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(Game.getInstance(), () -> arena.addPlayer(victim), 1);
			
			Toolkit.runCommands("Respawn", victim);
			
		}
		
	}

	private void setDeathMessage(Player victim, PlayerDeathEvent e) {

		if (victim.getLastDamageCause() == null && victim.getKiller() != null) {
			creditWithKill(victim, victim.getKiller());
			return;
		}

		DamageCause cause = victim.getLastDamageCause().getCause();

		if (cause == DamageCause.PROJECTILE) {

			broadcast(victim.getWorld(), Config.getS("Death.Messages.Shot").replace("%victim%", victim.getName()).replace("%shooter%", getShooter(victim.getLastDamageCause()).getName()));
			creditWithKill(victim, victim.getKiller());

//		} else if (cause == DamageCause.ENTITY_ATTACK) {
//
//			broadcast(victim.getWorld(), Config.getS("Death.Messages.Player").replace("%victim%", victim.getName()).replace("%killer%", victim.getKiller().getName()));
//			creditWithKill(victim, victim.getKiller());

		} else if (victim.getKiller() != null) {

			broadcast(victim.getWorld(), Config.getS("Death.Messages.Player").replace("%victim%", victim.getName()).replace("%killer%", victim.getKiller().getName()));
			creditWithKill(victim, victim.getKiller());

		} else if (arena.getHitCache().get(victim.getName()) != null) {

			String killerName = arena.getHitCache().get(victim.getName());
			broadcast(victim.getWorld(), Config.getS("Death.Messages.Player").replace("%victim%", victim.getName()).replace("%killer%", killerName));
			creditWithKill(victim, getPlayer(victim, killerName));

		} else if (cause == DamageCause.VOID) {

			broadcast(victim.getWorld(), Config.getS("Death.Messages.Void").replace("%victim%", victim.getName()));

		} else if (cause == DamageCause.FALL) {
			
			broadcast(victim.getWorld(), Config.getS("Death.Messages.Fall").replace("%victim%", victim.getName()));
			
		} else if (cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK || cause == DamageCause.LAVA) {
			
			broadcast(victim.getWorld(), Config.getS("Death.Messages.Fire").replace("%victim%", victim.getName()));
			
		} else if (cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_EXPLOSION) {
			
			broadcast(victim.getWorld(), Config.getS("Death.Messages.Explosion").replace("%victim%", victim.getName()));
			
		} else {
			
			broadcast(victim.getWorld(), Config.getS("Death.Messages.Unknown").replace("%victim%", victim.getName()));
			
		}
		
	}

	private Entity getShooter(EntityDamageEvent e) {

		EntityDamageByEntityEvent shotEvent = (EntityDamageByEntityEvent) e;

		Projectile damager = (Projectile) shotEvent.getDamager();
		Entity shooter = (Entity) damager.getShooter();

		return shooter;

	}

	private void creditWithKill(Player victim, Player killer) {

		if (victim.getName() != killer.getName()) {

			int payout = r.nextInt(PAYOUT_GAP) + MIN_PAYOUT;
			arena.getStats().addKill(killer.getUniqueId());
			arena.getLevels().addExperience(killer, resources.getLevels().getInt("Levels.General.Experience.Kill"));
			econ.depositPlayer(killer, payout);
			killer.sendMessage(resources.getMessages().getString("Messages.Other.Payout").replace("%amount%", String.valueOf(payout)));

			arena.getStats().addKill(killer.getUniqueId());
			arena.getLevels().addExperience(killer, resources.getLevels().getInt("Levels.General.Experience.Kill"));

			Toolkit.runKillCommands(victim, killer);

			if (resources.getScoreboard().getBoolean("Scoreboard.General.Enabled")) {

				new BukkitRunnable() {

					@Override
					public void run() {

						if (killer instanceof Player) {

							arena.updateScoreboards(killer, false);

						}

					}

				}.runTaskLater(Game.getInstance(), 20L);

			}

		}

	}
	
	private void broadcast(World world, String message) {
		
		if (Config.getB("Death.Messages.Enabled")) {
			
			for (Player all : world.getPlayers()) {
				
				all.sendMessage(message);
				
			}
			
		}
		
	}
	
	private void broadcast(World world, Sound sound, int volume, int pitch) {
		
		if (Config.getB("Death.Sound.Enabled")) {
			
			for (Player all : world.getPlayers()) {
				
				all.playSound(all.getLocation(), XSound.matchXSound(sound.toString()).get().parseSound(), volume, pitch);
				
			}
			
		}
		
	}

	private Player getPlayer(Player origin, String name) {

		for (Player player : origin.getWorld().getPlayers()) {

			if (player.getName().equals(name)) {

				return player;

			}

		}

		return null;

	}

}
