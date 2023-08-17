/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.huawei_dws;

import io.airbyte.db.jdbc.JdbcDatabase;
import io.airbyte.integrations.destination.jdbc.JdbcSqlOperations;
import io.airbyte.protocol.models.v0.AirbyteRecordMessage;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

public class HuaweiDwsSQLOperations extends JdbcSqlOperations {

    public HuaweiDwsSQLOperations() {
        super(new HuaweiDwsDataAdapter());
    }

    @Override
    public void createSchemaIfNotExists(JdbcDatabase database, String schemaName) throws Exception {
        try {
            String existQuery = String.format("SELECT * FROM information_schema.schemata WHERE schema_name = '%s'", schemaName);
            if (database.queryJsons(existQuery).size() == 0) {
                LOGGER.warn("Schema " + schemaName + " is not found! Trying to create a new one.");
                final String query = String.format("CREATE SCHEMA %s", schemaName);
                database.execute(query);
            }
            schemaSet.add(schemaName);
        } catch (final Exception e) {
            throw checkForKnownConfigExceptions(e).orElseThrow(() -> e);
        }
    }

    @Override
    public void insertRecordsInternal(final JdbcDatabase database,
                                      final List<AirbyteRecordMessage> records,
                                      final String schemaName,
                                      final String tmpTableName)
            throws SQLException {
        if (records.isEmpty()) {
            return;
        }

        database.execute(connection -> {
            File tmpFile = null;
            try {
                tmpFile = Files.createTempFile(tmpTableName + "-", ".tmp").toFile();
                writeBatchToFile(tmpFile, records);

                final var copyManager = new CopyManager(connection.unwrap(BaseConnection.class));
                final var sql = String.format("COPY %s.%s FROM stdin DELIMITER ',' CSV", schemaName, tmpTableName);
                final var bufferedReader = new BufferedReader(new FileReader(tmpFile, StandardCharsets.UTF_8));
                copyManager.copyIn(sql, bufferedReader);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (tmpFile != null) {
                        Files.delete(tmpFile.toPath());
                    }
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
