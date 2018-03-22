package com.guruinfo.scm.DPR.Ticketing;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.guruinfo.scm.R;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.CircleImageView;
import com.guruinfo.scm.common.FilePicker;
import com.guruinfo.scm.common.ImageLoader;
import com.guruinfo.scm.common.RestClient;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.HttpRequest;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.ui.MyExpandableListView;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.ui.TextViewPlus;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.squareup.picasso.Picasso;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ERP1 on 8/25/2016.
 */
public class DPRBasedTicketDisplay extends BaseActivity implements OnTaskCompleted {
    ProgressDialog processDialog;
    Context context = this;
    SessionManager session;
    TextViewPlus textView5;
    SCMTextView textView1, textView2, textView4,
            textView7, stageName, iow, uom;
    TextView textView6, textView3;
    String uid, Cre_Id, reopenStatus, reqStatus, mBookId, stageId, iowId, processType, childId, projectId,
            parameter, param;
    BackgroundTask bt;
    EditText edit_ticket;
    ArrayList<HashMap<String, String>> ParentArayList = new ArrayList<>();
    ArrayList<ArrayList<HashMap<String, String>>> childItems;
    int parentColorIndex = 0;
    MyExpandableListView listview;
    ScrollView scrollView;
    ArrayList<HashMap<String, String>> loadlist;
    private static final int REQUEST_PICK_FILE1 = 1;
    private static final int REQUEST_PICK_FILE2 = 2;
    private static final int REQUEST_PICK_FILE3 = 3;
    private static final int CAMERA_CAPTURE1 = 4;
    private static final int SELECT_PICTURE1 = 5;
    private static final int CAMERA_CAPTURE2 = 6;
    private static final int SELECT_PICTURE2 = 7;
    private static final int CAMERA_CAPTURE3 = 8;
    private static final int SELECT_PICTURE3 = 9;
    //captured picture uri
    private Uri picUri;
    TextView title, filePath1, filePath2, filePath3;
    String updatedon, response, ticket_confiq, unit_id, ticket_id;
    private File selectedFile1, selectedFile2, selectedFile3;
    int pos;
    Typeface arialbold, arial, moire_bold, robot_bold, roboto_regular, moire__extrabold, moire_light, moire__regular;
    ProgressBar progressbar1;
    public static final String MIME_TYPE_PDF = "application/pdf";
    ArrayList<String> doclist;
    TextView merged, duplicate;
    MyExpandableListView listview2, listview3;
    String Gallery_path1, Gallery_path2, Gallery_path3, Camera_Capturepath1, Camera_Capturepath2, Camera_Capturepath3;
    int temppos;
    Dialog confirmationdialog, Pickerdialog;
    ImageView Ticketing_Image, Document;
    TextView confirmation_text, assigned_to_label, assigned_Person;
    Button ok_button, cancel_button;
    BackgroundTask backgroundTask;
    CircleImageView ticketing_userimage;
    int loader = R.drawable.empty_dp_large;
    ImageLoader imgLoader;
    Uri CAPTUREURI;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    String colorCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dpr_ticket_view);
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        robot_bold = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
        moire_bold = Typeface.createFromAsset(getAssets(), "MOIRE-BOLD.TTF");
        moire__extrabold = Typeface.createFromAsset(getAssets(), "MOIRE-EXTRABOLD.TTF");
        moire_light = Typeface.createFromAsset(getAssets(), "MOIRE-LIGHT.TTF");
        moire__regular = Typeface.createFromAsset(getAssets(), "MOIRE-REGULAR.TTF");
        arial = Typeface.createFromAsset(getAssets(), "arial.ttf");
        arialbold = Typeface.createFromAsset(getAssets(), "Arial Bold.ttf");
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
        }
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    123);
        }
        confirmationdialog = new Dialog(DPRBasedTicketDisplay.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.image_conform, null);
        confirmationdialog.setContentView(convertView);
        ok_button = (Button) confirmationdialog.findViewById(R.id.ok);
        cancel_button = (Button) confirmationdialog.findViewById(R.id.cancel);
        confirmation_text = (TextView) confirmationdialog.findViewById(R.id.confirmation_text);
        confirmationdialog.setTitle("Confirmation Alert !");
        imgLoader = new ImageLoader(this);
        Pickerdialog = new Dialog(DPRBasedTicketDisplay.this);
        LayoutInflater inflater1 = getLayoutInflater();
        View convertView1 = inflater1.inflate(R.layout.activity_ticketing_attachment, null);
        Pickerdialog.setContentView(convertView1);
        Document = (ImageView) Pickerdialog.findViewById(R.id.document_img);
        Pickerdialog.setTitle("Attachments");

        ticket_id = getIntent().getStringExtra("ticketId");
        reopenStatus = getIntent().getStringExtra("reopenStatus");//false
        reqStatus = getIntent().getStringExtra("reqStatus");//Open
        mBookId = getIntent().getStringExtra("mBookId");
        stageId = getIntent().getStringExtra("stageId");
        iowId = getIntent().getStringExtra("iowId");
        processType = getIntent().getStringExtra("processType");
        childId = getIntent().getStringExtra("childId");
        projectId = getIntent().getStringExtra("projectId");

        doclist = new ArrayList<String>();
        textView1 = (SCMTextView) findViewById(R.id.id_ticketing_content);
        textView2 = (SCMTextView) findViewById(R.id.view_ticket_title);
        textView3 = (TextView) findViewById(R.id.view_ticket_subtitle);
        textView4 = (SCMTextView) findViewById(R.id.ticketing_timedate);
        textView5 = (TextViewPlus) findViewById(R.id.view_Description);
        textView6 = (TextView) findViewById(R.id.view_assigned_username);
        textView7 = (SCMTextView) findViewById(R.id.ticketing_status);
        stageName = (SCMTextView) findViewById(R.id.view_ticket_stage);
        iow = (SCMTextView) findViewById(R.id.view_ticket_iow);
        uom = (SCMTextView) findViewById(R.id.view_ticket_uom);
        title = (TextView) findViewById(R.id.title);
        edit_ticket = (EditText) findViewById(R.id.update_disc);
        String ss3 = " Description *";
        Spannable wordtoSpan3 = new SpannableString(ss3);
        wordtoSpan3.setSpan(new ForegroundColorSpan(Color.parseColor("#7b7b7b")), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan3.setSpan(new ForegroundColorSpan(Color.parseColor("#d22528")), 12, ss3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        edit_ticket.setHint(wordtoSpan3);
        param = "{'Action':'GENERAL_TICKETING','submode':'TICKET_VIEW','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'Ticket_id':'" + ticket_id + "','type':'addDescription','control':'general','ProcessType':'" + processType + "','ticketId':'" + ticket_id + "'}";
        bt = new BackgroundTask(this, "view");
        bt.execute("", "", param);
        listview = (MyExpandableListView) findViewById(R.id.listview1);
        duplicate = (TextView) findViewById(R.id.duplicate);
        merged = (TextView) findViewById(R.id.merged);
        listview2 = (MyExpandableListView) findViewById(R.id.listview2);
        listview3 = (MyExpandableListView) findViewById(R.id.listview3);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.smoothScrollTo(0, 0);
        listview.setFocusable(false);
        listview2.setFocusable(false);
        listview3.setFocusable(false);
        Button click_btn1 = (Button) findViewById(R.id.browse1);
        assigned_to_label = (TextView) findViewById(R.id.assigned_to_label);
        assigned_Person = (TextView) findViewById(R.id.assigned_Person);
        assigned_Person.setVisibility(View.VISIBLE);
        assigned_to_label.setVisibility(View.GONE);
        filePath1 = (TextView) findViewById(R.id.selected1);
        Button click_btn2 = (Button) findViewById(R.id.browse2);
        filePath2 = (TextView) findViewById(R.id.selected2);
        Button click_btn3 = (Button) findViewById(R.id.browse3);
        filePath3 = (TextView) findViewById(R.id.selected3);
        click_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temppos = 1;
                Pickerdialog.show();
            }
        });
        click_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temppos = 2;
                Pickerdialog.show();
            }
        });
        click_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                temppos = 3;
                Pickerdialog.show();
            }
        });
        if (reqStatus.equalsIgnoreCase("Satisfied") || reqStatus.equalsIgnoreCase("Closed") || reqStatus.equalsIgnoreCase("duplicate") || reqStatus.equalsIgnoreCase("merge")) {
            findViewById(R.id.footer).setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            findViewById(R.id.doc_lay).setVisibility(View.GONE);
            findViewById(R.id.update_disc).setVisibility(View.GONE);
        } else if (reopenStatus.equalsIgnoreCase("true")) {
            findViewById(R.id.footer).setVisibility(View.GONE);
            title.setVisibility(View.GONE);
            findViewById(R.id.doc_lay).setVisibility(View.GONE);
            findViewById(R.id.update_disc).setVisibility(View.GONE);
        } else {
            findViewById(R.id.footer).setVisibility(View.VISIBLE);
        }

        colorCode = getIntent().getStringExtra("colorCode");
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ticket Edit");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorCode)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darker(Color.parseColor(colorCode), 0.9f));
        }
    }

    public void footerbutton1(View v) {
        this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // case REQUEST_PICK_FILE:
                // if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                // selectedFile = new
                // File(data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                // String posi =data.getStringExtra("position");
                // // System.out.println("pios"+posi);
                // View hiddenInfo =
                // getLayoutInflater().inflate(R.layout.add_bookfacility_item,
                // table_content, false);
                // View child = table_content.getChildAt(Integer.parseInt(posi));
                // TextView file_Path=(TextView) child.findViewById(R.id.selected);
                // file_Path.setText(selectedFile.getPath());
                // }
                case CAMERA_CAPTURE1:
                    //get the Uri for the captured image
                    //picUri = data.getData();
                    picUri = CAPTUREURI;
                    Camera_Capturepath1 = getPath(picUri);
                    selectedFile1 = new File(Camera_Capturepath1);
                    //	filePath1.setText(Camera_Capturepath1);
                    confirmationdialog.show();
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                            filePath1.setText(Camera_Capturepath1);
                            if (filePath1.getText().toString().contains("No file chosen")) {
                                setToast("Please Select one File");
                            } else {
                                pos = 0;
                                new DoInBackground().execute();
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                        }
                    });
                    break;
                case SELECT_PICTURE1:
                    Uri selectedImageUri = data.getData();
                    Gallery_path1 = getPathFromUri(context, selectedImageUri);
                    selectedFile1 = new File(Gallery_path1);
                    confirmationdialog.show();
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                            filePath1.setText(Gallery_path1);
                            if (filePath1.getText().toString().contains("No file chosen")) {
                                setToast("Please Select one File");
                            } else {
                                pos = 0;
                                new DoInBackground().execute();
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                        }
                    });
                    break;
                case CAMERA_CAPTURE2:
                    //get the Uri for the captured image
                    //picUri = data.getData();
                    picUri = CAPTUREURI;
                    Camera_Capturepath2 = getPath(picUri);
                    selectedFile2 = new File(Camera_Capturepath2);
                    //filePath2.setText(Camera_Capturepath2);
                    confirmationdialog.show();
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                            filePath2.setText(Camera_Capturepath2);
                            if (filePath2.getText().toString().contains("No file chosen")) {
                                setToast("Please Select one File");
                            } else {
                                pos = 1;
                                new DoInBackground().execute();
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                        }
                    });
                    break;
                case SELECT_PICTURE2:
                    Uri selectedImageUri2 = data.getData();
                    Gallery_path2 = getPathFromUri(context, selectedImageUri2);
                    selectedFile2 = new File(Gallery_path2);
                    //	filePath2.setText(Gallery_path2);
                    confirmationdialog.show();
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                            filePath2.setText(Gallery_path2);
                            if (filePath2.getText().toString().contains("No file chosen")) {
                                setToast("Please Select one File");
                            } else {
                                pos = 1;
                                new DoInBackground().execute();
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                        }
                    });
                    break;
                case CAMERA_CAPTURE3:
                    //get the Uri for the captured image
                    //picUri = data.getData();
                    picUri = CAPTUREURI;
                    Camera_Capturepath3 = getPath(picUri);
                    selectedFile3 = new File(Camera_Capturepath3);
                    //filePath3.setText(Camera_Capturepath3);
                    confirmationdialog.show();
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                            filePath3.setText(Camera_Capturepath3);
                            if (filePath3.getText().toString().contains("No file chosen")) {
                                setToast("Please Select one File");
                            } else {
                                pos = 2;
                                new DoInBackground().execute();
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                        }
                    });
                    break;
                case SELECT_PICTURE3:
                    Uri selectedImageUri3 = data.getData();
                    Gallery_path3 = getPathFromUri(context, selectedImageUri3);
                    selectedFile3 = new File(Gallery_path3);
                    //	filePath3.setText(Gallery_path3);
                    confirmationdialog.show();
                    ok_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                            filePath3.setText(Gallery_path3);
                            if (filePath3.getText().toString().contains("No file chosen")) {
                                setToast("Please Select one File");
                            } else {
                                pos = 2;
                                new DoInBackground().execute();
                            }
                        }
                    });
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmationdialog.cancel();
                        }
                    });
                    break;
                case REQUEST_PICK_FILE1:
                    if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                        selectedFile1 = new File(
                                data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        String posi = data.getStringExtra("position");
                        confirmationdialog.show();
                        ok_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationdialog.cancel();
                                filePath1.setText(selectedFile1.getPath());
                                if (filePath1.getText().toString().contains("No file chosen")) {
                                    setToast("Please Select one File");
                                } else {
                                    pos = 0;
                                    new DoInBackground().execute();
                                }
                            }
                        });
                        cancel_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationdialog.cancel();
                            }
                        });
                    }
                    break;
                case REQUEST_PICK_FILE2:
                    if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                        selectedFile2 = new File(
                                data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        String posi = data.getStringExtra("position");
                        //filePath2.setText(selectedFile2.getPath());
                        confirmationdialog.show();
                        ok_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationdialog.cancel();
                                filePath2.setText(selectedFile2.getPath());
                                if (filePath2.getText().toString().contains("No file chosen")) {
                                    setToast("Please Select one File");
                                } else {
                                    pos = 1;
                                    new DoInBackground().execute();
                                }
                            }
                        });
                        cancel_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationdialog.cancel();
                            }
                        });
                    }
                    break;
                case REQUEST_PICK_FILE3:
                    if (data.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                        selectedFile3 = new File(
                                data.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        String posi = data.getStringExtra("position");
                        confirmationdialog.show();
                        ok_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationdialog.cancel();
                                filePath3.setText(selectedFile3.getPath());
                                if (filePath2.getText().toString().contains("No file chosen")) {
                                    setToast("Please Select one File");
                                } else {
                                    pos = 3;
                                    new DoInBackground().execute();
                                }
                            }
                        });
                        cancel_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationdialog.cancel();
                            }
                        });
                    }
                    break;
            }
        }
    }

    public String getPathFromUri(final Context context, final Uri uri) {
        boolean isAfterKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isAfterKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if ("com.android.externalstorage.documents".equals(
                    uri.getAuthority())) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    return "/stroage/" + type + "/" + split[1];
                }
            } else if ("com.android.providers.downloads.documents".equals(
                    uri.getAuthority())) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if ("com.android.providers.media.documents".equals(
                    uri.getAuthority())) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                contentUri = MediaStore.Files.getContentUri("external");
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {//MediaStore
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String[] projection = {
                MediaStore.Files.FileColumns.DATA
        };
        try {
            cursor = context.getContentResolver().query(
                    uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int cindex = cursor.getColumnIndexOrThrow(projection[0]);
                return cursor.getString(cindex);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isValid(String str) {
        boolean isValid = false;
        String expression = "^[a-z_A-Z0-9._, ]*$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public void footerbutton2(View v) {
        try {
            String disc = edit_ticket.getText().toString();
            if (disc.equals("")) {
                setToast("Please Enter Your Description");
            } else if (disc.startsWith(" ")) {
                setToast("Please Enter Your Description without space.");
            } else if (!isValid(disc.toString().trim())) {
                Toast.makeText(getApplicationContext(), "Oops!! Your Description entered is Invalid.", Toast.LENGTH_SHORT).show();
            } else {
              //  disc = URLEncoder.encode(edit_ticket.getText().toString().trim(), "UTF-8");
                disc = edit_ticket.getText().toString().trim();
                String doc_ids = "";
                if (doclist.size() == 0) {
                    doc_ids = "";
                } else {
                    doc_ids = doclist.toString();
                    doc_ids = doc_ids.substring(1, doc_ids.length() - 1);
                    doc_ids = doc_ids.replace(" ", "").trim();
                }
                System.out.println("data2" + reopenStatus);
                if (reopenStatus.equals("true")) {
                    parameter = "{'Action':'GENERAL_TICKETING','submode':'TICKET_ADD_SUBMIT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','ticketconfig':'" + ticket_confiq + "','main_description':'" + disc + "','main_ticket_id':'" + ticket_id + "','doc_ids':'" + doc_ids + "','control':'general','type':'REOPEN'}";
                    bt = new BackgroundTask(this, "result");
                    bt.execute("", "", parameter);
                    System.out.println("parm" + parameter);
                } else {
                    parameter = "{'Action':'GENERAL_TICKETING','submode':'TICKET_ADD_SUBMIT','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "','ticketconfig':'" + ticket_confiq + "','main_description':'" + disc + "','main_ticket_id':'" + ticket_id + "','doc_ids':'" + doc_ids + "','control':'general','type':'ADD_DESC','ProcessType':'" + processType + "','mbook_id':'" + mBookId + "','stage_id':'" + stageId + "','iow_id':'" + iowId + "','mbook_child_id':'" + childId + "'}";
                    bt = new BackgroundTask(this, "result");
                    bt.execute("", "", parameter);
                    System.out.println("parm" + parameter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_menu, menu);
            return true;
        }*/
    public String jsonArrayToString(JSONArray jsonArray) {
        String Values = "";
        String Valuesid = "";
        for (int a = 0; a < jsonArray.length(); a++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(a);
                if (Values.equalsIgnoreCase(""))
                    Values = jsonObject.getString("doc_name");
//                    Valuesid = jsonObject.getString("");
                else
                    Values = Values + " , " + jsonObject.getString("doc_name");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return Values;
    }

    private void parseResultjson(String values) {
        try {
            final JSONObject jso_obj = new JSONObject(values);
            JSONObject obj1 = jso_obj.getJSONObject("Rights");
            JSONArray array = obj1.getJSONArray("Load_Request");
            loadlist = ApiCalls.getArraylistfromJson(array.toString());
            Sharedpref.writegson(this, loadlist, "loadrequest");
            if (jso_obj.getString("code").equalsIgnoreCase("1")) {
                //setToast(jso_obj.getString("msg"));
                Toast.makeText(DPRBasedTicketDisplay.this,jso_obj.getString("msg"),Toast.LENGTH_SHORT);
                /*Intent intent = new Intent(context,TicketingViewAllActivity.class);
                intent.putExtra("to", "");
                intent.putExtra("comm_id", comm_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);*/
                this.finish();
            } else if (jso_obj.getString("code").equalsIgnoreCase("403")) {
                showSessionScreensDialog(this, jso_obj.getString("msg"));
            } else {
                setToast(jso_obj.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseJson(String values) {
        try {
            final JSONObject jso_obj = new JSONObject(values);
            JSONObject obj2 = jso_obj.getJSONObject("Rights");
            JSONArray array = obj2.getJSONArray("Load_Request");
            loadlist = ApiCalls.getArraylistfromJson(array.toString());
            Sharedpref.writegson(this, loadlist, "loadrequest");
            if (jso_obj.getString("code").equalsIgnoreCase("1")) {
                JSONObject obj = jso_obj.getJSONObject("Ticket_Details");
                ticket_id = obj.getString("Ticket_Id");
                ticket_confiq = obj.getString("Ticket_Config");
                unit_id = obj.getString("unit_id");
                updatedon = obj.getString("Update_on");
                textView1.setText(obj.getString("Ticket_Id"));
                textView2.setText(obj.getString("Category"));
                textView3.setText(obj.getString("Sub_Category"));
                textView4.setText(obj.getString("Ticket_Date"));
                //   textView5.setText(obj.getString("Description"));
               /* if (obj.getString("Description").toString().contains("%20")) {
                    String Description = obj.getString("Description").replace("%20", " ");
                    textView5.setText(Description);
                } else {*/
                textView5.setText(obj.getString("Description"));
                // }
                stageName.setText(obj.getString("stageName"));
                iow.setText(obj.getString("iow"));
                uom.setText(obj.getString("uom"));
                textView6.setText(obj.getString("Assigned_To"));
                textView7.setText(obj.getString("Ticket_Status"));
                JSONArray Ticket_Conversation = jso_obj.getJSONArray("Ticket_Conversation");
                ArrayList<ArrayList<HashMap<String, String>>> conversationList = resArray(Ticket_Conversation);
                final ParentListAdapter parentListAdapter = new ParentListAdapter(context, ParentArayList, conversationList);
                listview.setAdapter(parentListAdapter);
                //ListUtils.setDynamicHeight(listview);
                JSONArray arr1 = jso_obj.getJSONArray("Ticket_Duplicate_Conversation");
                ArrayList<ArrayList<HashMap<String, String>>> dupConversationList = resArray(arr1);
                if (dupConversationList.size() > 0) {
                    listview2.setVisibility(View.VISIBLE);
                    duplicate.setVisibility(View.VISIBLE);
                }
                final ParentListAdapter dupListAdapter = new ParentListAdapter(context, ParentArayList, dupConversationList);
                listview2.setAdapter(dupListAdapter);
                //ListUtils.setDynamicHeight(listview2);
                JSONArray arr2 = jso_obj.getJSONArray("Ticket_Merged_Conversation");
                ArrayList<ArrayList<HashMap<String, String>>> mergeConversationList = resArray(arr2);
                if (mergeConversationList.size() > 0) {
                    listview3.setVisibility(View.VISIBLE);
                    merged.setVisibility(View.VISIBLE);
                }
                final ParentListAdapter mergeListAdapter = new ParentListAdapter(context, ParentArayList, mergeConversationList);
                listview3.setAdapter(mergeListAdapter);
                //ListUtils.setDynamicHeight(listview3);
            } else if (jso_obj.getString("code").equalsIgnoreCase("403")) {
                showSessionScreensDialog(this, jso_obj.getString("msg"));
            } else {
                setToast(jso_obj.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<HashMap<String, String>>> resArray(JSONArray Ticket_Conversation) {
        childItems = new ArrayList<ArrayList<HashMap<String, String>>>();
        ParentArayList = new ArrayList<>();
        try {
            for (int i = 0; i < Ticket_Conversation.length(); i++) {
                //   ArrayList<HashMap<String, String>> childarrayList
                ArrayList<HashMap<String, String>> childarrayList = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> hashMap = new HashMap<>();
                JSONObject innerobject = Ticket_Conversation.getJSONObject(i);
                String Assignedto = innerobject.getString("AssignedTo");
                System.out.println("Assignedto" + Assignedto);
                if (parentColorIndex > 11) {
                    parentColorIndex = 0;
                    hashMap.put("color", "" + colorArray()[parentColorIndex]);
                } else {
                    hashMap.put("color", "" + colorArray()[parentColorIndex]);
                    parentColorIndex = parentColorIndex + 1;
                }
                hashMap.put("Assignedto", innerobject.getString("AssignedTo"));
                hashMap.put("EmpPhoto", innerobject.getString("EmpPhoto"));
                //      JSONArray Conversation = innerobject.getJSONArray("Conversation");
                JSONArray Conversation = Ticket_Conversation.getJSONObject(i).getJSONArray("Conversation");
                for (int j = 0; j < Conversation.length(); j++) {
                    HashMap<String, String> child = new HashMap<>();
                    JSONObject Conversationobj = null;
                    Conversationobj = Conversation.getJSONObject(j);
                    child.put("Status", Conversationobj.getString("Status"));
                    child.put("Description", Conversationobj.getString("Description"));
                    child.put("isCustomer", Conversationobj.getString("isCustomer"));
                    child.put("Date", Conversationobj.getString("Date"));
                    child.put("Assigned_Person", Conversationobj.getString("Assigned_Person"));
                    child.put("Id", Conversationobj.getString("Id"));
                    child.put("doc_name", jsonArrayToString(Conversationobj.getJSONArray("Doc_Array")));
                    childarrayList.add(child);
                    hashMap.put("Date", Conversationobj.getString("Date"));
                }
                ParentArayList.add(hashMap);
                childItems.add(new ArrayList<HashMap<String, String>>(childarrayList));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return childItems;
    }

    public static class ListUtils {
        public static void setDynamicHeight(ExpandableListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            mListView.setLayoutParams(params);
            mListView.requestLayout();
        }
    }

    public class ParentListAdapter extends BaseExpandableListAdapter {
        private Context mContex;
        private LayoutInflater mInflater;
        private LayoutInflater mChInflater;
        ArrayList<HashMap<String, String>> arrayList;
        ArrayList<ArrayList<HashMap<String, String>>> childRowItems;

        public ParentListAdapter(Context context, ArrayList<HashMap<String, String>> arrayList, ArrayList<ArrayList<HashMap<String, String>>> childRowItems) {
            this.arrayList = arrayList;
            this.childRowItems = childRowItems;
            this.mContex = context;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return childRowItems.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return childRowItems.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
            View row = convertView;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.ticket_detail_heading, parent, false);
            row.setOnClickListener(null);
            row.setOnLongClickListener(null);
            row.setLongClickable(false);
            for (int i = 0; i < arrayList.size(); i++) {
                System.out.println("arraylist" + i + ":" + arrayList.get(i));
            }
            //row.setBackground(getResources().getDrawable(R.drawable.transparent_border));
            TextViewPlus textView = (TextViewPlus) row.findViewById(R.id.activity_conversation_username);
            TextViewPlus date = (TextViewPlus) row.findViewById(R.id.activity_conversation_date);
            ticketing_userimage = (CircleImageView) row.findViewById(R.id.ticketing_user_image);
            String userimage_id = arrayList.get(groupPosition).get("EmpPhoto");
            if (userimage_id.equals("")) {
                //imgLoader.DisplayImage(AppContants.imageurl+userimage_id, loader, ticketing_userimage);
                Picasso.with(context)
                        .load(loader)
                        .into(ticketing_userimage);
            } else if (userimage_id.equals("0")) {
                //imgLoader.DisplayImage(AppContants.imageurl + userimage_id, loader, ticketing_userimage);
                Picasso.with(context).load(loader).into(ticketing_userimage);
            } else {
                Picasso.with(context)
                        .load(AppContants.imageurl + userimage_id)
                        .into(ticketing_userimage);
            }
/*
            RelativeLayout background = (RelativeLayout) row.findViewById(R.id.title_layout);
            background.setBackground(getResources().getDrawable(Integer.parseInt(arrayList.get(groupPosition).get("color"))));*/
            textView.setText(arrayList.get(groupPosition).get("Assignedto"));
            date.setText(arrayList.get(groupPosition).get("Date"));
            System.out.println("Parentview" + groupPosition + ":" + arrayList.get(groupPosition).get("Assignedto"));
            listview.expandGroup(groupPosition);
            //listview.setBackground(getResources().getDrawable(R.drawable.transparent_border));
            return row;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        public Object getChild(int index, int stub) {
            return childRowItems.get(index);
        }

        @Override
        public long getChildId(int index, int stub) {
            return stub;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return childRowItems.get(groupPosition).size();
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View row = convertView;
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (childRowItems.get(groupPosition).get(childPosition).get("isCustomer").equalsIgnoreCase("1"))
                row = inflater.inflate(R.layout.leftside_bubble, parent, false);
            else
                row = inflater.inflate(R.layout.rightside_bubbles, parent, false);
            row.setOnClickListener(null);
            row.setOnLongClickListener(null);
            row.setLongClickable(false);
            //RelativeLayout mainlayout = (LinearLayout) row.findViewById(R.id.activity_converation1);
            TextViewPlus date = (TextViewPlus) row.findViewById(R.id.conversation_title_timedate);
            TextViewPlus status = (TextViewPlus) row.findViewById(R.id.activity_conversation_status);
            TextViewPlus des = (TextViewPlus) row.findViewById(R.id.conversation_description);
            TextViewPlus attach = (TextViewPlus) row.findViewById(R.id.conversation_attachmentdetail);
            ImageView ticketstatus = (ImageView) row.findViewById(R.id.ticket_status);
            date.setText(childRowItems.get(groupPosition).get(childPosition).get("Date"));
            status.setText(childRowItems.get(groupPosition).get(childPosition).get("Status"));
            //status.setVisibility(View.GONE);
            //des.setText(childRowItems.get(groupPosition).get(childPosition).get("Description"));
           /* if (childRowItems.get(groupPosition).get(childPosition).get("Description").toString().contains("%20")) {
                String Description = childRowItems.get(groupPosition).get(childPosition).get("Description").replace("%20", " ");
                des.setText(Description);
            } else {
            */
            des.setText(childRowItems.get(groupPosition).get(childPosition).get("Description"));
            // }
            if (childRowItems.get(groupPosition).get(childPosition).get("Status").equalsIgnoreCase("Open")) {
                ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketicon_open));
                status.setTextColor(Color.parseColor("#1d89e5"));
            } else if (childRowItems.get(groupPosition).get(childPosition).get("Status").equalsIgnoreCase("Closed")) {
                ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketicon_close));
                status.setTextColor(Color.parseColor("#f52f38"));
            } else if (childRowItems.get(groupPosition).get(childPosition).get("Status").equalsIgnoreCase("Merged")) {
                ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketicon_merge));
                status.setTextColor(Color.parseColor("#d3af07"));
            } else if (childRowItems.get(groupPosition).get(childPosition).get("Status").equalsIgnoreCase("Process")) {
                ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketicon_process));
                status.setTextColor(Color.parseColor("#cb530b"));
            } else if (childRowItems.get(groupPosition).get(childPosition).get("Status").equalsIgnoreCase("duplicate")) {
                ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketicon_duplicate));
                status.setTextColor(Color.parseColor("#8a45c0"));
            } else if (childRowItems.get(groupPosition).get(childPosition).get("Status").equalsIgnoreCase("Satisfied")) {
                ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketing_satisfied));
                status.setTextColor(Color.parseColor("#238505"));
            } else if (childRowItems.get(groupPosition).get(childPosition).get("Status").equalsIgnoreCase("Reopen")) {
                //ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketing_reopen));
                ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketicon_reopen));
                status.setTextColor(Color.parseColor("#d36207"));
            } else if (childRowItems.get(groupPosition).get(childPosition).get("Status").equalsIgnoreCase("resend_otp")) {
                //ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketing_reopen));
                status.setVisibility(View.VISIBLE);
                ticketstatus.setVisibility(View.GONE);
                status.setTextColor(Color.parseColor("#850583"));
                status.setText(childRowItems.get(groupPosition).get(childPosition).get("Status"));
            } else if (childRowItems.get(groupPosition).get(childPosition).get("Status").equalsIgnoreCase("process-completed")) {
                //ticketstatus.setImageDrawable(getResources().getDrawable(R.drawable.ic_ticketing_reopen));
                status.setTextColor(Color.parseColor("#30a045"));
                status.setVisibility(View.VISIBLE);
                ticketstatus.setVisibility(View.GONE);
                status.setText(childRowItems.get(groupPosition).get(childPosition).get("Status"));
            } else {
                ticketstatus.setVisibility(View.GONE);
            }
            /*if (childRowItems.get(groupPosition).get(childPosition).get("Description").toString().contains("%20")) {
                String Description = childRowItems.get(groupPosition).get(childPosition).get("Description").replace("%20", " ");
                des.setText(Description);
            } else {*/
            des.setText(childRowItems.get(groupPosition).get(childPosition).get("Description"));
            // }
            if (childRowItems.get(groupPosition).get(childPosition).get("doc_name").equalsIgnoreCase("")) {
                attach.setVisibility(View.GONE);
            } else {
                attach.setText("Documents: " + childRowItems.get(groupPosition).get(childPosition).get("doc_name"));
                //doc_name  conversation_attachmentdetail
            }
            return row;
        }
    }

    class ViewHolder {
        @Bind(R.id.activity_conversation_username)
        TextViewPlus username;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /*
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // toggle nav drawer on selecting action bar app icon/title
            switch (item.getItemId()) {
                case android.R.id.home:
                    onBackPressed();
                    return true;
                case R.id.home_menu:
                    Intent intent = new Intent(context, DashBoardActivity.class);
                    startActivity(intent);
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
    */
    public void Upload(View v) throws UnsupportedEncodingException {
        new DoInBackground().execute();
    }

    private class DoInBackground extends AsyncTask<Void, Void, Void> implements
            DialogInterface.OnCancelListener {
        protected void onPreExecute() {
            processDialog = new ProgressDialog(context);
            processDialog.setCancelable(true);
            processDialog.show();
            processDialog.setCanceledOnTouchOutside(false);
            processDialog.setContentView(R.layout.loader);
            processDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        protected Void doInBackground(Void... unused) {
            try {
                String timestamp = Long.toString(System.currentTimeMillis());
                RestClient client = new RestClient(HttpRequest.portalURL + "Mobile_Devices?Action=DOC_UPLOAD");
                MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                File upFile = null;
                if (pos == 0) {
                    upFile = new File(selectedFile1.getPath());
                } else if (pos == 1) {
                    upFile = new File(selectedFile2.getPath());
                } else if (pos == 2) {
                    upFile = new File(selectedFile3.getPath());
                }
                FileBody fBody = new FileBody(upFile);
                mpEntity.addPart("ImageController", fBody);
                client.AddHeader("Authorization", "");
                client.ExecuteMultipart(RestClient.RequestMethod.POSTMULTIPART, mpEntity);
                //client.Execute(RequestMethod.POST);
                response = client.getResponse();
                // String timestamp = Long.toString(System.currentTimeMillis());
                // RestClient client = new
                // RestClient(HttpRequest.portalURL+"?action=DP_UPLOAD&");
                // client.AddHeader("Authorization","");
                // client.ExecuteMultipart(RequestMethod.POSTMULTIPART,reqEntity);
                // client.Execute(RequestMethod.POST);
                // response = client.getResponse();
                System.out.println("response" + response);
            } catch (Exception e) {
                processDialog.dismiss();
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            processDialog.dismiss();
            try {
                JSONObject jsonObj = new JSONObject(response);
                if (jsonObj.getString("code").equalsIgnoreCase("1")) {
                    setToast(jsonObj.getString("msg"));
                    doclist.add(jsonObj.getString("docid"));
                } else if (jsonObj.getString("code").equalsIgnoreCase("0")) {
                    setToast(jsonObj.getString("msg"));
                } else if (jsonObj.getString("code").equalsIgnoreCase("403")) {
                    showSessionScreensDialog(context, jsonObj.getString("msg"));
                } else {
                    setToast(jsonObj.getString("msg"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onCancel(DialogInterface dialog) {
            cancel(true);
            dialog.dismiss();
        }
    }

    /*  private void showInternetDialog2(Context context, String values, String params, String s) {
          AlertDialog.Builder builder1 = new AlertDialog.Builder(Tic);
          builder1.setMessage(err);
          builder1.setCancelable(true);
          builder1.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                  dialog.cancel();
                  finish();
                  Intent intent = new Intent(Intent.ACTION_MAIN);
                  intent.addCategory(Intent.CATEGORY_HOME);
                  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                  startActivity(intent);
                  android.os.Process.killProcess(android.os.Process.myPid());
              }
          });
          builder1.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                  getLoadData(requestParameterValues, flag);
              }
          });
          try {
              AlertDialog alert11 = builder1.create();
              alert11.show();
          } catch (Exception e) {
              e.printStackTrace();
          }
      }*/
    public void Upload1(View v) throws UnsupportedEncodingException {
        if (filePath1.getText().toString().contains("Selected file")) {
            setToast("Please Select one File");
        } else {
            pos = 0;
            new DoInBackground().execute();
        }
    }

    public void Upload2(View v) throws UnsupportedEncodingException {
        if (filePath2.getText().toString().contains("Selected file")) {
            setToast("Please Select one File");
        } else {
            pos = 1;
            new DoInBackground().execute();
        }
    }

    public void Upload3(View v) throws UnsupportedEncodingException {
        if (filePath3.getText().toString().contains("Selected file")) {
            setToast("Please Select one File");
        } else {
            pos = 2;
            new DoInBackground().execute();
        }
    }

    @Override
    public void onTaskCompleted(String values, String flag) {
        // TODO Auto-generated method stub
        if (flag.equals("internet")) {
            showInternetDialog(context, values);
        } else if (flag.equalsIgnoreCase("result")) {
            parseResultjson(values);
        } else if (flag.equalsIgnoreCase("view")) {
            parseJson(values);
        }
    }

    public void onDocument(View v) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Pickerdialog.cancel();
            if (temppos == 1) {
                Intent intent = new Intent(context, FilePicker.class);
                intent.putExtra("position", String.valueOf(0));
                startActivityForResult(intent, REQUEST_PICK_FILE1);
                pos = 0;
            } else if (temppos == 2) {
                Intent intent = new Intent(context, FilePicker.class);
                intent.putExtra("position", String.valueOf(0));
                startActivityForResult(intent, REQUEST_PICK_FILE2);
                pos = 1;
            } else if (temppos == 3) {
                Intent intent = new Intent(context, FilePicker.class);
                intent.putExtra("position", String.valueOf(0));
                startActivityForResult(intent, REQUEST_PICK_FILE3);
                pos = 2;
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
        }
    }

    public void onCamera(View v) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Pickerdialog.cancel();
            if (temppos == 1) {
                //use standard intent to capture an image
                //we will handle the returned data in onActivityResult
                try {
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fileName = "Image-" + n + ".JPEG";
                    final String appDirectoryName = "AmarprakashSCM";
                    final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), appDirectoryName);
                    imageRoot.mkdirs();
                    imageRoot.mkdirs();
                    final File file = new File(imageRoot, fileName);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "AmarprakashEAP");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "AmarprakashEAP Ticketing Image");
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
                    values.put("_data", file.getAbsolutePath());
                    ContentResolver cr = getContentResolver();
                    CAPTUREURI = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(captureIntent, CAMERA_CAPTURE1);
                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(DPRBasedTicketDisplay.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                pos = 0;
            } else if (temppos == 2) {
                try {
                    //use standard intent to capture an image
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fileName = "Image-" + n + ".JPEG";
                    final String appDirectoryName = "AmarprakashSCM";
                    final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), appDirectoryName);
                    imageRoot.mkdirs();
                    imageRoot.mkdirs();
                    final File file = new File(imageRoot, fileName);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "AmarprakashEAP");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "AmarprakashEAP Ticketing Image");
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
                    values.put("_data", file.getAbsolutePath());
                    ContentResolver cr = getContentResolver();
                    CAPTUREURI = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    //we will handle the returned data in onActivityResult
                    startActivityForResult(captureIntent, CAMERA_CAPTURE2);
                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(DPRBasedTicketDisplay.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                pos = 1;
            } else if (temppos == 3) {
                try {
                    //use standard intent to capture an image
                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fileName = "Image-" + n + ".JPEG";
                    final String appDirectoryName = "AmarprakashSCM";
                    final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), appDirectoryName);
                    imageRoot.mkdirs();
                    imageRoot.mkdirs();
                    final File file = new File(imageRoot, fileName);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "AmarprakashEAP");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "AmarprakashEAP Ticketing Image");
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
                    values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
                    values.put("_data", file.getAbsolutePath());
                    ContentResolver cr = getContentResolver();
                    CAPTUREURI = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    //we will handle the returned data in onActivityResult
                    startActivityForResult(captureIntent, CAMERA_CAPTURE3);
                } catch (ActivityNotFoundException anfe) {
                    //display an error message
                    String errorMessage = "Whoops - your device doesn't support capturing images!";
                    Toast toast = Toast.makeText(DPRBasedTicketDisplay.this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
                pos = 2;
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    123);
        }
    }

    public void onGallery(View v) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Pickerdialog.cancel();
            if (temppos == 1) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE1);
                pos = 0;
            } else if (temppos == 2) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE2);
                pos = 1;
            } else if (temppos == 3) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE3);
                pos = 2;
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    123);
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public void showInternetDialog(Context activity, String err_msg) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(err_msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Exit",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process
                                .myPid());
                    }
                });
        builder1.setNegativeButton("Retry",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        bt = new BackgroundTask(context, "view");
                        bt.execute("", "", param);
                    }
                });
        try {
            final AlertDialog alert11 = builder1.create();
            alert11.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                    btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                    //btnPositive.setTextSize(40);
                    Button btnNegative = alert11.getButton(Dialog.BUTTON_NEGATIVE);
                    btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                    //btnNegative.setTextSize(40);
                    Button btnNeutral = alert11.getButton(Dialog.BUTTON_NEUTRAL);
                    btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_PX, getContext().getResources().getDimension(R.dimen.Filter_textsize));
                    TextView textView = (TextView) alert11.findViewById(android.R.id.message);
                    if (context.getResources().getBoolean(R.bool.isTablet)) {
                        textView.setTextSize(25);
                    } else {
                        textView.setTextSize(16);
                    }
                }
            });
            alert11.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}