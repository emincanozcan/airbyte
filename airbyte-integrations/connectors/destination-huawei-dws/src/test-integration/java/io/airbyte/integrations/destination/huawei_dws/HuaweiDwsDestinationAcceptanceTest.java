/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.huawei_dws;

import com.fasterxml.jackson.databind.JsonNode;
import io.airbyte.integrations.standardtest.destination.DestinationAcceptanceTest;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HuaweiDwsDestinationAcceptanceTest extends DestinationAcceptanceTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(HuaweiDwsDestinationAcceptanceTest.class);

  private JsonNode configJson;

  @Override
  protected String getImageName() {
    return "airbyte/destination-huawei_dws:dev";
  }

  @Override
  protected JsonNode getConfig() {
    // TODO: Generate the configuration JSON file to be used for running the destination during the test
    // configJson can either be static and read from secrets/config.json directly
    // or created in the setup method
    return configJson;
  }

  @Override
  protected JsonNode getFailCheckConfig() {
    // TODO return an invalid config which, when used to run the connector's check connection operation,
    // should result in a failed connection check
    return null;
  }

  @Override
  protected List<JsonNode> retrieveRecords(TestDestinationEnv testEnv,
                                           String streamName,
                                           String namespace,
                                           JsonNode streamSchema)
      throws IOException {
    // TODO Implement this method to retrieve records which written to the destination by the connector.
    // Records returned from this method will be compared against records provided to the connector
    // to verify they were written correctly
    return null;
  }

  @Override
  protected void setup(TestDestinationEnv testEnv, HashSet<String> TEST_SCHEMAS) throws Exception {

  }

  @Override
  protected void tearDown(TestDestinationEnv testEnv, HashSet<String> TEST_SCHEMAS) throws Exception {

  }


}
