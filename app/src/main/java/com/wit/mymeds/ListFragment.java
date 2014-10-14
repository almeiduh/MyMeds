package com.wit.mymeds;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListFragment extends android.support.v4.app.ListFragment {

private static final String LIST_ITEM_TITLE = "list_title";
    private static final String LIST_ITEM_DESCRIPTION = "list_description" ;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        list.add(putData("Mucosolvan", "dias.."));
        list.add(putData("Ozonol", "dias.."));
        list.add(putData("Bepanthene", "dias.."));

        ListAdapter la = new SimpleAdapter(getActivity(), list, R.layout.list_item_layout, new String[] {LIST_ITEM_TITLE, LIST_ITEM_DESCRIPTION } , new int[]{R.id.list_title, R.id.list_description} );

        this.setListAdapter(la);

        return view;
    }

    private HashMap<String, String> putData(String name, String purpose) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put(LIST_ITEM_TITLE, name);
        item.put(LIST_ITEM_DESCRIPTION, purpose);
        return item;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);




    }
}
