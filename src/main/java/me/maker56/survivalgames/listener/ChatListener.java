package me.maker56.survivalgames.listener;

import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import me.maker56.survivalgames.SurvivalGames;
import me.maker56.survivalgames.game.Game;
import me.maker56.survivalgames.user.UserManager;
import me.maker56.survivalgames.user.UserState;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public class ChatListener implements Listener {

	private UserManager um = SurvivalGames.getUserManager();
	private static boolean chat;
	private static String design, specPrefix;
	
	public ChatListener() {
		reinitializeConfig();
	}
	
	public static void reinitializeConfig() {
		FileConfiguration config = SurvivalGames.instance.getConfig();
		chat = config.getBoolean("Chat.Enabled");
		design = ChatColor.translateAlternateColorCodes('&', config.getString("Chat.Design"));
		specPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("Chat.Spectator-State"));
	}

	
	// SEPARATED CHAT
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if(chat && !event.isCancelled()) {
			UserState u = um.getUser(event.getPlayer());
			
			if(u == null)
				u = um.getSpectator(event.getPlayer());
			
			if(u != null) {
				String format = design;
				String[] formats = getFormats(u.getPlayer());
				format = format.replace("{STATE}", u.isSpectator() ? specPrefix : "");
				format = format.replace("{PREFIX}", formats[0]);
				format = format.replace("{PLAYERNAME}", u.getName());
				format = format.replace("{SUFFIX}", formats[1]);
				format = format.replace("{MESSAGE}", event.getMessage());
				
				System.out.println(ChatColor.stripColor(format));
				
				BaseComponent[] bc = new ComponentBuilder(format)
				.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to show " + u.getName() + (u.getName().toLowerCase().endsWith("s") ? "" : "'s") + " statistics").create()))
				.event(new ClickEvent(Action.RUN_COMMAND, "/sg stats " + u.getName())).create();
	
				event.setCancelled(true);
				Game g = u.getGame();
				
				if(u.isSpectator()) {
					g.sendSpectators(bc);
				} else {
					g.sendMessage(bc);
				}
			} else {
				for(Iterator<Player> i = event.getRecipients().iterator(); i.hasNext();) {
					Player p = i.next();
					if(um.isPlaying(p.getName()) || um.isSpectator(p.getName()))
						i.remove();
				}
			}
			
		}

	}
	
	public String[] getFormats(Player p) {
		if(p.isOp())
			return new String[] { "§c", "§7> §r" };	
		return new String[] { "§a", "§7> §r" };	
	}

}
