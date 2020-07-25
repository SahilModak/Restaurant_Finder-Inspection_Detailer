package group17.cmpt276.iteration3.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import group17.cmpt276.iteration3.R;

//adapter for making a custom info window for all markers, used to display the restauarant name as
// the title and the address and hazard level as the snippet

public class CustomWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private final View mWindow;
    private Context mContext;

    public CustomWindowAdapter(Context mContext) {
        this.mContext = mContext;
        this.mWindow = LayoutInflater.from(mContext).inflate(R.layout.marker_info_window, null);
    }

    private void renderWindowText(Marker marker, View view){
        //int pos = (Integer) marker.getTag();
        TextView title = (TextView) view.findViewById(R.id.RestaurantNameInfo);
        title.setText(marker.getTitle());

        TextView str = (TextView) view.findViewById(R.id.RestaurantStrInfo);
        str.setText("Address: " + marker.getSnippet());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        renderWindowText(marker, mWindow);
        return mWindow;
    }
}
