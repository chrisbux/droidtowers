/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids.server;

import com.happydroids.HttpTestHelper;
import org.apache.http.HttpResponse;

public class TestHappyDroidService extends HappyDroidService {
  public TestHappyDroidService() {
    super(13);
    hasNetworkConnection = true;
  }

  @Override
  public HttpResponse makeGetRequest(String uri) {
    return HttpTestHelper.instance().makeGetRequest(uri);
  }

  @Override
  public HttpResponse makePostRequest(String uri, Object forServerDoNotCare) {
    return HttpTestHelper.instance().makePostRequest(uri, forServerDoNotCare);
  }

  public static void disableNetworkConnection() {
    hasNetworkConnection = false;
  }

  @Override
  public void withNetworkConnection(Runnable runnable) {
    if (hasNetworkConnection) {
      runnable.run();
    }
  }
}