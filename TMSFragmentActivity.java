package com.guruinfo.scm.tms;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.guruinfo.scm.R;
import com.guruinfo.scm.SCMDashboardActivityLatest;
import com.guruinfo.scm.common.BaseFragmentActivity;
import com.guruinfo.scm.common.SessionManager;
import com.guruinfo.scm.common.service.BackgroundTask;
import com.guruinfo.scm.common.service.OnTaskCompleted;
import java.util.ArrayList;
// Created by Kannan G on 10/1/2016.
public class TMSFragmentActivity extends BaseFragmentActivity implements OnTaskCompleted {
    public static String TAG = "TMSFragmentActivity";
    String FRAGMENT_TAG = "TMS Fragment";
    boolean isdrawerOpen = false;
    SessionManager session;
    String name, cr_id, uid, lastLoginId;
    BackgroundTask backgroundTask;
    String requestParameter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    ArrayList<String> sideMenuList;
    ImageView slideMenuBtn;
    public static TextView actionBarTitle;
    public static ImageView actionBarFilterBtn;
    public static ImageView actionBarBookMarkBtn;
    public static ImageView actionBarNewRequestBtn;
    public static ImageView homeIcon;
    public static ImageView dashBoard;
    FragmentManager fragmentManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tms_dashboard_fragment);
        session = new SessionManager(getApplicationContext());
        uid = session.getUserDetails().get(SessionManager.ID);
        cr_id = session.getUserDetails().get(SessionManager.CR_ID);
        loadActionBar();
        loadSideMenu();
        fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("listType", "");
        navigateTo(TMSListTreeActivity.class.getName(), bundle);
    }
    @Override
    public void onTaskCompleted(String values, String flag) {
    }
    public Fragment navigateTo(String fragmentClassToNavigateTo, Bundle bundle) {
        Log.d(TAG, "navigateToNextFragment-" + fragmentClassToNavigateTo);
        Fragment fragmentToNavigate = Fragment.instantiate(context, fragmentClassToNavigateTo);
        if (bundle != null)
            fragmentToNavigate.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);
        ft.replace(R.id.frame_container, fragmentToNavigate, FRAGMENT_TAG);
        // ft.add(R.id.frame_container, fragmentToNavigate, FRAGMENT_TAG);
        ft.addToBackStack(fragmentClassToNavigateTo);
        ft.commitAllowingStateLoss();
        getSupportFragmentManager().executePendingTransactions();
        return fragmentToNavigate;
    }
    private void loadSideMenu() {
        TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.string.app_name, /* "open drawer" description for accessibility */
                R.string.app_name /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                isdrawerOpen = false;
                // getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                isdrawerOpen = true;
                // getActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        sideMenuList = new ArrayList<String>();
        sideMenuList.add("Self Task");
        sideMenuList.add("My Task");
        sideMenuList.add("Assigned");
        sideMenuList.add("Group");
        sideMenuList.add("Tagged");
        navMenuIcons.recycle();
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
        NavigationListAdapter adapter = new NavigationListAdapter(getApplicationContext(), sideMenuList);
        mDrawerList.setAdapter(adapter);
    }
    private void loadActionBar() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.tms_custom_action_bar, null);
        slideMenuBtn = (ImageView) v.findViewById(R.id.menu_icon);
        actionBarFilterBtn = (ImageView) v.findViewById(R.id.search_icon);
        actionBarBookMarkBtn = (ImageView) v.findViewById(R.id.bookmark_icon);
        actionBarNewRequestBtn = (ImageView) v.findViewById(R.id.new_request_icon);
        actionBarTitle = (TextView) v.findViewById(R.id.small_title);
        homeIcon = (ImageView) v.findViewById(R.id.home);
        dashBoard = (ImageView) v.findViewById(R.id.dashboard_icon);
        homeIcon.setVisibility(View.VISIBLE);
        dashBoard.setVisibility(View.GONE);
        Spannable text = null;
        text = new SpannableString("Task Listing");
        text.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.white)), 0, text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        actionBarTitle.setText(text);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setCustomView(v);
        slideMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.RotateIn)
                        .duration(500)
                        .playOn(slideMenuBtn);
                if (!isdrawerOpen)
                    mDrawerLayout.openDrawer(mDrawerList);
                else
                    mDrawerLayout.closeDrawer(mDrawerList);
            }
        });
        dashBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TMSFragmentActivity.this, TMSFragmentActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        } );
        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                Intent intent = new Intent(TMSFragmentActivity.this, SCMDashboardActivityLatest.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);
        }
    }
    public void displayView(int position) {
        dashBoard.setVisibility(View.VISIBLE);
        if (sideMenuList.get(position).equalsIgnoreCase("Self Task")) {
            mDrawerLayout.closeDrawer(mDrawerList);
            Bundle bundle = new Bundle();
            bundle.putString("listType", "selfAssigned");
            navigateTo(TMSListTreeActivity.class.getName(), bundle);
        } else if (sideMenuList.get(position).equalsIgnoreCase("My Task")) {
            mDrawerLayout.closeDrawer(mDrawerList);
            Bundle bundle = new Bundle();
            bundle.putString("listType", "assignedToMe");
            navigateTo(TMSListTreeActivity.class.getName(), bundle);
        } else if (sideMenuList.get(position).equalsIgnoreCase("Assigned")) {
            mDrawerLayout.closeDrawer(mDrawerList);
            Bundle bundle = new Bundle();
            bundle.putString("listType", "assignedByMe");
            navigateTo(TMSListTreeActivity.class.getName(), bundle);
        } else if (sideMenuList.get(position).equalsIgnoreCase("Group")) {
            mDrawerLayout.closeDrawer(mDrawerList);
            Bundle bundle = new Bundle();
            bundle.putString("listType", "assignedAsGroupMember");
            navigateTo(TMSListTreeActivity.class.getName(), bundle);
        } else if (sideMenuList.get(position).equalsIgnoreCase("Tagged")) {
            mDrawerLayout.closeDrawer(mDrawerList);
            Bundle bundle = new Bundle();
            bundle.putString("listType", "Tagged");
            navigateTo(TMSListTreeActivity.class.getName(), bundle);
        }
    }
    @Override
    public void onBackPressed() {
        if (isdrawerOpen) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else if (fragmentManager != null) {
            if (fragmentManager.getBackStackEntryCount() > 0) {
                String currentFragment = getCurrentFragmentName();
                if (currentFragment.equals(TMSListTreeActivity.class.getName())) {
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    Intent intent = new Intent(TMSFragmentActivity.this, SCMDashboardActivityLatest.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    this.finish();
                } else {
                    Log.d(TAG, "###Fragment Not Handled, Hence calling super");
                    super.onBackPressed();
                }
            } else {
                super.onBackPressed();
            }
        } else {
            finish();
            traversToNextActivity(context, TMSFragmentActivity.class);
        }
    }
    public String getCurrentFragmentName() {
        String currentFragmentInBackStack = null;
        if (fragmentManager.getBackStackEntryCount() > 0) {
            //  Fragment prev_fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
            // prev_fragment.getClass().getName();
            currentFragmentInBackStack = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getClass().getName();
            Log.d(TAG, "currentFragmentInBackStack--" + currentFragmentInBackStack);
        }
        return currentFragmentInBackStack;
    }
}
