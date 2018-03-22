package com.guruinfo.scm.Equipment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.guruinfo.scm.R;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.AppContants;
import com.guruinfo.scm.common.BaseFragment;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import com.guruinfo.scm.common.ui.SCMTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;


public class Equip_History extends BaseFragment {

    TextView equip_close;
    ParentListAdapter adapter;
    ListView equip_history_list;
    LinearLayout close_lay;
    String listActionName = "MOBILE_EQUIPMENT_PROCESS";
    private static final String TAG = "EquipmentHistory";
    Context context;
    SessionManager session;
    static String uid, Cre_Id;
    BackgroundTask backgroundTask;
    String requestParameter;
    String Submode;
    public static String stageId = "";
    String projectId="";
    String Equip_Req_Id="";
    ArrayList<HashMap<String, String>> itemValuearraylist = new ArrayList<>();
    static EditText requestReturnable;
    @Bind(R.id.error_msg)
    SCMTextView emptyMsgTextView;

    // TODO: Rename and change types and number of parameters
    public static Equip_History newInstance(Bundle bundle) {
        Equip_History fragment = new Equip_History();
        fragment.setArguments(bundle);
        return fragment;
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
        View view = inflater.inflate(R.layout.equip_history_list, container, false);
        itemValuearraylist = new ArrayList<>();
        final Bundle bundle = this.getArguments();
        ButterKnife.bind(this, view);

        if (bundle != null) {

            Equip_Req_Id = bundle.getString("EQUIPREQID");
        }

        requestReturnable = (EditText) view.findViewById(R.id.newRequestReturnable);

        requestReturnable.addTextChangedListener(new
                                                         TextWatcher() {
                                                             @Override
                                                             public void beforeTextChanged(CharSequence s, int start, int count,
                                                                                           int after) {
                                                                 // TODO Auto-generated method stub
                                                             }

                                                             @Override
                                                             public void onTextChanged(CharSequence s, int start, int before,
                                                                                       int count) {
                                                                 // TODO Auto-generated method stub
                                                             }

                                                             @Override
                                                             public void afterTextChanged(Editable s) {
                                                                 //if (request != null)
                                                                 //getPOList(request, flagName);
                                                             }
                                                         }
        );
        equip_history_list = (ListView)view.findViewById(R.id.equip_history_list);

        equip_close = (TextView) view.findViewById(R.id.equip_close);
        close_lay = (LinearLayout)view.findViewById(R.id.close_lay);

        close_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                ((SCMDashboardActivityLatest) getActivity()).getSupportActionBar().setTitle(Html.fromHtml("<font color='#2d2e3a'>EQUIPMENT</font>"));
                NavigationFragmentManager(EquipListFragment.newInstance(null), "Equip");
            }
        });


        load(listActionName, "EQUIPMENT_HISTORY", "", "");

        return view;
    }

    public void load(final String action, final String submode, String pjtId, String stgeId) {
        this.Submode = submode;
        this.projectId = pjtId;
        this.stageId = stgeId;
        requestParameter = "{'Action':'MOBILE_EQUIPMENT_PROCESS','submode':'"+submode+"','Cre_Id':'" + Cre_Id + "','UID':'" + uid + "','unique_id':'"+Equip_Req_Id+"'}";
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

                try {
                    JSONArray EQUIPHisJSONArray = response.getJSONArray("History");
                    if (EQUIPHisJSONArray.length() > 0) {
                        emptyMsgTextView.setVisibility(View.GONE);
                        for (int i = 0; i < EQUIPHisJSONArray.length(); i++) {
                            HashMap<String, String> hashMap = new HashMap<>();

                            hashMap.put("User", EQUIPHisJSONArray.getJSONObject(i).getString("User"));
                            hashMap.put("Comments", EQUIPHisJSONArray.getJSONObject(i).getString("Comments"));
                            hashMap.put("DateandTime", EQUIPHisJSONArray.getJSONObject(i).getString("Date"));
                            hashMap.put("PreProcessStatus", EQUIPHisJSONArray.getJSONObject(i).getString("Pre Process Status"));
                            hashMap.put("CurrentProcessStatus", EQUIPHisJSONArray.getJSONObject(i).getString("Current Process Status"));
                            hashMap.put("value", EQUIPHisJSONArray.getJSONObject(i).getString("value"));
                            itemValuearraylist.add(hashMap);

                        }

                    }else {
                        emptyMsgTextView.setVisibility(View.VISIBLE);
                    }
                    adapter = new ParentListAdapter(context, R.layout.equip_history);
                    equip_history_list.setAdapter(adapter);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
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

    public class ParentListAdapter extends ArrayAdapter<String> {

        public ParentListAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public int getCount() {

             return itemValuearraylist.size();


        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Holder holder;
            View v = convertView;

            if (v == null) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = mInflater.inflate(R.layout.equip_history, null);
                holder = new Holder();

                holder.slot_date_time = (SCMTextView)v.findViewById(R.id.slot_date_time);
                holder.user = (SCMTextView)v.findViewById(R.id.user);
                holder.pre_process = (SCMTextView)v.findViewById(R.id.pre_process);
                holder.process_status = (SCMTextView)v.findViewById(R.id.process_status);
                holder.value = (SCMTextView)v.findViewById(R.id.value);
                holder.comments = (SCMTextView)v.findViewById(R.id.comments);
                v.setTag(holder);
            }else {
                holder = (Holder) v.getTag();
            }
            System.out.println("SIZE: "+itemValuearraylist.get(position).get("User"));

            holder.slot_date_time.setText(itemValuearraylist.get(position).get("DateandTime"));
            holder.user.setText(itemValuearraylist.get(position).get("User"));
            holder.pre_process.setText(itemValuearraylist.get(position).get("PreProcessStatus"));
            holder.process_status.setText(itemValuearraylist.get(position).get("CurrentProcessStatus"));
            holder.value.setText(itemValuearraylist.get(position).get("value"));
            holder.comments.setText(itemValuearraylist.get(position).get("Comments"));



            return v;
        }
    }

    static class Holder {

        SCMTextView slot_date_time, user, pre_process, process_status, value, comments;



    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }


}
