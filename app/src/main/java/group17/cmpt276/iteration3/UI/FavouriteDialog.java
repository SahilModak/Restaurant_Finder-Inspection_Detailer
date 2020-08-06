package group17.cmpt276.iteration3.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.appcompat.app.AppCompatDialogFragment;
import group17.cmpt276.iteration3.R;

/**
 * Builds and displays AlertDialog which informs user of new inspections for Favourites
 */
public class FavouriteDialog extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // create view
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.favourite_layout, null);

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = UpdatedFavouritesActivity.makeIntent(getActivity());
                startActivity(intent);
            }
        };

        return new AlertDialog.Builder(getActivity()).setTitle(R.string.attention)
                .setView(view)
                .setPositiveButton(R.string.ok, listener)
                .create();
    }
}
