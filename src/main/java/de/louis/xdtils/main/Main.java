package de.louis.xdtils.main;

import de.louis.xdtils.commands.*;
import de.louis.xdtils.listener.*;
import de.louis.xdtils.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private ConfigManager configManager;
    private VanishManager vanishManager;
    private SpyManager spyManager;
    private MsgManager msgManager;
    private AfkManager afkManager;
    private FreezeManager freezeManager;
    private MaintenanceManager maintenanceManager;
    private MuteManager muteManager;
    private TrailManager trailManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager       = new ConfigManager(this);
        this.vanishManager       = new VanishManager(this);
        this.spyManager          = new SpyManager();
        this.msgManager          = new MsgManager(spyManager);
        this.afkManager          = new AfkManager();
        this.freezeManager       = new FreezeManager();
        this.maintenanceManager  = new MaintenanceManager();
        this.muteManager         = new MuteManager();
        this.trailManager        = new TrailManager(this);

        if (getConfig().getBoolean("economy.enabled", true)) {
            this.economyManager = new EconomyManager(this);
            getLogger().info("Interne Economy wurde aktiviert.");
        } else {
            getLogger().info("Interne Economy ist deaktiviert.");
        }

        registerCommands();
        registerListeners();

        if (economyManager != null && getConfig().getBoolean("economy.vault-hook", true)) {
            if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
                getLogger().info("Vault wurde gefunden. Optionaler Economy-Hook kann initialisiert werden.");
            } else {
                getLogger().info("Vault wurde nicht gefunden.");
            }
        }

        getLogger().info("xdtils wurde aktiviert!");
    }

    @Override
    public void onDisable() {
        vanishManager.save();
        trailManager.shutdown();

        if (economyManager != null) {
            economyManager.save();
        }

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
        registerCommand("tp",      tp,                   tp,                   "xdtils.tp");
        registerCommand("tphere",  new TpHereCommand(),  null,                 "xdtils.tphere");
        registerCommand("tpworld", new TpWorldCommand(), new TpWorldCommand(), "xdtils.tpworld");

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
        OpCommand opCommand = new OpCommand(false);
        registerCommand("op", opCommand, opCommand, "xdtils.op");

        OpCommand deopCommand = new OpCommand(true);
        registerCommand("deop", deopCommand, deopCommand, "xdtils.deop");

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
        registerCommand("heal", new HealCommand(), new HealCommand(), "xdtils.heal");
        registerCommand("feed", new FeedCommand(), new FeedCommand(), "xdtils.feed");
        registerCommand("fly",  new FlyCommand(),  new FlyCommand(),  "xdtils.fly");

        // ClearChat
        registerCommand("cc", new ClearChatCommand(), null, "xdtils.clearchat");

        // Vanish
        VanishCommand vanish = new VanishCommand(vanishManager);
        registerCommand("vanish", vanish, vanish, "xdtils.vanish");
        registerCommand("v", vanish, vanish, "xdtils.vanish");

        // Info
        InfoCommand info = new InfoCommand();
        registerCommand("info", info, info, "xdtils.info");

        // Nick
        NickCommand nick = new NickCommand();
        registerCommand("nick", nick, nick, "xdtils.nick");

        // Msg / Reply / Ignore
        MsgCommand msg = new MsgCommand(msgManager);
        registerCommand("msg", msg, msg, "xdtils.msg");
        registerCommand("r", new ReplyCommand(msgManager), null, "xdtils.msg");
        IgnoreCommand ignore = new IgnoreCommand(msgManager);
        registerCommand("ignore", ignore, ignore, "xdtils.ignore");

        // AFK
        registerCommand("afk", new AfkCommand(afkManager), null, "xdtils.afk");

        // Seen / Playtime / Ping
        SeenCommand seen = new SeenCommand();
        registerCommand("seen", seen, seen, "xdtils.seen");
        PlaytimeCommand playtime = new PlaytimeCommand();
        registerCommand("playtime", playtime, playtime, "xdtils.playtime");
        PingCommand ping = new PingCommand();
        registerCommand("ping", ping, ping, "xdtils.ping");

        // Broadcast / Staff
        registerCommand("broadcast", new BroadcastCommand(), null, "xdtils.broadcast");
        registerCommand("staff", new StaffCommand(), null, "xdtils.staff");

        // Maintenance
        MaintenanceCommand maintenance = new MaintenanceCommand(maintenanceManager);
        registerCommand("maintenance", maintenance, maintenance, "xdtils.maintenance");

        // Freeze
        FreezeCommand freeze = new FreezeCommand(freezeManager);
        registerCommand("freeze", freeze, freeze, "xdtils.freeze");

        // Sudo
        SudoCommand sudo = new SudoCommand();
        registerCommand("sudo", sudo, sudo, "xdtils.sudo");

        // Rename / Lore / ItemDb
        registerCommand("rename", new RenameCommand(), null, "xdtils.rename");
        LoreCommand lore = new LoreCommand();
        registerCommand("lore", lore, lore, "xdtils.lore");
        registerCommand("itemdb", new ItemDbCommand(), null, "xdtils.itemdb");

        // CommandSpy / SocialSpy
        registerCommand("commandspy", new CommandSpyCommand(spyManager), null, "xdtils.commandspy");
        registerCommand("socialspy", new SocialSpyCommand(spyManager), null, "xdtils.socialspy");

        // Workstations
        registerWorkstation("workbench", "workbench", "xdtils.workbench");
        registerWorkstation("anvil", "anvil", "xdtils.anvil");
        registerWorkstation("grindstone", "grindstone", "xdtils.grindstone");
        registerWorkstation("cartography", "cartography", "xdtils.cartography");
        registerWorkstation("loom", "loom", "xdtils.loom");
        registerWorkstation("stonecutter", "stonecutter", "xdtils.stonecutter");
        registerWorkstation("smithing", "smithing", "xdtils.smithing");
        registerWorkstation("enchanting", "enchanting", "xdtils.enchanting");

        // Gameplay / Server / Items / Fun
        GodCommand god = new GodCommand();
        registerCommand("repair", new RepairCommand(), new RepairCommand(), "xdtils.repair");
        registerCommand("more", new MoreCommand(), null, "xdtils.more");
        registerCommand("restart", new RestartCommand(this), new RestartCommand(this), "xdtils.restart");
        registerCommand("tps", new TpsCommand(), null, "xdtils.tps");
        registerCommand("memory", new MemoryCommand(), null, "xdtils.memory");
        registerCommand("god", god, god, "xdtils.god");
        registerCommand("xp", new XpCommand(), new XpCommand(), "xdtils.xp");
        registerCommand("near", new NearCommand(), null, "xdtils.near");
        registerCommand("firework", new FireworkCommand(), null, "xdtils.firework");
        registerCommand("skull", new SkullCommand(), new SkullCommand(), "xdtils.skull");

        // Manager / Extra Commands
        InvSnapshotManager snapManager = new InvSnapshotManager();
        HistoryManager historyManager = new HistoryManager();

        registerCommand("invsave", new InvSaveCommand(snapManager), new InvSaveCommand(snapManager), "xdtils.invsave");
        registerCommand("invload", new InvLoadCommand(snapManager), new InvLoadCommand(snapManager), "xdtils.invload");
        registerCommand("recipe", new RecipeCommand(), new RecipeCommand(), "xdtils.recipe");
        registerCommand("mute", new MuteCommand(muteManager), new MuteCommand(muteManager), "xdtils.mute");
        registerCommand("unmute", new UnmuteCommand(muteManager), new UnmuteCommand(muteManager), "xdtils.mute");
        registerCommand("history", new HistoryCommand(historyManager), new HistoryCommand(historyManager), "xdtils.history");
        registerCommand("trail", new TrailCommand(trailManager), new TrailCommand(trailManager), "xdtils.trail");
        registerCommand("list", new ListCommand(), null, "xdtils.list");
        registerCommand("menu", new MenuCommand(this), null, "xdtils.menu");

        // Economy
        if (economyManager != null) {
            BalanceCommand balanceCommand = new BalanceCommand(economyManager);
            registerCommand("balance", balanceCommand, balanceCommand, "xdtils.balance");
            registerCommand("bal", balanceCommand, balanceCommand, "xdtils.balance");
            registerCommand("money", balanceCommand, balanceCommand, "xdtils.balance");

            if (getConfig().getBoolean("economy.pay-enabled", true)) {
                PayCommand payCommand = new PayCommand(economyManager);
                registerCommand("pay", payCommand, payCommand, "xdtils.pay");
            }

            if (getConfig().getBoolean("economy.admin-enabled", true)) {
                EcoCommand ecoCommand = new EcoCommand(economyManager);
                registerCommand("eco", ecoCommand, ecoCommand, "xdtils.eco");
            }
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new BackListener(), this);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), this);
        Bukkit.getPluginManager().registerEvents(new VanishListener(vanishManager), this);
        Bukkit.getPluginManager().registerEvents(new FreezeListener(freezeManager), this);
        Bukkit.getPluginManager().registerEvents(new MaintenanceListener(maintenanceManager), this);
        Bukkit.getPluginManager().registerEvents(new CommandSpyListener(spyManager), this);
        Bukkit.getPluginManager().registerEvents(new AfkListener(afkManager), this);
        Bukkit.getPluginManager().registerEvents(new MuteListener(muteManager), this);
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

        if (completer != null) {
            cmd.setTabCompleter(completer);
        }

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

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public boolean isEconomyEnabled() {
        return economyManager != null;
    }
}