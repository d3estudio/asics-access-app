package co.ferreri.asicsaccess;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.text.Normalizer;

public class Utils {
    private static String FIRST_TIME = "first_time";

    public static String removeSpecialCharacters(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public static String getOldFormattedDate() {
        return new DateTime().withYear(2000).toString();
    }

    public static String getCurrentFormatedDate() {
        return new DateTime().toString();
    }

    public static String getMidnightFormatedDate() {
        return new DateTime().withTimeAtStartOfDay().toString();
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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void hideKeyboardOnOutsideClick(View view, final Activity activity) {

        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                hideKeyboardOnOutsideClick(innerView, activity);
            }
        }
    }

    public static void showCenteredToast(Context context, String message, int duration){
        Toast toast = Toast.makeText(context, message, duration);
        TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
        if (v != null) v.setGravity(Gravity.CENTER);
        toast.show();
    }
}
