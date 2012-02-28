package ktmt.k52.viettts.MediaList;

public class MediaList {

	private String mediaName;
	private String mediaPath;
	private boolean isChecked;
	
	
	
	public MediaList(String mediaName, String mediaPath) {
		super();
		this.mediaName = mediaName;
		this.mediaPath = mediaPath;
		this.isChecked =false;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public String getMediaName() {
		return mediaName;
	}
	public String getMediaPath() {
		return mediaPath;
	}
	
	
	
}
