package fr.crizelia.auth.listeners;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import fr.crizelia.auth.AuthPlugin;
import fr.crizelia.auth.utils.TitleUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.UUID;

public class AuthListener implements Listener {

    private AuthPlugin authPlugin;
    private TitleUtils titleUtils;
    public ArrayList<UUID> authlocked = new ArrayList<UUID>();

    public AuthListener(AuthPlugin authPlugin) { this.authPlugin = authPlugin; this.titleUtils = authPlugin.titleUtils; }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event)
    {
        final Player player = event.getPlayer();

        if(!authPlugin.getConfig().contains(player.getUniqueId().toString()))
        {
            TextComponent OUI = new TextComponent("§8[§6§l»§8] §aOui§7, je veux utiliser §6Google Authentificator§7.");
            OUI.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Clique §epour §aaccepter§e.").create()));
            OUI.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/2fa oui"));
            TextComponent NON = new TextComponent("§8[§6§l»§8] §cNon§7, je ne veux pas utiliser §6Google §6Authentificator§7.");
            NON.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Clique §epour §crefuser§e.").create()));
            NON.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/2fa non"));

            player.sendMessage("§6§l[2FA] §7Voulez vous utiliser Google Authentificator ? ");
            player.spigot().sendMessage(OUI);
            player.spigot().sendMessage(NON);
            /*
                TODO: faire un message JSON pour si il accepte ou pas le 2FA

             */
        }
        else
        {
            if(authPlugin.getConfig().getBoolean(player.getUniqueId()+".auth.boolean"))
            {
                this.authlocked.add(player.getUniqueId());
                titleUtils.sendFullTitle(player, 0, 200, 0, "§6§lGoogle Authentificator", "§7Veuillez ecrire le §bcode §7dans le chat.");
                player.sendMessage("§6§l[2FA] §cVous n'avez que §630 secondes§c pour vous identifier.");

                Bukkit.getScheduler().runTaskTimer(AuthPlugin.getInstance(), new BukkitRunnable() {
                    int timer = 30;
                    public void run()
                    {
                        if(timer != 0 && (authlocked.contains(player.getUniqueId())))
                        {
                            timer--;
                            titleUtils.sendActionBar(player, "§cTemps restant : §6"+timer+"seconde(s)");
                        }
                        else if(timer == 0 && (authlocked.contains(player.getUniqueId())))
                        {
                            player.kickPlayer("§6§l[2FA] §cTemps écoulé.");
                            authlocked.remove(player.getUniqueId());
                            this.cancel();
                        }
                        else if(!authlocked.contains(player.getUniqueId()))
                        {
                            this.cancel();
                        }
                    }
                }, 0L, 20L);

            }
        }


    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();
        String message = event.getMessage();

        if(this.authlocked.contains(player.getUniqueId()))
        {
            try{
                Integer code = Integer.parseInt(message);
                if(this.playerInputCode(player, code))
                {
                    this.authlocked.remove(player.getUniqueId());
                    player.sendMessage("§6§l[2FA] §aCode réussi avec succès.");
                }
                else
                {
                    player.sendMessage("§6§l[2FA] §cCode incorrecte/expiré, il ne peut contenir que des chiffres.");
                }
            }catch(Exception e)
            {
                player.sendMessage("§6§l[2FA] §cCode incorrecte/expiré, il ne peut contenir que des chiffres.");
            }


            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMove(PlayerMoveEvent event)
    {
        if(this.authlocked.contains(event.getPlayer().getUniqueId())){ event.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCommand(PlayerCommandPreprocessEvent event)
    {
        if(this.authlocked.contains(event.getPlayer().getUniqueId())){ event.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event)
    {
        if(this.authlocked.contains(event.getPlayer().getUniqueId())){ event.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event)
    {
        if(this.authlocked.contains(event.getPlayer().getUniqueId())){ event.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDrop(PlayerDropItemEvent event)
    {
        if(this.authlocked.contains(event.getPlayer().getUniqueId())){ event.setCancelled(true); }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player)
        {
            final Player player = (Player) event.getEntity();
            if(this.authlocked.contains(player.getUniqueId()))
            {
                event.setCancelled(true);
            }

        }
        return;
    }


    private boolean playerInputCode(Player player, int code)
    {
        String secretKey = authPlugin.getConfig().getString(player.getUniqueId()+".auth.code");
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean codeisvalid = gAuth.authorize(secretKey, code);

        if (codeisvalid)
        {
            authlocked.remove(player.getUniqueId());
            return codeisvalid;
        }
        return codeisvalid;
    }


}
