public class Parcel {
    private String parcelID;
    private double weight;
    private String dimensions;
    private double collectionFee;
    private String status;
    public Parcel(String parcelID, double weight, String dimensions) {
        this.parcelID = parcelID;
        this.weight = weight;
        this.dimensions = dimensions;
        this.status = "Waiting";
        calculateCollectionFee();
    }

    public void calculateCollectionFee() {
        this.collectionFee = weight * 5.0;
    }

    public String getParcelID() {
        return parcelID;
    }

    public double getCollectionFee() {
        return collectionFee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "parcelID='" + parcelID + '\'' +
                ", weight=" + weight +
                ", dimensions='" + dimensions + '\'' +
                ", collectionFee=" + collectionFee +
                ", status='" + status + '\'' +
                '}';
    }
}
