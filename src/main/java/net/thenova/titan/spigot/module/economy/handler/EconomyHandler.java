package net.thenova.titan.spigot.module.economy.handler;

import net.milkbowl.vault.economy.Economy;
import net.thenova.titan.library.file.FileHandler;
import net.thenova.titan.library.file.json.JSONFile;
import net.thenova.titan.spigot.TitanSpigot;
import net.thenova.titan.spigot.plugin.users.modules.UserValidation;
import net.thenova.titan.spigot.users.UserHandler;
import net.thenova.titan.spigot.users.user.User;
import org.bukkit.plugin.ServicePriority;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

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
public enum EconomyHandler {
    INSTANCE;

    private BigDecimal default_balance;
    private BigDecimal minimum_value;

    public final void load() {
        JSONFile file = FileHandler.INSTANCE.loadJSONFile(EconomyDataFile.class);

        this.default_balance = BigDecimal.valueOf(file.get("default-balance", Double.class));
        this.minimum_value = BigDecimal.valueOf(file.get("minimum-value", Double.class));

        TitanSpigot.INSTANCE.getPlugin().getServer().getServicesManager()
                .register(Economy.class, new VaultEconomy(), TitanSpigot.INSTANCE.getPlugin(), ServicePriority.Highest);
    }

    /**
     * Return a list of users who are valid to be paid.
     * Exclude vanished users so they are not shown.
     *
     * @param sender - Person executing the command
     * @return - Return list of valid users.
     */
    public List<User> getValidUsers(User sender) {
        return UserHandler.INSTANCE.getOnlineUsers().stream().filter(other ->
                !other.getModule(UserValidation.class).contains("module_vanish")
                        && !sender.getUUID().equals(other.getUUID())).collect(Collectors.toList());
    }

    public BigDecimal getDefaultBalance() {
        return this.default_balance;
    }
    public BigDecimal getMinimumValue() {
        return this.minimum_value;
    }
}
