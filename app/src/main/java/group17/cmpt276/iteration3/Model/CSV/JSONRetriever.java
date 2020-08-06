package group17.cmpt276.iteration3.Model.CSV;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import group17.cmpt276.iteration3.Model.Date;

/**
 * JSON retriever class.
 * Handles downloading JSON files as string and extracting the relevant JSON objects to be used by the CSV downloader
 */
public class JSONRetriever extends Thread{
    private static final String TAG = "JSONRetriever" ;
    private DatabaseInfo databaseInfo;

    public JSONRetriever(){
    }

    public void run(){
        databaseInfo = DatabaseInfo.getInstance();
        try {
            parseRestaurantJSON(getJSONFromURL(databaseInfo.getSurreyDataRestaruantsURL()));
            parseInspectionJSON(getJSONFromURL(databaseInfo.getGetSurreyDataInspectionURL()));
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //go thru json objects from downloaded json string to find url
    private void parseInspectionJSON(String inpsectionURL) throws JSONException {
        try{
            JSONObject obj = new JSONObject(inpsectionURL);
            JSONObject jar = obj.getJSONObject("result");
            JSONArray jsonArray = jar.getJSONArray("resources");
            JSONObject JO_1 = (JSONObject) jsonArray.get(0);
            String inspURL = JO_1.getString("url");
            Log.i(TAG, "parseInspectionJSON: ispectionurl: "  + inspURL);
            databaseInfo.setInspectionCSVDataURL(inspURL);
        }
        catch(JSONException je){
            je.printStackTrace();
        }
    }

    //go through json objects to find modified date, format and url of restaurant data
    private void parseRestaurantJSON(String restaurantURL) throws JSONException {
        try {
            JSONObject obj = new JSONObject(restaurantURL);
            JSONObject jar = obj.getJSONObject("result");
            JSONArray jsonArray = jar.getJSONArray("resources");
            JSONObject JO_1 = (JSONObject) jsonArray.get(0);
            String restURL = JO_1.getString("url");
            Log.i(TAG, "run: restURL: " + restURL);

            // get format
            String format = JO_1.getString("format");
            Log.i(TAG, "run: format: " + format);

            // get last modified
            String date = JO_1.getString("last_modified");
            Log.i(TAG, "run: last modified: " + date);
            Date lastServerUpdate = new Date(date);

            databaseInfo.setServerLastUpdate(lastServerUpdate);
            databaseInfo.setRestaurantCSVDataURL(restURL);
        }
        catch(JSONException je){
            je.printStackTrace();
        }
    }

    //get a string representation of json data from a url
    private String getJSONFromURL(String inURL){
        StringBuilder response = new StringBuilder();
        try{
            URL url = new URL(inURL);
            //check connection
            BufferedReader input = new BufferedReader(new InputStreamReader(url.openStream()));

            String line = null;
            while ((line = input.readLine()) != null) {
                response.append(line);
            }

            input.close();
            return response.toString();
        }
        catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return null;
    }
}
