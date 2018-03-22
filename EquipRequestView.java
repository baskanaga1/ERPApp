package com.guruinfo.scm.Equipment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guruinfo.scm.R;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.service.RestClientHelper;
import com.guruinfo.scm.common.ui.SCMTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;


public class EquipRequestView extends BaseFragment {

    private static final String TAG = "EquipmentView";
    Context context;
    SessionManager session;
    static String uid, Cre_Id;
    BackgroundTask backgroundTask;
    String requestParameter;
    String listRequestParameter;
    String viewerType = "";
    boolean isEdit = false;

    String listActionName = "MOBILE_EQUIPMENT_PROCESS";
    String Submode;
    public static String stageId = "";
    String projectId="";
    String Equip_Req_Id="";
    String EquipId="";



    ArrayList<HashMap<String, String>> itemValuearraylist;
    TextView equip_req_cancel,equip_close;
    SCMTextView equip_slot_type, equip_req_by, equip_req_type, equip_project, equip_names, equip_id, equip_reqDate,
            equip_date, equip_date_time, equip_duration, equip_priority, is_sub_contract, equip_sub_contract,
            equip_sub_contractwo, equip_reminder, equip_notifyme_email, equip_notifyme_sms, equip_notifyme_app, equip_status, equip_descrip;

    LinearLayout datetime_lay, date_lay, cancel_lay, div_lay, close_lay, remarks_lay, subconwo_lay, subcon_lay, save_lay;
    EditText equip_remarks;

    // TODO: Rename and change types and number of parameters
    public static EquipRequestView newInstance(Bundle bundle) {
        EquipRequestView fragment = new EquipRequestView();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub

        inflater.inflate(R.menu.list_action, menu);

        if(isEdit)
            menu.findItem(R.id.edit).setVisible(true);
        else
            menu.findItem(R.id.edit).setVisible(false);

        menu.findItem(R.id.new_request).setVisible(false);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.favourite).setVisible(false);



        super.onCreateOptionsMenu(menu, inflater);

    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.edit:
                Bundle bundle = new Bundle();
                bundle.putString("HEADER", "Equipment Request Creation");
                bundle.putString("SLOT_TYPE", "Edit");
                bundle.putString("EQUIPREGID", Equip_Req_Id);
                bundle.putString("EQUIPID", EquipId);
                NavigationFragmentManager(EquipRequestCreation.newInstance(bundle), "EQUIP");
                return true;
            default:

                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        session = new SessionManager(context);
        uid = session.getUserDetails().get(SessionManager.ID);
        Cre_Id = session.getUserDetails().get(SessionManager.CR_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.equip_reg_view, container, false);

        final Bundle bundle = this.getArguments();

        if (bundle != null) {
            viewerType = bundle.getString("viewerType");
            Equip_Req_Id = bundle.getString("EQUIPREQID");
        }


        equip_close = (TextView)view.findViewById(R.id.equip_close);
        equip_req_cancel = (TextView)view.findViewById(R.id.equip_req_cancel);

        equip_slot_type = (SCMTextView)view.findViewById(R.id.equip_slot_type);
        equip_req_by = (SCMTextView)view.findViewById(R.id.equip_req_by);
        equip_req_type = (SCMTextView)view.findViewById(R.id.equip_req_type);
        equip_project = (SCMTextView)view.findViewById(R.id.equip_project);
        equip_reqDate = (SCMTextView)view.findViewById(R.id.equip_reqDate);
        equip_names = (SCMTextView)view.findViewById(R.id.equip_names);
        equip_id = (SCMTextView)view.findViewById(R.id.equip_id);
        equip_date = (SCMTextView)view.findViewById(R.id.equip_date);
        equip_date_time = (SCMTextView)view.findViewById(R.id.equip_date_time);
        equip_duration = (SCMTextView)view.findViewById(R.id.equip_duration);
        equip_priority = (SCMTextView)view.findViewById(R.id.equip_priority);
        is_sub_contract = (SCMTextView)view.findViewById(R.id.is_sub_contract);
        equip_sub_contract = (SCMTextView)view.findViewById(R.id.equip_sub_contract);
        equip_sub_contractwo = (SCMTextView)view.findViewById(R.id.equip_sub_contractwo);
        equip_reminder = (SCMTextView)view.findViewById(R.id.equip_reminder);
        equip_notifyme_email = (SCMTextView)view.findViewById(R.id.equip_notifyme_email);
        equip_notifyme_sms = (SCMTextView)view.findViewById(R.id.equip_notifyme_sms);
        equip_notifyme_app = (SCMTextView)view.findViewById(R.id.equip_notifyme_app);
        equip_status = (SCMTextView)view.findViewById(R.id.equip_status);
        equip_descrip = (SCMTextView)view.findViewById(R.id.equip_descrip);

