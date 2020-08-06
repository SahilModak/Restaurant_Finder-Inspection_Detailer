package group17.cmpt276.iteration3.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import group17.cmpt276.iteration3.Model.Date;
import group17.cmpt276.iteration3.Model.Favourite;
import group17.cmpt276.iteration3.Model.Inspection;
import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.R;


/**
 * Display name, address and gps coordinates of selected restaurant, along with
 * a scrollable list of inspections. User may select an inspection for more information.
 */
public class RestaurantDetailsActivity extends AppCompatActivity {

    private static final String EXTRA_RESTAURANT_INDEX = "jmt24.cmpt276.group17_iteration1.restaurant_index";
    public static final String CODE = "code";
    public static final String RESTAURANTINDEX = "restindex";
    private List<Inspection> inspectionsList;
    private int currentRestaurantIndex;
    private Restaurant restaurant;
    private ArrayAdapter<Inspection> adapter;
    private List<Favourite> favList;

    public static Intent makeIntent(Context context, int restaurantIndex) {
        Intent intent =  new Intent(context, RestaurantDetailsActivity.class);
        intent.putExtra(EXTRA_RESTAURANT_INDEX, restaurantIndex);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        setToolBar();
        getRestaurantDetails();
        updateView();
        populateListView();
        loadFromFavourites();
        setupFavouriteButton();
        registerClickCallback();
    }

    private void setupFavouriteButton() {
        final ImageButton favouriteButton = (ImageButton) findViewById(R.id.btnFavourite);
        if (restaurant.isFavourite()) {
            favouriteButton.setImageResource(android.R.drawable.btn_star_big_on);
        }

        favouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isFavourite = restaurant.isFavourite();
                if (isFavourite) {
                    favouriteButton.setImageResource(android.R.drawable.btn_star_big_off);
                    restaurant.setFavourite(false);
                    updateFavourites(false);
                } else {
                    favouriteButton.setImageResource(android.R.drawable.btn_star_big_on);
                    restaurant.setFavourite(true);
                    updateFavourites(true);
                }
            }
        });

    }

    /*
        Saves/Removes restaurantID to/from list of Favourites,
         converts list to Json and saves to SavedPreferences
        Adapted from https://codinginflow.com/tutorials/android/save-arraylist-to-sharedpreferences-with-gson
     */
    private void updateFavourites(boolean isFav) {
        Date defaultDate;
        String currentDate = "";
        Favourite fave;

        if (restaurant.getAllInspections().size() == 0) {
            defaultDate = new Date(Date.getCurrentDateAsString(currentDate));
            fave = new Favourite(restaurant.getRestaurantID(), defaultDate);
        } else {
            fave = new Favourite(restaurant.getRestaurantID(), restaurant.getInspection(0).getDate());
        }

        if(isFav) {
            favList.add(fave);
        } else {
            favList.remove(fave);
        }

        SharedPreferences prefs = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favList);
        editor.putString("favourite list", json);
        editor.apply();
    }

    /*
        Gets list of favourites from SharedPreferences and converts it from json to ArrayList<String>
        Adapted from https://codinginflow.com/tutorials/android/save-arraylist-to-sharedpreferences-with-gson
     */
    private void loadFromFavourites() {
        SharedPreferences prefs = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("favourite list", null);
        Type type = new TypeToken<ArrayList<Favourite>>() {}.getType();
        favList = gson.fromJson(json, type);

        if (favList == null) {
            favList = new ArrayList<>();
        }
    }

    private void setToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar_restaurant_detail);
        setSupportActionBar(toolbar);
    }

    private void getRestaurantDetails(){
        currentRestaurantIndex = getIntent().getIntExtra(EXTRA_RESTAURANT_INDEX, 0);
        RestaurantManager restaurantManager = RestaurantManager.getInstance();
        restaurant = restaurantManager.getRestaurant(currentRestaurantIndex);
        inspectionsList = restaurant.getAllInspections();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_restaurant_detail, menu);
        return true;
    }

    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_restaurant_detail_back:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateView() {
        TextView txtRestaurantName = findViewById(R.id.txtRestaurantName);
        txtRestaurantName.setText(getString(R.string.name,restaurant.getRestaurantName()));

        TextView txtRestaurantAddress = findViewById(R.id.txtRestaurantAddress);
        txtRestaurantAddress.setText(getString(R.string.address,restaurant.getRestaurantAddress()));

        TextView txtRestaurantGPS =  findViewById(R.id.txtRestaurantGPS);
        txtRestaurantGPS.setText(getString(R.string.gps, restaurant.getRestaurantGPS()[0], restaurant.getRestaurantGPS()[1]));
        txtRestaurantGPS.setTextColor(Color.BLUE);
        txtRestaurantGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = switchToMap(RestaurantDetailsActivity.this, currentRestaurantIndex);
                startActivityForResult(intent,42);
            }
        });
    }

    private void populateListView() {
        ListView list = findViewById(R.id.inspectionListView);

        if (adapter == null) {
            adapter = new InspectionListAdapter();
            list.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /*
    Custom adapter to provide a view for the Inspection ListView
    Returns a view for each Inspection in a collection of Inspection objects
     */
    private class InspectionListAdapter extends ArrayAdapter<Inspection> {
        public InspectionListAdapter() {
            super(RestaurantDetailsActivity.this, R.layout.inspection_view, inspectionsList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View inspectionView = convertView;
            if (inspectionView == null) {
                inspectionView = getLayoutInflater().inflate(R.layout.inspection_view, parent, false);
            }
            Inspection currentInspection = inspectionsList.get(position);
            setupInspectionView(currentInspection, inspectionView);

            return inspectionView;
        }
    }

    // setup each inspection in the list
    private void setupInspectionView (Inspection current, View inspectionView){
        ImageView imageHazard =inspectionView.findViewById(R.id.iconHazard);
        setupHazardLevel(current,imageHazard);

        TextView txtNumCrit =  inspectionView.findViewById(R.id.txtNumCrit);
        txtNumCrit.setText(getString(R.string.num_crit, current.getNumCriticalViolations()));

        TextView txtNumNonCrit = inspectionView.findViewById(R.id.txtNumNonCrit);
        txtNumNonCrit.setText(getString(R.string.num_non_crit, current.getNumNonCriticalViolations()));

        TextView txtTimeSince =  inspectionView.findViewById(R.id.txtTimeSinceInspection);
        txtTimeSince.setText(getString(R.string.time_since_inspection,
                current.getDate().makeActivityString(this)));

    }

    // setup the icon for the hazard level base on the different level that reads from CSV file
    private void setupHazardLevel(Inspection current, ImageView imageView){
        switch (current.getHazardLevel()){
            case "Low":
                imageView.setImageResource(R.drawable.ic_warning_low);break;
            case "Moderate":
                imageView.setImageResource(R.drawable.ic_warning_moderate);break;
            case "High":
               imageView.setImageResource(R.drawable.ic_warning_critical);break;
        }
    }

    // goes to the specific inspection base on which inspection did user click
    private void registerClickCallback() {
        ListView listView = findViewById(R.id.inspectionListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = InspectionDetail.makeIntent(RestaurantDetailsActivity.this, currentRestaurantIndex, position);
                startActivity(intent);
            }
        });
    }


    public static Intent switchToMap (Context c, int pos){
        Intent i =new Intent(c, MapsActivity.class);
        int code =42;
        i.putExtra(CODE,code);
        i.putExtra(RESTAURANTINDEX, pos);
        return i;
    }
}