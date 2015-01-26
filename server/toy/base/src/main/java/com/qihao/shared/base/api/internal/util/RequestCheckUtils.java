package com.qihao.shared.base.api.internal.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import com.qihao.shared.base.api.GeneralApiException;
import com.qihao.shared.base.api.FileItem;


public class RequestCheckUtils {
    public static final String ERROR_CODE_ARGUMENTS_MISS    = "400"; // Missing
    // Required
    // Arguments
    public static final String ERROR_CODE_ARGUMENTS_INVALID = "400"; // Invalid

    // Arguments

    public static void checkNotEmpty(Object value, String fieldName) throws GeneralApiException {
        if (value == null) {
            throw new GeneralApiException(ERROR_CODE_ARGUMENTS_MISS, "Missing Required Arguments:" + fieldName + "");
        }
        if (value instanceof String) {
            if (((String) value).trim().length() == 0) {
                throw new GeneralApiException(ERROR_CODE_ARGUMENTS_MISS, "Missing Required Arguments:" + fieldName + "");
            }
        }
    }

    public static void checkMaxLength(String value, int maxLength, String fieldName) throws GeneralApiException {
        if (value != null) {
            if (value.length() > maxLength) {
                throw new GeneralApiException(ERROR_CODE_ARGUMENTS_INVALID, "Invalid Arguments:the length of "
                        + fieldName + " can not be larger than " + maxLength + ".");
            }
        }
    }

    public static void checkMaxLength(FileItem fileItem, int maxLength, String fieldName) throws GeneralApiException {
        try {
            if (fileItem != null && fileItem.getContent() != null) {

                if (fileItem.getContent().length > maxLength) {
                    throw new GeneralApiException(ERROR_CODE_ARGUMENTS_INVALID, "Invalid Arguments:the length of "
                            + fieldName + " can not be larger than " + maxLength + ".");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkMaxListSize(String value, int maxSize, String fieldName) throws GeneralApiException {
        if (value != null) {
            String[] list = value.split(",");
            if (list != null && list.length > maxSize) {
                throw new GeneralApiException(ERROR_CODE_ARGUMENTS_INVALID,
                        "Invalid Arguments:the listsize(the string split by \",\") of " + fieldName
                                + " must be less than " + maxSize + ".");
            }
        }
    }

    public static void checkMaxValue(Long value, long maxValue, String fieldName) throws GeneralApiException {
        if (value != null) {
            if (value > maxValue) {
                throw new GeneralApiException(ERROR_CODE_ARGUMENTS_INVALID, "Invalid Arguments:the value of "
                        + fieldName + " can not be larger than " + maxValue + ".");
            }
        }
    }

    public static void checkMinValue(Long value, long minValue, String fieldName) throws GeneralApiException {
        if (value != null) {
            if (value < minValue) {
                throw new GeneralApiException(ERROR_CODE_ARGUMENTS_INVALID, "Invalid Arguments:the value of "
                        + fieldName + " can not be less than " + minValue + ".");
            }
        }
    }

    public static <T> void checkInSet(T value, Set<T> keySet, String fieldName) throws GeneralApiException {
        if (value == null || !keySet.contains(value)) {
            throw new GeneralApiException(ERROR_CODE_ARGUMENTS_INVALID, "Invalid Arguments:the value of " + fieldName
                    + " can not in default list.");
        }
    }

    public static <T> void checkInHashMap(T value, HashMap<T, ?> hashMap, String fieldName) throws GeneralApiException {
        if (value == null || !hashMap.containsKey(value)) {
            throw new GeneralApiException(ERROR_CODE_ARGUMENTS_INVALID, "Invalid Arguments:the value of " + fieldName
                    + " can not in default list.");
        }
    }

    public static <T> void checkIPConsistence(String ipValue, String fieldName) throws GeneralApiException {
        String ipSeparator = "-";
        String separator = ".";
        String[] iPList = ipValue.split(ipSeparator);
        int preEndIndex = iPList[0].lastIndexOf(separator);
        int nextEndIndex = iPList[1].lastIndexOf(separator);

        if (iPList.length != 2 || !iPList[0].substring(0, preEndIndex).equals(iPList[1].substring(0, nextEndIndex))) {
            throw new GeneralApiException(ERROR_CODE_ARGUMENTS_INVALID, "Invalid Arguments:the value of " + fieldName
                    + " can not in default list.");
        }

    }
}
