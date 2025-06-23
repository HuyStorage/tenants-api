package com.tenant.api.dto;


import lombok.Data;

@Data
public class ErrorCode {

    /**
     * Starting error code Service Category
     */
    public static final String CATEGORY_ERROR_NOT_FOUND = "ERROR-CATEGORY-ERROR-0000";
    public static final String CATEGORY_ERROR_NAME_EXISTED = "ERROR-CATEGORY-ERROR-0002";
    public static final String CATEGORY_ERROR_HAS_MOVIE = "ERROR-CATEGORY-ERROR-0003";

    /**
     * Starting error code Employee
     */
    public static final String ACCOUNT_ERROR_NOT_FOUND = "ERROR-ACCOUNT-ERROR-0000";
    public static final String ACCOUNT_ERROR_USERNAME_EXISTED = "ERROR-ACCOUNT-ERROR-0002";
    public static final String ACCOUNT_ERROR_PHONE_EXISTED = "ERROR-ACCOUNT-ERROR-0003";
    public static final String ACCOUNT_ERROR_EMAIL_EXISTED = "ERROR-ACCOUNT-ERROR-0004";
    public static final String ACCOUNT_ERROR_WRONG_PASSWORD = "ERROR-ACCOUNT-ERROR-0005";
    public static final String ACCOUNT_ERROR_NEW_PASSWORD_SAME_OLD_PASSWORD = "ERROR-ACCOUNT-ERROR-0006";
    public static final String ACCOUNT_ERROR_LOOKED = "ERROR-ACCOUNT-ERROR-0007";

    /**
     * Starting error code Employee
     */
    public static final String EMPLOYEE_ERROR_NOT_FOUND = "ERROR-EMPLOYEE-ERROR-0000";
    public static final String EMPLOYEE_ERROR_USERNAME_EXISTED = "ERROR-EMPLOYEE-ERROR-0002";
    public static final String EMPLOYEE_ERROR_PHONE_EXISTED = "ERROR-EMPLOYEE-ERROR-0003";
    public static final String EMPLOYEE_ERROR_EMAIL_EXISTED = "ERROR-EMPLOYEE-ERROR-0004";
    public static final String EMPLOYEE_ERROR_WRONG_PASSWORD = "ERROR-EMPLOYEE-ERROR-0005";
    public static final String EMPLOYEE_ERROR_NEW_PASSWORD_SAME_OLD_PASSWORD = "ERROR-EMPLOYEE-ERROR-0006";

    /**
     * Starting error code User
     */
    public static final String USER_ERROR_NOT_FOUND = "ERROR-USER-ERROR-0000";
    public static final String USER_ERROR_USERNAME_EXISTED = "ERROR-USER-ERROR-0002";
    public static final String USER_ERROR_PHONE_EXISTED = "ERROR-USER-ERROR-0003";
    public static final String USER_ERROR_EMAIL_EXISTED = "ERROR-USER-ERROR-0004";
    public static final String USER_ERROR_WRONG_PASSWORD = "ERROR-USER-ERROR-0005";
    public static final String USER_ERROR_NEW_PASSWORD_SAME_OLD_PASSWORD = "ERROR-USER-ERROR-0006";

    /**
     * Starting error code Group
     */
    public static final String GROUP_ERROR_NOT_FOUND = "ERROR-GROUP-ERROR-0001";
    public static final String GROUP_ERROR_NAME_EXISTED = "ERROR-GROUP-ERROR-0002";

    /**
     * Starting error code Permission
     */
    public static final String PERMISSION_ERROR_NOT_FOUND = "ERROR-PERMISSION-ERROR-0001";
    public static final String PERMISSION_ERROR_NAME_EXISTED = "ERROR-PERMISSION-ERROR-0002";
    public static final String PERMISSION_ERROR_CODE_EXISTED = "ERROR-PERMISSION-ERROR-0003";

    /**
     * Starting error code Video Library
     */
    public static final String VIDEO_LIBRARY_ERROR_NOT_FOUND = "ERROR-VIDEO-LIBRARY-ERROR-0000";
    public static final String VIDEO_LIBRARY_ERROR_NAME_EXISTED = "ERROR-VIDEO-LIBRARY-ERROR-0002";

    /**
     * Starting error code Movie
     */
    public static final String MOVIE_ERROR_NOT_FOUND = "ERROR-MOVIE-ERROR-0000";
    public static final String MOVIE_ERROR_SLUG_EXISTED = "ERROR-MOVIE-ERROR-0002";
    public static final String MOVIE_ERROR_HAS_ITEM = "ERROR-MOVIE-ERROR-0003";

    /**
     * Starting error code Movie Item
     */
    public static final String MOVIE_ITEM_ERROR_NOT_FOUND = "ERROR-MOVIE-ITEM-ERROR-0000";
    public static final String MOVIE_ITEM_ERROR_PARENT_REQUIRED = "ERROR-MOVIE-ITEM-ERROR-0002";
    public static final String MOVIE_ITEM_ERROR_VIDEO_REQUIRED = "ERROR-MOVIE-ITEM-ERROR-0003";
    public static final String MOVIE_ITEM_ERROR_KIND_INVALID = "ERROR-MOVIE-ITEM-ERROR-0004";
    public static final String MOVIE_ITEM_ERROR_INVALID_REQUEST = "ERROR-MOVIE-ITEM-ERROR-0005";

    /**
     * Starting error code Person
     */
    public static final String PERSON_ERROR_NOT_FOUND = "ERROR-PERSON-ERROR-0000";
    public static final String PERSON_ERROR_MOVIE_PERSON_EXISTED = "ERROR-PERSON-ERROR-0001";
    public static final String PERSON_ERROR_NOT_HAVE_KIND = "ERROR-PERSON-ERROR-0002";

    /**
     * Starting error code Movie Person
     */
    public static final String MOVIE_PERSON_ERROR_NOT_FOUND = "ERROR-MOVIE-PERSON-ERROR-0000";
    public static final String MOVIE_PERSON_ERROR_KIND_INVALID = "ERROR-MOVIE-PERSON-ERROR-0001";
    public static final String MOVIE_PERSON_ERROR_INVALID_REQUEST = "ERROR-MOVIE-PERSON-ERROR-0002";
}
