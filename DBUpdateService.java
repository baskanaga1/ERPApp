package com.guruinfo.scm;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import com.dd.CircularProgressButton;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.SCMApplication;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.DBService;
import com.guruinfo.scm.common.ui.TextViewPlus;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.Sharedpref;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import static com.guruinfo.scm.SCMDashboardActivityLatest.navigationView;
import static com.guruinfo.scm.common.service.DBService.*;
/**
 * Created by Kannan G on 7/14/2017.
 */
public class DBUpdateService extends BaseActivity implements View.OnClickListener {
    String TAG = "DBUpdateService";
    StatusDao statusDao;
    PjtStoreDao PjtsStoreDao;
    ContractorsDao contractorsDao;
    RequestedByDao requestedByDao;
    VendorNameDao vendorNameDao;
    ProjWoMasterDao projWoMasterDao;
    IOWMaterialChildDao iowMaterialChildDao;
    CommMasterDetailsDao commMasterDetailsDao;
    MaterialsMasterDao materialsMasterDao;
    UOMMaterialChildDao uomMaterialChildDao;
    StageListDao stageListDao;
    UpdateOnTableDao updateOnTableDao;
    MobileRightsKeyMasterDao mobileRightsKeyMasterDao;
    AdmEmpMasterDao admEmpMasterDao;
    ProjUserMaterialListDao projUserMaterialListDao;
    ProjAlternateMaterialMasterDao projAlternateMaterialMasterDao;
    MobileRightsMasterDao mobileRightsMasterDao;
    boolean isLoadService = false;
    protected DaoSession daoSession;
    static String cr_id, uid;
    SessionManager session;
    protected Database db;
    HashMap<String,String>tableUpdateTime=new HashMap<>();
    // UI
    CircularProgressButton syncAll;
    public static CircularProgressButton progress1, progress2, progress3, progress4, progress5, progress6, progress7, progress8, progress9, progress10, progress11, progress12, progress13, progress14, progress15, progress16, progress17, progress18, progress19, progress20, progress21, progress22, progress23, progress24, progress25, progress26, progress27, progress28, progress29, progress30, progress31, progress32, progress33, progress34, progress35, progress36, progress37, progress38, progress39, progress40, progress41, progress42, progress43, progress44, progress45, progress46, progress47, progress48, progress49, progress50, progress51, progress52, progress53, progress54, progress55, progress56, progress57, progress58, progress59, progress60,
            progress61, progress62, progress63, progress64, progress65, progress66, progress67, progress68, progress69, progress70, progress71, progress72, progress73, progress74, progress75, progress76, progress77, progress78, progress79, progress80, progress81, progress82, progress83, progress84, progress85, progress86, progress87, progress88, progress89, progress90, progress91, progress92, progress93, progress94, progress95, progress96, progress97, progress98, progress99, progress100, progress101, progress102, progress103, progress104, progress105, progress106, progress107, progress108, progress109;
    public static TextViewPlus updateOn1, updateOn2, updateOn3, updateOn4, updateOn5, updateOn6, updateOn7, updateOn8, updateOn9, updateOn10, updateOn11, updateOn12, updateOn13, updateOn14, updateOn15, updateOn16, updateOn17, updateOn18, updateOn19, updateOn20, updateOn21, updateOn22, updateOn23, updateOn24, updateOn25, updateOn26, updateOn27, updateOn28, updateOn29, updateOn30, updateOn31, updateOn32, updateOn33, updateOn34, updateOn35, updateOn36, updateOn37, updateOn38, updateOn39, updateOn40, updateOn41, updateOn42, updateOn43, updateOn44, updateOn45, updateOn46, updateOn47, updateOn48,
            updateOn49, updateOn50, updateOn51, updateOn52, updateOn53, updateOn54, updateOn55, updateOn56, updateOn57, updateOn58, updateOn59, updateOn60, updateOn61, updateOn62, updateOn63, updateOn64, updateOn65, updateOn66, updateOn67, updateOn68, updateOn69, updateOn70, updateOn71, updateOn72, updateOn73, updateOn74, updateOn75, updateOn76, updateOn77, updateOn78, updateOn79, updateOn80, updateOn81, updateOn82, updateOn83, updateOn84, updateOn85, updateOn86, updateOn87, updateOn88, updateOn89, updateOn90, updateOn91, updateOn92, updateOn93, updateOn94, updateOn95, updateOn96, updateOn97, updateOn98, updateOn99, updateOn100, updateOn101, updateOn102, updateOn103, updateOn104, updateOn105, updateOn106, updateOn107, updateOn108, updateOn109;
    public static TextViewPlus msg1, msg2, msg3, msg4, msg5, msg6, msg7, msg8, msg9, msg10, msg11, msg12, msg13, msg14, msg15, msg16, msg17, msg18, msg19, msg20, msg21, msg22, msg23, msg24, msg25, msg26, msg27, msg28, msg29, msg30, msg31, msg32, msg33, msg34, msg35, msg36, msg37, msg38, msg39, msg40, msg41, msg42, msg43, msg44, msg45, msg46, msg47, msg48, msg49, msg50, msg51, msg52, msg53, msg54, msg55, msg56, msg57, msg58, msg59, msg60, msg61, msg62, msg63, msg64, msg65, msg66, msg67, msg68, msg69, msg70, msg71, msg72, msg73, msg74, msg75, msg76, msg77, msg78, msg79, msg80, msg81, msg82, msg83, msg84, msg85, msg86, msg87, msg88, msg89, msg90, msg91, msg92, msg93, msg94, msg95, msg96, msg97, msg98, msg99, msg100, msg101, msg102, msg103, msg104, msg105, msg106, msg107, msg108, msg109;
    // service
    private DBService myService;
    private Intent serviceIntent;
    String response;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sync_all_db);
        //  getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        daoSession = ((SCMApplication) getContext().getApplicationContext()).getDaoSession();
        db = ((SCMApplication) context.getApplicationContext()).getDb();
        tableUpdateTime=new HashMap<>();
        statusDao = daoSession.getStatusDao();
        PjtsStoreDao = daoSession.getPjtStoreDao();
        contractorsDao = daoSession.getContractorsDao();
        requestedByDao = daoSession.getRequestedByDao();
        vendorNameDao = daoSession.getVendorNameDao();
        projWoMasterDao = daoSession.getProjWoMasterDao();
        iowMaterialChildDao = daoSession.getIOWMaterialChildDao();
        commMasterDetailsDao = daoSession.getCommMasterDetailsDao();
        materialsMasterDao = daoSession.getMaterialsMasterDao();
        uomMaterialChildDao = daoSession.getUOMMaterialChildDao();
        stageListDao = daoSession.getStageListDao();
        updateOnTableDao = daoSession.getUpdateOnTableDao();
        mobileRightsKeyMasterDao = daoSession.getMobileRightsKeyMasterDao();
        admEmpMasterDao = daoSession.getAdmEmpMasterDao();
        projUserMaterialListDao = daoSession.getProjUserMaterialListDao();
        projAlternateMaterialMasterDao = daoSession.getProjAlternateMaterialMasterDao();
        mobileRightsMasterDao = daoSession.getMobileRightsMasterDao();
        navigationView.getMenu().getItem(0).setChecked(true);
        isLoadService = isLoading();
        getFromSession();
        // CircularProgressButton button
        syncAll = (CircularProgressButton) findViewById(R.id.syn_all);
        progress1 = (CircularProgressButton) findViewById(R.id.progress1);
        progress2 = (CircularProgressButton) findViewById(R.id.progress2);
        progress3 = (CircularProgressButton) findViewById(R.id.progress3);
        progress4 = (CircularProgressButton) findViewById(R.id.progress4);
        progress5 = (CircularProgressButton) findViewById(R.id.progress5);
        progress6 = (CircularProgressButton) findViewById(R.id.progress6);
        progress7 = (CircularProgressButton) findViewById(R.id.progress7);
        progress8 = (CircularProgressButton) findViewById(R.id.progress8);
        progress9 = (CircularProgressButton) findViewById(R.id.progress9);
        progress10 = (CircularProgressButton) findViewById(R.id.progress10);
        progress11 = (CircularProgressButton) findViewById(R.id.progress11);
        progress12 = (CircularProgressButton) findViewById(R.id.progress12);
        progress13 = (CircularProgressButton) findViewById(R.id.progress13);
        progress14 = (CircularProgressButton) findViewById(R.id.progress14);
        progress15 = (CircularProgressButton) findViewById(R.id.progress15);
        progress16 = (CircularProgressButton) findViewById(R.id.progress16);
        progress17 = (CircularProgressButton) findViewById(R.id.progress17);
        progress18 = (CircularProgressButton) findViewById(R.id.progress18);
        progress19 = (CircularProgressButton) findViewById(R.id.progress19);
        progress20 = (CircularProgressButton) findViewById(R.id.progress20);
        progress21 = (CircularProgressButton) findViewById(R.id.progress21);
        progress22 = (CircularProgressButton) findViewById(R.id.progress22);
        progress23 = (CircularProgressButton) findViewById(R.id.progress23);
        progress24 = (CircularProgressButton) findViewById(R.id.progress24);
        progress25 = (CircularProgressButton) findViewById(R.id.progress25);
        progress26 = (CircularProgressButton) findViewById(R.id.progress26);
        progress27 = (CircularProgressButton) findViewById(R.id.progress27);
        progress28 = (CircularProgressButton) findViewById(R.id.progress28);
        progress29 = (CircularProgressButton) findViewById(R.id.progress29);
        progress30 = (CircularProgressButton) findViewById(R.id.progress30);
        progress31 = (CircularProgressButton) findViewById(R.id.progress31);
        progress32 = (CircularProgressButton) findViewById(R.id.progress32);
        progress33 = (CircularProgressButton) findViewById(R.id.progress33);
        progress34 = (CircularProgressButton) findViewById(R.id.progress34);
        progress35 = (CircularProgressButton) findViewById(R.id.progress35);
        progress36 = (CircularProgressButton) findViewById(R.id.progress36);
        progress37 = (CircularProgressButton) findViewById(R.id.progress37);
        progress38 = (CircularProgressButton) findViewById(R.id.progress38);
        progress39 = (CircularProgressButton) findViewById(R.id.progress39);
        progress40 = (CircularProgressButton) findViewById(R.id.progress40);
        progress41 = (CircularProgressButton) findViewById(R.id.progress41);
        progress42 = (CircularProgressButton) findViewById(R.id.progress42);
        progress43 = (CircularProgressButton) findViewById(R.id.progress43);
        progress44 = (CircularProgressButton) findViewById(R.id.progress44);
        progress45 = (CircularProgressButton) findViewById(R.id.progress45);
        progress46 = (CircularProgressButton) findViewById(R.id.progress46);
        progress47 = (CircularProgressButton) findViewById(R.id.progress47);
        progress48 = (CircularProgressButton) findViewById(R.id.progress48);
        progress49 = (CircularProgressButton) findViewById(R.id.progress49);
        progress50 = (CircularProgressButton) findViewById(R.id.progress50);
        progress51 = (CircularProgressButton) findViewById(R.id.progress51);
        progress52 = (CircularProgressButton) findViewById(R.id.progress52);
        progress53 = (CircularProgressButton) findViewById(R.id.progress53);
        progress54 = (CircularProgressButton) findViewById(R.id.progress54);
        progress55 = (CircularProgressButton) findViewById(R.id.progress55);
        progress56 = (CircularProgressButton) findViewById(R.id.progress56);
        progress57 = (CircularProgressButton) findViewById(R.id.progress57);
        progress58 = (CircularProgressButton) findViewById(R.id.progress58);
        progress59 = (CircularProgressButton) findViewById(R.id.progress59);
        progress60 = (CircularProgressButton) findViewById(R.id.progress60);
        progress61 = (CircularProgressButton) findViewById(R.id.progress61);
        progress62 = (CircularProgressButton) findViewById(R.id.progress62);
        progress63 = (CircularProgressButton) findViewById(R.id.progress63);
        progress64 = (CircularProgressButton) findViewById(R.id.progress64);
        progress65 = (CircularProgressButton) findViewById(R.id.progress65);
        progress66 = (CircularProgressButton) findViewById(R.id.progress66);
        progress67 = (CircularProgressButton) findViewById(R.id.progress67);
        progress68 = (CircularProgressButton) findViewById(R.id.progress68);
        progress69 = (CircularProgressButton) findViewById(R.id.progress69);
        progress70 = (CircularProgressButton) findViewById(R.id.progress70);
        progress71 = (CircularProgressButton) findViewById(R.id.progress71);
        progress72 = (CircularProgressButton) findViewById(R.id.progress72);
        progress73 = (CircularProgressButton) findViewById(R.id.progress73);
        progress74 = (CircularProgressButton) findViewById(R.id.progress74);
        progress75 = (CircularProgressButton) findViewById(R.id.progress75);
        progress76 = (CircularProgressButton) findViewById(R.id.progress76);
        progress77 = (CircularProgressButton) findViewById(R.id.progress77);
        progress78 = (CircularProgressButton) findViewById(R.id.progress78);
        progress79 = (CircularProgressButton) findViewById(R.id.progress79);
        progress80 = (CircularProgressButton) findViewById(R.id.progress80);
        progress81 = (CircularProgressButton) findViewById(R.id.progress81);
        progress82 = (CircularProgressButton) findViewById(R.id.progress82);
        progress83 = (CircularProgressButton) findViewById(R.id.progress83);
        progress84 = (CircularProgressButton) findViewById(R.id.progress84);
        progress85 = (CircularProgressButton) findViewById(R.id.progress85);
        progress86 = (CircularProgressButton) findViewById(R.id.progress86);
        progress87 = (CircularProgressButton) findViewById(R.id.progress87);
        progress88 = (CircularProgressButton) findViewById(R.id.progress88);
        progress89 = (CircularProgressButton) findViewById(R.id.progress89);
        progress90 = (CircularProgressButton) findViewById(R.id.progress90);
        progress91 = (CircularProgressButton) findViewById(R.id.progress91);
        progress92 = (CircularProgressButton) findViewById(R.id.progress92);
        progress93 = (CircularProgressButton) findViewById(R.id.progress93);
        progress94 = (CircularProgressButton) findViewById(R.id.progress94);
        progress95 = (CircularProgressButton) findViewById(R.id.progress95);
        progress96 = (CircularProgressButton) findViewById(R.id.progress96);
        progress97 = (CircularProgressButton) findViewById(R.id.progress97);
        progress98 = (CircularProgressButton) findViewById(R.id.progress98);
        progress99 = (CircularProgressButton) findViewById(R.id.progress99);
        progress100 = (CircularProgressButton) findViewById(R.id.progress100);
        progress101 = (CircularProgressButton) findViewById(R.id.progress101);
        progress102 = (CircularProgressButton) findViewById(R.id.progress102);
        progress103 = (CircularProgressButton) findViewById(R.id.progress103);
        progress104 = (CircularProgressButton) findViewById(R.id.progress104);
        progress105 = (CircularProgressButton) findViewById(R.id.progress105);
        progress106 = (CircularProgressButton) findViewById(R.id.progress106);
        progress107 = (CircularProgressButton) findViewById(R.id.progress107);
        progress108 = (CircularProgressButton) findViewById(R.id.progress108);
        progress109 = (CircularProgressButton) findViewById(R.id.progress109);
        //TextView
        updateOn1 = (TextViewPlus) findViewById(R.id.update_on1);
        updateOn2 = (TextViewPlus) findViewById(R.id.update_on2);
        updateOn3 = (TextViewPlus) findViewById(R.id.update_on3);
        updateOn4 = (TextViewPlus) findViewById(R.id.update_on4);
        updateOn5 = (TextViewPlus) findViewById(R.id.update_on5);
        updateOn6 = (TextViewPlus) findViewById(R.id.update_on6);
        updateOn7 = (TextViewPlus) findViewById(R.id.update_on7);
        updateOn8 = (TextViewPlus) findViewById(R.id.update_on8);
        updateOn9 = (TextViewPlus) findViewById(R.id.update_on9);
        updateOn10 = (TextViewPlus) findViewById(R.id.update_on10);
        updateOn11 = (TextViewPlus) findViewById(R.id.update_on11);
        updateOn12 = (TextViewPlus) findViewById(R.id.update_on12);
        updateOn13 = (TextViewPlus) findViewById(R.id.update_on13);
        updateOn14 = (TextViewPlus) findViewById(R.id.update_on14);
        updateOn15 = (TextViewPlus) findViewById(R.id.update_on15);
        updateOn16 = (TextViewPlus) findViewById(R.id.update_on16);
        updateOn17 = (TextViewPlus) findViewById(R.id.update_on17);
        updateOn18 = (TextViewPlus) findViewById(R.id.update_on18);
        updateOn19 = (TextViewPlus) findViewById(R.id.update_on19);
        updateOn20 = (TextViewPlus) findViewById(R.id.update_on20);
        updateOn21 = (TextViewPlus) findViewById(R.id.update_on21);
        updateOn22 = (TextViewPlus) findViewById(R.id.update_on22);
        updateOn23 = (TextViewPlus) findViewById(R.id.update_on23);
        updateOn24 = (TextViewPlus) findViewById(R.id.update_on24);
        updateOn25 = (TextViewPlus) findViewById(R.id.update_on25);
        updateOn26 = (TextViewPlus) findViewById(R.id.update_on26);
        updateOn27 = (TextViewPlus) findViewById(R.id.update_on27);
        updateOn28 = (TextViewPlus) findViewById(R.id.update_on28);
        updateOn29 = (TextViewPlus) findViewById(R.id.update_on29);
        updateOn30 = (TextViewPlus) findViewById(R.id.update_on30);
        updateOn31 = (TextViewPlus) findViewById(R.id.update_on31);
        updateOn32 = (TextViewPlus) findViewById(R.id.update_on32);
        updateOn33 = (TextViewPlus) findViewById(R.id.update_on33);
        updateOn34 = (TextViewPlus) findViewById(R.id.update_on34);
        updateOn35 = (TextViewPlus) findViewById(R.id.update_on35);
        updateOn36 = (TextViewPlus) findViewById(R.id.update_on36);
        updateOn37 = (TextViewPlus) findViewById(R.id.update_on37);
        updateOn38 = (TextViewPlus) findViewById(R.id.update_on38);
        updateOn39 = (TextViewPlus) findViewById(R.id.update_on39);
        updateOn40 = (TextViewPlus) findViewById(R.id.update_on40);
        updateOn41 = (TextViewPlus) findViewById(R.id.update_on41);
        updateOn42 = (TextViewPlus) findViewById(R.id.update_on42);
        updateOn43 = (TextViewPlus) findViewById(R.id.update_on43);
        updateOn44 = (TextViewPlus) findViewById(R.id.update_on44);
        updateOn45 = (TextViewPlus) findViewById(R.id.update_on45);
        updateOn46 = (TextViewPlus) findViewById(R.id.update_on46);
        updateOn47 = (TextViewPlus) findViewById(R.id.update_on47);
        updateOn48 = (TextViewPlus) findViewById(R.id.update_on48);
        updateOn49 = (TextViewPlus) findViewById(R.id.update_on49);
        updateOn50 = (TextViewPlus) findViewById(R.id.update_on50);
        updateOn51 = (TextViewPlus) findViewById(R.id.update_on51);
        updateOn52 = (TextViewPlus) findViewById(R.id.update_on52);
        updateOn53 = (TextViewPlus) findViewById(R.id.update_on53);
        updateOn54 = (TextViewPlus) findViewById(R.id.update_on54);
        updateOn55 = (TextViewPlus) findViewById(R.id.update_on55);
        updateOn56 = (TextViewPlus) findViewById(R.id.update_on56);
        updateOn57 = (TextViewPlus) findViewById(R.id.update_on57);
        updateOn58 = (TextViewPlus) findViewById(R.id.update_on58);
        updateOn59 = (TextViewPlus) findViewById(R.id.update_on59);
        updateOn60 = (TextViewPlus) findViewById(R.id.update_on60);
        updateOn61 = (TextViewPlus) findViewById(R.id.update_on61);
        updateOn62 = (TextViewPlus) findViewById(R.id.update_on62);
        updateOn63 = (TextViewPlus) findViewById(R.id.update_on63);
        updateOn64 = (TextViewPlus) findViewById(R.id.update_on64);
        updateOn65 = (TextViewPlus) findViewById(R.id.update_on65);
        updateOn66 = (TextViewPlus) findViewById(R.id.update_on66);
        updateOn67 = (TextViewPlus) findViewById(R.id.update_on67);
        updateOn68 = (TextViewPlus) findViewById(R.id.update_on68);
        updateOn69 = (TextViewPlus) findViewById(R.id.update_on69);
        updateOn70 = (TextViewPlus) findViewById(R.id.update_on70);
        updateOn71 = (TextViewPlus) findViewById(R.id.update_on71);
        updateOn72 = (TextViewPlus) findViewById(R.id.update_on72);
        updateOn73 = (TextViewPlus) findViewById(R.id.update_on73);
        updateOn74 = (TextViewPlus) findViewById(R.id.update_on74);
        updateOn75 = (TextViewPlus) findViewById(R.id.update_on75);
        updateOn76 = (TextViewPlus) findViewById(R.id.update_on76);
        updateOn77 = (TextViewPlus) findViewById(R.id.update_on77);
        updateOn78 = (TextViewPlus) findViewById(R.id.update_on78);
        updateOn79 = (TextViewPlus) findViewById(R.id.update_on79);
        updateOn80 = (TextViewPlus) findViewById(R.id.update_on80);
        updateOn81 = (TextViewPlus) findViewById(R.id.update_on81);
        updateOn82 = (TextViewPlus) findViewById(R.id.update_on82);
        updateOn83 = (TextViewPlus) findViewById(R.id.update_on83);
        updateOn84 = (TextViewPlus) findViewById(R.id.update_on84);
        updateOn85 = (TextViewPlus) findViewById(R.id.update_on85);
        updateOn86 = (TextViewPlus) findViewById(R.id.update_on86);
        updateOn87 = (TextViewPlus) findViewById(R.id.update_on87);
        updateOn88 = (TextViewPlus) findViewById(R.id.update_on88);
        updateOn89 = (TextViewPlus) findViewById(R.id.update_on89);
        updateOn90 = (TextViewPlus) findViewById(R.id.update_on90);
        updateOn91 = (TextViewPlus) findViewById(R.id.update_on91);
        updateOn92 = (TextViewPlus) findViewById(R.id.update_on92);
        updateOn93 = (TextViewPlus) findViewById(R.id.update_on93);
        updateOn94 = (TextViewPlus) findViewById(R.id.update_on94);
        updateOn95 = (TextViewPlus) findViewById(R.id.update_on95);
        updateOn96 = (TextViewPlus) findViewById(R.id.update_on96);
        updateOn97 = (TextViewPlus) findViewById(R.id.update_on97);
        updateOn98 = (TextViewPlus) findViewById(R.id.update_on98);
        updateOn99 = (TextViewPlus) findViewById(R.id.update_on99);
        updateOn100 = (TextViewPlus) findViewById(R.id.update_on100);
        updateOn101 = (TextViewPlus) findViewById(R.id.update_on101);
        updateOn102 = (TextViewPlus) findViewById(R.id.update_on102);
        updateOn103 = (TextViewPlus) findViewById(R.id.update_on103);
        updateOn104 = (TextViewPlus) findViewById(R.id.update_on104);
        updateOn105 = (TextViewPlus) findViewById(R.id.update_on105);
        updateOn106 = (TextViewPlus) findViewById(R.id.update_on106);
        updateOn107 = (TextViewPlus) findViewById(R.id.update_on107);
        updateOn108 = (TextViewPlus) findViewById(R.id.update_on108);
        updateOn109 = (TextViewPlus) findViewById(R.id.update_on109);
        msg1 = (TextViewPlus) findViewById(R.id.msg1);
        msg2 = (TextViewPlus) findViewById(R.id.msg2);
        msg3 = (TextViewPlus) findViewById(R.id.msg3);
        msg4 = (TextViewPlus) findViewById(R.id.msg4);
        msg5 = (TextViewPlus) findViewById(R.id.msg5);
        msg6 = (TextViewPlus) findViewById(R.id.msg6);
        msg7 = (TextViewPlus) findViewById(R.id.msg7);
        msg8 = (TextViewPlus) findViewById(R.id.msg8);
        msg9 = (TextViewPlus) findViewById(R.id.msg9);
        msg10 = (TextViewPlus) findViewById(R.id.msg10);
        msg11 = (TextViewPlus) findViewById(R.id.msg11);
        msg12 = (TextViewPlus) findViewById(R.id.msg12);
        msg13 = (TextViewPlus) findViewById(R.id.msg13);
        msg14 = (TextViewPlus) findViewById(R.id.msg14);
        msg15 = (TextViewPlus) findViewById(R.id.msg15);
        msg16 = (TextViewPlus) findViewById(R.id.msg16);
        msg17 = (TextViewPlus) findViewById(R.id.msg17);
        msg18 = (TextViewPlus) findViewById(R.id.msg18);
        msg19 = (TextViewPlus) findViewById(R.id.msg19);
        msg20 = (TextViewPlus) findViewById(R.id.msg20);
        msg21 = (TextViewPlus) findViewById(R.id.msg21);
        msg22 = (TextViewPlus) findViewById(R.id.msg22);
        msg23 = (TextViewPlus) findViewById(R.id.msg23);
        msg24 = (TextViewPlus) findViewById(R.id.msg24);
        msg25 = (TextViewPlus) findViewById(R.id.msg25);
        msg26 = (TextViewPlus) findViewById(R.id.msg26);
        msg27 = (TextViewPlus) findViewById(R.id.msg27);
        msg28 = (TextViewPlus) findViewById(R.id.msg28);
        msg29 = (TextViewPlus) findViewById(R.id.msg29);
        msg30 = (TextViewPlus) findViewById(R.id.msg30);
        msg31 = (TextViewPlus) findViewById(R.id.msg31);
        msg32 = (TextViewPlus) findViewById(R.id.msg32);
        msg33 = (TextViewPlus) findViewById(R.id.msg33);
        msg34 = (TextViewPlus) findViewById(R.id.msg34);
        msg35 = (TextViewPlus) findViewById(R.id.msg35);
        msg36 = (TextViewPlus) findViewById(R.id.msg36);
        msg37 = (TextViewPlus) findViewById(R.id.msg37);
        msg38 = (TextViewPlus) findViewById(R.id.msg38);
        msg39 = (TextViewPlus) findViewById(R.id.msg39);
        msg40 = (TextViewPlus) findViewById(R.id.msg40);
        msg41 = (TextViewPlus) findViewById(R.id.msg41);
        msg42 = (TextViewPlus) findViewById(R.id.msg42);
        msg43 = (TextViewPlus) findViewById(R.id.msg43);
        msg44 = (TextViewPlus) findViewById(R.id.msg44);
        msg45 = (TextViewPlus) findViewById(R.id.msg45);
        msg46 = (TextViewPlus) findViewById(R.id.msg46);
        msg47 = (TextViewPlus) findViewById(R.id.msg47);
        msg48 = (TextViewPlus) findViewById(R.id.msg48);
        msg49 = (TextViewPlus) findViewById(R.id.msg49);
        msg50 = (TextViewPlus) findViewById(R.id.msg50);
        msg51 = (TextViewPlus) findViewById(R.id.msg51);
        msg52 = (TextViewPlus) findViewById(R.id.msg52);
        msg53 = (TextViewPlus) findViewById(R.id.msg53);
        msg54 = (TextViewPlus) findViewById(R.id.msg54);
        msg55 = (TextViewPlus) findViewById(R.id.msg55);
        msg56 = (TextViewPlus) findViewById(R.id.msg56);
        msg57 = (TextViewPlus) findViewById(R.id.msg57);
        msg58 = (TextViewPlus) findViewById(R.id.msg58);
        msg59 = (TextViewPlus) findViewById(R.id.msg59);
        msg60 = (TextViewPlus) findViewById(R.id.msg60);
        msg61 = (TextViewPlus) findViewById(R.id.msg61);
        msg62 = (TextViewPlus) findViewById(R.id.msg62);
        msg63 = (TextViewPlus) findViewById(R.id.msg63);
        msg64 = (TextViewPlus) findViewById(R.id.msg64);
        msg65 = (TextViewPlus) findViewById(R.id.msg65);
        msg66 = (TextViewPlus) findViewById(R.id.msg66);
        msg67 = (TextViewPlus) findViewById(R.id.msg67);
        msg68 = (TextViewPlus) findViewById(R.id.msg68);
        msg69 = (TextViewPlus) findViewById(R.id.msg69);
        msg70 = (TextViewPlus) findViewById(R.id.msg70);
        msg71 = (TextViewPlus) findViewById(R.id.msg71);
        msg72 = (TextViewPlus) findViewById(R.id.msg72);
        msg73 = (TextViewPlus) findViewById(R.id.msg73);
        msg74 = (TextViewPlus) findViewById(R.id.msg74);
        msg75 = (TextViewPlus) findViewById(R.id.msg75);
        msg76 = (TextViewPlus) findViewById(R.id.msg76);
        msg77 = (TextViewPlus) findViewById(R.id.msg77);
        msg78 = (TextViewPlus) findViewById(R.id.msg78);
        msg79 = (TextViewPlus) findViewById(R.id.msg79);
        msg80 = (TextViewPlus) findViewById(R.id.msg80);
        msg81 = (TextViewPlus) findViewById(R.id.msg81);
        msg82 = (TextViewPlus) findViewById(R.id.msg82);
        msg83 = (TextViewPlus) findViewById(R.id.msg83);
        msg84 = (TextViewPlus) findViewById(R.id.msg84);
        msg85 = (TextViewPlus) findViewById(R.id.msg85);
        msg86 = (TextViewPlus) findViewById(R.id.msg86);
        msg87 = (TextViewPlus) findViewById(R.id.msg87);
        msg88 = (TextViewPlus) findViewById(R.id.msg88);
        msg89 = (TextViewPlus) findViewById(R.id.msg89);
        msg90 = (TextViewPlus) findViewById(R.id.msg90);
        msg91 = (TextViewPlus) findViewById(R.id.msg91);
        msg92 = (TextViewPlus) findViewById(R.id.msg92);
        msg93 = (TextViewPlus) findViewById(R.id.msg93);
        msg94 = (TextViewPlus) findViewById(R.id.msg94);
        msg95 = (TextViewPlus) findViewById(R.id.msg95);
        msg96 = (TextViewPlus) findViewById(R.id.msg96);
        msg97 = (TextViewPlus) findViewById(R.id.msg97);
        msg98 = (TextViewPlus) findViewById(R.id.msg98);
        msg99 = (TextViewPlus) findViewById(R.id.msg99);
        msg100 = (TextViewPlus) findViewById(R.id.msg100);
        msg101 = (TextViewPlus) findViewById(R.id.msg101);
        msg102 = (TextViewPlus) findViewById(R.id.msg102);
        msg103 = (TextViewPlus) findViewById(R.id.msg103);
        msg104 = (TextViewPlus) findViewById(R.id.msg104);
        msg105 = (TextViewPlus) findViewById(R.id.msg105);
        msg106 = (TextViewPlus) findViewById(R.id.msg106);
        msg107 = (TextViewPlus) findViewById(R.id.msg107);
        msg108 = (TextViewPlus) findViewById(R.id.msg108);
        msg109 = (TextViewPlus) findViewById(R.id.msg109);
        syncAll.setOnClickListener(this);
        progress1.setOnClickListener(this);
        progress2.setOnClickListener(this);
        progress3.setOnClickListener(this);
        progress4.setOnClickListener(this);
        progress5.setOnClickListener(this);
        progress6.setOnClickListener(this);
        progress7.setOnClickListener(this);
        progress8.setOnClickListener(this);
        progress9.setOnClickListener(this);
        progress10.setOnClickListener(this);
        progress11.setOnClickListener(this);
        progress12.setOnClickListener(this);
        progress13.setOnClickListener(this);
        progress14.setOnClickListener(this);
        progress15.setOnClickListener(this);
        progress16.setOnClickListener(this);
        progress17.setOnClickListener(this);
        progress18.setOnClickListener(this);
        progress19.setOnClickListener(this);
        progress20.setOnClickListener(this);
        progress21.setOnClickListener(this);
        progress22.setOnClickListener(this);
        progress23.setOnClickListener(this);
        progress24.setOnClickListener(this);
        progress25.setOnClickListener(this);
        progress26.setOnClickListener(this);
        progress27.setOnClickListener(this);
        progress28.setOnClickListener(this);
        progress29.setOnClickListener(this);
        progress30.setOnClickListener(this);
        progress31.setOnClickListener(this);
        progress32.setOnClickListener(this);
        progress33.setOnClickListener(this);
        progress34.setOnClickListener(this);
        progress35.setOnClickListener(this);
        progress36.setOnClickListener(this);
        progress37.setOnClickListener(this);
        progress38.setOnClickListener(this);
        progress39.setOnClickListener(this);
        progress40.setOnClickListener(this);
        progress41.setOnClickListener(this);
        progress42.setOnClickListener(this);
        progress43.setOnClickListener(this);
        progress44.setOnClickListener(this);
        progress45.setOnClickListener(this);
        progress46.setOnClickListener(this);
        progress47.setOnClickListener(this);
        progress48.setOnClickListener(this);
        progress49.setOnClickListener(this);
        progress50.setOnClickListener(this);
        progress51.setOnClickListener(this);
        progress52.setOnClickListener(this);
        progress53.setOnClickListener(this);
        progress54.setOnClickListener(this);
        progress55.setOnClickListener(this);
        progress56.setOnClickListener(this);
        progress57.setOnClickListener(this);
        progress58.setOnClickListener(this);
        progress59.setOnClickListener(this);
        progress60.setOnClickListener(this);
        progress61.setOnClickListener(this);
        progress62.setOnClickListener(this);
        progress63.setOnClickListener(this);
        progress64.setOnClickListener(this);
        progress65.setOnClickListener(this);
        progress66.setOnClickListener(this);
        progress67.setOnClickListener(this);
        progress68.setOnClickListener(this);
        progress69.setOnClickListener(this);
        progress70.setOnClickListener(this);
        progress71.setOnClickListener(this);
        progress72.setOnClickListener(this);
        progress73.setOnClickListener(this);
        progress74.setOnClickListener(this);
        progress75.setOnClickListener(this);
        progress76.setOnClickListener(this);
        progress77.setOnClickListener(this);
        progress78.setOnClickListener(this);
        progress79.setOnClickListener(this);
        progress80.setOnClickListener(this);
        progress81.setOnClickListener(this);
        progress82.setOnClickListener(this);
        progress83.setOnClickListener(this);
        progress84.setOnClickListener(this);
        progress85.setOnClickListener(this);
        progress86.setOnClickListener(this);
        progress87.setOnClickListener(this);
        progress88.setOnClickListener(this);
        progress89.setOnClickListener(this);
        progress90.setOnClickListener(this);
        progress91.setOnClickListener(this);
        progress92.setOnClickListener(this);
        progress93.setOnClickListener(this);
        progress94.setOnClickListener(this);
        progress95.setOnClickListener(this);
        progress96.setOnClickListener(this);
        progress97.setOnClickListener(this);
        progress98.setOnClickListener(this);
        progress99.setOnClickListener(this);
        progress100.setOnClickListener(this);
        progress101.setOnClickListener(this);
        progress102.setOnClickListener(this);
        progress103.setOnClickListener(this);
        progress104.setOnClickListener(this);
        progress105.setOnClickListener(this);
        progress106.setOnClickListener(this);
        progress107.setOnClickListener(this);
        progress108.setOnClickListener(this);
        progress109.setOnClickListener(this);
        if (getIntent().getStringExtra("res") != null) {
            response = getIntent().getStringExtra("res");
            onSync();
        }
        /*List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid)).list();
        for(int i=0; i<TableLists.size(); i++) {
            String flag=TableLists.get(i).getTable_name();
            if (flag.equalsIgnoreCase(uid+"_ProjectListWithStoreLoad") || flag.equalsIgnoreCase(uid+"_ProjectRequestedBytLoad") || flag.equalsIgnoreCase(uid+"_SatgeLoadBasedOnProjectList")
                    || flag.equalsIgnoreCase(uid+"_RightsTable") || flag.equalsIgnoreCase(uid+"_projUserMaterialList") || flag.equalsIgnoreCase(uid+"_projUserStageList")) {
                tableUpdateTime.put(TableLists.get(i).getTable_name(), TableLists.get(i).getLast_update() + "##" + TableLists.get(i).getStatus());
            }else {
                tableUpdateTime.put(TableLists.get(i).getUnique_id(), TableLists.get(i).getLast_update() + "##" + TableLists.get(i).getStatus());
            }
        }*/
        syncAll.setVisibility(View.GONE);
        loadingProgress();
        serviceIntent = new Intent(this, DBService.class);
        int TIME_OUT = 300;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    syncAllClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, TIME_OUT);
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ((DBService.MyBinder) service).getService();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            myService = null;
            Log.d(TAG, "Service Disconnected");
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        // start the service
        startService(serviceIntent);
        // bind to the service
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    public void onSync() {
        try {
            JSONObject resObject = new JSONObject(response);
            if (resObject.getString("ProjectListWithStoreLoad").equalsIgnoreCase("true")) {
                progress1.setVisibility(View.VISIBLE);
            } else {
                progress1.setVisibility(View.GONE);
            }
            if (resObject.getString("ProjectMaterialBytLoad").equalsIgnoreCase("true")) {
                progress2.setVisibility(View.VISIBLE);
            } else {
                progress2.setVisibility(View.GONE);
            }
            if (resObject.getString("ProjectContractorLoad").equalsIgnoreCase("true")) {
                progress3.setVisibility(View.VISIBLE);
            } else {
                progress3.setVisibility(View.GONE);
            }
            if (resObject.getString("ProjectRequestedBytLoad").equalsIgnoreCase("true")) {
                progress4.setVisibility(View.VISIBLE);
            } else {
                progress4.setVisibility(View.GONE);
            }
            if (resObject.getString("ProjectVendorName").equalsIgnoreCase("true")) {
                progress5.setVisibility(View.VISIBLE);
            } else {
                progress5.setVisibility(View.GONE);
            }
            if (resObject.getString("ProjectStatusLoad").equalsIgnoreCase("true")) {
                progress6.setVisibility(View.VISIBLE);
            } else {
                progress6.setVisibility(View.GONE);
            }
            if (resObject.getString("WoRefTable").equalsIgnoreCase("true")) {
                progress7.setVisibility(View.VISIBLE);
            } else {
                progress7.setVisibility(View.GONE);
            }
            if (resObject.getString("ProjIowMaterialChildTable").equalsIgnoreCase("true")) {
                progress8.setVisibility(View.VISIBLE);
            } else {
                progress8.setVisibility(View.GONE);
            }
            if (resObject.getString("ProjCmnMasterDetailsTable").equalsIgnoreCase("true")) {
                progress9.setVisibility(View.VISIBLE);
            } else {
                progress9.setVisibility(View.GONE);
            }
            if (resObject.getString("projMaterialUomChild").equalsIgnoreCase("true")) {
                progress10.setVisibility(View.VISIBLE);
            } else {
                progress10.setVisibility(View.GONE);
            }
            if (resObject.getString("SatgeLoadBasedOnProjectList").equalsIgnoreCase("true")) {
                progress11.setVisibility(View.VISIBLE);
            } else {
                progress11.setVisibility(View.GONE);
            }
           /* if (resObject.getString("RightsTable").equalsIgnoreCase("true")) {
                progress12.setVisibility(View.VISIBLE);
            } else {
                progress12.setVisibility(View.GONE);
            }*/
            if (resObject.getString("projMirProcChild").equalsIgnoreCase("true")) {
                progress13.setVisibility(View.VISIBLE);
            } else {
                progress13.setVisibility(View.GONE);
            }
            if (resObject.getString("projMirMaster").equalsIgnoreCase("true")) {
                progress14.setVisibility(View.VISIBLE);
            } else {
                progress14.setVisibility(View.GONE);
            }
            if (resObject.getString("projMinProcChild").equalsIgnoreCase("true")) {
                progress15.setVisibility(View.VISIBLE);
            } else {
                progress15.setVisibility(View.GONE);
            }
            if (resObject.getString("projMinMaster").equalsIgnoreCase("true")) {
                progress16.setVisibility(View.VISIBLE);
            } else {
                progress16.setVisibility(View.GONE);
            }
            if (resObject.getString("projMrProcChild").equalsIgnoreCase("true")) {
                progress17.setVisibility(View.VISIBLE);
            } else {
                progress17.setVisibility(View.GONE);
            }
            if (resObject.getString("mobileRightsKeyMaster").equalsIgnoreCase("true")) {
                progress18.setVisibility(View.VISIBLE);
            } else {
                progress18.setVisibility(View.GONE);
            }
            if (resObject.getString("admEmpMaster").equalsIgnoreCase("true")) {
                progress19.setVisibility(View.VISIBLE);
            } else {
                progress19.setVisibility(View.GONE);
            }
            if (resObject.getString("projUserMaterialList").equalsIgnoreCase("true")) {
                progress20.setVisibility(View.VISIBLE);
            } else {
                progress20.setVisibility(View.GONE);
            }
            if (resObject.getString("projAlternateMaterialMaster").equalsIgnoreCase("true")) {
                progress21.setVisibility(View.VISIBLE);
            } else {
                progress21.setVisibility(View.GONE);
            }
            if (resObject.getString("mobileRightsMaster").equalsIgnoreCase("true")) {
                progress22.setVisibility(View.VISIBLE);
            } else {
                progress22.setVisibility(View.GONE);
            }
            if (resObject.getString("projMaterialChild").equalsIgnoreCase("true")) {
                progress23.setVisibility(View.VISIBLE);
            } else {
                progress23.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoMaster").equalsIgnoreCase("true")) {
                progress24.setVisibility(View.VISIBLE);
            } else {
                progress24.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoItemChild").equalsIgnoreCase("true")) {
                progress25.setVisibility(View.VISIBLE);
            } else {
                progress25.setVisibility(View.GONE);
            }
            if (resObject.getString("projJobIowStageMaster").equalsIgnoreCase("true")) {
                progress26.setVisibility(View.VISIBLE);
            } else {
                progress26.setVisibility(View.GONE);
            }
            if (resObject.getString("projStageIowMaterialDet").equalsIgnoreCase("true")) {
                progress27.setVisibility(View.VISIBLE);
            } else {
                progress27.setVisibility(View.GONE);
            }
            if (resObject.getString("projMrMaster").equalsIgnoreCase("true")) {
                progress28.setVisibility(View.VISIBLE);
            } else {
                progress28.setVisibility(View.GONE);
            }
            if (resObject.getString("projVendorMasterView").equalsIgnoreCase("true")) {
                progress29.setVisibility(View.VISIBLE);
            } else {
                progress29.setVisibility(View.GONE);
            }
            if (resObject.getString("admEmpMasterView").equalsIgnoreCase("true")) {
                progress30.setVisibility(View.VISIBLE);
            } else {
                progress30.setVisibility(View.GONE);
            }
            if (resObject.getString("arcApprovalConfig").equalsIgnoreCase("true")) {
                progress31.setVisibility(View.VISIBLE);
            } else {
                progress31.setVisibility(View.GONE);
            }
            if (resObject.getString("projUserProjectList").equalsIgnoreCase("true")) {
                progress32.setVisibility(View.VISIBLE);
            } else {
                progress32.setVisibility(View.GONE);
            }
            if (resObject.getString("projStoreStock").equalsIgnoreCase("true")) {
                progress33.setVisibility(View.VISIBLE);
            } else {
                progress33.setVisibility(View.GONE);
            }
            if (resObject.getString("projStoreMaster").equalsIgnoreCase("true")) {
                progress34.setVisibility(View.VISIBLE);
            } else {
                progress34.setVisibility(View.GONE);
            }
            if (resObject.getString("projProjectAddressMaster").equalsIgnoreCase("true")) {
                progress35.setVisibility(View.VISIBLE);
            } else {
                progress35.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoOtherChargeChild").equalsIgnoreCase("true")) {
                progress36.setVisibility(View.VISIBLE);
            } else {
                progress36.setVisibility(View.GONE);
            }
            if (resObject.getString("cmnPartyAddressInfo").equalsIgnoreCase("true")) {
                progress37.setVisibility(View.VISIBLE);
            } else {
                progress37.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoGernalTermsChild").equalsIgnoreCase("true")) {
                progress38.setVisibility(View.VISIBLE);
            } else {
                progress38.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoTermsChild").equalsIgnoreCase("true")) {
                progress39.setVisibility(View.VISIBLE);
            } else {
                progress39.setVisibility(View.GONE);
            }
            if (resObject.getString("cmnTaxMaster").equalsIgnoreCase("true")) {
                progress40.setVisibility(View.VISIBLE);
            } else {
                progress40.setVisibility(View.GONE);
            }
            if (resObject.getString("cmnPartyCompanyInfo").equalsIgnoreCase("true")) {
                progress41.setVisibility(View.VISIBLE);
            } else {
                progress41.setVisibility(View.GONE);
            }
            if (resObject.getString("cmnPartyIdDocInfo").equalsIgnoreCase("true")) {
                progress42.setVisibility(View.VISIBLE);
            } else {
                progress42.setVisibility(View.GONE);
            }
            if (resObject.getString("projMbookMaster").equalsIgnoreCase("true")) {
                progress43.setVisibility(View.VISIBLE);
            } else {
                progress43.setVisibility(View.GONE);
            }
            if (resObject.getString("projContractorMasterView").equalsIgnoreCase("true")) {
                progress44.setVisibility(View.VISIBLE);
            } else {
                progress44.setVisibility(View.GONE);
            }
            if (resObject.getString("projProjectMaster").equalsIgnoreCase("true")) {
                progress45.setVisibility(View.VISIBLE);
            } else {
                progress45.setVisibility(View.GONE);
            }
            if (resObject.getString("projMbookSubActivityChild").equalsIgnoreCase("true")) {
                progress46.setVisibility(View.VISIBLE);
            } else {
                progress46.setVisibility(View.GONE);
            }
            if (resObject.getString("projMbookIowNmrChild").equalsIgnoreCase("true")) {
                progress47.setVisibility(View.VISIBLE);
            } else {
                progress47.setVisibility(View.GONE);
            }
            if (resObject.getString("projMbookIowChild").equalsIgnoreCase("true")) {
                progress48.setVisibility(View.VISIBLE);
            } else {
                progress48.setVisibility(View.GONE);
            }
            if (resObject.getString("projStageChild").equalsIgnoreCase("true")) {
                progress49.setVisibility(View.VISIBLE);
            } else {
                progress49.setVisibility(View.GONE);
            }
            if (resObject.getString("projLabourMaster").equalsIgnoreCase("true")) {
                progress50.setVisibility(View.VISIBLE);
            } else {
                progress50.setVisibility(View.GONE);
            }
            if (resObject.getString("projFormulaMaster").equalsIgnoreCase("true")) {
                progress51.setVisibility(View.VISIBLE);
            } else {
                progress51.setVisibility(View.GONE);
            }
            if (resObject.getString("projMbookIowGridChild").equalsIgnoreCase("true")) {
                progress52.setVisibility(View.VISIBLE);
            } else {
                progress52.setVisibility(View.GONE);
            }
           /* if (resObject.getString("projJobStageGridIow").equalsIgnoreCase("true")) {
                progress53.setVisibility(View.VISIBLE);
            } else {
                progress53.setVisibility(View.GONE);
            }
            if (resObject.getString("projJobStageGridMaster").equalsIgnoreCase("true")) {
                progress54.setVisibility(View.VISIBLE);
            } else {
                progress54.setVisibility(View.GONE);
            }*/
            if (resObject.getString("projIowMaster").equalsIgnoreCase("true")) {
                progress55.setVisibility(View.VISIBLE);
            } else {
                progress55.setVisibility(View.GONE);
            }
            if (resObject.getString("projIowMaterialChild").equalsIgnoreCase("true")) {
                progress56.setVisibility(View.VISIBLE);
            } else {
                progress56.setVisibility(View.GONE);
            }
            if (resObject.getString("projJobMaster").equalsIgnoreCase("true")) {
                progress57.setVisibility(View.VISIBLE);
            } else {
                progress57.setVisibility(View.GONE);
            }
            if (resObject.getString("projJobIowMaster").equalsIgnoreCase("true")) {
                progress58.setVisibility(View.VISIBLE);
            } else {
                progress58.setVisibility(View.GONE);
            }
            if (resObject.getString("projUserStageList").equalsIgnoreCase("true")) {
                progress59.setVisibility(View.VISIBLE);
            } else {
                progress59.setVisibility(View.GONE);
            }
            if (resObject.getString("projMrChild").equalsIgnoreCase("true")) {
                progress60.setVisibility(View.VISIBLE);
            } else {
                progress60.setVisibility(View.GONE);
            }
            if (resObject.getString("projMrItemScheduleChild").equalsIgnoreCase("true")) {
                progress61.setVisibility(View.VISIBLE);
            } else {
                progress61.setVisibility(View.GONE);
            }
            if (resObject.getString("projMirChild").equalsIgnoreCase("true")) {
                progress62.setVisibility(View.VISIBLE);
            } else {
                progress62.setVisibility(View.GONE);
            }
            if (resObject.getString("projVechicleMovementForm").equalsIgnoreCase("true")) {
                progress63.setVisibility(View.VISIBLE);
            } else {
                progress63.setVisibility(View.GONE);
            }
            if (resObject.getString("projIndentMaster").equalsIgnoreCase("true")) {
                progress64.setVisibility(View.VISIBLE);
            } else {
                progress64.setVisibility(View.GONE);
            }
            if (resObject.getString("projIndentChild").equalsIgnoreCase("true")) {
                progress65.setVisibility(View.VISIBLE);
            } else {
                progress65.setVisibility(View.GONE);
            }
            if (resObject.getString("projMinChild").equalsIgnoreCase("true")) {
                progress66.setVisibility(View.VISIBLE);
            } else {
                progress66.setVisibility(View.GONE);
            }
            if (resObject.getString("projGrnMaster").equalsIgnoreCase("true")) {
                progress67.setVisibility(View.VISIBLE);
            } else {
                progress67.setVisibility(View.GONE);
            }
            if (resObject.getString("projGrnItemChild").equalsIgnoreCase("true")) {
                progress68.setVisibility(View.VISIBLE);
            } else {
                progress68.setVisibility(View.GONE);
            }
            if (resObject.getString("weightData").equalsIgnoreCase("true")) {
                progress69.setVisibility(View.VISIBLE);
            } else {
                progress69.setVisibility(View.GONE);
            }
            if (resObject.getString("projGrnOtherChargeChild").equalsIgnoreCase("true")) {
                progress70.setVisibility(View.VISIBLE);
            } else {
                progress70.setVisibility(View.GONE);
            }
            if (resObject.getString("projBmrfMaster").equalsIgnoreCase("true")) {
                progress71.setVisibility(View.VISIBLE);
            } else {
                progress71.setVisibility(View.GONE);
            }
            if (resObject.getString("projMatBmrfChild").equalsIgnoreCase("true")) {
                progress72.setVisibility(View.VISIBLE);
            } else {
                progress72.setVisibility(View.GONE);
            }
            if (resObject.getString("projMrirMaster").equalsIgnoreCase("true")) {
                progress73.setVisibility(View.VISIBLE);
            } else {
                progress73.setVisibility(View.GONE);
            }
            if (resObject.getString("projMrirItemChild").equalsIgnoreCase("true")) {
                progress74.setVisibility(View.VISIBLE);
            } else {
                progress74.setVisibility(View.GONE);
            }
            if (resObject.getString("projMrirOtherChargeChild").equalsIgnoreCase("true")) {
                progress75.setVisibility(View.VISIBLE);
            } else {
                progress75.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtdnMaster").equalsIgnoreCase("true")) {
                progress76.setVisibility(View.VISIBLE);
            } else {
                progress76.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtdnChild").equalsIgnoreCase("true")) {
                progress77.setVisibility(View.VISIBLE);
            } else {
                progress77.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtdnOtherChargeChild").equalsIgnoreCase("true")) {
                progress78.setVisibility(View.VISIBLE);
            } else {
                progress78.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtnMaster").equalsIgnoreCase("true")) {
                progress79.setVisibility(View.VISIBLE);
            } else {
                progress79.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtnChild").equalsIgnoreCase("true")) {
                progress80.setVisibility(View.VISIBLE);
            } else {
                progress80.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtrnMaster").equalsIgnoreCase("true")) {
                progress81.setVisibility(View.VISIBLE);
            } else {
                progress81.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtnCloseMaster").equalsIgnoreCase("true")) {
                progress82.setVisibility(View.VISIBLE);
            } else {
                progress82.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoBillRecommendationMaster").equalsIgnoreCase("true")) {
                progress83.setVisibility(View.VISIBLE);
            } else {
                progress83.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoBillMaster").equalsIgnoreCase("true")) {
                progress84.setVisibility(View.VISIBLE);
            } else {
                progress84.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoBillRecommendationChild").equalsIgnoreCase("true")) {
                progress85.setVisibility(View.VISIBLE);
            } else {
                progress85.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoBillRecommendationPaymentChild").equalsIgnoreCase("true")) {
                progress86.setVisibility(View.VISIBLE);
            } else {
                progress86.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoBillRecommendationPaymentMaster").equalsIgnoreCase("true")) {
                progress87.setVisibility(View.VISIBLE);
            } else {
                progress87.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoBillRecommendationPoChild").equalsIgnoreCase("true")) {
                progress88.setVisibility(View.VISIBLE);
            } else {
                progress88.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoBillItemChild").equalsIgnoreCase("true")) {
                progress89.setVisibility(View.VISIBLE);
            } else {
                progress89.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoBillRecommendationPaymentDetChild").equalsIgnoreCase("true")) {
                progress90.setVisibility(View.VISIBLE);
            } else {
                progress90.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoActBillOthersChild").equalsIgnoreCase("true")) {
                progress91.setVisibility(View.VISIBLE);
            } else {
                progress91.setVisibility(View.GONE);
            }
            if (resObject.getString("projPoBillOtherChargeChild").equalsIgnoreCase("true")) {
                progress92.setVisibility(View.VISIBLE);
            } else {
                progress92.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtanMaster").equalsIgnoreCase("true")) {
                progress93.setVisibility(View.VISIBLE);
            } else {
                progress93.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtanChild").equalsIgnoreCase("true")) {
                progress94.setVisibility(View.VISIBLE);
            } else {
                progress94.setVisibility(View.GONE);
            }
            if (resObject.getString("projMtanOtherChargeChild").equalsIgnoreCase("true")) {
                progress95.setVisibility(View.VISIBLE);
            } else {
                progress95.setVisibility(View.GONE);
            }
            if (resObject.getString("projWoBillIowChild").equalsIgnoreCase("true")) {
                progress96.setVisibility(View.VISIBLE);
            } else {
                progress96.setVisibility(View.GONE);
            }
            if (resObject.getString("projWoBillSubActivityChild").equalsIgnoreCase("true")) {
                progress97.setVisibility(View.VISIBLE);
            } else {
                progress97.setVisibility(View.GONE);
            }
            if (resObject.getString("projWoBillNmrChild").equalsIgnoreCase("true")) {
                progress98.setVisibility(View.VISIBLE);
            } else {
                progress98.setVisibility(View.GONE);
            }
            if (resObject.getString("projMbookQaMaster").equalsIgnoreCase("true")) {
                progress99.setVisibility(View.VISIBLE);
            } else {
                progress99.setVisibility(View.GONE);
            }
            if (resObject.getString("projWoBillIowDetChild").equalsIgnoreCase("true")) {
                progress100.setVisibility(View.VISIBLE);
            } else {
                progress100.setVisibility(View.GONE);
            }
            if (resObject.getString("projWoBillIowTaxChild").equalsIgnoreCase("true")) {
                progress101.setVisibility(View.VISIBLE);
            } else {
                progress101.setVisibility(View.GONE);
            }
            if (resObject.getString("projWoBillSubActivityDetChild").equalsIgnoreCase("true")) {
                progress102.setVisibility(View.VISIBLE);
            } else {
                progress102.setVisibility(View.GONE);
            }
            if (resObject.getString("projWoBillNmrDetChild").equalsIgnoreCase("true")) {
                progress103.setVisibility(View.VISIBLE);
            } else {
                progress103.setVisibility(View.GONE);
            }
            if (resObject.getString("finAccountMaster").equalsIgnoreCase("true")) {
                progress104.setVisibility(View.VISIBLE);
            } else {
                progress104.setVisibility(View.GONE);
            }
            if (resObject.getString("projWoBillRecommendationPaymentMaster").equalsIgnoreCase("true")) {
                progress105.setVisibility(View.VISIBLE);
            } else {
                progress105.setVisibility(View.GONE);
            }
            if (resObject.getString("projWoBillRecommendationPaymentChild").equalsIgnoreCase("true")) {
                progress106.setVisibility(View.VISIBLE);
            } else {
                progress106.setVisibility(View.GONE);
            }
            if (resObject.getString("projMirnMaster").equalsIgnoreCase("true")) {
                progress107.setVisibility(View.VISIBLE);
            } else {
                progress107.setVisibility(View.GONE);
            }
            if (resObject.getString("projMirnChild").equalsIgnoreCase("true")) {
                progress108.setVisibility(View.VISIBLE);
            } else {
                progress108.setVisibility(View.GONE);
            }
            if (resObject.getString("projMbookLabourChild").equalsIgnoreCase("true")) {
                progress109.setVisibility(View.VISIBLE);
            } else {
                progress109.setVisibility(View.GONE);
            }
            JSONObject rightsObject = resObject.getJSONObject("Rights");
            JSONObject dashboardValue = resObject.getJSONObject("mobileRights");
            dashboardValue.put("Rights", rightsObject);
            RightsTableDao rightsTableDao = daoSession.getRightsTableDao();
            rightsTableDao.insertOrReplace(new RightsTable(uid, dashboardValue.toString(), uid));
            String updateDateR = dashboardValue.getString("currentDate");
            updateOnTableDao.insertOrReplace(new UpdateOnTable(uid + "_RightsTable", "RightsTable", uid, updateDateR, "Updated"));
            progress12.setVisibility(View.GONE);
            ApprovalStatusDao approvalStatusDao = daoSession.getApprovalStatusDao();
            JSONArray rightsStatusArray = resObject.getJSONArray("mobileApprovalRightsAllProcess");
            String insertSql = "Insert or Replace into " + approvalStatusDao.getTablename()
                    + " values (?,?,?,?,?,?,?);";
            daoSession = ((SCMApplication) context.getApplicationContext()).getDaoSession();
            db = ((SCMApplication) context.getApplicationContext()).getDb();
            DatabaseStatement insertStatement = db.compileStatement(insertSql);
            db.beginTransaction();
            for (int i = 0; i < rightsStatusArray.length(); i++) {
                insertStatement.clearBindings();
                insertStatement.bindString(1, rightsStatusArray.getJSONObject(i).getString("Name") + "_" + uid);
                insertStatement.bindString(2, uid);
                insertStatement.bindString(3, rightsStatusArray.getJSONObject(i).getString("Name"));
                insertStatement.bindString(4, rightsStatusArray.getJSONObject(i).getString("Approval_Status").toString());
                insertStatement.bindString(5, rightsStatusArray.getJSONObject(i).getString("Approval_Rights").toString());
                insertStatement.bindString(6, rightsStatusArray.getJSONObject(i).getString("Tabs"));
                insertStatement.bindString(7, rightsStatusArray.getJSONObject(i).getString("validationQry"));
                insertStatement.execute();
                Log.d("Approval Status Master", "Approval Status Inserted " + (i + 1));
            }
            insertStatement.close();         db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadingProgress() {
        //ProjectListWithStoreLoad
        String lastUpdateDate1 = "";
        String status1 = "";
        List<UpdateOnTable> TableLists1 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("ProjectListWithStoreLoad")).list();
        if (TableLists1.size()>0) {
            lastUpdateDate1 = TableLists1.get(0).getLast_update();
            status1 = TableLists1.get(0).getStatus();
            if (isLoadService) //1 != null)
                if (status1.equalsIgnoreCase("Progress")) {
                    progress1.setProgress(1);
                    msg1.setText("InProgress...");
                    msg1.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status1.equalsIgnoreCase("Loading")) {
                    progress1.setProgress(1);
                    msg1.setText("Loading...");
                    msg1.setTextColor(getResources().getColor(R.color.gray));
                } else if (status1.equalsIgnoreCase("Updating")) {
                    progress1.setProgress(1);
                    msg1.setText("Database inserting...");
                    msg1.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn1.setText(lastUpdateDate1);
        //ProjectMaterialBytLoad
        String lastUpdateDate2 = "";
        String status2 = "";
        List<UpdateOnTable> TableLists2 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjectMaterialBytLoad")).list();
        if (TableLists2.size() > 0) {
            lastUpdateDate2 = TableLists2.get(0).getLast_update();
            status2 = TableLists2.get(0).getStatus();
            if (isLoadService) //2 != null) {
                if (status2.equalsIgnoreCase("Progress")) {
                    progress2.setProgress(1);
                    msg2.setText("InProgress...");
                    msg2.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status2.equalsIgnoreCase("Loading")) {
                    progress2.setProgress(1);
                    msg2.setText("Loading...");
                    msg2.setTextColor(getResources().getColor(R.color.gray));
                } else if (status2.equalsIgnoreCase("Updating")) {
                    progress2.setProgress(1);
                    msg2.setText("Database inserting...");
                    msg2.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn2.setText(lastUpdateDate2);
        //ProjectContractorLoad
        String lastUpdateDate3 = "";
        String status3 = "";
        List<UpdateOnTable> TableLists3 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjectContractorLoad")).list();
        if (TableLists3.size() > 0) {
            lastUpdateDate3 = TableLists3.get(0).getLast_update();
            status3 = TableLists3.get(0).getStatus();
            if (isLoadService) //3 != null)
                if (status3.equalsIgnoreCase("Progress")) {
                    progress3.setProgress(1);
                    msg3.setText("InProgress...");
                    msg3.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status3.equalsIgnoreCase("Loading")) {
                    progress3.setProgress(1);
                    msg3.setText("Loading...");
                    msg3.setTextColor(getResources().getColor(R.color.gray));
                } else if (status3.equalsIgnoreCase("Updating")) {
                    progress3.setProgress(1);
                    msg3.setText("Database inserting...");
                    msg3.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn3.setText(lastUpdateDate3);
        //ProjectRequestedBytLoad
        String lastUpdateDate4 = "";
        String status4 = "";
        List<UpdateOnTable> TableLists4 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("ProjectRequestedBytLoad")).list();
        if (TableLists4.size() > 0) {
            lastUpdateDate4 = TableLists4.get(0).getLast_update();
            status4 = TableLists4.get(0).getStatus();
            if (isLoadService) //4 != null)
                if (status4.equalsIgnoreCase("Progress")) {
                    progress4.setProgress(1);
                    msg4.setText("InProgress...");
                    msg4.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status4.equalsIgnoreCase("Loading")) {
                    progress4.setProgress(1);
                    msg4.setText("Loading...");
                    msg4.setTextColor(getResources().getColor(R.color.gray));
                } else if (status4.equalsIgnoreCase("Updating")) {
                    progress4.setProgress(1);
                    msg4.setText("Database inserting...");
                    msg4.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn4.setText(lastUpdateDate4);
        //ProjectVendorName
        String lastUpdateDate5 = "";
        String status5 = "";
        List<UpdateOnTable> TableLists5 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjectVendorName")).list();
        if (TableLists5.size() > 0) {
            lastUpdateDate5 = TableLists5.get(0).getLast_update();
            status5 = TableLists5.get(0).getStatus();
            if (isLoadService) //5 != null)
                if (status5.equalsIgnoreCase("Progress")) {
                    progress5.setProgress(1);
                    msg5.setText("InProgress...");
                    msg5.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status5.equalsIgnoreCase("Loading")) {
                    progress5.setProgress(1);
                    msg5.setText("Loading...");
                    msg5.setTextColor(getResources().getColor(R.color.gray));
                } else if (status5.equalsIgnoreCase("Updating")) {
                    progress5.setProgress(1);
                    msg5.setText("Database inserting...");
                    msg5.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn5.setText(lastUpdateDate5);
        //ProjectStatusLoad
        String lastUpdateDate6 = "";
        String status6 = "";
        List<UpdateOnTable> TableLists6 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjectStatusLoad")).list();
        if (TableLists6.size() > 0) {
            lastUpdateDate6 = TableLists6.get(0).getLast_update();
            status6 = TableLists6.get(0).getStatus();
            if (isLoadService) //6 != null)
                if (status6.equalsIgnoreCase("Progress")) {
                    progress6.setProgress(1);
                    msg6.setText("InProgress...");
                    msg6.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status6.equalsIgnoreCase("Loading")) {
                    progress6.setProgress(1);
                    msg6.setText("Loading...");
                    msg6.setTextColor(getResources().getColor(R.color.gray));
                } else if (status6.equalsIgnoreCase("Updating")) {
                    progress6.setProgress(1);
                    msg6.setText("Database inserting...");
                    msg6.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn6.setText(lastUpdateDate6);
        //WoRefTable
        String lastUpdateDate7 = "";
        String status7 = "";
        List<UpdateOnTable> TableLists7 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("WoRefTable")).list();
        if (TableLists7.size() > 0) {
            lastUpdateDate7 = TableLists7.get(0).getLast_update();
            status7 = TableLists7.get(0).getStatus();
            if (isLoadService) //7 != null)
                if (status7.equalsIgnoreCase("Progress")) {
                    progress7.setProgress(1);
                    msg7.setText("InProgress...");
                    msg7.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status7.equalsIgnoreCase("Loading")) {
                    progress7.setProgress(1);
                    msg7.setText("Loading...");
                    msg7.setTextColor(getResources().getColor(R.color.gray));
                } else if (status7.equalsIgnoreCase("Updating")) {
                    progress7.setProgress(1);
                    msg7.setText("Database inserting...");
                    msg7.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn7.setText(lastUpdateDate7);
        //ProjIowMaterialChildTable
        String lastUpdateDate8 = "";
        String status8 = "";
        List<UpdateOnTable> TableLists8 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjIowMaterialChildTable")).list();
        if (TableLists8.size() > 0) {
            lastUpdateDate8 = TableLists8.get(0).getLast_update();
            status8 = TableLists8.get(0).getStatus();
            if (isLoadService) //8 != null)
                if (status8.equalsIgnoreCase("Progress")) {
                    progress8.setProgress(1);
                    msg8.setText("InProgress...");
                    msg8.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status8.equalsIgnoreCase("Loading")) {
                    progress8.setProgress(1);
                    msg8.setText("Loading...");
                    msg8.setTextColor(getResources().getColor(R.color.gray));
                } else if (status8.equalsIgnoreCase("Updating")) {
                    progress8.setProgress(1);
                    msg8.setText("Database inserting...");
                    msg8.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn8.setText(lastUpdateDate8);
        //ProjCmnMasterDetailsTable
        String lastUpdateDate9 = "";
        String status9 = "";
        List<UpdateOnTable> TableLists9 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjCmnMasterDetailsTable")).list();
        if (TableLists9.size() > 0) {
            lastUpdateDate9 = TableLists9.get(0).getLast_update();
            status9 = TableLists9.get(0).getStatus();
            if (isLoadService) //9 != null)
                if (status9.equalsIgnoreCase("Progress")) {
                    progress9.setProgress(1);
                    msg9.setText("InProgress...");
                    msg9.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status9.equalsIgnoreCase("Loading")) {
                    progress9.setProgress(1);
                    msg9.setText("Loading...");
                    msg9.setTextColor(getResources().getColor(R.color.gray));
                } else if (status9.equalsIgnoreCase("Updating")) {
                    progress9.setProgress(1);
                    msg9.setText("Database inserting...");
                    msg9.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn9.setText(lastUpdateDate9);
        //projMaterialUomChild
        String lastUpdateDate10 = "";
        String status10 = "";
        List<UpdateOnTable> TableLists10 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMaterialUomChild")).list();
        if (TableLists10.size() > 0) {
            lastUpdateDate10 = TableLists10.get(0).getLast_update();
            status10 = TableLists10.get(0).getStatus();
            if (isLoadService) //10 != null)
                if (status10.equalsIgnoreCase("Progress")) {
                    progress10.setProgress(1);
                    msg10.setText("InProgress...");
                    msg10.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status10.equalsIgnoreCase("Loading")) {
                    progress10.setProgress(1);
                    msg10.setText("Loading...");
                    msg10.setTextColor(getResources().getColor(R.color.gray));
                } else if (status10.equalsIgnoreCase("Updating")) {
                    progress10.setProgress(1);
                    msg10.setText("Database inserting...");
                    msg10.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn10.setText(lastUpdateDate10);
        //SatgeLoadBasedOnProjectList
        String lastUpdateDate11 = "";
        String status11 = "";
        List<UpdateOnTable> TableLists11 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("SatgeLoadBasedOnProjectList")).list();
        if (TableLists11.size() > 0) {
            lastUpdateDate11 = TableLists11.get(0).getLast_update();
            status11 = TableLists11.get(0).getStatus();
            if (isLoadService) //11 != null)
                if (status11.equalsIgnoreCase("Progress")) {
                    progress11.setProgress(1);
                    msg11.setText("InProgress...");
                    msg11.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status11.equalsIgnoreCase("Loading")) {
                    progress11.setProgress(1);
                    msg11.setText("Loading...");
                    msg11.setTextColor(getResources().getColor(R.color.gray));
                } else if (status11.equalsIgnoreCase("Updating")) {
                    Float divValue = ((Float.parseFloat("" + incrementStage) / Float.parseFloat("" + totalStage)) * 100);
                    int pbValue = (int) (Math.round(divValue));
                    progress11.setProgress(pbValue);
                    msg11.setText("Database inserting...");
                    msg11.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn11.setText(lastUpdateDate11);
        //RightsTable
        String lastUpdateDate12 = "";
        String status12 = "";
        List<UpdateOnTable> TableLists12 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("RightsTable")).list();
        if (TableLists12.size() > 0) {
            lastUpdateDate12 = TableLists12.get(0).getLast_update();
            status12 = TableLists12.get(0).getStatus();
            if (isLoadService) //12 != null)
                if (status12.equalsIgnoreCase("Progress")) {
                    progress12.setProgress(1);
                    msg12.setText("InProgress...");
                    msg12.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status12.equalsIgnoreCase("Loading")) {
                    progress12.setProgress(1);
                    msg12.setText("Loading...");
                    msg12.setTextColor(getResources().getColor(R.color.gray));
                } else if (status12.equalsIgnoreCase("Updating")) {
                    progress12.setProgress(1);
                    msg12.setText("Database inserting...");
                    msg12.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn12.setText(lastUpdateDate12);
        //proj_mir_proc_child
        String lastUpdateDate13 = "";
        String status13 = "";
        List<UpdateOnTable> TableLists13 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_mir_proc_child")).list();
        if (TableLists13.size() > 0) {
            lastUpdateDate13 = TableLists13.get(0).getLast_update();
            status13 = TableLists13.get(0).getStatus();
            if (isLoadService) //13 != null)
                if (status13.equalsIgnoreCase("Progress")) {
                    progress13.setProgress(1);
                    msg13.setText("InProgress...");
                    msg13.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status13.equalsIgnoreCase("Loading")) {
                    progress13.setProgress(1);
                    msg13.setText("Loading...");
                    msg13.setTextColor(getResources().getColor(R.color.gray));
                } else if (status13.equalsIgnoreCase("Updating")) {
                    progress13.setProgress(1);
                    msg13.setText("Database inserting...");
                    msg13.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn13.setText(lastUpdateDate13);
        //proj_mir_master
        String lastUpdateDate14 = "";
        String status14 = "";
        List<UpdateOnTable> TableLists14 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_mir_master")).list();
        if (TableLists14.size() > 0) {
            lastUpdateDate14 = TableLists14.get(0).getLast_update();
            status14 = TableLists14.get(0).getStatus();
            if (isLoadService) //14 != null)
                if (status14.equalsIgnoreCase("Progress")) {
                    progress14.setProgress(1);
                    msg14.setText("InProgress...");
                    msg14.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status14.equalsIgnoreCase("Loading")) {
                    progress14.setProgress(1);
                    msg14.setText("Loading...");
                    msg14.setTextColor(getResources().getColor(R.color.gray));
                } else if (status14.equalsIgnoreCase("Updating")) {
                    progress14.setProgress(1);
                    msg14.setText("Database inserting...");
                    msg14.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn14.setText(lastUpdateDate14);
        //proj_min_proc_child
        String lastUpdateDate15 = "";
        String status15 = "";
        List<UpdateOnTable> TableLists15 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_min_proc_child")).list();
        if (TableLists15.size() > 0) {
            lastUpdateDate15 = TableLists15.get(0).getLast_update();
            status15 = TableLists15.get(0).getStatus();
            if (isLoadService) //15 != null)
                if (status15.equalsIgnoreCase("Progress")) {
                    progress15.setProgress(1);
                    msg15.setText("InProgress...");
                    msg15.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status15.equalsIgnoreCase("Loading")) {
                    progress15.setProgress(1);
                    msg15.setText("Loading...");
                    msg15.setTextColor(getResources().getColor(R.color.gray));
                } else if (status15.equalsIgnoreCase("Updating")) {
                    progress15.setProgress(1);
                    msg15.setText("Database inserting...");
                    msg15.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn15.setText(lastUpdateDate15);
        //proj_min_master
        String lastUpdateDate16 = "";
        String status16 = "";
        List<UpdateOnTable> TableLists16 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_min_master")).list();
        if (TableLists16.size() > 0) {
            lastUpdateDate16 = TableLists16.get(0).getLast_update();
            status16 = TableLists16.get(0).getStatus();
            if (isLoadService) //16 != null)
                if (status16.equalsIgnoreCase("Progress")) {
                    progress16.setProgress(1);
                    msg16.setText("InProgress...");
                    msg16.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status16.equalsIgnoreCase("Loading")) {
                    progress16.setProgress(1);
                    msg16.setText("Loading...");
                    msg16.setTextColor(getResources().getColor(R.color.gray));
                } else if (status16.equalsIgnoreCase("Updating")) {
                    progress16.setProgress(1);
                    msg16.setText("Database inserting...");
                    msg16.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn16.setText(lastUpdateDate16);
        //proj_mr_proc_child
        String lastUpdateDate17 = "";
        String status17 = "";
        List<UpdateOnTable> TableLists17 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_mr_proc_child")).list();
        if (TableLists17.size() > 0) {
            lastUpdateDate17 = TableLists17.get(0).getLast_update();
            status17 = TableLists17.get(0).getStatus();
            if (isLoadService) //17 != null)
                if (status17.equalsIgnoreCase("Progress")) {
                    progress17.setProgress(1);
                    msg17.setText("InProgress...");
                    msg17.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status17.equalsIgnoreCase("Loading")) {
                    progress17.setProgress(1);
                    msg17.setText("Loading...");
                    msg17.setTextColor(getResources().getColor(R.color.gray));
                } else if (status17.equalsIgnoreCase("Updating")) {
                    progress17.setProgress(1);
                    msg17.setText("Database inserting...");
                    msg17.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn17.setText(lastUpdateDate17);
        //mobileRightsKeyMaster
        String lastUpdateDate18 = "";
        String status18 = "";
        List<UpdateOnTable> TableLists18 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("mobileRightsKeyMaster")).list();
        if (TableLists18.size() > 0) {
            lastUpdateDate18 = TableLists18.get(0).getLast_update();
            status18 = TableLists18.get(0).getStatus();
            if (isLoadService) //18 != null)
                if (status18.equalsIgnoreCase("Progress")) {
                    progress18.setProgress(1);
                    msg18.setText("InProgress...");
                    msg18.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status18.equalsIgnoreCase("Loading")) {
                    progress18.setProgress(1);
                    msg18.setText("Loading...");
                    msg18.setTextColor(getResources().getColor(R.color.gray));
                } else if (status18.equalsIgnoreCase("Updating")) {
                    progress18.setProgress(1);
                    msg18.setText("Database inserting...");
                    msg18.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn18.setText(lastUpdateDate18);
        //admEmpMaster
        String lastUpdateDate19 = "";
        String status19 = "";
        List<UpdateOnTable> TableLists19 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("admEmpMaster")).list();
        if (TableLists19.size() > 0) {
            lastUpdateDate19 = TableLists19.get(0).getLast_update();
            status19 = TableLists19.get(0).getStatus();
            if (isLoadService) //19 != null)
                if (status19.equalsIgnoreCase("Progress")) {
                    progress19.setProgress(1);
                    msg19.setText("InProgress...");
                    msg19.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status19.equalsIgnoreCase("Loading")) {
                    progress19.setProgress(1);
                    msg19.setText("Loading...");
                    msg19.setTextColor(getResources().getColor(R.color.gray));
                } else if (status19.equalsIgnoreCase("Updating")) {
                    progress19.setProgress(1);
                    msg19.setText("Database inserting...");
                    msg19.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn19.setText(lastUpdateDate19);
        //projUserMaterialList
        String lastUpdateDate20 = "";
        String status20 = "";
        List<UpdateOnTable> TableLists20 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("projUserMaterialList")).list();
        if (TableLists20.size() > 0) {
            lastUpdateDate20 = TableLists20.get(0).getLast_update();
            status20 = TableLists20.get(0).getStatus();
            if (isLoadService) //20 != null)
                if (status20.equalsIgnoreCase("Progress")) {
                    progress20.setProgress(1);
                    msg20.setText("InProgress...");
                    msg20.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status20.equalsIgnoreCase("Loading")) {
                    progress20.setProgress(1);
                    msg20.setText("Loading...");
                    msg20.setTextColor(getResources().getColor(R.color.gray));
                } else if (status20.equalsIgnoreCase("Updating")) {
                    progress20.setProgress(1);
                    msg20.setText("Database inserting...");
                    msg20.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn20.setText(lastUpdateDate20);
        //projAlternateMaterialMaster
        String lastUpdateDate21 = "";
        String status21 = "";
        List<UpdateOnTable> TableLists21 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projAlternateMaterialMaster")).list();
        if (TableLists21.size() > 0) {
            lastUpdateDate21 = TableLists21.get(0).getLast_update();
            status21 = TableLists21.get(0).getStatus();
            if (isLoadService) //21 != null)
                if (status21.equalsIgnoreCase("Progress")) {
                    progress21.setProgress(1);
                    msg21.setText("InProgress...");
                    msg21.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status21.equalsIgnoreCase("Loading")) {
                    progress21.setProgress(1);
                    msg21.setText("Loading...");
                    msg21.setTextColor(getResources().getColor(R.color.gray));
                } else if (status21.equalsIgnoreCase("Updating")) {
                    progress21.setProgress(1);
                    msg21.setText("Database inserting...");
                    msg21.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn21.setText(lastUpdateDate21);
        //mobileRightsMaster
        String lastUpdateDate22 = "";
        String status22 = "";
        List<UpdateOnTable> TableLists22 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("mobileRightsMaster")).list();
        if (TableLists22.size() > 0) {
            lastUpdateDate22 = TableLists22.get(0).getLast_update();
            status22 = TableLists22.get(0).getStatus();
            if (isLoadService) //22 != null)
                if (status22.equalsIgnoreCase("Progress")) {
                    progress22.setProgress(1);
                    msg22.setText("InProgress...");
                    msg22.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status22.equalsIgnoreCase("Loading")) {
                    progress22.setProgress(1);
                    msg22.setText("Loading...");
                    msg22.setTextColor(getResources().getColor(R.color.gray));
                } else if (status22.equalsIgnoreCase("Updating")) {
                    progress22.setProgress(1);
                    msg22.setText("Database inserting...");
                    msg22.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn22.setText(lastUpdateDate22);
        //projMaterialChild
        String lastUpdateDate23 = "";
        String status23 = "";
        List<UpdateOnTable> TableLists23 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMaterialChild")).list();
        if (TableLists23.size() > 0) {
            lastUpdateDate23 = TableLists23.get(0).getLast_update();
            status23 = TableLists23.get(0).getStatus();
            if (isLoadService) //23 != null)
                if (status23.equalsIgnoreCase("Progress")) {
                    progress23.setProgress(1);
                    msg23.setText("InProgress...");
                    msg23.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status23.equalsIgnoreCase("Loading")) {
                    progress23.setProgress(1);
                    msg23.setText("Loading...");
                    msg23.setTextColor(getResources().getColor(R.color.gray));
                } else if (status23.equalsIgnoreCase("Updating")) {
                    progress23.setProgress(1);
                    msg23.setText("Database inserting...");
                    msg23.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn23.setText(lastUpdateDate23);
        //projPoMaster
        String lastUpdateDate24 = "";
        String status24 = "";
        List<UpdateOnTable> TableLists24 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoMaster")).list();
        if (TableLists24.size() > 0) {
            lastUpdateDate24 = TableLists24.get(0).getLast_update();
            status24 = TableLists24.get(0).getStatus();
            if (isLoadService) //24 != null)
                if (status24.equalsIgnoreCase("Progress")) {
                    progress24.setProgress(1);
                    msg24.setText("InProgress...");
                    msg24.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status24.equalsIgnoreCase("Loading")) {
                    progress24.setProgress(1);
                    msg24.setText("Loading...");
                    msg24.setTextColor(getResources().getColor(R.color.gray));
                } else if (status24.equalsIgnoreCase("Updating")) {
                    progress24.setProgress(1);
                    msg24.setText("Database inserting...");
                    msg24.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn24.setText(lastUpdateDate24);
        //projPoItemChild
        String lastUpdateDate25 = "";
        String status25 = "";
        List<UpdateOnTable> TableLists25 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoItemChild")).list();
        if (TableLists25.size() > 0) {
            lastUpdateDate25 = TableLists25.get(0).getLast_update();
            status25 = TableLists25.get(0).getStatus();
            if (isLoadService) //25 != null)
                if (status25.equalsIgnoreCase("Progress")) {
                    progress25.setProgress(1);
                    msg25.setText("InProgress...");
                    msg25.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status25.equalsIgnoreCase("Loading")) {
                    progress25.setProgress(1);
                    msg25.setText("Loading...");
                    msg25.setTextColor(getResources().getColor(R.color.gray));
                } else if (status25.equalsIgnoreCase("Updating")) {
                    progress25.setProgress(1);
                    msg25.setText("Database inserting...");
                    msg25.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn25.setText(lastUpdateDate25);
        //projJobIowStageMaster
        String lastUpdateDate26 = "";
        String status26 = "";
        List<UpdateOnTable> TableLists26 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobIowStageMaster")).list();
        if (TableLists26.size() > 0) {
            lastUpdateDate26 = TableLists26.get(0).getLast_update();
            status26 = TableLists26.get(0).getStatus();
            if (isLoadService) //26 != null)
                if (status26.equalsIgnoreCase("Progress")) {
                    progress26.setProgress(1);
                    msg26.setText("InProgress...");
                    msg26.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status26.equalsIgnoreCase("Loading")) {
                    progress26.setProgress(1);
                    msg26.setText("Loading...");
                    msg26.setTextColor(getResources().getColor(R.color.gray));
                } else if (status26.equalsIgnoreCase("Updating")) {
                    progress26.setProgress(1);
                    msg26.setText("Database inserting...");
                    msg26.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn26.setText(lastUpdateDate26);
        //projStageIowMaterialDet
        String lastUpdateDate27 = "";
        String status27 = "";
        List<UpdateOnTable> TableLists27 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projStageIowMaterialDet")).list();
        if (TableLists27.size() > 0) {
            lastUpdateDate27 = TableLists27.get(0).getLast_update();
            status27 = TableLists27.get(0).getStatus();
            if (isLoadService) //27 != null)
                if (status27.equalsIgnoreCase("Progress")) {
                    progress27.setProgress(1);
                    msg27.setText("InProgress...");
                    msg27.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status27.equalsIgnoreCase("Loading")) {
                    progress27.setProgress(1);
                    msg27.setText("Loading...");
                    msg27.setTextColor(getResources().getColor(R.color.gray));
                } else if (status27.equalsIgnoreCase("Updating")) {
                    progress27.setProgress(1);
                    msg27.setText("Database inserting...");
                    msg27.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn27.setText(lastUpdateDate27);
        //projMrMaster
        String lastUpdateDate28 = "";
        String status28 = "";
        List<UpdateOnTable> TableLists28 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrMaster")).list();
        if (TableLists28.size() > 0) {
            lastUpdateDate28 = TableLists28.get(0).getLast_update();
            status28 = TableLists28.get(0).getStatus();
            if (isLoadService) //28 != null)
                if (status28.equalsIgnoreCase("Progress")) {
                    progress28.setProgress(1);
                    msg28.setText("InProgress...");
                    msg28.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status28.equalsIgnoreCase("Loading")) {
                    progress28.setProgress(1);
                    msg28.setText("Loading...");
                    msg28.setTextColor(getResources().getColor(R.color.gray));
                } else if (status28.equalsIgnoreCase("Updating")) {
                    progress28.setProgress(1);
                    msg28.setText("Database inserting...");
                    msg28.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn28.setText(lastUpdateDate28);
        //projVendorMasterView
        String lastUpdateDate29 = "";
        String status29 = "";
        List<UpdateOnTable> TableLists29 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projVendorMasterView")).list();
        if (TableLists29.size() > 0) {
            lastUpdateDate29 = TableLists29.get(0).getLast_update();
            status29 = TableLists29.get(0).getStatus();
            if (isLoadService) //29 != null)
                if (status29.equalsIgnoreCase("Progress")) {
                    progress29.setProgress(1);
                    msg29.setText("InProgress...");
                    msg29.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status29.equalsIgnoreCase("Loading")) {
                    progress29.setProgress(1);
                    msg29.setText("Loading...");
                    msg29.setTextColor(getResources().getColor(R.color.gray));
                } else if (status29.equalsIgnoreCase("Updating")) {
                    progress29.setProgress(1);
                    msg29.setText("Database inserting...");
                    msg29.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn29.setText(lastUpdateDate29);
        //admEmpMasterView
        String lastUpdateDate30 = "";
        String status30 = "";
        List<UpdateOnTable> TableLists30 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("admEmpMasterView")).list();
        if (TableLists30.size() > 0) {
            lastUpdateDate30 = TableLists30.get(0).getLast_update();
            status30 = TableLists30.get(0).getStatus();
            if (isLoadService) //30 != null)
                if (status30.equalsIgnoreCase("Progress")) {
                    progress30.setProgress(1);
                    msg30.setText("InProgress...");
                    msg30.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status30.equalsIgnoreCase("Loading")) {
                    progress30.setProgress(1);
                    msg30.setText("Loading...");
                    msg30.setTextColor(getResources().getColor(R.color.gray));
                } else if (status30.equalsIgnoreCase("Updating")) {
                    progress30.setProgress(1);
                    msg30.setText("Database inserting...");
                    msg30.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn30.setText(lastUpdateDate30);
        //arcApprovalConfig
        String lastUpdateDate31 = "";
        String status31 = "";
        List<UpdateOnTable> TableLists31 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("arcApprovalConfig")).list();
        if (TableLists31.size() > 0) {
            lastUpdateDate31 = TableLists31.get(0).getLast_update();
            status31 = TableLists31.get(0).getStatus();
            if (isLoadService) //31 != null)
                if (status31.equalsIgnoreCase("Progress")) {
                    progress31.setProgress(1);
                    msg31.setText("InProgress...");
                    msg31.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status31.equalsIgnoreCase("Loading")) {
                    progress31.setProgress(1);
                    msg31.setText("Loading...");
                    msg31.setTextColor(getResources().getColor(R.color.gray));
                } else if (status31.equalsIgnoreCase("Updating")) {
                    progress31.setProgress(1);
                    msg31.setText("Database inserting...");
                    msg31.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn31.setText(lastUpdateDate31);
        //projUserProjectList
        String lastUpdateDate32 = "";
        String status32 = "";
        List<UpdateOnTable> TableLists32 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projUserProjectList")).list();
        if (TableLists32.size() > 0) {
            lastUpdateDate32 = TableLists32.get(0).getLast_update();
            status32 = TableLists32.get(0).getStatus();
            if (isLoadService) //32 != null)
                if (status32.equalsIgnoreCase("Progress")) {
                    progress32.setProgress(1);
                    msg32.setText("InProgress...");
                    msg32.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status32.equalsIgnoreCase("Loading")) {
                    progress32.setProgress(1);
                    msg32.setText("Loading...");
                    msg32.setTextColor(getResources().getColor(R.color.gray));
                } else if (status32.equalsIgnoreCase("Updating")) {
                    progress32.setProgress(1);
                    msg32.setText("Database inserting...");
                    msg32.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn32.setText(lastUpdateDate32);
        //projStoreStock
        String lastUpdateDate33 = "";
        String status33 = "";
        List<UpdateOnTable> TableLists33 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projStoreStock")).list();
        if (TableLists33.size() > 0) {
            lastUpdateDate33 = TableLists33.get(0).getLast_update();
            status33 = TableLists33.get(0).getStatus();
            if (isLoadService) //33 != null)
                if (status33.equalsIgnoreCase("Progress")) {
                    progress33.setProgress(1);
                    msg33.setText("InProgress...");
                    msg33.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status33.equalsIgnoreCase("Loading")) {
                    progress33.setProgress(1);
                    msg33.setText("Loading...");
                    msg33.setTextColor(getResources().getColor(R.color.gray));
                } else if (status33.equalsIgnoreCase("Updating")) {
                    progress33.setProgress(1);
                    msg33.setText("Database inserting...");
                    msg33.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn33.setText(lastUpdateDate33);
        //projStoreMaster
        String lastUpdateDate34 = "";
        String status34 = "";
        List<UpdateOnTable> TableLists34 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projStoreMaster")).list();
        if (TableLists34.size() > 0) {
            lastUpdateDate34 = TableLists34.get(0).getLast_update();
            status34 = TableLists34.get(0).getStatus();
            if (isLoadService) //34 != null)
                if (status34.equalsIgnoreCase("Progress")) {
                    progress34.setProgress(1);
                    msg34.setText("InProgress...");
                    msg34.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status34.equalsIgnoreCase("Loading")) {
                    progress34.setProgress(1);
                    msg34.setText("Loading...");
                    msg34.setTextColor(getResources().getColor(R.color.gray));
                } else if (status34.equalsIgnoreCase("Updating")) {
                    progress34.setProgress(1);
                    msg34.setText("Database inserting...");
                    msg34.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn34.setText(lastUpdateDate34);
        //projProjectAddressMaster
        String lastUpdateDate35 = "";
        String status35 = "";
        List<UpdateOnTable> TableLists35 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projProjectAddressMaster")).list();
        if (TableLists35.size() > 0) {
            lastUpdateDate35 = TableLists35.get(0).getLast_update();
            status35 = TableLists35.get(0).getStatus();
            if (isLoadService) //35 != null)
                if (status35.equalsIgnoreCase("Progress")) {
                    progress35.setProgress(1);
                    msg35.setText("InProgress...");
                    msg35.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status35.equalsIgnoreCase("Loading")) {
                    progress35.setProgress(1);
                    msg35.setText("Loading...");
                    msg35.setTextColor(getResources().getColor(R.color.gray));
                } else if (status35.equalsIgnoreCase("Updating")) {
                    progress35.setProgress(1);
                    msg35.setText("Database inserting...");
                    msg35.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn35.setText(lastUpdateDate35);
        //projPoOtherChargeChild
        String lastUpdateDate36 = "";
        String status36 = "";
        List<UpdateOnTable> TableLists36 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoOtherChargeChild")).list();
        if (TableLists36.size() > 0) {
            lastUpdateDate36 = TableLists36.get(0).getLast_update();
            status36 = TableLists36.get(0).getStatus();
            if (isLoadService) //36 != null)
                if (status36.equalsIgnoreCase("Progress")) {
                    progress36.setProgress(1);
                    msg36.setText("InProgress...");
                    msg36.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status36.equalsIgnoreCase("Loading")) {
                    progress36.setProgress(1);
                    msg36.setText("Loading...");
                    msg36.setTextColor(getResources().getColor(R.color.gray));
                } else if (status36.equalsIgnoreCase("Updating")) {
                    progress36.setProgress(1);
                    msg36.setText("Database inserting...");
                    msg36.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn36.setText(lastUpdateDate36);
        //cmnPartyAddressInfo
        String lastUpdateDate37 = "";
        String status37 = "";
        List<UpdateOnTable> TableLists37 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("cmnPartyAddressInfo")).list();
        if (TableLists37.size() > 0) {
            lastUpdateDate37 = TableLists37.get(0).getLast_update();
            status37 = TableLists37.get(0).getStatus();
            if (isLoadService) //37 != null)
                if (status37.equalsIgnoreCase("Progress")) {
                    progress37.setProgress(1);
                    msg37.setText("InProgress...");
                    msg37.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status37.equalsIgnoreCase("Loading")) {
                    progress37.setProgress(1);
                    msg37.setText("Loading...");
                    msg37.setTextColor(getResources().getColor(R.color.gray));
                } else if (status37.equalsIgnoreCase("Updating")) {
                    progress37.setProgress(1);
                    msg37.setText("Database inserting...");
                    msg37.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn37.setText(lastUpdateDate37);
        //projPoGernalTermsChild
        String lastUpdateDate38 = "";
        String status38 = "";
        List<UpdateOnTable> TableLists38 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoGernalTermsChild")).list();
        if (TableLists38.size() > 0) {
            lastUpdateDate38 = TableLists38.get(0).getLast_update();
            status38 = TableLists38.get(0).getStatus();
            if (isLoadService) //38 != null)
                if (status38.equalsIgnoreCase("Progress")) {
                    progress38.setProgress(1);
                    msg38.setText("InProgress...");
                    msg38.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status38.equalsIgnoreCase("Loading")) {
                    progress38.setProgress(1);
                    msg38.setText("Loading...");
                    msg38.setTextColor(getResources().getColor(R.color.gray));
                } else if (status38.equalsIgnoreCase("Updating")) {
                    progress38.setProgress(1);
                    msg38.setText("Database inserting...");
                    msg38.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn38.setText(lastUpdateDate38);
        //projPoTermsChild
        String lastUpdateDate39 = "";
        String status39 = "";
        List<UpdateOnTable> TableLists39 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoTermsChild")).list();
        if (TableLists39.size() > 0) {
            lastUpdateDate39 = TableLists39.get(0).getLast_update();
            status39 = TableLists39.get(0).getStatus();
            if (isLoadService) //39 != null)
                if (status39.equalsIgnoreCase("Progress")) {
                    progress39.setProgress(1);
                    msg39.setText("InProgress...");
                    msg39.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status39.equalsIgnoreCase("Loading")) {
                    progress39.setProgress(1);
                    msg39.setText("Loading...");
                    msg39.setTextColor(getResources().getColor(R.color.gray));
                } else if (status39.equalsIgnoreCase("Updating")) {
                    progress39.setProgress(1);
                    msg39.setText("Database inserting...");
                    msg39.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn39.setText(lastUpdateDate39);
        //cmnTaxMaster
        String lastUpdateDate40 = "";
        String status40 = "";
        List<UpdateOnTable> TableLists40 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("cmnTaxMaster")).list();
        if (TableLists40.size() > 0) {
            lastUpdateDate40 = TableLists40.get(0).getLast_update();
            status40 = TableLists40.get(0).getStatus();
            if (isLoadService) //40 != null)
                if (status40.equalsIgnoreCase("Progress")) {
                    progress40.setProgress(1);
                    msg40.setText("InProgress...");
                    msg40.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status40.equalsIgnoreCase("Loading")) {
                    progress40.setProgress(1);
                    msg40.setText("Loading...");
                    msg40.setTextColor(getResources().getColor(R.color.gray));
                } else if (status40.equalsIgnoreCase("Updating")) {
                    progress40.setProgress(1);
                    msg40.setText("Database inserting...");
                    msg40.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn40.setText(lastUpdateDate40);
        //cmnPartyCompanyInfo
        String lastUpdateDate41 = "";
        String status41 = "";
        List<UpdateOnTable> TableLists41 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("cmnPartyCompanyInfo")).list();
        if (TableLists41.size() > 0) {
            lastUpdateDate41 = TableLists41.get(0).getLast_update();
            status41 = TableLists41.get(0).getStatus();
            if (isLoadService) //41 != null)
                if (status41.equalsIgnoreCase("Progress")) {
                    progress41.setProgress(1);
                    msg41.setText("InProgress...");
                    msg41.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status41.equalsIgnoreCase("Loading")) {
                    progress41.setProgress(1);
                    msg41.setText("Loading...");
                    msg41.setTextColor(getResources().getColor(R.color.gray));
                } else if (status41.equalsIgnoreCase("Updating")) {
                    progress41.setProgress(1);
                    msg41.setText("Database inserting...");
                    msg41.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn41.setText(lastUpdateDate41);
        //cmnPartyIdDocInfo
        String lastUpdateDate42 = "";
        String status42 = "";
        List<UpdateOnTable> TableLists42 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("cmnPartyIdDocInfo")).list();
        if (TableLists42.size() > 0) {
            lastUpdateDate42 = TableLists42.get(0).getLast_update();
            status42 = TableLists42.get(0).getStatus();
            if (isLoadService) //42 != null)
                if (status42.equalsIgnoreCase("Progress")) {
                    progress42.setProgress(1);
                    msg42.setText("InProgress...");
                    msg42.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status42.equalsIgnoreCase("Loading")) {
                    progress42.setProgress(1);
                    msg42.setText("Loading...");
                    msg42.setTextColor(getResources().getColor(R.color.gray));
                } else if (status42.equalsIgnoreCase("Updating")) {
                    progress42.setProgress(1);
                    msg42.setText("Database inserting...");
                    msg42.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn42.setText(lastUpdateDate42);
        //projMbookMaster
        String lastUpdateDate43 = "";
        String status43 = "";
        List<UpdateOnTable> TableLists43 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookMaster")).list();
        if (TableLists43.size() > 0) {
            lastUpdateDate43 = TableLists43.get(0).getLast_update();
            status43 = TableLists43.get(0).getStatus();
            if (isLoadService) //43 != null)
                if (status43.equalsIgnoreCase("Progress")) {
                    progress43.setProgress(1);
                    msg43.setText("InProgress...");
                    msg43.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status43.equalsIgnoreCase("Loading")) {
                    progress43.setProgress(1);
                    msg43.setText("Loading...");
                    msg43.setTextColor(getResources().getColor(R.color.gray));
                } else if (status43.equalsIgnoreCase("Updating")) {
                    progress43.setProgress(1);
                    msg43.setText("Database inserting...");
                    msg43.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn43.setText(lastUpdateDate43);
        //projContractorMasterView
        String lastUpdateDate44 = "";
        String status44 = "";
        List<UpdateOnTable> TableLists44 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projContractorMasterView")).list();
        if (TableLists44.size() > 0) {
            lastUpdateDate44 = TableLists44.get(0).getLast_update();
            status44 = TableLists44.get(0).getStatus();
            if (isLoadService) //44 != null)
                if (status44.equalsIgnoreCase("Progress")) {
                    progress44.setProgress(1);
                    msg44.setText("InProgress...");
                    msg44.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status44.equalsIgnoreCase("Loading")) {
                    progress44.setProgress(1);
                    msg44.setText("Loading...");
                    msg44.setTextColor(getResources().getColor(R.color.gray));
                } else if (status44.equalsIgnoreCase("Updating")) {
                    progress44.setProgress(1);
                    msg44.setText("Database inserting...");
                    msg44.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn44.setText(lastUpdateDate44);
        //projProjectMaster
        String lastUpdateDate45 = "";
        String status45 = "";
        List<UpdateOnTable> TableLists45 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projProjectMaster")).list();
        if (TableLists45.size() > 0) {
            lastUpdateDate45 = TableLists45.get(0).getLast_update();
            status45 = TableLists45.get(0).getStatus();
            if (isLoadService) //45 != null)
                if (status45.equalsIgnoreCase("Progress")) {
                    progress45.setProgress(1);
                    msg45.setText("InProgress...");
                    msg45.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status45.equalsIgnoreCase("Loading")) {
                    progress45.setProgress(1);
                    msg45.setText("Loading...");
                    msg45.setTextColor(getResources().getColor(R.color.gray));
                } else if (status45.equalsIgnoreCase("Updating")) {
                    progress45.setProgress(1);
                    msg45.setText("Database inserting...");
                    msg45.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn45.setText(lastUpdateDate45);
        //projMbookSubActivityChild
        String lastUpdateDate46 = "";
        String status46 = "";
        List<UpdateOnTable> TableLists46 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookSubActivityChild")).list();
        if (TableLists46.size() > 0) {
            lastUpdateDate46 = TableLists46.get(0).getLast_update();
            status46 = TableLists46.get(0).getStatus();
            if (isLoadService) //46 != null)
                if (status46.equalsIgnoreCase("Progress")) {
                    progress46.setProgress(1);
                    msg46.setText("InProgress...");
                    msg46.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status46.equalsIgnoreCase("Loading")) {
                    progress46.setProgress(1);
                    msg46.setText("Loading...");
                    msg46.setTextColor(getResources().getColor(R.color.gray));
                } else if (status46.equalsIgnoreCase("Updating")) {
                    progress46.setProgress(1);
                    msg46.setText("Database inserting...");
                    msg46.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn46.setText(lastUpdateDate46);
        //projMbookIowNmrChild
        String lastUpdateDate47 = "";
        String status47 = "";
        List<UpdateOnTable> TableLists47 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookIowNmrChild")).list();
        if (TableLists47.size() > 0) {
            lastUpdateDate47 = TableLists47.get(0).getLast_update();
            status47 = TableLists47.get(0).getStatus();
            if (isLoadService) //47 != null)
                if (status47.equalsIgnoreCase("Progress")) {
                    progress47.setProgress(1);
                    msg47.setText("InProgress...");
                    msg47.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status47.equalsIgnoreCase("Loading")) {
                    progress47.setProgress(1);
                    msg47.setText("Loading...");
                    msg47.setTextColor(getResources().getColor(R.color.gray));
                } else if (status47.equalsIgnoreCase("Updating")) {
                    progress47.setProgress(1);
                    msg47.setText("Database inserting...");
                    msg47.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn47.setText(lastUpdateDate47);
        //projMbookiowChild
        String lastUpdateDate48 = "";
        String status48 = "";
        List<UpdateOnTable> TableLists48 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookiowChild")).list();
        if (TableLists48.size() > 0) {
            lastUpdateDate48 = TableLists48.get(0).getLast_update();
            status48 = TableLists48.get(0).getStatus();
            if (isLoadService) //48 != null)
                if (status48.equalsIgnoreCase("Progress")) {
                    progress48.setProgress(1);
                    msg48.setText("InProgress...");
                    msg48.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status48.equalsIgnoreCase("Loading")) {
                    progress48.setProgress(1);
                    msg48.setText("Loading...");
                    msg48.setTextColor(getResources().getColor(R.color.gray));
                } else if (status48.equalsIgnoreCase("Updating")) {
                    progress48.setProgress(1);
                    msg48.setText("Database inserting...");
                    msg48.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn48.setText(lastUpdateDate48);
        //projStageChild
        String lastUpdateDate49 = "";
        String status49 = "";
        List<UpdateOnTable> TableLists49 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projStageChild")).list();
        if (TableLists49.size() > 0) {
            lastUpdateDate49 = TableLists49.get(0).getLast_update();
            status49 = TableLists49.get(0).getStatus();
            if (isLoadService) //49 != null)
                if (status49.equalsIgnoreCase("Progress")) {
                    progress49.setProgress(1);
                    msg49.setText("InProgress...");
                    msg49.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status49.equalsIgnoreCase("Loading")) {
                    progress49.setProgress(1);
                    msg49.setText("Loading...");
                    msg49.setTextColor(getResources().getColor(R.color.gray));
                } else if (status49.equalsIgnoreCase("Updating")) {
                    progress49.setProgress(1);
                    msg49.setText("Database inserting...");
                    msg49.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn49.setText(lastUpdateDate49);
        //projLabourMaster
        String lastUpdateDate50 = "";
        String status50 = "";
        List<UpdateOnTable> TableLists50 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projLabourMaster")).list();
        if (TableLists50.size() > 0) {
            lastUpdateDate50 = TableLists50.get(0).getLast_update();
            status50 = TableLists50.get(0).getStatus();
            if (isLoadService) //50 != null)
                if (status50.equalsIgnoreCase("Progress")) {
                    progress50.setProgress(1);
                    msg50.setText("InProgress...");
                    msg50.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status50.equalsIgnoreCase("Loading")) {
                    progress50.setProgress(1);
                    msg50.setText("Loading...");
                    msg50.setTextColor(getResources().getColor(R.color.gray));
                } else if (status50.equalsIgnoreCase("Updating")) {
                    progress50.setProgress(1);
                    msg50.setText("Database inserting...");
                    msg50.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn50.setText(lastUpdateDate50);
        //projFormulaMaster
        String lastUpdateDate51 = "";
        String status51 = "";
        List<UpdateOnTable> TableLists51 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projFormulaMaster")).list();
        if (TableLists51.size() > 0) {
            lastUpdateDate51 = TableLists51.get(0).getLast_update();
            status51 = TableLists51.get(0).getStatus();
            if (isLoadService) //51 != null)
                if (status51.equalsIgnoreCase("Progress")) {
                    progress51.setProgress(1);
                    msg51.setText("InProgress...");
                    msg51.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status51.equalsIgnoreCase("Loading")) {
                    progress51.setProgress(1);
                    msg51.setText("Loading...");
                    msg51.setTextColor(getResources().getColor(R.color.gray));
                } else if (status51.equalsIgnoreCase("Updating")) {
                    progress51.setProgress(1);
                    msg51.setText("Database inserting...");
                    msg51.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn51.setText(lastUpdateDate51);
        //projMbookIowGridChild
        String lastUpdateDate52 = "";
        String status52 = "";
        List<UpdateOnTable> TableLists52 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookIowGridChild")).list();
        if (TableLists52.size() > 0) {
            lastUpdateDate52 = TableLists52.get(0).getLast_update();
            status52 = TableLists52.get(0).getStatus();
            if (isLoadService) //52 != null)
                if (status52.equalsIgnoreCase("Progress")) {
                    progress52.setProgress(1);
                    msg52.setText("InProgress...");
                    msg52.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status52.equalsIgnoreCase("Loading")) {
                    progress52.setProgress(1);
                    msg52.setText("Loading...");
                    msg52.setTextColor(getResources().getColor(R.color.gray));
                } else if (status52.equalsIgnoreCase("Updating")) {
                    progress52.setProgress(1);
                    msg52.setText("Database inserting...");
                    msg52.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn52.setText(lastUpdateDate52);
      /*  //projJobStageGridIow
        String lastUpdateDate53 = "";
        String status53 = "";
        List<UpdateOnTable> TableLists53 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobStageGridIow")).list();
        if (TableLists53.size() > 0) {
            lastUpdateDate53 = TableLists53.get(0).getLast_update();
            status53 = TableLists53.get(0).getStatus();
            if (isLoadService) //53 != null)
                if (status53.equalsIgnoreCase("Progress")) {
                    progress53.setProgress(1);
                    msg53.setText("InProgress...");
                    msg53.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status53.equalsIgnoreCase("Loading")) {
                    progress53.setProgress(1);
                    msg53.setText("Loading...");
                    msg53.setTextColor(getResources().getColor(R.color.gray));
                } else if (status53.equalsIgnoreCase("Updating")) {
                    progress53.setProgress(1);
                    msg53.setText("Database inserting...");
                    msg53.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn53.setText(lastUpdateDate53);
        //projJobStageGridMaster
        String lastUpdateDate54 = "";
        String status54 = "";
        List<UpdateOnTable> TableLists54 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobStageGridMaster")).list();
        if (TableLists54.size() > 0) {
            lastUpdateDate54 = TableLists54.get(0).getLast_update();
            status54 = TableLists54.get(0).getStatus();
            if (isLoadService) //54 != null)
                if (status54.equalsIgnoreCase("Progress")) {
                    progress54.setProgress(1);
                    msg54.setText("InProgress...");
                    msg54.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status54.equalsIgnoreCase("Loading")) {
                    progress54.setProgress(1);
                    msg54.setText("Loading...");
                    msg54.setTextColor(getResources().getColor(R.color.gray));
                } else if (status54.equalsIgnoreCase("Updating")) {
                    progress54.setProgress(1);
                    msg54.setText("Database inserting...");
                    msg54.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn54.setText(lastUpdateDate54);*/
        //projIowMaster
        String lastUpdateDate55 = "";
        String status55 = "";
        List<UpdateOnTable> TableLists55 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projIowMaster")).list();
        if (TableLists55.size() > 0) {
            lastUpdateDate55 = TableLists55.get(0).getLast_update();
            status55 = TableLists55.get(0).getStatus();
            if (isLoadService) //55 != null)
                if (status55.equalsIgnoreCase("Progress")) {
                    progress55.setProgress(1);
                    msg55.setText("InProgress...");
                    msg55.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status55.equalsIgnoreCase("Loading")) {
                    progress55.setProgress(1);
                    msg55.setText("Loading...");
                    msg55.setTextColor(getResources().getColor(R.color.gray));
                } else if (status55.equalsIgnoreCase("Updating")) {
                    progress55.setProgress(1);
                    msg55.setText("Database inserting...");
                    msg55.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn55.setText(lastUpdateDate55);
        //ProjIowMaterialChild
        String lastUpdateDate56 = "";
        String status56 = "";
        List<UpdateOnTable> TableLists56 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projIowMaterialChild")).list();
        if (TableLists56.size() > 0) {
            lastUpdateDate56 = TableLists56.get(0).getLast_update();
            status56 = TableLists56.get(0).getStatus();
            if (isLoadService) //56 != null)
                if (status56.equalsIgnoreCase("Progress")) {
                    progress56.setProgress(1);
                    msg56.setText("InProgress...");
                    msg56.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status56.equalsIgnoreCase("Loading")) {
                    progress56.setProgress(1);
                    msg56.setText("Loading...");
                    msg56.setTextColor(getResources().getColor(R.color.gray));
                } else if (status56.equalsIgnoreCase("Updating")) {
                    progress56.setProgress(1);
                    msg56.setText("Database inserting...");
                    msg56.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn56.setText(lastUpdateDate56);
        //ProjJobMaster
        String lastUpdateDate57 = "";
        String status57 = "";
        List<UpdateOnTable> TableLists57 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobMaster")).list();
        if (TableLists57.size() > 0) {
            lastUpdateDate57 = TableLists57.get(0).getLast_update();
            status57 = TableLists57.get(0).getStatus();
            if (isLoadService) //57 != null)
                if (status57.equalsIgnoreCase("Progress")) {
                    progress57.setProgress(1);
                    msg57.setText("InProgress...");
                    msg57.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status57.equalsIgnoreCase("Loading")) {
                    progress57.setProgress(1);
                    msg57.setText("Loading...");
                    msg57.setTextColor(getResources().getColor(R.color.gray));
                } else if (status57.equalsIgnoreCase("Updating")) {
                    progress57.setProgress(1);
                    msg57.setText("Database inserting...");
                    msg57.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn57.setText(lastUpdateDate57);
        //ProjJobIowMaster
        String lastUpdateDate58 = "";
        String status58 = "";
        List<UpdateOnTable> TableLists58 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobIowMaster")).list();
        if (TableLists58.size() > 0) {
            lastUpdateDate58 = TableLists58.get(0).getLast_update();
            status58 = TableLists58.get(0).getStatus();
            if (isLoadService) //58 != null)
                if (status58.equalsIgnoreCase("Progress")) {
                    progress58.setProgress(1);
                    msg58.setText("InProgress...");
                    msg58.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status58.equalsIgnoreCase("Loading")) {
                    progress58.setProgress(1);
                    msg58.setText("Loading...");
                    msg58.setTextColor(getResources().getColor(R.color.gray));
                } else if (status58.equalsIgnoreCase("Updating")) {
                    progress58.setProgress(1);
                    msg58.setText("Database inserting...");
                    msg58.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn58.setText(lastUpdateDate58);
        //projUserStageList
        String lastUpdateDate59 = "";
        String status59 = "";
        List<UpdateOnTable> TableLists59 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("projUserStageList")).list();
        if (TableLists59.size() > 0) {
            lastUpdateDate59 = TableLists59.get(0).getLast_update();
            status59 = TableLists59.get(0).getStatus();
            if (isLoadService) //59 != null)
                if (status59.equalsIgnoreCase("Progress")) {
                    progress59.setProgress(1);
                    msg59.setText("InProgress...");
                    msg59.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status59.equalsIgnoreCase("Loading")) {
                    progress59.setProgress(1);
                    msg59.setText("Loading...");
                    msg59.setTextColor(getResources().getColor(R.color.gray));
                } else if (status59.equalsIgnoreCase("Updating")) {
                    progress59.setProgress(1);
                    msg59.setText("Database inserting...");
                    msg59.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn59.setText(lastUpdateDate59);
        //projMrChild
        String lastUpdateDate60 = "";
        String status60 = "";
        List<UpdateOnTable> TableLists60 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrChild")).list();
        if (TableLists60.size() > 0) {
            lastUpdateDate60 = TableLists60.get(0).getLast_update();
            status60 = TableLists60.get(0).getStatus();
            if (isLoadService) //60 != null)
                if (status60.equalsIgnoreCase("Progress")) {
                    progress60.setProgress(1);
                    msg60.setText("InProgress...");
                    msg60.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status60.equalsIgnoreCase("Loading")) {
                    progress60.setProgress(1);
                    msg60.setText("Loading...");
                    msg60.setTextColor(getResources().getColor(R.color.gray));
                } else if (status60.equalsIgnoreCase("Updating")) {
                    progress60.setProgress(1);
                    msg60.setText("Database inserting...");
                    msg60.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn60.setText(lastUpdateDate60);
        //projMrItemScheduleChild
        String lastUpdateDate61 = "";
        String status61 = "";
        List<UpdateOnTable> TableLists61 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrItemScheduleChild")).list();
        if (TableLists61.size() > 0) {
            lastUpdateDate61 = TableLists61.get(0).getLast_update();
            status61 = TableLists61.get(0).getStatus();
            if (isLoadService) //61 != null)
                if (status61.equalsIgnoreCase("Progress")) {
                    progress61.setProgress(1);
                    msg61.setText("InProgress...");
                    msg61.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status61.equalsIgnoreCase("Loading")) {
                    progress61.setProgress(1);
                    msg61.setText("Loading...");
                    msg61.setTextColor(getResources().getColor(R.color.gray));
                } else if (status61.equalsIgnoreCase("Updating")) {
                    progress61.setProgress(1);
                    msg61.setText("Database inserting...");
                    msg61.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn61.setText(lastUpdateDate61);
        //projMirChild
        String lastUpdateDate62 = "";
        String status62 = "";
        List<UpdateOnTable> TableLists62 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMirChild")).list();
        if (TableLists62.size() > 0) {
            lastUpdateDate62 = TableLists62.get(0).getLast_update();
            status62 = TableLists62.get(0).getStatus();
            if (isLoadService) //62 != null)
                if (status62.equalsIgnoreCase("Progress")) {
                    progress62.setProgress(1);
                    msg62.setText("InProgress...");
                    msg62.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status62.equalsIgnoreCase("Loading")) {
                    progress62.setProgress(1);
                    msg62.setText("Loading...");
                    msg62.setTextColor(getResources().getColor(R.color.gray));
                } else if (status62.equalsIgnoreCase("Updating")) {
                    progress62.setProgress(1);
                    msg62.setText("Database inserting...");
                    msg62.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn62.setText(lastUpdateDate62);
        //projVechicleMovementForm
        String lastUpdateDate63 = "";
        String status63 = "";
        List<UpdateOnTable> TableLists63 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projVechicleMovementForm")).list();
        if (TableLists63.size() > 0) {
            lastUpdateDate63 = TableLists63.get(0).getLast_update();
            status63 = TableLists63.get(0).getStatus();
            if (isLoadService) //63 != null)
                if (status63.equalsIgnoreCase("Progress")) {
                    progress63.setProgress(1);
                    msg63.setText("InProgress...");
                    msg63.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status63.equalsIgnoreCase("Loading")) {
                    progress63.setProgress(1);
                    msg63.setText("Loading...");
                    msg63.setTextColor(getResources().getColor(R.color.gray));
                } else if (status63.equalsIgnoreCase("Updating")) {
                    progress63.setProgress(1);
                    msg63.setText("Database inserting...");
                    msg63.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn63.setText(lastUpdateDate63);
        //projIndentMaster
        String lastUpdateDate64 = "";
        String status64 = "";
        List<UpdateOnTable> TableLists64 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projIndentMaster")).list();
        if (TableLists64.size() > 0) {
            lastUpdateDate64 = TableLists64.get(0).getLast_update();
            status64 = TableLists64.get(0).getStatus();
            if (isLoadService) //64 != null)
                if (status64.equalsIgnoreCase("Progress")) {
                    progress64.setProgress(1);
                    msg64.setText("InProgress...");
                    msg64.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status64.equalsIgnoreCase("Loading")) {
                    progress64.setProgress(1);
                    msg64.setText("Loading...");
                    msg64.setTextColor(getResources().getColor(R.color.gray));
                } else if (status64.equalsIgnoreCase("Updating")) {
                    progress64.setProgress(1);
                    msg64.setText("Database inserting...");
                    msg64.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn64.setText(lastUpdateDate64);
        //projIndentChild
        String lastUpdateDate65 = "";
        String status65 = "";
        List<UpdateOnTable> TableLists65 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projIndentChild")).list();
        if (TableLists65.size() > 0) {
            lastUpdateDate65 = TableLists65.get(0).getLast_update();
            status65 = TableLists65.get(0).getStatus();
            if (isLoadService) //65 != null)
                if (status65.equalsIgnoreCase("Progress")) {
                    progress65.setProgress(1);
                    msg65.setText("InProgress...");
                    msg65.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status65.equalsIgnoreCase("Loading")) {
                    progress65.setProgress(1);
                    msg65.setText("Loading...");
                    msg65.setTextColor(getResources().getColor(R.color.gray));
                } else if (status65.equalsIgnoreCase("Updating")) {
                    progress65.setProgress(1);
                    msg65.setText("Database inserting...");
                    msg65.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn65.setText(lastUpdateDate65);
        //projMinChild
        String lastUpdateDate66 = "";
        String status66 = "";
        List<UpdateOnTable> TableLists66 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMinChild")).list();
        if (TableLists66.size() > 0) {
            lastUpdateDate66 = TableLists66.get(0).getLast_update();
            status66 = TableLists66.get(0).getStatus();
            if (isLoadService) //66 != null)
                if (status66.equalsIgnoreCase("Progress")) {
                    progress66.setProgress(1);
                    msg66.setText("InProgress...");
                    msg66.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status66.equalsIgnoreCase("Loading")) {
                    progress66.setProgress(1);
                    msg66.setText("Loading...");
                    msg66.setTextColor(getResources().getColor(R.color.gray));
                } else if (status66.equalsIgnoreCase("Updating")) {
                    progress66.setProgress(1);
                    msg66.setText("Database inserting...");
                    msg66.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn66.setText(lastUpdateDate66);
        //projGrnMaster
        String lastUpdateDate67 = "";
        String status67 = "";
        List<UpdateOnTable> TableLists67 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projGrnMaster")).list();
        if (TableLists67.size() > 0) {
            lastUpdateDate67 = TableLists67.get(0).getLast_update();
            status67 = TableLists67.get(0).getStatus();
            if (isLoadService) //67 != null)
                if (status67.equalsIgnoreCase("Progress")) {
                    progress67.setProgress(1);
                    msg67.setText("InProgress...");
                    msg67.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status67.equalsIgnoreCase("Loading")) {
                    progress67.setProgress(1);
                    msg67.setText("Loading...");
                    msg67.setTextColor(getResources().getColor(R.color.gray));
                } else if (status67.equalsIgnoreCase("Updating")) {
                    progress67.setProgress(1);
                    msg67.setText("Database inserting...");
                    msg67.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn67.setText(lastUpdateDate67);
        //projGrnItemChild
        String lastUpdateDate68 = "";
        String status68 = "";
        List<UpdateOnTable> TableLists68 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projGrnItemChild")).list();
        if (TableLists68.size() > 0) {
            lastUpdateDate68 = TableLists68.get(0).getLast_update();
            status68 = TableLists68.get(0).getStatus();
            if (isLoadService) //68 != null)
                if (status68.equalsIgnoreCase("Progress")) {
                    progress68.setProgress(1);
                    msg68.setText("InProgress...");
                    msg68.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status68.equalsIgnoreCase("Loading")) {
                    progress68.setProgress(1);
                    msg68.setText("Loading...");
                    msg68.setTextColor(getResources().getColor(R.color.gray));
                } else if (status68.equalsIgnoreCase("Updating")) {
                    progress68.setProgress(1);
                    msg68.setText("Database inserting...");
                    msg68.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn68.setText(lastUpdateDate68);
        //weightData
        String lastUpdateDate69 = "";
        String status69 = "";
        List<UpdateOnTable> TableLists69 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("weightData")).list();
        if (TableLists69.size() > 0) {
            lastUpdateDate69 = TableLists69.get(0).getLast_update();
            status69 = TableLists69.get(0).getStatus();
            if (isLoadService) //69 != null)
                if (status69.equalsIgnoreCase("Progress")) {
                    progress69.setProgress(1);
                    msg69.setText("InProgress...");
                    msg69.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status69.equalsIgnoreCase("Loading")) {
                    progress69.setProgress(1);
                    msg69.setText("Loading...");
                    msg69.setTextColor(getResources().getColor(R.color.gray));
                } else if (status69.equalsIgnoreCase("Updating")) {
                    progress69.setProgress(1);
                    msg69.setText("Database inserting...");
                    msg69.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn69.setText(lastUpdateDate69);
        //projGrnOtherChargeChild
        String lastUpdateDate70 = "";
        String status70 = "";
        List<UpdateOnTable> TableLists70 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projGrnOtherChargeChild")).list();
        if (TableLists70.size() > 0) {
            lastUpdateDate70 = TableLists70.get(0).getLast_update();
            status70 = TableLists70.get(0).getStatus();
            if (isLoadService) //70 != null)
                if (status70.equalsIgnoreCase("Progress")) {
                    progress70.setProgress(1);
                    msg70.setText("InProgress...");
                    msg70.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status70.equalsIgnoreCase("Loading")) {
                    progress70.setProgress(1);
                    msg70.setText("Loading...");
                    msg70.setTextColor(getResources().getColor(R.color.gray));
                } else if (status70.equalsIgnoreCase("Updating")) {
                    progress70.setProgress(1);
                    msg70.setText("Database inserting...");
                    msg70.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn70.setText(lastUpdateDate70);
        //projBmrfMaster
        String lastUpdateDate71 = "";
        String status71 = "";
        List<UpdateOnTable> TableLists71 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projBmrfMaster")).list();
        if (TableLists71.size() > 0) {
            lastUpdateDate71 = TableLists71.get(0).getLast_update();
            status71 = TableLists71.get(0).getStatus();
            if (isLoadService) //71 != null)
                if (status71.equalsIgnoreCase("Progress")) {
                    progress71.setProgress(1);
                    msg71.setText("InProgress...");
                    msg71.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status71.equalsIgnoreCase("Loading")) {
                    progress71.setProgress(1);
                    msg71.setText("Loading...");
                    msg71.setTextColor(getResources().getColor(R.color.gray));
                } else if (status71.equalsIgnoreCase("Updating")) {
                    progress71.setProgress(1);
                    msg71.setText("Database inserting...");
                    msg71.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn71.setText(lastUpdateDate71);
        //projMatBmrfChild
        String lastUpdateDate72 = "";
        String status72 = "";
        List<UpdateOnTable> TableLists72 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMatBmrfChild")).list();
        if (TableLists72.size() > 0) {
            lastUpdateDate72 = TableLists72.get(0).getLast_update();
            status72 = TableLists72.get(0).getStatus();
            if (isLoadService) //72 != null)
                if (status72.equalsIgnoreCase("Progress")) {
                    progress72.setProgress(1);
                    msg72.setText("InProgress...");
                    msg72.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status72.equalsIgnoreCase("Loading")) {
                    progress72.setProgress(1);
                    msg72.setText("Loading...");
                    msg72.setTextColor(getResources().getColor(R.color.gray));
                } else if (status72.equalsIgnoreCase("Updating")) {
                    progress72.setProgress(1);
                    msg72.setText("Database inserting...");
                    msg72.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn72.setText(lastUpdateDate72);
        //projMrirMaster
        String lastUpdateDate73 = "";
        String status73 = "";
        List<UpdateOnTable> TableLists73 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrirMaster")).list();
        if (TableLists73.size() > 0) {
            lastUpdateDate73 = TableLists73.get(0).getLast_update();
            status73 = TableLists73.get(0).getStatus();
            if (isLoadService) //73 != null)
                if (status73.equalsIgnoreCase("Progress")) {
                    progress73.setProgress(1);
                    msg73.setText("InProgress...");
                    msg73.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status73.equalsIgnoreCase("Loading")) {
                    progress73.setProgress(1);
                    msg73.setText("Loading...");
                    msg73.setTextColor(getResources().getColor(R.color.gray));
                } else if (status73.equalsIgnoreCase("Updating")) {
                    progress73.setProgress(1);
                    msg73.setText("Database inserting...");
                    msg73.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn73.setText(lastUpdateDate73);
        //projMrirItemChild
        String lastUpdateDate74 = "";
        String status74 = "";
        List<UpdateOnTable> TableLists74 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrirItemChild")).list();
        if (TableLists74.size() > 0) {
            lastUpdateDate74 = TableLists74.get(0).getLast_update();
            status74 = TableLists74.get(0).getStatus();
            if (isLoadService) //74 != null)
                if (status74.equalsIgnoreCase("Progress")) {
                    progress74.setProgress(1);
                    msg74.setText("InProgress...");
                    msg74.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status74.equalsIgnoreCase("Loading")) {
                    progress74.setProgress(1);
                    msg74.setText("Loading...");
                    msg74.setTextColor(getResources().getColor(R.color.gray));
                } else if (status74.equalsIgnoreCase("Updating")) {
                    progress74.setProgress(1);
                    msg74.setText("Database inserting...");
                    msg74.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn74.setText(lastUpdateDate74);
        //projMrirOtherChargeChild
        String lastUpdateDate75 = "";
        String status75 = "";
        List<UpdateOnTable> TableLists75 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrirOtherChargeChild")).list();
        if (TableLists75.size() > 0) {
            lastUpdateDate75 = TableLists75.get(0).getLast_update();
            status75 = TableLists75.get(0).getStatus();
            if (isLoadService) //75 != null)
                if (status75.equalsIgnoreCase("Progress")) {
                    progress75.setProgress(1);
                    msg75.setText("InProgress...");
                    msg75.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status75.equalsIgnoreCase("Loading")) {
                    progress75.setProgress(1);
                    msg75.setText("Loading...");
                    msg75.setTextColor(getResources().getColor(R.color.gray));
                } else if (status75.equalsIgnoreCase("Updating")) {
                    progress75.setProgress(1);
                    msg75.setText("Database inserting...");
                    msg75.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn75.setText(lastUpdateDate75);
        //projMtdnMaster
        String lastUpdateDate76 = "";
        String status76 = "";
        List<UpdateOnTable> TableLists76 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtdnMaster")).list();
        if (TableLists76.size() > 0) {
            lastUpdateDate76 = TableLists76.get(0).getLast_update();
            status76 = TableLists76.get(0).getStatus();
            if (isLoadService) //76 != null)
                if (status76.equalsIgnoreCase("Progress")) {
                    progress76.setProgress(1);
                    msg76.setText("InProgress...");
                    msg76.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status76.equalsIgnoreCase("Loading")) {
                    progress76.setProgress(1);
                    msg76.setText("Loading...");
                    msg76.setTextColor(getResources().getColor(R.color.gray));
                } else if (status76.equalsIgnoreCase("Updating")) {
                    progress76.setProgress(1);
                    msg76.setText("Database inserting...");
                    msg76.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn76.setText(lastUpdateDate76);
        //projMtdnChild
        String lastUpdateDate77 = "";
        String status77 = "";
        List<UpdateOnTable> TableLists77 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtdnChild")).list();
        if (TableLists77.size() > 0) {
            lastUpdateDate77 = TableLists77.get(0).getLast_update();
            status77 = TableLists77.get(0).getStatus();
            if (isLoadService) //77 != null)
                if (status77.equalsIgnoreCase("Progress")) {
                    progress77.setProgress(1);
                    msg77.setText("InProgress...");
                    msg77.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status77.equalsIgnoreCase("Loading")) {
                    progress77.setProgress(1);
                    msg77.setText("Loading...");
                    msg77.setTextColor(getResources().getColor(R.color.gray));
                } else if (status77.equalsIgnoreCase("Updating")) {
                    progress77.setProgress(1);
                    msg77.setText("Database inserting...");
                    msg77.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn77.setText(lastUpdateDate77);
        //projMtdnOtherChargeChild
        String lastUpdateDate78 = "";
        String status78 = "";
        List<UpdateOnTable> TableLists78 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtdnOtherChargeChild")).list();
        if (TableLists78.size() > 0) {
            lastUpdateDate78 = TableLists78.get(0).getLast_update();
            status78 = TableLists78.get(0).getStatus();
            if (isLoadService) //78 != null)
                if (status78.equalsIgnoreCase("Progress")) {
                    progress78.setProgress(1);
                    msg78.setText("InProgress...");
                    msg78.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status78.equalsIgnoreCase("Loading")) {
                    progress78.setProgress(1);
                    msg78.setText("Loading...");
                    msg78.setTextColor(getResources().getColor(R.color.gray));
                } else if (status78.equalsIgnoreCase("Updating")) {
                    progress78.setProgress(1);
                    msg78.setText("Database inserting...");
                    msg78.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn78.setText(lastUpdateDate78);
        //projMtnMaster
        String lastUpdateDate79 = "";
        String status79 = "";
        List<UpdateOnTable> TableLists79 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtnMaster")).list();
        if (TableLists79.size() > 0) {
            lastUpdateDate79 = TableLists79.get(0).getLast_update();
            status79 = TableLists79.get(0).getStatus();
            if (isLoadService) //79 != null)
                if (status79.equalsIgnoreCase("Progress")) {
                    progress79.setProgress(1);
                    msg79.setText("InProgress...");
                    msg79.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status79.equalsIgnoreCase("Loading")) {
                    progress79.setProgress(1);
                    msg79.setText("Loading...");
                    msg79.setTextColor(getResources().getColor(R.color.gray));
                } else if (status79.equalsIgnoreCase("Updating")) {
                    progress79.setProgress(1);
                    msg79.setText("Database inserting...");
                    msg79.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn79.setText(lastUpdateDate79);
        //projMtnChild
        String lastUpdateDate80 = "";
        String status80 = "";
        List<UpdateOnTable> TableLists80 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtnChild")).list();
        if (TableLists80.size() > 0) {
            lastUpdateDate80 = TableLists80.get(0).getLast_update();
            status80 = TableLists80.get(0).getStatus();
            if (isLoadService) //80 != null)
                if (status80.equalsIgnoreCase("Progress")) {
                    progress80.setProgress(1);
                    msg80.setText("InProgress...");
                    msg80.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status80.equalsIgnoreCase("Loading")) {
                    progress80.setProgress(1);
                    msg80.setText("Loading...");
                    msg80.setTextColor(getResources().getColor(R.color.gray));
                } else if (status80.equalsIgnoreCase("Updating")) {
                    progress80.setProgress(1);
                    msg80.setText("Database inserting...");
                    msg80.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn80.setText(lastUpdateDate80);
        //projMtrnMaster
        String lastUpdateDate81 = "";
        String status81 = "";
        List<UpdateOnTable> TableLists81 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtrnMaster")).list();
        if (TableLists81.size() > 0) {
            lastUpdateDate81 = TableLists81.get(0).getLast_update();
            status81 = TableLists81.get(0).getStatus();
            if (isLoadService) //81 != null)
                if (status81.equalsIgnoreCase("Progress")) {
                    progress81.setProgress(1);
                    msg81.setText("InProgress...");
                    msg81.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status81.equalsIgnoreCase("Loading")) {
                    progress81.setProgress(1);
                    msg81.setText("Loading...");
                    msg81.setTextColor(getResources().getColor(R.color.gray));
                } else if (status81.equalsIgnoreCase("Updating")) {
                    progress81.setProgress(1);
                    msg81.setText("Database inserting...");
                    msg81.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn81.setText(lastUpdateDate81);
        //projMtnCloseMaster
        String lastUpdateDate82 = "";
        String status82 = "";
        List<UpdateOnTable> TableLists82 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtnCloseMaster")).list();
        if (TableLists82.size() > 0) {
            lastUpdateDate82 = TableLists82.get(0).getLast_update();
            status82 = TableLists82.get(0).getStatus();
            if (isLoadService) //82 != null)
                if (status82.equalsIgnoreCase("Progress")) {
                    progress82.setProgress(1);
                    msg82.setText("InProgress...");
                    msg82.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status82.equalsIgnoreCase("Loading")) {
                    progress82.setProgress(1);
                    msg82.setText("Loading...");
                    msg82.setTextColor(getResources().getColor(R.color.gray));
                } else if (status82.equalsIgnoreCase("Updating")) {
                    progress82.setProgress(1);
                    msg82.setText("Database inserting...");
                    msg82.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn82.setText(lastUpdateDate82);
        //projPoBillRecommendationMaster
        String lastUpdateDate83 = "";
        String status83 = "";
        List<UpdateOnTable> TableLists83 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationMaster")).list();
        if (TableLists83.size() > 0) {
            lastUpdateDate83 = TableLists83.get(0).getLast_update();
            status83 = TableLists83.get(0).getStatus();
            if (isLoadService) //83 != null)
                if (status83.equalsIgnoreCase("Progress")) {
                    progress83.setProgress(1);
                    msg83.setText("InProgress...");
                    msg83.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status83.equalsIgnoreCase("Loading")) {
                    progress83.setProgress(1);
                    msg83.setText("Loading...");
                    msg83.setTextColor(getResources().getColor(R.color.gray));
                } else if (status83.equalsIgnoreCase("Updating")) {
                    progress83.setProgress(1);
                    msg83.setText("Database inserting...");
                    msg83.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn83.setText(lastUpdateDate83);
        //projPoBillMaster
        String lastUpdateDate84 = "";
        String status84 = "";
        List<UpdateOnTable> TableLists84 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillMaster")).list();
        if (TableLists84.size() > 0) {
            lastUpdateDate84 = TableLists84.get(0).getLast_update();
            status84 = TableLists84.get(0).getStatus();
            if (isLoadService) //84 != null)
                if (status84.equalsIgnoreCase("Progress")) {
                    progress84.setProgress(1);
                    msg84.setText("InProgress...");
                    msg84.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status84.equalsIgnoreCase("Loading")) {
                    progress84.setProgress(1);
                    msg84.setText("Loading...");
                    msg84.setTextColor(getResources().getColor(R.color.gray));
                } else if (status84.equalsIgnoreCase("Updating")) {
                    progress84.setProgress(1);
                    msg84.setText("Database inserting...");
                    msg84.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn84.setText(lastUpdateDate84);
        //projPoBillRecommendationChild
        String lastUpdateDate85 = "";
        String status85 = "";
        List<UpdateOnTable> TableLists85 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationChild")).list();
        if (TableLists85.size() > 0) {
            lastUpdateDate85 = TableLists85.get(0).getLast_update();
            status85 = TableLists85.get(0).getStatus();
            if (isLoadService) //85 != null)
                if (status85.equalsIgnoreCase("Progress")) {
                    progress85.setProgress(1);
                    msg85.setText("InProgress...");
                    msg85.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status85.equalsIgnoreCase("Loading")) {
                    progress85.setProgress(1);
                    msg85.setText("Loading...");
                    msg85.setTextColor(getResources().getColor(R.color.gray));
                } else if (status85.equalsIgnoreCase("Updating")) {
                    progress85.setProgress(1);
                    msg85.setText("Database inserting...");
                    msg85.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn85.setText(lastUpdateDate85);
        //projPoBillRecommendationPaymentChild
        String lastUpdateDate86 = "";
        String status86 = "";
        List<UpdateOnTable> TableLists86 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationPaymentChild")).list();
        if (TableLists86.size() > 0) {
            lastUpdateDate86 = TableLists86.get(0).getLast_update();
            status86 = TableLists86.get(0).getStatus();
            if (isLoadService) //86 != null)
                if (status86.equalsIgnoreCase("Progress")) {
                    progress86.setProgress(1);
                    msg86.setText("InProgress...");
                    msg86.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status86.equalsIgnoreCase("Loading")) {
                    progress86.setProgress(1);
                    msg86.setText("Loading...");
                    msg86.setTextColor(getResources().getColor(R.color.gray));
                } else if (status86.equalsIgnoreCase("Updating")) {
                    progress86.setProgress(1);
                    msg86.setText("Database inserting...");
                    msg86.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn86.setText(lastUpdateDate86);
        //projPoBillRecommendationPaymentMaster
        String lastUpdateDate87 = "";
        String status87 = "";
        List<UpdateOnTable> TableLists87 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationPaymentMaster")).list();
        if (TableLists87.size() > 0) {
            lastUpdateDate87 = TableLists87.get(0).getLast_update();
            status87 = TableLists87.get(0).getStatus();
            if (isLoadService) //87 != null)
                if (status87.equalsIgnoreCase("Progress")) {
                    progress87.setProgress(1);
                    msg87.setText("InProgress...");
                    msg87.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status87.equalsIgnoreCase("Loading")) {
                    progress87.setProgress(1);
                    msg87.setText("Loading...");
                    msg87.setTextColor(getResources().getColor(R.color.gray));
                } else if (status87.equalsIgnoreCase("Updating")) {
                    progress87.setProgress(1);
                    msg87.setText("Database inserting...");
                    msg87.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn87.setText(lastUpdateDate87);
        //projPoBillRecommendationPoChild
        String lastUpdateDate88 = "";
        String status88 = "";
        List<UpdateOnTable> TableLists88 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationPoChild")).list();
        if (TableLists88.size() > 0) {
            lastUpdateDate88 = TableLists88.get(0).getLast_update();
            status88 = TableLists88.get(0).getStatus();
            if (isLoadService) //88 != null)
                if (status88.equalsIgnoreCase("Progress")) {
                    progress88.setProgress(1);
                    msg88.setText("InProgress...");
                    msg88.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status88.equalsIgnoreCase("Loading")) {
                    progress88.setProgress(1);
                    msg88.setText("Loading...");
                    msg88.setTextColor(getResources().getColor(R.color.gray));
                } else if (status88.equalsIgnoreCase("Updating")) {
                    progress88.setProgress(1);
                    msg88.setText("Database inserting...");
                    msg88.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn88.setText(lastUpdateDate88);
        //projPoBillItemChild
        String lastUpdateDate89 = "";
        String status89 = "";
        List<UpdateOnTable> TableLists89 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillItemChild")).list();
        if (TableLists89.size() > 0) {
            lastUpdateDate89 = TableLists89.get(0).getLast_update();
            status89 = TableLists89.get(0).getStatus();
            if (isLoadService) //89 != null)
                if (status89.equalsIgnoreCase("Progress")) {
                    progress89.setProgress(1);
                    msg89.setText("InProgress...");
                    msg89.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status89.equalsIgnoreCase("Loading")) {
                    progress89.setProgress(1);
                    msg89.setText("Loading...");
                    msg89.setTextColor(getResources().getColor(R.color.gray));
                } else if (status89.equalsIgnoreCase("Updating")) {
                    progress89.setProgress(1);
                    msg89.setText("Database inserting...");
                    msg89.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn89.setText(lastUpdateDate89);
        //projPoBillRecommendationPaymentDetChild
        String lastUpdateDate90 = "";
        String status90 = "";
        List<UpdateOnTable> TableLists90 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationPaymentDetChild")).list();
        if (TableLists90.size() > 0) {
            lastUpdateDate90 = TableLists90.get(0).getLast_update();
            status90 = TableLists90.get(0).getStatus();
            if (isLoadService) //90 != null)
                if (status90.equalsIgnoreCase("Progress")) {
                    progress90.setProgress(1);
                    msg90.setText("InProgress...");
                    msg90.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status90.equalsIgnoreCase("Loading")) {
                    progress90.setProgress(1);
                    msg90.setText("Loading...");
                    msg90.setTextColor(getResources().getColor(R.color.gray));
                } else if (status90.equalsIgnoreCase("Updating")) {
                    progress90.setProgress(1);
                    msg90.setText("Database inserting...");
                    msg90.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn90.setText(lastUpdateDate90);
        //projPoActBillOthersChild
        String lastUpdateDate91 = "";
        String status91 = "";
        List<UpdateOnTable> TableLists91 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoActBillOthersChild")).list();
        if (TableLists91.size() > 0) {
            lastUpdateDate91 = TableLists91.get(0).getLast_update();
            status91 = TableLists91.get(0).getStatus();
            if (isLoadService) //91 != null)
                if (status91.equalsIgnoreCase("Progress")) {
                    progress91.setProgress(1);
                    msg91.setText("InProgress...");
                    msg91.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status91.equalsIgnoreCase("Loading")) {
                    progress91.setProgress(1);
                    msg91.setText("Loading...");
                    msg91.setTextColor(getResources().getColor(R.color.gray));
                } else if (status91.equalsIgnoreCase("Updating")) {
                    progress91.setProgress(1);
                    msg91.setText("Database inserting...");
                    msg91.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn91.setText(lastUpdateDate91);
        //projPoBillOtherChargeChild
        String lastUpdateDate92 = "";
        String status92 = "";
        List<UpdateOnTable> TableLists92 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillOtherChargeChild")).list();
        if (TableLists92.size() > 0) {
            lastUpdateDate92 = TableLists92.get(0).getLast_update();
            status92 = TableLists92.get(0).getStatus();
            if (isLoadService) //92 != null)
                if (status92.equalsIgnoreCase("Progress")) {
                    progress92.setProgress(1);
                    msg92.setText("InProgress...");
                    msg92.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status92.equalsIgnoreCase("Loading")) {
                    progress92.setProgress(1);
                    msg92.setText("Loading...");
                    msg92.setTextColor(getResources().getColor(R.color.gray));
                } else if (status92.equalsIgnoreCase("Updating")) {
                    progress92.setProgress(1);
                    msg92.setText("Database inserting...");
                    msg92.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn92.setText(lastUpdateDate92);
        //projMtanMaster
        String lastUpdateDate93 = "";
        String status93 = "";
        List<UpdateOnTable> TableLists93 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtanMaster")).list();
        if (TableLists93.size() > 0) {
            lastUpdateDate93 = TableLists93.get(0).getLast_update();
            status93 = TableLists93.get(0).getStatus();
            if (isLoadService) //93 != null)
                if (status93.equalsIgnoreCase("Progress")) {
                    progress93.setProgress(1);
                    msg93.setText("InProgress...");
                    msg93.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status93.equalsIgnoreCase("Loading")) {
                    progress93.setProgress(1);
                    msg93.setText("Loading...");
                    msg93.setTextColor(getResources().getColor(R.color.gray));
                } else if (status93.equalsIgnoreCase("Updating")) {
                    progress93.setProgress(1);
                    msg93.setText("Database inserting...");
                    msg93.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn93.setText(lastUpdateDate93);
        //projMtanChild
        String lastUpdateDate94 = "";
        String status94 = "";
        List<UpdateOnTable> TableLists94 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtanChild")).list();
        if (TableLists94.size() > 0) {
            lastUpdateDate94 = TableLists94.get(0).getLast_update();
            status94 = TableLists94.get(0).getStatus();
            if (isLoadService) //94 != null)
                if (status94.equalsIgnoreCase("Progress")) {
                    progress94.setProgress(1);
                    msg94.setText("InProgress...");
                    msg94.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status94.equalsIgnoreCase("Loading")) {
                    progress94.setProgress(1);
                    msg94.setText("Loading...");
                    msg94.setTextColor(getResources().getColor(R.color.gray));
                } else if (status94.equalsIgnoreCase("Updating")) {
                    progress94.setProgress(1);
                    msg94.setText("Database inserting...");
                    msg94.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn94.setText(lastUpdateDate94);
        //projMtanOtherChargeChild
        String lastUpdateDate95 = "";
        String status95 = "";
        List<UpdateOnTable> TableLists95 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtanOtherChargeChild")).list();
        if (TableLists95.size() > 0) {
            lastUpdateDate95 = TableLists95.get(0).getLast_update();
            status95 = TableLists95.get(0).getStatus();
            if (isLoadService) //95 != null)
                if (status95.equalsIgnoreCase("Progress")) {
                    progress95.setProgress(1);
                    msg95.setText("InProgress...");
                    msg95.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status95.equalsIgnoreCase("Loading")) {
                    progress95.setProgress(1);
                    msg95.setText("Loading...");
                    msg95.setTextColor(getResources().getColor(R.color.gray));
                } else if (status95.equalsIgnoreCase("Updating")) {
                    progress95.setProgress(1);
                    msg95.setText("Database inserting...");
                    msg95.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn95.setText(lastUpdateDate95);
        //projWoBillIowChild
        String lastUpdateDate96 = "";
        String status96 = "";
        List<UpdateOnTable> TableLists96 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillIowChild")).list();
        if (TableLists96.size() > 0) {
            lastUpdateDate96 = TableLists96.get(0).getLast_update();
            status96 = TableLists96.get(0).getStatus();
            if (isLoadService) //96 != null)
                if (status96.equalsIgnoreCase("Progress")) {
                    progress96.setProgress(1);
                    msg96.setText("InProgress...");
                    msg96.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status96.equalsIgnoreCase("Loading")) {
                    progress96.setProgress(1);
                    msg96.setText("Loading...");
                    msg96.setTextColor(getResources().getColor(R.color.gray));
                } else if (status96.equalsIgnoreCase("Updating")) {
                    progress96.setProgress(1);
                    msg96.setText("Database inserting...");
                    msg96.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn96.setText(lastUpdateDate96);
        //projWoBillSubActivityChild
        String lastUpdateDate97 = "";
        String status97 = "";
        List<UpdateOnTable> TableLists97 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillSubActivityChild")).list();
        if (TableLists97.size() > 0) {
            lastUpdateDate97 = TableLists97.get(0).getLast_update();
            status97 = TableLists97.get(0).getStatus();
            if (isLoadService) //97 != null)
                if (status97.equalsIgnoreCase("Progress")) {
                    progress97.setProgress(1);
                    msg97.setText("InProgress...");
                    msg97.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status97.equalsIgnoreCase("Loading")) {
                    progress97.setProgress(1);
                    msg97.setText("Loading...");
                    msg97.setTextColor(getResources().getColor(R.color.gray));
                } else if (status97.equalsIgnoreCase("Updating")) {
                    progress97.setProgress(1);
                    msg97.setText("Database inserting...");
                    msg97.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn97.setText(lastUpdateDate97);
        //projWoBillNmrChild
        String lastUpdateDate98 = "";
        String status98 = "";
        List<UpdateOnTable> TableLists98 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillNmrChild")).list();
        if (TableLists98.size() > 0) {
            lastUpdateDate98 = TableLists98.get(0).getLast_update();
            status98 = TableLists98.get(0).getStatus();
            if (isLoadService) //98 != null)
                if (status98.equalsIgnoreCase("Progress")) {
                    progress98.setProgress(1);
                    msg98.setText("InProgress...");
                    msg98.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status98.equalsIgnoreCase("Loading")) {
                    progress98.setProgress(1);
                    msg98.setText("Loading...");
                    msg98.setTextColor(getResources().getColor(R.color.gray));
                } else if (status98.equalsIgnoreCase("Updating")) {
                    progress98.setProgress(1);
                    msg98.setText("Database inserting...");
                    msg98.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn98.setText(lastUpdateDate98);
        //projMbookQaMaster
        String lastUpdateDate99 = "";
        String status99 = "";
        List<UpdateOnTable> TableLists99 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookQaMaster")).list();
        if (TableLists99.size() > 0) {
            lastUpdateDate99 = TableLists99.get(0).getLast_update();
            status99 = TableLists99.get(0).getStatus();
            if (isLoadService) //99 != null)
                if (status99.equalsIgnoreCase("Progress")) {
                    progress99.setProgress(1);
                    msg99.setText("InProgress...");
                    msg99.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status99.equalsIgnoreCase("Loading")) {
                    progress99.setProgress(1);
                    msg99.setText("Loading...");
                    msg99.setTextColor(getResources().getColor(R.color.gray));
                } else if (status99.equalsIgnoreCase("Updating")) {
                    progress99.setProgress(1);
                    msg99.setText("Database inserting...");
                    msg99.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn99.setText(lastUpdateDate99);
        //projWoBillIowDetChild
        String lastUpdateDate100 = "";
        String status100 = "";
        List<UpdateOnTable> TableLists100 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillIowDetChild")).list();
        if (TableLists100.size() > 0) {
            lastUpdateDate100 = TableLists100.get(0).getLast_update();
            status100 = TableLists100.get(0).getStatus();
            if (isLoadService) //100 != null)
                if (status100.equalsIgnoreCase("Progress")) {
                    progress100.setProgress(1);
                    msg100.setText("InProgress...");
                    msg100.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status100.equalsIgnoreCase("Loading")) {
                    progress100.setProgress(1);
                    msg100.setText("Loading...");
                    msg100.setTextColor(getResources().getColor(R.color.gray));
                } else if (status100.equalsIgnoreCase("Updating")) {
                    progress100.setProgress(1);
                    msg100.setText("Database inserting...");
                    msg100.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn100.setText(lastUpdateDate100);
        //projWoBillIowTaxChild
        String lastUpdateDate101 = "";
        String status101 = "";
        List<UpdateOnTable> TableLists101 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillIowTaxChild")).list();
        if (TableLists101.size() > 0) {
            lastUpdateDate101 = TableLists101.get(0).getLast_update();
            status101 = TableLists101.get(0).getStatus();
            if (isLoadService) //101 != null)
                if (status101.equalsIgnoreCase("Progress")) {
                    progress101.setProgress(1);
                    msg101.setText("InProgress...");
                    msg101.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status101.equalsIgnoreCase("Loading")) {
                    progress101.setProgress(1);
                    msg101.setText("Loading...");
                    msg101.setTextColor(getResources().getColor(R.color.gray));
                } else if (status101.equalsIgnoreCase("Updating")) {
                    progress101.setProgress(1);
                    msg101.setText("Database inserting...");
                    msg101.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn101.setText(lastUpdateDate101);
        //projWoBillSubActivityDetChild
        String lastUpdateDate102 = "";
        String status102 = "";
        List<UpdateOnTable> TableLists102 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillSubActivityDetChild")).list();
        if (TableLists102.size() > 0) {
            lastUpdateDate102 = TableLists102.get(0).getLast_update();
            status102 = TableLists102.get(0).getStatus();
            if (isLoadService) //102 != null)
                if (status102.equalsIgnoreCase("Progress")) {
                    progress102.setProgress(1);
                    msg102.setText("InProgress...");
                    msg102.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status102.equalsIgnoreCase("Loading")) {
                    progress102.setProgress(1);
                    msg102.setText("Loading...");
                    msg102.setTextColor(getResources().getColor(R.color.gray));
                } else if (status102.equalsIgnoreCase("Updating")) {
                    progress102.setProgress(1);
                    msg102.setText("Database inserting...");
                    msg102.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn102.setText(lastUpdateDate102);
        //projWoBillNmrDetChild
        String lastUpdateDate103 = "";
        String status103 = "";
        List<UpdateOnTable> TableLists103 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillNmrDetChild")).list();
        if (TableLists103.size() > 0) {
            lastUpdateDate103 = TableLists103.get(0).getLast_update();
            status103 = TableLists103.get(0).getStatus();
            if (isLoadService) //103 != null)
                if (status103.equalsIgnoreCase("Progress")) {
                    progress103.setProgress(1);
                    msg103.setText("InProgress...");
                    msg103.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status103.equalsIgnoreCase("Loading")) {
                    progress103.setProgress(1);
                    msg103.setText("Loading...");
                    msg103.setTextColor(getResources().getColor(R.color.gray));
                } else if (status103.equalsIgnoreCase("Updating")) {
                    progress103.setProgress(1);
                    msg103.setText("Database inserting...");
                    msg103.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn103.setText(lastUpdateDate103);

        //finAccountMaster
        String lastUpdateDate104 = "";
        String status104 = "";
        List<UpdateOnTable> TableLists104 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("finAccountMaster")).list();
        if (TableLists104.size() > 0) {
            lastUpdateDate104 = TableLists104.get(0).getLast_update();
            status104 = TableLists104.get(0).getStatus();
            if (isLoadService) //104 != null)
                if (status104.equalsIgnoreCase("Progress")) {
                    progress104.setProgress(1);
                    msg104.setText("InProgress...");
                    msg104.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status104.equalsIgnoreCase("Loading")) {
                    progress104.setProgress(1);
                    msg104.setText("Loading...");
                    msg104.setTextColor(getResources().getColor(R.color.gray));
                } else if (status104.equalsIgnoreCase("Updating")) {
                    progress104.setProgress(1);
                    msg104.setText("Database inserting...");
                    msg104.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn104.setText(lastUpdateDate104);

        //projWoBillRecommendationPaymentMaster
        String lastUpdateDate105 = "";
        String status105 = "";
        List<UpdateOnTable> TableLists105 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillRecommendationPaymentMaster")).list();
        if (TableLists105.size() > 0) {
            lastUpdateDate105 = TableLists105.get(0).getLast_update();
            status105 = TableLists105.get(0).getStatus();
            if (isLoadService) //105 != null)
                if (status105.equalsIgnoreCase("Progress")) {
                    progress105.setProgress(1);
                    msg105.setText("InProgress...");
                    msg105.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status105.equalsIgnoreCase("Loading")) {
                    progress105.setProgress(1);
                    msg105.setText("Loading...");
                    msg105.setTextColor(getResources().getColor(R.color.gray));
                } else if (status105.equalsIgnoreCase("Updating")) {
                    progress105.setProgress(1);
                    msg105.setText("Database inserting...");
                    msg105.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn105.setText(lastUpdateDate105);

        //projWoBillRecommendationPaymentChild
        String lastUpdateDate106 = "";
        String status106 = "";
        List<UpdateOnTable> TableLists106 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillRecommendationPaymentChild")).list();
        if (TableLists106.size() > 0) {
            lastUpdateDate106 = TableLists106.get(0).getLast_update();
            status106 = TableLists106.get(0).getStatus();
            if (isLoadService) //106 != null)
                if (status106.equalsIgnoreCase("Progress")) {
                    progress106.setProgress(1);
                    msg106.setText("InProgress...");
                    msg106.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status106.equalsIgnoreCase("Loading")) {
                    progress106.setProgress(1);
                    msg106.setText("Loading...");
                    msg106.setTextColor(getResources().getColor(R.color.gray));
                } else if (status106.equalsIgnoreCase("Updating")) {
                    progress106.setProgress(1);
                    msg106.setText("Database inserting...");
                    msg106.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn106.setText(lastUpdateDate106);

        //projMirnMaster
        String lastUpdateDate107 = "";
        String status107 = "";
        List<UpdateOnTable> TableLists107 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMirnMaster")).list();
        if (TableLists107.size() > 0) {
            lastUpdateDate107 = TableLists107.get(0).getLast_update();
            status107 = TableLists107.get(0).getStatus();
            if (isLoadService) //107 != null)
                if (status107.equalsIgnoreCase("Progress")) {
                    progress107.setProgress(1);
                    msg107.setText("InProgress...");
                    msg107.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status107.equalsIgnoreCase("Loading")) {
                    progress107.setProgress(1);
                    msg107.setText("Loading...");
                    msg107.setTextColor(getResources().getColor(R.color.gray));
                } else if (status107.equalsIgnoreCase("Updating")) {
                    progress107.setProgress(1);
                    msg107.setText("Database inserting...");
                    msg107.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn107.setText(lastUpdateDate107);

        //projMirnChild
        String lastUpdateDate108 = "";
        String status108 = "";
        List<UpdateOnTable> TableLists108 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMirnChild")).list();
        if (TableLists108.size() > 0) {
            lastUpdateDate108 = TableLists108.get(0).getLast_update();
            status108 = TableLists108.get(0).getStatus();
            if (isLoadService) //108 != null)
                if (status108.equalsIgnoreCase("Progress")) {
                    progress108.setProgress(1);
                    msg108.setText("InProgress...");
                    msg108.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status108.equalsIgnoreCase("Loading")) {
                    progress108.setProgress(1);
                    msg108.setText("Loading...");
                    msg108.setTextColor(getResources().getColor(R.color.gray));
                } else if (status108.equalsIgnoreCase("Updating")) {
                    progress108.setProgress(1);
                    msg108.setText("Database inserting...");
                    msg108.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn108.setText(lastUpdateDate108);

        //projMbookLabourChild
        String lastUpdateDate109 = "";
        String status109 = "";
        List<UpdateOnTable> TableLists109 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookLabourChild")).list();
        if (TableLists109.size() > 0) {
            lastUpdateDate109 = TableLists109.get(0).getLast_update();
            status109 = TableLists109.get(0).getStatus();
            if (isLoadService) //109 != null)
                if (status109.equalsIgnoreCase("Progress")) {
                    progress109.setProgress(1);
                    msg109.setText("InProgress...");
                    msg109.setTextColor(getResources().getColor(R.color.blue_bg));
                } else if (status109.equalsIgnoreCase("Loading")) {
                    progress109.setProgress(1);
                    msg109.setText("Loading...");
                    msg109.setTextColor(getResources().getColor(R.color.gray));
                } else if (status109.equalsIgnoreCase("Updating")) {
                    progress109.setProgress(1);
                    msg109.setText("Database inserting...");
                    msg109.setTextColor(getResources().getColor(R.color.black));
                }
        }
        updateOn109.setText(lastUpdateDate109);
    }
    public Boolean isLoading() {
        boolean isLoad = false;
        if (!((flag1 == null) && (flag2 == null) && (flag3 == null) && (flag4 == null) && (flag5 == null) && (flag6 == null) && (flag7 == null) && (flag8 == null) && (flag9 == null) && (flag10 == null) && (flag11 == null) && (flag12 == null) && (flag13 == null) && (flag14 == null) && (flag15 == null) && (flag16 == null) && (flag17 == null) && (flag18 == null) && (flag19 == null) && (flag20 == null) && (flag21 == null) && (flag22 == null) && (flag23 == null) && (flag24 == null) && (flag25 == null) && (flag26 == null) && (flag27 == null) && (flag28 == null) && (flag29 == null && flag30 == null && flag31 == null && flag32 == null && flag33 == null && flag34 == null && flag35 == null && flag36 == null && flag37 == null && flag38 == null && flag39 == null && flag40 == null && flag41 == null && flag42 == null && flag43 == null && flag44 == null && flag45 == null && flag46 == null && flag47 == null && flag48 == null && flag49 == null && flag50 == null && flag51 == null && flag52 == null && flag55 == null && flag56 == null && flag57 == null && flag58 == null && flag59 == null && flag60 == null && flag61 == null && flag62 == null && flag63 == null && flag64 == null && flag65 == null && flag66 == null && flag67 == null && flag68 == null && flag69 == null && flag70 == null && flag71 == null && flag72 == null && flag73 == null && flag74 == null && flag75 == null && flag76 == null && flag77 == null && flag78 == null && flag79 == null && flag80 == null && flag81 == null && flag82 == null && flag83 == null && flag84 == null && flag85 == null && flag86 == null && flag87 == null && flag88 == null && flag89 == null && flag90 == null && flag91 == null && flag92 == null && flag93 == null && flag94 == null && flag95 == null&& flag96 == null&& flag97 == null&& flag98 == null&& flag99 == null&& flag100 == null&& flag101 == null&& flag102 == null&& flag103 == null&& flag104 == null&& flag105 == null&& flag106 == null&& flag107 == null&& flag108 == null&& flag109 == null)))
            isLoad = true;
        return isLoad;
    }
    private void getFromSession() {
        session = new SessionManager(getApplicationContext());
        uid = session.getUserDetails().get(SessionManager.ID);
        cr_id = session.getUserDetails().get(SessionManager.CR_ID);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.syn_all:
                // call method within the service
                syncAllClick();
                break;
            case R.id.progress1:
                // call method within the service
                progressClick1();
                break;
            case R.id.progress2:
                // call method within the service
                progressClick2();
                break;
            case R.id.progress3:
                // call method within the service
                progressClick3();
                break;
            case R.id.progress4:
                // call method within the service
                progressClick4();
                break;
            case R.id.progress5:
                // call method within the service
                progressClick5();
                break;
            case R.id.progress6:
                // call method within the service
                progressClick6();
                break;
            case R.id.progress7:
                // call method within the service
                progressClick7();
                break;
            case R.id.progress8:
                // call method within the service
                progressClick8();
                break;
            case R.id.progress9:
                // call method within the service
                progressClick9();
                break;
            case R.id.progress10:
                // call method within the service
                progressClick10();
                break;
            case R.id.progress11:
                // call method within the service
                progressClick11();
                break;
            case R.id.progress12:
                progressClick12();
                break;
            case R.id.progress13:
                progressClick13();
                break;
            case R.id.progress14:
                progressClick14();
                break;
            case R.id.progress15:
                progressClick15();
                break;
            case R.id.progress16:
                progressClick16();
                break;
            case R.id.progress17:
                progressClick17();
                break;
            case R.id.progress18:
                progressClick18();
                break;
            case R.id.progress19:
                progressClick19();
                break;
            case R.id.progress20:
                progressClick20();
                break;
            case R.id.progress21:
                progressClick21();
                break;
            case R.id.progress22:
                progressClick22();
                break;
            case R.id.progress23:
                progressClick23();
                break;
            case R.id.progress24:
                progressClick24();
                break;
            case R.id.progress25:
                progressClick25();
                break;
            case R.id.progress26:
                progressClick26();
                break;
            case R.id.progress27:
                progressClick27();
                break;
            case R.id.progress28:
                progressClick28();
                break;
            case R.id.progress29:
                progressClick29();
                break;
            case R.id.progress30:
                progressClick30();
                break;
            case R.id.progress31:
                progressClick31();
                break;
            case R.id.progress32:
                progressClick32();
                break;
            case R.id.progress33:
                progressClick33();
                break;
            case R.id.progress34:
                progressClick34();
                break;
            case R.id.progress35:
                progressClick35();
                break;
            case R.id.progress36:
                progressClick36();
                break;
            case R.id.progress37:
                progressClick37();
                break;
            case R.id.progress38:
                progressClick38();
                break;
            case R.id.progress39:
                progressClick39();
                break;
            case R.id.progress40:
                progressClick40();
                break;
            case R.id.progress41:
                progressClick41();
                break;
            case R.id.progress42:
                progressClick42();
                break;
            case R.id.progress43:
                progressClick43();
                break;
            case R.id.progress44:
                progressClick44();
                break;
            case R.id.progress45:
                progressClick45();
                break;
            case R.id.progress46:
                progressClick46();
                break;
            case R.id.progress47:
                progressClick47();
                break;
            case R.id.progress48:
                progressClick48();
                break;
            case R.id.progress49:
                progressClick49();
                break;
            case R.id.progress50:
                progressClick50();
                break;
            case R.id.progress51:
                progressClick51();
                break;
            case R.id.progress52:
                progressClick52();
                break;
           /* case R.id.progress53:
                progressClick53();
                break;
            case R.id.progress54:
                progressClick54();
                break;*/
            case R.id.progress55:
                progressClick55();
                break;
            case R.id.progress56:
                progressClick56();
                break;
            case R.id.progress57:
                progressClick57();
                break;
            case R.id.progress58:
                progressClick58();
                break;
            case R.id.progress59:
                progressClick59();
                break;
            case R.id.progress60:
                progressClick60();
                break;
            case R.id.progress61:
                progressClick61();
                break;
            case R.id.progress62:
                progressClick62();
                break;
            case R.id.progress63:
                progressClick63();
                break;
            case R.id.progress64:
                progressClick64();
                break;
            case R.id.progress65:
                progressClick65();
                break;
            case R.id.progress66:
                progressClick66();
                break;
            case R.id.progress67:
                progressClick67();
                break;
            case R.id.progress68:
                progressClick68();
                break;
            case R.id.progress69:
                progressClick69();
                break;
            case R.id.progress70:
                progressClick70();
                break;
            case R.id.progress71:
                progressClick71();
                break;
            case R.id.progress72:
                progressClick72();
                break;
            case R.id.progress73:
                progressClick73();
                break;
            case R.id.progress74:
                progressClick74();
                break;
            case R.id.progress75:
                progressClick75();
                break;
            case R.id.progress76:
                progressClick76();
                break;
            case R.id.progress77:
                progressClick77();
                break;
            case R.id.progress78:
                progressClick78();
                break;
            case R.id.progress79:
                progressClick79();
                break;
            case R.id.progress80:
                progressClick80();
                break;
            case R.id.progress81:
                progressClick81();
                break;
            case R.id.progress82:
                progressClick82();
                break;
            case R.id.progress83:
                progressClick83();
                break;
            case R.id.progress84:
                progressClick84();
                break;
            case R.id.progress85:
                progressClick85();
                break;
            case R.id.progress86:
                progressClick86();
                break;
            case R.id.progress87:
                progressClick87();
                break;
            case R.id.progress88:
                progressClick88();
                break;
            case R.id.progress89:
                progressClick89();
                break;
            case R.id.progress90:
                progressClick90();
                break;
            case R.id.progress91:
                progressClick91();
                break;
            case R.id.progress92:
                progressClick92();
                break;
            case R.id.progress93:
                progressClick93();
                break;
            case R.id.progress94:
                progressClick94();
                break;
            case R.id.progress95:
                progressClick95();
                break;
            case R.id.progress96:
                progressClick96();
                break;
            case R.id.progress97:
                progressClick97();
                break;
            case R.id.progress98:
                progressClick98();
                break;
            case R.id.progress99:
                progressClick99();
                break;
            case R.id.progress100:
                progressClick100();
                break;
            case R.id.progress101:
                progressClick101();
                break;
            case R.id.progress102:
                progressClick102();
                break;
            case R.id.progress103:
                progressClick103();
                break;
            case R.id.progress104:
                progressClick104();
                break;
            case R.id.progress105:
                progressClick105();
                break;
            case R.id.progress106:
                progressClick106();
                break;
            case R.id.progress107:
                progressClick107();
                break;
            case R.id.progress108:
                progressClick108();
                break;
            case R.id.progress109:
                progressClick109();
                break;
        }
    }
    public void progressClick1() {
        if (progress1.getVisibility() == View.VISIBLE)
            if (progress1.getProgress() == -1) {
                msg1.setText("");
                progress1.setProgress(0);
            } else if (progress1.getProgress() == 0) {
                msg1.setText("Loading...");
                msg1.setTextColor(getResources().getColor(R.color.gray));
                progress1.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("ProjectListWithStoreLoad")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'ProjectListWithStoreLoad'}";
                Log.d(TAG, "ProjectListWithStoreLoad--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn1, progress1, msg1, "ProjectListWithStoreLoad");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.bindString(1, uid + "_ProjectListWithStoreLoad");
                insertStatement.bindString(2, "ProjectListWithStoreLoad");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick2() {
        if (progress2.getVisibility() == View.VISIBLE)
            if (progress2.getProgress() == -1) {
                msg2.setText("");
                progress2.setProgress(0);
            } else if (progress2.getProgress() == 0) {
                msg2.setText("Loading...");
                msg2.setTextColor(getResources().getColor(R.color.gray));
                progress2.setProgress(1);
                String lastUpdateDate2 = "";
                List<UpdateOnTable> TableLists2 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjectMaterialBytLoad")).list();
                if (TableLists2.size() > 0) {
                    lastUpdateDate2 = TableLists2.get(0).getLast_update();
                }
                String req2 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate2 + "','dataTable':'ProjectMaterialBytLoad'}";
                Log.d(TAG, "ProjectMaterialBytLoad--> " + ApiCalls.getURLfromJson(req2, context));
                myService.doServiceStuff(req2, updateOn2, progress2, msg2, "ProjectMaterialBytLoad");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, "ProjectMaterialBytLoad");
                insertStatement.bindString(2, "ProjectMaterialBytLoad");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate2);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick3() {
        if (progress3.getVisibility() == View.VISIBLE)
            if (progress3.getProgress() == -1) {
                msg3.setText("");
                progress3.setProgress(0);
            } else if (progress3.getProgress() == 0) {
                msg3.setText("Loading...");
                msg3.setTextColor(getResources().getColor(R.color.gray));
                progress3.setProgress(1);
                String lastUpdateDate3 = "";
                List<UpdateOnTable> TableLists3 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjectContractorLoad")).list();
                if (TableLists3.size() > 0) {
                    lastUpdateDate3 = TableLists3.get(0).getLast_update();
                }
                String req3 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate3 + "','dataTable':'ProjectContractorLoad'}";
                Log.d(TAG, "ProjectContractorLoad--> " + ApiCalls.getURLfromJson(req3, context));
                myService.doServiceStuff(req3, updateOn3, progress3, msg3, "ProjectContractorLoad");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, "ProjectContractorLoad");
                insertStatement.bindString(2, "ProjectContractorLoad");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate3);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick4() {
        if (progress4.getVisibility() == View.VISIBLE)
            if (progress4.getProgress() == -1) {
                msg4.setText("");
                progress4.setProgress(0);
            } else if (progress4.getProgress() == 0) {
                msg4.setText("Loading...");
                msg4.setTextColor(getResources().getColor(R.color.gray));
                progress4.setProgress(1);
                String lastUpdateDate4 = "";
                List<UpdateOnTable> TableLists4 = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("ProjectRequestedBytLoad")).list();
                if (TableLists4.size() > 0) {
                    lastUpdateDate4 = TableLists4.get(0).getLast_update();
                }
                String req4 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate4 + "','dataTable':'ProjectRequestedBytLoad'}";
                Log.d(TAG, "ProjectRequestedBytLoad--> " + ApiCalls.getURLfromJson(req4, context));
                myService.doServiceStuff(req4, updateOn4, progress4, msg4, "ProjectRequestedBytLoad");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, uid + "_ProjectRequestedBytLoad");
                insertStatement.bindString(2, "ProjectRequestedBytLoad");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate4);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick5() {
        if (progress5.getVisibility() == View.VISIBLE)
            if (progress5.getProgress() == -1) {
                msg5.setText("");
                progress5.setProgress(0);
            } else if (progress5.getProgress() == 0) {
                msg5.setText("Loading...");
                msg5.setTextColor(getResources().getColor(R.color.gray));
                progress5.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjectVendorName")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'ProjectVendorName'}";
                Log.d(TAG, "ProjectVendorName--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn5, progress5, msg5, "ProjectVendorName");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, "ProjectVendorName");
                insertStatement.bindString(2, "ProjectVendorName");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick6() {
        if (progress6.getVisibility() == View.VISIBLE)
            if (progress6.getProgress() == -1) {
                msg6.setText("");
                progress6.setProgress(0);
            } else if (progress6.getProgress() == 0) {
                msg6.setText("Loading...");
                msg6.setTextColor(getResources().getColor(R.color.gray));
                progress6.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjectStatusLoad")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'ProjectStatusLoad'}";
                Log.d(TAG, "ProjectStatusLoad--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn6, progress6, msg6, "ProjectStatusLoad");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, "ProjectStatusLoad");
                insertStatement.bindString(2, "ProjectStatusLoad");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick7() {
        if (progress7.getVisibility() == View.VISIBLE)
            if (progress7.getProgress() == -1) {
                msg7.setText("");
                progress7.setProgress(0);
            } else if (progress7.getProgress() == 0) {
                msg7.setText("Loading...");
                msg7.setTextColor(getResources().getColor(R.color.gray));
                progress7.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("WoRefTable")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'WoRefTable'}";
                Log.d(TAG, "WoRefTable--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn7, progress7, msg7, "WoRefTable");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, "WoRefTable");
                insertStatement.bindString(2, "WoRefTable");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick8() {
        if (progress8.getVisibility() == View.VISIBLE)
            if (progress8.getProgress() == -1) {
                msg8.setText("");
                progress8.setProgress(0);
            } else if (progress8.getProgress() == 0) {
                msg8.setText("Loading...");
                msg8.setTextColor(getResources().getColor(R.color.gray));
                progress8.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjIowMaterialChildTable")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'ProjIowMaterialChildTable'}";
                Log.d(TAG, "ProjIowMaterialChildTable--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn8, progress8, msg8, "ProjIowMaterialChildTable");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, "ProjIowMaterialChildTable");
                insertStatement.bindString(2, "ProjIowMaterialChildTable");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick9() {
        if (progress9.getVisibility() == View.VISIBLE)
            if (progress9.getProgress() == -1) {
                msg9.setText("");
                progress9.setProgress(0);
            } else if (progress9.getProgress() == 0) {
                msg9.setText("Loading...");
                msg9.setTextColor(getResources().getColor(R.color.gray));
                msg9.setText("Loading...");
                msg9.setTextColor(getResources().getColor(R.color.gray));
                progress9.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("ProjCmnMasterDetailsTable")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'ProjCmnMasterDetailsTable'}";
                Log.d(TAG, "ProjCmnMasterDetailsTable--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn9, progress9, msg9, "ProjCmnMasterDetailsTable");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, "ProjCmnMasterDetailsTable");
                insertStatement.bindString(2, "ProjCmnMasterDetailsTable");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick10() {
        if (progress10.getVisibility() == View.VISIBLE)
            if (progress10.getProgress() == -1) {
                msg10.setText("");
                progress10.setProgress(0);
            } else if (progress10.getProgress() == 0) {
                msg10.setText("Loading...");
                msg10.setTextColor(getResources().getColor(R.color.gray));
                progress10.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMaterialUomChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMaterialUomChild'}";
                Log.d(TAG, "projMaterialUomChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn10, progress10, msg10, "projMaterialUomChild");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, "projMaterialUomChild");
                insertStatement.bindString(2, "projMaterialUomChild");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick11() {
        if (progress11.getVisibility() == View.VISIBLE)
            if (progress11.getProgress() == -1) {
                msg11.setText("");
                progress11.setProgress(0);
            } else if (progress11.getProgress() == -1) {
                msg11.setText("");
                progress11.setProgress(0);
            } else if (progress11.getProgress() == 0) {
                totalStage = 0;
                incrementStage = 0;
                msg11.setText("Loading...");
                msg11.setTextColor(getResources().getColor(R.color.gray));
                progress11.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("SatgeLoadBasedOnProjectList")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'ProjectListLoad'}";
                Log.d(TAG, "ProjectList--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn11, progress11, msg11, "ProjectListLoad");
                updateOnTableDao.insertOrReplace(new UpdateOnTable(uid + "_SatgeLoadBasedOnProjectList", "SatgeLoadBasedOnProjectList", uid, lastUpdateDate1, "Loading"));
            }
    }
    public void progressClick12() {
        if (progress12.getVisibility() == View.VISIBLE)
            if (progress12.getProgress() == -1) {
                msg12.setText("");
                progress12.setProgress(0);
            } else if (progress12.getProgress() == 0) {
                msg12.setText("Loading...");
                msg12.setTextColor(getResources().getColor(R.color.gray));
                progress12.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("RightsTable")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'HOME_PAGE','Cre_Id':'" + cr_id + "','UID':'" + uid + "','DB':'true'}";
                Log.d(TAG, "RightsTable--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn12, progress12, msg12, "RightsTable");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, uid + "_RightsTable");
                insertStatement.bindString(2, "RightsTable");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick13() {
        if (progress13.getVisibility() == View.VISIBLE)
            if (progress13.getProgress() == -1) {
                msg13.setText("");
                progress13.setProgress(0);
            } else if (progress13.getProgress() == 0) {
                msg13.setText("Loading...");
                msg13.setTextColor(getResources().getColor(R.color.gray));
                progress13.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_mir_proc_child")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMirProcChild','page':'1'}";
                Log.d(TAG, "proj_mir_proc_child--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn13, progress13, msg13, "proj_mir_proc_child");
                dbInsert("proj_mir_proc_child", lastUpdateDate1);
            }
    }
    public void dbInsert(String tableName, String date) {
        String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                + " values (?,?,?,?,?);";
        DatabaseStatement insertStatement = db.compileStatement(insertSql);
        db.beginTransaction();
        insertStatement.clearBindings();
        insertStatement.bindString(1, tableName);
        insertStatement.bindString(2, tableName);
        insertStatement.bindString(3, uid);
        insertStatement.bindString(4, date);
        insertStatement.bindString(5, "Loading");
        insertStatement.execute();
        insertStatement.close();         db.setTransactionSuccessful();
        db.endTransaction();
    }
    public void progressClick14() {
        if (progress14.getVisibility() == View.VISIBLE)
            if (progress14.getProgress() == -1) {
                msg14.setText("");
                progress14.setProgress(0);
            } else if (progress14.getProgress() == 0) {
                msg14.setText("Loading...");
                msg14.setTextColor(getResources().getColor(R.color.gray));
                progress14.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_mir_master")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMirMaster','page':'1'}";
                Log.d(TAG, "proj_mir_master--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn14, progress14, msg14, "proj_mir_master");
                dbInsert("proj_mir_master", lastUpdateDate1);
            }
    }
    public void progressClick15() {
        if (progress15.getVisibility() == View.VISIBLE)
            if (progress15.getProgress() == -1) {
                msg15.setText("");
                progress15.setProgress(0);
            } else if (progress15.getProgress() == 0) {
                msg15.setText("Loading...");
                msg15.setTextColor(getResources().getColor(R.color.gray));
                progress15.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_min_proc_child")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMinProcChild','page':'1'}";
                Log.d(TAG, "proj_min_proc_child--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn15, progress15, msg15, "proj_min_proc_child");
                dbInsert("proj_min_proc_child", lastUpdateDate1);
            }
    }
    public void progressClick16() {
        if (progress16.getVisibility() == View.VISIBLE)
            if (progress16.getProgress() == -1) {
                msg16.setText("");
                progress16.setProgress(0);
            } else if (progress16.getProgress() == 0) {
                msg16.setText("Loading...");
                msg16.setTextColor(getResources().getColor(R.color.gray));
                progress16.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_min_master")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMinMaster','page':'1'}";
                Log.d(TAG, "proj_min_master--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn16, progress16, msg16, "proj_min_master");
                dbInsert("proj_min_master", lastUpdateDate1);
            }
    }
    public void progressClick17() {
        if (progress17.getVisibility() == View.VISIBLE)
            if (progress17.getProgress() == -1) {
                msg17.setText("");
                progress17.setProgress(0);
            } else if (progress17.getProgress() == 0) {
                msg17.setText("Loading...");
                msg17.setTextColor(getResources().getColor(R.color.gray));
                progress17.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("proj_mr_proc_child")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMrProcChild'}";
                Log.d(TAG, "proj_mr_proc_child--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn17, progress17, msg17, "proj_mr_proc_child");
                dbInsert("proj_mr_proc_child", lastUpdateDate1);
            }
    }
    public void progressClick18() {
        if (progress18.getVisibility() == View.VISIBLE)
            if (progress18.getProgress() == -1) {
                msg18.setText("");
                progress18.setProgress(0);
            } else if (progress18.getProgress() == 0) {
                msg18.setText("Loading...");
                msg18.setTextColor(getResources().getColor(R.color.gray));
                progress18.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("mobileRightsKeyMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'mobileRightsKeyMaster'}";
                Log.d(TAG, "mobileRightsKeyMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn18, progress18, msg18, "mobileRightsKeyMaster");
                dbInsert("mobileRightsKeyMaster", lastUpdateDate1);
            }
    }
    public void progressClick19() {
        if (progress19.getVisibility() == View.VISIBLE)
            if (progress19.getProgress() == -1) {
                msg19.setText("");
                progress19.setProgress(0);
            } else if (progress19.getProgress() == 0) {
                msg19.setText("Loading...");
                msg19.setTextColor(getResources().getColor(R.color.gray));
                progress19.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("admEmpMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'admEmpMaster'}";
                Log.d(TAG, "admEmpMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn19, progress19, msg19, "admEmpMaster");
                dbInsert("admEmpMaster", lastUpdateDate1);
            }
    }
    public void progressClick20() {
        if (progress20.getVisibility() == View.VISIBLE)
            if (progress20.getProgress() == -1) {
                msg20.setText("");
                progress20.setProgress(0);
            } else if (progress20.getProgress() == 0) {
                msg20.setText("Loading...");
                msg20.setTextColor(getResources().getColor(R.color.gray));
                progress20.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.User_id.eq(uid), UpdateOnTableDao.Properties.Table_name.eq("projUserMaterialList")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projUserMaterialList'}";
                Log.d(TAG, "projUserMaterialList--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn20, progress20, msg20, "projUserMaterialList");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.clearBindings();
                insertStatement.bindString(1, uid + "_projUserMaterialList");
                insertStatement.bindString(2, "projUserMaterialList");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick21() {
        if (progress21.getVisibility() == View.VISIBLE)
            if (progress21.getProgress() == -1) {
                msg21.setText("");
                progress21.setProgress(0);
            } else if (progress21.getProgress() == 0) {
                msg21.setText("Loading...");
                msg21.setTextColor(getResources().getColor(R.color.gray));
                progress21.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projAlternateMaterialMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projAlternateMaterialMaster'}";
                Log.d(TAG, "projAlternateMaterialMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn21, progress21, msg21, "projAlternateMaterialMaster");
                dbInsert("projAlternateMaterialMaster", lastUpdateDate1);
            }
    }
    public void progressClick22() {
        if (progress22.getVisibility() == View.VISIBLE)
            if (progress22.getProgress() == -1) {
                msg22.setText("");
                progress22.setProgress(0);
            } else if (progress22.getProgress() == 0) {
                msg22.setText("Loading...");
                msg22.setTextColor(getResources().getColor(R.color.gray));
                progress22.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("mobileRightsMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'mobileRightsMaster'}";
                Log.d(TAG, "mobileRightsMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn22, progress22, msg22, "mobileRightsMaster");
                dbInsert("mobileRightsMaster", lastUpdateDate1);
            }
    }
    public void progressClick23() {
        if (progress23.getVisibility() == View.VISIBLE)
            if (progress23.getProgress() == -1) {
                msg23.setText("");
                progress23.setProgress(0);
            } else if (progress23.getProgress() == 0) {
                msg23.setText("Loading...");
                msg23.setTextColor(getResources().getColor(R.color.gray));
                progress23.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMaterialChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMaterialChild'}";
                Log.d(TAG, "projMaterialChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn23, progress23, msg23, "projMaterialChild");
                dbInsert("projMaterialChild", lastUpdateDate1);
            }
    }
    public void progressClick24() {
        if (progress24.getVisibility() == View.VISIBLE)
            if (progress24.getProgress() == -1) {
                msg24.setText("");
                progress24.setProgress(0);
            } else if (progress24.getProgress() == 0) {
                msg24.setText("Loading...");
                msg24.setTextColor(getResources().getColor(R.color.gray));
                progress24.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoMaster'}";
                Log.d(TAG, "projPoMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn24, progress24, msg24, "projPoMaster");
                dbInsert("projPoMaster", lastUpdateDate1);
            }
    }
    public void progressClick25() {
        if (progress25.getVisibility() == View.VISIBLE)
            if (progress25.getProgress() == -1) {
                msg25.setText("");
                progress25.setProgress(0);
            } else if (progress25.getProgress() == 0) {
                msg25.setText("Loading...");
                msg25.setTextColor(getResources().getColor(R.color.gray));
                progress25.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoItemChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoItemChild','page':'1'}";
                Log.d(TAG, "projPoItemChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn25, progress25, msg25, "projPoItemChild");
                dbInsert("projPoItemChild", lastUpdateDate1);
            }
    }
    public void progressClick26() {
        if (progress26.getVisibility() == View.VISIBLE)
            if (progress26.getProgress() == -1) {
                msg26.setText("");
                progress26.setProgress(0);
            } else if (progress26.getProgress() == 0) {
                msg26.setText("Loading...");
                msg26.setTextColor(getResources().getColor(R.color.gray));
                progress26.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobIowStageMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projJobIowStageMaster','page':'1'}";
                Log.d(TAG, "projJobIowStageMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn26, progress26, msg26, "projJobIowStageMaster");
                dbInsert("projJobIowStageMaster", lastUpdateDate1);
            }
    }
    public void progressClick27() {
        if (progress27.getVisibility() == View.VISIBLE)
            if (progress27.getProgress() == -1) {
                msg27.setText("");
                progress27.setProgress(0);
            } else if (progress27.getProgress() == 0) {
                msg27.setText("Loading...");
                msg27.setTextColor(getResources().getColor(R.color.gray));
                progress27.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projStageIowMaterialDet")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projStageIowMaterialDet'}";
                Log.d(TAG, "projStageIowMaterialDet--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn27, progress27, msg27, "projStageIowMaterialDet");
                dbInsert("projStageIowMaterialDet", lastUpdateDate1);
            }
    }
    public void progressClick28() {
        if (progress28.getVisibility() == View.VISIBLE)
            if (progress28.getProgress() == -1) {
                msg28.setText("");
                progress28.setProgress(0);
            } else if (progress28.getProgress() == 0) {
                msg28.setText("Loading...");
                msg28.setTextColor(getResources().getColor(R.color.gray));
                progress28.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMrMaster'}";
                Log.d(TAG, "projMrMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn28, progress28, msg28, "projMrMaster");
                dbInsert("projMrMaster", lastUpdateDate1);
            }
    }
    public void progressClick29() {
        if (progress29.getVisibility() == View.VISIBLE)
            if (progress29.getProgress() == -1) {
                msg29.setText("");
                progress29.setProgress(0);
            } else if (progress29.getProgress() == 0) {
                msg29.setText("Loading...");
                msg29.setTextColor(getResources().getColor(R.color.gray));
                progress29.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projVendorMasterView")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projVendorMasterView'}";
                Log.d(TAG, "projVendorMasterView--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn29, progress29, msg29, "projVendorMasterView");
                dbInsert("projVendorMasterView", lastUpdateDate1);
            }
    }
    public void progressClick30() {
        if (progress30.getVisibility() == View.VISIBLE)
            if (progress30.getProgress() == -1) {
                msg30.setText("");
                progress30.setProgress(0);
            } else if (progress30.getProgress() == 0) {
                msg30.setText("Loading...");
                msg30.setTextColor(getResources().getColor(R.color.gray));
                progress30.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("admEmpMasterView")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'admEmpMasterView'}";
                Log.d(TAG, "admEmpMasterView--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn30, progress30, msg30, "admEmpMasterView");
                dbInsert("admEmpMasterView", lastUpdateDate1);
            }
    }
    public void progressClick31() {
        if (progress31.getVisibility() == View.VISIBLE)
            if (progress31.getProgress() == -1) {
                msg31.setText("");
                progress31.setProgress(0);
            } else if (progress31.getProgress() == 0) {
                msg31.setText("Loading...");
                msg31.setTextColor(getResources().getColor(R.color.gray));
                progress31.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("arcApprovalConfig")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'arcApprovalConfig'}";
                Log.d(TAG, "arcApprovalConfig--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn31, progress31, msg31, "arcApprovalConfig");
                dbInsert("arcApprovalConfig", lastUpdateDate1);
            }
    }
    public void progressClick32() {
        if (progress32.getVisibility() == View.VISIBLE)
            if (progress32.getProgress() == -1) {
                msg32.setText("");
                progress32.setProgress(0);
            } else if (progress32.getProgress() == 0) {
                msg32.setText("Loading...");
                msg32.setTextColor(getResources().getColor(R.color.gray));
                progress32.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projUserProjectList")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projUserProjectList'}";
                Log.d(TAG, "projUserProjectList--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn32, progress32, msg32, "projUserProjectList");
                dbInsert("projUserProjectList", lastUpdateDate1);
            }
    }
    public void progressClick33() {
        if (progress33.getVisibility() == View.VISIBLE)
            if (progress33.getProgress() == -1) {
                msg33.setText("");
                progress33.setProgress(0);
            } else if (progress33.getProgress() == 0) {
                msg33.setText("Loading...");
                msg33.setTextColor(getResources().getColor(R.color.gray));
                progress33.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projStoreStock")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projStoreStock','page':'1'}";
                Log.d(TAG, "projStoreStock--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn33, progress33, msg33, "projStoreStock");
                dbInsert("projStoreStock", lastUpdateDate1);
            }
    }
    public void progressClick34() {
        if (progress34.getVisibility() == View.VISIBLE)
            if (progress34.getProgress() == -1) {
                msg34.setText("");
                progress34.setProgress(0);
            } else if (progress34.getProgress() == 0) {
                msg34.setText("Loading...");
                msg34.setTextColor(getResources().getColor(R.color.gray));
                progress34.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projStoreMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projStoreMaster'}";
                Log.d(TAG, "projStoreMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn34, progress34, msg34, "projStoreMaster");
                dbInsert("projStoreMaster", lastUpdateDate1);
            }
    }
    public void progressClick35() {
        if (progress35.getVisibility() == View.VISIBLE)
            if (progress35.getProgress() == -1) {
                msg35.setText("");
                progress35.setProgress(0);
            } else if (progress35.getProgress() == 0) {
                msg35.setText("Loading...");
                msg35.setTextColor(getResources().getColor(R.color.gray));
                progress35.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projProjectAddressMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projProjectAddressMaster'}";
                Log.d(TAG, "projProjectAddressMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn35, progress35, msg35, "projProjectAddressMaster");
                dbInsert("projProjectAddressMaster", lastUpdateDate1);
            }
    }
    public void progressClick36() {
        if (progress36.getVisibility() == View.VISIBLE)
            if (progress36.getProgress() == -1) {
                msg36.setText("");
                progress36.setProgress(0);
            } else if (progress36.getProgress() == 0) {
                msg36.setText("Loading...");
                msg36.setTextColor(getResources().getColor(R.color.gray));
                progress36.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoOtherChargeChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoOtherChargeChild'}";
                Log.d(TAG, "projPoOtherChargeChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn36, progress36, msg36, "projPoOtherChargeChild");
                dbInsert("projPoOtherChargeChild", lastUpdateDate1);
            }
    }
    public void progressClick37() {
        if (progress37.getVisibility() == View.VISIBLE)
            if (progress37.getProgress() == -1) {
                msg37.setText("");
                progress37.setProgress(0);
            } else if (progress37.getProgress() == 0) {
                msg37.setText("Loading...");
                msg37.setTextColor(getResources().getColor(R.color.gray));
                progress37.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("cmnPartyAddressInfo")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'cmnPartyAddressInfo','page':'1'}";
                Log.d(TAG, "cmnPartyAddressInfo--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn37, progress37, msg37, "cmnPartyAddressInfo");
                dbInsert("cmnPartyAddressInfo", lastUpdateDate1);
            }
    }
    public void progressClick38() {
        if (progress38.getVisibility() == View.VISIBLE)
            if (progress38.getProgress() == -1) {
                msg38.setText("");
                progress38.setProgress(0);
            } else if (progress38.getProgress() == 0) {
                msg38.setText("Loading...");
                msg38.setTextColor(getResources().getColor(R.color.gray));
                progress38.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoGernalTermsChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoGernalTermsChild'}";
                Log.d(TAG, "projPoGernalTermsChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn38, progress38, msg38, "projPoGernalTermsChild");
                dbInsert("projPoGernalTermsChild", lastUpdateDate1);
            }
    }
    public void progressClick39() {
        if (progress39.getVisibility() == View.VISIBLE)
            if (progress39.getProgress() == -1) {
                msg39.setText("");
                progress39.setProgress(0);
            } else if (progress39.getProgress() == 0) {
                msg39.setText("Loading...");
                msg39.setTextColor(getResources().getColor(R.color.gray));
                progress39.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoTermsChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoTermsChild'}";
                Log.d(TAG, "projPoTermsChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn39, progress39, msg39, "projPoTermsChild");
                dbInsert("projPoTermsChild", lastUpdateDate1);
            }
    }
    public void progressClick40() {
        if (progress40.getVisibility() == View.VISIBLE)
            if (progress40.getProgress() == -1) {
                msg40.setText("");
                progress40.setProgress(0);
            } else if (progress40.getProgress() == 0) {
                msg40.setText("Loading...");
                msg40.setTextColor(getResources().getColor(R.color.gray));
                progress40.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("cmnTaxMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'cmnTaxMaster'}";
                Log.d(TAG, "cmnTaxMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn40, progress40, msg40, "cmnTaxMaster");
                dbInsert("cmnTaxMaster", lastUpdateDate1);
            }
    }
    public void progressClick41() {
        if (progress41.getVisibility() == View.VISIBLE)
            if (progress41.getProgress() == -1) {
                msg41.setText("");
                progress41.setProgress(0);
            } else if (progress41.getProgress() == 0) {
                msg41.setText("Loading...");
                msg41.setTextColor(getResources().getColor(R.color.gray));
                progress41.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("cmnPartyCompanyInfo")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'cmnPartyCompanyInfo'}";
                Log.d(TAG, "cmnPartyCompanyInfo--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn41, progress41, msg41, "cmnPartyCompanyInfo");
                dbInsert("cmnPartyCompanyInfo", lastUpdateDate1);
            }
    }
    public void progressClick42() {
        if (progress42.getVisibility() == View.VISIBLE)
            if (progress42.getProgress() == -1) {
                msg42.setText("");
                progress42.setProgress(0);
            } else if (progress42.getProgress() == 0) {
                msg42.setText("Loading...");
                msg42.setTextColor(getResources().getColor(R.color.gray));
                progress42.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("cmnPartyIdDocInfo")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'cmnPartyIdDocInfo'}";
                Log.d(TAG, "cmnPartyIdDocInfo--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn42, progress42, msg42, "cmnPartyIdDocInfo");
                dbInsert("cmnPartyIdDocInfo", lastUpdateDate1);
            }
    }
    public void progressClick43() {
        if (progress43.getVisibility() == View.VISIBLE)
            if (progress43.getProgress() == -1) {
                msg43.setText("");
                progress43.setProgress(0);
            } else if (progress43.getProgress() == 0) {
                msg43.setText("Loading...");
                msg43.setTextColor(getResources().getColor(R.color.gray));
                progress43.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMbookMaster','page':'1'}";
                Log.d(TAG, "projMbookMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn43, progress43, msg43, "projMbookMaster");
                dbInsert("projMbookMaster", lastUpdateDate1);
            }
    }
    public void progressClick44() {
        if (progress44.getVisibility() == View.VISIBLE)
            if (progress44.getProgress() == -1) {
                msg44.setText("");
                progress44.setProgress(0);
            } else if (progress44.getProgress() == 0) {
                msg44.setText("Loading...");
                msg44.setTextColor(getResources().getColor(R.color.gray));
                progress44.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projContractorMasterView")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projContractorMasterView'}";
                Log.d(TAG, "projContractorMasterView--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn44, progress44, msg44, "projContractorMasterView");
                dbInsert("projContractorMasterView", lastUpdateDate1);
            }
    }
    public void progressClick45() {
        if (progress45.getVisibility() == View.VISIBLE)
            if (progress45.getProgress() == -1) {
                msg45.setText("");
                progress45.setProgress(0);
            } else if (progress45.getProgress() == 0) {
                msg45.setText("Loading...");
                msg45.setTextColor(getResources().getColor(R.color.gray));
                progress45.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projProjectMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projProjectMaster'}";
                Log.d(TAG, "projProjectMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn45, progress45, msg45, "projProjectMaster");
                dbInsert("projProjectMaster", lastUpdateDate1);
            }
    }
    public void progressClick46() {
        if (progress46.getVisibility() == View.VISIBLE)
            if (progress46.getProgress() == -1) {
                msg46.setText("");
                progress46.setProgress(0);
            } else if (progress46.getProgress() == 0) {
                msg46.setText("Loading...");
                msg46.setTextColor(getResources().getColor(R.color.gray));
                progress46.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookSubActivityChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMbookSubActivityChild','page':'1'}";
                Log.d(TAG, "projMbookSubActivityChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn46, progress46, msg46, "projMbookSubActivityChild");
                dbInsert("projMbookSubActivityChild", lastUpdateDate1);
            }
    }
    public void progressClick47() {
        if (progress47.getVisibility() == View.VISIBLE)
            if (progress47.getProgress() == -1) {
                msg47.setText("");
                progress47.setProgress(0);
            } else if (progress47.getProgress() == 0) {
                msg47.setText("Loading...");
                msg47.setTextColor(getResources().getColor(R.color.gray));
                progress47.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookIowNmrChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMbookIowNmrChild','page':'1'}";
                Log.d(TAG, "projMbookIowNmrChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn47, progress47, msg47, "projMbookIowNmrChild");
                dbInsert("projMbookIowNmrChild", lastUpdateDate1);
            }
    }
    public void progressClick48() {
        if (progress48.getVisibility() == View.VISIBLE)
            if (progress48.getProgress() == -1) {
                msg48.setText("");
                progress48.setProgress(0);
            } else if (progress48.getProgress() == 0) {
                msg48.setText("Loading...");
                msg48.setTextColor(getResources().getColor(R.color.gray));
                progress48.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookiowChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMbookIowChild','page':'1'}";
                Log.d(TAG, "projMbookiowChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn48, progress48, msg48, "projMbookiowChild");
                dbInsert("projMbookiowChild", lastUpdateDate1);
            }
    }
    public void progressClick49() {
        if (progress49.getVisibility() == View.VISIBLE)
            if (progress49.getProgress() == -1) {
                msg49.setText("");
                progress49.setProgress(0);
            } else if (progress49.getProgress() == 0) {
                msg49.setText("Loading...");
                msg49.setTextColor(getResources().getColor(R.color.gray));
                progress49.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projStageChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projStageChild'}";
                Log.d(TAG, "projStageChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn49, progress49, msg49, "projStageChild");
                dbInsert("projStageChild", lastUpdateDate1);
            }
    }
    public void progressClick50() {
        if (progress50.getVisibility() == View.VISIBLE)
            if (progress50.getProgress() == -1) {
                msg50.setText("");
                progress50.setProgress(0);
            } else if (progress50.getProgress() == 0) {
                msg50.setText("Loading...");
                msg50.setTextColor(getResources().getColor(R.color.gray));
                progress50.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projLabourMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projLabourMaster'}";
                Log.d(TAG, "projLabourMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn50, progress50, msg50, "projLabourMaster");
                dbInsert("projLabourMaster", lastUpdateDate1);
            }
    }
    public void progressClick51() {
        if (progress51.getVisibility() == View.VISIBLE)
            if (progress51.getProgress() == -1) {
                msg51.setText("");
                progress51.setProgress(0);
            } else if (progress51.getProgress() == 0) {
                msg51.setText("Loading...");
                msg51.setTextColor(getResources().getColor(R.color.gray));
                progress51.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projFormulaMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projFormulaMaster'}";
                Log.d(TAG, "projFormulaMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn51, progress51, msg51, "projFormulaMaster");
                dbInsert("projFormulaMaster", lastUpdateDate1);
            }
    }
    public void progressClick52() {
        if (progress52.getVisibility() == View.VISIBLE)
            if (progress52.getProgress() == -1) {
                msg52.setText("");
                progress52.setProgress(0);
            } else if (progress52.getProgress() == 0) {
                msg52.setText("Loading...");
                msg52.setTextColor(getResources().getColor(R.color.gray));
                progress52.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookIowGridChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMbookIowGridChild','page':'1'}";
                Log.d(TAG, "projMbookIowGridChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn52, progress52, msg52, "projMbookIowGridChild");
                dbInsert("projMbookIowGridChild", lastUpdateDate1);
            }
    }
  /*  public void progressClick53() {
        if (progress53.getVisibility() == View.VISIBLE)
            if (progress53.getProgress() == -1) {
                msg53.setText("");
                progress53.setProgress(0);
            } else if (progress53.getProgress() == 0) {
                msg53.setText("Loading...");
                msg53.setTextColor(getResources().getColor(R.color.gray));
                progress53.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobStageGridIow")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projJobStageGridIow','page':'1'}";
                Log.d(TAG, "projJobStageGridIow--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn53, progress53, msg53, "projJobStageGridIow");
                dbInsert("projJobStageGridIow", lastUpdateDate1);
            }
    }
    public void progressClick54() {
        if (progress54.getVisibility() == View.VISIBLE)
            if (progress54.getProgress() == -1) {
                msg54.setText("");
                progress54.setProgress(0);
            } else if (progress54.getProgress() == 0) {
                msg54.setText("Loading...");
                msg54.setTextColor(getResources().getColor(R.color.gray));
                progress54.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobStageGridMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'','dataTable':'projJobStageGridMaster','page':'1'}";
                Log.d(TAG, "projJobStageGridMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn54, progress54, msg54, "projJobStageGridMaster");
                dbInsert("projJobStageGridMaster", lastUpdateDate1);
            }
    }*/
    public void progressClick55() {
        if (progress55.getVisibility() == View.VISIBLE)
            if (progress55.getProgress() == -1) {
                msg55.setText("");
                progress55.setProgress(0);
            } else if (progress55.getProgress() == 0) {
                msg55.setText("Loading...");
                msg55.setTextColor(getResources().getColor(R.color.gray));
                progress55.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projIowMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projIowMaster'}";
                Log.d(TAG, "projIowMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn55, progress55, msg55, "projIowMaster");
                dbInsert("projIowMaster", lastUpdateDate1);
            }
    }
    public void progressClick56() {
        if (progress56.getVisibility() == View.VISIBLE)
            if (progress56.getProgress() == -1) {
                msg56.setText("");
                progress56.setProgress(0);
            } else if (progress56.getProgress() == 0) {
                msg56.setText("Loading...");
                msg56.setTextColor(getResources().getColor(R.color.gray));
                progress56.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projIowMaterialChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projIowMaterialChild'}";
                Log.d(TAG, "projIowMaterialChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn56, progress56, msg56, "projIowMaterialChild");
                dbInsert("projIowMaterialChild", lastUpdateDate1);
            }
    }
    public void progressClick57() {
        if (progress57.getVisibility() == View.VISIBLE)
            if (progress57.getProgress() == -1) {
                msg57.setText("");
                progress57.setProgress(0);
            } else if (progress57.getProgress() == 0) {
                msg57.setText("Loading...");
                msg57.setTextColor(getResources().getColor(R.color.gray));
                progress57.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projJobMaster'}";
                Log.d(TAG, "projJobMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn57, progress57, msg57, "projJobMaster");
                dbInsert("projJobMaster", lastUpdateDate1);
            }
    }
    public void progressClick58() {
        if (progress58.getVisibility() == View.VISIBLE)
            if (progress58.getProgress() == -1) {
                msg58.setText("");
                progress58.setProgress(0);
            } else if (progress58.getProgress() == 0) {
                msg58.setText("Loading...");
                msg58.setTextColor(getResources().getColor(R.color.gray));
                progress58.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projJobIowMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projJobIowMaster'}";
                Log.d(TAG, "projJobIowMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn58, progress58, msg58, "projJobIowMaster");
                dbInsert("projJobIowMaster", lastUpdateDate1);
            }
    }
    public void progressClick59() {
        if (progress59.getVisibility() == View.VISIBLE)
            if (progress59.getProgress() == -1) {
                msg59.setText("");
                progress59.setProgress(0);
            } else if (progress59.getProgress() == 0) {
                msg59.setText("Loading...");
                msg59.setTextColor(getResources().getColor(R.color.gray));
                progress59.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projUserStageList")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projUserStageList','page':'1'}";
                Log.d(TAG, "projUserStageList--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn59, progress59, msg59, "projUserStageList");
                String insertSql = "Insert or Replace into " + updateOnTableDao.getTablename()
                        + " values (?,?,?,?,?);";
                DatabaseStatement insertStatement = db.compileStatement(insertSql);
                db.beginTransaction();
                insertStatement.bindString(1, uid + "_projUserStageList");
                insertStatement.bindString(2, "projUserStageList");
                insertStatement.bindString(3, uid);
                insertStatement.bindString(4, lastUpdateDate1);
                insertStatement.bindString(5, "Loading");
                insertStatement.execute();
                insertStatement.close();         db.setTransactionSuccessful();
                db.endTransaction();
            }
    }
    public void progressClick60() {
        if (progress60.getVisibility() == View.VISIBLE)
            if (progress60.getProgress() == -1) {
                msg60.setText("");
                progress60.setProgress(0);
            } else if (progress60.getProgress() == 0) {
                msg60.setText("Loading...");
                msg60.setTextColor(getResources().getColor(R.color.gray));
                progress60.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMrChild','page':'1'}";
                Log.d(TAG, "projMrChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn60, progress60, msg60, "projMrChild");
                dbInsert("projMrChild", lastUpdateDate1);
            }
    }
    public void progressClick61() {
        if (progress61.getVisibility() == View.VISIBLE)
            if (progress61.getProgress() == -1) {
                msg61.setText("");
                progress61.setProgress(0);
            } else if (progress61.getProgress() == 0) {
                msg61.setText("Loading...");
                msg61.setTextColor(getResources().getColor(R.color.gray));
                progress61.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrItemScheduleChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMrItemScheduleChild'}";
                Log.d(TAG, "projMrItemScheduleChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn61, progress61, msg61, "projMrItemScheduleChild");
                dbInsert("projMrItemScheduleChild", lastUpdateDate1);
            }
    }
    public void progressClick62() {
        if (progress62.getVisibility() == View.VISIBLE)
            if (progress62.getProgress() == -1) {
                msg62.setText("");
                progress62.setProgress(0);
            } else if (progress62.getProgress() == 0) {
                msg62.setText("Loading...");
                msg62.setTextColor(getResources().getColor(R.color.gray));
                progress62.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMirChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMirChild','page':'1'}";
                Log.d(TAG, "projMirChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn62, progress62, msg62, "projMirChild");
                dbInsert("projMirChild", lastUpdateDate1);
            }
    }
    public void progressClick63() {
        if (progress63.getVisibility() == View.VISIBLE)
            if (progress63.getProgress() == -1) {
                msg63.setText("");
                progress63.setProgress(0);
            } else if (progress63.getProgress() == 0) {
                msg63.setText("Loading...");
                msg63.setTextColor(getResources().getColor(R.color.gray));
                progress63.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projVechicleMovementForm")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projVechicleMovementForm','page':'1'}";
                Log.d(TAG, "projVechicleMovementForm--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn63, progress63, msg63, "projVechicleMovementForm");
                dbInsert("projVechicleMovementForm", lastUpdateDate1);
            }
    }
    public void progressClick64() {
        if (progress64.getVisibility() == View.VISIBLE)
            if (progress64.getProgress() == -1) {
                msg64.setText("");
                progress64.setProgress(0);
            } else if (progress64.getProgress() == 0) {
                msg64.setText("Loading...");
                msg64.setTextColor(getResources().getColor(R.color.gray));
                progress64.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projIndentMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projIndentMaster','page':'1'}";
                Log.d(TAG, "projIndentMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn64, progress64, msg64, "projIndentMaster");
                dbInsert("projIndentMaster", lastUpdateDate1);
            }
    }
    public void progressClick65() {
        if (progress65.getVisibility() == View.VISIBLE)
            if (progress65.getProgress() == -1) {
                msg65.setText("");
                progress65.setProgress(0);
            } else if (progress65.getProgress() == 0) {
                msg65.setText("Loading...");
                msg65.setTextColor(getResources().getColor(R.color.gray));
                progress65.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projIndentChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projIndentChild','page':'1'}";
                Log.d(TAG, "projIndentChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn65, progress65, msg65, "projIndentChild");
                dbInsert("projIndentChild", lastUpdateDate1);
            }
    }
    public void progressClick66() {
        if (progress66.getVisibility() == View.VISIBLE)
            if (progress66.getProgress() == -1) {
                msg66.setText("");
                progress66.setProgress(0);
            } else if (progress66.getProgress() == 0) {
                msg66.setText("Loading...");
                msg66.setTextColor(getResources().getColor(R.color.gray));
                progress66.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMinChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMinChild','page':'1'}";
                Log.d(TAG, "projMinChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn66, progress66, msg66, "projMinChild");
                dbInsert("projMinChild", lastUpdateDate1);
            }
    }
    public void progressClick67() {
        if (progress67.getVisibility() == View.VISIBLE)
            if (progress67.getProgress() == -1) {
                msg67.setText("");
                progress67.setProgress(0);
            } else if (progress67.getProgress() == 0) {
                msg67.setText("Loading...");
                msg67.setTextColor(getResources().getColor(R.color.gray));
                progress67.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projGrnMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projGrnMaster','page':'1'}";
                Log.d(TAG, "projGrnMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn67, progress67, msg67, "projGrnMaster");
                dbInsert("projGrnMaster", lastUpdateDate1);
            }
    }
    public void progressClick68() {
        if (progress68.getVisibility() == View.VISIBLE)
            if (progress68.getProgress() == -1) {
                msg68.setText("");
                progress68.setProgress(0);
            } else if (progress68.getProgress() == 0) {
                msg68.setText("Loading...");
                msg68.setTextColor(getResources().getColor(R.color.gray));
                progress68.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projGrnItemChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projGrnItemChild','page':'1'}";
                Log.d(TAG, "projGrnItemChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn68, progress68, msg68, "projGrnItemChild");
                dbInsert("projGrnItemChild", lastUpdateDate1);
            }
    }
    public void progressClick69() {
        if (progress69.getVisibility() == View.VISIBLE)
            if (progress69.getProgress() == -1) {
                msg69.setText("");
                progress69.setProgress(0);
            } else if (progress69.getProgress() == 0) {
                msg69.setText("Loading...");
                msg69.setTextColor(getResources().getColor(R.color.gray));
                progress69.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("weightData")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'weightData','page':'1'}";
                Log.d(TAG, "weightData--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn69, progress69, msg69, "weightData");
                dbInsert("weightData", lastUpdateDate1);
            }
    }
    public void progressClick70() {
        if (progress70.getVisibility() == View.VISIBLE)
            if (progress70.getProgress() == -1) {
                msg70.setText("");
                progress70.setProgress(0);
            } else if (progress70.getProgress() == 0) {
                msg70.setText("Loading...");
                msg70.setTextColor(getResources().getColor(R.color.gray));
                progress70.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projGrnOtherChargeChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projGrnOtherChargeChild'}";
                Log.d(TAG, "projGrnOtherChargeChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn70, progress70, msg70, "projGrnOtherChargeChild");
                dbInsert("projGrnOtherChargeChild", lastUpdateDate1);
            }
    }
    public void progressClick71() {
        if (progress71.getVisibility() == View.VISIBLE)
            if (progress71.getProgress() == -1) {
                msg71.setText("");
                progress71.setProgress(0);
            } else if (progress71.getProgress() == 0) {
                msg71.setText("Loading...");
                msg71.setTextColor(getResources().getColor(R.color.gray));
                progress71.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projBmrfMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projBmrfMaster'}";
                Log.d(TAG, "projBmrfMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn71, progress71, msg71, "projBmrfMaster");
                dbInsert("projBmrfMaster", lastUpdateDate1);
            }
    }
    public void progressClick72() {
        if (progress72.getVisibility() == View.VISIBLE)
            if (progress72.getProgress() == -1) {
                msg72.setText("");
                progress72.setProgress(0);
            } else if (progress72.getProgress() == 0) {
                msg72.setText("Loading...");
                msg72.setTextColor(getResources().getColor(R.color.gray));
                progress72.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMatBmrfChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMatBmrfChild'}";
                Log.d(TAG, "projMatBmrfChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn72, progress72, msg72, "projMatBmrfChild");
                dbInsert("projMatBmrfChild", lastUpdateDate1);
            }
    }
    public void progressClick73() {
        if (progress73.getVisibility() == View.VISIBLE)
            if (progress73.getProgress() == -1) {
                msg73.setText("");
                progress73.setProgress(0);
            } else if (progress73.getProgress() == 0) {
                msg73.setText("Loading...");
                msg73.setTextColor(getResources().getColor(R.color.gray));
                progress73.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrirMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMrirMaster'}";
                Log.d(TAG, "projMrirMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn73, progress73, msg73, "projMrirMaster");
                dbInsert("projMrirMaster", lastUpdateDate1);
            }
    }
    public void progressClick74() {
        if (progress74.getVisibility() == View.VISIBLE)
            if (progress74.getProgress() == -1) {
                msg74.setText("");
                progress74.setProgress(0);
            } else if (progress74.getProgress() == 0) {
                msg74.setText("Loading...");
                msg74.setTextColor(getResources().getColor(R.color.gray));
                progress74.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrirItemChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMrirItemChild'}";
                Log.d(TAG, "projMrirItemChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn74, progress74, msg74, "projMrirItemChild");
                dbInsert("projMrirItemChild", lastUpdateDate1);
            }
    }
    public void progressClick75() {
        if (progress75.getVisibility() == View.VISIBLE)
            if (progress75.getProgress() == -1) {
                msg75.setText("");
                progress75.setProgress(0);
            } else if (progress75.getProgress() == 0) {
                msg75.setText("Loading...");
                msg75.setTextColor(getResources().getColor(R.color.gray));
                progress75.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMrirOtherChargeChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMrirOtherChargeChild'}";
                Log.d(TAG, "projMrirOtherChargeChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn75, progress75, msg75, "projMrirOtherChargeChild");
                dbInsert("projMrirOtherChargeChild", lastUpdateDate1);
            }
    }
    public void progressClick76() {
        if (progress76.getVisibility() == View.VISIBLE)
            if (progress76.getProgress() == -1) {
                msg76.setText("");
                progress76.setProgress(0);
            } else if (progress76.getProgress() == 0) {
                msg76.setText("Loading...");
                msg76.setTextColor(getResources().getColor(R.color.gray));
                progress76.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtdnMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtdnMaster'}";
                Log.d(TAG, "projMtdnMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn76, progress76, msg76, "projMtdnMaster");
                dbInsert("projMtdnMaster", lastUpdateDate1);
            }
    }
    public void progressClick77() {
        if (progress77.getVisibility() == View.VISIBLE)
            if (progress77.getProgress() == -1) {
                msg77.setText("");
                progress77.setProgress(0);
            } else if (progress77.getProgress() == 0) {
                msg77.setText("Loading...");
                msg77.setTextColor(getResources().getColor(R.color.gray));
                progress77.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtdnChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtdnChild'}";
                Log.d(TAG, "projMtdnChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn77, progress77, msg77, "projMtdnChild");
                dbInsert("projMtdnChild", lastUpdateDate1);
            }
    }
    public void progressClick78() {
        if (progress78.getVisibility() == View.VISIBLE)
            if (progress78.getProgress() == -1) {
                msg78.setText("");
                progress78.setProgress(0);
            } else if (progress78.getProgress() == 0) {
                msg78.setText("Loading...");
                msg78.setTextColor(getResources().getColor(R.color.gray));
                progress78.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtdnOtherChargeChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtdnOtherChargeChild'}";
                Log.d(TAG, "projMtdnOtherChargeChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn78, progress78, msg78, "projMtdnOtherChargeChild");
                dbInsert("projMtdnOtherChargeChild", lastUpdateDate1);
            }
    }
    public void progressClick79() {
        if (progress79.getVisibility() == View.VISIBLE)
            if (progress79.getProgress() == -1) {
                msg79.setText("");
                progress79.setProgress(0);
            } else if (progress79.getProgress() == 0) {
                msg79.setText("Loading...");
                msg79.setTextColor(getResources().getColor(R.color.gray));
                progress79.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtnMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtnMaster'}";
                Log.d(TAG, "projMtnMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn79, progress79, msg79, "projMtnMaster");
                dbInsert("projMtnMaster", lastUpdateDate1);
            }
    }
    public void progressClick80() {
        if (progress80.getVisibility() == View.VISIBLE)
            if (progress80.getProgress() == -1) {
                msg80.setText("");
                progress80.setProgress(0);
            } else if (progress80.getProgress() == 0) {
                msg80.setText("Loading...");
                msg80.setTextColor(getResources().getColor(R.color.gray));
                progress80.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtnChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtnChild'}";
                Log.d(TAG, "projMtnChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn80, progress80, msg80, "projMtnChild");
                dbInsert("projMtnChild", lastUpdateDate1);
            }
    }
    public void progressClick81() {
        if (progress81.getVisibility() == View.VISIBLE)
            if (progress81.getProgress() == -1) {
                msg81.setText("");
                progress81.setProgress(0);
            } else if (progress81.getProgress() == 0) {
                msg81.setText("Loading...");
                msg81.setTextColor(getResources().getColor(R.color.gray));
                progress81.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtrnMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtrnMaster'}";
                Log.d(TAG, "projMtrnMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn81, progress81, msg81, "projMtrnMaster");
                dbInsert("projMtrnMaster", lastUpdateDate1);
            }
    }
    public void progressClick82() {
        if (progress82.getVisibility() == View.VISIBLE)
            if (progress82.getProgress() == -1) {
                msg82.setText("");
                progress82.setProgress(0);
            } else if (progress82.getProgress() == 0) {
                msg82.setText("Loading...");
                msg82.setTextColor(getResources().getColor(R.color.gray));
                progress82.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtnCloseMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtnCloseMaster'}";
                Log.d(TAG, "projMtnCloseMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn82, progress82, msg82, "projMtnCloseMaster");
                dbInsert("projMtnCloseMaster", lastUpdateDate1);
            }
    }
    public void progressClick83() {
        if (progress83.getVisibility() == View.VISIBLE)
            if (progress83.getProgress() == -1) {
                msg83.setText("");
                progress83.setProgress(0);
            } else if (progress83.getProgress() == 0) {
                msg83.setText("Loading...");
                msg83.setTextColor(getResources().getColor(R.color.gray));
                progress83.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoBillRecommendationMaster'}";
                Log.d(TAG, "projPoBillRecommendationMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn83, progress83, msg83, "projPoBillRecommendationMaster");
                dbInsert("projPoBillRecommendationMaster", lastUpdateDate1);
            }
    }
    public void progressClick84() {
        if (progress84.getVisibility() == View.VISIBLE)
            if (progress84.getProgress() == -1) {
                msg84.setText("");
                progress84.setProgress(0);
            } else if (progress84.getProgress() == 0) {
                msg84.setText("Loading...");
                msg84.setTextColor(getResources().getColor(R.color.gray));
                progress84.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoBillMaster'}";
                Log.d(TAG, "projPoBillMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn84, progress84, msg84, "projPoBillMaster");
                dbInsert("projPoBillMaster", lastUpdateDate1);
            }
    }
    public void progressClick85() {
        if (progress85.getVisibility() == View.VISIBLE)
            if (progress85.getProgress() == -1) {
                msg85.setText("");
                progress85.setProgress(0);
            } else if (progress85.getProgress() == 0) {
                msg85.setText("Loading...");
                msg85.setTextColor(getResources().getColor(R.color.gray));
                progress85.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoBillRecommendationChild'}";
                Log.d(TAG, "projPoBillRecommendationChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn85, progress85, msg85, "projPoBillRecommendationChild");
                dbInsert("projPoBillRecommendationChild", lastUpdateDate1);
            }
    }
    public void progressClick86() {
        if (progress86.getVisibility() == View.VISIBLE)
            if (progress86.getProgress() == -1) {
                msg86.setText("");
                progress86.setProgress(0);
            } else if (progress86.getProgress() == 0) {
                msg86.setText("Loading...");
                msg86.setTextColor(getResources().getColor(R.color.gray));
                progress86.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationPaymentChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoBillRecommendationPaymentChild'}";
                Log.d(TAG, "projPoBillRecommendationPaymentChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn86, progress86, msg86, "projPoBillRecommendationPaymentChild");
                dbInsert("projPoBillRecommendationPaymentChild", lastUpdateDate1);
            }
    }
    public void progressClick87() {
        if (progress87.getVisibility() == View.VISIBLE)
            if (progress87.getProgress() == -1) {
                msg87.setText("");
                progress87.setProgress(0);
            } else if (progress87.getProgress() == 0) {
                msg87.setText("Loading...");
                msg87.setTextColor(getResources().getColor(R.color.gray));
                progress87.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationPaymentMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoBillRecommendationPaymentMaster'}";
                Log.d(TAG, "projPoBillRecommendationPaymentMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn87, progress87, msg87, "projPoBillRecommendationPaymentMaster");
                dbInsert("projPoBillRecommendationPaymentMaster", lastUpdateDate1);
            }
    }
    public void progressClick88() {
        if (progress88.getVisibility() == View.VISIBLE)
            if (progress88.getProgress() == -1) {
                msg88.setText("");
                progress88.setProgress(0);
            } else if (progress88.getProgress() == 0) {
                msg88.setText("Loading...");
                msg88.setTextColor(getResources().getColor(R.color.gray));
                progress88.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationPoChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoBillRecommendationPoChild'}";
                Log.d(TAG, "projPoBillRecommendationPoChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn88, progress88, msg88, "projPoBillRecommendationPoChild");
                dbInsert("projPoBillRecommendationPoChild", lastUpdateDate1);
            }
    }
    public void progressClick89() {
        if (progress89.getVisibility() == View.VISIBLE)
            if (progress89.getProgress() == -1) {
                msg89.setText("");
                progress89.setProgress(0);
            } else if (progress89.getProgress() == 0) {
                msg89.setText("Loading...");
                msg89.setTextColor(getResources().getColor(R.color.gray));
                progress89.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillItemChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoBillItemChild'}";
                Log.d(TAG, "projPoBillItemChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn89, progress89, msg89, "projPoBillItemChild");
                dbInsert("projPoBillItemChild", lastUpdateDate1);
            }
    }
    public void progressClick90() {
        if (progress90.getVisibility() == View.VISIBLE)
            if (progress90.getProgress() == -1) {
                msg90.setText("");
                progress90.setProgress(0);
            } else if (progress90.getProgress() == 0) {
                msg90.setText("Loading...");
                msg90.setTextColor(getResources().getColor(R.color.gray));
                progress90.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillRecommendationPaymentDetChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoBillRecommendationPaymentDetChild'}";
                Log.d(TAG, "projPoBillRecommendationPaymentDetChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn90, progress90, msg90, "projPoBillRecommendationPaymentDetChild");
                dbInsert("projPoBillRecommendationPaymentDetChild", lastUpdateDate1);
            }
    }
    public void progressClick91() {
        if (progress91.getVisibility() == View.VISIBLE)
            if (progress91.getProgress() == -1) {
                msg91.setText("");
                progress91.setProgress(0);
            } else if (progress91.getProgress() == 0) {
                msg91.setText("Loading...");
                msg91.setTextColor(getResources().getColor(R.color.gray));
                progress91.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoActBillOthersChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoActBillOthersChild'}";
                Log.d(TAG, "projPoActBillOthersChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn91, progress91, msg91, "projPoActBillOthersChild");
                dbInsert("projPoActBillOthersChild", lastUpdateDate1);
            }
    }
    public void progressClick92() {
        if (progress92.getVisibility() == View.VISIBLE)
            if (progress92.getProgress() == -1) {
                msg92.setText("");
                progress92.setProgress(0);
            } else if (progress92.getProgress() == 0) {
                msg92.setText("Loading...");
                msg92.setTextColor(getResources().getColor(R.color.gray));
                progress92.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projPoBillOtherChargeChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projPoBillOtherChargeChild'}";
                Log.d(TAG, "projPoBillOtherChargeChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn92, progress92, msg92, "projPoBillOtherChargeChild");
                dbInsert("projPoBillOtherChargeChild", lastUpdateDate1);
            }
    }
    public void progressClick93() {
        if (progress93.getVisibility() == View.VISIBLE)
            if (progress93.getProgress() == -1) {
                msg93.setText("");
                progress93.setProgress(0);
            } else if (progress93.getProgress() == 0) {
                msg93.setText("Loading...");
                msg93.setTextColor(getResources().getColor(R.color.gray));
                progress93.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtanMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtanMaster'}";
                Log.d(TAG, "projMtanMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn93, progress93, msg93, "projMtanMaster");
                dbInsert("projMtanMaster", lastUpdateDate1);
            }
    }
    public void progressClick94() {
        if (progress94.getVisibility() == View.VISIBLE)
            if (progress94.getProgress() == -1) {
                msg94.setText("");
                progress94.setProgress(0);
            } else if (progress94.getProgress() == 0) {
                msg94.setText("Loading...");
                msg94.setTextColor(getResources().getColor(R.color.gray));
                progress94.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtanChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtanChild'}";
                Log.d(TAG, "projMtanChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn94, progress94, msg94, "projMtanChild");
                dbInsert("projMtanChild", lastUpdateDate1);
            }
    }
    public void progressClick95() {
        if (progress95.getVisibility() == View.VISIBLE)
            if (progress95.getProgress() == -1) {
                msg95.setText("");
                progress95.setProgress(0);
            } else if (progress95.getProgress() == 0) {
                msg95.setText("Loading...");
                msg95.setTextColor(getResources().getColor(R.color.gray));
                progress95.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMtanOtherChargeChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMtanOtherChargeChild'}";
                Log.d(TAG, "projMtanOtherChargeChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn95, progress95, msg95, "projMtanOtherChargeChild");
                dbInsert("projMtanOtherChargeChild", lastUpdateDate1);
            }
    }
    public void progressClick96() {
        if (progress96.getVisibility() == View.VISIBLE)
            if (progress96.getProgress() == -1) {
                msg96.setText("");
                progress96.setProgress(0);
            } else if (progress96.getProgress() == 0) {
                msg96.setText("Loading...");
                msg96.setTextColor(getResources().getColor(R.color.gray));
                progress96.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillIowChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projWoBillIowChild'}";
                Log.d(TAG, "projWoBillIowChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn96, progress96, msg96, "projWoBillIowChild");
                dbInsert("projWoBillIowChild", lastUpdateDate1);
            }
    }
    public void progressClick97() {
        if (progress97.getVisibility() == View.VISIBLE)
            if (progress97.getProgress() == -1) {
                msg97.setText("");
                progress97.setProgress(0);
            } else if (progress97.getProgress() == 0) {
                msg97.setText("Loading...");
                msg97.setTextColor(getResources().getColor(R.color.gray));
                progress97.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillSubActivityChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projWoBillSubActivityChild'}";
                Log.d(TAG, "projWoBillSubActivityChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn97, progress97, msg97, "projWoBillSubActivityChild");
                dbInsert("projWoBillSubActivityChild", lastUpdateDate1);
            }
    }
    public void progressClick98() {
        if (progress98.getVisibility() == View.VISIBLE)
            if (progress98.getProgress() == -1) {
                msg98.setText("");
                progress98.setProgress(0);
            } else if (progress98.getProgress() == 0) {
                msg98.setText("Loading...");
                msg98.setTextColor(getResources().getColor(R.color.gray));
                progress98.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillNmrChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projWoBillNmrChild'}";
                Log.d(TAG, "projWoBillNmrChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn98, progress98, msg98, "projWoBillNmrChild");
                dbInsert("projWoBillNmrChild", lastUpdateDate1);
            }
    }
    public void progressClick99() {
        if (progress99.getVisibility() == View.VISIBLE)
            if (progress99.getProgress() == -1) {
                msg99.setText("");
                progress99.setProgress(0);
            } else if (progress99.getProgress() == 0) {
                msg99.setText("Loading...");
                msg99.setTextColor(getResources().getColor(R.color.gray));
                progress99.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookQaMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMbookQaMaster'}";
                Log.d(TAG, "projMbookQaMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn99, progress99, msg99, "projMbookQaMaster");
                dbInsert("projMbookQaMaster", lastUpdateDate1);
            }
    }
    public void progressClick100() {
        if (progress100.getVisibility() == View.VISIBLE)
            if (progress100.getProgress() == -1) {
                msg100.setText("");
                progress100.setProgress(0);
            } else if (progress100.getProgress() == 0) {
                msg100.setText("Loading...");
                msg100.setTextColor(getResources().getColor(R.color.gray));
                progress100.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillIowDetChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projWoBillIowDetChild'}";
                Log.d(TAG, "projWoBillIowDetChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn100, progress100, msg100, "projWoBillIowDetChild");
                dbInsert("projWoBillIowDetChild", lastUpdateDate1);
            }
    }
    public void progressClick101() {
        if (progress101.getVisibility() == View.VISIBLE)
            if (progress101.getProgress() == -1) {
                msg101.setText("");
                progress101.setProgress(0);
            } else if (progress101.getProgress() == 0) {
                msg101.setText("Loading...");
                msg101.setTextColor(getResources().getColor(R.color.gray));
                progress101.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillIowTaxChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projWoBillIowTaxChild'}";
                Log.d(TAG, "projWoBillIowTaxChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn101, progress101, msg101, "projWoBillIowTaxChild");
                dbInsert("projWoBillIowTaxChild", lastUpdateDate1);
            }
    }
    public void progressClick102() {
        if (progress102.getVisibility() == View.VISIBLE)
            if (progress102.getProgress() == -1) {
                msg102.setText("");
                progress102.setProgress(0);
            } else if (progress102.getProgress() == 0) {
                msg102.setText("Loading...");
                msg102.setTextColor(getResources().getColor(R.color.gray));
                progress102.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillSubActivityDetChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projWoBillSubActivityDetChild'}";
                Log.d(TAG, "projWoBillSubActivityDetChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn102, progress102, msg102, "projWoBillSubActivityDetChild");
                dbInsert("projWoBillSubActivityDetChild", lastUpdateDate1);
            }
    }
    public void progressClick103() {
        if (progress103.getVisibility() == View.VISIBLE)
            if (progress103.getProgress() == -1) {
                msg103.setText("");
                progress103.setProgress(0);
            } else if (progress103.getProgress() == 0) {
                msg103.setText("Loading...");
                msg103.setTextColor(getResources().getColor(R.color.gray));
                progress103.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillNmrDetChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projWoBillNmrDetChild'}";
                Log.d(TAG, "projWoBillNmrDetChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn103, progress103, msg103, "projWoBillNmrDetChild");
                dbInsert("projWoBillNmrDetChild", lastUpdateDate1);
            }
    }
    public void progressClick104() {
        if (progress104.getVisibility() == View.VISIBLE)
            if (progress104.getProgress() == -1) {
                msg104.setText("");
                progress104.setProgress(0);
            } else if (progress104.getProgress() == 0) {
                msg104.setText("Loading...");
                msg104.setTextColor(getResources().getColor(R.color.gray));
                progress104.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("finAccountMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'finAccountMaster'}";
                Log.d(TAG, "finAccountMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn104, progress104, msg104, "finAccountMaster");
                dbInsert("finAccountMaster", lastUpdateDate1);
            }
    }
    public void progressClick105() {
        if (progress105.getVisibility() == View.VISIBLE)
            if (progress105.getProgress() == -1) {
                msg105.setText("");
                progress105.setProgress(0);
            } else if (progress105.getProgress() == 0) {
                msg105.setText("Loading...");
                msg105.setTextColor(getResources().getColor(R.color.gray));
                progress105.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillRecommendationPaymentMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projWoBillRecommendationPaymentMaster'}";
                Log.d(TAG, "projWoBillRecommendationPaymentMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn105, progress105, msg105, "projWoBillRecommendationPaymentMaster");
                dbInsert("projWoBillRecommendationPaymentMaster", lastUpdateDate1);
            }
    }
    public void progressClick106() {
        if (progress106.getVisibility() == View.VISIBLE)
            if (progress106.getProgress() == -1) {
                msg106.setText("");
                progress106.setProgress(0);
            } else if (progress106.getProgress() == 0) {
                msg106.setText("Loading...");
                msg106.setTextColor(getResources().getColor(R.color.gray));
                progress106.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projWoBillRecommendationPaymentChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projWoBillRecommendationPaymentChild'}";
                Log.d(TAG, "projWoBillRecommendationPaymentChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn106, progress106, msg106, "projWoBillRecommendationPaymentChild");
                dbInsert("projWoBillRecommendationPaymentChild", lastUpdateDate1);
            }
    }
    public void progressClick107() {
        if (progress107.getVisibility() == View.VISIBLE)
            if (progress107.getProgress() == -1) {
                msg107.setText("");
                progress107.setProgress(0);
            } else if (progress107.getProgress() == 0) {
                msg107.setText("Loading...");
                msg107.setTextColor(getResources().getColor(R.color.gray));
                progress107.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMirnMaster")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMirnMaster'}";
                Log.d(TAG, "projMirnMaster--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn107, progress107, msg107, "projMirnMaster");
                dbInsert("projMirnMaster", lastUpdateDate1);
            }
    }
    public void progressClick108() {
        if (progress108.getVisibility() == View.VISIBLE)
            if (progress108.getProgress() == -1) {
                msg108.setText("");
                progress108.setProgress(0);
            } else if (progress108.getProgress() == 0) {
                msg108.setText("Loading...");
                msg108.setTextColor(getResources().getColor(R.color.gray));
                progress108.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMirnChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMirnChild'}";
                Log.d(TAG, "projMirnChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn108, progress108, msg108, "projMirnChild");
                dbInsert("projMirnChild", lastUpdateDate1);
            }
    }
    public void progressClick109() {
        if (progress109.getVisibility() == View.VISIBLE)
            if (progress109.getProgress() == -1) {
                msg109.setText("");
                progress109.setProgress(0);
            } else if (progress109.getProgress() == 0) {
                msg109.setText("Loading...");
                msg109.setTextColor(getResources().getColor(R.color.gray));
                progress109.setProgress(1);
                String lastUpdateDate1 = "";
                List<UpdateOnTable> TableLists = updateOnTableDao.queryBuilder().where(UpdateOnTableDao.Properties.Table_name.eq("projMbookLabourChild")).list();
                if (TableLists.size() > 0) {
                    lastUpdateDate1 = TableLists.get(0).getLast_update();
                }
                String req1 = "{'Action':'MRALL_PROCESS','submode':'META_DATA','Cre_Id':'" + cr_id + "','UID':'" + uid + "','type':'force','lastUpdateDate':'" + lastUpdateDate1 + "','dataTable':'projMbookLabourChild'}";
                Log.d(TAG, "projMbookLabourChild--> " + ApiCalls.getURLfromJson(req1, context));
                myService.doServiceStuff(req1, updateOn109, progress109, msg109, "projMbookLabourChild");
                dbInsert("projMbookLabourChild", lastUpdateDate1);
            }
    }
    public void syncAllClick() {
        syncAll.setProgress(0);
        progressClick1();
        progressClick2();
        progressClick3();
        progressClick4();
        progressClick5();
        progressClick6();
        progressClick7();
        progressClick8();
        progressClick9();
        progressClick10();
        progressClick11();
        progressClick12();
        progressClick13();
        progressClick14();
        progressClick15();
        progressClick16();
        progressClick17();
        progressClick18();
        progressClick19();
        progressClick20();
        progressClick21();
        progressClick22();
        progressClick23();
        progressClick24();
        progressClick25();
        progressClick26();
        progressClick27();
        progressClick28();
        progressClick29();
        progressClick30();
        progressClick31();
        progressClick32();
        progressClick33();
        progressClick34();
        progressClick35();
        progressClick36();
        progressClick37();
        progressClick38();
        progressClick39();
        progressClick40();
        progressClick41();
        progressClick42();
        progressClick43();
        progressClick44();
        progressClick45();
        progressClick46();
        progressClick47();
        progressClick48();
        progressClick49();
        progressClick50();
        progressClick51();
        progressClick52();
       /* progressClick53();
        progressClick54();*/
        progressClick55();
        progressClick56();
        progressClick57();
        progressClick58();
        progressClick59();
        progressClick60();
        progressClick61();
        progressClick62();
        progressClick63();
        progressClick64();
        progressClick65();
        progressClick66();
        progressClick67();
        progressClick68();
        progressClick69();
        progressClick70();
        progressClick71();
        progressClick72();
        progressClick73();
        progressClick74();
        progressClick75();
        progressClick76();
        progressClick77();
        progressClick78();
        progressClick79();
        progressClick80();
        progressClick81();
        progressClick82();
        progressClick83();
        progressClick84();
        progressClick85();
        progressClick86();
        progressClick87();
        progressClick88();
        progressClick89();
        progressClick90();
        progressClick91();
        progressClick92();
        progressClick93();
        progressClick94();
        progressClick95();
        progressClick96();
        progressClick97();
        progressClick98();
        progressClick99();
        progressClick100();
        progressClick101();
        progressClick102();
        progressClick103();
        progressClick104();
        progressClick105();
        progressClick106();
        progressClick107();
        progressClick108();
        progressClick109();
    }
    @Override
    public void onDestroy() {
        Log.w(TAG, "------------------------------------------ Destroyed Location update Service");
        super.onDestroy();
        // startService(new Intent(this, DBService.class)); // add this line
    }
    @Override
    protected void onPause() {
        super.onPause();
        // stopService(serviceIntent);
        // unbindService(serviceConnection);
    }
    @Override
    public void onBackPressed() {
        Sharedpref.setPrefBoolean(context, uid+"_DB_UPDATE", true);
        finish();
        Intent in = new Intent(this, SCMDashboardActivityLatest.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
    }
}