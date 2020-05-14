package net.thenova.titan.spigot.module.economy.handler;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.thenova.titan.library.util.UNumber;
import net.thenova.titan.spigot.module.economy.user.UserEconomy;
import net.thenova.titan.spigot.users.UserHandler;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.util.ArrayList;
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
public final class VaultEconomy implements Economy {

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Overlord Economy";
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }

    @Override
    public String format(double amount) {
        return UNumber.displayCurrencyExactly(BigDecimal.valueOf(amount));
    }

    @Override
    public String currencyNamePlural() {
        return "";
    }

    @Override
    public String currencyNameSingular() {
        return "";
    }

    /**
     * Account existence checks
     */

    @Override
    public boolean hasAccount(String name) {
        return UserHandler.INSTANCE.playerExists(name);
    }
    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return this.hasAccount(player.getName());
    }
    @Override
    public boolean hasAccount(String name, String world) {
        return this.hasAccount(name);
    }
    @Override
    public boolean hasAccount(OfflinePlayer player, String world) {
        return this.hasAccount(player.getName());
    }

    /**
     * Get account balance
     */

    @Override
    public double getBalance(String name) {
        return !UserHandler.INSTANCE.playerExists(name) ? 0
                : UserHandler.INSTANCE.getUser(name).getModule(UserEconomy.class).getDouble();
    }
    @Override
    public double getBalance(OfflinePlayer player) {
        return this.getBalance(player.getName());
    }
    @Override
    public double getBalance(String name, String world) {
        return this.getBalance(name);
    }
    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return this.getBalance(player.getName());
    }

    /**
     * Balance check
     */

    @Override
    public boolean has(String name, double amount) {
        return !UserHandler.INSTANCE.playerExists(name)
                && UserHandler.INSTANCE.getUser(name).getModule(UserEconomy.class).has(amount);
    }
    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return this.has(player.getName(), amount);
    }
    @Override
    public boolean has(String name, String world, double amount) {
        return this.has(name, amount);
    }
    @Override
    public boolean has(OfflinePlayer player, String world, double amount) {
        return this.has(player.getName(), amount);
    }

    /**
     * Withdraw from account
     */

    @Override
    public EconomyResponse withdrawPlayer(String name, double amount) {
        if(amount < 0.0D) {
            return new EconomyResponse(0.0D,
                    0.0D,
                    EconomyResponse.ResponseType.FAILURE,
                    "Cannot withdraw negative funds");
        }

        if(!UserHandler.INSTANCE.playerExists(name)) {
            return new EconomyResponse(0.0,
                    0.0,
                    EconomyResponse.ResponseType.FAILURE,
                    "User does not exist.");
        } else {
            final UserEconomy econ = UserHandler.INSTANCE.getUser(name).getModule(UserEconomy.class);
            econ.take(amount);
            return new EconomyResponse(amount, econ.getDouble(), EconomyResponse.ResponseType.SUCCESS, null);
        }
    }
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return this.withdrawPlayer(player.getName(), amount);
    }
    @Override
    public EconomyResponse withdrawPlayer(String name, String world, double amount) {
        return this.withdrawPlayer(name, amount);
    }
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String world, double amount) {
        return this.withdrawPlayer(player.getName(), amount);
    }

    /**
     * Deposit to account
     */

    @Override
    public EconomyResponse depositPlayer(String name, double amount) {
        if(amount < 0.0D) {
            return new EconomyResponse(0.0D,
                    0.0D,
                    EconomyResponse.ResponseType.FAILURE,
                    "Cannot withdraw negative funds");
        }

        if(!UserHandler.INSTANCE.playerExists(name)) {
            return new EconomyResponse(0.0,
                    0.0,
                    EconomyResponse.ResponseType.FAILURE,
                    "User does not exist.");
        } else {
            final UserEconomy econ = UserHandler.INSTANCE.getUser(name).getModule(UserEconomy.class);
            econ.give(amount);
            return new EconomyResponse(amount, econ.getDouble(), EconomyResponse.ResponseType.SUCCESS, null);
        }
    }
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return this.depositPlayer(player.getName(), amount);
    }
    @Override
    public EconomyResponse depositPlayer(String name, String world, double amount) {
        return this.depositPlayer(name, amount);
    }
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String world, double amount) {
        return this.depositPlayer(player.getName(), amount);
    }

    /**
     * Account creation
     */

    @Override
    public boolean createPlayerAccount(String name) {
        return false;
    }
    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return this.createPlayerAccount(player.getName());
    }
    @Override
    public boolean createPlayerAccount(String name, String world) {
        return this.createPlayerAccount(name);
    }
    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String world) {
        return this.createPlayerAccount(player.getName());
    }

    /**
     * Bank methods
     * Methods all disabled, Banks are not supported.
     */

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public EconomyResponse createBank(String s, String s1) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse createBank(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse deleteBank(String s) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse bankBalance(String s) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse bankHas(String s, double v) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse bankWithdraw(String s, double v) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse bankDeposit(String s, double v) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse isBankOwner(String s, String s1) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse isBankOwner(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse isBankMember(String s, String s1) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public EconomyResponse isBankMember(String s, OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0.0D, 0.0D, EconomyResponse.ResponseType.NOT_IMPLEMENTED, getName() + " does not support bank accounts!");
    }
    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }
}
