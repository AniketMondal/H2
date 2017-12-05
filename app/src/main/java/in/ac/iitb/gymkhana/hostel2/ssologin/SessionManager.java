package in.ac.iitb.gymkhana.hostel2.ssologin;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.params.StreamConfigurationMap;

/**
 * Created by bhavesh on 25/11/17.
 */

public class SessionManager {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String PREF_NAME = "SSOLogin";

    private static final String IS_LOGIN = "islogin";
    private static final String KEY_NAME = "name";
    private static final String KEY_LDAPID = "ldapid";
    private static final String KEY_ROLLNO = "rollno";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String ldapid, String rollno){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_LDAPID, ldapid);
        editor.putString(KEY_ROLLNO, rollno);
        editor.commit();
    }

    public String getName() {
        return pref.getString(KEY_NAME, null);
    }

    public String getLDAPid() {
        return pref.getString(KEY_LDAPID, null);
    }

    public String getRollNo() {
        return pref.getString(KEY_ROLLNO,null);
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();
    }

}
