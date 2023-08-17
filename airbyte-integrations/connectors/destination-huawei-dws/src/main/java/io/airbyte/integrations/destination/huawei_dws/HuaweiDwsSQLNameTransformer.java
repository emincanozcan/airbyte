/*
 * Copyright (c) 2023 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.integrations.destination.huawei_dws;

import io.airbyte.integrations.destination.StandardNameTransformer;

public class HuaweiDwsSQLNameTransformer extends StandardNameTransformer {

  @Override
  public String applyDefaultCase(final String input) {
    return input.toLowerCase();
  }

}
