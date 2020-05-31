package com.coding.sirjavlux.bossbar;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.coding.sirjavlux.consumables.WaterBarManager;
import com.coding.sirjavlux.core.Main;
import com.coding.sirjavlux.health.HealthEffects;

public class BossbarManager {
	
	private HashMap<Player, BarInstance> bars;
	
	public BossbarManager() {
		bars = new HashMap<>();
		startBarUpdater();
	}
	
	private BossBar createBar() {
		return Bukkit.createBossBar("set text", BarColor.BLUE, BarStyle.SEGMENTED_10);
	}
	
	public void addPlayerBar(Player p) {
		if (!bars.containsKey(p)) {
			BossBar bar = createBar();
			bar.addPlayer(p);
			bars.put(p, new BarInstance(bar));
		}
	}
	
	public void removePlayerBar(Player p) {
		if (bars.containsKey(p)) bars.remove(p);
	}
	
	public HashMap<Player, BarInstance> getBars() {
		return new HashMap<>(bars);
	}
	
	private void startBarUpdater() {
		int runnableSpeed = 20;
		new BukkitRunnable() {
			@Override
			public void run() {
				for (Entry<Player, BarInstance> entry : getBars().entrySet()) {
					Player p = entry.getKey();
					if (!p.isOnline()) {
						removePlayerBar(p);
					} else {
						UUID uuid = p.getUniqueId();
						BarInstance instance = entry.getValue();
						BossBar bar = instance.getBar();
						//if bar progress is not empty
						if (bar.getProgress() > 0.09) {
							double newProg = bar.getProgress() - 0.1 < 0 ? 0 : bar.getProgress() - 0.1;
							bar.setProgress(newProg);
						}
						//if bar progress is empty
						else {
							Message emergancyMessage = null;
							for (int i = 0; i < Message.values().length; i++) {
								Message testMessage = instance.getNextMessage();
								switch (testMessage) {
								case Bleeding: if (HealthEffects.isBleeding(uuid) && instance.prevMess != testMessage) {
									emergancyMessage = testMessage;
									instance.prevMess = testMessage;
								}
									break;
								case BrokenLeg: if (HealthEffects.isBrokenLeg(uuid) && instance.prevMess != testMessage) {
									emergancyMessage = testMessage;
									instance.prevMess = testMessage;
								}
									break;
								case Concussion: if (HealthEffects.isConcussion(uuid) && instance.prevMess != testMessage) {
									emergancyMessage = testMessage;
									instance.prevMess = testMessage;
								}
									break;
								case Hunger: if (p.getFoodLevel() < 4 && instance.prevMess != testMessage) {
									emergancyMessage = testMessage;
									instance.prevMess = testMessage;
								}
									break;
								case Thirst: if (WaterBarManager.getPlayerWater(p) < 5d && instance.prevMess != testMessage) {
									emergancyMessage = testMessage;
									instance.prevMess = testMessage;
								}
									break;
								}
								if (emergancyMessage != null) break;
							}
							//if emargancy message
							if (emergancyMessage != null) {
								bar.setColor(BarColor.RED);
								switch (emergancyMessage) {
								case Bleeding: bar.setTitle(ChatColor.RED + "You are bleeding, use a bandage to stop the bleeding!");
									break;
								case BrokenLeg: bar.setTitle(ChatColor.GRAY + "You have a broken leg, use a splint to fix it!");
									break;
								case Concussion: bar.setTitle(ChatColor.YELLOW + "You have a concussion, use some aspirin!");
									break;
								case Hunger: bar.setTitle(ChatColor.DARK_PURPLE + "You are low on food!");
									break;
								case Thirst: bar.setTitle(ChatColor.AQUA + "You are low on water!");
									break;
								}
							}
							//if not emergency
							else {
								bar.setColor(BarColor.BLUE);
								bar.setTitle("set text");
								instance.prevMess = null;
							}
							bar.setProgress(1);
						}
					}
				}
			}
		}.runTaskTimerAsynchronously(Main.getPlugin(Main.class), runnableSpeed, runnableSpeed);
	}
	
	public class BarInstance {
		
		private BossBar bar;
		private int messageNr = 0;
		public Message prevMess;
		
		public BarInstance(BossBar bar) {
			this.bar = bar;
		}
		
		public BossBar getBar() { return bar; }
		
		public Message getNextMessage() {
			messageNr = messageNr + 1 >= Message.values().length ? 0 : messageNr + 1;
			return Message.values()[messageNr];
		}

	}
	
	private enum Message {
		Bleeding,
		BrokenLeg,
		Concussion,
		Thirst,
		Hunger
	}
}
