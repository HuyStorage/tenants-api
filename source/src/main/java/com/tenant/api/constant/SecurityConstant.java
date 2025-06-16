package com.tenant.api.constant;

public class SecurityConstant {
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_EMPLOYEE = "employee";
    public static final String GRANT_TYPE_POS = "pos";
    public static final String GRANT_TYPE_VIEW = "view";

    public static final Integer USER_KIND_ADMIN = 1;
    public static final Integer USER_KIND_MANAGER = 2;
    public static final Integer USER_KIND_EMPLOYEE = 3;
    public static final Integer USER_KIND_USER = 4;

    private SecurityConstant() {
        throw new IllegalStateException("Utility class");
    }
}
