package com.articreep.holeinthewall;

import com.articreep.holeinthewall.environments.TheVoid;
import com.articreep.holeinthewall.multiplayer.Pregame;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class HoleInTheWall extends JavaPlugin implements CommandExecutor {
    private static HoleInTheWall instance = null;

    @Override
    public void onEnable() {
        instance = this;
        getCommand("holeinthewall").setExecutor(this);
        getServer().getPluginManager().registerEvents(new PlayingFieldManager(), this);
        getServer().getPluginManager().registerEvents(new TheVoid(), this);
        Bukkit.getLogger().info(ChatColor.BLUE + "HoleInTheWall has been enabled!");

        saveDefaultConfig();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            // todo temporary
            PlayingFieldManager.pregame = new Pregame(Bukkit.getWorld("flat"), 2, 30);
            PlayingFieldManager.parseConfig(getConfig());
        }, 1);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static HoleInTheWall getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                PlayingFieldManager.removeAllGames();
                PlayingFieldManager.parseConfig(getConfig());
                sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
                return true;
            } else if (args[0].equalsIgnoreCase("abort")) {
                // todo temporary
                if (PlayingFieldManager.game != null) {
                    PlayingFieldManager.game.stop();
                    PlayingFieldManager.game = null;
                    sender.sendMessage("Game aborted");
                } else {
                    sender.sendMessage("No game to abort");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("timer")) {
                if (PlayingFieldManager.pregame.isActive()) {
                    PlayingFieldManager.pregame.cancelCountdown();
                    sender.sendMessage("Timer cancelled");
                } else {
                    PlayingFieldManager.pregame.startCountdown();
                    sender.sendMessage("Timer started");
                }
            } else if (args[0].equalsIgnoreCase("start")) {
                if (PlayingFieldManager.pregame.isActive()) {
                    PlayingFieldManager.pregame.startGame();
                    sender.sendMessage("Starting game");
                } else {
                    sender.sendMessage("Start a timer with /holeinthewall timer first");
                }

            } else {
                return false;
            }
        }
        return true;
    }
}
