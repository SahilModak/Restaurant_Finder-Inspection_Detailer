package group17.cmpt276.iteration3.UI;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import group17.cmpt276.iteration3.Model.Restaurant;
import group17.cmpt276.iteration3.R;

/*
The RestaurantMarkerRenderer renders the clusters of restaurants, determines if they should be a cluster, and defines the style for
a restaurant not in a cluster
 */

public class RestaurantMarkerRenderer extends DefaultClusterRenderer<Restaurant> {

    private Context mContext;
    private GoogleMap map;

    public RestaurantMarkerRenderer(Context context, GoogleMap map, ClusterManager<Restaurant> clusterManager) {
        super(context, map, clusterManager);
        mContext = context;
        this.map = map;
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull Restaurant restaurant, @NonNull MarkerOptions markerOptions) {
        boolean flag = false;
        Drawable circleDrawable = mContext.getResources().getDrawable(R.drawable.ic_food);
        if(restaurant.numOfInspections()!= 0) {
            flag = true;
            switch (restaurant.getInspection(0).getHazardLevel()) {
                case "Low":
                    circleDrawable = mContext.getResources().getDrawable(R.drawable.ic_warning_low);
                    break;
                case "Moderate":
                    circleDrawable = mContext.getResources().getDrawable(R.drawable.ic_warning_moderate);
                    break;
                case "High":
                    circleDrawable = mContext.getResources().getDrawable(R.drawable.ic_warning_critical);
                    break;
            }
        }
        LatLng GPS = restaurant.getPosition();
        if(flag) {
            BitmapDescriptor icon = getMarkerIconFromDrawable(circleDrawable);
            markerOptions.position(GPS).title(restaurant.getRestaurantName()).icon(icon).snippet(getInfoWindowStr(restaurant));
        }
        else{
            BitmapDescriptor icon = getMarkerIconFromDrawable(circleDrawable);
            markerOptions.position(GPS).title(restaurant.getRestaurantName()).icon(icon).snippet(restaurant.getRestaurantAddress());
        }
    }

    @Override
    protected void onBeforeClusterRendered(@NonNull Cluster<Restaurant> cluster, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<Restaurant> cluster) {
        return cluster.getSize() > 5;
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private String getInfoWindowStr(Restaurant restaurant){
        if(restaurant.getAllInspections().size() == 0){
            return "Address: " + restaurant.getRestaurantAddress() +
                    "\nAddress: No inspections found";
        }
        return "Address: " + restaurant.getRestaurantAddress() +
                "\nHazard Level: " + restaurant.getInspection(0).getHazardLevel();
    }
}
