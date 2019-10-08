package site.jnmk.janmaki.ender_chest_pet.listener;

import org.bukkit.SoundCategory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashSet;
import java.util.Set;

public class Open_CloseChestListener implements Listener {

    private Set<Player> set = new HashSet<>();

    @EventHandler
    public void onOpen(PlayerInteractAtEntityEvent event){
        Entity entity = event.getRightClicked();
        if (!(entity instanceof  ArmorStand) && !(entity instanceof Wolf)){
            return;
        }
        String name = entity.getName();
        if (!name.contains("c:") && !name.contains("p:")){
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        player.openInventory(player.getEnderChest());
        player.playSound(player.getLocation(),"minecraft:block.ender_chest.open", SoundCategory.BLOCKS,1,1);
        set.add(player);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        if (set.contains(player)){
            player.playSound(player.getLocation(),"minecraft:block.ender_chest.close", SoundCategory.BLOCKS,1,1);
            set.remove(player);
        }
    }
}
