package furious.dataobjs;

public class USMurderObject {

    public String VictimName;
    public String NewsURL;
    public String Desc;
    public String DCNumber;
    public String ImageURL;
    public String NewsStoryDesc;
    public String NewsStoryTitle;
    public String NewsStoryPubDate;
    public String NewsStoryStoryTubeURL;
    public boolean isNewsStory = false;


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

    public String getNewsStoryDesc() {
        return NewsStoryDesc;
    }

    public void setNewsStoryDesc(String newsStoryDesc) {
        NewsStoryDesc = newsStoryDesc;
    }

    public String getNewsStoryTitle() {
        return NewsStoryTitle;
    }

    public void setNewsStoryTitle(String newsStoryTitle) {
        NewsStoryTitle = newsStoryTitle;
    }

    public String getNewsStoryPubDate() {
        return NewsStoryPubDate;
    }

    public void setNewsStoryPubDate(String newsStoryPubDate) {
        NewsStoryPubDate = newsStoryPubDate;
    }

    public String getNewsStoryStoryTubeURL() {
        return NewsStoryStoryTubeURL;
    }

    public void setNewsStoryStoryTubeURL(String newsStoryStoryTubeURL) {
        NewsStoryStoryTubeURL = newsStoryStoryTubeURL;
    }


    public String getDCNumber() {
        return DCNumber;
    }


    public boolean isNewsStory() {
        return isNewsStory;
    }

    public void setNewsStory(boolean newsStory) {
        isNewsStory = newsStory;
    }





}
