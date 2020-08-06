package group17.cmpt276.iteration3.Model.CSV;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.File;

import group17.cmpt276.iteration3.Model.Date;

/**
 * Stores metadata obtained from Surrey JSON
 * Stores information about last update and if it is the first time opening app
 * Singleton class
 */

public class DatabaseInfo {
    private static final String TAG = "Database info";
    private static DatabaseInfo databaseInfo;

    //urls from where data can be downloaded
    private String restaurantCSVDataURL;
    private String inspectionCSVDataURL;

    private String restaurantFileName;
    private String inspectionFileName;

    //file names for newly downloaded files
    private String newRestaurantFileName;
    private String newInspectionFileName;

    String firstOpenKey = "firstOpen";
    String lastUpdateKey = "lastUpdate";
    static final String FILESUPDATEDKEY = "filesUpdated";

    private Date lastUpdateDate;
    private Date serverLastUpdate;

    boolean hasAskedForUpdate = false;

    private DatabaseInfo() {
        restaurantFileName = "Rest.csv";
        inspectionFileName = "Inspections.csv";
        newRestaurantFileName = "newRest.csv";
        newInspectionFileName = "newInspections.csv";
    }

    public static DatabaseInfo getInstance(){
        if(databaseInfo == null){
            databaseInfo = new DatabaseInfo();
        }
        return databaseInfo;
    }

    public boolean getHasAskedForUpdate() {
        return hasAskedForUpdate;
    }

    public void setHasAskedForUpdate() {
        hasAskedForUpdate = true;
    }

    public void setServerLastUpdate(Date serverLastUpdate) {
        this.serverLastUpdate = serverLastUpdate;
    }


    public int firstOpen(SharedPreferences sharedPreferences){
        int firstOpen = sharedPreferences.getInt(firstOpenKey,0);
        if(firstOpen == 0){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(firstOpenKey,1);
            editor.apply();
        }
        Log.i(TAG, "firstOpen: " + firstOpen);
        return firstOpen;
    }

    public boolean updateAccepted(SharedPreferences sharedPreferences){
        boolean acceptUpdate = sharedPreferences.getBoolean(FILESUPDATEDKEY,false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FILESUPDATEDKEY,true);
        editor.apply();
        return acceptUpdate;
    }

    public void updateCanceled(SharedPreferences sharedPreferences){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FILESUPDATEDKEY,false);
        editor.apply();
    }

    static public boolean filesUpdated(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(FILESUPDATEDKEY, false);
    }

    //get date of last update from shared prefs
    public void getDataLastUpdate(SharedPreferences sharedPreferences){
        String lastUpdate = sharedPreferences.getString(lastUpdateKey, "2020/06/17T00:00:00");
        Log.i(TAG, "getDataLastUpdate: " + lastUpdate);
        this.lastUpdateDate = new Date(lastUpdate);
    }

    //if update data finished, save last update to shared prefs
    public void setDataLastUpdate(SharedPreferences sharedPreferences){
        String newDataDate = serverLastUpdate.getSaveTag();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(lastUpdateKey,newDataDate);
        editor.apply();
    }

    public boolean needToUpdate(){
        //compare last update with current date
        Date currDate = new Date(lastUpdateDate.getCurrentDateAsString());

        //if over 20 hours, check the surrey server, if there is a new update return true.
        //check
        double timeSinceLastUpdate = dateDifference(currDate,lastUpdateDate);
        if(timeSinceLastUpdate >= 20){
            getMetaData();
            double differenceFromServerUpdate = dateDifference(serverLastUpdate,lastUpdateDate);
            return Math.abs(differenceFromServerUpdate) >= 5;
        }
        else{
            return false;
        }
    }

    //gets the difference between 2 Dates (more recent date, previous date) in hours
    public double dateDifference(Date firstDate, Date lastDate){
        float dateDifference = 0;
        dateDifference += (firstDate.getYear() - lastDate.getYear()) * 8760.0;
        dateDifference += (firstDate.getMonth() - lastDate.getMonth()) * 730.001;
        dateDifference += (firstDate.getDay() - lastDate.getDay()) * 24.0;
        dateDifference += (firstDate.getHour() - lastDate.getHour());
        dateDifference += (firstDate.getMinute() - lastDate.getMinute()) * (1/60.0);
        dateDifference += (firstDate.getSeconds() - lastDate.getSeconds()) * (1/3600.0);
        return  dateDifference;
    }

    //get the metadata from surrey website
    private void getMetaData(){
        JSONRetriever jsonRetriever = new JSONRetriever();
        jsonRetriever.start();
        try{
            jsonRetriever.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //change overwrite old file versions
    public void updateToNewFiles(Context context){
        File oldRestCSV = context.getFileStreamPath(restaurantFileName);
        assert(oldRestCSV != null);
        File newRestCSV = context.getFileStreamPath(newRestaurantFileName);
        newRestCSV.renameTo(oldRestCSV);

        File oldInspectionCSV = context.getFileStreamPath(inspectionFileName);
        File newInspectionCSV = context.getFileStreamPath(newInspectionFileName);
        newInspectionCSV.renameTo(oldInspectionCSV);
    }

    public String getNewRestaurantFileName() {
        return newRestaurantFileName;
    }

    public String getNewInspectionFileName() {
        return newInspectionFileName;
    }

    public String getRestaurantFileName() {
        return restaurantFileName;
    }

    public String getInspectionFileName() {
        return inspectionFileName;
    }

    //where json files containing metadata are stored
    public String getSurreyDataRestaurantsURL() {
        return "http://data.surrey.ca/api/3/action/package_show?id=restaurants";
    }

    public String getGetSurreyDataInspectionURL() {
        return "http://data.surrey.ca/api/3/action/package_show?id=fraser-health-restaurant-inspection-reports";
    }

    public String getRestaurantCSVDataURL() {
        return restaurantCSVDataURL;
    }

    public void setRestaurantCSVDataURL(String restaurantCSVDataURL) {
        this.restaurantCSVDataURL = restaurantCSVDataURL;
    }

    public String getInspectionCSVDataURL() {
        return inspectionCSVDataURL;
    }

    public void setInspectionCSVDataURL(String inspectionCSVDataURL) {
        this.inspectionCSVDataURL = inspectionCSVDataURL;
    }
}