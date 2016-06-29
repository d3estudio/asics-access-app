package co.ferreri.asicsaccess;

import android.content.Context;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;

import org.joda.time.DateTime;

import java.text.Normalizer;

public class Utils {
    private static String FIRST_TIME = "first_time";

    public static String removeSpecialCharacters(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String getOldFormattedDate() {
        return new DateTime().withYear(2000).toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String getCurrentFormatedDate() {
        return new DateTime().toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String getMidnightFormatedDate() {
        return new DateTime().withTimeAtStartOfDay().toString("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    }

    public static String getCellPhoneId(Context context) {
        TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        return mngr.getDeviceId();
    }

    public static void storePreferenceDate(Context context, String key, String date) {

        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putString(key, date)
                .commit();
    }

    public static String getStoredDate(Context context, String key) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(key, getOldFormattedDate());
    }

    public static void storeIsInitial(Context context) {
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(FIRST_TIME, false)
                .commit();
    }

    public static boolean getIsInitial(Context context) {
        return PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(FIRST_TIME, true);
    }
}
