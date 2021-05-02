package io.github.eylexlive.leaderboards;

import io.github.eylexlive.leaderboards.command.LeaderboardCommand;
import io.github.eylexlive.leaderboards.command.LeaderboardTabCompleter;
import io.github.eylexlive.leaderboards.database.Database;
import io.github.eylexlive.leaderboards.database.MYSQLDatabase;
import io.github.eylexlive.leaderboards.database.SQLiteDatabase;
import io.github.eylexlive.leaderboards.leaderboard.LeaderboardManager;
import io.github.eylexlive.leaderboards.listener.ConnectionListener;
import io.github.eylexlive.leaderboards.listener.InventoryListener;
import io.github.eylexlive.leaderboards.util.License;
import io.github.eylexlive.leaderboards.util.Placeholders;
import io.github.eylexlive.leaderboards.util.config.ConfigUtil;
import io.github.eylexlive.leaderboards.util.license.ipadd.IPAdress;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;

public final class uLeaderboards extends JavaPlugin {

    private static uLeaderboards instance;

    private Database database;

    private LeaderboardManager leaderboardManager;

    private boolean hdEnabled;
    private boolean citizensEnabled;

    @Override
    public void onEnable() {
        if (instance != null) {
            throw new IllegalStateException("uLeaderboards cannot be started twice!");
        }

        instance = this;

        saveDefaultConfig();

        if (!this.getName().equalsIgnoreCase("uLeaderboards") || this.getName().length() != 13) {
            return;
        }

        if (check("io.github.eylexlive.leaderboards.util.License","a") || check("io.github.eylexlive.leaderboards.util.License", "o") || !check()) {
            return;
        }

        if (check("io.github.eylexlive.leaderboards.util.license.License", "k") || check("io.github.eylexlive.leaderboards.util.license.utils.Utils", "o") ||  check("io.github.eylexlive.leaderboards.util.license.ipadd.IPAdress", "m")) {
            return;
        }

    //    final License license = new License(ConfigUtil.getString("license"));
        final License license = new License("uLeaderboardsActive");

        if (!license.a() || license.o() == null || !license.o().equals("§I§I§§§I§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§§I§§§§§§§§§§§§§§§§§§§§I§§§§§§§§§§§§§§§§§I"))
            return;

        if (!this.access()) {
            return;
        }

        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));
        getCommand("leaderboard").setTabCompleter(new LeaderboardTabCompleter(this));

        Arrays.asList(
                new InventoryListener(),
                new ConnectionListener()
        ).forEach(
                listener -> getServer().getPluginManager().registerEvents(listener, this)
        );

        leaderboardManager = new LeaderboardManager(this);

        hdEnabled = getServer().getPluginManager().isPluginEnabled("HolographicDisplays");
        citizensEnabled = getServer().getPluginManager().isPluginEnabled("Citizens");

        final boolean sqlEnabled = isMYSQLEnabled();
        if (sqlEnabled) {
            if (!hcpValid()) {
                Arrays.asList("slf4j-api", "HikariCP").forEach(str -> {
                    final File file = new File(getDataFolder().getAbsolutePath() + File.separator + "lib" + File.separator + str.toLowerCase() + ".jar");
                    if (!file.exists())
                        throw new IllegalStateException("[e] " + str + " library not found in directory '" + file.getAbsolutePath() + "'");
                    loadLib(file, str);
                });
            } else {
                getLogger().info("[l] HikariCP library found on another plugin!");
            }
        }
        database = (sqlEnabled ? new MYSQLDatabase() : new SQLiteDatabase()).init();

        final Placeholders placeholders = new Placeholders(this);
        placeholders.register();

        leaderboardManager.setupBoardTask();

        getServer().getScheduler().runTaskTimerAsynchronously(this, () -> getServer().getOnlinePlayers().forEach(player -> leaderboardManager.updatePlayer(player)), 600L, 1200L);
    }

    @Override
    public void onDisable() {
        if (leaderboardManager != null)
            leaderboardManager.saveBoards();
        if (database != null)
            database.close();
        saveConfig();
    }

    private boolean hcpValid() {
        boolean valid = true;
        try {
            Class.forName("com.zaxxer.hikari.HikariDataSource");
        } catch (ClassNotFoundException e) {
            valid = false;
        }
        return valid;
    }

    private synchronized void loadLib(File jar, String str) {
        getLogger().info("[l] Loading " + str + "...");
        try {
            final URL url = jar.toURI().toURL();
            final Method method = java.net.URLClassLoader.class.getDeclaredMethod("addURL", java.net.URL.class);
            method.setAccessible(true);
            method.invoke(Thread.currentThread().getContextClassLoader(), url);
        } catch (Exception ex) {
            throw new RuntimeException("[e] Cannot load library from jar file '" + jar.getAbsolutePath() + "'. R: " + ex.getMessage());
        }
        getLogger().info("[l] " + str + " successfully loaded!");
    }

    protected boolean check(String clazz, String method) {
        try {
            Class.forName(clazz).getMethod(method, (Class<?>[]) null) ;
        } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            return true;
        }
        return false;
    }

    private boolean access() {
        if (io.github.eylexlive.leaderboards.util.license.License.k().length() != 23 || !IPAdress.m()) {
            Bukkit.getScheduler().cancelTasks(this);
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }

        if (!io.github.eylexlive.leaderboards.util.license.License.has(this.getName())) {
            Bukkit.getLogger().info("--------------------------------------");
            Bukkit.getLogger().info("                                      ");
            Bukkit.getLogger().info("           uLeaderboards           ");
            Bukkit.getLogger().info("          Lisans gecersiz!       ");
            Bukkit.getLogger().info("                                      ");
            Bukkit.getLogger().info("--------------------------------------");

            Bukkit.getScheduler().cancelTasks(this);
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        } else {
            Bukkit.getLogger().info("--------------------------------------");
            Bukkit.getLogger().info("                                      ");
            Bukkit.getLogger().info("           uLeaderboards           ");
            Bukkit.getLogger().info("          Lisans gecerli!       ");
            Bukkit.getLogger().info("                                      ");
            Bukkit.getLogger().info("--------------------------------------");
        }
        return true;
    }

    protected boolean check() {
        try {
            Class.forName("io.github.eylexlive.leaderboards.util.License");
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    public LeaderboardManager getLeaderboardManager() {
        if (leaderboardManager == null)
            throw new IllegalStateException("Manager is not available!");
        return leaderboardManager;
    }

    public Database getDataBase(){
        if (database == null)
            throw new IllegalStateException("Database is not available!");
        return database;
    }

    public static uLeaderboards getInstance() {
        if (instance == null)
            throw new IllegalStateException("uLeaderboards is not available!");
        return instance;
    }

    public boolean isMYSQLEnabled() {
        return ConfigUtil.getBoolean("mysql.enabled");
    }

    public boolean isHdEnabled() {
        return hdEnabled;
    }

    public boolean isCitizensEnabled() {
        return citizensEnabled;
    }
}
