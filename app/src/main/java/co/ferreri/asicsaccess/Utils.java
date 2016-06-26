package co.ferreri.asicsaccess;

import org.joda.time.DateTime;

import java.text.Normalizer;

public class Utils {
    public static String removeSpecialCharacters(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String getOldFormatedDate(){
        return new DateTime().withYear(2000).toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String getCurrentFormatedDate(){
        return new DateTime().toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }
}
