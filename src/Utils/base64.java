package Utils;

import java.util.Base64;

public class base64 {

    public static byte[] base64Decode_Strtoui8(String base64encodedString)
    {

        return Base64.getDecoder().decode(base64encodedString);

    }
    public static String base64Encode_ui8toStr(byte[] StringBytes)
    {

        return Base64.getEncoder().encodeToString(StringBytes);
    }
}