        equip_remarks = (EditText)view.findViewById(R.id.equip_remarks);
        datetime_lay = (LinearLayout)view.findViewById(R.id.datetime_lay);
        date_lay = (LinearLayout)view.findViewById(R.id.date_lay);
        cancel_lay = (LinearLayout)view.findViewById(R.id.cancel_lay);
        div_lay = (LinearLayout)view.findViewById(R.id.div_lay);
        close_lay = (LinearLayout)view.findViewById(R.id.close_lay);
        remarks_lay = (LinearLayout)view.findViewById(R.id.remarks_lay);
        subcon_lay = (LinearLayout)view.findViewById(R.id.subcon_lay);
        subconwo_lay = (LinearLayout)view.findViewById(R.id.subconwo_lay);
        save_lay = (LinearLayout)view.findViewById(R.id.save_lay);


        load(listActionName, "EQUIPMENT_VIEW", "", "");
        //equip_notifyme_email.setText("(\u2713)Email");

        cancel_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remarks_lay.setVisibility(View.VISIBLE);
                save_lay.setVisibility(View.VISIBLE);
                cancel_lay.setVisibility(View.GONE);



            }
        });
        save_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'EQUIPMENT_CANCEL_PROCESS','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','equip_appointment_id':'"+Equip_Req_Id+"','status':'cancel','Remarks':'"+equip_remarks.getText().toString()+"'}";
                onListLoad(requestParameter, "EQUIPMENT_CANCEL_PROCESS");
            }
        });

        close_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>EQUIPMENT</font>"));
                NavigationFragmentManager(EquipListFragment.newInstance(null), "EQUIP");
            }
        });

        return view;
    }
    public void onListLoad(String req, final String flag) {
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
                showInternetDialog1(context, error, requestParameter, flag);
            }
        });
    }
    public void showInternetDialog1(Context activity, String err_msg, final String requestParameterValues, final String flag) {
        final Dialog dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(R.layout.offline_mode_alert); // your custom view.
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        TextView text = (TextView) dialog.findViewById(R.id.alert_msg);
        LinearLayout offLineLayout = (LinearLayout) dialog.findViewById(R.id.offline_lay);
        text.setText(err_msg);
        offLineLayout.setVisibility(View.GONE);
        Button offLineButton = (Button) dialog.findViewById(R.id.offline_btn);
        Button retryButton = (Button) dialog.findViewById(R.id.retry_btn);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                onListLoad(requestParameterValues, flag);
            }
        });

        dialog.show();

    }
    public void load(final String action, final String submode, String pjtId, String stgeId) {
        this.Submode = submode;
        this.projectId = pjtId;
        this.stageId = stgeId;
        requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'"+submode+"','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','unique_id':'"+Equip_Req_Id+"','viewer_type':'"+viewerType+"'}";
        Log.d(TAG, requestParameter);
        backgroundTask = new BackgroundTask(this.getActivity(), Submode, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(String values, String flagMsg) {
                try {
                    if (flagMsg.equals("internet")) {
                        showInternetDialog(context, values, action, submode, projectId, stageId);
                    } else {
                        JSONObject jsonObject = new JSONObject(values);
                        parseJSONResponse(jsonObject, submode);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        backgroundTask.execute("", "", requestParameter);
    }

    private void parseJSONResponse(JSONObject response, String flag) {
        try {
            if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_1)) {
                if (flag.equals("EQUIPMENT_VIEW")) {
                    if(response.getString("isEdit").equalsIgnoreCase("true")){

                        isEdit=true;
                        setHasOptionsMenu(true);

                    }
                    if(response.getString("cancelRequest").equalsIgnoreCase("true")){


                        cancel_lay.setVisibility(View.VISIBLE);
                        div_lay.setVisibility(View.VISIBLE);
                    }
                    if(response.getString("notifyMeObject").equalsIgnoreCase("true")) {
                        JSONObject notifyObj = response.getJSONObject("notifyMe");
                        if (notifyObj.getString("Email").equalsIgnoreCase("Email")) {
                            equip_notifyme_email.setText("(\u2713)" + notifyObj.getString("Email"));
                        }else
                            equip_notifyme_email.setText("(-)Email");
                        if (notifyObj.getString("SMS").equalsIgnoreCase("SMS")) {
                            equip_notifyme_sms.setText("(\u2713)" + notifyObj.getString("SMS"));
                        }else
                            equip_notifyme_sms.setText("(-)SMS");
                        if (notifyObj.getString("App").equalsIgnoreCase("App")) {
                            equip_notifyme_app.setText("(\u2713)" + notifyObj.getString("App"));
                        }else
                            equip_notifyme_app.setText("(-)App");
                    }else{
                        equip_notifyme_email.setText("-");
                    }
                    JSONObject genInfo = response.getJSONObject("generalInformation");
                    equip_slot_type.setText(genInfo.getString("equipmentSlotType"));
                    equip_req_by.setText(genInfo.getString("requestedBy"));
                    equip_req_type.setText(genInfo.getString("requestType"));
                    equip_project.setText(genInfo.getString("project"));
                    equip_names.setText(genInfo.getString("equipmentSlotName"));
                    equip_id.setText(genInfo.getString("equipmentId"));
                    EquipId = genInfo.getString("equipmentId");
                    equip_reqDate.setText(genInfo.getString("requestDate"));

                    if(genInfo.getString("HeaderDate").equalsIgnoreCase("Date")) {
                        equip_date.setText(genInfo.getString("Date"));
                        datetime_lay.setVisibility(View.GONE);
                    }else {
                        equip_date_time.setText(genInfo.getString("Date & Time"));
                        date_lay.setVisibility(View.GONE);
                    }

                    if(genInfo.getString("IsSubContractor").equalsIgnoreCase("true")) {
                        subcon_lay.setVisibility(View.VISIBLE);
                        subconwo_lay.setVisibility(View.VISIBLE);
                        equip_sub_contract.setText(genInfo.getString("subContractor"));
                        equip_sub_contractwo.setText(genInfo.getString("IsSubContractorWo"));
                        is_sub_contract.setText("(\u2713)Is Sub-Contract");
                    }

                    equip_duration.setText(genInfo.getString("duration"));
                    equip_priority.setText(genInfo.getString("priority"));
                    equip_reminder.setText(genInfo.getString("remainder"));
                    equip_status.setText(genInfo.getString("status"));
                    equip_descrip.setText(genInfo.getString("descripions"));
                    equip_remarks.setText(genInfo.getString("remarks"));



                }else if(flag.equals("EQUIPMENT_CANCEL_PROCESS")) {
                    if(response.getString(AppContants.RESPONSE_MESSAGE).equalsIgnoreCase("Equipment Request 'Cancel' Successfully")){
                        Bundle bundle = new Bundle();
                        bundle.putString("viewerType", viewerType);
                        bundle.putString("EQUIPREQID", Equip_Req_Id);
                        NavigationFragmentManager(EquipRequestView.newInstance(bundle), "EQUIP");
                    }
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
                }

            } else if (response.getString(AppContants.RESPONSE_CODE_KEY).equalsIgnoreCase(AppContants.RESPONSE_CODE_VALUE_403)) {
                showSessionScreensDialog(context, response.getString(AppContants.RESPONSE_MESSAGE));
            } else {
                if (flag.equals("otpresponse")) {
                    // setmsgToast(dialog.getWindow().getDecorView(), response.getString("msg"));
                } else
                    setToast(response.getString(AppContants.RESPONSE_MESSAGE));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showInternetDialog(Context activity, String err_msg, final String actionName, final String submodeName, final String pjtids, final String stId) {
        final Dialog dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(R.layout.offline_mode_alert); // your custom view.
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        TextView text = (TextView) dialog.findViewById(R.id.alert_msg);
        LinearLayout offLineLayout = (LinearLayout) dialog.findViewById(R.id.offline_lay);
        text.setText(err_msg);
        offLineLayout.setVisibility(View.GONE);
        Button offLineButton = (Button) dialog.findViewById(R.id.offline_btn);
        Button retryButton = (Button) dialog.findViewById(R.id.retry_btn);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                load(actionName, submodeName, pjtids, stId);
            }
        });

        dialog.show();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
   /*     if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
   */ }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

}
