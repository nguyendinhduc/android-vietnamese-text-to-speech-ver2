package ktmt.k52.viettts.MediaList;


import ktmt.k52.viettts.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomViewGroup extends LinearLayout {
	public CheckBox cb;
	public TextView mediaName;
	public TextView mediaPath;
	
	public CustomViewGroup(Context context) {
		super(context);
		// Sử dụng LayoutInflater để gán giao diện trong list.xml cho class này
		LayoutInflater li = (LayoutInflater) this.getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		li.inflate(R.layout.custommedialist, this, true);
		// Lấy về các View qua Id
		cb = (CheckBox) findViewById(R.id.check_media);
		mediaName = (TextView) findViewById(R.id.media_name);
		mediaPath = (TextView) findViewById(R.id.media_path);
		
				}
}
