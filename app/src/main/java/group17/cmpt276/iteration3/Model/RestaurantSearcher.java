package group17.cmpt276.iteration3.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class RestaurantSearcher {
    private static final String TAG = "Restaurant Searcher";
    private List<Restaurant> searchedRestaurants = new ArrayList<>();
    private RestaurantManager restaurantManager;

    public RestaurantSearcher(){
        restaurantManager = RestaurantManager.getInstance();
    }

    //default cases (no check): "", false, -1, ""
    public void searchForRestaurants(String searchString, boolean checkFavorites, int nCriticalLastYear, boolean nCriticalLess, String recentHazardLevel){
        searchByName(searchString);
    }

    private void searchByName(String searchString) {
        if (searchString.equals("")) {
            searchedRestaurants = restaurantManager.getAllRestaurants();
        }
        for (Restaurant restaurant : restaurantManager.getAllRestaurants()) {
            if (restaurant.getRestaurantName().toLowerCase().contains(searchString.toLowerCase())) {
                searchedRestaurants.add(restaurant);
                Log.i(TAG, "searchByName: added restaurant " + restaurant.toString());
            }
        }
    }

    private void searchForFavorites(){
        for(Restaurant restaurant: searchedRestaurants){
            if(!restaurant.isFav()){
                searchedRestaurants.remove(restaurant);
            }
        }
    }


    public List<Restaurant> getSearchedRestaurants(){
        return searchedRestaurants;
    }

    private void clearSearch(){
        searchedRestaurants.clear();
    }
}
