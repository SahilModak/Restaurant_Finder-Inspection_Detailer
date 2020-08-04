package group17.cmpt276.iteration3.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import group17.cmpt276.iteration3.Model.CSV.DatabaseInfo;
import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.R;

public class UpdatedFavouritesActivity extends AppCompatActivity {

    private static final String EXTRA_RESTAURANT_INDEX = "jmt24.cmpt276.group17_iteration1.restaurant_index";
    RestaurantManager manager;
    DatabaseInfo databaseInfo;
    SharedPreferences sharedPreferences;
    private ArrayAdapter<Restaurant> adapter;

    public static Intent makeIntent(Context context) {
        Intent intent =  new Intent(context, UpdatedFavouritesActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updated_favourites);

        manager = RestaurantManager.getInstance();
        populateListView();
//        registerClickCallback();

    }

    private void populateListView() {

        ListView listView = (ListView) findViewById(R.id.favouritesListView);
        if (adapter == null) {
            adapter = new RestaurantListAdapter(this, R.layout.restaurant_view, (ArrayList<Restaurant>) manager.getAllRestaurants());
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

//    // go to the specific restaurant detail page when user click on that restaurant
//    private void registerClickCallback() {
//        ListView listView = (ListView) findViewById(R.id.restaurantListView);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = RestaurantDetailsActivity.makeIntent(UpdatedFavouritesActivity.this, position);
//                startActivity(intent);
//            }
//        });
//    }
}