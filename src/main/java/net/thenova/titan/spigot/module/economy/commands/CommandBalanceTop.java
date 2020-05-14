package net.thenova.titan.spigot.module.economy.commands;

import net.thenova.titan.library.Titan;
import net.thenova.titan.library.command.data.CommandContext;
import net.thenova.titan.library.command.data.CommandPermission;
import net.thenova.titan.library.database.sql.SQLQuery;
import net.thenova.titan.library.util.UNumber;
import net.thenova.titan.library.util.pagination.Page;
import net.thenova.titan.library.util.pagination.Pagination;
import net.thenova.titan.spigot.TitanSpigot;
import net.thenova.titan.spigot.command.SpigotCommand;
import net.thenova.titan.spigot.command.sender.SpigotSender;
import net.thenova.titan.spigot.data.message.MessageHandler;
import net.thenova.titan.spigot.module.economy.database.DatabaseEconomy;
import net.thenova.titan.spigot.users.UUIDCache;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
public final class CommandBalanceTop extends SpigotCommand<SpigotSender> implements CommandPermission<SpigotSender> {

    private static final long CACHE_TIME = TimeUnit.SECONDS.toMillis(5);
    private final static String QUERY = "SELECT `uuid`,`balance` FROM `economy_users` ORDER BY `balance` DESC";

    private static long CACHE_AGE = 0;
    private static BigDecimal TOTAL = BigDecimal.ZERO;
    private static Pagination<BalPlayer> CACHE;
    private static boolean LOCK = false;

    public CommandBalanceTop() {
        super("balancetop", "baltop");
    }

    @Override
    public void execute(SpigotSender sender, CommandContext context) {
        int page = 1;

        if(context.getArguments().length > 0) {
            if(!UNumber.isInt(context.getArgument(0))) {
                MessageHandler.INSTANCE.build("module.economy.balancetop.unknown-page").send(sender);
                return;
            } else {
                page = Integer.parseInt(context.getArgument(0));
            }
        }

        if(CACHE_AGE < System.currentTimeMillis() - CACHE_TIME && !LOCK) {
            renewCache(sender, page);
        } else {
            outputCache(sender, page);
        }
    }

    @Override
    public boolean hasPermission(SpigotSender sender) {
        return sender.getSender().hasPermission("titan.command.balancetop");
    }

    private void renewCache(SpigotSender sender, int page) {
        LOCK = true;
        TitanSpigot.INSTANCE.getPlugin().getServer().getScheduler()
                .runTaskAsynchronously(TitanSpigot.INSTANCE.getPlugin(), new CacheTask(() -> outputCache(sender, page)));
    }

    private void outputCache(SpigotSender sender, int pageNumber) {
        if(pageNumber > CACHE.total()) {
            MessageHandler.INSTANCE.build("module.economy.balancetop.unknown-page").send(sender);
            return;
        }

        List<Page<BalPlayer>> players = CACHE.page(pageNumber);

        StringBuilder builder = new StringBuilder(MessageHandler.INSTANCE.get("module.economy.balancetop.title")
                .replace("%total%", UNumber.displayCurrencyExactly(TOTAL)))
                .append("&r\n");

        final String build = MessageHandler.INSTANCE.get("module.economy.balancetop.value");
        players.forEach(page -> builder.append(build
                .replace("%position%", page.getNumber() + "")
                .replace("%name%", page.getValue().name)
                .replace("%balance%", UNumber.displayCurrencyExactly(page.getValue().balance)))
                .append("\n"));
        builder.append(pageNumber == CACHE.total()
                ? MessageHandler.INSTANCE.get("module.economy.balancetop.suffix.last")
                : MessageHandler.INSTANCE.get("module.economy.balancetop.suffix.normal")
                .replace("%next-page%", (pageNumber + 1) + ""));

        MessageHandler.INSTANCE.message(builder.toString()).send(sender);
    }

    private class CacheTask implements Runnable {

        private final Runnable callback;

        CacheTask(Runnable callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            new SQLQuery(new DatabaseEconomy(), QUERY).execute(res -> {
                try {
                    List<BalPlayer> players = new ArrayList<>();
                    BigDecimal tempTotal = BigDecimal.ZERO;

                    while(res.next()) {
                        final BigDecimal value = res.getBigDecimal("balance");
                        players.add(new BalPlayer(
                                UUIDCache.INSTANCE.getName(UUID.fromString(res.getString("uuid"))),
                                value));
                        tempTotal = tempTotal.add(value);
                    }

                    CACHE = new Pagination<>(players, 10);
                    TOTAL = tempTotal;
                    LOCK = false;
                    CACHE_AGE = System.currentTimeMillis();
                } catch (SQLException ex) {
                    Titan.INSTANCE.getLogger().info("[Module] [Economy] - BalanceTop cache, failed to load records.", ex);
                }
            });

            this.callback.run();
        }
    }

    private static class BalPlayer {
        private final String name;
        private final BigDecimal balance;

        BalPlayer(String name, BigDecimal balance) {
            this.name = name;
            this.balance = balance;
        }
    }
}
