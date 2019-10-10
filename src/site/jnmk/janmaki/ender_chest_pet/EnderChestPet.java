package site.jnmk.janmaki.ender_chest_pet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import site.jnmk.janmaki.ender_chest_pet.listener.*;

public class EnderChestPet extends JavaPlugin {

    public FileConfiguration config;
    public PetCore core;

    @Override
    public void onEnable(){
        saveDefaultConfig();
        config = getConfig();
        core = new PetCore(this);
        PluginManager plManager = Bukkit.getPluginManager();
        plManager.registerEvents(new ClickEnderChestListener(this),this);
        plManager.registerEvents(new Open_ClosePetChestListener(),this);
        plManager.registerEvents(new PunchPetListener(this),this);
        plManager.registerEvents(new ClickMenuListener(this),this);
        plManager.registerEvents(new MenuCloseListener(),this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage(ChatColor.RED+"Player only command.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            return false;
        }
        if (args[0].equalsIgnoreCase("call")){
            if (player.hasPermission("ecp.call") || player.hasPermission("ecp.*")) {
                Wolf wolf = core.getWolf(player);
                if (wolf == null) player.sendMessage(ChatColor.RED+"You don't have a pet.");
                else wolf.teleport(player);
            }
            else
                player.sendMessage(ChatColor.RED+"Insufficient perm. [ ecp.call ]");
            return true;
        }
        else if (args[0].equalsIgnoreCase("reload")){
            if (player.hasPermission("ecp.reload") || player.hasPermission("ecp.*")) {
                reloadConfig();
                config = getConfig();
                player.sendMessage(ChatColor.GREEN+"Reload config file!");
            }
            else
                player.sendMessage(ChatColor.RED+"Insufficient perm. [ ecp.reload ]");
            return true;
        }
        return false;
    }
}
