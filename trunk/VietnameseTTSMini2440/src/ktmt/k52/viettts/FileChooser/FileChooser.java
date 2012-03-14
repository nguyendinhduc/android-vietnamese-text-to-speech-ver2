package ktmt.k52.viettts.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ktmt.k52.viettts.R;
import ktmt.k52.viettts.MediaList.MediaList;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

/**
 * Class thể hiện list folder và file cho phép người dùng có thể chọn
 * file bất kỳ,gần giống OpenFileDialog của .NET
 * @see ListActivity
 * @author DungNT
 */
public class FileChooser extends ListActivity {

	/**
	 * File hiện tại đang trỏ đến
	 * @see File
	 */
	private File currentDir;
	/**
	 * 
	 */
	private FileArrayAdapter adapter;
	/**
	 * đường dẫn file
	 */
	private static String file_path;

	/**
	 * tạo giao diện ban đầu
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		currentDir = new File("/sdcard/");
		fill(currentDir);
	}

	/**
	 * Lấy hết file và folder của thư mục hiện tại và đưa vào trong list
	 * @param f thư mục hiện tại
	 * @see File
	 * 
	 */
	private void fill(File f) {
		File[] dirs = f.listFiles();
		this.setTitle("Current Dir: " + f.getName());
		List<Option> dir = new ArrayList<Option>();
		List<Option> fls = new ArrayList<Option>();
		try {
			for (File ff : dirs) {
				if (ff.isDirectory())
					dir.add(new Option(ff.getName(), "Folder", ff
							.getAbsolutePath()));
				else {
					fls.add(new Option(ff.getName(), "File Size: "
							+ ff.length(), ff.getAbsolutePath()));
				}
			}
		} catch (Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!f.getName().equalsIgnoreCase("sdcard"))
			dir.add(0, new Option("..", "Parent Directory", f.getParent()));
		adapter = new FileArrayAdapter(FileChooser.this, R.layout.file_view,
				dir);
		this.setListAdapter(adapter);
	}
	
	/**
	 * Sự kiện khi ấn vào một phần tử của list
	 * @param l list hiện tại
	 * @param v view
	 * @param position vị trí được ấn vào trong list
	 * @param id id
	 * @see ListView
	 * @see View
	 * 
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if (o.getData().equalsIgnoreCase("folder")
				|| o.getData().equalsIgnoreCase("parent directory")) {
			currentDir = new File(o.getPath());
			fill(currentDir);
		} else {
			onFileClick(o);
		}
	}
	/**
	 * Xử lý khi ấn vào file
	 * @param o Option được ấn vào
	 */
	private void onFileClick(Option o) {

		file_path = o.getPath();
		finish();

	}
	/**
	 * Sự kiện kết thúc activity FileChoose
	 */
	@Override
	public void finish() {
		// Prepare data intent
		Intent data = new Intent();
		data.putExtra("filepath", file_path);

		// Activity finished ok, return the data
		setResult(RESULT_OK, data);

		super.finish();
	}

}