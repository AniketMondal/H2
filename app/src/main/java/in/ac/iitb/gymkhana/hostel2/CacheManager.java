package in.ac.iitb.gymkhana.hostel2;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bhavesh on 07/10/17.
 */

public class CacheManager {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    private static final String PREF_NAME = "MyCache";

    private static final String MENU = "menu";
    public static final String NEWS = "news";

    public CacheManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void addMenu(String menu){
        editor.putString(MENU, menu);
        editor.commit();
    }

    public void addNews(String news){
        editor.putString(NEWS, news);
        editor.commit();
    }

    public String getMenu() {
        return pref.getString(MENU, null);
    }

    public String getNews() {
        return pref.getString(NEWS, null);
    }

}
