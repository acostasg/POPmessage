package costas.albert.popmessage.services.google.maps;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import costas.albert.popmessage.entity.Message;

public class GroupMessages implements ClusterItem {

    private Message message;

    public GroupMessages(Message message) {
        this.message = message;
    }

    @Override
    public LatLng getPosition() {
        return new LatLng(
                Double.valueOf(message.getLocation().getLat()),
                Double.valueOf(message.getLocation().getLon())
        );
    }

    public Message message() {
        return message;
    }

}
