package furious.dataobjs;

public class USMurderObject {

    public String VictimName;
    public String NewsURL;
    public String Desc;
    public String DCNumber;
    public String ImageURL;
    public boolean isNewsStory = false;



    public String getDCNumber() {
        return DCNumber;
    }

    public void setDCNumber(String DCNumber) {
        this.DCNumber = DCNumber;
    }

    public String getVictimName() {
        return VictimName;
    }

    public void setVictimName(String victimName) {
        VictimName = victimName;
    }

    public String getNewsURL() {
        return NewsURL;
    }

    public void setNewsURL(String newsURL) {
        NewsURL = newsURL;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public boolean isNewsStory() {
        return isNewsStory;
    }

    public void setNewsStory(boolean newsStory) {
        isNewsStory = newsStory;
    }





}
