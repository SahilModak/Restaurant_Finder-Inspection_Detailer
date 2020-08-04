package group17.cmpt276.iteration3.UI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.ArrayList;

import group17.cmpt276.iteration3.Model.CSV.DatabaseInfo;
import group17.cmpt276.iteration3.Model.CSV.RestaurantReader;
import group17.cmpt276.iteration3.Model.NewDataNotify;
import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.R;

/*
Display a scrollable list of restaurants with their number of violations and last inspection date.
User may select a restaurant for more information.
 */
public class RestaurantListActivity extends AppCompatActivity{
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
        restaurantManager.setCalledFavourites(false);


        populateListView();
        registerClickCallback();
    }

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
                startActivity(new Intent(this, SearchScreen.class));
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
        ListView listView = (ListView) findViewById(R.id.restaurantListView);
        if (adapter == null) {
            adapter = new RestaurantListAdapter(this, R.layout.restaurant_view, (ArrayList<Restaurant>) restaurantManager.getAllRestaurants());
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        restaurantManager.setCalledFavourites(false);
        NewDataNotify newDataNotify = NewDataNotify.getInstance();
        if(calledSearch && newDataNotify.isNewData()){
            Log.i(TAG, "onResume: need to refresh data");
            adapter.addAll(restaurantManager.getAllRestaurants());
            adapter.notifyDataSetChanged();
            calledSearch = false;
            newDataNotify.setNewData(false);
        }
        populateListView();
    }

    // go to the specific restaurant detail page when user click on that restaurant
    private void registerClickCallback() {
        ListView listView = (ListView) findViewById(R.id.restaurantListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = RestaurantDetailsActivity.makeIntent(RestaurantListActivity.this, position);
                startActivity(intent);
            }
        });
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, RestaurantListActivity.class);
    }
}