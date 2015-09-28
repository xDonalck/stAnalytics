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
import com.shepherdjerred.stanalytics.MySQL.IntegerConsumer;

public class JoinEvent implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

	IntegerConsumer<Integer> incrementNewCount = new IntegerConsumer<Integer>() {
	    public void accept(Integer result) {
		result++;

		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date = new Date();

		MySQL.getInstance().runUpdate("UPDATE daily SET new ='" + result + "' WHERE date='" + dateFormat.format(date) + "'");
	    }
	};

	BooleanConsumer<Boolean> ifPlayerNew = new BooleanConsumer<Boolean>() {
	    public void accept(boolean result) {
		if (!(result)) {
		    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		    Date date = new Date();

		    MySQL.getInstance().getNewPlayers(incrementNewCount, dateFormat.format(date));
		}
	    }
	};

	IntegerConsumer<Integer> incrementUniqueCount = new IntegerConsumer<Integer>() {
	    public void accept(Integer result) {

		if (result > 0) {
		    result++;

		    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		    Date date = new Date();

		    MySQL.getInstance().runUpdate("UPDATE daily SET uniqueplayers ='" + result + "' WHERE date='" + dateFormat.format(date) + "'");
		} else {
		    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		    Date date = new Date();

		    MySQL.getInstance().runUpdate("INSERT INTO daily VALUES ('" + dateFormat.format(date) + "'," + 1 + "," + 0 + ")");
		}

		MySQL.getInstance().checkPlayer(ifPlayerNew, event.getPlayer().getUniqueId().toString());

	    }
	};

	BooleanConsumer<Boolean> ifPlayerExists = new BooleanConsumer<Boolean>() {
	    public void accept(boolean result) {
		if (!(result)) {
		    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		    Date date = new Date();

		    MySQL.getInstance().getUniquePlayers(incrementUniqueCount, dateFormat.format(date));

		    Main.getInstance().getLogger().info("Adding " + event.getPlayer().getName() + " to players");
		    MySQL.getInstance().runUpdate("INSERT INTO players VALUES ('" + event.getPlayer().getName() + "','" + event.getPlayer().getUniqueId() + "','" + dateFormat.format(date) + "')");
		}
	    }
	};

	// Run the table checking
	MySQL.getInstance().checkPlayerOnDate(ifPlayerExists, event.getPlayer().getUniqueId().toString());
    }
}
