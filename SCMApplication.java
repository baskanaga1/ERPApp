package com.guruinfo.scm.common;
import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import com.guruinfo.scm.DaoMaster;
import com.guruinfo.scm.DaoSession;
import com.guruinfo.scm.common.db.SCMDataBaseOpenHelper;
import org.greenrobot.greendao.database.Database;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static com.guruinfo.scm.common.AppContants.DB_PWD;
/**
 * Created by Kannan G on 01-Apr-16.
 */
public class SCMApplication extends Application {
    private static Context context;
    public static DaoSession daoSession;
    private static SCMApplication mInstance;
    public static DaoMaster.DevOpenHelper helper;
    public static Database db;
    public static Database readableDb;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public static final boolean ENCRYPTED = true;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        context = getApplicationContext();
        if (permissionHandle()) {
            setupDatabase();
        }
    }
    public void setupDatabase() {
       /* File path = new File(Environment.getExternalStorageDirectory(), "EAP/data/SCM");
        path.getParentFile().mkdirs();
        SCMDataBaseOpenHelper SCMDatabaseOpenHelper = new SCMDataBaseOpenHelper(this, path.getAbsolutePath(), null);
        db = SCMDatabaseOpenHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();*/
        File path = new File(Environment.getExternalStorageDirectory(), "EAP/data/SCM");
        path.getParentFile().mkdirs();
        helper = new DaoMaster.DevOpenHelper(this, path.getAbsolutePath(), null);
        db = helper.getEncryptedWritableDb(DB_PWD);
        readableDb = helper.getEncryptedReadableDb(DB_PWD);
        daoSession = new DaoMaster(db).newSession();
    }
    public static synchronized SCMApplication getInstance() {
        return mInstance;
    }
    public DaoSession getDaoSession() {
        if (daoSession == null)
            if (permissionHandle()) {
                setupDatabase();
            }
        return daoSession;
    }
    public DaoSession getDashBoardDaoSession() {
            if (permissionHandle()) {
                setupDatabase();
            }
        return daoSession;
    }
    public Database getDb() {
        return db;
    }
    public Database getreadableDb() {
        if (readableDb == null)
            readableDb = helper.getEncryptedReadableDb(DB_PWD);
        return readableDb;
    }
    public Boolean permissionHandle() {
        int storage = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readSto = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readSto != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            /*ActivityCompat.requestPermissions(SCMApplication.this,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);*/
            return false;
        }
        return true;
    }
}
