package net.thenova.titan.spigot.module.economy;

import de.arraying.kotys.JSONArray;
import net.thenova.titan.library.command.data.Command;
import net.thenova.titan.library.database.connection.IDatabase;
import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.economy.commands.CommandBalance;
import net.thenova.titan.spigot.module.economy.commands.CommandBalanceTop;
import net.thenova.titan.spigot.module.economy.commands.CommandEconomy;
import net.thenova.titan.spigot.module.economy.commands.CommandPay;
import net.thenova.titan.spigot.module.economy.database.DatabaseEconomy;
import net.thenova.titan.spigot.module.economy.database.tables.DBTableEconomy;
import net.thenova.titan.spigot.module.economy.handler.EconomyHandler;
import net.thenova.titan.spigot.module.economy.user.UserEconomy;
import net.thenova.titan.spigot.plugin.IPlugin;
import net.thenova.titan.spigot.users.user.module.UserModule;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Copyright 2019 ipr0james
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public final class Economy implements IPlugin {

    @Override
    public String name() {
        return "Economy";
    }

    @Override
    public void load() {
        EconomyHandler.INSTANCE.load();
    }

    @Override
    public void messages(final MessageHandler handler) {
        handler.add("module.economy.number", "%prefix.error% You must provide a number as the amount.");
        handler.add("module.economy.positive-value", "%prefix.error% You must enter a positive value.");
        handler.add("module.economy.insufficient-funds", "%prefix.error% You do not have sufficient funds.");
        handler.add("module.economy.minimum-value", "%prefix.error% The minimum amount you can provide is &c%amount%&7.");

        /* Balance */
        handler.add("module.economy.balance.self", "%prefix.info% Your balance is &d$%balance%&7.");
        handler.add("module.economy.balance.other", "%prefix.info% %player%'s balance is &d$%balance%&7.");

        /* Balance Top */
        handler.add("module.economy.balancetop.unknown-page", "%prefix.error% That page does not exist.");
        handler.add("module.economy.balancetop.title", "%prefix.info% Server Total: &d%total%");
        handler.add("module.economy.balancetop.value", "&8%position%. &d%name% %balance%");
        handler.add("module.economy.balancetop.suffix.normal", "&7Type &d/baltop %next-page% &7to read the next page.");
        handler.add("module.economy.balancetop.suffix.last", "&7You have reached the last page.");

        /* Economy */
        // Economy - Help
        handler.add("module.economy.economy.help", new JSONArray()
                .append("",
                        "&d/eco give <player> <amount> &7- Give a player money",
                        "&d/eco reset <player> <amount> &7- Reset a players money",
                        "&d/eco set <player> <amount> &7- Set a players money",
                        "&d/eco take <player> <amount> &7- Take money from a player",
                        ""));

        // Economy - Give
        handler.add("module.economy.economy.give.complete.user", "%prefix.info% &d%amount% &7has been added to your account.");
        handler.add("module.economy.economy.give.complete.sender", "%prefix.info% &d%amount% &7has been added to &d%player%&7.");

        // Economy - Reset
        handler.add("module.economy.economy.reset.complete.user", "%prefix.info% Your balance has been reset to &d%amount%&7.");
        handler.add("module.economy.economy.reset.complete.sender", "%prefix.info% &d%player% &7balance has been reset to server default.");

        // Economy - Set
        handler.add("module.economy.economy.set.complete.user", "%prefix.info% Your balance has been set to &d%amount%&7.");
        handler.add("module.economy.economy.set.complete.sender", "%prefix.info% &d%player% &7balance has been set to &d%amount%&7.");

        // Economy - Take
        handler.add("module.economy.economy.take.complete.user", "%prefix.info% &d%amount% &7has been taken your account.");
        handler.add("module.economy.economy.take.complete.sender", "%prefix.info% &d%amount% &7has been taken from &d%player%&7.");

        /* Pay */
        handler.add("module.economy.pay.self", "%prefix.error% You cannot pay money to yourself.");
        handler.add("module.economy.pay.received", "%prefix.info% &d%amount% &7has been received from &d%player%&7.");
        handler.add("module.economy.pay.paid", "%prefix.info% You have paid &d%amount% &7to %player%");
    }

    @Override
    public void reload() {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public IDatabase database() {
        return new DatabaseEconomy();
    }

    @Override
    public List<DatabaseTable> tables() {
        return Collections.singletonList(new DBTableEconomy());
    }

    @Override
    public List<Listener> listeners() {
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public List<Command> commands() {
        return Arrays.asList(new CommandBalance(), new CommandBalanceTop(), new CommandEconomy(), new CommandPay());
    }

    @Override
    public List<Class<? extends UserModule>> user() {
        return Collections.singletonList(UserEconomy.class);
    }
}
