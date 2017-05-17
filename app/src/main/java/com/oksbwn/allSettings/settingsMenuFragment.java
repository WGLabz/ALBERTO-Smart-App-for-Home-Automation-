package com.oksbwn.allSettings;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.oksbwn.R;
import com.oksbwn.processMessages.readInboxMessage;
import com.oksbwn.readContactsToPC.SyncContacts;

public class settingsMenuFragment extends Fragment {
    public settingsMenuFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_settings_menu, container, false);
        ImageButton vlcSettings= (ImageButton)rootView.findViewById(R.id.vlcSettings);
        ImageButton syncData= (ImageButton)rootView.findViewById(R.id.syncData);
        vlcSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread th=new Thread() {
                    public void run() {
                        try {
                            Intent i = new Intent(getActivity(),com.oksbwn.allSettings.vlcSettings.class);
                            startActivity(i);
                        }catch(Exception e){
                            Log.d("Settings Error",e.getMessage());
                        }
                    }
                };
                th.start();
            }
        });
        return rootView;
    }
}
