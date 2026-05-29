package de.louis.xdtils.commands;

import de.louis.xdtils.manager.permissions.PermissionGroup;
import de.louis.xdtils.manager.permissions.PermissionMenu;
import de.louis.xdtils.manager.permissions.PermissionSystemManager;
import de.louis.xdtils.manager.permissions.PermissionUserData;
import de.louis.xdtils.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class PermissionCommand implements CommandExecutor, TabCompleter {

    private final PermissionSystemManager manager;
    private final PermissionMenu menu;

    public PermissionCommand(PermissionSystemManager manager) {
        this.manager = manager;
        this.menu = new PermissionMenu(manager);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!manager.isEnabled()) {
            sender.sendMessage(MessageUtil.permissionsDisabled());
            return true;
        }

        if (args.length == 0) {
            if (sender instanceof Player player) {
                menu.openMain(player);
            } else {
                sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: " + MessageUtil.command("/permissions") + " <group|user|list|reload></gray>"));
            }
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                manager.saveAndReload();
                sender.sendMessage(MessageUtil.prefixed("<gray>Permission-System wurde neu geladen.</gray>"));
                return true;
            }
            case "list" -> {
                String groups = manager.getSortedGroups().stream()
                        .map(PermissionGroup::getName)
                        .collect(Collectors.joining(", "));
                sender.sendMessage(MessageUtil.prefixed("<gray>Gruppen: <#4DA3FF>" + (groups.isEmpty() ? "Keine" : groups) + "</#4DA3FF></gray>"));
                return true;
            }
            case "group" -> {
                return handleGroup(sender, args);
            }
            case "user" -> {
                return handleUser(sender, args);
            }
        }

        sender.sendMessage(MessageUtil.prefixed("<gray>Unbekannter Unterbefehl.</gray>"));
        return true;
    }

    private boolean handleGroup(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions group <create|delete|info|addperm|removeperm|inherit|uninherit> ...</gray>"));
            return true;
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        String groupName = args[2];

        switch (action) {
            case "create" -> {
                manager.createGroup(groupName);
                sender.sendMessage(MessageUtil.permissionGroupCreated(groupName));
                return true;
            }
            case "delete" -> {
                manager.deleteGroup(groupName);
                sender.sendMessage(MessageUtil.permissionGroupDeleted(groupName));
                return true;
            }
            case "info" -> {
                PermissionGroup group = manager.getGroup(groupName);
                if (group == null) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Gruppe nicht gefunden.</gray>"));
                    return true;
                }

                sender.sendMessage(MessageUtil.prefixed("<gray>Gruppe: <#4DA3FF>" + group.getName() + "</#4DA3FF></gray>"));
                sender.sendMessage(MessageUtil.prefixed("<gray>Displayname: <#4DA3FF>" + group.getDisplayName() + "</#4DA3FF></gray>"));
                sender.sendMessage(MessageUtil.prefixed("<gray>Priorität: <#4DA3FF>" + group.getPriority() + "</#4DA3FF></gray>"));
                sender.sendMessage(MessageUtil.prefixed("<gray>Permissions: <#4DA3FF>" +
                        (group.getPermissions().isEmpty() ? "Keine" : String.join(", ", group.getPermissions())) +
                        "</#4DA3FF></gray>"));
                sender.sendMessage(MessageUtil.prefixed("<gray>Vererbt von: <#4DA3FF>" +
                        (group.getInheritedGroups().isEmpty() ? "Keine" : String.join(", ", group.getInheritedGroups())) +
                        "</#4DA3FF></gray>"));
                return true;
            }
            case "addperm" -> {
                if (args.length < 4) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions group addperm <gruppe> <permission></gray>"));
                    return true;
                }

                Set<String> resolved = resolvePermissionInput(args[3]);
                if (resolved.isEmpty()) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Keine passenden Permissions für <red>" + args[3] + "</red> gefunden.</gray>"));
                    return true;
                }

                for (String permission : resolved) {
                    manager.addGroupPermission(groupName, permission);
                }

                sender.sendMessage(MessageUtil.prefixed("<gray>" + resolved.size() + " Permission(s) wurden der Gruppe "
                        + MessageUtil.player(groupName) + "<gray> hinzugefügt.</gray>"));
                return true;
            }
            case "removeperm" -> {
                if (args.length < 4) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions group removeperm <gruppe> <permission></gray>"));
                    return true;
                }

                Set<String> resolved = resolvePermissionInput(args[3]);
                if (resolved.isEmpty()) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Keine passenden Permissions für <red>" + args[3] + "</red> gefunden.</gray>"));
                    return true;
                }

                for (String permission : resolved) {
                    manager.removeGroupPermission(groupName, permission);
                }

                sender.sendMessage(MessageUtil.prefixed("<gray>" + resolved.size() + " Permission(s) wurden von der Gruppe "
                        + MessageUtil.player(groupName) + "<gray> entfernt.</gray>"));
                return true;
            }
            case "inherit" -> {
                if (args.length < 4) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions group inherit <gruppe> <andereGruppe></gray>"));
                    return true;
                }

                manager.addGroupInheritance(groupName, args[3]);
                sender.sendMessage(MessageUtil.prefixed("<gray>Gruppe " + MessageUtil.player(groupName)
                        + "<gray> erbt nun von " + MessageUtil.player(args[3]) + "<gray>.</gray>"));
                return true;
            }
            case "uninherit" -> {
                if (args.length < 4) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions group uninherit <gruppe> <andereGruppe></gray>"));
                    return true;
                }

                manager.removeGroupInheritance(groupName, args[3]);
                sender.sendMessage(MessageUtil.prefixed("<gray>Vererbung wurde entfernt.</gray>"));
                return true;
            }
        }

        sender.sendMessage(MessageUtil.prefixed("<gray>Unbekannter group-Unterbefehl.</gray>"));
        return true;
    }

    private boolean handleUser(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions user <info|addgroup|removegroup|addperm|removeperm> <spieler> ...</gray>"));
            return true;
        }

        String action = args[1].toLowerCase(Locale.ROOT);
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);

        if (target.getName() == null && !target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(MessageUtil.playerNotFound(args[2]));
            return true;
        }

        UUID uuid = target.getUniqueId();
        PermissionUserData data = manager.getUser(uuid);

        switch (action) {
            case "info" -> {
                sender.sendMessage(MessageUtil.prefixed("<gray>Spieler: " + MessageUtil.player(target.getName()) + "<gray></gray>"));
                sender.sendMessage(MessageUtil.prefixed("<gray>Gruppen: <#4DA3FF>" +
                        (data.getGroups().isEmpty() ? "Keine" : String.join(", ", data.getGroups())) +
                        "</#4DA3FF></gray>"));
                sender.sendMessage(MessageUtil.prefixed("<gray>Direkte Permissions: <#4DA3FF>" +
                        (data.getPermissions().isEmpty() ? "Keine" : String.join(", ", data.getPermissions())) +
                        "</#4DA3FF></gray>"));
                return true;
            }
            case "addgroup" -> {
                if (args.length < 4) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions user addgroup <spieler> <gruppe></gray>"));
                    return true;
                }

                manager.addUserGroup(uuid, args[3]);
                sender.sendMessage(MessageUtil.groupAdded(target.getName(), args[3]));
                return true;
            }
            case "removegroup" -> {
                if (args.length < 4) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions user removegroup <spieler> <gruppe></gray>"));
                    return true;
                }

                manager.removeUserGroup(uuid, args[3]);
                sender.sendMessage(MessageUtil.groupRemoved(target.getName(), args[3]));
                return true;
            }
            case "addperm" -> {
                if (args.length < 4) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions user addperm <spieler> <permission></gray>"));
                    return true;
                }

                Set<String> resolved = resolvePermissionInput(args[3]);
                if (resolved.isEmpty()) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Keine passenden Permissions für <red>" + args[3] + "</red> gefunden.</gray>"));
                    return true;
                }

                for (String permission : resolved) {
                    manager.addUserPermission(uuid, permission);
                }

                sender.sendMessage(MessageUtil.prefixed("<gray>" + resolved.size() + " Permission(s) wurden für "
                        + MessageUtil.player(target.getName()) + "<gray> hinzugefügt.</gray>"));
                return true;
            }
            case "removeperm" -> {
                if (args.length < 4) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Benutzung: /permissions user removeperm <spieler> <permission></gray>"));
                    return true;
                }

                Set<String> resolved = resolvePermissionInput(args[3]);
                if (resolved.isEmpty()) {
                    sender.sendMessage(MessageUtil.prefixed("<gray>Keine passenden Permissions für <red>" + args[3] + "</red> gefunden.</gray>"));
                    return true;
                }

                for (String permission : resolved) {
                    manager.removeUserPermission(uuid, permission);
                }

                sender.sendMessage(MessageUtil.prefixed("<gray>" + resolved.size() + " Permission(s) wurden von "
                        + MessageUtil.player(target.getName()) + "<gray> entfernt.</gray>"));
                return true;
            }
        }

        sender.sendMessage(MessageUtil.prefixed("<gray>Unbekannter user-Unterbefehl.</gray>"));
        return true;
    }

    private Set<String> resolvePermissionInput(String input) {
        String normalized = input.toLowerCase(Locale.ROOT);
        Set<String> known = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);

        known.addAll(manager.getAllKnownPermissions());

        if (normalized.equals("*")) {
            return known;
        }

        if (normalized.endsWith("*")) {
            String prefix = normalized.substring(0, normalized.length() - 1);
            return known.stream()
                    .filter(permission -> permission.toLowerCase(Locale.ROOT).startsWith(prefix))
                    .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)));
        }

        return new TreeSet<>(Collections.singleton(normalized));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return filter(List.of("group", "user", "list", "reload"), args[0]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("group")) {
            return filter(List.of("create", "delete", "info", "addperm", "removeperm", "inherit", "uninherit"), args[1]);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("user")) {
            return filter(List.of("info", "addgroup", "removegroup", "addperm", "removeperm"), args[1]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("group")) {
            return filter(new ArrayList<>(manager.getGroups().keySet()), args[2]);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("user")) {
            List<String> names = Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            return filter(names, args[2]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("user")
                && (args[1].equalsIgnoreCase("addgroup") || args[1].equalsIgnoreCase("removegroup"))) {
            return filter(new ArrayList<>(manager.getGroups().keySet()), args[3]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("addperm")) {
            List<String> suggestions = new ArrayList<>(manager.getAllKnownPermissions());
            suggestions.add("xdtils.*");
            suggestions.add("*");
            return filter(suggestions, args[3]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("group") && args[1].equalsIgnoreCase("removeperm")) {
            PermissionGroup group = manager.getGroup(args[2]);
            if (group == null) {
                return Collections.emptyList();
            }
            return filter(new ArrayList<>(group.getPermissions()), args[3]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("user") && args[1].equalsIgnoreCase("addperm")) {
            List<String> suggestions = new ArrayList<>(manager.getAllKnownPermissions());
            suggestions.add("xdtils.*");
            suggestions.add("*");
            return filter(suggestions, args[3]);
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("user") && args[1].equalsIgnoreCase("removeperm")) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
            if (target.getName() == null && !target.hasPlayedBefore() && !target.isOnline()) {
                return Collections.emptyList();
            }

            PermissionUserData data = manager.getUser(target.getUniqueId());
            return filter(new ArrayList<>(data.getPermissions()), args[3]);
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> input, String current) {
        return input.stream()
                .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(current.toLowerCase(Locale.ROOT)))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}