package ktmt.k52.viettts.FileChooser;

import org.apache.http.HttpResponse;

/**
 * Class dùng để lưu trữ thông tin về những file và thư mục
 * trong bộ nhớ
 * @author DungNT
 * @see Comparable
 */
public class Option implements Comparable<Option>{
	/**
	 * Tên file hoặc folder
	 */
	private String name;
	/**
	 * Dữ liệu trong file
	 * nếu là file thì data là filesize
	 * nếu là folder thì data sẽ là Folder
	 */
	private String data;
	/**
	 * đường dẫn file,folder
	 */
	private String path;
	
	/**
	 * Contructor
	 * @param n name - tên file
	 * @param d data - dữ liệu
	 * @param p path - đường dẫn
	 */
	public Option(String n,String d,String p)
	{
		name = n;
		data = d;
		path = p;
	}
	/**
	 * lấy tên file
	 * @return tên file dạng {@link String}
	 */
	public String getName()
	{
		return name;
	}
	/**
	 * lấy data
	 * @return data dạng {@link String}
	 */
	public String getData()
	{
		return data;
	}
	
	/**
	 * lấy đường dẫn file
	 * @return đường dẫn file dạng {@link String}
	 */
	public String getPath()
	{
		return path;
	}
	
	/**
	 * so sánh với Option khác
	 * @param o Option cần so sánh
	 * @return 0 nếu giống,-1 nếu không giống
	 */
	@Override
	public int compareTo(Option o) {
		if(this.name != null)
			return this.name.toLowerCase().compareTo(o.getName().toLowerCase()); 
		else 
			throw new IllegalArgumentException();
	}
}

