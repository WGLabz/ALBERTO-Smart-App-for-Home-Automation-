package com.oksbwn.vlc;
import java.net.Authenticator;
import java.net.URL;

import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.oksbwn.R;

public class vlcControlsFragment extends Fragment {
    String exceptionGot="";
    TextView tv1=null;
    public vlcControlsFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_vlc_controls, container, false);
        tv1 = (TextView)rootView.findViewById(R.id.errorView);
        tv1.setTextColor(Color.BLACK);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final ImageButton Prev = (ImageButton)rootView. findViewById(R.id.prevButton);
        final ImageButton Play = (ImageButton)rootView. findViewById(R.id.playButton);
        final ImageButton Next = (ImageButton) rootView.findViewById(R.id.nextButton);
        final ImageButton TenS = (ImageButton) rootView.findViewById(R.id.seek10Sec);
        final ImageButton OMin = (ImageButton) rootView.findViewById(R.id.seekOneMinute);
        final ImageButton MTS = (ImageButton) rootView.findViewById(R.id.back10Sec);
        final SeekBar volume = (SeekBar) rootView.findViewById(R.id.seekSoundBar);
        //volume.get

        TenS.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    String urlString = "http://192.168.0.1:8080/requests/status.xml?command=seek&val=+20";
                    Authenticator.setDefault(new authoRize());
                    URL url = new URL(urlString);
                    url.getContent();
                } catch (Exception e) {
                    setExceptionMessage(e.getMessage());
                }

            }

        });
        OMin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Thread th= new Thread() {
                    public void run() {
                        try {
                            String urlString = "http://192.168.0.1:8080/requests/status.xml?command=seek&val=+60";
                            Authenticator.setDefault(new authoRize());
                            URL url = new URL(urlString);
                            url.getContent();
                        }
                        catch( Exception e ) {
                            setExceptionMessage(e.getMessage());
                        } }};
                th.start();
            }
        });
        MTS.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {

                    String urlString = "http://192.168.0.1:8080/requests/status.xml?command=seek&val=-20";
                    Authenticator.setDefault(new authoRize());
                    URL url = new URL(urlString);
                    url.getContent();
                    v.getBackground().clearColorFilter();
                } catch (Exception e) {
                    setExceptionMessage(e.getMessage());
                }

            }

        });
        Prev.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    String urlString = "http://192.168.0.1:8080/requests/status.xml?command=pl_previous";
                    Authenticator.setDefault(new authoRize());
                    URL url = new URL(urlString);
                    url.getContent();
                } catch (Exception e) {
                    setExceptionMessage(e.getMessage());
                }

            }

        });
        Play.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    String urlString = "http://192.168.0.1:8080/requests/status.xml?command=pl_pause";
                    Authenticator.setDefault(new authoRize());
                    URL url = new URL(urlString);
                    url.getContent();
                } catch (Exception e) {
                    setExceptionMessage(e.getMessage());
                }

            }

        });
        Next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    String urlString = "http://192.168.0.1:8080/requests/status.xml?command=pl_next";
                    Authenticator.setDefault(new authoRize());
                    URL url = new URL(urlString);
                    url.getContent();
                } catch (Exception e) {
                    setExceptionMessage(e.getMessage());
                }

            }

        });
        volume.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar,int progresValue, boolean fromUser)
            {
                progress = progresValue;
                try {
                    String urlString = "http://192.168.0.1:8080/requests/status.xml?command=volume&val="+5*progress;
                    Authenticator.setDefault(new authoRize());
                    URL url = new URL(urlString);
                    url.getContent();
                } catch (Exception e) {
                    setExceptionMessage(e.getMessage());
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv1.setText(progress + "/" + seekBar.getMax());
            }
        });
        return rootView;
    }

    private final Handler myHandler = new Handler();

    final Runnable updateRunnable = new Runnable() {
        public void run() {
            //call the activity method that updates the UI
            updateUI();
        }
    };


    private void updateUI()
    {
        tv1.setText(exceptionGot);
    }

    private void setExceptionMessage(String exception)
    {
        exceptionGot=exception;
        myHandler.post(updateRunnable);

    }
}
