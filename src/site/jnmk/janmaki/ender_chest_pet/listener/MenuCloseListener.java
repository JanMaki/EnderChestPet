package site.jnmk.janmaki.ender_chest_pet.listener;

import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class MenuCloseListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event){
        if (event.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY+"ChestPet")){
            Player player = (Player) event.getPlayer();
            player.playSound(player.getLocation(),"minecraft:block.chest.close", SoundCategory.BLOCKS,1,0.9f);
        }
    }

}
