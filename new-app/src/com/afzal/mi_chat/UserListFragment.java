package com.afzal.mi_chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class UserListFragment extends Fragment {

    private View mUserListView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mUserListView = inflater.inflate(R.layout.user_list, null);
        return mUserListView;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ListView list = (ListView) mUserListView.findViewById(R.id.userlist);
        String[] array = new String[] {
                "Afzal",
                "AndrewS",
                "Defroster",
                "Differential",
                "Entropy",
                "Freija",
                "Grover",
                "Jester",
                "Jimbojones",
                "Ponyo",
                "SilentHero-_-",
                "Tailsnake",
                "Toast",
                "goodnews.inc",
                "justinftw",
                "romita_sur",
                "sarahsullz",
                "Defroster" };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.user_list_item, R.id.user_row, array);

        list.setAdapter(adapter);
    }
}
