package ktmt.k52.viettts.FileChooser;

import java.util.ArrayList;
import java.util.List;

import ktmt.k52.viettts.R;
import ktmt.k52.viettts.MediaList.MediaList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * FileArrayAdapter sẽ được sử dụng thay thế cho ArrayAdapter được bind với
 * ListView. Thông thường ArrayAdapter chỉ cho hiển thị String bằng TextView
 * nhưng với việc kế thừa và override phương thức getView, ta có thể định nghĩa lại
 * hiển thị cho các thành phần của ListView.
 * 
 * @author DungNT
 * @see ArrayAdapter
 * @see Option
 */
public class FileArrayAdapter extends ArrayAdapter<Option>{
	/**
	 * Context mà list hiển thị
	 * @see Context
	 */
	private Context c;
	/**
	 * id của custom Textview đã viết lại trong layout
	 */
	private int id;
	/**
	 * List phần tử  Option
	 * @see List
	 * @see Option
	 */
	private List<Option>items;
	/**
	 * Contructor
	 * @param context Context mà list hiển thị
	 * @param textViewResourceId id của custom Textview đã viết lại trong layout
	 * @param objects List phần tử  Option
	 * @see Context
	 * @see Option
	 * @see List
	 */
	public FileArrayAdapter(Context context, int textViewResourceId,
			List<Option> objects) {
		super(context, textViewResourceId, objects);
		c = context;
		id = textViewResourceId;
		items = objects;
	}
	
	/**
	 *Lấy phần tử thứ i của list
	 *@param i Vị trí phần tử cần lấy
	 *@see Option 
	 */
	@Override
	public Option getItem(int i)
	 {
		 return items.get(i);
	 }
	
	/**
	 * Phương thức xác định View mà Adapter hiển thị, ở đây chính là
	 * CustomViewGroup
	 * Bắt buộc phải Override khi kế thừa từ ArrayAdapter
	 * @param position vị trí đối tượng Media hiện tại
	 * @param convertView
	 * @param parent
	 * @see View
	 * @see ViewGroup
	 */
	 @Override
       public View getView(int position, View convertView, ViewGroup parent) {
               View v = convertView;
               if (v == null) {
                   LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   v = vi.inflate(id, null);
               }
               final Option o = items.get(position);
               if (o != null) {
                       TextView t1 = (TextView) v.findViewById(R.id.TextView01);
                       TextView t2 = (TextView) v.findViewById(R.id.TextView02);
                       
                       if(t1!=null)
                       	t1.setText(o.getName());
                       if(t2!=null)
                       	t2.setText(o.getData());
                       
               }
               return v;
       }

}

