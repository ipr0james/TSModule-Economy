package net.thenova.titan.spigot.module.economy.user;

import net.thenova.titan.library.Titan;
import net.thenova.titan.library.database.sql.SQLQuery;
import net.thenova.titan.spigot.module.economy.database.DatabaseEconomy;
import net.thenova.titan.spigot.module.economy.database.tables.DBTableEconomy;
import net.thenova.titan.spigot.module.economy.handler.EconomyHandler;
import net.thenova.titan.spigot.users.user.module.UserModule;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.SQLException;

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
public final class UserEconomy extends UserModule {

    private static final MathContext MATH_CONTEXT = MathContext.DECIMAL128;

    private String table;
    private BigDecimal balance;

    @Override
    public void load() {
        this.table = new DBTableEconomy().getName();
        new SQLQuery(new DatabaseEconomy(), "INSERT IGNORE INTO `" + this.table + "` (`uuid`, `balance`) VALUES (?, ?)",
                    this.user.getUUID().toString(), EconomyHandler.INSTANCE.getDefaultBalance())
                .execute(() ->
                        new SQLQuery(new DatabaseEconomy(), "SELECT `balance` FROM `" + this.table + "` WHERE `uuid` = ?",
                                    this.user.getUUID().toString())
                                .execute(res -> {
                                    try {
                                        while(res.next()) {
                                            this.balance = res.getBigDecimal("balance");
                                        }
                                    } catch (final SQLException ex) {
                                        Titan.INSTANCE.getLogger().info("[Module-Economy] [UserEconomy] - Failed to load user balance", ex);
                                    }
                                }));
    }

    /**
     *
     *
     * @param amount
     * @return
     */
    public boolean has(double amount) {
        return this.has(BigDecimal.valueOf(amount));
    }
    public boolean has(BigDecimal amount) {
        return amount.compareTo(this.balance) <= 0;
    }

    /**
     *
     *
     * @param amount
     */
    public void give(double amount) {
        this.give(BigDecimal.valueOf(amount));
    }
    public void give(BigDecimal amount) {
        this.balance = this.balance.add(amount, MATH_CONTEXT);

        new SQLQuery(new DatabaseEconomy(),"UPDATE `" + this.table + "` SET `balance` = ? WHERE `uuid` = ?",
                    this.balance, this.user.getUUID())
                .execute();
    }

    /**
     *
     *
     * @param amount
     */
    public void take(double amount) {
        this.take(BigDecimal.valueOf(amount));
    }
    public void take(BigDecimal amount) {
        if(!has(amount)) {
            this.balance = new BigDecimal(0);
        } else {
            this.balance = this.balance.subtract(amount, MATH_CONTEXT);
        }

        new SQLQuery(new DatabaseEconomy(),"UPDATE `" + this.table + "` SET `balance` = ? WHERE `uuid` = ?",
                    this.balance, this.user.getUUID())
                .execute();
    }

    /**
     *
     * @param amount
     */
    public void set(BigDecimal amount) {
        this.balance = amount;
        
        new SQLQuery(new DatabaseEconomy(),"UPDATE `" + this.table + "` SET `balance` = ? WHERE `uuid` = ?",
                    this.balance, this.user.getUUID())
                .execute();
    }

    /**
     *
     * @return
     */
    public BigDecimal getBalance() {
        return this.balance;
    }
    public double getDouble() {
        double amount = this.balance.doubleValue();

        if (new BigDecimal(amount).compareTo(this.balance) > 0) {
            // closest double is bigger than the exact amount
            // -> get the previous double value to not return more money than the user has
            amount = Math.nextAfter(amount, Double.NEGATIVE_INFINITY);
        }

        return amount;
    }
}
