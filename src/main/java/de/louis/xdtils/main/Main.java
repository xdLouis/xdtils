package de.louis.xdtils.main;

import de.louis.xdtils.commands.*;
import de.louis.xdtils.listener.*;
import de.louis.xdtils.manager.*;
import de.louis.xdtils.manager.permissions.PermissionSystemManager;
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
    private PermissionSystemManager permissionSystemManager;
    private GlowManager glowManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.configManager = new ConfigManager(this);
        this.vanishManager = new VanishManager(this);
        this.spyManager = new SpyManager();
        this.msgManager = new MsgManager(spyManager);
        this.afkManager = new AfkManager();
        this.freezeManager = new FreezeManager();
        this.maintenanceManager = new MaintenanceManager();
        this.muteManager = new MuteManager();
        this.trailManager = new TrailManager(this);

        if (getConfig().getBoolean("economy.enabled", true)) {
            this.economyManager = new EconomyManager(this);
            getLogger().info("Interne Economy wurde aktiviert.");
        } else {
            getLogger().info("Interne Economy ist deaktiviert.");
        }

        if (getConfig().getBoolean("permissions-system.enabled", true)) {
            this.permissionSystemManager = new PermissionSystemManager(this);
            getLogger().info("Internes Permission-System wurde aktiviert.");
        } else {
            getLogger().info("Internes Permission-System ist deaktiviert.");
        }

        this.glowManager = new GlowManager(this);
        if (glowManager.isEnabledInConfig()) {
            getLogger().info("Glow-System wurde aktiviert.");
        } else {
            getLogger().info("Glow-System ist deaktiviert.");
        }

        registerCommands();
        registerListeners();

        if (permissionSystemManager != null) {
            permissionSystemManager.refreshAllOnlinePlayers();
        }

        if (glowManager != null) {
            glowManager.refreshAll();
        }

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

        if (permissionSystemManager != null) {
            permissionSystemManager.save();
        }

        if (glowManager != null) {
            glowManager.save();
        }

        getLogger().info("xdtils wurde deaktiviert.");
    }

    private void registerCommands() {
        GamemodeCommand gm = new GamemodeCommand();
        registerCommand("gamemode", gm, gm, "xdtils.gamemode");
        registerCommand("gm", gm, gm, "xdtils.gamemode");

        SpeedCommand speedAuto = new SpeedCommand(SpeedCommand.SpeedType.AUTO);
        SpeedCommand speedWalk = new SpeedCommand(SpeedCommand.SpeedType.WALK);
        SpeedCommand speedFly = new SpeedCommand(SpeedCommand.SpeedType.FLY);
        registerCommand("speed", speedAuto, speedAuto, "xdtils.speed");
        registerCommand("walkspeed", speedWalk, speedWalk, "xdtils.walkspeed");
        registerCommand("flyspeed", speedFly, speedFly, "xdtils.flyspeed");

        EnchantCommand enchant = new EnchantCommand();
        registerCommand("enchant", enchant, enchant, "xdtils.enchant");

        registerCommand("hat", new HatCommand(), null, "xdtils.hat");

        InvseeCommand invsee = new InvseeCommand(this);
        registerCommand("invsee", invsee, invsee, "xdtils.invsee");

        registerCommand("back", new BackCommand(), null, "xdtils.back");

        TeleportCommand tp = new TeleportCommand();
        registerCommand("tp", tp, tp, "xdtils.tp");

        TpHereCommand tpHere = new TpHereCommand();
        registerCommand("tphere", tpHere, null, "xdtils.tphere");

        TpWorldCommand tpWorld = new TpWorldCommand();
        registerCommand("tpworld", tpWorld, tpWorld, "xdtils.tpworld");

        ClearCommand clear = new ClearCommand();
        registerCommand("clear", clear, clear, "xdtils.clear");

        registerCommand("trash", new TrashCommand(this), null, "xdtils.trash");

        ItemCommand item = new ItemCommand();
        registerCommand("i", item, item, "xdtils.item");

        GiveCommand give = new GiveCommand();
        registerCommand("give", give, give, "xdtils.give");

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

        OpCommand opCommand = new OpCommand(false);
        registerCommand("op", opCommand, opCommand, "xdtils.op");

        OpCommand deopCommand = new OpCommand(true);
        registerCommand("deop", deopCommand, deopCommand, "xdtils.deop");

        KillCommand kill = new KillCommand();
        registerCommand("kill", kill, kill, "xdtils.kill");

        TimeCommand time = new TimeCommand();
        registerCommand("time", time, time, "xdtils.time");

        WeatherCommand weather = new WeatherCommand();
        registerCommand("weather", weather, weather, "xdtils.weather");

        DifficultyCommand difficulty = new DifficultyCommand();
        registerCommand("difficulty", difficulty, difficulty, "xdtils.difficulty");

        WhitelistCommand whitelist = new WhitelistCommand();
        registerCommand("whitelist", whitelist, whitelist, "xdtils.whitelist");

        HealCommand heal = new HealCommand();
        registerCommand("heal", heal, heal, "xdtils.heal");

        FeedCommand feed = new FeedCommand();
        registerCommand("feed", feed, feed, "xdtils.feed");

        FlyCommand fly = new FlyCommand();
        registerCommand("fly", fly, fly, "xdtils.fly");

        registerCommand("cc", new ClearChatCommand(), null, "xdtils.clearchat");

        VanishCommand vanish = new VanishCommand(vanishManager);
        registerCommand("vanish", vanish, vanish, "xdtils.vanish");
        registerCommand("v", vanish, vanish, "xdtils.vanish");

        InfoCommand info = new InfoCommand();
        registerCommand("info", info, info, "xdtils.info");

        NickCommand nick = new NickCommand();
        registerCommand("nick", nick, nick, "xdtils.nick");

        MsgCommand msg = new MsgCommand(msgManager);
        registerCommand("msg", msg, msg, "xdtils.msg");

        registerCommand("r", new ReplyCommand(msgManager), null, "xdtils.msg");

        IgnoreCommand ignore = new IgnoreCommand(msgManager);
        registerCommand("ignore", ignore, ignore, "xdtils.ignore");

        registerCommand("afk", new AfkCommand(afkManager), null, "xdtils.afk");

        SeenCommand seen = new SeenCommand();
        registerCommand("seen", seen, seen, "xdtils.seen");

        PlaytimeCommand playtime = new PlaytimeCommand();
        registerCommand("playtime", playtime, playtime, "xdtils.playtime");

        PingCommand ping = new PingCommand();
        registerCommand("ping", ping, ping, "xdtils.ping");

        registerCommand("broadcast", new BroadcastCommand(), null, "xdtils.broadcast");
        registerCommand("staff", new StaffCommand(), null, "xdtils.staff");

        MaintenanceCommand maintenance = new MaintenanceCommand(maintenanceManager);
        registerCommand("maintenance", maintenance, maintenance, "xdtils.maintenance");

        FreezeCommand freeze = new FreezeCommand(freezeManager);
        registerCommand("freeze", freeze, freeze, "xdtils.freeze");

        SudoCommand sudo = new SudoCommand();
        registerCommand("sudo", sudo, sudo, "xdtils.sudo");

        registerCommand("rename", new RenameCommand(), null, "xdtils.rename");

        LoreCommand lore = new LoreCommand();
        registerCommand("lore", lore, lore, "xdtils.lore");

        registerCommand("itemdb", new ItemDbCommand(), null, "xdtils.itemdb");

        registerCommand("commandspy", new CommandSpyCommand(spyManager), null, "xdtils.commandspy");
        registerCommand("socialspy", new SocialSpyCommand(spyManager), null, "xdtils.socialspy");

        registerWorkstation("workbench", "workbench", "xdtils.workbench");
        registerWorkstation("anvil", "anvil", "xdtils.anvil");
        registerWorkstation("grindstone", "grindstone", "xdtils.grindstone");
        registerWorkstation("cartography", "cartography", "xdtils.cartography");
        registerWorkstation("loom", "loom", "xdtils.loom");
        registerWorkstation("stonecutter", "stonecutter", "xdtils.stonecutter");
        registerWorkstation("smithing", "smithing", "xdtils.smithing");
        registerWorkstation("enchanting", "enchanting", "xdtils.enchanting");

        GodCommand god = new GodCommand();
        registerCommand("god", god, god, "xdtils.god");

        RepairCommand repair = new RepairCommand();
        registerCommand("repair", repair, repair, "xdtils.repair");

        registerCommand("more", new MoreCommand(), null, "xdtils.more");

        RestartCommand restart = new RestartCommand(this);
        registerCommand("restart", restart, restart, "xdtils.restart");

        registerCommand("tps", new TpsCommand(), null, "xdtils.tps");
        registerCommand("memory", new MemoryCommand(), null, "xdtils.memory");

        XpCommand xp = new XpCommand();
        registerCommand("xp", xp, xp, "xdtils.xp");

        registerCommand("near", new NearCommand(), null, "xdtils.near");
        registerCommand("firework", new FireworkCommand(), null, "xdtils.firework");

        SkullCommand skull = new SkullCommand();
        registerCommand("skull", skull, skull, "xdtils.skull");

        InvSnapshotManager snapManager = new InvSnapshotManager();
        HistoryManager historyManager = new HistoryManager();

        InvSaveCommand invSave = new InvSaveCommand(snapManager);
        registerCommand("invsave", invSave, invSave, "xdtils.invsave");

        InvLoadCommand invLoad = new InvLoadCommand(snapManager);
        registerCommand("invload", invLoad, invLoad, "xdtils.invload");

        RecipeCommand recipe = new RecipeCommand();
        registerCommand("recipe", recipe, recipe, "xdtils.recipe");

        MuteCommand mute = new MuteCommand(muteManager);
        registerCommand("mute", mute, mute, "xdtils.mute");

        UnmuteCommand unmute = new UnmuteCommand(muteManager);
        registerCommand("unmute", unmute, unmute, "xdtils.mute");

        HistoryCommand history = new HistoryCommand(historyManager);
        registerCommand("history", history, history, "xdtils.history");

        TrailCommand trail = new TrailCommand(trailManager);
        registerCommand("trail", trail, trail, "xdtils.trail");

        registerCommand("list", new ListCommand(), null, "xdtils.list");
        registerCommand("menu", new MenuCommand(this), null, "xdtils.menu");

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

        if (permissionSystemManager != null) {
            PermissionCommand permissionCommand = new PermissionCommand(permissionSystemManager);
            registerCommand("permissions", permissionCommand, permissionCommand, "xdtils.permissions");
            registerCommand("perms", permissionCommand, permissionCommand, "xdtils.permissions");
        }

        if (glowManager != null) {
            GlowCommand glow = new GlowCommand(glowManager);
            registerCommand("glow", glow, glow, "xdtils.glow");
        }
        EnderChestCommand enderChest = new EnderChestCommand();
        registerCommand("enderchest", enderChest, enderChest, "xdtils.enderchest");
        registerCommand("ec", enderChest, enderChest, "xdtils.enderchest");

        TopCommand top = new TopCommand();
        registerCommand("top", top, top, "xdtils.top");

        BottomCommand bottom = new BottomCommand();
        registerCommand("bottom", bottom, bottom, "xdtils.bottom");

        MobCommand mob = new MobCommand();
        registerCommand("mob", mob, mob, "xdtils.mob");
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

        if (permissionSystemManager != null && getConfig().getBoolean("permissions-system.gui-enabled", true)) {
            Bukkit.getPluginManager().registerEvents(new PermissionMenuListener(permissionSystemManager), this);
        }

        if (permissionSystemManager != null) {
            Bukkit.getPluginManager().registerEvents(new PermissionPlayerListener(permissionSystemManager), this);
        }

        if (glowManager != null) {
            Bukkit.getPluginManager().registerEvents(new GlowListener(glowManager), this);
        }
    }

    private void registerCommand(String name, CommandExecutor executor, TabCompleter completer, String defaultPermission) {
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

    public PermissionSystemManager getPermissionSystemManager() {
        return permissionSystemManager;
    }

    public boolean isPermissionSystemEnabled() {
        return permissionSystemManager != null;
    }
}