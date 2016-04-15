package me.maker56.survivalgames.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.Util;
import me.maker56.survivalgames.arena.Arena;
import me.maker56.survivalgames.commands.messages.MessageHandler;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.game.GameState;
import me.maker56.survivalgames.user.UserManager;

public class DomeListener implements Listener {
	
	private static boolean registered = false;
	
	public static boolean isRegistered() {
		return registered;
	}
	
	public DomeListener() {
		Bukkit.getPluginManager().registerEvents(this, SurvivalGames.getInstance());
		registered = true;
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		Location from = event.getFrom();
		Location to = event.getTo();
		if(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
			Player p = event.getPlayer();
			UserManager um = SurvivalGames.userManger;
			if(um.isPlaying(p.getName())) {
				Game game = um.getUser(p.getName()).getGame();
				if(game.getState() == GameState.DEATHMATCH) {
					Arena arena = game.getCurrentArena();
					if(arena.isDomeEnabled()) {
						double distance = arena.domeDistance(p.getLocation());
						if(distance >= arena.getDomeRadius()) {
							p.spawnParticle(Particle.FLAME, p.getEyeLocation(), 16451);
							p.spawnParticle(Particle.FLAME, p.getEyeLocation(), 16452);
							p.spawnParticle(Particle.FLAME, p.getEyeLocation(), 16457);
							
							Vector v = Util.calculateVector(p.getLocation(), arena.getDomeMiddle());
							v.multiply(2);
							p.setVelocity(v);

							p.sendMessage(MessageHandler.getMessage("game-deathmatch-end-reached"));
							
							if(distance > arena.getDomeRadius() && distance - arena.getDomeRadius() > 8) {
								p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1, true));
							}
						}
					}
				}
			}
		}
	}

}
