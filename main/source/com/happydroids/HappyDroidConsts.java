/*
 * Copyright (c) 2012. HappyDroids LLC, All rights reserved.
 */

package com.happydroids;

public class HappyDroidConsts {
  public static final String HAPPYDROIDS_SERVER = "local.happydroids.com";
  public static final String HAPPYDROIDS_URI = "http://" + HAPPYDROIDS_SERVER;
  public static final String HAPPYDROIDS_API_KEY = "f88c02844c913c20f84ea29cbabec97c10c828a8caf204de41";
  public static final boolean DEBUG = true;
  public static boolean DISPLAY_DEBUG_INFO = true;
  public static final long HAPPYDROIDS_PING_FREQUENCY = DEBUG ? 5000 : 60000;
  public static String VERSION = "1.0.40";
  public static int VERSION_CODE = 1040;
  public static String GIT_SHA = "f1693f95a52ac8096dba2ed4892cb61f64245ecd";
  public static final byte[] OBFUSCATION_SALT = "ad076e981c2ea4103f1a6e30b5e8d0bd81bca536".getBytes();

  public static final String OBFUSCATION_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr1epMa3vopbqUJAfVe90GqfjfYUQB7Edb5fBUfTyLJ6lXQORZyvpiF+vTtCA0FEHI4jB9V4TMaJcFrnTk5MZDUHi1zkj0cSn9OG7znzEvSFwfJ63b/UWBZIdgx5/bE63Mkv3LL87aNFWlg5TzgR7mQtIxHjP4iP0y4mxJJujt49ArFxYWoIIBZCv0e5zyUtQDLPYfirp3nNUPLg/wW1VNeUutkR+71r6+z/a1MeKMfUzVOoSJisnNhqWhlSkrN4Mlz5ehJhDt/ubf9n0AFafusGnmrdYFwGrOjpWDCkOpLEvkvlZiNV+sshRVaRUCwFKPBbjV/NFsDKlkdgZnms2WwIDAQAB";
  public static final boolean ENABLE_HAPPYDROIDS_CONNECT = true;
  public static final boolean ENABLE_NEWS_TICKER = false;
}