import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class Parcel {
    private String parcelID;
    private double weight;
    private String dimensions;
    private double collectionFee;
    private String status;
    private Date dateReceived;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public Parcel(String parcelID, double weight, String dimensions, String dateReceived){
        this.parcelID = parcelID;
        this.weight = weight;
        this.dimensions = dimensions;
        this.status = "Waiting";
        this.dateReceived = parseDate(dateReceived);
    }

    public double calculateCollectionFee(String dateReceived) {
        // Calculate the difference in days
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate receivedDate = LocalDate.parse(dateReceived, formatter);
        LocalDate currentDate = LocalDate.now();
        long daysDifference = ChronoUnit.DAYS.between(receivedDate, currentDate);
        this.collectionFee = weight * Integer.valueOf(dimensions) * daysDifference;
        return collectionFee;
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

    public String getDateReceived() {
        return dateFormat.format(dateReceived);
    }

    private Date parseDate(String dateReceived) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return sdf.parse(dateReceived); // Parse the string into Date object
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Return null if there's an error in parsing
        }
    }

    public long getDateDiff(String dateReceived){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate receivedDate = LocalDate.parse(dateReceived, formatter);
        LocalDate currentDate = LocalDate.now();
        long daysDifference = ChronoUnit.DAYS.between(receivedDate, currentDate);
        return daysDifference;
    }
    public double getWeight() {
        return weight;
    }

    public String getDimensions() {
        return dimensions;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "parcelID='" + parcelID + '\'' +
                ", weight=" + weight +
                ", dimensions='" + dimensions + '\'' +
                ", collectionFee=" + collectionFee +
                ", status='" + status + '\'' +
                ", dateReceived=" + dateFormat.format(dateReceived) +
                '}';
    }
}
