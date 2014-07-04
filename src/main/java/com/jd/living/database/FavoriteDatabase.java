package com.jd.living.database;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

import com.jd.living.LivingApplication;
import com.jd.living.database.ormlite.FavoriteRepository;
import com.jd.living.model.Listing;
import com.jd.living.model.Result;
import com.jd.living.model.ormlite.Favorite;

@EBean(scope = EBean.Scope.Singleton)
public class FavoriteDatabase extends BooliDatabase {

    @Bean
    SearchDatabase listingsDatabase;

    public interface FavoriteListener {
        void onUpdateFavorites(List<Listing> listings);
        void onDetailsRequestedForFavorite(int booliId);
    }

    private List<Listing> favorites = new ArrayList<Listing>();
    //private Map<Integer, Listing> map = new HashMap<Integer, Listing>();
    private FavoriteListener favoriteListener;
    private FavoriteRepository favoriteRepository;

    @Override
    protected void init() {

        for (Favorite favorite : getRepository().getFavorites()) {
            server.getListing(favorite.getId());
        }
        /*
        preferences = context.getSharedPreferences(SearchPreferenceKey.PREFERENCE_FAVORITES, Context.MODE_PRIVATE);

        int numbers = preferences.getInt(SearchPreferenceKey.PREFERENCE_NBR_OF_FAVORITES, 0);
        for (int i = 1 ; i <= numbers ; i++) {
            server.getListing(preferences.getInt(SearchPreferenceKey.PREFERENCE_FAVORITE + i, 0));
        }
        */
    }

    @Override
    public List<Listing> getResult(){
        return favorites;
    }

    public void setFavoriteListener(FavoriteListener listener) {
        favoriteListener = listener;
        listener.onUpdateFavorites(getResult());
    }

    @Override
    public void setCurrentId(int booliId) {
        currentBooliId = booliId;
        favoriteListener.onDetailsRequestedForFavorite(booliId);
    }

    public void updateFavorite(Listing listing) {
        if (isFavorite(listing)) {
            removeFavorite(listing);
        } else {
            addFavorite(listing);
            server.getListing(listing.getBooliId());
        }
    }

    private void addFavorite(Listing listing) {
        getRepository().addFavorite(new Favorite(listing.getBooliId()));
        /*
        SharedPreferences.Editor editor = preferences.edit();

        int nbrOfFavorites = preferences.getInt(SearchPreferenceKey.PREFERENCE_NBR_OF_FAVORITES, 0);
        editor.putInt(SearchPreferenceKey.PREFERENCE_FAVORITE + (nbrOfFavorites + 1), listing.getBooliId() );
        editor.putInt(SearchPreferenceKey.PREFERENCE_NBR_OF_FAVORITES, nbrOfFavorites + 1);
        editor.commit();
        */
    }

    private void removeFavorite(Listing listing) {
        /*
        SharedPreferences.Editor editor = preferences.edit();

        int nbrOfFavorites = preferences.getInt(SearchPreferenceKey.PREFERENCE_NBR_OF_FAVORITES, 0);
        for (int i = 1; i <= nbrOfFavorites; i++ ) {
            editor.remove(SearchPreferenceKey.PREFERENCE_FAVORITE + i);
        }
        editor.commit();
        */

        favorites.remove(listing);

        /*
        int index = 1;
        for (Listing l : getResult()) {
            editor.putInt(SearchPreferenceKey.PREFERENCE_FAVORITE + index++, l.getBooliId());
        }

        editor.putInt(SearchPreferenceKey.PREFERENCE_NBR_OF_FAVORITES, getResult().size());
        editor.commit();
        */
        getRepository().deleteFavorite(listing.getBooliId());

        notifyListeners(false);
    }

    public boolean isFavorite(Listing listing) {
        return getRepository().isFavorite(listing.getBooliId());
    }

    @Override
    public void onListingsResult(BooliDatabase.ActionCode action, Result result) {
        switch (action) {
            case FAVORITE:
                for (Listing listing : result.getResult()) {
                    favorites.add(listing);
                }
                notifyListeners(true);
                break;
            default:
                break;
        }
    }

    private void notifyListeners(boolean listingAdded) {
        favoriteListener.onUpdateFavorites(getResult());
    }

    private FavoriteRepository getRepository() {
        if (favoriteRepository == null) {
            favoriteRepository = ((LivingApplication) context.getApplicationContext()).getFavoriteRepository();
        }
        return favoriteRepository;
    }
}
