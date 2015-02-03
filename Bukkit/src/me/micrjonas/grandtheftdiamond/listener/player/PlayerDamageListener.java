package me.micrjonas.grandtheftdiamond.listener.player;

import java.util.concurrent.TimeUnit;

import me.micrjonas.grandtheftdiamond.GrandTheftDiamond;
import me.micrjonas.grandtheftdiamond.Team;
import me.micrjonas.grandtheftdiamond.api.event.cause.JailReason;
import me.micrjonas.grandtheftdiamond.api.event.cause.WantedLevelChangeCause;
import me.micrjonas.grandtheftdiamond.api.event.player.PlayerDeathInGameEvent;
import me.micrjonas.grandtheftdiamond.data.FileManager;
import me.micrjonas.grandtheftdiamond.data.PluginData;
import me.micrjonas.grandtheftdiamond.data.PluginFile;
import me.micrjonas.grandtheftdiamond.data.TemporaryPluginData;
import me.micrjonas.grandtheftdiamond.gang.GangManager;
import me.micrjonas.grandtheftdiamond.jail.JailManager;
import me.micrjonas.grandtheftdiamond.manager.EconomyManager;
import me.micrjonas.grandtheftdiamond.messenger.Messenger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerDamageListener implements Listener{
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		final Player p = e.getEntity();
		if (TemporaryPluginData.getInstance().isIngame(p)) {
			try {
				p.spigot().respawn();	
			}	
			catch (NoSuchMethodError ex) { }
		}
		
	}
	
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (((Damageable) p).getHealth() - e.getDamage() <= 0) {
				e.setCancelled(true);
				PlayerDeathInGameEvent e2 = new PlayerDeathInGameEvent(p, e.getCause(), PluginData.getInstance().getPlayersRespawnLocation(p));
				Bukkit.getPluginManager().callEvent(e2);
				respawn(p, e2.getRespawnLocation(), true);
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (!TemporaryPluginData.getInstance().isIngame(p)) {
				return;	
			}
			Player dmgP;
			if (e.getDamager() instanceof Player) {
				dmgP = (Player) e.getDamager();
			}
			else if (e.getDamager() instanceof Projectile) {
				Projectile pr = (Projectile) e.getDamager();
				if (pr.getShooter() instanceof Player) {
					dmgP = (Player) pr.getShooter();
				}
				else {
					return;
				}
			}
			else {
				return;
			}	
			if (TemporaryPluginData.getInstance().isIngame(dmgP)) {
				if (PluginData.getInstance().getTeam(p) == Team.COP && PluginData.getInstance().getTeam(dmgP) == Team.COP && FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getBoolean("damage.disableCopDamageByCop")) {	
					e.setCancelled(true);
					Messenger.getInstance().sendPluginMessage(p, "cannotHitCopsAsCop");
					return;
				}	
				else if (PluginData.getInstance().getTeam(p) == Team.CIVILIAN && PluginData.getInstance().getWantedLevel(p) < 1 && PluginData.getInstance().getTeam(dmgP) == Team.COP && FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getBoolean("damage.disableNotGangsterDamageByCop")) {	
					e.setCancelled(true);
					Messenger.getInstance().sendPluginMessage(p, "cannotHitNonGangstersAsCop");
					return;	
				}
						
				else if (!GangManager.getInstance().canAttack(p, dmgP)) {
					e.setCancelled(true);
					Messenger.getInstance().sendPluginMessage(p, "gang.noPvP");
					return;
				}
			}
			if (((Damageable) p).getHealth() - e.getDamage() <= 0) {
				e.setCancelled(true);
				PlayerDeathInGameEvent e2 = new PlayerDeathInGameEvent(p, e.getCause(), p.getLocation(), e.getDamager());
				Bukkit.getPluginManager().callEvent(e2);
				respawn(p, e2.getRespawnLocation(), true);
				if (e.getDamager() instanceof Player) {	
					manageMoneyAndExpKilledOther((Player) e.getDamager(), p);
					manageMoneyAndExpKilled(p, (Player) e.getDamager());	
					playerKilledPlayer(p, (Player) e.getDamager());
				}
				else {
					manageMoneyAndExpDied(p);
				}
			}			
		}
	}
	
	private void playerKilledPlayer(Player p, Player killer) {
		if (TemporaryPluginData.getInstance().isIngame(p) && TemporaryPluginData.getInstance().isIngame(killer)) {
			Team teamP = PluginData.getInstance().getTeam(p);
			Team teamKiller = PluginData.getInstance().getTeam(killer);
			int wantedLevel = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("kill." + teamKiller.name().toLowerCase() + "Killed." + teamP.name().toLowerCase() + ".wantedLevel");
			int experience = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("kill." + teamKiller.name().toLowerCase() + "Killed." + teamP.name().toLowerCase() + ".experience");
			int money = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("kill." + teamKiller.name().toLowerCase() + "Killed." + teamP.name().toLowerCase() + ".money");
			PluginData.getInstance().setWantedLevel(killer, PluginData.getInstance().getWantedLevel(killer) + wantedLevel);
			PluginData.getInstance().addExp(killer, experience);
			EconomyManager.getInstance().deposit(killer, money, true);
			if (teamKiller == Team.COP && FileManager.getInstance().getFileConfiguration(PluginFile.CONFIG).getBoolean("banSystem.copAutoban.use")) {
				if (teamP == Team.CIVILIAN && 
						PluginData.getInstance().getWantedLevel(p) < 1 && 
						FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getBoolean("kill.copKilled.jailCopAfterKillingNonGangster")) {
					JailManager.getInstance().jailPlayer(p, JailReason.COP_KILLED_CIVILIAN);
				}
			}
		}
	}

	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent e){
		final Player p = e.getPlayer();
		if (TemporaryPluginData.getInstance().isIngame(p)) {	
			Location loc = p.getLocation();
			e.setRespawnLocation(PluginData.getInstance().getPlayersSpawn(p));
			respawn(p, loc, false);
		}
	}
	
	
	private void respawn(final Player p, Location loc, boolean teleport) {
		p.getInventory().clear();
        p.getInventory().setHelmet(new ItemStack(Material.AIR));
        p.getInventory().setChestplate(new ItemStack(Material.AIR));
        p.getInventory().setLeggings(new ItemStack(Material.AIR));
        p.getInventory().setBoots(new ItemStack(Material.AIR));
        if (teleport) {
        	TemporaryPluginData.getInstance().setIngame(p, false);
			p.teleport(PluginData.getInstance().getPlayersSpawn(p));
			TemporaryPluginData.getInstance().setIngame(p, true);
        }
        PluginData.getInstance().deathCountOneUp(p);
		PluginData.getInstance().setWantedLevel(p, 0, WantedLevelChangeCause.DEATH);
		GrandTheftDiamond.scheduleSyncDelayedTask(new Runnable() {
			@Override
			public void run() {
				p.setFireTicks(0);
				p.setHealth(p.getHealthScale());
				p.setFoodLevel(20);
				p.setExp(0);
				p.setLevel(0);
			}
		}, 0.5, TimeUnit.SECONDS);
	}
	
	
	private void manageMoneyAndExpKilled(Player p, Player killer) {
		int money = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("death.killed.money");
		int exp = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("death.killed.experience");
		String lostOrGotMoney = "";
		String lostOrGotExp = "";
		String moneyString = String.valueOf(money);
		String expString = String.valueOf(exp);
		if (money < 0) {
			lostOrGotMoney = Messenger.getInstance().getPluginWord("lost");
			moneyString = String.valueOf(money*-1);
		}
		else {
			lostOrGotMoney = Messenger.getInstance().getPluginWord("got");
		}
		if (exp < 0) {
			lostOrGotExp = Messenger.getInstance().getPluginWord("lost");
			expString = String.valueOf(exp*-1);
		}
		else {
			lostOrGotExp = Messenger.getInstance().getPluginWord("got");
		}
		Messenger.getInstance().sendPluginMessage(p, "getKilled", new Player[]{killer},new String[]{"%lostOrGotMoney%",
				"%lostOrGotExp%",
				"%amountMoney%",
				"%amountExp%"}, new String[]{
				lostOrGotMoney,
				lostOrGotExp,
				moneyString,
				expString}
		);
	}
	
	
	private void manageMoneyAndExpKilledOther(Player killer, Player p) {
		int money = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("kill." + PluginData.getInstance().getTeam(p).name().toLowerCase() + "Killed." + PluginData.getInstance().getTeam(p).name().toLowerCase() + ".money");
		int exp = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("kill." + PluginData.getInstance().getTeam(p).name().toLowerCase() + "Killed." + PluginData.getInstance().getTeam(p).name().toLowerCase() + ".experience");
		String lostOrGotMoney = "";
		String lostOrGotExp = "";
		String moneyString = String.valueOf(money);
		String expString = String.valueOf(exp);
		if (money < 0) {
			lostOrGotMoney = Messenger.getInstance().getPluginWord("lost");
			moneyString = String.valueOf(money*-1);
		}
		else {
			lostOrGotMoney = Messenger.getInstance().getPluginWord("got");
		}
		if (exp < 0) {
			lostOrGotExp = Messenger.getInstance().getPluginWord("lost");
			expString = String.valueOf(exp*-1);
		}
		
		else {
			lostOrGotExp = Messenger.getInstance().getPluginWord("got");
		}
		Messenger.getInstance().sendPluginMessage(killer, "killedOther", new Player[]{p}, new String[]{"%lostOrGotMoney%",
				"%lostOrGotExp%", 
				"%amountMoney%",
				"%amountExp%"}, new String[]{
				lostOrGotMoney,
				lostOrGotExp,
				moneyString,
				expString}
		);
	}
	
	
	private void manageMoneyAndExpDied(Player p) {
		int money = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("death.died.money");
		int exp = FileManager.getInstance().getFileConfiguration(PluginFile.EVENT_CONFIG).getInt("death.died.experience");
		String lostOrGotMoney = "";
		String lostOrGotExp = "";
		String moneyString = String.valueOf(money);
		String expString = String.valueOf(exp);
		if (money < 0) {
			lostOrGotMoney = Messenger.getInstance().getPluginWord("lost");
			moneyString = String.valueOf(money*-1);
		}
		else {
			lostOrGotMoney = Messenger.getInstance().getPluginWord("got");
		}
		if (exp < 0) {
			lostOrGotExp = Messenger.getInstance().getPluginWord("lost");
			expString = String.valueOf(exp*-1);
		}
		else {
			lostOrGotExp = Messenger.getInstance().getPluginWord("got");
		}
		Messenger.getInstance().sendPluginMessage(p, "died", new String[]{"%lostOrGotMoney%",
				"%lostOrGotExp%",
				"%amountMoney%",
				"%amountExp%"}, new String[]{
				lostOrGotMoney,
				lostOrGotExp,
				moneyString,
				expString}
		);
	}
	
}
