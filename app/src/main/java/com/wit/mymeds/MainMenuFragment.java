package com.wit.mymeds;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainMenuFragment extends Fragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainMenuFragment newInstance() {
        MainMenuFragment fragment = new MainMenuFragment();
        return fragment;
    }

    public MainMenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Button goToListButton = (Button) getActivity().findViewById(R.id.go_to_list_button);

        goToListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //FragmentManager fragmentManager = getFragmentManager();
                //Fragment listFragment = new ListFragment();
                //fragmentManager.beginTransaction()
                //        .replace(R.id.main_fragment_container, listFragment)
                //        .commit();
                MainActivity ma = (MainActivity) getActivity();
                ma.onNavigationDrawerItemSelected(MainActivity.LIST_NAVIGATION_DRAWER_POSITION);

            }
        });

    }
}
