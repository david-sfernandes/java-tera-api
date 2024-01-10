package com.terabyte.teraapi.utils;

import org.json.JSONObject;

import lombok.Data;

@Data
public class BitGroupsReqBody {
  JSONObject body = new JSONObject();

  public BitGroupsReqBody() {
    body.put("params", new JSONObject().put("parentId", "55faa46e3a621503728b457c"));
    body.put("jsonrpc", "2.0");
    body.put("method", "getCustomGroupsList");
    body.put("id", "301f7b05-ec02-481b-9ed6-c07b97de2b7b");
  }
}
