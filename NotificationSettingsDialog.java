package com.guruinfo.scm;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TableRow;
import android.widget.TextView;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.utils.Sharedpref;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.URISyntaxException;
import static com.guruinfo.scm.common.BaseFragment.setToast;
import static com.guruinfo.scm.common.BaseFragment.showSessionScreensDialog;
/**
 * Created by ERP on 3/10/2017.
 */
public class NotificationSettingsDialog extends AppCompatActivity{
    String TAG = "NotiSettings";
    Context context;
    String uid, Cre_Id;
    String requestParameter;
    public static EditText soundPicker;
    SessionManager session;
    private Uri mCurrentSelectedUri;
    private static final int SELECT_SOUND = 100;
    TableRow notification_sound;
    LinearLayout notificationList;
    ProgressBar pb;
    LinearLayout dataDisplay;
    TextView submit;
    String selectedAudioURI="",selectedAudioName="";
    RadioGroup notification_radiogroup;
    RadioButton notification_on,notification_off;
    Drawable drawable;
    ColorFilter filter;
    ImageButton close;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_settings);
        getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wmlp = getWindow().getAttributes();
        wmlp.gravity = Gravity.CENTER | Gravity.CENTER;
        context = this;
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);
        soundPicker = (EditText) findViewById(R.id.sound_picker);
        notification_radiogroup = (RadioGroup)findViewById(R.id.notification_radiogroup);
        notification_on = (RadioButton) findViewById(R.id.notification_on);
        notification_off = (RadioButton) findViewById(R.id.notification_off);
        notification_sound = (TableRow) findViewById(R.id.notification_sound);
        dataDisplay = (LinearLayout) findViewById(R.id.datas);
        submit = (TextView) findViewById(R.id.submit_btn);
        close = (ImageButton) findViewById(R.id.close_dialog_button);
        pb = (ProgressBar) findViewById(R.id.pb);
        notificationList = (LinearLayout) findViewById(R.id.allNotification);
        drawable =getResources().getDrawable(R.drawable.ic_drawable_audio);
        if (Sharedpref.GetPrefString(this, "Notification_sound_Name") != null) {
            soundPicker.setText(Sharedpref.GetPrefString(this, "Notification_sound_Name"));
        }
        requestParameter = "{'Action':'NOTIFICATION_ALERT','submode':'NOTIFICATION_LIST','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "'}";
        onLoad(requestParameter, "LOAD");
        pb.setVisibility(View.VISIBLE);
        //  dataDisplay.setVisibility(View.GONE);
       /* soundPicker.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                *//*Intent intent = new Intent();
                intent.setType("audio");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                Intent intent = new Intent();
                intent.setType("audio*//*//**//*");*//*
                                               //intent.setAction(Intent.ACTION_GET_CONTENT);
                                               //startActivityForResult(Intent.createChooser(intent, "Select Audio"), SELECT_SOUND);
               *//* Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_SOUND);*//*
                                               traversTopToNextActivity(context, NotificationToneSelect.class);
                                           }
                                       }
        );*/
        if ((Sharedpref.GetPrefString(getApplicationContext(), "Notification_settings").equalsIgnoreCase(""))||(Sharedpref.GetPrefString(getApplicationContext(), "Notification_settings").equalsIgnoreCase("1"))) {
            // notification_on.setChecked(true);
            filter = new LightingColorFilter( getResources().getColor(R.color.black), getResources().getColor(R.color.black));
            drawable.setColorFilter(filter);
            soundPicker.setBackgroundResource(R.drawable.borderpatch);
            soundPicker.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            soundPicker.setText(Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound_Name"));
            selectedAudioName=Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound_Name");
            selectedAudioURI=""+Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound");
            for (int i = 0; i < notification_sound.getChildCount(); i++) {
                View view = notification_sound.getChildAt(i);
                view.setEnabled(true); // Or whatever you want to do with the view.
            }
            for (int i = 0; i < notificationList.getChildCount(); i++) {
                View view = notificationList.getChildAt(i);
                view.setEnabled(true); // Or whatever you want to do with the view.
            }
        }else
        {
            notification_off.setChecked(true);
            filter = new LightingColorFilter( getResources().getColor(R.color.notification_off_color), getResources().getColor(R.color.notification_off_color));
            drawable.setColorFilter(filter);
            //soundPicker.setBackgroundDrawable(context.getResources(),getDrawable(R.drawable.table_border));
            soundPicker.setBackgroundResource(R.drawable.border_transparent);
            soundPicker.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            soundPicker.setText(Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound_Name"));
            selectedAudioName=Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound_Name");
            selectedAudioURI=Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound");
            if(!(Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound").equalsIgnoreCase(""))){
                mCurrentSelectedUri=Uri.parse(Sharedpref.GetPrefString(getApplicationContext(), "Notification_sound"));
            }
            for (int i = 0; i < notification_sound.getChildCount(); i++) {
                View view = notification_sound.getChildAt(i);
                view.setEnabled(false); // Or whatever you want to do with the view.
            }
            for (int i = 0; i < notificationList.getChildCount(); i++) {
                Switch view = (Switch) notificationList.getChildAt(i);
                view.setEnabled(false); // Or whatever you want to do with the view.
                view.setChecked(false);
            }
        }
        notification_radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId)
                {
                    case R.id.notification_on:
                        filter = new LightingColorFilter( getResources().getColor(R.color.black), getResources().getColor(R.color.black));
                        drawable.setColorFilter(filter);
                        soundPicker.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                        soundPicker.setBackgroundResource(R.drawable.borderpatch);
                        for (int i = 0; i < notification_sound.getChildCount(); i++) {
                            View view = notification_sound.getChildAt(i);
                            view.setEnabled(true); // Or whatever you want to do with the view.
                        }
                        for (int i = 0; i < notificationList.getChildCount(); i++) {
                            View view = notificationList.getChildAt(i);
                            view.setEnabled(true); // Or whatever you want to do with the view.
                        }
                        break;
                    case R.id.notification_off:
                        filter = new LightingColorFilter( getResources().getColor(R.color.notification_off_color), getResources().getColor(R.color.notification_off_color));
                        drawable.setColorFilter(filter);
                        soundPicker.setBackgroundResource(R.drawable.border_transparent);
                        soundPicker.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                        for (int i = 0; i < notification_sound.getChildCount(); i++) {
                            View view = notification_sound.getChildAt(i);
                            view.setEnabled(false); // Or whatever you want to do with the view.
                        }
                        for (int i = 0; i < notificationList.getChildCount(); i++) {
                            Switch view = (Switch) notificationList.getChildAt(i);
                            view.setEnabled(false); // Or whatever you want to do with the view.
                            view.setChecked(false);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // finish();
                onSubmit();
                // ringtoneTv.setText("Name : " + ringtoneName + "\nUri : " + ringtoneUri);
            }
        });
        soundPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Application needs read storage permission for Builder.TYPE_MUSIC .
                if (ActivityCompat.checkSelfPermission(NotificationSettingsDialog.this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    RingtonePickerDialog.Builder ringtonePickerBuilder = new RingtonePickerDialog
                            .Builder(NotificationSettingsDialog.this, getSupportFragmentManager());
                    //Set title of the dialog.
                    //If set null, no title will be displayed.
                    ringtonePickerBuilder.setTitle("EAP Notification Tone");
                    //Add the desirable ringtone types.
                    // if (musicCb.isChecked())
                    ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_MUSIC);
                    //if (notificationCb.isChecked())
                    ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
                    //if (ringtoneCb.isChecked())
                    ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_RINGTONE);
                    // if (alarmCb.isChecked())
                    ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_ALARM);
                    //set the text to display of the positive (ok) button.
                    //If not set OK will be the default text.
                    ringtonePickerBuilder.setPositiveButtonText("SET TONE");
                    //set text to display as negative button.
                    //If set null, negative button will not be displayed.
                    ringtonePickerBuilder.setCancelButtonText("CANCEL");
                    //Set flag true if you want to play the com.ringtonepicker.sample of the clicked tone.
                    ringtonePickerBuilder.setPlaySampleWhileSelection(true);
                    //Set the callback listener.
                    ringtonePickerBuilder.setListener(new RingtonePickerListener() {
                        @Override
                        public void OnRingtoneSelected(String ringtoneName, Uri ringtoneUri) {
                            mCurrentSelectedUri = ringtoneUri;
                            soundPicker.setText(ringtoneName);
                            selectedAudioName = ringtoneName;
                            selectedAudioURI = "" + ringtoneUri;
                        }
                    });
                    //set the currently selected uri, to mark that ringtone as checked by default.
                    //If no ringtone is currently selected, pass null.
                    ringtonePickerBuilder.setCurrentRingtoneUri(mCurrentSelectedUri);
                    //Display the dialog.
                    ringtonePickerBuilder.show();
                } else {
                    ActivityCompat.requestPermissions(NotificationSettingsDialog.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            123);
                }
            }
        });
    }
    public void onLoad(String req, final String flag) {
        requestParameter = req;
        Log.d(TAG, requestParameter);
        RestClientHelper.getInstance().getURL(requestParameter, context, new RestClientHelper.RestClientListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    System.out.println(TAG + " --> " + response);
                    JSONObject jsonObject = new JSONObject(response);
                    parseJSONResponse(jsonObject, flag);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(String error) {
                showInternetDialog(context, error, requestParameter, flag);
            }
        });
    }
    public void showInternetDialog(Context activity, String err_msg, final String requestParameterValues, final String flag) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(err_msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        builder1.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                onLoad(requestParameterValues, flag);
            }
        });
        try {
            final AlertDialog alert11 = builder1.create();
            alert11.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button btnPositive = alert11.getButton(Dialog.BUTTON_POSITIVE);
                    btnPositive.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                    //btnPositive.setTextSize(40);
                    Button btnNegative = alert11.getButton(Dialog.BUTTON_NEGATIVE);
                    btnNegative.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                    //btnNegative.setTextSize(40);
                    Button btnNeutral = alert11.getButton(Dialog.BUTTON_NEUTRAL);
                    btnNeutral.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
                    TextView textView = (TextView) alert11.findViewById(android.R.id.message);
                    if(context.getResources().getBoolean(R.bool.isTablet)) {
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
    private void parseJSONResponse(JSONObject response, String flag) {
        try {
            if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                if (flag.equals("LOAD")) {
                    notificationDisplay(response);
                } else if (flag.equalsIgnoreCase("SUBMIT")) {
                    if (notification_on.isChecked()) {
                        Sharedpref.SetPrefString(getApplicationContext(), "Notification_settings", "1");
                    } else if (notification_off.isChecked()) {
                        Sharedpref.SetPrefString(getApplicationContext(), "Notification_settings", "0");
                    }
                    Sharedpref.SetPrefString(getApplicationContext(), "Notification_sound_Name", selectedAudioName);
                    Sharedpref.SetPrefString(getApplicationContext(), "Notification_sound", "" + selectedAudioURI);
                    finish();
                } else {
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                }
            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            } else {
                setToast(response.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void notificationDisplay(JSONObject object) {
        try {
            pb.setVisibility(View.GONE);
            dataDisplay.setVisibility(View.VISIBLE);
            JSONArray jsonArray = object.getJSONArray("NotificationValues");
            Boolean isEnable = false;
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getJSONObject(i).getString("Status").equalsIgnoreCase("true")) {
                    isEnable = true;
                }
            }
            if (isEnable) {
                notification_on.setChecked(true);
                for (int i = 0; i < jsonArray.length(); i++) {
                    // Switch view = (Switch) notificationList.getChildAt(i);
                    if (jsonArray.getJSONObject(i).getString("Status").equalsIgnoreCase("true")) {
                       /* view.setChecked(true);
                        view.setTag(jsonArray.getJSONObject(i).getString("key"));*/
                        notificationList.addView(addSwitch(jsonArray.getJSONObject(i).getString("DisplayName"), jsonArray.getJSONObject(i).getString("key"), true, true));
                    } else {
                       /* view.setChecked(false);
                        view.setTag(jsonArray.getJSONObject(i).getString("key"));*/
                        notificationList.addView(addSwitch(jsonArray.getJSONObject(i).getString("DisplayName"), jsonArray.getJSONObject(i).getString("key"), false, true));
                    }
                }
            } else {
                notification_on.setChecked(false);
                for (int i = 0; i < notification_sound.getChildCount(); i++) {
                    View view = notification_sound.getChildAt(i);
                    view.setEnabled(false); // Or whatever you want to do with the view.
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                   /* Switch view = (Switch) notificationList.getChildAt(i);
                    view.setEnabled(false); // Or whatever you want to do with the view.
                    view.setChecked(false);
                    view.setTag(jsonArray.getJSONObject(i).getString("key"));*/
                    notificationList.addView(addSwitch(jsonArray.getJSONObject(i).getString("DisplayName"), jsonArray.getJSONObject(i).getString("key"), false, false));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Switch addSwitch(String name, String key, Boolean check, Boolean editable) {
        Switch rowSwitch = new Switch(this);
        rowSwitch.setText(name);
        rowSwitch.setChecked(check);
        rowSwitch.setEnabled(editable);
        rowSwitch.setTag(key);
        rowSwitch.setPadding(0, 15, 0, 15);
        rowSwitch.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return rowSwitch;
    }
    public void onSubmit() {
        int MultipleSelect = 0;
        int MultipleUnSelect = 0;
        Boolean isMultipleSelect = false;
        Boolean isMultipleUnSelect = false;
        String curType1 = "";
        String curType2 = "";
        String type = "";
        String value = "";
        if (!(notification_on.isChecked())) {
            type = "AllONAndOffConrol";
            value = "0";
        } else {
            type = "addONAndOffConrol";
            for (int i = 0; i < notificationList.getChildCount(); i++) {
                Switch view = (Switch) notificationList.getChildAt(i);
                if (view.isChecked()) {
                    MultipleSelect++;
                    if (curType1.equalsIgnoreCase("")) {
                        curType1 = (view.getTag().toString().replace(" ", "%20"));
                    } else {
                        curType1 = curType1 + "," + view.getTag().toString();
                    }
                } else {
                    MultipleUnSelect++;
                    if (curType2.equalsIgnoreCase("")) {
                        curType2 = view.getTag().toString();
                    } else {
                        curType2 = curType2 + "," + (view.getTag().toString().replace(" ", "%20"));
                    }
                }
            }
        }
        if (MultipleSelect > 1)
            isMultipleSelect = true;
        if (MultipleUnSelect > 1)
            isMultipleUnSelect = true;
        if ((notification_on.isChecked()) && MultipleSelect == 0) {
            type = "AllONAndOffConrol";
            value = "0";
        }
        if (notification_on.isChecked() && MultipleUnSelect == 0) {
            type = "AllONAndOffConrol";
            value = "1";
            curType1 = "";
            curType2 = "";
        }
        if (MultipleSelect > 0 && MultipleUnSelect > 0) {
            type = "addONAndOffConrol";
            value = "";
        }
        requestParameter = "{'Action':'NOTIFICATION_ALERT','submode':'NOTIFICATION_SETTINGS','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "'" +
                ",'isMultipleOn':'" + isMultipleSelect + "','isMultipleOff':'" + isMultipleUnSelect + "','curType1':'" + curType1 + "'" +
                ",'curType2':'" + curType2 + "','type':'" + type + "','value':'" + value + "'}";
        onLoad(requestParameter, "SUBMIT");
    }
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = context.getContentResolver().query(contentUri, null, null, null, null);
        int idx;
        if (contentUri.getPath().startsWith("/external/image") || contentUri.getPath().startsWith("/internal/image")) {
            idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        } else if (contentUri.getPath().startsWith("/external/video") || contentUri.getPath().startsWith("/internal/video")) {
            idx = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA);
        } else if (contentUri.getPath().startsWith("/external/audio") || contentUri.getPath().startsWith("/internal/audio")) {
            idx = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA);
        } else {
            return contentUri.getPath();
        }
        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(idx);
        }
        return null;
    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
    public String getPath(Uri uri) {
        /*String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();*/
        String[] filePathColumn = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnindex = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        String file_path = cursor.getString(columnindex);
        Log.d(getClass().getName(), "file_path" + file_path);
        Uri fileUri = Uri.parse("file://" + file_path);
        cursor.close();
        return fileUri.toString();
    }
    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }
    public static String getFileNameByUri(Context context, Uri uri) {
        String fileName = "unknown";//default fileName
        Uri filePathUri = uri;
        if (uri.getScheme().toString().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);//Instead of "MediaStore.Images.Media.DATA" can be used "_data"
                filePathUri = Uri.parse(cursor.getString(column_index));
                fileName = filePathUri.getLastPathSegment().toString();
            }
        } else if (uri.getScheme().compareTo("file") == 0) {
            fileName = filePathUri.getLastPathSegment().toString();
        } else {
            fileName = fileName + "_" + filePathUri.getLastPathSegment();
        }
        return fileName;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SELECT_SOUND:
                    /*//Uri selectedImageUri = data.getData();
                    //String sound_path = getPath(selectedImageUri);
                    String path = getRealPathFromURI(context, data.getData());
                    soundPicker.setText(path);*/
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path
                    String path = null;
                    try {
                        path = getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Sharedpref.SetPrefString(this, "Notification_sound", path);
                    break;
            }
        }
    }
}