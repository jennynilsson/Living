package com.jd.living.activity.details;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;

import android.app.Activity;

import com.jd.living.database.SearchDatabase;
import com.jd.living.model.Listing;

@EActivity
public abstract class DetailsActivity extends Activity {

    @Bean
    SearchDatabase listingsDatabase;

    protected Listing listing;

    protected abstract void onUpdate();
    protected abstract void onInit();

    @AfterViews
    public void init(){
        onInit();
        int booliId = getIntent().getIntExtra("booliId", 1);
        listing = listingsDatabase.getListing(booliId);

        if (listing != null) {
            setTitle(listing.getAddress());
            onUpdate();
        }
    }
}
