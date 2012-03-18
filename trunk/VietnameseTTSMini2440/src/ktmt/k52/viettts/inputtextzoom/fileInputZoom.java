package ktmt.k52.viettts.inputtextzoom;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import ktmt.k52.viettts.R;
import ktmt.k52.viettts.VietnameseTTSMini2440Activity;
import ktmt.k52.viettts.FileChooser.FileChooser;
import android.app.Activity;
import android.content.Intent;
import android.hook.VietKeyListenner;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class dùng để tạo giao diện EditText cho người dùng nhập
 * Tiếng Việt
 * @see Activity
 * @author DungNT
 */
public class fileInputZoom extends Activity implements
OnKeyboardActionListener, OnKeyListener{
	/**
	 * Giao diện để người dùng nhập tiếng việt
	 * @see EditText
	 */
	private EditText inputzoom;
	/**
	 * Nút ấn để người dùng xóa text
	 * @see ImageButton
	 */
	private ImageButton clearInput; 
	/**
	 * Nút ấn để người dùng back lại text lúc đầu
	 * @see ImageButton
	 */
	private ImageButton goBack; 
	/**
	 * Nút ấn để người dùng trở lại giao diện chính
	 * @see ImageButton
	 */
	private ImageButton returnHome; 
	/**
	 * Nút ấn để người dùng chọn text file để hiển thị lên textview
	 * @see ImageButton
	 */
	private ImageButton btChoose;
	/**
	 * text mà người dùng nhập - Tiếng Việt
	 */
	private String textinput;
	/**
	 * text sao lưu để dùng cho nút Back
	 */
	private String backupInput;
	// Các hằng dùng cho tạo Option Menu
	/**
	 * Các hằng dùng cho tạo Option Menu
	 * Menu OK
	 */
	private static final int OK = Menu.FIRST;
	/**
	 * Các hằng dùng cho tạo Option Menu
	 * Menu Cancel
	 */
	private static final int CANCEL = Menu.FIRST + 2;
	
	/**
	 * Request code dùng trong tạo activity chọn file text
	 */
	protected static final int REQUEST_CODE = 0;

	EditText currentEditText = null;
	private static boolean shift = false;
	private KeyboardView keyboardView;
	private Keyboard qwertyKeyboard,shiftKeyboard;
	/**
	 * hàm tạo activity
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textinputzoom);
		//set tieng viet
		 keyboardView = (KeyboardView) findViewById(R.id.keyboardview);
		 qwertyKeyboard = new Keyboard(this, R.xml.qwerty);
		 shiftKeyboard = new Keyboard(this,R.xml.qwertyshift);
		keyboardView.setKeyboard(qwertyKeyboard);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(true);
		keyboardView.setOnKeyListener(this);
		keyboardView.setOnKeyboardActionListener(this);
		// init
		inputzoom = (EditText) findViewById(R.id.InputZoom);
		clearInput = (ImageButton) findViewById(R.id.clearInputZoom);

		goBack = (ImageButton) findViewById(R.id.back);
		returnHome = (ImageButton) findViewById(R.id.btReturn);

		btChoose = (ImageButton) findViewById(R.id.Choose);

		// dua text nhan cua main activity vao input text
		Bundle receiver = this.getIntent().getExtras();
		backupInput = receiver.getString("textInputComming");

		inputzoom.setText(backupInput);
		inputzoom.setCursorVisible(true);
		inputzoom.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction()==KeyEvent.ACTION_DOWN){
					 return VietKeyListenner.setKey(inputzoom, keyCode,event);
				}		
				return false;
			}
		});
		inputzoom.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				toggleKeyboardVisibility(inputzoom);
			}
		});
		// cac su kien nut an
		clearInput.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				inputzoom.setText("");

			}
		});
		returnHome.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				textinput = inputzoom.getText().toString();
				finish();
			}
		});

		goBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				inputzoom.setText(backupInput);

			}
		});

		btChoose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// tạo intent đề chạy activity file chooser
				Intent fileChoose = new Intent(fileInputZoom.this,
						FileChooser.class);
				// Set the request code to any code you like, you can identify
				// the callback via this code
				startActivityForResult(fileChoose, REQUEST_CODE);

			}
		});

	}
	/**
	 * hàm kết thúc activity
	 */
	@Override
	public void finish() {
		// Prepare data intent
		Intent data = new Intent();
		data.putExtra("textInputReturn", textinput);

		// Activity finished ok, return the data
		setResult(RESULT_OK, data);
		super.finish();
	}

	/**
	 *Hàm tạo Option Menu 
	 *@param menu Menu cần tạo
	 *@return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, OK, 0, "Return").setIcon(R.drawable.okicon);
		menu.add(0, CANCEL, 0, "Cancel").setIcon(R.drawable.closeicon);
		return true;
	}

	// 
	/**
	 * Xử lý sự kiện khi các option trong Option Menu được lựa chọn
	 * @param item item được chọn trong option menu
	 * @return true
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case OK: {
			textinput = inputzoom.getText().toString();
			finish();
			break;
		}
		case CANCEL: {
			break;
		}
		}
		return true;
	}

	/**
	 *Hàm trả về kết quả khi kết thúc activity chọn file text
	 *Khi chọn xong file text,sẽ hiển thị text của file lên textview 
	 *@param requestCode request code mà activity fileinputzoom gửi đến sự kiện chọn file text
	 *@param resultCode kết quả trả lại từ activity chọn file
	 *@param data dữ liệu gửi về từ activity chọn file
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("filepath")) {

				String fileChooserPath = data.getExtras().getString("filepath");

				try {
					File file = new File(fileChooserPath);
					FileInputStream fIn = new FileInputStream(file);

					// Read file with UTF-8

					InputStreamReader isr = new InputStreamReader(fIn, "UTF-8");

					char[] inputBuffer = new char[8192];

					isr.read(inputBuffer);

					String readString = new String(inputBuffer);

					// Load content file on ViewText

					inputzoom.setText(readString);

				} catch (Exception e) {

					Toast.makeText(fileInputZoom.this, e.getMessage(),
							Toast.LENGTH_SHORT).show();

				}

			}
		}

	}
	
	private void toggleKeyboardVisibility(EditText ei) {
		KeyboardView keyboardView = (KeyboardView) findViewById(R.id.keyboardview);
		int visibility = keyboardView.getVisibility();
		switch (visibility) {
		case View.VISIBLE:
			keyboardView.setVisibility(View.GONE);
			break;
		case View.GONE:
		case View.INVISIBLE:
			keyboardView.setVisibility(View.VISIBLE);
			currentEditText = ei;
			break;
		}
	}
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		  HashMap<String, String> keyCodeMap = new HashMap<String, String>();
		    /*   keyCodeMap.put("1", "1");
		       keyCodeMap.put("2", "2");
		       keyCodeMap.put("3", "3");
		       keyCodeMap.put("4", "4");
		       keyCodeMap.put("5", "5");
		       keyCodeMap.put("6", "6");
		       keyCodeMap.put("7", "7");
		       keyCodeMap.put("8", "8");
		       keyCodeMap.put("9", "9");
		       keyCodeMap.put("0", "0");*/
		       keyCodeMap.put("11", "q");
		       keyCodeMap.put("12", "w");
		       keyCodeMap.put("13", "e");
		       keyCodeMap.put("14", "r");
		       keyCodeMap.put("15", "t");
		       keyCodeMap.put("16", "y");
		       keyCodeMap.put("17", "u");
		       keyCodeMap.put("18", "i");
		       keyCodeMap.put("19", "o");
		       keyCodeMap.put("10", "p");
		       keyCodeMap.put("21", "a");
		       keyCodeMap.put("22", "s");
		       keyCodeMap.put("23", "d");
		       keyCodeMap.put("24", "f");
		       keyCodeMap.put("25", "g");
		       keyCodeMap.put("26", "h");
		       keyCodeMap.put("27", "j");
		       keyCodeMap.put("28", "k");
		       keyCodeMap.put("29", "l");
		       keyCodeMap.put("31", "z");
		       keyCodeMap.put("32", "x");
		       keyCodeMap.put("33", "c");
		       keyCodeMap.put("34", "v");
		       keyCodeMap.put("35", "b");
		       keyCodeMap.put("36", "n");
		       keyCodeMap.put("37", "m");
		       
		       keyCodeMap.put("111", "Q");
		       keyCodeMap.put("112", "W");
		       keyCodeMap.put("113", "E");
		       keyCodeMap.put("114", "R");
		       keyCodeMap.put("115", "T");
		       keyCodeMap.put("116", "Y");
		       keyCodeMap.put("117", "U");
		       keyCodeMap.put("118", "I");
		       keyCodeMap.put("119", "O");
		       keyCodeMap.put("110", "P");
		       keyCodeMap.put("121", "A");
		       keyCodeMap.put("122", "S");
		       keyCodeMap.put("123", "D");
		       keyCodeMap.put("124", "F");
		       keyCodeMap.put("125", "G");
		       keyCodeMap.put("126", "H");
		       keyCodeMap.put("127", "J");
		       keyCodeMap.put("128", "K");
		       keyCodeMap.put("129", "L");
		       keyCodeMap.put("131", "Z");
		       keyCodeMap.put("132", "X");
		       keyCodeMap.put("133", "C");
		       keyCodeMap.put("134", "V");
		       keyCodeMap.put("135", "B");
		       keyCodeMap.put("136", "N");
		       keyCodeMap.put("137", "M");
		       
		       keyCodeMap.put("-3", "\n");
		       keyCodeMap.put("-2", ",");
		       keyCodeMap.put("-1", "!");
		       keyCodeMap.put("-4", ".");
		       keyCodeMap.put("-5", " ");
		       keyCodeMap.put("-6", "?");
		       
		       	String c = keyCodeMap.get(String.valueOf(primaryCode));
	       if(!(c == null)){
	        currentEditText.append(c);
	        VietKeyListenner.setKey3(currentEditText);
	       }
	       else{
	        switch(primaryCode){
	        case -7:
	         if(currentEditText.getText().toString().length() > 0){	 
	          currentEditText.setText(currentEditText.getText().toString().substring(0, currentEditText.getText().toString().length() - 1));
	         }
	         break;
	        case 45://shift
	        	shift = !shift;
	        	if(shift)
	        	{
	        	keyboardView.setKeyboard(shiftKeyboard);	
	        	}else{
	        		keyboardView.setKeyboard(qwertyKeyboard);	
	        	}
	        	break;
	        	}
	        }
		
	}
	@Override
	public void onPress(int primaryCode) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRelease(int primaryCode) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onText(CharSequence text) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void swipeDown() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void swipeLeft() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void swipeRight() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void swipeUp() {
		// TODO Auto-generated method stub
		
	}

}
