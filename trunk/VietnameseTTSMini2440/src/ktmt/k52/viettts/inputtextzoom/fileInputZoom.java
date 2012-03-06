package ktmt.k52.viettts.inputtextzoom;

import ktmt.k52.viettts.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class fileInputZoom extends Activity{

	private TextView inputzoom;
	private ImageButton clearInput,goBack,returnHome;
	private String textinput;
	private String backupInput;
	// Các hằng dùng cho tạo Option Menu
		private static final int OK = Menu.FIRST;
		private static final int CANCEL = Menu.FIRST + 2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.textinputzoom);
		
		//init
		inputzoom = (TextView)findViewById(R.id.InputZoom);
		clearInput = (ImageButton)findViewById(R.id.clearInputZoom);
		
		goBack = (ImageButton)findViewById(R.id.back);
		returnHome=(ImageButton)findViewById(R.id.btReturn);
		
		//dua text nhan cua main activity vao input text
		Bundle receiver = this.getIntent().getExtras();
			backupInput = receiver.getString("textInputComming");
		
		inputzoom.setText(backupInput);
		
		//cac su kien nut an
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
			menu.add(0, OK, 0, "Return")
					.setIcon(R.drawable.okicon);
			menu.add(0, CANCEL, 0, "Cancel").setIcon(
					R.drawable.closeicon);
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

}
