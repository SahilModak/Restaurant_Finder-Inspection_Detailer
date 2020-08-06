package group17.cmpt276.iteration3.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.R;

/**
 * Display a scrollable list of favourite restaurants with their number of violations
 * and last inspection date. User may select a favourite restaurant for more information.
 */
public class UpdatedFavouritesActivity extends AppCompatActivity {

    RestaurantManager manager;
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
        manager.sortByRestaurantName();
        populateListView();
        registerClickCallback();

    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.setCalledFavourites(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

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

    // go to the specific restaurant detail page when user click on that restaurant
    private void registerClickCallback() {
        ListView listView = (ListView) findViewById(R.id.favouritesListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = RestaurantDetailsActivity.makeIntent(UpdatedFavouritesActivity.this, position);
                manager.setCalledFavourites(true);
                startActivity(intent);
            }
        });
    }
}