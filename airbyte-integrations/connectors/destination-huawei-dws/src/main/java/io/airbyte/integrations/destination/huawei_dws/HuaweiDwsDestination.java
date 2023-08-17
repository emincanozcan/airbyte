/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.huawei_dws;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.airbyte.commons.json.Jsons;
import io.airbyte.db.factory.DatabaseDriver;
import io.airbyte.db.jdbc.JdbcUtils;
import io.airbyte.integrations.base.Destination;
import io.airbyte.integrations.base.IntegrationRunner;
import io.airbyte.integrations.base.ssh.SshWrappedDestination;
import io.airbyte.integrations.destination.jdbc.AbstractJdbcDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static io.airbyte.integrations.util.PostgresSslConnectionUtils.*;

public class HuaweiDwsDestination extends AbstractJdbcDestination implements Destination {


    private static final Logger LOGGER = LoggerFactory.getLogger(HuaweiDwsDestination.class);

    public static final String DRIVER_CLASS = DatabaseDriver.POSTGRESQL.getDriverClassName();

    public static Destination sshWrappedDestination() {
        return new SshWrappedDestination(new HuaweiDwsDestination(), JdbcUtils.HOST_LIST_KEY, JdbcUtils.PORT_LIST_KEY);
    }

    public HuaweiDwsDestination() {
        super(DRIVER_CLASS, new HuaweiDwsSQLNameTransformer(), new HuaweiDwsSQLOperations());
    }

    @Override
    protected Map<String, String> getDefaultConnectionProperties(final JsonNode config) {
        final Map<String, String> additionalParameters = new HashMap<>();
        if (!config.has(PARAM_SSL) || config.get(PARAM_SSL).asBoolean()) {
            if (config.has(PARAM_SSL_MODE)) {
                if (DISABLE.equals(config.get(PARAM_SSL_MODE).get(PARAM_MODE).asText())) {
                    additionalParameters.put("sslmode", DISABLE);
                } else {
                    additionalParameters.putAll(obtainConnectionOptions(config.get(PARAM_SSL_MODE)));
                }
            } else {
                additionalParameters.put(JdbcUtils.SSL_KEY, "true");
                additionalParameters.put("sslmode", "require");
            }
        }
        return additionalParameters;
    }

    @Override
    public JsonNode toJdbcConfig(final JsonNode config) {
        final String schema = Optional.ofNullable(config.get(JdbcUtils.SCHEMA_KEY)).map(JsonNode::asText).orElse("public");

        String encodedDatabase = config.get(JdbcUtils.DATABASE_KEY).asText();
        if (encodedDatabase != null) {
            try {
                encodedDatabase = URLEncoder.encode(encodedDatabase, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // Should never happen
                e.printStackTrace();
            }
        }
        final String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?",
                config.get(JdbcUtils.HOST_KEY).asText(),
                config.get(JdbcUtils.PORT_KEY).asText(),
                encodedDatabase);

        final ImmutableMap.Builder<Object, Object> configBuilder = ImmutableMap.builder()
                .put(JdbcUtils.USERNAME_KEY, config.get(JdbcUtils.USERNAME_KEY).asText())
                .put(JdbcUtils.JDBC_URL_KEY, jdbcUrl)
                .put(JdbcUtils.SCHEMA_KEY, schema);

        if (config.has(JdbcUtils.PASSWORD_KEY)) {
            configBuilder.put(JdbcUtils.PASSWORD_KEY, config.get(JdbcUtils.PASSWORD_KEY).asText());
        }

        if (config.has(JdbcUtils.JDBC_URL_PARAMS_KEY)) {
            configBuilder.put(JdbcUtils.JDBC_URL_PARAMS_KEY, config.get(JdbcUtils.JDBC_URL_PARAMS_KEY).asText());
        }

        return Jsons.jsonNode(configBuilder.build());
    }

    public static void main(final String[] args) throws Exception {
        final Destination destination = HuaweiDwsDestination.sshWrappedDestination();
        LOGGER.info("starting destination: {}", HuaweiDwsDestination.class);
        new IntegrationRunner(destination).run(args);
        LOGGER.info("completed destination: {}", HuaweiDwsDestination.class);
    }

}
