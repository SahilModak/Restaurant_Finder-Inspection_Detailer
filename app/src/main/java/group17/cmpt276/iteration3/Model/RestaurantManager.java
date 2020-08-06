package group17.cmpt276.iteration3.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Restaurant Manager class (Singleton)
 * Implements an arraylist of Restaurants and methods to operate and access this list
 */
public class RestaurantManager implements Iterable<Restaurant>{

    private static final String TAG = "RestaurantManager";
    //Array List to store all Restaurants
    private List<Restaurant> allRestaurants = new ArrayList<>();
    private List<Restaurant> searchedRestaurants = new ArrayList<>();
    private List<Restaurant> favesWithUpdates = new ArrayList<>();
    private static RestaurantManager instance;
    private boolean flag = true;
    private boolean calledSearch = false; //determines if the class should return a search list
    private boolean calledFavourites = false;
    private NewDataNotify newDataNotify;

    //private constructor to stop duplication
    private RestaurantManager(){
        newDataNotify = NewDataNotify.getInstance();
    }

    //sets the array list that contains restaurant the meet parameter search criteria
    public void setSearchedRestaurants(String searchString, boolean checkFavorites, int maxCriticalViolation, int minCriticalViolation, String recentHazardLevel){
        if(searchString.equals("") && !checkFavorites && maxCriticalViolation == -1 && minCriticalViolation == -1 && recentHazardLevel.equals("")){
            if(calledSearch){
                newDataNotify.setNewData(true);
            }
            calledSearch = false;
            return;
        }
        calledSearch = true;
        searchedRestaurants = new ArrayList<>();
        newDataNotify.setNewData(true);

        //iterate though all restaurants, selecting only those that match criteria
        for(int i = 0; i < allRestaurants.size(); i++){
            Restaurant restaurant = allRestaurants.get(i);
            boolean matchSearchString = false;
            boolean matchFavorite = false;
            boolean matchNCritical = false;
            boolean matchHazard = false;

            //check if it is a favorite
            if(checkFavorites){
                if(restaurant.isFavourite()){
                    matchFavorite = true;
                }
            }
            else{
                matchFavorite = true;
            }

            //check if it matches search string
            if(searchString.equals("")) {
                matchSearchString = true;
            }
            else{
                if (restaurant.getRestaurantName().toLowerCase().contains(searchString.toLowerCase())){
                    matchSearchString = true;
                }
            }

            //check to see if searching violations in necessary
            if(maxCriticalViolation == -1 && minCriticalViolation == -1){
                matchNCritical = true;
            }
            else{
                //just looking for [min, noMax]
                if(maxCriticalViolation != -1 && minCriticalViolation == -1){
                    if(restaurant.getNCriticalLastYear() >= minCriticalViolation){
                        matchNCritical = true;
                    }
                }
                else{
                    if(restaurant.getNCriticalLastYear() >= minCriticalViolation && restaurant.getNCriticalLastYear() <= maxCriticalViolation){
                        matchNCritical = true;
                    }
                }
            }

            //check to see if match hazard level
            if(recentHazardLevel.equals("")) {
                matchHazard = true;
            }
            else {
                if(restaurant.numOfInspections() > 0){
                    if(restaurant.getInspection(0).getHazardLevel().toLowerCase().equals(recentHazardLevel.toLowerCase())){
                        matchHazard = true;
                    }
                }
            }

            if(matchFavorite && matchHazard && matchNCritical && matchSearchString){
                searchedRestaurants.add(restaurant);
            }
        }
    }


    public void clearSearch(){
        searchedRestaurants = null;
        calledSearch = false;
    }

    public boolean isCalledSearch() {
        return calledSearch;
    }


    public List<Restaurant> getAllRestaurants(){
        if(calledSearch){
            return searchedRestaurants;
        } else if(calledFavourites) {
            return favesWithUpdates;
        }
        return allRestaurants;
    }

    public static RestaurantManager getInstance(){
        if(instance == null){
            instance = new RestaurantManager();
        }
        return instance;
    }

    public int getRestaurantPositionInArray(Restaurant restaurant){
        if(calledSearch){
            return searchedRestaurants.indexOf(restaurant);
        }
        return allRestaurants.indexOf(restaurant);
    }

    public void setFlagFalse(){
        flag = false;
    }

    public void setFlagTrue(){
        flag = true;
    }

    public boolean getFlag(){
        return flag;
    }

    public void setCalledFavourites(boolean calledFavourites) {
        this.calledFavourites = calledFavourites;
    }

    public void addRestaurant(Restaurant restaurant){
        allRestaurants.add(restaurant);
    }

    //function to get a particular restaurant at given position
    public Restaurant getRestaurant(int position){
        if(calledSearch){
            return searchedRestaurants.get(position);
        } else if (calledFavourites) {
            return favesWithUpdates.get(position);
        } else{
            return allRestaurants.get(position);
        }
    }

    public int numOfRestaurants(){
        //returns the number of restaurants stored
        return allRestaurants.size();
    }

    public List<Restaurant> getFavesWithUpdates() {
        return favesWithUpdates;
    }

    public void setFavesWithUpdates(List<Restaurant> faveRestaurants) {
        this.favesWithUpdates = faveRestaurants;
    }

    @Override
    public Iterator<Restaurant> iterator(){
        //override to make the class iterable
        return allRestaurants.iterator();
    }

    public Restaurant searchById(String id){
        for(Restaurant x : allRestaurants){
            if(x.getRestaurantID().equals(id)){
                return x;
            }
        }
        return null;
    }

    private void swapPositions(int i, int j){
        Restaurant temp = allRestaurants.get(i);
        allRestaurants.set(i, allRestaurants.get(j));
        allRestaurants.set(j, temp);
    }

    public void sortAllRestaurantsInspections(){
        for (Restaurant rest: allRestaurants) {
            rest.sortInspectionsByDate();
        }
    }


    public void sortByRestaurantName(){
        List<Restaurant> listToSort = new ArrayList<>();

        if(calledFavourites){
            Collections.sort(favesWithUpdates);
        } else {
            listToSort = allRestaurants;
        }

        if(listToSort.size() == 0){
            return;
        }
        for(int i = 0; i < listToSort.size(); i++){
            for(int j = 0; j < listToSort.size();j++){
                if(listToSort.get(i).getRestaurantID().compareTo(listToSort.get(j).getRestaurantID()) == 0){
                    continue;
                }
                if (listToSort.get(i).getRestaurantName().compareTo(listToSort.get(j).getRestaurantName()) < 0){
                    swapPositions(i, j);
                }
            }
        }
    }
}
