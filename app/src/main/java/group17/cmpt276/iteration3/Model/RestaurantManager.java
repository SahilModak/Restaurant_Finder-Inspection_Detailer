package group17.cmpt276.iteration3.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
Restaurant Manager class (Singleton)
Implements an arraylist of Restaurants and methods to operate and access this list
 */

public class RestaurantManager implements Iterable<Restaurant>{

    private static final String TAG = "RestaurantManager";
    //Array List to store all Restaurants
    private List<Restaurant> allRestaurants = new ArrayList<>();
    private List<Restaurant> searchedRestaurants = new ArrayList<>();
    private static RestaurantManager instance;
    private boolean flag = true;
    private boolean calledSearch = false; //determines if the class should return a search list

    //private constructor to stop duplication
    private RestaurantManager(){
    }

    public void setSearchedRestaurants(String searchString, boolean checkFavorites, int maxCriticalViolation, int minCriticalViolation, String recentHazardLevel){
        calledSearch = true;
        searchedRestaurants = new ArrayList<>();

        if(searchString.equals("") && !checkFavorites && maxCriticalViolation == -1 && minCriticalViolation == -1 && recentHazardLevel.equals("")){
            calledSearch = false;
            return;
        }

        Log.i(TAG, "setSearchedRestaurants: looking for restaurnats");
        Log.i(TAG, "setSearchedRestaurants: search critera:" + searchString + ":" + checkFavorites + ":" + maxCriticalViolation + ":" + minCriticalViolation + ":" + recentHazardLevel);

        //iterate though all resaurants, selecting only those that match criteria
        for(int i = 0; i < allRestaurants.size(); i++){
            Restaurant restaurant = allRestaurants.get(i);
            boolean matchSearchString = false;
            boolean matchFavorite = false;
            boolean matchNCritical = false;
            boolean matchHazard = false;

            //check if it is a favorite
            if(checkFavorites){
                if(restaurant.isFav()){
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
                    Log.i(TAG, "setSearchedRestaurants: found match" + restaurant.getRestaurantName() +" : " + searchString);
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
                        Log.i(TAG, "setSearchedRestaurants: found match" + restaurant.getInspection(0).getHazardLevel() +" : " + recentHazardLevel);
                    }
                }
            }

            Log.i(TAG, "setSearchedRestaurants: " + matchFavorite + matchHazard + matchNCritical + matchSearchString);
            if(matchFavorite && matchHazard && matchNCritical && matchSearchString){
                searchedRestaurants.add(restaurant);
                Log.i(TAG, "setSearchedRestaurants: found matching restaurnat" + restaurant.toString());
            }
        }
    }


    public void clearSearch(){
        searchedRestaurants = new ArrayList<>();
        searchedRestaurants = null;
        calledSearch = false;
    }



    public List<Restaurant> getAllRestaurants(){
        if(calledSearch){
            Log.i(TAG, "getAllRestaurants: returning only search res");
            return searchedRestaurants;
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

    public void addRestaurant(Restaurant restaurant){
        allRestaurants.add(restaurant);
    }

    //function to get a particular restaurant at given position
    public Restaurant getRestaurant(int position){
        if(calledSearch){
            return searchedRestaurants.get(position);
        }
        else{
            return allRestaurants.get(position);
        }
    }

    public int numOfRestaurants(){
        //returns the number of restaurants stored
        return allRestaurants.size();
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

    public ArrayList<Restaurant> searchByName(String name){
        ArrayList<Restaurant> searchedRestaurants = new ArrayList<>();
        for(Restaurant x : allRestaurants){
            if(x.getRestaurantName().contains(name) ){
                searchedRestaurants.add(x);
            }
        }
        if(searchedRestaurants.size() == 0){
            return null;
        }
        return searchedRestaurants;
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
        if(allRestaurants.size() == 0){
            return;
        }
        List<Restaurant> sortedList = new ArrayList<>();
        for(int i = 0; i < allRestaurants.size(); i++){
            for(int j = 0; j < allRestaurants.size();j++){
                if(allRestaurants.get(i).getRestaurantID().compareTo(allRestaurants.get(j).getRestaurantID()) == 0){
                    continue;
                }
                if (allRestaurants.get(i).getRestaurantName().compareTo(allRestaurants.get(j).getRestaurantName()) < 0){
                    swapPositions(i, j);
                }
            }
        }
    }
}
