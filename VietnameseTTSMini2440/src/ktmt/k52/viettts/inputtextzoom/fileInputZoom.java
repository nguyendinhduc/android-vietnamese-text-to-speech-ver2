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
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class dùng để tạo giao diện EditText cho người dùng nhập Tiếng Việt
 * 
 * @see Activity
 * @author DungNT+LamPT
 */
public class fileInputZoom extends Activity implements OnTouchListener,
		OnClickListener, OnFocusChangeListener {
	/**
	 * Giao diện để người dùng nhập tiếng việt
	 * 
	 * @see EditText
	 */
	private EditText inputzoom;
	/**
	 * Nút ấn để người dùng xóa text
	 * 
	 * @see ImageButton
	 */
	private ImageButton clearInput;
	/**
	 * Nút ấn để người dùng back lại text lúc đầu
	 * 
	 * @see ImageButton
	 */
	private ImageButton goBack;
	/**
	 * Nút ấn để người dùng trở lại giao diện chính
	 * 
	 * @see ImageButton
	 */
	private ImageButton returnHome;
	/**
	 * Nút ấn để người dùng chọn text file để hiển thị lên textview
	 * 
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
	 * Các hằng dùng cho tạo Option Menu Menu OK
	 */
	private static final int OK = Menu.FIRST;
	/**
	 * Các hằng dùng cho tạo Option Menu Menu Cancel
	 */
	private static final int CANCEL = Menu.FIRST + 2;

	/**
	 * Request code dùng trong tạo activity chọn file text
	 */
	protected static final int REQUEST_CODE = 0;

	/**
	 * cac nút đặc biệt trên bàn phím Space
	 */
	private Button mBSpace;
	/**
	 * cac nút đặc biệt trên bàn phím Ẩn bàn phím
	 */
	private Button mBdone;
	/**
	 * cac nút đặc biệt trên bàn phím Nút xóa
	 */
	private Button mBack;
	/**
	 * cac nút đặc biệt trên bàn phím Shift
	 */
	private Button mBChange;
	/**
	 * cac nút đặc biệt trên bàn phím Số
	 */
	private Button mNum;
	/**
	 * keyboard view
	 */
	private RelativeLayout mLayout;
	/**
	 * Keyboard
	 */
	private RelativeLayout mKLayout;
	/**
	 * Biến bool trả về true khi edittext đang được edit
	 */
	private boolean isEdit = false;
	/**
	 * Viết hoa
	 */
	private String mUpper = "upper";
	/**
	 * Viết thường
	 */
	private String mLower = "lower";

	/**
	 * độ rọng nút theo màn hình
	 */
	private int w;
	/**
	 * Độ rộng của màn hình hiển thị
	 */
	private int mWindowWidth;
	/**
	 * Các phím viết thường
	 */
	private String sL[] = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w",
			"x", "y", "z", ".", "]", "[", ":", ";", "," };
	/**
	 * Các phím viết hoa
	 */
	private String cL[] = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
			"X", "Y", "Z", ".", "]", "[", ":", ";", "," };
	/**
	 * các kí tự đặc biệt
	 */
	private String nS[] = { "!", ")", "'", "#", "3", "$", "%", "&", "8", "*",
			"?", "/", "+", "-", "9", "0", "1", "4", "@", "5", "7", "(", "2",
			"\"", "6", "_", ".", "]", "[", "<", ">", "," };
	/**
	 * các nút của bàn phím
	 */
	private Button mB[] = new Button[32];

	/**
	 * hàm tạo activity
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// new add
		// adjusting key regarding window sizes

		super.onCreate(savedInstanceState);
		setContentView(R.layout.textinputzoom);
		setKeys();
		setFrow();
		setSrow();
		setTrow();
		setForow();

		// init
		inputzoom = (EditText) findViewById(R.id.InputZoom);
		inputzoom.setOnTouchListener(this);
		inputzoom.setOnFocusChangeListener(this);
		inputzoom.setOnClickListener(this);
		mLayout = (RelativeLayout) findViewById(R.id.xK1);
		mKLayout = (RelativeLayout) findViewById(R.id.xKeyBoard);

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
				if (event.getAction() == KeyEvent.ACTION_DOWN) {
					return VietKeyListenner.setKey(inputzoom, keyCode, event);
				}
				return false;
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
	 * Hàm tạo Option Menu
	 * 
	 * @param menu
	 *            Menu cần tạo
	 * @return true
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
	 * 
	 * @param item
	 *            item được chọn trong option menu
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
	 * Hàm trả về kết quả khi kết thúc activity chọn file text Khi chọn xong
	 * file text,sẽ hiển thị text của file lên textview
	 * 
	 * @param requestCode
	 *            request code mà activity fileinputzoom gửi đến sự kiện chọn
	 *            file text
	 * @param resultCode
	 *            kết quả trả lại từ activity chọn file
	 * @param data
	 *            dữ liệu gửi về từ activity chọn file
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

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v == inputzoom && hasFocus == true) {
			isEdit = true;

		}

	}

	@Override
	public void onClick(View v) {
		if (v == mBChange) {

			if (mBChange.getTag().equals(mUpper)) {
				changeSmallLetters();
				changeSmallTags();
			} else if (mBChange.getTag().equals(mLower)) {
				changeCapitalLetters();
				changeCapitalTags();
			}

		} else if (v != mBdone && v != mBack && v != mBChange && v != mNum) {
			addText(v);

		} else if (v == mBdone) {

			disableKeyboard();

		} else if (v == mBack) {
			isBack(v);
		} else if (v == mNum) {
			String nTag = (String) mNum.getTag();
			if (nTag.equals("num")) {
				changeSyNuLetters();
				changeSyNuTags();
				mBChange.setVisibility(Button.INVISIBLE);

			}
			if (nTag.equals("ABC")) {
				changeCapitalLetters();
				changeCapitalTags();
			}

		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v == inputzoom) {
			hideDefaultKeyboard();
			enableKeyboard();

		}
		return true;
	}

	/**
	 * Hàm nhét text vào view sau khi nhận sự kiện từ bàn phím ảo
	 * 
	 * @param v
	 *            view cần nhập text
	 * @see View
	 */
	private void addText(View v) {
		if (isEdit == true) {
			String b = "";
			b = (String) v.getTag();
			if (b != null) {
				// adding text in Edittext
				inputzoom.append(b);
				VietKeyListenner.setKey3(inputzoom);

			}
		}
	}

	/**
	 * Hàm xử lý khi có sự kiện ấn nút back
	 * 
	 * @param v
	 *            view cần xử lý text
	 * @see View
	 */
	private void isBack(View v) {
		if (isEdit == true) {
			CharSequence cc = inputzoom.getText();
			if (cc != null && cc.length() > 0) {
				{
					inputzoom.setText("");
					inputzoom.append(cc.subSequence(0, cc.length() - 1));

				}

			}
		}
	}

	/**
	 * hàm xử lý sự kiện khi nhấn nút chuyển từ bàn phím số về bàn phím chữ cái
	 * xử lý với Text của Button
	 */
	private void changeSmallLetters() {
		mBChange.setVisibility(Button.VISIBLE);
		for (int i = 0; i < sL.length; i++)
			mB[i].setText(sL[i]);
		mNum.setTag("123");
	}

	/**
	 * hàm xử lý sự kiện khi nhấn nút chuyển từ bàn phím số về bàn phím chữ cái
	 * xử lý với tag của Button
	 */
	private void changeSmallTags() {
		for (int i = 0; i < sL.length; i++)
			mB[i].setTag(sL[i]);
		mBChange.setTag("lower");
		mNum.setTag("num");
	}

	/**
	 * hàm xử lý sự kiện khi ấn chuyển sang chữ hoa xử lý với text của Button
	 */
	private void changeCapitalLetters() {
		mBChange.setVisibility(Button.VISIBLE);
		for (int i = 0; i < cL.length; i++)
			mB[i].setText(cL[i]);
		mBChange.setTag("upper");
		mNum.setText("123");

	}

	/**
	 * hàm xử lý sự kiện khi ấn chuyển sang chữ hoa xử lý với tag của Button
	 */
	private void changeCapitalTags() {
		for (int i = 0; i < cL.length; i++)
			mB[i].setTag(cL[i]);
		mNum.setTag("num");

	}

	/**
	 * hàm xử lý sự kiện khi ấn chuyển sang bàn phím số xử lý với Text
	 */
	private void changeSyNuLetters() {

		for (int i = 0; i < nS.length; i++)
			mB[i].setText(nS[i]);
		mNum.setText("ABC");
	}

	/**
	 * hàm xử lý sự kiện khi ấn chuyển sang bàn phím số xử lý với Tag
	 */
	private void changeSyNuTags() {
		for (int i = 0; i < nS.length; i++)
			mB[i].setTag(nS[i]);
		mNum.setTag("ABC");
	}

	/**
	 * hàm hiện custom keyboard
	 */
	private void enableKeyboard() {

		mLayout.setVisibility(RelativeLayout.VISIBLE);
		mKLayout.setVisibility(RelativeLayout.VISIBLE);

	}

	/**
	 * hàm ẩn custom keyboard
	 */
	private void disableKeyboard() {
		mLayout.setVisibility(RelativeLayout.INVISIBLE);
		mKLayout.setVisibility(RelativeLayout.INVISIBLE);

	}

	/**
	 * hàm ẩn softkeyboard default của thiết bị tránh trường hợp soft keyboard
	 * không tương thích
	 */
	private void hideDefaultKeyboard() {
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}

	/**
	 * Hàm đặt độ rộng,độ dài các nút của bàn phím theo độ rộng khung màn hình
	 * hàng thứ nhất
	 */
	private void setFrow() {
		w = (mWindowWidth / 13);
		w = w - 15;
		mB[16].setWidth(w);
		mB[22].setWidth(w + 3);
		mB[4].setWidth(w);
		mB[17].setWidth(w);
		mB[19].setWidth(w);
		mB[24].setWidth(w);
		mB[20].setWidth(w);
		mB[8].setWidth(w);
		mB[14].setWidth(w);
		mB[15].setWidth(w);
		mB[16].setHeight(50);
		mB[22].setHeight(50);
		mB[4].setHeight(50);
		mB[17].setHeight(50);
		mB[19].setHeight(50);
		mB[24].setHeight(50);
		mB[20].setHeight(50);
		mB[8].setHeight(50);
		mB[14].setHeight(50);
		mB[15].setHeight(50);

	}

	/**
	 * Hàm đặt độ rộng,độ dài các nút của bàn phím theo độ rộng khung màn hình
	 * hàng thứ hai
	 */
	private void setSrow() {
		w = (mWindowWidth / 10);
		mB[0].setWidth(w);
		mB[18].setWidth(w);
		mB[3].setWidth(w);
		mB[5].setWidth(w);
		mB[6].setWidth(w);
		mB[7].setWidth(w);
		mB[26].setWidth(w);
		mB[9].setWidth(w);
		mB[10].setWidth(w);
		mB[11].setWidth(w);
		mB[26].setWidth(w);

		mB[0].setHeight(50);
		mB[18].setHeight(50);
		mB[3].setHeight(50);
		mB[5].setHeight(50);
		mB[6].setHeight(50);
		mB[7].setHeight(50);
		mB[9].setHeight(50);
		mB[10].setHeight(50);
		mB[11].setHeight(50);
		mB[26].setHeight(50);
	}

	/**
	 * Hàm đặt độ rộng,độ dài các nút của bàn phím theo độ rộng khung màn hình
	 * hàng thứ ba
	 */
	private void setTrow() {
		w = (mWindowWidth / 12);
		mB[25].setWidth(w);
		mB[23].setWidth(w);
		mB[2].setWidth(w);
		mB[21].setWidth(w);
		mB[1].setWidth(w);
		mB[13].setWidth(w);
		mB[12].setWidth(w);
		mB[27].setWidth(w);
		mB[28].setWidth(w);
		mBack.setWidth(w);

		mB[25].setHeight(50);
		mB[23].setHeight(50);
		mB[2].setHeight(50);
		mB[21].setHeight(50);
		mB[1].setHeight(50);
		mB[13].setHeight(50);
		mB[12].setHeight(50);
		mB[27].setHeight(50);
		mB[28].setHeight(50);
		mBack.setHeight(50);

	}

	/**
	 * Hàm đặt độ rộng,độ dài các nút của bàn phím theo độ rộng khung màn hình
	 * hàng thứ tư
	 */
	private void setForow() {
		w = (mWindowWidth / 10);
		mBSpace.setWidth(w * 4);
		mBSpace.setHeight(50);
		mB[29].setWidth(w);
		mB[29].setHeight(50);

		mB[30].setWidth(w);
		mB[30].setHeight(50);

		mB[31].setHeight(50);
		mB[31].setWidth(w);
		mBdone.setWidth(w + (w / 1));
		mBdone.setHeight(50);

	}

	/**
	 * Map các nút từ file xml vào các Button trong main
	 */
	private void setKeys() {
		mWindowWidth = getWindowManager().getDefaultDisplay().getWidth(); // getting
		// window
		// height
		// getting ids from xml files
		mB[0] = (Button) findViewById(R.id.xA);
		mB[1] = (Button) findViewById(R.id.xB);
		mB[2] = (Button) findViewById(R.id.xC);
		mB[3] = (Button) findViewById(R.id.xD);
		mB[4] = (Button) findViewById(R.id.xE);
		mB[5] = (Button) findViewById(R.id.xF);
		mB[6] = (Button) findViewById(R.id.xG);
		mB[7] = (Button) findViewById(R.id.xH);
		mB[8] = (Button) findViewById(R.id.xI);
		mB[9] = (Button) findViewById(R.id.xJ);
		mB[10] = (Button) findViewById(R.id.xK);
		mB[11] = (Button) findViewById(R.id.xL);
		mB[12] = (Button) findViewById(R.id.xM);
		mB[13] = (Button) findViewById(R.id.xN);
		mB[14] = (Button) findViewById(R.id.xO);
		mB[15] = (Button) findViewById(R.id.xP);
		mB[16] = (Button) findViewById(R.id.xQ);
		mB[17] = (Button) findViewById(R.id.xR);
		mB[18] = (Button) findViewById(R.id.xS);
		mB[19] = (Button) findViewById(R.id.xT);
		mB[20] = (Button) findViewById(R.id.xU);
		mB[21] = (Button) findViewById(R.id.xV);
		mB[22] = (Button) findViewById(R.id.xW);
		mB[23] = (Button) findViewById(R.id.xX);
		mB[24] = (Button) findViewById(R.id.xY);
		mB[25] = (Button) findViewById(R.id.xZ);
		mB[26] = (Button) findViewById(R.id.xS1);
		mB[27] = (Button) findViewById(R.id.xS2);
		mB[28] = (Button) findViewById(R.id.xS3);
		mB[29] = (Button) findViewById(R.id.xS4);
		mB[30] = (Button) findViewById(R.id.xS5);
		mB[31] = (Button) findViewById(R.id.xS6);
		mBSpace = (Button) findViewById(R.id.xSpace);
		mBdone = (Button) findViewById(R.id.xDone);
		mBChange = (Button) findViewById(R.id.xChange);
		mBack = (Button) findViewById(R.id.xBack);
		mNum = (Button) findViewById(R.id.xNum);
		for (int i = 0; i < mB.length; i++)
			mB[i].setOnClickListener(this);
		mBSpace.setOnClickListener(this);
		mBdone.setOnClickListener(this);
		mBack.setOnClickListener(this);
		mBChange.setOnClickListener(this);
		mNum.setOnClickListener(this);

	}

}
