package com.example.deOPP;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DeOPP extends JavaPlugin
        implements Listener, CommandExecutor
{
    private boolean advanced;
    private boolean onLoad;
    private List<String> commands = new ArrayList<>();

    @Override
    public void onEnable()
    {
        if (!new File(getDataFolder() + File.separator + "config.yml").exists())
            saveDefaultConfig();
        loadCfg();

        if (onLoad) checkAll();

        Bukkit.getPluginManager().registerEvents(this, this);

    }

    private void loadCfg()
    {
        FileConfiguration cfg = getConfig();
        commands = cfg.getStringList("commands");
        advanced = cfg.getBoolean("advanced");
        onLoad = cfg.getBoolean("onLoad");
    }

    private void checkAll()
    {
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers())
        {
            if (isOp(offlinePlayer)) deOp(offlinePlayer);
        }
    }

    private boolean isOp (OfflinePlayer p)
    {
        if (p.isOp()) return true;
        if (advanced)
        {
            String perm = UUID.randomUUID().toString();
            Player player = p.getPlayer();
            if (player == null) return false;
            return player.hasPermission(perm);
        }
        return false;
    }

    private void deOp(OfflinePlayer p)
    {
        String nick = p.getName();
        ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

        for (String command : commands)
        {
            if (command.equals("deop"))
            {
                p.setOp(false);
                continue;
            }

            if (command.contains("log"))
            {
                log(command.replace("log ", "").replace("{player}", nick));
                continue;
            }

            if (commands.contains("execute"))
            {
                Bukkit.dispatchCommand(console, command.replace("execute ", "").replace("{player}", nick));
            }
        }
    }

    private void log (String str)
    {
        System.out.println(ChatColor.LIGHT_PURPLE + "[DeOP]" + ChatColor.RESET + str);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (sender instanceof Player) return false;
        if (args.length != 1) return false;
        loadCfg();
        log("reloaded");
        return true;

    }

    @EventHandler
    private void onExit (PlayerQuitEvent e)
    {
        Player p = e.getPlayer();
        if(isOp(p)) deOp(p);
        if (advanced)
        {
            String perm = UUID.randomUUID().toString();
            if (p.hasPermission(perm)) deOp(p);
        }
    }

    @Override
    public void onDisable()
    {
        if (onLoad) checkAll();

    }
}
