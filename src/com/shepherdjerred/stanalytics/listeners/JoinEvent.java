package com.shepherdjerred.stanalytics.listeners;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.shepherdjerred.stanalytics.Main;
import com.shepherdjerred.stanalytics.MySQL;
import com.shepherdjerred.stanalytics.MySQL.BooleanConsumer;

public class JoinEvent implements Listener  {
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

	    BooleanConsumer<Boolean> ifPlayerExists = new BooleanConsumer<Boolean>() {
		public void accept(boolean result) {
		    if (!(result)) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date date = new Date();
			
			Main.getInstance().getLogger().info("Adding " + event.getPlayer().getName()+ " to unique players");
			MySQL.getInstance().runUpdate("INSERT INTO dailyuniqueplayers VALUES ('" + event.getPlayer().getName() + "','" + event.getPlayer().getUniqueId() + "','" + dateFormat.format(date) + "')");
		    }
		}
	    };
	    
	    // Run the table checking
	    MySQL.getInstance().checkPlayer(ifPlayerExists, event.getPlayer().getUniqueId().toString());
    }
}
