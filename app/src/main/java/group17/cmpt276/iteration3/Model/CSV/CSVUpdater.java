package group17.cmpt276.iteration3.Model.CSV;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

/**
 * Download and updates the saved csv files
 */

public class CSVUpdater{
    Context context;

    public CSVUpdater(Context context){
        this.context = context;
    }

    //downloads a csv file from a given url to a given saveName location
    private void downloadCSVFromURL(String urlIn, String fileName){
        try {
            //get the url
            URL url = new URL(urlIn);

            //get a buffered reader
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String buffer = "";
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            while ((buffer = reader.readLine()) != null) {
                buffer += '\n';
                outputStreamWriter.write(buffer); //write to file
            }
            outputStreamWriter.close();
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(Context context) {
        DatabaseInfo databaseInfo = DatabaseInfo.getInstance();
        //download csvs from online to new files, overwrite files later.
        downloadCSVFromURL(databaseInfo.getRestaurantCSVDataURL(), databaseInfo.getNewRestaurantFileName());
        downloadCSVFromURL(databaseInfo.getInspectionCSVDataURL(), databaseInfo.getNewInspectionFileName());
        databaseInfo.updateToNewFiles(context);
    }
}