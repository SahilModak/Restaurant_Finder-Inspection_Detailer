package group17.cmpt276.iteration3.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.Model.RestaurantManager;
import group17.cmpt276.iteration3.R;

/*
Builds and displays AlertDialog which prompts user to update data.
*/
public class FavouriteDialog extends AppCompatDialogFragment {

    private static final String TAG = "Favourite fragment";
    private RestaurantManager manager = RestaurantManager.getInstance();
    private ArrayAdapter<Restaurant> adapter;

//    public interface UpdateDialogListener {
//        void onDialogPositiveClick(DialogFragment dialog);
//        void onDialogNegativeClick(DialogFragment dialog);
//
//    }
//    UpdateDialogListener listener;

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        try {
//            listener = (UpdateDialogListener) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(getActivity().toString() + " must implement UpdateDialogListener");
//        }
//    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // create view
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.favourite_layout, null);
        populateListView(view);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };

        return new AlertDialog.Builder(getActivity()).setTitle("New inspections for your favourites!")
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .create();
    }

    private void populateListView(View view) {

        ListView listView = (ListView) view.findViewById(R.id.restaurantListView);
        if (adapter == null) {
            adapter = new listAdapter(getContext(), R.layout.restaurant_view, manager.getAllRestaurants());
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    /*
    Custom adapter to provide a view for the Restaurant ListView
    Returns a view for each Restaurant in a collection of Restaurant objects
     */
    private class listAdapter extends ArrayAdapter<Restaurant> {

//        Context context;
//        int resource;
//        List<Restaurant> list;

        public listAdapter(Context context, int resource, List<Restaurant> list) {
            super(context, resource, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View restaurantView = convertView;
            if (restaurantView == null) {
                restaurantView = getLayoutInflater().inflate(R.layout.restaurant_view, parent, false);
            }

            Restaurant currentRestaurant = manager.getRestaurant(position);
            setupRestaurantView(currentRestaurant, restaurantView);

            restaurantView.setBackgroundResource(R.drawable.border);
            if (currentRestaurant.isFavourite()) {
                restaurantView.setBackgroundResource(R.drawable.border_fave);
            }

            return restaurantView;
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
                            getDate().makeActivityString(getActivity())));
        }
    }
}
