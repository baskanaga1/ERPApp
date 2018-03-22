package com.guruinfo.scm.DPR.Ticketing;

import android.Manifest;
import android.app.ActionBar;
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
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.guruinfo.scm.R;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.BaseActivity;
import com.guruinfo.scm.common.FilePicker;
import com.guruinfo.scm.common.RestClient;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.ui.SCMTextView;
import com.guruinfo.scm.common.ui.TextViewPlus;
import com.guruinfo.scm.common.utils.ApiCalls;
import com.guruinfo.scm.common.utils.Sharedpref;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.guruinfo.scm.common.AppContants.CLASS_INTENT;

/**
 * Created by Kannan G on 8/25/2016.
 */
public class DPRBasedTickedAdd extends BaseActivity implements
        OnTaskCompleted {
    private Uri picUri;
    ProgressDialog processDialog;
    Context context = this;
    String response;
    SessionManager session;
    String ticket_id, parameter, uid, Cre_Id;
    BackgroundTask bt;
    ArrayList<HashMap<String, String>> loadlist;
    EditText desc;
    TableLayout table_content;
    TextView remove_row_btn, add_row_btn, stageName, iow, uom;
    ;
    private static final int REQUEST_PICK_FILE1 = 1;
    private static final int REQUEST_PICK_FILE2 = 2;
    private static final int REQUEST_PICK_FILE3 = 3;
    private static final int CAMERA_CAPTURE1 = 4;
    private static final int SELECT_PICTURE1 = 5;
    private static final int CAMERA_CAPTURE2 = 6;
    private static final int SELECT_PICTURE2 = 7;
    private static final int CAMERA_CAPTURE3 = 8;
    private static final int SELECT_PICTURE3 = 9;
    private File selectedFile1, selectedFile2, selectedFile3;
    String Gallery_path1, Gallery_path2, Gallery_path3, Camera_Capturepath1, Camera_Capturepath2, Camera_Capturepath3;
    TextView filePath;
    int i = 0;
    Typeface robot_bold, roboto_regular;
    String subcate_name, unit, Config_id, reopenStatus, reqStatus, mBookId, stageId, iowId, processType, childId, projectId, uomValue;
    TextView filePath1, filePath2, filePath3;
    ArrayList<String> doclist;
    int pos;
    Dialog confirmationdialog, Pickerdialog;
    TextView confirmation_text;
    Button ok_button, cancel_button;
    int temppos;
    Uri CAPTUREURI;
    TextViewPlus category, subcategory;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    String colorCode;
    LinearLayout dispLay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dpr_ticket_add);
        robot_bold = Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf");
        roboto_regular = Typeface.createFromAsset(getAssets(),
                "Roboto-Regular.ttf");
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        dispLay=(LinearLayout)findViewById(R.id.disp_lay);
        dispLay.setVisibility(View.GONE);
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
        ticket_id = getIntent().getStringExtra("ticketId");
        reopenStatus = getIntent().getStringExtra("reopenStatus");//false
        reqStatus = getIntent().getStringExtra("reqStatus");//Open
        mBookId = getIntent().getStringExtra("mBookId");
        stageId = getIntent().getStringExtra("stageId");
        iowId = getIntent().getStringExtra("iowId");
        processType = getIntent().getStringExtra("processType");
        childId = getIntent().getStringExtra("childId");
        projectId = getIntent().getStringExtra("projectId");
        uomValue = getIntent().getStringExtra("uom");

        System.out.println("ticket_id---" + ticket_id);
        category = (TextViewPlus) findViewById(R.id.category);
        subcategory = (TextViewPlus) findViewById(R.id.subcategory);
        doclist = new ArrayList<String>();
        parameter = "{'Action':'GENERAL_TICKETING','submode':'TICKET_VIEW','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'Ticket_id':'" + ticket_id + "','type':'internal','ProcessType':'" + processType + "','IowId':'" + iowId + "','stageId':'" + stageId + "','MbookId':'" + mBookId + "','childid':'" + childId + "','proj_id':'" + projectId + "','uom':'" + uomValue + "','check':'true'}";
        bt = new BackgroundTask(this);
        bt.execute("", "", parameter);
        System.out.println("parma" + parameter);
        desc = (EditText) findViewById(R.id.desc);
        add_row_btn = (TextView) findViewById(R.id.add_row_btn);
        remove_row_btn = (TextView) findViewById(R.id.remove_row_btn);
        table_content = (TableLayout) findViewById(R.id.table_content);
        Button click_btn1 = (Button) findViewById(R.id.browse1);
        filePath1 = (TextView) findViewById(R.id.selected1);
        Button click_btn2 = (Button) findViewById(R.id.browse2);
        filePath2 = (TextView) findViewById(R.id.selected2);
        Button click_btn3 = (Button) findViewById(R.id.browse3);
        filePath3 = (TextView) findViewById(R.id.selected3);
        stageName = (SCMTextView) findViewById(R.id.view_ticket_stage);
        iow = (SCMTextView) findViewById(R.id.view_ticket_iow);
        uom = (SCMTextView) findViewById(R.id.view_ticket_uom);
        String ss2 = " Description *";
        Spannable wordtoSpan2 = new SpannableString(ss2);
        wordtoSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#7b7b7b")), 0, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        wordtoSpan2.setSpan(new ForegroundColorSpan(Color.parseColor("#d22528")), 12, ss2.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        desc.setHint(wordtoSpan2);
        confirmationdialog = new Dialog(DPRBasedTickedAdd.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.image_conform, null);
        confirmationdialog.setContentView(convertView);
        ok_button = (Button) confirmationdialog.findViewById(R.id.ok);
        cancel_button = (Button) confirmationdialog.findViewById(R.id.cancel);
        confirmation_text = (TextView) confirmationdialog.findViewById(R.id.confirmation_text);
        confirmationdialog.setTitle("Confirmation Alert !");
        Pickerdialog = new Dialog(DPRBasedTickedAdd.this);
        LayoutInflater inflater1 = getLayoutInflater();
        View convertView1 = inflater1.inflate(R.layout.activity_ticketing_attachment, null);
        Pickerdialog.setContentView(convertView1);
        Pickerdialog.setTitle("Attachments");
        click_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
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
        add_row_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                addRow(i, "");
                i++;
            }
        });
        remove_row_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                View hiddenInfo = getLayoutInflater().inflate(R.layout.add_poll_item, table_content, false);
                int childCount = table_content.getChildCount();
                for (int i = 1; i <= childCount; i++) {
                    if (i == childCount) {
                        table_content.removeViewAt(i - 1);
                    }
                }
            }
        });
        colorCode = getIntent().getStringExtra("colorCode");
        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.setVisibility(View.VISIBLE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Ticket Add");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(colorCode)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(darker(Color.parseColor(colorCode), 0.9f));
        }
    }

    private void addRow(final int position, String id) {
        View hiddenInfo = getLayoutInflater().inflate(
                R.layout.add_bookfacility_item, table_content, false);
        Button click_btn = (Button) hiddenInfo.findViewById(R.id.browse);
        filePath = (TextView) hiddenInfo.findViewById(R.id.selected);
        click_btn.setTag(position);
        // click_btn.setOnClickListener(new OnClickListener() {
        // @Override
        // public void onClick(View v) {
        // int tag = (Integer) v.getTag();
        // // System.out.println("sdf"+tag);
        // // TODO Auto-generated method stub
        // Intent intent = new Intent(context, FilePicker.class);
        // intent.putExtra("position",String.valueOf(position));
        // startActivityForResult(intent, REQUEST_PICK_FILE);
        // }
        // });
        table_content.addView(hiddenInfo);
    }

    public void Upload1(View v) throws UnsupportedEncodingException {
        if (filePath1.getText().toString().contains("No file chosen")) {
            setToast("Please Select one File");
        } else {
            pos = 0;
            new DoInBackground().execute();
        }
    }

    public void Upload2(View v) throws UnsupportedEncodingException {
        if (filePath2.getText().toString().contains("No file chosen")) {
            setToast("Please Select one File");
        } else {
            pos = 1;
            new DoInBackground().execute();
        }
    }

    public void Upload3(View v) throws UnsupportedEncodingException {
        if (filePath3.getText().toString().contains("No file chosen")) {
            setToast("Please Select one File");
        } else {
            pos = 2;
            new DoInBackground().execute();
        }
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
                RestClient client = new RestClient(com.guruinfo.scm.common.service.HttpRequest.portalURL + "Mobile_Devices?Action=DOC_UPLOAD");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
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
                            System.out.println("camera" + selectedFile1.toString());
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
                    //Gallery_path1 = getPath(selectedImageUri);
                    Gallery_path1 = getPathFromUri(context, selectedImageUri);
                    selectedFile1 = new File(Gallery_path1);
                    System.out.println("Filepath" + Gallery_path1);
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

    public void footerbutton1(View v) {

        this.finish();
    }

    /*    @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_menu, menu);
            return true;
        }
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
        }*/
    @Override
    public void onTaskCompleted(String values, String flag) {
        // TODO Auto-generated method stub
        if (flag.equals("internet")) {
            showInternetDialog(context, values);
        } else if (flag.equalsIgnoreCase("result")) {
            parseResultjson(values);
        } else {
            parsejson(values);
        }
    }

    private void parsejson(String values) {
        try {
            final JSONObject jso_obj = new JSONObject(values);
            JSONObject obj1 = jso_obj.getJSONObject("Rights");
            JSONArray array = obj1.getJSONArray("Load_Request");
            loadlist = ApiCalls.getArraylistfromJson(array.toString());
            Sharedpref.writegson(this, loadlist, "loadrequest");
            if (jso_obj.getString("code").equalsIgnoreCase("1")) {
                JSONObject resObject = jso_obj.getJSONObject("Ticket_Details");
                Config_id = resObject.getString("Ticket_Config");
                if(!(Config_id.equalsIgnoreCase("0"))) {
                    dispLay.setVisibility(View.VISIBLE);
                    category.setText(resObject.getString("Category"));
                    subcategory.setText(resObject.getString("Sub_Category"));
                    stageName.setText(resObject.getString("stageName"));
                    iow.setText(resObject.getString("iow"));
                    uom.setText(resObject.getString("uom"));
                    ticket_id = resObject.getString("Ticket_Id");
                }else {
                    ticketConfigDialog();
                }
            } else if (jso_obj.getString("code").equalsIgnoreCase("403")) {
                showSessionScreensDialog(this, jso_obj.getString("msg"));
            } else {
                setToast(jso_obj.getString("msg"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void ticketConfigDialog(){
        final Dialog dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(R.layout.dpr_ticket_config); // your custom view.
        dialog.setCancelable(false);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        Button cancel_btn = (Button) dialog.findViewById(R.id.cancel_btn);
        Button submit_btn = (Button) dialog.findViewById(R.id.submit_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parameter = "{'Action':'GENERAL_TICKETING','submode':'TICKET_VIEW','Cre_Id':'" + Cre_Id + "', 'UID':'" + uid + "', 'Ticket_id':'" + ticket_id + "','type':'internal','ProcessType':'" + processType + "','IowId':'" + iowId + "','stageId':'" + stageId + "','MbookId':'" + mBookId + "','childid':'" + childId + "','proj_id':'" + projectId + "','uom':'" + uomValue + "','check':'true','configCreate':'true'}";
                bt = new BackgroundTask(context);
                bt.execute("", "", parameter);
                dialog.dismiss();
            }
        });
        dialog.show();
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
            String disc = URLEncoder.encode(desc.getText().toString().trim(), "UTF-8");
            if (disc.equals("")) {
                setToast("Please Enter Your  Description.");
            } else if (disc.startsWith(" ")) {
                setToast("Please Enter Your Description without space.");
            } else {
                String doc_ids = "";
                if (doclist.size() == 0) {
                    doc_ids = "";
                } else {
                    doc_ids = doclist.toString();
                    doc_ids = doc_ids.substring(1, doc_ids.length() - 1);
                    doc_ids = doc_ids.replace(" ", "").trim();
                }

                parameter = "{'Action':'GENERAL_TICKETING','submode':'TICKET_ADD_SUBMIT','date':'','Cre_Id':'" + Cre_Id + "','ticketconfig':'" + ticket_id + "','Config_id':'" + Config_id + "', 'main_description':'" + disc + "', 'UID':'" + uid + "', 'doc_ids':'" + doc_ids + "',  'control':'general',  'main_ticket_id':'0','ProcessType':'" + processType + "','mbook_id':'" + mBookId + "','stage_id':'" + stageId + "','iow_id':'" + iowId + "','mbook_child_id':'" + childId + "'}";
                bt = new BackgroundTask(this, "result");
                bt.execute("", "", parameter);
                System.out.println("parm" + parameter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseResultjson(String values) {
        try {
            final JSONObject jso_obj = new JSONObject(values);
            JSONObject obj1 = jso_obj.getJSONObject("Rights");
            JSONArray array = obj1.getJSONArray("Load_Request");
            loadlist = ApiCalls.getArraylistfromJson(array.toString());
            Sharedpref.writegson(this, loadlist, "loadrequest");
            if (jso_obj.getString("code").equalsIgnoreCase("1")) {
               // setToast(jso_obj.getString("msg"));
                Toast.makeText(DPRBasedTickedAdd.this,jso_obj.getString("msg"),Toast.LENGTH_SHORT);
                if (CLASS_INTENT != null) {
                    Intent intent = CLASS_INTENT;
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
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
                        bt = new BackgroundTask(context);
                        bt.execute("", "", parameter);
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

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
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
                android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Pickerdialog.cancel();
            if (temppos == 1) {
                //use standard intent to capture an image
                //we will handle the returned data in onActivityResult
                try {
                    //Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir =
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);
                    String fileName = "Image-" + n + ".JPEG";
                    final String appDirectoryName = "AmarprakashEAP";
                    final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), appDirectoryName);
                    imageRoot.mkdirs();
                    imageRoot.mkdirs();
                    final File file = new File(imageRoot, fileName);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "AmarprakashEAP");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "EAP Ticketing Image");
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
                    Toast toast = Toast.makeText(DPRBasedTickedAdd.this, errorMessage, Toast.LENGTH_SHORT);
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
                    final String appDirectoryName = "AmarprakashEAP";
                    final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), appDirectoryName);
                    imageRoot.mkdirs();
                    imageRoot.mkdirs();
                    final File file = new File(imageRoot, fileName);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "AmarprakashEAP");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "EAP Ticketing Image");
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
                    Toast toast = Toast.makeText(DPRBasedTickedAdd.this, errorMessage, Toast.LENGTH_SHORT);
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
                    final String appDirectoryName = "AmarprakashEAP";
                    final File imageRoot = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), appDirectoryName);
                    imageRoot.mkdirs();
                    imageRoot.mkdirs();
                    final File file = new File(imageRoot, fileName);
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "AmarprakashEAP");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "EAP Ticketing Image");
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
                    Toast toast = Toast.makeText(DPRBasedTickedAdd.this, errorMessage, Toast.LENGTH_SHORT);
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


    public void onHome(View v) {
        Intent in = new Intent(this, SCMDashboardActivityLatest.class);
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(in);
        finish();
    }
}
