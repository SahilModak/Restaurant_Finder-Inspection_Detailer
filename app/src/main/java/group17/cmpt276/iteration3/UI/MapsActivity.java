package group17.cmpt276.iteration3.UI;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import group17.cmpt276.iteration3.Model.CSV.DatabaseInfo;
import group17.cmpt276.iteration3.Model.CSV.RestaurantReader;
import group17.cmpt276.iteration3.Model.Favourite;
import group17.cmpt276.iteration3.Model.NewDataNotify;
import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.R;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        ClusterManager.OnClusterItemInfoWindowClickListener,
        ClusterManager.OnClusterItemClickListener<Restaurant>,
        ClusterManager.OnClusterClickListener,
        ClusterManager.OnClusterInfoWindowClickListener,
        UpdateDialog.UpdateDialogListener{


    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final int LOCATION_UPDATE_INTERVAL = 3000000;
    private static float newLatitude = 9999;
    private static float newLongitude = 9999;

    private ClusterManager<Restaurant> mClusterManager;
    private boolean flag = false;
    private boolean camflag;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Handler mapHandler = new Handler();
    private Runnable mapRunnable;
    private Location currentLocation;
    private RestaurantManager manager = RestaurantManager.getInstance();
    private Marker marker;
    private boolean calledSearch = false;

    public static final int REQUEST_CODE_FOR_UPDATE = 42;
    private DatabaseInfo databaseInfo;
    SharedPreferences sharedPreferences;
    private List<Favourite> favList;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        databaseInfo = DatabaseInfo.getInstance();
        sharedPreferences = this.getSharedPreferences("shared preferences",MODE_PRIVATE);
        camflag = true;
        getLocPermissions();
        getToRestaurantList();
        startSearchInMap();

        //check for updates
        if(!databaseInfo.getHasAskedForUpdate()) {
            try {
                updateCheck();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            getRestaurantManager();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        loadFromFavourites();
        resetFavourites();
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

    // sets previously favourited restaurants isFavourite to true for subsequent launches
    private void resetFavourites() {
        for (int i = 0; i < favList.size(); i++) {
            Restaurant restaurant = manager.searchById(favList.get(i).getID());
            if (restaurant != null ) {
                restaurant.setFavourite(true);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void  updateCheck() throws InterruptedException {
        databaseInfo.getDataLastUpdate(sharedPreferences);
        if(databaseInfo.needToUpdate()){
            showUpdateDialog();
        }
    }

    // Create and show dialog that prompts user for update
    public void showUpdateDialog() {
        Log.i(TAG, "showUpdateDialog: showing update dialog");
        DialogFragment dialog = new UpdateDialog();
        dialog.show(getSupportFragmentManager(), "UpdateDialog");
        databaseInfo.setHasAskedForUpdate();
    }

    // modify result of positive click in UpdateDialog
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        SharedPreferences prefs = this.getSharedPreferences("shared preferences", MODE_PRIVATE);
        databaseInfo.updateAccepted(prefs);

        Intent intent = UpdateActivity.makeIntent(MapsActivity.this);
        startActivityForResult(intent, REQUEST_CODE_FOR_UPDATE);
    }

    // modify result of negative click in UpdateDialog
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        dialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_FOR_UPDATE:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        manager.getAllRestaurants().clear();
                        manager.setFlagTrue();
                        getRestaurantManager();
                        setmClusterManager();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    resetFavourites();
                    checkFavouritesForUpdates();
                }
        }
    }

    private void checkFavouritesForUpdates() {
        List<Restaurant> faveRestaurants = new ArrayList<>();
        Restaurant rest;
        for (Favourite fave : favList) {
            rest = manager.searchById(fave.getID());
            if (rest != null && fave.getDateLastInspection() != rest.getInspection(0).getDate()) {
                faveRestaurants.add(rest);
            }
        }

        if (faveRestaurants.size() > 0) {
            manager.setFavesWithUpdates(faveRestaurants);
//            manager.setCalledFavourites(true);
            // TODO: new activty to display  updated favourites
            showFavouriteDialog();
        }

    }

    // Create and show dialog that prompts user for update
    public void showFavouriteDialog() {
        Log.i(TAG, "showFavouriteDialog: showing favourite dialog");
        DialogFragment dialog = new FavouriteDialog();
//        manager.setCalledFavourites(true);
        dialog.show(getSupportFragmentManager(), "FavouriteDialog");
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getRestaurantManager() throws FileNotFoundException {
        manager = RestaurantManager.getInstance();
        if(manager.getFlag()){
            loadFromCSV(this);
            manager.sortByRestaurantName();
            manager.setFlagFalse();
            manager.sortAllRestaurantsInspections();
        }
    }

    //loads restaurant data from a csv file (either stored locally or pre-installed)
    @RequiresApi(api = Build.VERSION_CODES.O)
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

        //create csv reader
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

    private void getToRestaurantList() {
        ImageButton imageButton = findViewById(R.id.IB_restaurant_List);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RestaurantListActivity.makeIntent(MapsActivity.this);
                startActivity(intent);
                finish();
            }
        });
    }

    //starts the search activity from within map
    private void startSearchInMap(){
        ImageButton imageButton = findViewById(R.id.IB_search_in_map);
        imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                calledSearch = true;
                startActivity(new Intent(MapsActivity.this, SearchScreen.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        NewDataNotify newDataNotify = NewDataNotify.getInstance();
        if(calledSearch && newDataNotify.isNewData()){
            refreshItems();
            calledSearch = false;
            mMap.animateCamera(CameraUpdateFactory.zoomTo((float) (mMap.getCameraPosition().zoom - 0.25)));
            newDataNotify.setNewData(false);
        }
    }

    //refreshes the markers on the map
    private void refreshItems(){
        mMap.clear();
        mClusterManager.clearItems();  // calling just in case (may not be needed)
        mClusterManager.addItems(manager.getAllRestaurants());
    }

    //sets up the cluster manager
    private void setmClusterManager(){
        mClusterManager = new ClusterManager<>(this,mMap);
        //todo: set sorted restaurants here
        mClusterManager.addItems(manager.getAllRestaurants());
        RestaurantMarkerRenderer renderer = new RestaurantMarkerRenderer(this,mMap,mClusterManager);
        mClusterManager.setRenderer(renderer);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.getMarkerCollection().setInfoWindowAdapter( new CustomWindowAdapter(this));
        mClusterManager.getClusterMarkerCollection().setInfoWindowAdapter(new CustomWindowAdapter(this));
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Map is ready");
        mMap = googleMap;
        setmClusterManager();

        if (flag) {
            updateLocation();
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
        }

        mMap.setInfoWindowAdapter(new CustomWindowAdapter(MapsActivity.this));
    }



    private void getLocPermissions() {
        Log.d(TAG, "Getting Location Permissions");
        String[] permissions = {FINE_LOCATION, COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                flag = true;
                initializeMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case (REQUEST_CODE): {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            flag = false;
                            Log.d(TAG, "Permission Denied");
                            return;
                        }
                    }
                    flag = true;
                    Log.d(TAG, "All permissions allowed");
                    initializeMap();
                }
            }
        }
    }

    private void initializeMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        Log.d(TAG, "Initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void updateLocation() {
        Log.d(TAG, "getting current location");
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (flag) {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Device Location Found");
                            currentLocation = (Location) task.getResult();
                            changeLocations();
                            if(camflag) {
                                camflag = false;
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), DEFAULT_ZOOM);
                            }
                        }
                        else {
                            Log.d(TAG, "Device Location Not Found");
                            Toast.makeText(MapsActivity.this,
                                    "unable to get device location, please check your permissions",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getLocation(): Security Exception" + e.getMessage());
        }
    }

    private void moveCamera(LatLng location, float zoomFactor) {
        Log.d(TAG, "moveCamera: moving camera to: " +
                "lat: " + location.latitude +
                "lng: " + location.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomFactor));
    }


    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void startLocationRunnable(){
        Log.d(TAG, "startsLocationRunnable: starting runnable for retrieving updated device location");
        mapHandler.postDelayed(mapRunnable = new Runnable() {
            @Override
            public void run() {
                updateLocation();
                mapHandler.postDelayed(mapRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationRunnable(){
        mapHandler.removeCallbacks(mapRunnable);
    }

    @Override
    public void onStart(){
        super.onStart();
        startLocationRunnable();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopLocationRunnable();
    }

    private void changeLocations(){
        getCoord();// get coordinates from restaurant detail activity
        if(newLatitude!= 9999 &&  newLongitude!= 9999){ // check whether latitude and longitude is modified by intent
            currentLocation.setLatitude(newLatitude);   //modify the current location if we get new coordinate
            currentLocation.setLongitude(newLongitude);
            LatLng latLng = new LatLng(newLatitude,newLongitude);
            Intent intent = getIntent();

            int pos = intent.getIntExtra(RestaurantDetailsActivity.RESTAURANTINDEX,0);
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(manager.getRestaurant(pos).getLatitude(),
                    manager.getRestaurant(pos).getLongitude()))
                    .title(manager.getRestaurant(pos).getRestaurantName())
                    .snippet(getInfoWindowStr(manager.getRestaurant(pos)))
                    .icon(getIcon(manager.getRestaurant(pos))));
            marker.setTag(pos);
            mMap.setInfoWindowAdapter(new CustomWindowAdapter(MapsActivity.this));
            marker.showInfoWindow();

        }

    }

    private String getInfoWindowStr(Restaurant restaurant){
        if(restaurant.getAllInspections().size() == 0)
        {
            return  getString(R.string.Address)
                    + restaurant.getRestaurantAddress()
                    + getString(R.string.No_inspections_found);
        }

        return getString(R.string.Address)
                + restaurant.getRestaurantAddress()
                + getString(R.string.Hazard_level)
                + restaurant.getInspection(0).getHazardLevel();
    }

    private void getCoord(){
        Intent intent = getIntent();
        int code = intent.getIntExtra(RestaurantDetailsActivity.CODE,0);
        if(code == 42){ //if we get the right code means the coordinate is passed to here, we modify the coordinates
            int pos = intent.getIntExtra(RestaurantDetailsActivity.RESTAURANTINDEX, -1);
            if(pos != -1){
                newLatitude = manager.getRestaurant(pos).getLatitude();
                newLongitude = manager.getRestaurant(pos).getLongitude();
            }
        }
        else{
            newLatitude = 9999;
            newLongitude = 9999;
        }
    }



    @Override
    public boolean onClusterItemClick(Restaurant item) {
        return false;
    }


    @Override
    public void onClusterItemInfoWindowClick(ClusterItem restaurant) {
        Log.i(TAG, "onClusterItemInfoWindowClick: registered click on restaurant : " + restaurant.toString());
        //todo: change to sorted list
        Intent intent = RestaurantDetailsActivity.makeIntent(MapsActivity.this, (Integer) manager.getRestaurantPositionInArray((Restaurant)restaurant));
        startActivity(intent);
    }


    @Override
    public void onClusterInfoWindowClick(Cluster cluster) {
        Toast.makeText(MapsActivity.this,
                "unable to get device location, please check your permissions",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onClusterClick(Cluster cluster) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), mMap.getCameraPosition().zoom + 2));
        return true;
    }

    private BitmapDescriptor getIcon(Restaurant restaurant){
        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_food);
        if(restaurant.numOfInspections()!= 0) {
            switch (restaurant.getInspection(0).getHazardLevel()) {
                case "Low":
                    circleDrawable = getResources().getDrawable(R.drawable.ic_warning_low);
                    break;
                case "Moderate":
                    circleDrawable = getResources().getDrawable(R.drawable.ic_warning_moderate);
                    break;
                case "High":
                    circleDrawable = getDrawable(R.drawable.ic_warning_critical);
                    break;
            }
        }
        return getMarkerIconFromDrawable(circleDrawable);
    }
}
