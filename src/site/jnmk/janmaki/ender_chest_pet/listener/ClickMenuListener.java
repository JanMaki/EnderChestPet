package site.jnmk.janmaki.ender_chest_pet.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import site.jnmk.janmaki.ender_chest_pet.EnderChestPet;


public class ClickMenuListener implements Listener {
    private EnderChestPet enderChestPet;

    public ClickMenuListener(EnderChestPet enderChestPet){
        this.enderChestPet = enderChestPet;
    }

    @EventHandler
    public void onClickMenu(InventoryClickEvent event){
        if (!event.getView().getTitle().equalsIgnoreCase(ChatColor.DARK_GRAY+"ChestPet")) {
            return;
        }
        if (event.getRawSlot() >= 27){
            return;
        }
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        if (item==null) {
            return;
        }
        Inventory inv = event.getInventory();
        if (event.getRawSlot() != 15) {
            switch (item.getType()) {
                case RED_BED:
                    enderChestPet.core.setStay(player, true);
                    reloadInv(inv,player);
                    break;
                case DIAMOND_BOOTS:
                    enderChestPet.core.setStay(player, false);
                    reloadInv(inv,player);
                    break;
                case COBWEB:
                    if (!enderChestPet.core.isStay(player)) enderChestPet.core.setCollectMode(player,!enderChestPet.core.isCollectMode(player));
                    reloadInv(inv,player);
                    break;
                case TNT:
                    Location loc = enderChestPet.core.getLocation(player);
                    if (loc.getWorld() == null) {
                        break;
                    }
                    player.playSound(loc, "minecraft:item.shield.block", SoundCategory.BLOCKS, 2f, 1.7f);
                    enderChestPet.core.remove(player);
                    player.closeInventory();
                    new BukkitRunnable(){
                        @Override
                        public void run(){
                            loc.getWorld().dropItem(loc, new ItemStack(Material.ENDER_CHEST));
                            loc.getWorld().dropItem(loc, new ItemStack(Material.ARMOR_STAND));
                        }
                    }.runTaskLater(enderChestPet,6);
            }
        }
        else {
            ItemStack holdItem = event.getCursor();
            if (holdItem != null && holdItem.getType() != Material.AIR){
                enderChestPet.core.setItem(player,holdItem);
                ItemStack itemStack = new ItemStack(holdItem.getType());
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta != null)
                    itemMeta.setDisplayName("ChangeView");
                itemStack.setItemMeta(itemMeta);
                event.getInventory().setItem(event.getRawSlot(),itemStack);
            }
        }
        player.updateInventory();
    }


    private void reloadInv(Inventory inv,Player player){
        Material[] materials = {Material.GRAY_STAINED_GLASS_PANE,Material.YELLOW_STAINED_GLASS_PANE};
        ItemStack[] items = new ItemStack[2];
        for (int i = 0; i < materials.length ; i++){
            ItemStack item = new ItemStack(materials[i]);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta != null)
                itemMeta.setDisplayName(" ");
            item.setItemMeta(itemMeta);
            items[i] = item;
        }
        inv.setItem(0,items[0]);
        inv.setItem(2,items[0]);
        inv.setItem(4,items[0]);
        inv.setItem(18,items[0]);
        inv.setItem(20,items[0]);
        inv.setItem(22,items[0]);
        boolean isStay = enderChestPet.core.isStay(player);
        if (isStay){
            inv.setItem(0,items[1]);
            inv.setItem(18,items[1]);
        }
        else {
            inv.setItem(2,items[1]);
            inv.setItem(20,items[1]);
        }
        boolean isCollectMode = enderChestPet.core.isCollectMode(player);
        if (isCollectMode){
            inv.setItem(4,items[1]);
            inv.setItem(22,items[1]);
        }
        player.updateInventory();
    }
}
