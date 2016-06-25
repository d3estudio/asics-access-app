package co.ferreri.asicsaccess;

import java.text.Normalizer;

public class Utils {
    public static String removeSpecialCharacters(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }
}
