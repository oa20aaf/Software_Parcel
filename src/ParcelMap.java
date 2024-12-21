import java.util.HashMap;
import java.util.Map;

public class ParcelMap {
    private Map<String, Parcel> parcelMap;

    public ParcelMap() {
        this.parcelMap = new HashMap<>();
    }

    public void addParcel(Parcel parcel) {
        parcelMap.put(parcel.getParcelID(), parcel);
    }

    public Parcel findParcelByID(String parcelID) {
        return parcelMap.get(parcelID);
    }

    public Map<String, Parcel> getAllParcels() {
        return parcelMap;
    }
}
