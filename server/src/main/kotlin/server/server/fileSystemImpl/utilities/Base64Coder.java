package server.server.fileSystemImpl.utilities;

import java.util.Base64;

public class Base64Coder {
    public static String encodeString(String word){
        return Base64.getEncoder().encodeToString(word.getBytes());
    }

    public static String decodeString(String encodedWord){
        byte[] decoded = Base64.getDecoder().decode(encodedWord);
        return new String(decoded);
    }
}
