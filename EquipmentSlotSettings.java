package com.guruinfo.scm.Equipment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.guruinfo.scm.R;
import com.guruinfo.scm.common.BaseFragment;

import static com.guruinfo.scm.SCMDashboardActivityLatest.NavigationFragmentManager;


public class EquipmentSlotSettings extends BaseFragment {
    Context context;

    Button equip_close,equip_reset, equip_save;

    public static EquipmentSlotSettings newInstance(Bundle bundle) {
        EquipmentSlotSettings fragment = new EquipmentSlotSettings();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub

        inflater.inflate(R.menu.list_action, menu);

        menu.findItem(R.id.new_request).setVisible(false);
        menu.findItem(R.id.filter).setVisible(false);
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.favourite).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments() != null) {
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.equip_slot_settings, container, false);
        final Bundle bundle = this.getArguments();
        if (bundle != null) {

        }
        equip_close = (Button)view.findViewById(R.id.equip_close);
        equip_save = (Button)view.findViewById(R.id.equip_save);
        equip_reset = (Button)view.findViewById(R.id.equip_reset);


        equip_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                NavigationFragmentManager(EquipListFragment.newInstance(bundle), "Equip");
            }
        });

        return view;
    }



    @Override
    public void onDetach() {
        super.onDetach();
       // mListener = null;
    }


}
