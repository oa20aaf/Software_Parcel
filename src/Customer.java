public class Customer {
    private int sequenceNo;
    private String Firstname;
    private String Lastname;
    private String parcelID;

    public Customer(int sequenceNo, String name,String Lastname, String parcelID) {
        this.sequenceNo = sequenceNo;
        this.Firstname = name;
        this.Lastname = Lastname;
        this.parcelID = parcelID;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getName() {
        return Firstname;
    }
    public String getLastname() {
        return Lastname;
    }

    public String getParcelID() {
        return parcelID;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "sequenceNo=" + sequenceNo +
                ", name='" + Firstname + '\'' +
                ", parcelID='" + parcelID + '\'' +
                '}';
    }
}
