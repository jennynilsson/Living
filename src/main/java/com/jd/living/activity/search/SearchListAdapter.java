package com.jd.living.activity.search;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.jd.living.R;
import com.jd.living.model.Listing;
import com.jd.living.model.Result;
import com.jd.living.database.ListingsDatabase;


@EBean
public class SearchListAdapter extends ArrayAdapter<Listing> implements ListingsDatabase.ListingsListener {

    @Bean
    ListingsDatabase database;

    private List<Listing> listings = new ArrayList<Listing>();

    public SearchListAdapter(Context context) {
        super(context, R.layout.list_item);
    }

    @AfterInject
    public void init(){
        database.registerListingsListener(this);
        database.launchListingsSearch();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListItem listItem;
        if (convertView == null) {
            listItem = ListItem_.build(getContext());
        } else {
            listItem = (ListItem) convertView;
        }

        listItem.bind(getItem(position));

        return listItem;
    }

    @UiThread
    public void update() {
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listings.size();
    }

    @Override
    public Listing getItem(int position) {
        return listings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onUpdate(Result result) {
        this.listings = result.getResult();
        update();
    }

    @Override
    public void onSearchStarted() {

    }
}