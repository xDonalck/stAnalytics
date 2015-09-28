package com.shepherdjerred.stanalytics;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import com.shepherdjerred.stanalytics.MySQL.BooleanConsumer;
import com.shepherdjerred.stanalytics.commands.MainCommand;
import com.shepherdjerred.stanalytics.listeners.JoinEvent;

public class Main extends JavaPlugin {

    // Provide instance of Main class
    private static Main instance;

    public Main() {
	instance = this;
    }

    public static Main getInstance() {
	return instance;
    }

    // Create MySQL variables
    String host, port, database, username, password;

    public void onEnable() {
	this.saveDefaultConfig();

	// Set MySQL variables to config values
	host = getConfig().getString("mysql.hostname");
	port = getConfig().getString("mysql.port");
	database = getConfig().getString("mysql.database");
	username = getConfig().getString("mysql.username");
	password = getConfig().getString("mysql.password");

	// Connect to the MySQL database
	try {
	    MySQL.getInstance().openConnection();
	    MySQL.getInstance().statement = MySQL.getInstance().connection.createStatement();
	    getLogger().info("Connection to MySQL database was successful!");
	    
	    // Check that the tables exist and the database has been initialized
	    BooleanConsumer<Boolean> checkTables = new BooleanConsumer<Boolean>() {
		public void accept(boolean result) {
		    if (!(result)) { //Create the tables if they don't exist
			Bukkit.broadcastMessage("Some tables don't exist! Initializing database now.");
			MySQL.getInstance().runUpdate("CREATE TABLE players(username VARCHAR(16), uuid CHAR(36), date DATE)");
			MySQL.getInstance().runUpdate("CREATE TABLE daily(date DATE, uniqueplayers SMALLINT(6), new SMALLINT(6), UNIQUE (date))");
			MySQL.getInstance().runUpdate("CREATE TABLE periodic(date DATE, time TIME, count SMALLINT(6), list TEXT)");
		    } else {
			getLogger().info("All tables are present in the database.");
		    }
		}
	    };
	    
	    MySQL.getInstance().checkTables(checkTables);
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    getLogger().info("Connection to MySQL database failed!");
	    getLogger().info("Disabling stAnalytics due to MySQL error");
	    Bukkit.getPluginManager().disablePlugin(this);
	} catch (SQLException e) {
	    e.printStackTrace();
	    getLogger().info("Connection to MySQL database failed!");
	    getLogger().info("Disabling stAnalytics due to MySQL error");
	    Bukkit.getPluginManager().disablePlugin(this);
	}

	// Register events
	getServer().getPluginManager().registerEvents(new JoinEvent(), this);

	// Register commands
	this.getCommand("sta").setExecutor(new MainCommand());
	
	// Setup variables for announcers
	BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
	DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
	
	// Send unique player count every 5 minutes
	    scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
		@Override
		public void run() {
	            Date date = new Date();
		    
		    List<String> playerArray = new ArrayList<String>();
		    int count = 0;
		    
		    for (Player p : Bukkit.getOnlinePlayers()) {
			count++;
			playerArray.add(p.getName());
		    }
		    
		    String playerString = String.join(", ", playerArray);
		    
		    MySQL.getInstance().runUpdate("INSERT INTO periodic VALUES ('" + dateFormat.format(date) + "','" + timeFormat.format(date) + "','" + count + "','" + playerString + "') ");
		}
	    }, getConfig().getInt("interval") * 60 * 20, getConfig().getInt("interval") * 60 * 20);
	}

    // Method for getting strings from the config with color codes
    public String getConfigString(String input) {
	return ChatColor.translateAlternateColorCodes('&', Main.getInstance().getConfig().getString(input));
    }
}
