package group17.cmpt276.iteration3.Model;

import android.util.Log;

public class SearchCriteria {

    private static final String TAG = "search criteria";
    private static SearchCriteria instance;
    private boolean searchFavourites = false;

    private String searchRestaurantName = "";
    private String searchHazardLevel = "";
    private int maxViolations;
    private int minViolations = 0;

    public static SearchCriteria getInstance(){
        if(instance == null){
            instance = new SearchCriteria();
        }
        return instance;
    }

    private SearchCriteria(){

    }

    public String getSearchRestaurantName() {
        return searchRestaurantName;
    }

    public void setSearchRestaurantName(String searchRestaurantName) {
        this.searchRestaurantName = searchRestaurantName;
    }

    public void clearSearch(){
        searchRestaurantName = "";
        searchHazardLevel = "";
    }

    public boolean isSearchFavourites() {
        return searchFavourites;
    }

    public String getSearchHazardLevel() {
        return searchHazardLevel;
    }

    public int getMaxViolations() {
        return maxViolations;
    }

    public int getMinViolations() {
        return minViolations;
    }

    public void setSearchFavourites(boolean searchFavourites) {
        this.searchFavourites = searchFavourites;
    }

    public void setSearchHazardLevel(String searchHazardLevel) {
        this.searchHazardLevel = searchHazardLevel;
    }

    public void setMaxViolations(int maxViolations) {
        Log.i(TAG, "setting max violations: ");
        this.maxViolations = maxViolations;
    }

    public void setMinViolations(int minViolations) {
        Log.i(TAG, "setMinViolations: setting min violations");
        this.minViolations = minViolations;
    }
}
