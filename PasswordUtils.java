package miniproject;

import java.security.MessageDigest;

public class PasswordUtils {
    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");//secure hash algo(256 bit,32 bytes)
            byte[] b = md.digest(input.getBytes("UTF-8"));//convert into byte
            StringBuilder sb = new StringBuilder();//abc-ba7816
            for (byte x : b) sb.append(String.format("%02x", x));//to digit hexadecimal value
            return sb.toString();//returns final hashed pswrd
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verify(String plain, String hashed) {
        return hash(plain).equals(hashed);//matches-login if not no
    }
}
