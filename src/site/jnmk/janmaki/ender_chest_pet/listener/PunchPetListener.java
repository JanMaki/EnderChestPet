package site.jnmk.janmaki.ender_chest_pet.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import site.jnmk.janmaki.ender_chest_pet.EnderChestPet;

public class PunchPetListener implements Listener {
    private EnderChestPet enderChestPet;

    public PunchPetListener(EnderChestPet enderChestPet){
        this.enderChestPet = enderChestPet;
    }

    @EventHandler
    public void onPunch(EntityDamageByEntityEvent event){
        Entity victim = event.getEntity();
        if (!(victim instanceof ArmorStand)){
            return;
        }
        ArmorStand armorStand = (ArmorStand) victim;
        Entity attacker = event.getDamager();
        if (!(attacker instanceof Player)) {
            return;
        }
        Player player = (Player)attacker;
        String asName = armorStand.getName();
        if (!asName.contains("c:")){
            return;
        }
        event.setCancelled(true);
        if (!asName.contains(player.getName())){
            player.sendMessage(ChatColor.RED+"This is another player's pet.");
            return;
        }
        Inventory inv = Bukkit.createInventory(player,27, ChatColor.DARK_GRAY+"ChestPet");

        Material[] materials = {Material.GRAY_STAINED_GLASS_PANE,Material.YELLOW_STAINED_GLASS_PANE,
        Material.RED_BED,Material.DIAMOND_BOOTS,Material.COBWEB, enderChestPet.core.getItem(player),Material.TNT};
        String[] names = {" "," ",ChatColor.BOLD+"StayMode",ChatColor.BOLD+"FollowMode",
        ChatColor.BOLD+"CorrectItemMode",ChatColor.BOLD+"ChangeView",ChatColor.BOLD+"Destroy"};
        ItemStack[] items = new ItemStack[7];
        for (int i = 0;i < materials.length;i++){
            Material material = materials[i];
            ItemStack itemStack = new ItemStack(material,1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null){
                continue;
            }
            itemMeta.setDisplayName(names[i]);
            itemStack.setItemMeta(itemMeta);
            items[i] = itemStack;
        }
        for (int i = 0;i < 27;i++){
            ItemStack itemStack = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE,1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta == null){
                continue;
            }
            itemMeta.setDisplayName(" ");
            itemStack.setItemMeta(itemMeta);
            inv.setItem(i,itemStack);
        }
        inv.setItem(0,items[0]);
        inv.setItem(2,items[0]);
        inv.setItem(4,items[0]);
        inv.setItem(9,items[2]);
        inv.setItem(11,items[3]);
        inv.setItem(13,items[4]);
        inv.setItem(15,items[5]);
        inv.setItem(17,items[6]);
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
        player.openInventory(inv);
        player.playSound(player.getLocation(),"minecraft:block.chest.open", SoundCategory.BLOCKS,1,0.9f);
    }
}
