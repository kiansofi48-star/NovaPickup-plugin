package me.novapixel.novapickup;

import org.bukkit.Material;
import org.bukkit.command.*;
import org.bukkit.util.StringUtil;

import java.util.*;

public final class NovaPickupCommand
        implements CommandExecutor, TabCompleter {

    private final NovaPickup plugin;
    private final PickupManager manager;

    public NovaPickupCommand(
            NovaPickup plugin,
            PickupManager manager
    ) {

        this.plugin = plugin;
        this.manager = manager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.hasPermission(
                "novapickup.admin"
        )) {

            sender.sendMessage(
                    "§cYou don't have permission."
            );

            return true;
        }

        if (args.length == 0) {

            help(sender, label);

            return true;
        }

        switch (
                args[0]
                        .toLowerCase(Locale.ROOT)
        ) {

            case "add" ->
                    add(sender, args);

            case "delete", "remove" ->
                    delete(sender, args);

            case "edit" ->
                    edit(sender, args);

            case "reload" -> {

                plugin.reloadNovaPickup();

                sender.sendMessage(
                        "§aNovaPickup reloaded!"
                );
            }

            default ->
                    help(sender, label);
        }

        return true;
    }

    private void add(
            CommandSender sender,
            String[] args
    ) {

        String blockName =
                value(args, "block");

        String dropName =
                value(args, "drop");

        String amountText =
                value(args, "amount");

        if (
                blockName == null ||
                        dropName == null ||
                        amountText == null
        ) {

            sender.sendMessage(
                    "§cUsage: /novapickup add block:STONE drop:DIAMOND amount:3"
            );

            return;
        }

        Material block =
                material(blockName);

        Material drop =
                material(dropName);

        Integer amount =
                amount(amountText);

        if (block == null) {

            sender.sendMessage(
                    "§cInvalid block: " + blockName
            );

            return;
        }

        if (drop == null) {

            sender.sendMessage(
                    "§cInvalid drop: " + dropName
            );

            return;
        }

        if (
                amount == null ||
                        amount <= 0
        ) {

            sender.sendMessage(
                    "§cAmount must be greater than 0."
            );

            return;
        }

        if (!manager.addRule(
                block,
                drop,
                amount
        )) {

            sender.sendMessage(
                    "§cThis block already exists. Use edit."
            );

            return;
        }

        sender.sendMessage(
                "§aAdded §f" +
                        block.name() +
                        " §7-> §f" +
                        amount +
                        "x " +
                        drop.name()
        );
    }

    private void delete(
            CommandSender sender,
            String[] args
    ) {

        String blockName =
                value(args, "block");

        if (blockName == null) {

            sender.sendMessage(
                    "§cUsage: /novapickup delete block:STONE"
            );

            return;
        }

        Material block =
                material(blockName);

        if (block == null) {

            sender.sendMessage(
                    "§cInvalid block."
            );

            return;
        }

        if (!manager.deleteRule(block)) {

            sender.sendMessage(
                    "§cThis block isn't configured."
            );

            return;
        }

        sender.sendMessage(
                "§aDeleted §f" +
                        block.name()
        );
    }

    private void edit(
            CommandSender sender,
            String[] args
    ) {

        String blockName =
                value(args, "block");

        String dropName =
                value(args, "drop");

        String amountText =
                value(args, "amount");

        if (
                blockName == null ||
                        dropName == null ||
                        amountText == null
        ) {

            sender.sendMessage(
                    "§cUsage: /novapickup edit block:STONE drop:EMERALD amount:5"
            );

            return;
        }

        Material block =
                material(blockName);

        Material drop =
                material(dropName);

        Integer amount =
                amount(amountText);

        if (
                block == null ||
                        drop == null ||
                        amount == null ||
                        amount <= 0
        ) {

            sender.sendMessage(
                    "§cInvalid block, drop or amount."
            );

            return;
        }

        if (!manager.editRule(
                block,
                drop,
                amount
        )) {

            sender.sendMessage(
                    "§cThis block isn't configured. Use add."
            );

            return;
        }

        sender.sendMessage(
                "§aEdited §f" +
                        block.name() +
                        " §7-> §f" +
                        amount +
                        "x " +
                        drop.name()
        );
    }

    private void help(
            CommandSender sender,
            String label
    ) {

        sender.sendMessage(
                "§8§m-----------------------------"
        );

        sender.sendMessage(
                "§b§lNovaPickup"
        );

        sender.sendMessage(
                "§f/" + label +
                        " add block:STONE drop:DIAMOND amount:3"
        );

        sender.sendMessage(
                "§f/" + label +
                        " delete block:STONE"
        );

        sender.sendMessage(
                "§f/" + label +
                        " edit block:STONE drop:EMERALD amount:5"
        );

        sender.sendMessage(
                "§f/" + label +
                        " reload"
        );

        sender.sendMessage(
                "§8§m-----------------------------"
        );
    }

    private String value(
            String[] args,
            String key
    ) {

        for (String arg : args) {

            int colon =
                    arg.indexOf(':');

            if (
                    colon <= 0 ||
                            colon == arg.length() - 1
            ) {
                continue;
            }

            if (
                    arg.substring(
                            0,
                            colon
                    ).equalsIgnoreCase(key)
            ) {

                return arg.substring(
                        colon + 1
                );
            }
        }

        return null;
    }

    private Material material(
            String value
    ) {

        try {

            return Material.valueOf(
                    value.trim()
                            .toUpperCase(Locale.ROOT)
            );

        } catch (Exception e) {

            return null;
        }
    }

    private Integer amount(
            String value
    ) {

        try {

            return Integer.parseInt(value);

        } catch (NumberFormatException e) {

            return null;
        }
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (!sender.hasPermission(
                "novapickup.admin"
        )) {

            return Collections.emptyList();
        }

        if (args.length == 1) {

            return partial(
                    args[0],
                    List.of(
                            "add",
                            "delete",
                            "edit",
                            "reload"
                    )
            );
        }

        if (args.length == 2) {

            if (
                    args[0].equalsIgnoreCase("add") ||
                            args[0].equalsIgnoreCase("edit")
            ) {

                return partial(
                        args[1],
                        List.of(
                                "block:",
                                "drop:",
                                "amount:"
                        )
                );
            }

            if (
                    args[0].equalsIgnoreCase("delete")
            ) {

                return partial(
                        args[1],
                        List.of("block:")
                );
            }
        }

        if (
                args.length == 3 &&
                        (
                                args[0].equalsIgnoreCase("add") ||
                                        args[0].equalsIgnoreCase("edit")
                        )
        ) {

            return partial(
                    args[2],
                    List.of(
                            "drop:",
                            "amount:"
                    )
            );
        }

        if (
                args.length == 4 &&
                        (
                                args[0].equalsIgnoreCase("add") ||
                                        args[0].equalsIgnoreCase("edit")
                        )
        ) {

            return partial(
                    args[3],
                    List.of("amount:")
            );
        }

        return Collections.emptyList();
    }

    private List<String> partial(
            String input,
            List<String> values
    ) {

        List<String> result =
                new ArrayList<>();

        StringUtil.copyPartialMatches(
                input,
                values,
                result
        );

        Collections.sort(result);

        return result;
    }
}