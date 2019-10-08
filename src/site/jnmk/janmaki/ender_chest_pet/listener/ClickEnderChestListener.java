package site.jnmk.janmaki.ender_chest_pet.listener;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import site.jnmk.janmaki.ender_chest_pet.EnderChestPet;

public class ClickEnderChestListener implements Listener {
    private EnderChestPet enderChestPet;

    public ClickEnderChestListener(EnderChestPet enderChestPet){
        this.enderChestPet = enderChestPet;
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND){
            return;
        }
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.ENDER_CHEST){
            return;
        }
        Player player = event.getPlayer();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        String requestItem = enderChestPet.config.getString("RequestItem",Material.ARMOR_STAND.toString());
        if (requestItem == null){
            requestItem = Material.ARMOR_STAND.toString();
        }
        Material material = Material.matchMaterial(requestItem);
        if (material == null){
            material = Material.ARMOR_STAND;
        }
        if (itemStack.getType() != material) {
            return;
        }
        event.setCancelled(true);
        if (enderChestPet.core.getWolf(player) != null){
            player.sendMessage(ChatColor.RED+"You already have a pet!");
            return;
        }
        block.setType(Material.AIR);
        if (player.getGameMode() != GameMode.CREATIVE){
            itemStack.setAmount(itemStack.getAmount()-1);
            player.getInventory().setItemInMainHand(itemStack);
            player.updateInventory();
        }
        Location location = block.getLocation();
        location.add(0.5,0,0.5);
        enderChestPet.core.spawn(enderChestPet,location,player);
    }
}
