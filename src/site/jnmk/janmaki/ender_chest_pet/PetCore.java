package site.jnmk.janmaki.ender_chest_pet;

import net.minecraft.server.v1_14_R1.PacketPlayOutEntityDestroy;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;

public class PetCore {
    private EnderChestPet enderChestPet;
    private Map<Wolf,ArmorStand> chests = new HashMap<>();
    private Set<Wolf> collectMode = new HashSet<>();

    PetCore(EnderChestPet enderChestPet){
        this.enderChestPet = enderChestPet;
        load();

        new BukkitRunnable() {
            @Override
            public void run(){
                updateEntity();
            }
        }.runTaskTimer(enderChestPet,0,1);
        new BukkitRunnable() {
            @Override
            public void run(){
                updatePlayer();
            }
        }.runTaskTimerAsynchronously(enderChestPet,0,20);
        new BukkitRunnable() {
            @Override
            public void run(){
                updateDropItem();
            }
        }.runTaskTimer(enderChestPet,0,5);
    }

    public void spawn(EnderChestPet enderChestPet, Location location, Player owner){
        World world = location.getWorld();
        if (world == null){
            return;
        }
        Wolf wolf = (Wolf) world.spawnEntity(location,EntityType.WOLF);
        AttributeInstance instance = wolf.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
        if (instance != null)
            instance.setBaseValue(0.5);
        wolf.setSilent(true);
        wolf.setOwner(owner);
        wolf.setCustomName(("p:"+owner.getName()));
        wolf.setInvulnerable(true);
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(location,EntityType.ARMOR_STAND);
        armorStand.setCustomName("c:"+owner.getName());
        armorStand.setCustomNameVisible(false);
        String defaultBlock = enderChestPet.config.getString("DefaultViewItem",Material.ENDER_CHEST.toString());
        if (defaultBlock == null){
            defaultBlock =  Material.ENDER_CHEST.toString();
        }
        Material material = Material.matchMaterial(defaultBlock);
        if (material == null){
            material = Material.ENDER_CHEST;
        }
        armorStand.setHelmet(new ItemStack(material));
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        enderChestPet.core.addChest(wolf,armorStand);
        enderChestPet.core.updatePlayer();
    }

    public boolean isStay(Player player){
        Wolf wolf = getWolf(player);
        if (wolf != null) return wolf.isSitting();
        return false;
    }

    public void setStay(Player player,boolean bool){
        Wolf wolf = getWolf(player);
        if (wolf != null) {
            wolf.setSitting(bool);
            ArmorStand armorStand = chests.get(wolf);
            if (bool) {
                armorStand.setHeadPose(new EulerAngle(0,0,45));
                setCollectMode(player,false);
            }
            else {
                armorStand.setHeadPose(new EulerAngle(0,0,0));
            }
        }

    }

    public void setItem(Player player,ItemStack itemStack){
        Wolf wolf = getWolf(player);
        if (wolf != null) chests.get(wolf).setHelmet(itemStack);
    }

    public Material getItem(Player player){
        Wolf wolf = getWolf(player);
        if (wolf != null) return chests.get(wolf).getHelmet().getType();
        return Material.AIR;
    }

    public Location getLocation(Player player){
        Wolf wolf = getWolf(player);
        if (wolf == null){
            return null;
        }
        return wolf.getLocation();
    }

    public void remove(Player player){
        Wolf wolf = getWolf(player);
        if (wolf == null) {
            return;
        }
        ArmorStand armorStand = chests.get(wolf);
        chests.remove(wolf);
        wolf.remove();
        armorStand.remove();
    }

    public void setCollectMode(Player player,Boolean bool){
        Wolf wolf = getWolf(player);
        if (wolf == null){
            return;
        }
        if (bool){
            collectMode.add(wolf);
        }
        else {
            collectMode.remove(wolf);
        }
    }

    public boolean isCollectMode(Player player){
        Wolf wolf = getWolf(player);
        if (wolf != null){
            return collectMode.contains(wolf);
        }
        return false;
    }

