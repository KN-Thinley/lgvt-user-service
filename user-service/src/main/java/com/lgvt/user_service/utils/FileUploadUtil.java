package com.lgvt.user_service.utils;

import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FileUploadUtil {
    public static final long MAX_FILE_SIZE = 1024 * 1024 * 5; // 5MB
    public static final String IMAGE_PATTERN = ".*\\.(jpg|jpeg|png)$";
    public static final String DATE_FORMAT = "yyyyMMddHHmmss";
    public static final String FILE_NAME_PATTERN = "%S_%S";

    public static boolean isAllowedExtension(final String fileName, final String pattern) {
        final Matcher matcher = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(fileName);
        return matcher.matches();
    }

    public static void assertAllowed(MultipartFile file, String pattern) {
        final long size = file.getSize();
        if (size > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of " + MAX_FILE_SIZE + " bytes");
        }

        final String fileName = file.getOriginalFilename();
        final String extension = FilenameUtils.getExtension(fileName);

        if (!isAllowedExtension(fileName, pattern)) {
            throw new IllegalArgumentException("File type" + extension + "not allowed. Allowed types are: " + pattern);
        }
    }

    public static String getFileName(final String name) {
        final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        final String date = dateFormat.format(System.currentTimeMillis());
        return String.format(FILE_NAME_PATTERN, name, date);
    }
}
