package com.greentechpay.notificationservice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponseMessage {
    public static final String SUCCESS = "Success Sending Notification";
    public static final String ERROR = "Error Sending Notification";
    public static final String NOTIFICATION_ID_IS_NOT_EXIST = "notification could not find by id: ";
    public static final String USER_IS_NOT_FOUND = "user could not find by id: ";
}
