package furious.dataobjs;


public class NewsStoryBookmarkObject{
	
	
	private String Title;
	private String ID;
	private String Description;
	private String ImageURL;
	private String StoryDate;
	private int District;
	private String Division;
	private boolean UCVideo;
	private String Category;
	private String CrimeType;
	private String VideoURL;
	private String Author;
	private String DCNumber;


	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getImageURL() {
		return ImageURL;
	}
	public void setImageURL(String imageURL) {
		ImageURL = imageURL;
	}
	public String getStoryDate() {
		return StoryDate;
	}
	public void setStoryDate(String storyDate) {
		StoryDate = storyDate;
	}
	public int getDistrict() {
		return District;
	}
	public void setDistrict(int district) {
		District = district;
	}
	public String getDivision() {
		return Division;
	}
	public void setDivision(String division) {
		Division = division;
	}
	public boolean isUCVideo() {
		return UCVideo;
	}
	public void setUCVideo(boolean uCVideo) {
		UCVideo = uCVideo;
	}
	public String getCategory() {
		return Category;
	}
	public void setCategory(String category) {
		Category = category;
	}
	public String getCrimeType() {
		return CrimeType;
	}
	public void setCrimeType(String crimeType) {
		CrimeType = crimeType;
	}
	public String getVideoURL() {
		return VideoURL;
	}
	public void setVideoURL(String videoURL) {
		VideoURL = videoURL;
	}
	public String getAuthor() {
		return Author;
	}
	public void setAuthor(String author) {
		Author = author;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getDCNumber() {
		return DCNumber;
	}

	public void setDCNumber(String DCNumber) {
		this.DCNumber = DCNumber;
	}
	
}