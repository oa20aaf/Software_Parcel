public class Customer {
    private int sequenceNo;
    private String name;
    private String parcelID;

    public Customer(int sequenceNo, String name, String parcelID) {
        this.sequenceNo = sequenceNo;
        this.name = name;
        this.parcelID = parcelID;
    }

    public int getSequenceNo() {
        return sequenceNo;
    }

    public String getName() {
        return name;
    }

    public String getParcelID() {
        return parcelID;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "sequenceNo=" + sequenceNo +
                ", name='" + name + '\'' +
                ", parcelID='" + parcelID + '\'' +
                '}';
    }
}
