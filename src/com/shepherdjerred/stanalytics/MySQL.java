package com.shepherdjerred.stanalytics;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bukkit.Bukkit;

public class MySQL {

    // Provide Instances
    private static MySQL instance;

    public MySQL() {
	instance = this;
    }

    public static MySQL getInstance() {
	if (instance == null) {
	    instance = new MySQL();
	}
	return instance;
    }

    // MySQL variables
    Connection connection;
    Statement statement;

    // MySQL openConnection
    public void openConnection() throws SQLException, ClassNotFoundException {
	if (connection != null && !connection.isClosed()) {
	    return;
	}
	connection = DriverManager.getConnection("jdbc:mysql://" + Main.getInstance().host + ":" + Main.getInstance().port + "/" + Main.getInstance().database, Main.getInstance().username, Main.getInstance().password);
	Class.forName("com.mysql.jdbc.Driver");
    }

    // MySQL runUpdate
    public void runUpdate(String input) {

	Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {

		try {
		    openConnection();
		    statement = connection.createStatement();
		    statement.executeUpdate(input);
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	    }
	});

    }

    // MySQL check if tables exist
    public void checkTables(BooleanConsumer<Boolean> consumer) {

	Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {

		try {
		    openConnection();
		    statement = connection.createStatement();

		    DatabaseMetaData dbm = connection.getMetaData();
		    boolean playersExists = false;
		    boolean periodicExists = false;
		    boolean dailyExists = false;
		    boolean allExist = false;

		    // Check if the players table exists
		    ResultSet result = dbm.getTables(null, null, "players", null);
		    if (result.next()) {
			playersExists = true;
		    }

		    // Check if the daily table exists
		    result = dbm.getTables(null, null, "daily", null);
		    if (result.next()) {
			dailyExists = true;
		    }

		    // Check if the periodic table exists
		    result = dbm.getTables(null, null, "periodic", null);
		    if (result.next()) {
			periodicExists = true;
		    }

		    // Check if all tables exist
		    if (playersExists && periodicExists && dailyExists) {
			allExist = true;
		    }

		    // Return allExist to the consumer
		    if (consumer != null) {
			consumer.accept(allExist);
		    }

		    result.close();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	    }
	});

    }

    // MySQL check if player exists in database on certain date
    public void checkPlayerOnDate(BooleanConsumer<Boolean> consumer, String uuid) {

	Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {

		try {
		    openConnection();
		    statement = connection.createStatement();
		    boolean exists = false;

		    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		    Date date = new Date();

		    ResultSet result = statement.executeQuery("SELECT * from players WHERE uuid = '" + uuid + "' AND date ='" + dateFormat.format(date) + "';");

		    if (result.next()) {
			exists = true;
		    }

		    // Return exists to the consumer
		    if (consumer != null) {
			consumer.accept(exists);
		    }

		    result.close();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	    }
	});

    }

    // MySQL check if player exists in database
    public void checkPlayer(BooleanConsumer<Boolean> consumer, String uuid) {

	Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {

		try {
		    openConnection();
		    statement = connection.createStatement();
		    boolean exists = false;

		    ResultSet result = statement.executeQuery("SELECT * from players WHERE uuid = '" + uuid + "';");

		    result.last();
		    int size = result.getRow();
		    result.beforeFirst();

		    if (size > 1) {
			exists = false;
		    }

		    // Return exists to the consumer
		    if (consumer != null) {
			consumer.accept(exists);
		    }

		    result.close();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	    }
	});

    }

    // MySQL get value of unique players
    public void getUniquePlayers(IntegerConsumer<Integer> consumer, String date) {

	Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {

		try {
		    openConnection();
		    statement = connection.createStatement();
		    Integer count = 0;

		    ResultSet result = statement.executeQuery("SELECT * from daily WHERE date = '" + date + "';");

		    if (result.next()) {
			count = result.getInt("uniqueplayers");
		    }

		    // Return count to the consumer
		    if (consumer != null) {
			consumer.accept(count);
		    }

		    result.close();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	    }
	});

    }

    // MySQL get value of new players
    public void getNewPlayers(IntegerConsumer<Integer> consumer, String date) {

	Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.getInstance(), new Runnable() {
	    @Override
	    public void run() {

		try {
		    openConnection();
		    statement = connection.createStatement();
		    Integer count = 0;

		    ResultSet result = statement.executeQuery("SELECT * from daily WHERE date = '" + date + "';");

		    if (result.next()) {
			count = result.getInt("new");
		    }

		    // Return count to the consumer
		    if (consumer != null) {
			consumer.accept(count);
		    }

		    result.close();
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		} catch (SQLException e) {
		    e.printStackTrace();
		}

	    }
	});

    }

    // MySQL BooleanConsumer
    @SuppressWarnings("hiding")
    public interface BooleanConsumer<Boolean> {
	public void accept(boolean result);
    }

    // MySQL IntegerConsumer
    @SuppressWarnings("hiding")
    public interface IntegerConsumer<Integer> {
	public void accept(Integer result);
    }

    // MySQL ArrayListConsumer
    @SuppressWarnings("hiding")
    public interface ArrayListConsumer<ArrayList> {
	public void accept(ArrayList result);
    }
}