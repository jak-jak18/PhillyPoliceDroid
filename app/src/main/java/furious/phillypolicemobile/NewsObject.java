package furious.phillypolicemobile;

public class NewsObject{
	private String ID;
	private String DistrictNumber;
	private String StoryTitle;
	private String CaptionURL;
	private String StoryExcert;
	private String StoryDate;
	private String StoryURL;
	private String VideoURL;
	private String Description;
	private String AlertType;
	private String Author;
	private boolean isUC_Vid = false;
	
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getDistrictNumber() {
		return DistrictNumber;
	}
	public void setDistrictNumber(String districtNumber) {
		DistrictNumber = districtNumber;
	}
	public String getStoryTitle() {
		return StoryTitle;
	}
	public void setStoryTitle(String storyTitle) {
		StoryTitle = storyTitle;
	}
	public String getCaptionURL() {
		return CaptionURL;
	}
	public void setCaptionURL(String captionURL) {
		CaptionURL = captionURL;
	}

	public String getStoryURL() {
		return StoryURL;
	}
	public void setStoryURL(String storyURL) {
		StoryURL = storyURL;
	}
	public String getStoryExcert() {
		return StoryExcert;
	}
	public void setStoryExcert(String storyExcert) {
		StoryExcert = storyExcert;
	}
	public String getStoryDate() {
		return StoryDate;
	}
	public void setStoryDate(String storyDate) {
		StoryDate = storyDate;
	}
	public String getVideoURL() {
		return VideoURL;
	}
	public void setVideoURL(String videoURL) {
		VideoURL = videoURL;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getAlertType() {
		return AlertType;
	}
	public void setAlertType(String alertType) {
		AlertType = alertType;
	}
	public String getAuthor() {
		return Author;
	}
	public void setAuthor(String author) {
		Author = author;
	}
	public boolean isUC_Vid() {
		return isUC_Vid;
	}
	public void setUC_Vid(boolean isUC_Vid) {
		this.isUC_Vid = isUC_Vid;
	}
	
	
	
	
	
}