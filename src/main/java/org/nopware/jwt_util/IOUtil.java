package org.nopware.jwt_util;

import com.google.common.base.Charsets;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class IOUtil {
    /**
     * Reads a string from a file or stdin.
     *
     * @param path the path to the file, or null to read from stdin
     * @return the string read from the file or stdin
     * @throws IOException if the file or stdin cannot be read
     */
    public static String readStringFromFileOrStdin(@Nullable Path path) throws IOException {
        if (path != null) {
            return Files.readString(path);
        } else {
            return new String(System.in.readAllBytes(), Charsets.US_ASCII);
        }
    }

    public static byte[] base64Decode(String base64) {
        return java.util.Base64.getDecoder().decode(base64);
    }

    public static String base64Encode(byte[] bytes) {
        return java.util.Base64.getEncoder().encodeToString(bytes);
    }
}
