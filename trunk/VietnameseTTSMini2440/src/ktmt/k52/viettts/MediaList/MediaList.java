package ktmt.k52.viettts.MediaList;

/**
 * Class thể hiện các phần tử trong list,các phần tử này sẽ
 * bao gồm tên,đường dẫn tới file nhạc và biến check để xóa
 * @author DungNT
 */
public class MediaList {

	/**
	 * Tên file nhạc
	 * @see String
	 */
	private String mediaName;
	/**
	 * Đường dẫn file nhạc
	 * @see String
	 */
	private String mediaPath;
	/**
	 * Biến check để xóa khi cần
	 */
	private boolean isChecked;
	
	
	/**
	 * Contructor
	 * @param mediaName Tên file nhạc
	 * @param mediaPath Đường dẫn file nhạc
	 */
	public MediaList(String mediaName, String mediaPath) {
		super();
		this.mediaName = mediaName;
		this.mediaPath = mediaPath;
		this.isChecked =false;
	}
	
	/**
	 * Lấy biến check để xóa khi cần
	 * @return true khi phần tử được check,fasle khi không
	 */
	public boolean isChecked() {
		return isChecked;
	}
	
	/**
	 * đặt biến check để xóa khi cần
	 * @param isChecked biến check
	 */
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	
	/**
	 * Lấy tên file nhạc
	 * @return tên file nhạc dạng {@link String}
	 */
	public String getMediaName() {
		return mediaName;
	}
	
	/**
	 * Lấy đường dẫn file nhạc
	 * @return đường dẫn file nhạc dạng {@link String}
	 */
	public String getMediaPath() {
		return mediaPath;
	}
	
	
	
}
