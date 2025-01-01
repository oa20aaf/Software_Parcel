import java.util.HashMap;
import java.util.Map;

public class ParcelMap {
    private Map<String, Parcel> parcelMap;

    public ParcelMap() {
        this.parcelMap = new HashMap<>();
    }

    // Add a parcel to the map
    public void addParcel(Parcel parcel) {
        parcelMap.put(parcel.getParcelID(), parcel);
    }

    // Find a parcel by its ID
    public Parcel findParcelByID(String parcelID) {
        return parcelMap.get(parcelID);
    }

    // Get all parcels
    public Map<String, Parcel> getAllParcels() {
        return parcelMap;
    }

    // Remove a parcel by its ID
    public void removeParcel(String parcelID) {
        if (parcelMap.containsKey(parcelID)) {
            parcelMap.remove(parcelID);
        }
    }
}
