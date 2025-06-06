package com.tenant.api.constant;

public class BaseConstant {

    public static final Integer USER_KIND_ADMIN = 1;
    public static final Integer USER_KIND_MANAGER = 2;

    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_PENDING = 0;
    public static final Integer STATUS_LOCK = -1;
    public static final Integer STATUS_DELETE = -2;

    public static final String APP_ID_GENERATOR_NAME = "idGenerator";
    public static final String APP_ID_GENERATOR_STRATEGY = "com.tenant.api.storage.id.IdGenerator";

    private BaseConstant(){
        throw new IllegalStateException("Utility class");
    }

}
