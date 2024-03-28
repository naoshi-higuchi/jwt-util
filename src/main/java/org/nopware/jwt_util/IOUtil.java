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
     * @param path the path to the file, or null to read from stdin, or "-" to read from stdin.
     * @return the string read from the file or stdin
     * @throws IOException if the file or stdin cannot be read
     */
    public static String readStringFromFileOrStdin(@Nullable Path path) throws IOException {
        byte[] bytes = readAllBytesFromFileOrStdin(path);
        return new String(bytes, Charsets.UTF_8);
    }

    public static byte[] readAllBytesFromFileOrStdin(@Nullable Path path) throws IOException {
        if (path == null || path.toString().equals("-")) {
            return System.in.readAllBytes();
        } else {
            return Files.readAllBytes(path);
        }
    }

    public static byte[] base64Decode(String base64) {
        return java.util.Base64.getUrlDecoder().decode(base64);
    }

    public static String base64Encode(byte[] bytes) {
        return java.util.Base64.getUrlEncoder().encodeToString(bytes);
    }
}
