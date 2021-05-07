package fr.crizelia.auth;

import fr.crizelia.auth.managers.AuthManager;
import fr.crizelia.auth.utils.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class AuthPlugin extends JavaPlugin
{
    private static AuthPlugin instance;
    private AuthManager authManager;
    public TitleUtils titleUtils = new TitleUtils();

    @Override
    public void onEnable()
    {
        instance = this;
        this.sendLogger("§aPlugin has enabled.");
        this.sendLogger("Version running : §a"+this.getDescription().getVersion());
        this.sendLogger("Created by : §dJus2§5Raisin");
        this.authManager = new AuthManager(this);
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
    }

    @Override
    public void onDisable()
    {
        this.sendLogger("§cPlugin has disabled.");
        this.sendLogger("Version running : §a"+this.getDescription().getVersion());
        this.sendLogger("Created by : §dJus2§5Raisin");
    }

    public void sendLogger(String message) { Bukkit.getConsoleSender().sendMessage("§6§l[2FA] §7- §e"+message); }

    public AuthManager getAuthManager() { return authManager; }

    public static AuthPlugin getInstance() { return instance; }
}
