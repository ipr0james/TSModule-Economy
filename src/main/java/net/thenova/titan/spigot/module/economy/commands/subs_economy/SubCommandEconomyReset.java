package net.thenova.titan.spigot.module.economy.commands.subs_economy;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.command.data.CommandUsage;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.data.message.placeholders.Placeholder;
import net.thenova.titan.spigot.data.message.placeholders.PlayerPlaceholder;
import net.thenova.titan.spigot.module.economy.handler.EconomyHandler;
import net.thenova.titan.spigot.module.economy.user.UserEconomy;
import net.thenova.titan.spigot.users.UserHandler;
import net.thenova.titan.spigot.users.user.User;

import java.math.BigDecimal;

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
        min = 1,
        usage = "economy reset <player>",
        description = "Reset a player to the default balance."
)
public final class SubCommandEconomyReset extends SpigotCommand<SpigotSender> implements CommandPermission<SpigotSender> {

    public SubCommandEconomyReset() {
        super("reset");
    }

    @Override
    public void execute(SpigotSender sender, CommandContext context) {
        final String name = context.getArgument(0);
        if(!UserHandler.INSTANCE.playerExists(name)) {
            MessageHandler.INSTANCE.build("error.player.exists").send(sender);
            return;
        }

        final User other = UserHandler.INSTANCE.getUser(name);
        final BigDecimal def = EconomyHandler.INSTANCE.getDefaultBalance();

        other.getModule(UserEconomy.class).set(def);
        if(other.isOnline()) {
            MessageHandler.INSTANCE.build("module.economy.economy.reset.complete.user")
                    .placeholder(new PlayerPlaceholder(sender.name()),
                            new Placeholder("amount", def.toString())).send(other);
        }

        MessageHandler.INSTANCE.build("module.economy.economy.reset.complete.sender")
                .placeholder(new PlayerPlaceholder(other),
                        new Placeholder("amount", def.toString()))
                .send(sender);
    }

    @Override
    public boolean hasPermission(SpigotSender sender) {
        return sender.getSender().hasPermission("titan.command.economy.reset");
    }
}
