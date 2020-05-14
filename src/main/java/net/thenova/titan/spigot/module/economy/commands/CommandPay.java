package net.thenova.titan.spigot.module.economy.commands;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.library.util.UNumber;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import net.thenova.titan.spigot.module.economy.handler.EconomyHandler;
import net.thenova.titan.spigot.module.economy.user.UserEconomy;
import net.thenova.titan.spigot.plugin.users.modules.UserValidation;
import net.thenova.titan.spigot.users.UserHandler;
import net.thenova.titan.spigot.users.user.User;

import java.math.BigDecimal;
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
@CommandUsage(
        min = 2,
        usage = "pay {player} {amount}",
        description = "Pay a user an amount of money"
)
public final class CommandPay extends SpigotCommand<User> implements CommandPermission<User> {

    public CommandPay() {
        super("pay");
    }

    @Override
    public void execute(User user, CommandContext context) {
        final String[] args = context.getArguments();

        if(args[1].contains("-")) {
            MessageHandler.INSTANCE.build("module.economy.positive-value").send(user);
            return;
        }

        final BigDecimal amount;
        try {
            amount = new BigDecimal(args[1]);
        } catch (NumberFormatException ex) {
            MessageHandler.INSTANCE.build("module.economy.number").send(user);
            return;
        }

        if(amount.compareTo(EconomyHandler.INSTANCE.getMinimumValue()) < 0) {
            MessageHandler.INSTANCE.build("module.economy.minimum-value")
                    .placeholder(new Placeholder("amount", EconomyHandler.INSTANCE.getMinimumValue()))
                    .send(user);
            return;
        }

        final String name = args[0];
        final UserEconomy economy = user.getModule(UserEconomy.class);
        final List<User> recipients;

        if(name.equals("*") && user.hasPermission("titan.command.pay.all")) {
            recipients = EconomyHandler.INSTANCE.getValidUsers(user);
        } else {
            if(!UserHandler.INSTANCE.playerExists(name)) {
                MessageHandler.INSTANCE.build("error.player.exists").send(user);
                return;
            }

            final User other;
            if(!(other = UserHandler.INSTANCE.getUser(name)).isOnline()
                    || other.getModule(UserValidation.class).contains("module_vanish")) {
                MessageHandler.INSTANCE.build("error.player.offline").send(user);
                return;
            }

            if(other.getUUID().equals(user.getUUID())) {
                MessageHandler.INSTANCE.build("module.economy.pay.self").send(user);
                return;
            }

            recipients = Collections.singletonList(other);
        }

        final BigDecimal multipliedAmount = amount.multiply(BigDecimal.valueOf(recipients.size()));

        if(!economy.has(multipliedAmount)) {
            MessageHandler.INSTANCE.build("module.economy.insufficient-funds").send(user);
            return;
        }

        economy.take(multipliedAmount);

        recipients.forEach(other -> other.getModule(UserEconomy.class).give(amount));
        MessageHandler.INSTANCE.build("module.economy.pay.received")
                .placeholder(new Placeholder("amount", UNumber.displayCurrencyExactly(amount)),
                        new PlayerPlaceholder(user))
                .send(recipients);

        MessageHandler.INSTANCE.build("module.economy.pay.paid")
                .placeholder(new Placeholder("amount", UNumber.displayCurrencyExactly(amount)),
                        new PlayerPlaceholder(recipients.size() > 1 ? "all players" : recipients.get(0).getName()))
                .send(user);
    }

    @Override
    public boolean hasPermission(User user) {
        return user.hasPermission("titan.command.pay");
    }
}
