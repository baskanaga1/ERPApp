package com.guruinfo.scm.tms;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.guruinfo.scm.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by Kannan G on 11/22/2016.
 */
public class TMSLitigationTab extends Activity {
    int beigeRowColor;
    int lightGreyRowColor;
    int lastAppliedColor;
    String response;
    TableLayout cutomerTable, leadTable, empTable, vendorTable, contractorTable;
    TabHost tabHost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tms_litegation_tab_dialogt);
        beigeRowColor = getResources().getColor(R.color.unread_bg);
        lightGreyRowColor = getResources().getColor(R.color.read_bg);
        TabHost host = (TabHost) findViewById(R.id.tabHost);
        cutomerTable = (TableLayout) findViewById(R.id.customer_table);
        leadTable = (TableLayout) findViewById(R.id.lead_table);
        empTable = (TableLayout) findViewById(R.id.emp_table);
        vendorTable = (TableLayout) findViewById(R.id.vendor_table);
        contractorTable = (TableLayout) findViewById(R.id.contractor_table);
        host.setup();
        response=getIntent().getStringExtra("response");
        //response="{\"Rights\":{\"Portal_Dash_Board\":[],\"Process_Request\":[],\"Dash_Board\":[{\"Dash_Borad_Name\":\"Availability\",\"Action\":\"AVAILABILITY_DASHBOARD\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"Lead\",\"Action\":\"LEAD\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"Calls\",\"Action\":\"S_CALLS\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"Meet\",\"Action\":\"S_MEET\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"Visit\",\"Action\":\"S_VISIT\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"Approvals\",\"Action\":\"APPROVALS\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"Campaign\",\"Action\":\"CAMPAIGN\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"Offers\",\"Action\":\"OFFERS_AND_DISCOUNTS\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"Reports\",\"Action\":\"Reports\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"TIME SHEET\",\"Action\":\"TIME_SHEET\",\"Type\":\"EMPLOYEE\"},{\"Dash_Borad_Name\":\"TMS\",\"Action\":\"TMS\",\"Type\":\"EMPLOYEE\"}],\"Load_Request\":[{\"Action\":\"LEAD_SEARCH\"},{\"Action\":\"LEAD_DISPLAY\"},{\"Action\":\"LEAD_ADD\"},{\"Action\":\"AVAILABILITY\"},{\"Action\":\"UNIT_DETAILS\"},{\"Action\":\"BROCHURE\"},{\"Action\":\"BROCHURE_EMAIL\"},{\"Action\":\"QUOATION\"},{\"Action\":\"QUOATION_SAVE\"},{\"Action\":\"QUOATION_LIST\"},{\"Action\":\"QUOATION_VIEW\"},{\"Action\":\"QUOATION_MAIL\"},{\"Action\":\"BOOKING_APPROVAL_LIST\"},{\"Action\":\"BOOKING_APPROVAL\"},{\"Action\":\"APPROVE_BOOKING\"},{\"Action\":\"CR_DR_SEARCH\"},{\"Action\":\"CAMP\"},{\"Action\":\"UNIT_BLOCK_PROCESS\"},{\"Action\":\"WISH_LIST\"},{\"Action\":\"SALE_REPORTS\"},{\"Action\":\"REFERENCE_APPROVAL\"},{\"Action\":\"APPROVE_REFERNECE\"},{\"Action\":\"GOODIES_APPROVAL\"},{\"Action\":\"DISCOUNT_APPROVAL\"},{\"Action\":\"BRS_APPROVAL\"},{\"Action\":\"CHEQUE_APPROVAL\"},{\"Action\":\"DEMAND_APPROVAL\"},{\"Action\":\"CHEQUE_HOLD_APPROVAL\"},{\"Action\":\"IDLOGIC\"},{\"Action\":\"INVOICE_APPROVAL\"},{\"Action\":\"PAYMENT_APPROVAL\"},{\"Action\":\"CP_APPROVAL\"},{\"Action\":\"PROJ_ALLOTMENT_APPROVAL\"},{\"Action\":\"LOAD_BOOKING\"},{\"Action\":\"CEO_ALLOT_LETTER_APVL\"},{\"Action\":\"FACILITY_BOOKING_APVL\"},{\"Action\":\"MAIN_VENDOR_BILL_APVL\"},{\"Action\":\"CARPARK_ADDITIONAL_APVL\"},{\"Action\":\"CARPARK_CANCEL_APVL\"},{\"Action\":\"EMP_APPROVAL_SEARCH\"},{\"Action\":\"WAIVER_APPROVAL\"},{\"Action\":\"REFUND_APPROVAL\"},{\"Action\":\"SWAP_APPROVAL\"},{\"Action\":\"CANCELLATION_APPROVAL\"}],\"Maintainance_Dash_Board\":[],\"SCM\":[],\"Approvals\":[{\"Action_Key\":\"BOOKING_APPROVAL_LIST\",\"Count\":0,\"Action\":\"Booking Approval List\"},{\"Action_Key\":\"CR_DR_SEARCH\",\"Count\":0,\"Action\":\"Credit Debit Search\"},{\"Action_Key\":\"REFERENCE_APPROVAL\",\"Count\":0,\"Action\":\"Reference Approval List\"},{\"Action_Key\":\"GOODIES_APPROVAL\",\"Count\":0,\"Action\":\"Goodies Approval List\"},{\"Action_Key\":\"DISCOUNT_APPROVAL\",\"Count\":0,\"Action\":\"Discount Approval List\"},{\"Action_Key\":\"BRS_APPROVAL\",\"Count\":0,\"Action\":\"BRS Approval List\"},{\"Action_Key\":\"CHEQUE_APPROVAL\",\"Count\":0,\"Action\":\"Cheque Status Approval\"},{\"Action_Key\":\"DEMAND_APPROVAL\",\"Count\":0,\"Action\":\"Demand Approval\"},{\"Action_Key\":\"CHEQUE_HOLD_APPROVAL\",\"Count\":0,\"Action\":\"Cheque Hold Approval\"},{\"Action_Key\":\"IDLOGIC\",\"Count\":0,\"Action\":\"ID Logic Approval\"},{\"Action_Key\":\"INVOICE_APPROVAL\",\"Count\":0,\"Action\":\"Invoice Approval\"},{\"Action_Key\":\"PAYMENT_APPROVAL\",\"Count\":0,\"Action\":\"Payment Approval\"},{\"Action_Key\":\"CP_APPROVAL\",\"Count\":0,\"Action\":\"Channel Partner Approval\"},{\"Action_Key\":\"PROJ_ALLOTMENT_APPROVAL\",\"Count\":0,\"Action\":\"Project Allotment Approval\\r\\n\"},{\"Action_Key\":\"CEO_ALLOT_LETTER_APVL\",\"Count\":0,\"Action\":\"Customer Allotment Letter Approval\"},{\"Action_Key\":\"FACILITY_BOOKING_APVL\",\"Count\":0,\"Action\":\"Facility Booking Approval\"},{\"Action_Key\":\"MAIN_VENDOR_BILL_APVL\",\"Count\":0,\"Action\":\"Vendor Bill Approval\"},{\"Action_Key\":\"CARPARK_ADDITIONAL_APVL\",\"Count\":0,\"Action\":\"Carpark Additional Approval\"},{\"Action_Key\":\"CARPARK_CANCEL_APVL\",\"Count\":0,\"Action\":\"Carpark Cancellation Approval\"},{\"Action_Key\":\"EMP_APPROVAL_SEARCH\",\"Count\":0,\"Action\":\"Employee Approval Search\"},{\"Action_Key\":\"WAIVER_APPROVAL\",\"Count\":0,\"Action\":\"Waiver Approval\"},{\"Action_Key\":\"REFUND_APPROVAL\",\"Count\":0,\"Action\":\"Refund Approval\"},{\"Action_Key\":\"SWAP_APPROVAL\",\"Count\":0,\"Action\":\"Swap Approval\"},{\"Action_Key\":\"CANCELLATION_APPROVAL\",\"Count\":0,\"Action\":\"Cancellation Approval\"}]},\"litigationListLead\":[{\"lead_code\":\"\",\"lead_sub_status\":\"1505\",\"lead_status\":\"1367\",\"lead_name\":\"ywhhwbhhs shs hdjej. GA\",\"lead_id\":\"55303\"},{\"lead_code\":\"\",\"lead_sub_status\":\"1505\",\"lead_status\":\"1367\",\"lead_name\":\"ywhhwbhhs shs hdjej. GA\",\"lead_id\":\"55303\"}],\"litigationListContractor\":[],\"litigationListCustomer\":[{\"custmer_name\":\"SRIDHAR ss\",\"customer_code\":\"APC00002\",\"project_name\":\"xcv\",\"unit_id\":\"2\",\"unit_name\":\"A102\",\"customer_id\":\"2\",\"cust_unit_id\":\"2\"},{\"custmer_name\":\"SRIDHAR ss\",\"customer_code\":\"APC00002\",\"project_name\":\"xcv\",\"unit_id\":\"2\",\"unit_name\":\"A102\",\"customer_id\":\"2\",\"cust_unit_id\":\"2\"}],\"litigationListVendor\":[],\"code\":\"1\",\"litigationListEmp\":[{\"emp_code\":\"DK00002\",\"emp_name\":\"ddddddddddd  Surana\",\"emp_id\":\"1\",\"emp_dept\":\"Board of Directors\"},{\"emp_code\":\"DK00002\",\"emp_name\":\"ddddddddddd  Surana\",\"emp_id\":\"1\",\"emp_dept\":\"Board of Directors\"}],\"msg\":\"success\"}";
        try {
            JSONObject resJson=new JSONObject(response);
            JSONArray litigationListLead=resJson.getJSONArray("litigationListLead");
            for(int i=0; i<litigationListLead.length(); i++){
               leadTable.addView(addTableRowToTableLayout(litigationListLead.getJSONObject(i),"Lead",11,(i+1)));
            }
            JSONArray litigationListContractor=resJson.getJSONArray("litigationListContractor");
            for(int i=0; i<litigationListContractor.length(); i++){
                contractorTable.addView(addTableRowToTableLayout(litigationListContractor.getJSONObject(i),"Contractor",9,(i+1)));
            }
            JSONArray litigationListCustomer=resJson.getJSONArray("litigationListCustomer");
            for(int i=0; i<litigationListCustomer.length(); i++){
                cutomerTable.addView(addTableRowToTableLayout(litigationListCustomer.getJSONObject(i),"Customer",11,(i+1)));
            }
            JSONArray litigationListVendor=resJson.getJSONArray("litigationListVendor");
            for(int i=0; i<litigationListVendor.length(); i++){
                vendorTable.addView(addTableRowToTableLayout(litigationListVendor.getJSONObject(i),"Vendor",9,(i+1)));
            }
            JSONArray litigationListEmp=resJson.getJSONArray("litigationListEmp");
            for(int i=0; i<litigationListEmp.length(); i++){
                empTable.addView(addTableRowToTableLayout(litigationListEmp.getJSONObject(i),"Emp",9,(i+1)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Customer");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Customer");
        host.addTab(spec);
        //Tab 2
        spec = host.newTabSpec("Lead");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Lead");
        host.addTab(spec);
        //Tab 3
        spec = host.newTabSpec("Employee");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Employee");
        host.addTab(spec);
        //Tab 4
        spec = host.newTabSpec("Vendor");
        spec.setContent(R.id.tab4);
        spec.setIndicator("Vendor");
        host.addTab(spec);
        //Tab 5
        spec = host.newTabSpec("Contractor");
        spec.setContent(R.id.tab5);
        spec.setIndicator("Contractor");
        host.addTab(spec);
    }
  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    public TableRow addTableRowToTableLayout(JSONObject list, String type, float sumWeight, int childIndex) {
        TableRow tableRow = new TableRow(this);
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.FILL_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        tableRow.setGravity(Gravity.CENTER_VERTICAL);
        tableRow.setLayoutParams(layoutParams);
        tableRow.setOrientation(LinearLayout.HORIZONTAL);
        tableRow.setWeightSum(sumWeight);
        if(getApplicationContext().getResources().getBoolean(R.bool.isTablet))
            tableRow.setPadding(10, 10, 10, 10);
        else
        tableRow.setPadding(5, 5, 5, 5);
        applyAlternateRowColor(tableRow);
        try {
        if (type.equals("Lead")) {
            tableRow.addView(addTextViewToTableRow("" + childIndex, null, 1), 0);
            tableRow.addView(addTextViewToTableRow(list.getString("lead_name"), null, 3), 1);
            tableRow.addView(addTextViewToTableRow(list.getString("lead_code"), null, 3), 2);
            tableRow.addView(addTextViewToTableRow(list.getString("lead_status"), null, 2), 3);
            tableRow.addView(addTextViewToTableRow(list.getString("lead_sub_status"), null, 2), 4);
        } else if (type.equals("Customer")) {
            tableRow.addView(addTextViewToTableRow("" + childIndex, null, 1), 0);
            tableRow.addView(addTextViewToTableRow(list.getString("custmer_name"), null, 3), 1);
            tableRow.addView(addTextViewToTableRow(list.getString("customer_code"), null, 3), 2);
            tableRow.addView(addTextViewToTableRow(list.getString("unit_name"), null, 2), 3);
            tableRow.addView(addTextViewToTableRow(list.getString("project_name"), null, 2), 4);
        } else if (type.equals("Contractor")) {
            tableRow.addView(addTextViewToTableRow("" + childIndex, null, 1), 0);
            tableRow.addView(addTextViewToTableRow(list.getString("contractor_name"), null, 3), 1);
            tableRow.addView(addTextViewToTableRow(list.getString("contractor_code"), null, 2), 2);
            tableRow.addView(addTextViewToTableRow(list.getString("pri_email"), null, 3), 3);
        } else if (type.equals("Vendor")) {
            tableRow.addView(addTextViewToTableRow("" + childIndex, null, 1), 0);
            tableRow.addView(addTextViewToTableRow(list.getString("vendor_name"), null, 3), 1);
            tableRow.addView(addTextViewToTableRow(list.getString("vendor_code"), null, 2), 2);
            tableRow.addView(addTextViewToTableRow(list.getString("vendor_email"), null, 3), 3);
        } else if (type.equals("Emp")) {
            tableRow.addView(addTextViewToTableRow("" + childIndex, null, 1), 0);
            tableRow.addView(addTextViewToTableRow(list.getString("emp_name"), null, 3), 1);
            tableRow.addView(addTextViewToTableRow(list.getString("emp_code"), null, 3), 2);
            tableRow.addView(addTextViewToTableRow(list.getString("emp_dept"), null, 2), 3);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tableRow;
    }
    public TextView addTextViewToTableRow(String value, Typeface typeface, int layoutWeight) {
        TextView textView = new TextView(this);
        textView.setTextColor(getResources().getColor(R.color.result_color));
        if (typeface != null)
            textView.setTypeface(typeface);
        //textView.setTextSize(14);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.Filter_textsize));
        textView.setGravity(Gravity.CENTER);
        if(getApplicationContext().getResources().getBoolean(R.bool.isTablet))
            textView.setPadding(10, 10, 10, 10);
        else
        textView.setPadding(5, 5, 5, 5);
        textView.setText(value);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, layoutWeight);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        textView.setLayoutParams(layoutParams);
        return textView;
    }
    public void applyAlternateRowColor(LinearLayout dataTable) {
        if (lastAppliedColor == 0) {
            dataTable.setBackgroundColor(beigeRowColor);
            lastAppliedColor = beigeRowColor;
        } else if (lastAppliedColor == beigeRowColor) {
            dataTable.setBackgroundColor(lightGreyRowColor);
            lastAppliedColor = lightGreyRowColor;
        } else if (lastAppliedColor == lightGreyRowColor) {
            dataTable.setBackgroundColor(beigeRowColor);
            lastAppliedColor = beigeRowColor;
        }
    }
}