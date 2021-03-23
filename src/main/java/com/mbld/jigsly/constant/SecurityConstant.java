package com.mbld.jigsly.constant;

public class SecurityConstant {
    public static final long EXPIRATION_TIME = 5*24*60*60*1000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String JWT_TOKEN_HEADER = "Jwt-Token";
    public static final String[] PUBLIC_URLS = { "/login", "/register", "/lobby/**", "/game/**", "resources/images/**" };
}