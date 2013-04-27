package com.afzal.mi_chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OptionsListFragment extends Fragment {

    private View mOptionsView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOptionsView = inflater.inflate(R.layout.options_list, null);
        return mOptionsView;
    }
}