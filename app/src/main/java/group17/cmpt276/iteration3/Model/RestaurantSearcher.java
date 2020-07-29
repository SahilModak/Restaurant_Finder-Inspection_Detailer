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
        if(checkFavorites){
            searchForFavorites();
        }
        searchForNCriticalLastYear(nCriticalLastYear,nCriticalLess);
        searchByLastInspectionHazardLevel(recentHazardLevel);
    }

    //search for restaurnt by name, by default include all restaurants (if no search criteria)
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

    //remove restaruants who do not have an inspection, or do
    private void searchByLastInspectionHazardLevel(String searchHazardString) {
        if (searchHazardString.equals("")) {
            return;
        }
        for (Restaurant restaurant : restaurantManager.getAllRestaurants()) {
            //remove restaurants without a last inspection
            if(restaurant.numOfInspections() == 0) {
                searchedRestaurants.remove(restaurant);
            }
            else{
                if(!restaurant.getInspection(0).getHazardLevel().equals(searchHazardString)){
                    searchedRestaurants.remove(restaurant);
                }
            }
        }
    }

    //remove rests that aren't favorites
    private void searchForFavorites(){
        for(Restaurant restaurant: searchedRestaurants){
            if(!restaurant.isFav()){
                searchedRestaurants.remove(restaurant);
            }
        }
    }

    //remove restaurnts who do not meet the n critical last year search criteria
    private void searchForNCriticalLastYear(int nCriticalLastYear, boolean nCriticalLess){
        if(nCriticalLastYear == -1){
            return;
        }
        //check to see if we want the n critical violations less than input value ie: last years violations <= 5
        //Remove restaurants who do have more or fewer than nCriticalLastYear
        if(nCriticalLess){
            for(Restaurant restaurant: searchedRestaurants){
                if((restaurant.getNCriticalLastYear() > nCriticalLastYear)){
                    searchedRestaurants.remove(restaurant);
                }
            }
        }
        else{
            for(Restaurant restaurant: searchedRestaurants){
                if((restaurant.getNCriticalLastYear() < nCriticalLastYear)){
                    searchedRestaurants.remove(restaurant);
                }
            }
        }
    }


    public List<Restaurant> getSearchedRestaurants(){
        return searchedRestaurants;
    }

    private void clearSearch(){
        searchedRestaurants.clear();
    }

    //for debug
    public void printSortList(){
        for(Restaurant restaurant: searchedRestaurants){
            Log.i(TAG, "printSortList (rest in search list): " + restaurant.toString());
        }
    }
}
