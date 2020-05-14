package net.thenova.titan.spigot.module.economy.database.tables;

import net.thenova.titan.library.database.sql.table.DatabaseTable;
import net.thenova.titan.library.database.sql.table.column.TableColumn;
import net.thenova.titan.library.database.sql.table.column.data_type.BigInt;
import net.thenova.titan.library.database.sql.table.column.data_type.VarChar;
import net.thenova.titan.spigot.module.economy.database.DatabaseEconomy;

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
public class DBTableEconomy extends DatabaseTable {

    public DBTableEconomy() {
        super("economy_users", new DatabaseEconomy());
    }

    @Override
    public void init() {
        registerColumn(
                new TableColumn("uuid", new VarChar(VarChar.LENGTH_UUID)).setPrimary(),
                new TableColumn("balance", new BigInt()).setDefault(BigDecimal.ZERO)
        );
    }
}
