package ktmt.k52.viettts.inputtextzoom;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import ktmt.k52.viettts.R;
import ktmt.k52.viettts.VietnameseTTSMini2440Activity;
import ktmt.k52.viettts.FileChooser.FileChooser;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class fileInputZoom extends Activity {

	private TextView inputzoom;
	private ImageButton clearInput, goBack, returnHome, btChoose;
	private String textinput;
	private String backupInput;
	// Các hằng dùng cho tạo Option Menu
	private static final int OK = Menu.FIRST;
	private static final int CANCEL = Menu.FIRST + 2;
	protected static final int REQUEST_CODE = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textinputzoom);

		// init
		inputzoom = (TextView) findViewById(R.id.InputZoom);
		clearInput = (ImageButton) findViewById(R.id.clearInputZoom);

		goBack = (ImageButton) findViewById(R.id.back);
		returnHome = (ImageButton) findViewById(R.id.btReturn);

		btChoose = (ImageButton) findViewById(R.id.Choose);

		// dua text nhan cua main activity vao input text
		Bundle receiver = this.getIntent().getExtras();
		backupInput = receiver.getString("textInputComming");

		inputzoom.setText(backupInput);

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

	@Override
	public void finish() {
		// Prepare data intent
		Intent data = new Intent();
		data.putExtra("textInputReturn", textinput);

		// Activity finished ok, return the data
		setResult(RESULT_OK, data);
		super.finish();
	}

	// Tạo Option Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, OK, 0, "Return").setIcon(R.drawable.okicon);
		menu.add(0, CANCEL, 0, "Cancel").setIcon(R.drawable.closeicon);
		return true;
	}

	// Xử lý sự kiện khi các option trong Option Menu được lựa chọn
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

}
