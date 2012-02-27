package ktmt.k52.viettts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import ktmt.k52.viettts.FileChooser.FileChooser;

import org.apache.http.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class VietnameseTTSMini2440Activity extends Activity {
	/** Called when the activity is first created. */

	private EditText inputText;
	private ImageButton btSubmit, btChoose, btPlay, btStop, btExit,btClear,btPause;
	private ListView listText;
	private CheckBox cbText;
	private TextView status;

	private final int REQUEST_CODE = 0;
	private StreamMedia audioStreamer;

	public static String fileChooserPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initControl();

		// test
		inputText.setText("Thử nghiệm tiếng nói");

		// đặt sự kiện ấn nút submit
		btSubmit.setOnClickListener(new OnClickListener() {
			String temp = inputText.getText().toString();

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					String response = HttpHelp.postPageIsolar(temp);
					status.setText("Requesting to isolar..");

					String audioUrl = HttpHelp.getIsolarAudioUrl(response);
					status.setText("Getting audio url..");

					String mediaName = mediaName(audioUrl);
					audioStreamer.startStreaming(audioUrl, mediaName);
					btSubmit.setEnabled(false);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		// nut Exit
		btExit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new AlertDialog.Builder(VietnameseTTSMini2440Activity.this)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setTitle("Xác nhận")
						.setMessage("Bạn thật sự muốn thoát?")
						.setPositiveButton("Vâng",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// Stop the activity
										VietnameseTTSMini2440Activity.this
												.finish();
									}

								}).setNegativeButton("Không", null).show();

			}
		});

		// nút chooser
		btChoose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// tạo intent đề chạy activity file chooser
				Intent fileChoose = new Intent(
						VietnameseTTSMini2440Activity.this, FileChooser.class);
				// Set the request code to any code you like, you can identify
				// the callback via this code
				startActivityForResult(fileChoose, REQUEST_CODE);

				// Toast.makeText(VietnameseTTSMini2440Activity.this,
				// fileChooserPath, Toast.LENGTH_SHORT).show();

			}
		});
		
		
		//nut clear
		
		btClear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				inputText.setText("");
			}
		});
		
		//checkbox
		cbText.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(buttonView==cbText)
				{
					if(isChecked)
					{
						btChoose.setEnabled(false);
						inputText.setClickable(true);
						inputText.setFocusable(true);

					}else
					{
						btChoose.setEnabled(true);
						inputText.setClickable(false);
						inputText.setFocusable(false);
					}
						
				}
				
			}
		});
		

	}

	private void initControl() {
		inputText = (EditText) findViewById(R.id.Input);
		btSubmit = (ImageButton) findViewById(R.id.submit);
		btChoose = (ImageButton) findViewById(R.id.Choose);
		btChoose.setEnabled(false);
		btPlay = (ImageButton) findViewById(R.id.play);
		btStop = (ImageButton) findViewById(R.id.stop);
		btExit = (ImageButton) findViewById(R.id.exit);
		btClear = (ImageButton)findViewById(R.id.clearInput);
		btPause =(ImageButton)findViewById(R.id.pause);
		
		listText = (ListView) findViewById(R.id.list);
		cbText = (CheckBox) findViewById(R.id.Get_text);
		cbText.setChecked(true);
		status = (TextView) findViewById(R.id.text_kb_streamed);

		audioStreamer = new StreamMedia(this, status, btPlay, btSubmit);

	}

	private String mediaName(String mediaUrl) {
		int i = mediaUrl.lastIndexOf("/");
		String mediaName = mediaUrl.substring(i + 1);

		return mediaName;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			if (data.hasExtra("filepath")) {

				fileChooserPath = data.getExtras().getString("filepath");
				
				try {
					File file = new File(fileChooserPath); 
					 FileInputStream fIn = new FileInputStream(file); 

					// Read file with UTF-8

					InputStreamReader isr = new InputStreamReader(fIn, "UTF-8");

					char[] inputBuffer = new char[8192];

					isr.read(inputBuffer);

					String readString = new String(inputBuffer);

					// Load content file on ViewText

					inputText.setText(readString);

				} catch (Exception e) {

					System.out.print(e.getMessage());

				}

			}
		}

	}

}