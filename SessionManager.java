package com.guruinfo.scm.common;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.guruinfo.scm.LoginActivity;
import com.guruinfo.scm.common.utils.Sharedpref;
import java.util.HashMap;
@SuppressLint("InlinedApi")
public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "SessionLogin";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String ID = "id";
    public static final String UTYPE = "type";
    public static final String NAME = "name";
    public static final String STATUS = "status";
    public static final String CR_ID = "cr_id";
    public static final String USER_NAME = "userName";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_CHATKEY = "chatKey";
    public static final String FETCHED = "keys";
    public static final String LAST_ID = "lastLogin";
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void createLoginSession(String id, String name, String status, String cr_id, String userName, String userEmail, String chatKey, String fetched) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(ID, id);
//        editor.putString(UTYPE, type);
        editor.putString(NAME, name);
        editor.putString(STATUS, status);
        editor.putString(CR_ID, cr_id);
        editor.putString(USER_NAME, userName);
        editor.putString(USER_EMAIL, userEmail);
        editor.putString(USER_CHATKEY, chatKey);
        editor.putString(FETCHED, fetched);
        editor.commit();
    }
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(ID, pref.getString(ID, null));
        user.put(UTYPE, pref.getString(UTYPE, null));
        user.put(NAME, pref.getString(NAME, null));
        user.put(STATUS, pref.getString(STATUS, null));
        user.put(CR_ID, pref.getString(CR_ID, null));
        user.put(USER_NAME, pref.getString(USER_NAME, null));
        user.put(USER_EMAIL, pref.getString(USER_EMAIL, null));
        user.put(USER_CHATKEY, pref.getString(USER_CHATKEY, null));
        user.put(FETCHED, pref.getString(FETCHED, null));
        return user;
    }
   /* public void logoutUser() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, DashBoardActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }*/
    public void logoutUserAct(Context context) {
        editor.clear();
        editor.commit();
        Sharedpref.setPrefBoolean(context, "islogin", false);
        Sharedpref.setPrefBoolean(context, "USER_LOGGED_IN", false);
        Sharedpref.SetPrefString(context, "type", "");
        Sharedpref.SetPrefString(context, "UserType", "");
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}