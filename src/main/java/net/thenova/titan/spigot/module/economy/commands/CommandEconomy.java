package net.thenova.titan.spigot.module.economy.commands;

import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.economy.commands.subs_economy.SubCommandEconomyGive;
import net.thenova.titan.spigot.module.economy.commands.subs_economy.SubCommandEconomyReset;
import net.thenova.titan.spigot.module.economy.commands.subs_economy.SubCommandEconomySet;
import net.thenova.titan.spigot.module.economy.commands.subs_economy.SubCommandEconomyTake;

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
public final class CommandEconomy extends SpigotCommand<SpigotSender> implements CommandPermission<SpigotSender> {

    public CommandEconomy() {
        super("economy", "eco");

        this.addSubCommand(new SubCommandEconomyGive(),
                new SubCommandEconomyReset(),
                new SubCommandEconomySet(),
                new SubCommandEconomyTake());
    }

    @Override
    public void execute(SpigotSender sender, CommandContext context) {
        MessageHandler.INSTANCE.build("module.economy.economy.help").send(sender);
    }

    @Override
    public boolean hasPermission(SpigotSender sender) {
        return sender.getSender().hasPermission("titan.command.economy.base");
    }
}
