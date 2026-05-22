package de.louis.xdtils.main;

import de.louis.xdtils.commands.*;
import de.louis.xdtils.listener.BackListener;
import de.louis.xdtils.listener.ChatListener;
import de.louis.xdtils.listener.VanishListener;
import de.louis.xdtils.manager.ConfigManager;
import de.louis.xdtils.manager.VanishManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;
    private VanishManager vanishManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.vanishManager = new VanishManager(this);
        registerCommands();
        registerListeners();
        getLogger().info("xdtils wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        vanishManager.save();
        getLogger().info("xdtils wurde deaktiviert.");
    }

    private void registerCommands() {
        // Gamemode
        GamemodeCommand gm = new GamemodeCommand();
        registerCommand("gamemode", gm, gm, "xdtils.gamemode");
        registerCommand("gm",       gm, gm, "xdtils.gamemode");

        // Speed
        SpeedCommand speedAuto = new SpeedCommand(SpeedCommand.SpeedType.AUTO);
        SpeedCommand speedWalk = new SpeedCommand(SpeedCommand.SpeedType.WALK);
        SpeedCommand speedFly  = new SpeedCommand(SpeedCommand.SpeedType.FLY);
        registerCommand("speed",     speedAuto, speedAuto, "xdtils.speed");
        registerCommand("walkspeed", speedWalk, speedWalk, "xdtils.walkspeed");
        registerCommand("flyspeed",  speedFly,  speedFly,  "xdtils.flyspeed");

        // Enchant
        EnchantCommand enchant = new EnchantCommand();
        registerCommand("enchant", enchant, enchant, "xdtils.enchant");

        // Hat
        registerCommand("hat", new HatCommand(), null, "xdtils.hat");

        // Invsee
        InvseeCommand invsee = new InvseeCommand(this);
        registerCommand("invsee", invsee, invsee, "xdtils.invsee");

        // Back
        registerCommand("back", new BackCommand(), null, "xdtils.back");

        // Teleport
        TeleportCommand tp = new TeleportCommand();
        registerCommand("tp", tp, tp, "xdtils.tp");
        registerCommand("tphere", new TpHereCommand(), null, "xdtils.tphere");

        // Clear / Trash
        ClearCommand clear = new ClearCommand();
        registerCommand("clear", clear, clear, "xdtils.clear");
        registerCommand("trash", new TrashCommand(this), null, "xdtils.trash");

        // Item / Give
        ItemCommand item = new ItemCommand();
        registerCommand("i", item, item, "xdtils.item");
        GiveCommand give = new GiveCommand();
        registerCommand("give", give, give, "xdtils.give");

        // Kick / Ban / Pardon
        KickCommand kick = new KickCommand();
        registerCommand("kick", kick, kick, "xdtils.kick");
        BanCommand ban = new BanCommand(false);
        registerCommand("ban", ban, ban, "xdtils.ban");
        BanCommand banIp = new BanCommand(true);
        registerCommand("ban-ip", banIp, banIp, "xdtils.ban.ip");
        PardonCommand pardon = new PardonCommand(false);
        registerCommand("pardon", pardon, pardon, "xdtils.pardon");
        PardonCommand pardonIp = new PardonCommand(true);
        registerCommand("pardon-ip", pardonIp, pardonIp, "xdtils.pardon.ip");

        // Op / Deop
        registerCommand("op",   new OpCommand(false), new OpCommand(false), "xdtils.op");
        registerCommand("deop", new OpCommand(true),  new OpCommand(true),  "xdtils.deop");

        // Kill
        KillCommand kill = new KillCommand();
        registerCommand("kill", kill, kill, "xdtils.kill");

        // Time / Weather / Difficulty
        TimeCommand time = new TimeCommand();
        registerCommand("time", time, time, "xdtils.time");
        WeatherCommand weather = new WeatherCommand();
        registerCommand("weather", weather, weather, "xdtils.weather");
        DifficultyCommand difficulty = new DifficultyCommand();
        registerCommand("difficulty", difficulty, difficulty, "xdtils.difficulty");

        // Whitelist
        WhitelistCommand whitelist = new WhitelistCommand();
        registerCommand("whitelist", whitelist, whitelist, "xdtils.whitelist");

        // Heal / Feed / Fly
        HealCommand heal = new HealCommand();
        registerCommand("heal", heal, heal, "xdtils.heal");
        FeedCommand feed = new FeedCommand();
        registerCommand("feed", feed, feed, "xdtils.feed");
        FlyCommand fly = new FlyCommand();
        registerCommand("fly", fly, fly, "xdtils.fly");

        // ClearChat
        registerCommand("cc", new ClearChatCommand(), null, "xdtils.clearchat");

        // Vanish
        VanishCommand vanish = new VanishCommand(vanishManager);
        registerCommand("vanish", vanish, vanish, "xdtils.vanish");
        registerCommand("v", vanish, vanish, "xdtils.vanish");

        // Workstations
        registerWorkstation("workbench",   "workbench",   "xdtils.workbench");
        registerWorkstation("anvil",       "anvil",       "xdtils.anvil");
        registerWorkstation("grindstone",  "grindstone",  "xdtils.grindstone");
        registerWorkstation("cartography", "cartography", "xdtils.cartography");
        registerWorkstation("loom",        "loom",        "xdtils.loom");
        registerWorkstation("stonecutter", "stonecutter", "xdtils.stonecutter");
        registerWorkstation("smithing",    "smithing",    "xdtils.smithing");
        registerWorkstation("enchanting",  "enchanting",  "xdtils.enchanting");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BackListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new VanishListener(vanishManager), this);
    }

    private void registerCommand(String name, CommandExecutor executor,
                                 TabCompleter completer, String defaultPermission) {
        if (!configManager.isCommandEnabled(name)) {
            getLogger().info("[xdtils] Command /" + name + " ist deaktiviert.");
            return;
        }
        var cmd = getCommand(name);
        if (cmd == null) return;
        cmd.setExecutor(executor);
        if (completer != null) cmd.setTabCompleter(completer);
        String permission = configManager.getCommandPermission(name, defaultPermission);
        cmd.setPermission(permission);
    }

    private void registerWorkstation(String commandName, String type, String defaultPermission) {
        if (!configManager.isCommandEnabled(commandName)) {
            getLogger().info("[xdtils] Command /" + commandName + " ist deaktiviert.");
            return;
        }
        var cmd = getCommand(commandName);
        if (cmd == null) return;
        cmd.setExecutor(new WorkstationCommand(type));
        String permission = configManager.getCommandPermission(commandName, defaultPermission);
        cmd.setPermission(permission);
    }
}