package ktmt.k52.viettts.MediaList;


import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ListMediaAdapter extends ArrayAdapter<MediaList> {
	ArrayList<MediaList> array;
	int resource;
	Context context;

	public ListMediaAdapter(Context context, int textViewResourceId,
			ArrayList<MediaList> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		resource = textViewResourceId;
		array = objects;
	}

	// Phương thức xác định View mà Adapter hiển thị, ở đây chính là
	// CustomViewGroup
	// Bắt buộc phải Override khi kế thừa từ ArrayAdapter
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View MediaView = convertView;
		if (MediaView == null) {
			MediaView = new CustomViewGroup(getContext());
		}
		// Lấy về đối tượng Media hiện tại
		final MediaList Media = array.get(position);
		if (Media != null) {
			TextView mediaName = ((CustomViewGroup) MediaView).mediaName;
			TextView mediaPath = ((CustomViewGroup) MediaView).mediaPath;
			CheckBox checkMedia = ((CustomViewGroup) MediaView).cb;
			// Set sự kiện khi đánh dấu vào checkbox trên list
			checkMedia.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					Media.setChecked(isChecked);
				}
			});
			
			
			// Lấy về nội dung cho TextView và CheckBox dựa vào đối tượng Media
			// hiện tại
			mediaName.setText(Media.getMediaName());
			mediaPath.setText(Media.getMediaPath());
			checkMedia.setChecked(Media.isChecked());
		}
		return MediaView;
	}
}
