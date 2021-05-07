package fr.crizelia.auth.managers;

import fr.crizelia.auth.AuthPlugin;
import fr.crizelia.auth.commands.AuthCommands;
import fr.crizelia.auth.listeners.AuthListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public class AuthManager {

    public AuthManager(AuthPlugin authPlugin)
    {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new AuthListener(authPlugin), authPlugin);
        authPlugin.getCommand("2fa").setExecutor(new AuthCommands(authPlugin));
    }
}
