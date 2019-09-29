package furious.dataobjs;


public class CrimeObject {


    private String TimeStamp;
    private String DistrictNumber;
    private String PSArea;
    private String DispatchTime;
    private String AddressBlock;
    private String HashTag;

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getDistrictNumber() {
        return DistrictNumber;
    }

    public void setDistrictNumber(String districtNumber) {
        DistrictNumber = districtNumber;
    }

    public String getPSArea() {
        return PSArea;
    }

    public void setPSArea(String PSArea) {
        this.PSArea = PSArea;
    }

    public String getDispatchTime() {
        return DispatchTime;
    }

    public void setDispatchTime(String dispatchTime) {
        DispatchTime = dispatchTime;
    }

    public String getAddressBlock() {
        return AddressBlock;
    }

    public void setAddressBlock(String addressBlock) {
        AddressBlock = addressBlock;
    }

    public String getCrimeName() {
        return CrimeName;
    }

    public void setCrimeName(String crimeName) {
        CrimeName = crimeName;
    }

    private String CrimeName;


    public String getHashTag() {
        return HashTag;
    }

    public void setHashTag(String hashTag) {
        HashTag = hashTag;
    }
}
