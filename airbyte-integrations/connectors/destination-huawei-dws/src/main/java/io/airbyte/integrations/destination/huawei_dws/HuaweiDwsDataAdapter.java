/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.huawei_dws;

import io.airbyte.commons.json.Jsons;
import io.airbyte.integrations.destination.jdbc.DataAdapter;

public class HuaweiDwsDataAdapter extends DataAdapter {

  public HuaweiDwsDataAdapter() {
    super(jsonNode -> jsonNode.isTextual() && jsonNode.textValue().contains("\u0000"),
        jsonNode -> {
          final String textValue = jsonNode.textValue().replaceAll("\\u0000", "");
          return Jsons.jsonNode(textValue);
        });
  }

}
