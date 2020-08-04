package group17.cmpt276.iteration3.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    private List<Restaurant> faveList = manager.getAllRestaurants();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // create view
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.favourite_layout, null);
//        adapter = new RestaurantListAdapter(getActivity(), R.layout.restaurant_view, (ArrayList<Restaurant>) faveList);
//        populateListView(view);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = UpdatedFavouritesActivity.makeIntent(getActivity());
                startActivity(intent);
            }
        };

        return new AlertDialog.Builder(getActivity()).setTitle("New inspections for your favourites!")
                .setView(view)
                .setPositiveButton(android.R.string.ok, listener)
                .create();
    }

    private void populateListView(View view) {

        ListView listView = (ListView) view.findViewById(R.id.favouritesListView);
        if (adapter == null) {
            adapter = new RestaurantListAdapter(getActivity(), R.layout.restaurant_view, (ArrayList<Restaurant>) faveList);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

}
