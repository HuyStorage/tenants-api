package com.tenant.api.constant;

public class BaseConstant {

    public static final Integer USER_KIND_ADMIN = 1;
    public static final Integer USER_KIND_MANAGER = 2;
    public static final Integer USER_KIND_EMPLOYEE = 3;
    public static final Integer USER_KIND_USER = 4;

    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_PENDING = 0;
    public static final Integer STATUS_LOCK = -1;
    public static final Integer STATUS_DELETE = -2;

    public static final Integer MOVIE_TYPE_SINGLE = 1;
    public static final Integer MOVIE_TYPE_SERIES = 2;

    public static final Integer MOVIE_ITEM_KIND_SEASON = 1;
    public static final Integer MOVIE_ITEM_KIND_EPISODE = 2;
    public static final Integer MOVIE_ITEM_KIND_TRAILER = 3;

    public static final Integer AGE_RATING_GENERAL = 1;   // G - General Audience
    public static final Integer AGE_RATING_PG = 2;        // PG - Parental Guidance
    public static final Integer AGE_RATING_PG13 = 3;      // PG-13 - Not under 13
    public static final Integer AGE_RATING_R = 4;         // R - Restricted (under 17 needs adult)
    public static final Integer AGE_RATING_NC17 = 5;      // NC-17 - No one 17 and under admitted
    public static final Integer AGE_RATING_18_PLUS = 6;   // 18+ - Local classification

    public static final String APP_ID_GENERATOR_NAME = "idGenerator";
    public static final String APP_ID_GENERATOR_STRATEGY = "com.tenant.api.storage.id.IdGenerator";

    public static final String USERNAME_PATTERN = "^(?=.{3,20}$)(?!.*[_.]{2})[a-zA-Z][a-zA-Z0-9_]*[a-zA-Z0-9]$";
    public static final String PHONE_PATTERN = "^0[35789][0-9]{8}$";
    public static final String EMAIL_PATTERN = "^(?!.*[.]{2,})[a-zA-Z0-9.%]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    private BaseConstant(){
        throw new IllegalStateException("Utility class");
    }

}
