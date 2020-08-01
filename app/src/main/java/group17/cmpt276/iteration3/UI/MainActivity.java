package group17.cmpt276.iteration3.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import group17.cmpt276.iteration3.Model.CSV.DatabaseInfo;
import group17.cmpt276.iteration3.Model.CSV.RestaurantReader;
import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.R;

/*
Display a scrollable list of restaurants with their number of violations and last inspection date.
User may select a restaurant for more information.
 */
public class MainActivity extends AppCompatActivity{
    private static final String TAG = "Main";
    public static final int REQUEST_CODE_FOR_UPDATE = 42;
    RestaurantManager restaurantManager;
    DatabaseInfo databaseInfo;
    SharedPreferences sharedPreferences;

    private ArrayAdapter<Restaurant> adapter;
    private boolean calledSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: created");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseInfo = DatabaseInfo.getInstance();
        sharedPreferences = this.getSharedPreferences("shared preferences",MODE_PRIVATE);

        try {
            getRestaurantManager();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        populateListView();
        registerClickCallback();
    }


//    @Override
//    protected void onResume() {
//        super.onResume();
//        adapter.notifyDataSetChanged();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FOR_UPDATE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        restaurantManager.getAllRestaurants().clear();
                        restaurantManager.setFlagTrue();
                        getRestaurantManager();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    // do nothing
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.item_menu_map:
                startActivity(new Intent(this, MapsActivity.class));
                finish();
                return true;
            case R.id.item_menu_search:
                calledSearch = true;
                startActivity(new Intent(this, OptionsScreen.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRestaurantManager() throws FileNotFoundException {
        restaurantManager = RestaurantManager.getInstance();
        if(restaurantManager.getFlag()){
            loadFromCSV(this);
            restaurantManager.sortByRestaurantName();
            restaurantManager.setFlagFalse();
            restaurantManager.sortAllRestaurantsInspections();
        }
    }

    //loads restaurant data from a csv file (either stored locally or pre-installed)
    private void loadFromCSV(Context context) throws FileNotFoundException {
        RestaurantReader csvReader = new RestaurantReader();

        SharedPreferences sharedPreferences = this.getSharedPreferences("shared preferences",MODE_PRIVATE);
        //first time install, set the reader to old style reader

        boolean hasUpdated = DatabaseInfo.filesUpdated(this);

        InputStream restFileStream = null;
        InputStream inspFileStream = null;

        //first time install, set the reader to old dada from iteration 1
        if(databaseInfo.firstOpen(sharedPreferences) == 0 || !hasUpdated){
            restFileStream = getResources().openRawResource(R.raw.restaurants_itr1);
            inspFileStream = getResources().openRawResource(R.raw.inspectionreports_itr1);
            Log.i(TAG, "loadFromCSV: reading default rests");
        }
        else{   //use previously downloaded files
            try {
                restFileStream = context.openFileInput(databaseInfo.getRestaurantFileName());
                inspFileStream = context.openFileInput(databaseInfo.getInspectionFileName());

            } catch (IOException e) {
                Log.i(TAG, "onCreate: caught exception");
                e.printStackTrace();
            }
        }

        Log.i(TAG, "onCreate: created csvreader");
        try {
            assert restFileStream != null;
            Reader readerR = new InputStreamReader(restFileStream);
            csvReader.readRestaurantCSV(readerR);
            readerR.close();

            assert inspFileStream != null;
            Reader readerI = new InputStreamReader(inspFileStream);
            csvReader.readInspectionCSV(readerI,this);
            readerI.close();
        } catch (IOException e) {
            Log.i(TAG, "onCreate: caught exception");
            e.printStackTrace();
        }

    }

    private void populateListView() {
        adapter = new listAdapter();
        ListView listView = (ListView) findViewById(R.id.restaurantListView);
        listView.setAdapter(adapter);
    }

    /*
    Custom adapter to provide a view for the Restaurant ListView
    Returns a view for each Restaurant in a collection of Restaurant objects
     */
    private class listAdapter extends ArrayAdapter<Restaurant> {
        public listAdapter() {
            super(MainActivity.this, R.layout.restaurant_view, restaurantManager.getAllRestaurants());
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View restaurantView = convertView;
            if (restaurantView == null) {
                restaurantView = getLayoutInflater().inflate(R.layout.restaurant_view, parent, false);
            }

            Restaurant currentRestaurant = restaurantManager.getRestaurant(position);
            setupRestaurantView(currentRestaurant, restaurantView);
            return restaurantView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(calledSearch){
            adapter.clear();
            adapter.addAll(restaurantManager.getAllRestaurants());
            adapter.notifyDataSetChanged();
            calledSearch = false;
        }
    }

    // setup each restaurant view in the list
    private void setupRestaurantView (Restaurant current, View restaurantView){
        ImageView imageRestaurant = (ImageView) restaurantView.findViewById(R.id.iconRestaurant);
        imageRestaurant.setImageResource(R.drawable.ic_baseline_fastfood_24);
        setupRestaurantIcon(imageRestaurant,current);

        ImageView imageHazard = (ImageView) restaurantView.findViewById(R.id.iconWarning);
        setupHazardLevel(imageHazard,current);

        TextView txtName = (TextView) restaurantView.findViewById(R.id.txtName);
        txtName.setText(current.getRestaurantName());

        TextView txtNumViolations = (TextView) restaurantView.findViewById(R.id.txtNumViolations);
        txtNumViolations.setText(getString(R.string.num_violations, current.sumViolations()));

        TextView txtTimeSinceInspection = (TextView) restaurantView.findViewById(R.id.txtInspectionDate);
        setupInspectionTime(txtTimeSinceInspection,current);

        if (current.isFavourite()) {
            restaurantView.setBackgroundColor(Color.YELLOW);
        }
    }

    private void setupRestaurantIcon(ImageView imageRestaurant,Restaurant current){
        setupPortion1(imageRestaurant, current.getRestaurantName());
        setupPortion2(imageRestaurant,current.getRestaurantName());
    }

    private void setupPortion1(ImageView imageRestaurant, String restaurantName){
        if(restaurantName.contains("A&W")){
            //https://logos-download.com/?s=A%26W
            imageRestaurant.setImageResource(R.drawable.logo_aw);
            Log.i(TAG, "restaurant is A&W");
        }
        else if(restaurantName.contains("McDonald's")){
            //https://logos-download.com/?s=McDonald
            imageRestaurant.setImageResource(R.drawable.logo_mcdonald);
            Log.i(TAG, "restaurant is McDonald's");
        }
        else if(restaurantName.contains("Starbucks Coffee")){
            //https://logos-download.com/?s=starbucks
            imageRestaurant.setImageResource(R.drawable.logo_starbucks);
            Log.i(TAG, "restaurant is Starbucks");
        }
        else if(restaurantName.contains("Tim Hortons")){
            //https://logos-download.com/?s=tim+hortons
            imageRestaurant.setImageResource(R.drawable.logo_tim_hortons);
            Log.i(TAG, "restaurant is Time Hortons");
        }
        else if(restaurantName.contains("Safeway")){
            //https://logos-download.com/?s=safeway
            imageRestaurant.setImageResource(R.drawable.logo_safeway);
            Log.i(TAG, "restaurant is safeway");
        }
    }

    private void setupPortion2(ImageView imageRestaurant, String restaurantName){
        if(restaurantName.contains("KFC")){
            //https://logos-download.com/?s=KFC
            imageRestaurant.setImageResource(R.drawable.logo_kfc);
            Log.i(TAG, "restaurant is kfc");
        }
        else if(restaurantName.contains("Burger King")){
            //https://logos-download.com/?s=burger+King
            imageRestaurant.setImageResource(R.drawable.logo_burger_king);
            Log.i(TAG, "restaurant is burger king");
        }
        else if(restaurantName.contains("Pizza Hut")){
            //https://logos-download.com/?s=pizza+Hut
            imageRestaurant.setImageResource(R.drawable.logo_pizza_hut);
            Log.i(TAG, "restaurant is pizza hut");
        }
        else if(restaurantName.contains("7-Eleven")){
            //https://logos-download.com/?s=7-eleven
            imageRestaurant.setImageResource(R.drawable.logo_7_eleven);
            Log.i(TAG, "restaurant is 7-eleven");
        }
        else if(restaurantName.contains("Blenz coffee")){
            //https://www.vcc.ca/services/eat-shop--more/blenz/
            imageRestaurant.setImageResource(R.drawable.logo_blenz_coffee);
            Log.i(TAG, "restaurant is blenz coffee");
        }
    }

    //setup the hazard level icon base on the latest inspection. If there's no inspection, shows the nonInspection icon
    private void setupHazardLevel(ImageView imageHazard,Restaurant current){
        imageHazard.setImageResource(R.drawable.ic_inspection_empty);
        if(current.numOfInspections()!= 0){
            switch (current.getInspection(0).getHazardLevel()){
                case "Low":
                    imageHazard.setImageResource(R.drawable.ic_warning_low);break;
                case "Moderate":
                    imageHazard.setImageResource(R.drawable.ic_warning_moderate);break;
                case "High":
                    imageHazard.setImageResource(R.drawable.ic_warning_critical);
            }
        }
    }

    // show the latest inspection date for each restaurant, if the restaurant has no inspection, shows no inspection date
    private void setupInspectionTime(TextView txtTimeSinceInspection, Restaurant current){
        if (current.numOfInspections() < 1) {
            txtTimeSinceInspection.setText(R.string.no_inspection_date);
        }
        else {
            txtTimeSinceInspection.setText(getString(R.string.Inspection_Time,
                    current.getInspection(0).
                            getDate().makeActivityString(this)));
        }
    }

    // go to the specific restaurant detail page when user click on that restaurant
    private void registerClickCallback() {
        ListView listView = (ListView) findViewById(R.id.restaurantListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = RestaurantDetailsActivity.makeIntent(MainActivity.this, position);
                startActivity(intent);
            }
        });
    }
}