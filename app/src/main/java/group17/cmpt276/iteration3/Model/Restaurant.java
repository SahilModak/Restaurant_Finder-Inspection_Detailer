package group17.cmpt276.iteration3.Model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import group17.cmpt276.iteration3.Model.CSV.DatabaseInfo;

/*
Restaurant Class contains metadata about a single restaurant including a list of inspections,
name, address, id and gps coordinates and methods to access these fields
 */

public class Restaurant implements Iterable<Inspection>, ClusterItem, Comparable<Restaurant> {
    private String restaurantName;
    private String restaurantAddress; //consider making address an object if we need them searchable (hard to parse text)
    private float[] restaurantGPS;
    private String restaurantID;
    private List<Inspection> allInspections = new ArrayList<>();
    private final LatLng mPosition;
    private boolean isFavourite;

    //create new restaurant
    public Restaurant(String restaurantName, String restaurantAddress, float[] restaurantGPS, String restaurantID) {
        this.restaurantName = restaurantName;
        this.restaurantAddress = restaurantAddress;
        this.restaurantGPS = restaurantGPS;
        this.restaurantID = restaurantID;
        mPosition = new LatLng(restaurantGPS[0],restaurantGPS[1]);
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public List<Inspection> getAllInspections(){
        return allInspections;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    //returns number of critical violations a restaurant had within the last year
    public int getNCriticalLastYear(){
        if(allInspections.size() == 0){
            return 0;
        }

        DatabaseInfo databaseInfo = DatabaseInfo.getInstance();
        Date inspectionDate = allInspections.get(0).getDate();
        Date today = new Date(inspectionDate.getCurrentDateAsString());

        int nCrit = 0;
        for(Inspection inspection: allInspections){
            //check if inspection was within the last year
            if( Math.abs(databaseInfo.dateDifference(today,inspection.getDate())) <=  8760.0){
                nCrit += inspection.getNumCriticalViolations();
            }
        }
        return nCrit;
    }



    @Override
    public Iterator<Inspection> iterator() {
        return allInspections.iterator();
    }

    public void addInspection(Inspection x){
        //add function to add new inspection
        int i = 0;
        while(i < allInspections.size()){
            if(allInspections.get(i) == x){
                return;
            }
            i = i + 1;
        }
        allInspections.add(x);
    }

    public Inspection getInspection(int position){
        //function to get a particular inspection
        return allInspections.get(position);
    }


    public int numOfInspections(){
        return allInspections.size();
    }

    //tostring method used for log - NOT for output to UI (not in strings xml)
    @Override
    public String toString() {
        return "Restaurant{" +
                "restaurantName='" + restaurantName + '\'' +
                ", restaurantAddress='" + restaurantAddress + '\'' +
                ", restaurantGPS=" + Arrays.toString(restaurantGPS) +
                ", restaurantID='" + restaurantID + '\'' +
                ", allInspections=" + allInspections +
                '}';
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public float[] getRestaurantGPS() {
        return restaurantGPS;
    }

    public int sumViolations() {
        int sum = 0;

        if( allInspections.size() > 0) {
            sum = getInspection(0).numOfViolations();
        }
        return sum;
    }

    private void swapPositions(int i, int j){
        Inspection temp = allInspections.get(i);
        allInspections.set(i, allInspections.get(j));
        allInspections.set(j, temp);
    }

    public float getLatitude(){
        return restaurantGPS[0];
    }

    public float getLongitude(){
        return restaurantGPS[1];
    }

    public void sortInspectionsByDate(){
        for(int i = 0; i < allInspections.size();i++){
            for(int j = 0; j < allInspections.size(); j++){
                if(allInspections.get(i) == allInspections.get(j)){
                    continue;
                }
                if(allInspections.get(i).getDate().getYear() > allInspections.get(j).getDate().getYear()){
                    swapPositions(i, j);
                }
                else if (allInspections.get(i).getDate().getYear() == allInspections.get(j).getDate().getYear()){
                    if(allInspections.get(i).getDate().getMonth() > allInspections.get(j).getDate().getMonth()){
                        swapPositions(i, j);
                    }
                    else if(allInspections.get(i).getDate().getMonth() == allInspections.get(j).getDate().getMonth()){
                        if(allInspections.get(i).getDate().getDay() > allInspections.get(j).getDate().getDay()){
                            swapPositions(i, j);
                        }
                    }
                }
            }
        }
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Nullable
    @Override
    public String getTitle() {
        return this.restaurantName;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        isFavourite = favourite;
    }

    @Override
    public int compareTo(Restaurant otherRestaurant) {
        int result = this.restaurantName.compareTo(otherRestaurant.restaurantName);

        if (result < 0) {
            return -1;
        }
        else if (result > 0) {
            return 1;
        }else {
            return 0;
        }
    }
}
