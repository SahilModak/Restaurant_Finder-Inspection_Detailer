package group17.cmpt276.iteration3.Model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
Restaurant Manager class (Singleton)
Implements an arraylist of Restaurants and methods to operate and access this list
 */

public class RestaurantManager implements Iterable<Restaurant>{

    //Array List to store all Restaurants
    private List<Restaurant> allRestaurants = new ArrayList<>();
    private static RestaurantManager instance;
    private boolean flag = true;

    //private constructor to stop duplication
    private RestaurantManager(){
    }

    public List<Restaurant> getAllRestaurants(){
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

    public void deleteAllRestaurants(){
        allRestaurants.clear();
    }

    public Restaurant getRestaurant(int position){
        //function to get a particular restaurant at given position
        return allRestaurants.get(position);
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
