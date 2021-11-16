package com.gmail.aoibotowner.gunstudy;

import net.minecraft.server.v1_16_R3.PacketPlayOutEntityDestroy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.HashSet;

public final class GunStudy extends JavaPlugin implements Listener {

    public static HashSet<Player> players = new HashSet<>();
    public static HashSet<Arrow> Arrows = new HashSet<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for (Arrow arrow : Arrows) {
                    arrow.getWorld().spawnParticle(Particle.ASH, arrow.getLocation(), 1);
                }
            }
        }, 0L, 1L);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();
        if (item == null) return;
        if (item.getType() == Material.STICK && players.contains(p)) {
            Vector v = p.getLocation().getDirection();
            Arrow arrow = p.getWorld().spawnArrow(p.getLocation().add(0, p.getEyeHeight(), 0), v, 10, 2);
            arrow.setGravity(false);
            Arrows.add(arrow);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(((CraftArrow) arrow).getHandle().getId()));
            p.sendMessage(ChatColor.BLUE + "Spawned arrow!");
        }
    }

    @EventHandler
    public void onDestroy(ProjectileHitEvent e) {
        if(e.getEntityType() != EntityType.ARROW) return;
        if(Arrows.contains((Arrow) e.getEntity())) {
            Arrows.remove((Arrow) e.getEntity());
            e.getEntity().remove();
            if(e.getHitBlock() != null) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        players.add(e.getPlayer());
    }
}
