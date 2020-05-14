package net.thenova.titan.spigot.module.economy.commands.subs_economy;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.library.util.UNumber;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageBuilder;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import net.thenova.titan.spigot.module.economy.handler.EconomyHandler;
import net.thenova.titan.spigot.module.economy.user.UserEconomy;
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
        usage = "economy give <player> <amount>",
        description = "Give a player an amount of money"
)
public final class SubCommandEconomyGive extends SpigotCommand<SpigotSender> implements CommandPermission<SpigotSender> {

    public SubCommandEconomyGive() {
        super("give");
    }

    @Override
    public void execute(SpigotSender sender, CommandContext context) {
        final String arg = context.getArgument(1);
        if(arg.contains("-")) {
            MessageHandler.INSTANCE.build("module.economy.positive-value").send(sender);
            return;
        }

        final BigDecimal amount;
        try {
            amount = new BigDecimal(arg);
        } catch (NumberFormatException ex) {
            MessageHandler.INSTANCE.build("module.economy.number").send(sender);
            return;
        }

        if(amount.compareTo(EconomyHandler.INSTANCE.getMinimumValue()) < 0) {
            MessageHandler.INSTANCE.build("module.economy.minimum-value")
                    .placeholder(new Placeholder("amount", EconomyHandler.INSTANCE.getMinimumValue()))
                    .send(sender);
            return;
        }

        final String name = context.getArgument(0);
        final List<User> recipients;
        if(name.equals("*") && sender.getSender().hasPermission("titan.command.economy.give.all")) {
            recipients = UserHandler.INSTANCE.getOnlineUsers();
        } else {
            if(!UserHandler.INSTANCE.playerExists(name)) {
                MessageHandler.INSTANCE.build("error.player.exists").send(sender);
                return;
            }

            recipients = Collections.singletonList(UserHandler.INSTANCE.getUser(name));
        }

        MessageBuilder builder = MessageHandler.INSTANCE.build("module.economy.economy.give.complete.user")
                .placeholder(new Placeholder("amount", UNumber.displayCurrencyExactly(amount)),
                        new PlayerPlaceholder(sender.getSender().getName()));

        recipients.forEach(other -> other.getModule(UserEconomy.class).give(amount));
        recipients.stream().filter(User::isOnline).forEach(builder::send);

        MessageHandler.INSTANCE.build("module.economy.economy.give.complete.sender")
                .placeholder(new Placeholder("amount", UNumber.displayCurrencyExactly(amount)),
                        new PlayerPlaceholder(recipients.size() > 1 ? "all players" : recipients.get(0).getName()))
                .send(sender);
    }

    @Override
    public boolean hasPermission(SpigotSender sender) {
        return sender.getSender().hasPermission("titan.command.economy.give");
    }
}
