package group17.cmpt276.iteration3.Model;

public class SearchCriteria {
    private static SearchCriteria instance;
    private boolean searchFavourites = false;
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
        this.maxViolations = maxViolations;
    }

    public void setMinViolations(int minViolations) {
        this.minViolations = minViolations;
    }
}
