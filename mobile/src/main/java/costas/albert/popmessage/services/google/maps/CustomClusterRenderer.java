package costas.albert.popmessage.services.google.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import costas.albert.popmessage.R;

public class CustomClusterRenderer extends DefaultClusterRenderer<GroupMessages> {

    private final Context mContext;
    private final IconGenerator mClusterIconGenerator;

    public CustomClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<GroupMessages> clusterManager) {
        super(context, map, clusterManager);
        mClusterIconGenerator = new IconGenerator(context.getApplicationContext());
        mContext = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(GroupMessages item,
                                               MarkerOptions markerOptions) {
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_location))
                .snippet(item.message().userName())
                .title(item.message().getText());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<GroupMessages> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);

        mClusterIconGenerator.setBackground(
                ContextCompat.getDrawable(mContext, R.drawable.background_circle));
        mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);
        final Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

}