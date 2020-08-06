package group17.cmpt276.iteration3.Model.CSV;
import android.content.Context;
import android.util.Log;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.StringTokenizer;
import group17.cmpt276.iteration3.Model.Inspection;
import group17.cmpt276.iteration3.Model.Date;
import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.Model.Violation;

/**
 * Restaurant Reader Class,
 * Takes a file streams from activity, parses and creates new model objects from CSV files
 *
 * CSV reading adapted from
 * https://www.callicoder.com/java-read-write-csv-file-opencsv/
 */

public class RestaurantReader {
    private static final String TAG = "CSV reader";
    RestaurantManager restaurantManager;

    public RestaurantReader(){}

    //parse restaurant csv, add restaurants to list
    public void readRestaurantCSV(Reader reader) throws IOException {
        try (
                CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()
        ) {
            String[] nextRecord;

            int i = 0;
            while ((nextRecord = csvReader.readNext()) != null) {
                float[] gps = new float[] {Float.parseFloat(nextRecord[5]),Float.parseFloat(nextRecord[6])};
                Restaurant restaurant = new Restaurant(nextRecord[1],nextRecord[2]+", "+nextRecord[3],
                        gps,nextRecord[0].trim());

                restaurantManager = RestaurantManager.getInstance();
                restaurantManager.addRestaurant(restaurant);
                //logRestaurantRead(nextRecord, i);
                i++;
            }
        }
        catch (FileNotFoundException fnf){
            Log.i(TAG, "readRestaurantCSV: file not found!");
            fnf.printStackTrace();
        }
        catch (IOException ioe){
            Log.i(TAG, "readRestaurantCSV: IOE exception");
            ioe.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }
    }

    //parse inspection csv, add inspections to
    public void readInspectionCSV(Reader reader, Context context) throws IOException {
        try (
                CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build()
        ) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                //logInspectionRead(nextRecord);

                if(!nextRecord[1].equals("")){
                    String x = nextRecord[1];
                    Date date = new Date(Integer.parseInt(x.substring(0,4)),
                            Integer.parseInt(x.substring(4,6)),
                            Integer.parseInt(x.substring(6,8)),context);

                    int numCriticalVio = Integer.parseInt(nextRecord[3]);
                    int numNonCriticalVio = Integer.parseInt(nextRecord[4]);

                   //InspectionDate inspectionDate, String inspectionType, int numCriticalViolations, int numNonCriticalViolations, String hazardLevel)
                    Inspection inspection = new Inspection(date, nextRecord[2], numCriticalVio,numNonCriticalVio, nextRecord[6].trim());

                    restaurantManager = RestaurantManager.getInstance();
                    Restaurant inspectedRestaurant = restaurantManager.searchById(nextRecord[0].trim());
                    if(inspectedRestaurant == null){
                        Log.i(TAG, "readInspectionCSV: bad find restaurant id: " + nextRecord[0].trim() );
                    }
                    else{
                        inspectedRestaurant.addInspection(inspection);
                        if(numCriticalVio + numNonCriticalVio > 0 ){
                            addViolations(inspection,nextRecord[5],context);
                        }
                    }
                }else{
                    Log.i(TAG, "readInspectionCSV: found empty inspection");
                }
            }
        }
        catch (FileNotFoundException fnf){
            Log.e(TAG, "readRestaurantCSV: file not found!");
            fnf.printStackTrace();
        }
        catch (IOException ioe){
            Log.e(TAG, "readRestaurantCSV: IOE exception");
            ioe.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();

        }
    }

    //add a parsed violation to inspection
    private void addViolations(Inspection inspection, String violationList, Context context){
        StringTokenizer tokenizer = new StringTokenizer(violationList,"|");
        while(tokenizer.hasMoreTokens()){
            StringTokenizer tokenizer2 = new StringTokenizer(tokenizer.nextToken(),",");
            String[] violationString = new String[4];
            int i = 0;
            while(tokenizer2.hasMoreTokens() && i < 4){
                violationString[i] = tokenizer2.nextToken();
                i ++;
            }
            boolean isCrit = false;
            if(violationString[1].equals("Critical")){
                isCrit = true;
            }
            //boolean isCritical, String description, int violationNum
            Violation violation = new Violation(isCrit,violationString[2],Integer.parseInt(violationString[0]),context);
            inspection.addViolation(violation);
        }
    }

    //log adding inspection, error checking
    private void logInspectionRead(String[] nextRecord){
        //todo: extract the violations, ?? add to string? parsed later
        //"TrackingNumber","InspectionDate","InspType","NumCritical","NumNonCritical","HazardRating","ViolLump"
        Log.i(TAG, "readInspectionCSV: " + "TRACKINGNUMBER : " + nextRecord[0]);
        Log.i(TAG, "readInspectionCSV: " + "InspectionDate : " + nextRecord[1]);
        Log.i(TAG, "readInspectionCSV: " + "InspType : " + nextRecord[2]);
        Log.i(TAG, "readInspectionCSV: " + "NumCritical : " + nextRecord[3]);
        Log.i(TAG, "readInspectionCSV: " + "NumNonCritical: " + nextRecord[4]);
        Log.i(TAG, "readInspectionCSV: " + "HazardRating: " + nextRecord[6]);
        Log.i(TAG, "readInspectionCSV: " + "ViolLump: " + nextRecord[5]);
        Log.i(TAG, "readInspectionCSV: " + "==========================");
    }

    //log adding of rest from csv, error checking
    private void logRestaurantRead(String[] nextRecord, int index) {
        Log.i(TAG, "logRestaurantRead: testing exrtra adds!!!!!");
        Log.i(TAG, "readRestaurantCSV: length" + nextRecord.length);
        Log.i(TAG, "readRestaurantCSV: " + "TRACKINGNUMBER : " + nextRecord[0]);
        Log.i(TAG, "readRestaurantCSV: " + "NAME : " + nextRecord[1]);
        Log.i(TAG, "readRestaurantCSV: " + "PHYSICALADDRESS : " + nextRecord[2]);
        Log.i(TAG, "readRestaurantCSV: " + "PHYSICALCITY : " + nextRecord[3]);
        Log.i(TAG, "readRestaurantCSV: " + "FACTYPE: " + nextRecord[4]);
        Log.i(TAG, "readRestaurantCSV: " + "Lat: " + nextRecord[5]);
        Log.i(TAG, "readRestaurantCSV: " + "Long: " + nextRecord[6]);
        Log.i(TAG, "readRestaurantCSV: " + "==========================");
        Log.i(TAG, "readRestaurantCSV: num restaurants" + restaurantManager.numOfRestaurants());
        Log.i(TAG, "readRestaurantCSV: restaurant " + index + " :" + restaurantManager.getRestaurant(index));
    }
}
