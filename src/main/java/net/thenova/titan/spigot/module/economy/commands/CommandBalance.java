package net.thenova.titan.spigot.module.economy.commands;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.util.UNumber;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import net.thenova.titan.spigot.module.economy.user.UserEconomy;
import net.thenova.titan.spigot.users.UserHandler;
import net.thenova.titan.spigot.users.user.User;

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
public final class CommandBalance extends SpigotCommand<User> implements CommandPermission<User> {

    public CommandBalance() {
        super("balance", "bal", "money");
    }

    @Override
    public void execute(User user, CommandContext context) {
        final String[] args = context.getArguments();

        if(args.length == 0) {
            MessageHandler.INSTANCE.build("module.economy.balance.self")
                    .placeholder(new Placeholder("balance", UNumber.fixMoney(user.getModule(UserEconomy.class).getDouble())))
                    .send(user);
        } else {
            if(!UserHandler.INSTANCE.playerExists(args[0])) {
                MessageHandler.INSTANCE.build("error.player.exists").send(user);
            } else {
                final User other = UserHandler.INSTANCE.getUser(args[0]);

                MessageHandler.INSTANCE.build("module.economy.balance.other")
                        .placeholder(new PlayerPlaceholder(other.getName()),
                                new Placeholder("balance", UNumber.fixMoney(other.getModule(UserEconomy.class).getDouble())))
                        .send(user);
            }
        }
    }

    @Override
    public boolean hasPermission(User user) {
        return user.hasPermission("titan.command.balance");
    }
}
