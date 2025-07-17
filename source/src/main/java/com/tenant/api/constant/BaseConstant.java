package com.tenant.api.constant;

public class BaseConstant {

    public static final Integer USER_KIND_ADMIN = 1;
    public static final Integer USER_KIND_MANAGER = 2;
    public static final Integer USER_KIND_EMPLOYEE = 3;
    public static final Integer USER_KIND_USER = 10;
    public static final Integer USER_KIND_USER_VIP = 11;

    public static final Integer LOGIN_ROLE_EMPLOYEE = 1;
    public static final Integer LOGIN_ROLE_USER = 2;

    public static final int PLATFORM_IOS = 1;
    public static final int PLATFORM_ANDROID = 2;

    public static final String GRANT_TYPE_EMPLOYEE = "employee";
    public static final String GRANT_TYPE_USER = "user";

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
    public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>]).{8,}$";

    public static final Integer GENDER_MALE = 1;
    public static final Integer GENDER_FEMALE = 2;
    public static final Integer GENDER_OTHER = 3;

    public static final Integer PERSON_KIND_ACTOR = 1;
    public static final Integer PERSON_KIND_DIRECTOR = 2;

    public static final Integer VIDEO_LIBRARY_STATE_PROCESSING = 0;
    public static final Integer VIDEO_LIBRARY_STATE_READY = 1;
    public static final Integer VIDEO_LIBRARY_STATE_ERROR = 2;

    public static final String CMD_UPDATE_VIDEO = "CMD_UPDATE_VIDEO";
    public static final String CMD_CONVERT_VIDEO = "CMD_CONVERT_VIDEO";
    public static final String CMD_DELETE_VIDEO = "CMD_DELETE_VIDEO";

    public static final Boolean SIDEBAR_ACTIVE_TRUE = true;
    public static final boolean SIDEBAR_ACTIVE_FALSE = false;

    public static final Integer REACTION_TYPE_LIKE = 1;
    public static final Integer REACTION_TYPE_DISLIKE = 2;

    private BaseConstant(){
        throw new IllegalStateException("Utility class");
    }

}
