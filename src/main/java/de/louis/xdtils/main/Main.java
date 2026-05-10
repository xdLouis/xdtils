package de.louis.xdtils.main;

import de.louis.xdtils.commands.*;
import de.louis.xdtils.listener.BackListener;
import de.louis.xdtils.listener.ChatListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();
        getLogger().info("xdtils wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        getLogger().info("xdtils wurde deaktiviert.");
    }

    private void registerCommands() {
        // Gamemode
        GamemodeCommand gm = new GamemodeCommand();
        registerCommand("gamemode", gm, gm);
        registerCommand("gm", gm, gm);

        // Speed
        SpeedCommand speedAuto = new SpeedCommand(SpeedCommand.SpeedType.AUTO);
        SpeedCommand speedWalk = new SpeedCommand(SpeedCommand.SpeedType.WALK);
        SpeedCommand speedFly  = new SpeedCommand(SpeedCommand.SpeedType.FLY);
        registerCommand("speed",     speedAuto, speedAuto);
        registerCommand("walkspeed", speedWalk, speedWalk);
        registerCommand("flyspeed",  speedFly,  speedFly);

        // Enchant
        EnchantCommand enchant = new EnchantCommand();
        registerCommand("enchant", enchant, enchant);

        // Hat
        registerCommand("hat", new HatCommand(), null);

        // Invsee
        InvseeCommand invsee = new InvseeCommand(this);
        registerCommand("invsee", invsee, invsee);

        // Back
        registerCommand("back", new BackCommand(), null);

        // Teleport
        TeleportCommand tp = new TeleportCommand();
        registerCommand("tp", tp, tp);

        // TpHere
        TpHereCommand tpHere = new TpHereCommand();
        registerCommand("tphere", tpHere, null);

        // Clear
        ClearCommand clear = new ClearCommand();
        registerCommand("clear", clear, clear);

        // Trash
        registerCommand("trash", new TrashCommand(this), null);

        // Item / Give
        ItemCommand item = new ItemCommand();
        registerCommand("i", item, item);
        GiveCommand give = new GiveCommand();
        registerCommand("give", give, give);

        // Kick / Ban / Pardon
        KickCommand kick = new KickCommand();
        registerCommand("kick", kick, kick);
        BanCommand ban = new BanCommand(false);
        registerCommand("ban", ban, ban);
        BanCommand banIp = new BanCommand(true);
        registerCommand("ban-ip", banIp, banIp);
        PardonCommand pardon = new PardonCommand(false);
        registerCommand("pardon", pardon, pardon);
        PardonCommand pardonIp = new PardonCommand(true);
        registerCommand("pardon-ip", pardonIp, pardonIp);

        // Op / Deop
        OpCommand op = new OpCommand(false);
        registerCommand("op", op, op);
        OpCommand deop = new OpCommand(true);
        registerCommand("deop", deop, deop);

        // Kill
        KillCommand kill = new KillCommand();
        registerCommand("kill", kill, kill);

        // Time / Weather / Difficulty
        TimeCommand time = new TimeCommand();
        registerCommand("time", time, time);
        WeatherCommand weather = new WeatherCommand();
        registerCommand("weather", weather, weather);
        DifficultyCommand difficulty = new DifficultyCommand();
        registerCommand("difficulty", difficulty, difficulty);

        // Whitelist
        WhitelistCommand whitelist = new WhitelistCommand();
        registerCommand("whitelist", whitelist, whitelist);

        // Heal / Feed / Fly
        HealCommand heal = new HealCommand();
        registerCommand("heal", heal, heal);
        FeedCommand feed = new FeedCommand();
        registerCommand("feed", feed, feed);
        FlyCommand fly = new FlyCommand();
        registerCommand("fly", fly, fly);

        // ClearChat
        registerCommand("cc", new ClearChatCommand(), null);

        // Workstations
        registerWorkstation("workbench",   "workbench");
        registerWorkstation("anvil",       "anvil");
        registerWorkstation("grindstone",  "grindstone");
        registerWorkstation("cartography", "cartography");
        registerWorkstation("loom",        "loom");
        registerWorkstation("stonecutter", "stonecutter");
        registerWorkstation("smithing",    "smithing");
        registerWorkstation("enchanting",  "enchanting");
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BackListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
    }

    private void registerCommand(String name,
                                 org.bukkit.command.CommandExecutor executor,
                                 org.bukkit.command.TabCompleter completer) {
        var cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(executor);
            if (completer != null) cmd.setTabCompleter(completer);
        }
    }

    private void registerWorkstation(String commandName, String type) {
        var cmd = getCommand(commandName);
        if (cmd != null) cmd.setExecutor(new WorkstationCommand(type));
    }
}