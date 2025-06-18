package com.tenant.api.dto;


import lombok.Data;

@Data
public class ErrorCode {

    /**
     * Starting error code Service Category
     */
    public static final String CATEGORY_ERROR_NOT_FOUND = "ERROR-CATEGORY-ERROR-0000";
    public static final String CATEGORY_ERROR_NAME_EXISTED = "ERROR-CATEGORY-ERROR-0002";

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
}
