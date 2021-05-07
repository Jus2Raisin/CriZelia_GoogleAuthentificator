package fr.crizelia.auth.commands;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import fr.crizelia.auth.AuthPlugin;
import fr.crizelia.auth.utils.TitleUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class AuthCommands implements CommandExecutor
{

    private AuthPlugin authPlugin;
    private TitleUtils titleUtils;

    public AuthCommands(AuthPlugin authPlugin)
    {
        this.authPlugin = authPlugin;
        this.titleUtils = authPlugin.titleUtils;
    }

    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings)
    {

        final Player player = (Player) commandSender;

        if (!authPlugin.getConfig().contains(player.getUniqueId().toString()))
        {

            if (strings.length == 1)
            {
                if (strings[0].equalsIgnoreCase("oui"))
                {
                    GoogleAuthenticator gAuth = new GoogleAuthenticator();
                    GoogleAuthenticatorKey key = gAuth.createCredentials();
                    TextComponent messageKey = new TextComponent("§6§l[2FA] §eVoici votre code : §7(Clique sur le message) \n§b"+key.getKey());
                    messageKey.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("§6Clique §epour copier").create()));
                    messageKey.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, key.getKey()));
                    player.spigot().sendMessage(messageKey);
                    authPlugin.getConfig().set(player.getUniqueId() + ".auth.boolean", true);
                    authPlugin.getConfig().set(player.getUniqueId() + ".auth.code", key.getKey());
                    authPlugin.saveConfig();
                    titleUtils.sendFullTitle(player, 0, 60, 0, "§6Google Authentificator", "§cVous utilisez à present §6§l2FA§7.");
                    return true;
                }

                else if (strings[0].equalsIgnoreCase("non"))
                {
                    authPlugin.getConfig().set(player.getUniqueId() + ".auth.boolean", false);
                    authPlugin.saveConfig();
                    return true;
                }

                else if (strings[0].equalsIgnoreCase("change"))
                {
                    player.sendMessage("§6§l[2FA] §cActuellement indisponible.");
                    return true;
                }

                else
                {
                    player.sendMessage("§6§l[2FA] §cErreur de syntaxe, §a/2FA <oui/non>");
                    return false;
                }


            }
            else
            {
                player.sendMessage("§6§l[2FA] §cErreur de syntaxe, §a/2FA <oui/non>");
                return false;
            }
        }
        else
        {
            player.sendMessage("§6§l[2FA] §cVous avez déjà choisis. §7(/2fa change)");
            return false;
        }
    }
}