    public Wolf getWolf(Player player){
        for (Wolf wolf:chests.keySet()){
            String name = wolf.getName();
            if (name.contains(player.getName())){
                return wolf;
            }
        }
        return null;
    }

    private void load(){
        for (World world: Bukkit.getWorlds()){
            for (Entity entity : world.getEntities()){
                if (!(entity instanceof ArmorStand)){
                    continue;
                }
                ArmorStand armorStand = (ArmorStand) entity;
                String asName = armorStand.getCustomName();
                if (asName == null || !asName.contains("c:")){
                    continue;
                }
                String pName = asName.replaceAll("c:","");
                LivingEntity result = null;
                for (LivingEntity livingEntity:world.getLivingEntities()){
                    if (!(livingEntity instanceof Wolf)) {
                        continue;
                    }
                    String wName = livingEntity.getName();
                    if (!wName.equalsIgnoreCase("p:"+pName)) {
                        continue;
                    }
                    result = livingEntity;
                    break;
                }
                if (result == null){
                    continue;
                }
                final Wolf wolf = (Wolf) result;
                addChest(wolf,armorStand);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location tpLocation = wolf.getLocation();
                        tpLocation.add(0,-0.75,0);
                        armorStand.teleport(tpLocation);
                    }
                }.runTaskTimer(enderChestPet,1,1);
            }
        }
    }

    private void addChest(Wolf wolf,ArmorStand armorStand){
        chests.put(wolf,armorStand);
    }

    private void updateEntity(){
        for (Wolf wolf:chests.keySet()){
            Location tpLocation = wolf.getLocation();
            tpLocation.add(0,-0.75,0);
            ArmorStand armorStand = chests.get(wolf);
            armorStand.teleport(tpLocation);
            // (*'ω'*) Secret Function !!
            if (!chests.get(wolf).getHelmet().getType().toString().contains("SWORD"))
                wolf.setTarget(null);
        }
    }

    private void updatePlayer(){
        for (Wolf wolf:chests.keySet()){
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wolf.getEntityId());
            for (Player player:Bukkit.getOnlinePlayers()){
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
        for (Wolf wolf:chests.keySet()){
            Location location = wolf.getLocation();
            location.add(0,1,0);
            World world = location.getWorld();
            if (world == null){
                continue;
            }
            for (Entity entity:location.getWorld().getNearbyEntities(location,5,5,5)){
                if (!(entity instanceof Player)){
                    continue;
                }
                Player player = (Player) entity;
                player.spawnParticle(Particle.END_ROD,location,1,0,0,0,0);
            }
        }
    }

    private void updateDropItem(){
        for (Wolf wolf:collectMode) {
            String name = wolf.getName();
            name = name.replaceAll("p:","");
            Player player = Bukkit.getPlayer(name);
            if (player == null){
                continue;
            }
            Location location = wolf.getLocation();
            World world = location.getWorld();
            if (world == null) {
                continue;
            }
            if (!wolf.isSitting()){
                Collection<Entity> entities = world.getNearbyEntities(location, 5, 1, 5);
                for (Entity entity : entities) {
                    if (!(entity instanceof Item)) {
                        continue;
                    }
                    Vector vector = entity.getLocation().toVector().subtract(location.toVector()).normalize();
                    vector.multiply(0.4);
                    vector.add(new Vector(0,0.1,0));
                    wolf.setVelocity(vector);
                    break;
                }
            }
            Collection<Entity> entities2 = world.getNearbyEntities(location, 1, 1, 1);
            for (Entity entity : entities2) {
                if (!(entity instanceof Item)) {
                    continue;
                }
                Item item = (Item) entity;
                ItemStack itemStack = item.getItemStack();
                Inventory enderChest = player.getEnderChest();
                if (enderChest.firstEmpty() != -1){
                    world.playSound(location,"minecraft:entity.item.pickup", SoundCategory.BLOCKS,1,1.2f);
                    enderChest.addItem(itemStack);
                    item.remove();
                }
            }
        }
    }
}
