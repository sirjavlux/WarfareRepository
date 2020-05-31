package com.coding.sirjavlux.bossbar;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.coding.sirjavlux.core.Main;

public class BossbarListener implements Listener {

	@EventHandler
	public void joinEvent(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		Main.getInstance().getBossbarManager().addPlayerBar(p);
	}
	
	@EventHandler
	public void quitEvent(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Main.getInstance().getBossbarManager().removePlayerBar(p);
	}
}
