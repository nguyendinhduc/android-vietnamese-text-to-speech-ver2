package ktmt.k52.viettts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ktmt.k52.viettts.FileChooser.FileChooser;
import ktmt.k52.viettts.MediaList.ListMediaAdapter;
import ktmt.k52.viettts.MediaList.MediaList;
import ktmt.k52.viettts.inputtextzoom.fileInputZoom;

import org.apache.http.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class VietnameseTTSMini2440Activity extends Activity {
	/** Called when the activity is first created. */

	private EditText inputText;
	private ImageButton btSubmit, btChoose, btPlay, btStop, btExit, btClear,
			btPause, btReset;
	private SeekBar seekBar;
	private ListView listText;
	private CheckBox cbText;
	private TextView status;

	private final int REQUEST_CODE = 0;
	private final int REQUEST_CODE_INPUT_ZOOM = 1;
	private StreamMedia audioStreamer;

	public static String fileChooserPath;

	// list
	// Các hằng dùng cho tạo Option Menu
	private static final int DELETE_WORK = Menu.FIRST;
	private static final int ABOUT = Menu.FIRST + 2;
	ArrayList<MediaList> array;
	ListMediaAdapter arrayAdapter;

	//

	private final Handler handler = new Handler();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initControl();

		// test
		inputText.setText("Thử nghiệm tiếng nói");

		// đặt sự kiện ấn nút submit
		btSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				btSubmit.setEnabled(false);
				String temp = inputText.getText().toString();

				try {

					status.setText("Checking internet..");

					if (!isOnline()) {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								VietnameseTTSMini2440Activity.this);
						builder.setTitle("Lỗi network");
						builder.setMessage("Làm ơn hãy kiểm tra lại network");

						builder.setPositiveButton("Continue",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								});
						builder.show();
					} else {
						HttpHelp http = new HttpHelp();
						updateText("Connecting to server");

						/*
						 * String response = http.postPageIsolar(temp); String
						 * audioUrl = http.getIsolarAudioUrl(response) .trim();
						 */
						String response = http.postPageVozMe(temp);

						String audioUrl = http.getVozMeAudioUrl(response)
								.trim();

						String mediaName = audioUrl.substring(
								audioUrl.lastIndexOf("/") + 1).trim();

						audioStreamer = new StreamMedia(
								VietnameseTTSMini2440Activity.this, status,
								array, arrayAdapter, btSubmit);

						updateText("Start Streaming");
						audioStreamer.startStreaming(audioUrl, mediaName);

						// String audioUrl =
						// HttpHelp.getIsolarAudioUrl(response);
						// status.setText("Getting audio url..");

						// String mediaName = mediaName(audioUrl);

						// isolor die,test zing
						/*
						 * String audioUrl =
						 * "http://www.downloadtaxi.com/d/1330483872/Baby_ringstone_Justin_Bieber.mp3"
						 * ; String mediaName = audioUrl.substring(audioUrl
						 * .lastIndexOf("/") + 1); audioStreamer = new
						 * StreamMedia( VietnameseTTSMini2440Activity.this,
						 * status, array, arrayAdapter, btSubmit);
						 * audioStreamer.startStreaming(audioUrl, mediaName);
						 */
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					Toast.makeText(VietnameseTTSMini2440Activity.this,
							e.getMessage(), Toast.LENGTH_SHORT).show();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(VietnameseTTSMini2440Activity.this,
							e.getMessage(), Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(VietnameseTTSMini2440Activity.this,
							e.getMessage(), Toast.LENGTH_SHORT).show();
				}

			}
		});

		btPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (audioStreamer != null) {
					audioStreamer.resume();
					primarySeekBarProgressUpdater();
				}

			}
		});

		btPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (audioStreamer != null) {
					audioStreamer.pause();
					primarySeekBarProgressUpdater();
				}

			}
		});

		btStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (audioStreamer != null) {
					audioStreamer.stop();
					seekBar.setProgress(0);
					seekBar.setEnabled(false);
					// primarySeekBarProgressUpdater();
				}

			}
		});

		btReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (audioStreamer != null) {
					audioStreamer.reset();
					audioStreamer.resume();
					primarySeekBarProgressUpdater();
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
										//Xoa cac file am thanh trong list neu co
											if(array.size()!=0)
											{
											for (MediaList media : array) {
												File deleteFile = new File(media.getMediaPath());
												deleteFile.delete();
											}
											array.clear();
											}
										
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

			}
		});

		// nut clear

		btClear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				inputText.setText("");
			}
		});

		// checkbox
		cbText.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (buttonView == cbText) {
					if (isChecked) {
						btChoose.setEnabled(false);
						inputText.setClickable(true);
						inputText.setFocusable(true);
						// tạo intent đề chạy activity file chooser
						Intent inputTextZoom = new Intent(
								VietnameseTTSMini2440Activity.this,
								fileInputZoom.class);
						Bundle sendBundle = new Bundle();
						sendBundle.putString("textInputComming", inputText
								.getText().toString());

						inputTextZoom.putExtras(sendBundle);

						// Set the request code to any code you like, you can
						// identify
						// the callback via this code
						startActivityForResult(inputTextZoom,
								REQUEST_CODE_INPUT_ZOOM);

					} else {
						btChoose.setEnabled(true);
						inputText.setClickable(false);
						inputText.setFocusable(false);
					}
				}

			}
		});

		// list

		listText.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {

				// LinearLayout temp2
				// =(LinearLayout)view.findViewById(R.id.layoutChoose);

				MediaList temp = arrayAdapter.getItem(position);
				Toast.makeText(VietnameseTTSMini2440Activity.this,
						temp.getMediaPath(), Toast.LENGTH_SHORT).show();
				File file = new File(temp.getMediaPath());
				try {
					audioStreamer.startMediaPlayer(file);
				} catch (IOException e) {

					Toast.makeText(VietnameseTTSMini2440Activity.this,
							e.getMessage(), Toast.LENGTH_SHORT).show();
				}

				seekBar.setEnabled(true);
				seekBar.setMax(audioStreamer.getMediaPlayer().getDuration());
				seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (fromUser) {
							audioStreamer.getMediaPlayer().seekTo(progress);
						}

					}
				});

				primarySeekBarProgressUpdater();
			}

		});

	}

	private void initControl() {
		inputText = (EditText) findViewById(R.id.Input);
		inputText.setEnabled(false);
		btSubmit = (ImageButton) findViewById(R.id.submit);
		btChoose = (ImageButton) findViewById(R.id.Choose);
		// btChoose.setEnabled(false);
		btPlay = (ImageButton) findViewById(R.id.play);
		btStop = (ImageButton) findViewById(R.id.stop);
		btExit = (ImageButton) findViewById(R.id.exit);
		btClear = (ImageButton) findViewById(R.id.clearInput);
		btPause = (ImageButton) findViewById(R.id.pause);
		btReset = (ImageButton) findViewById(R.id.reset);

		seekBar = (SeekBar) findViewById(R.id.seek_bar);
		seekBar.setEnabled(false);
		cbText = (CheckBox) findViewById(R.id.Get_text);
		cbText.setChecked(false);
		status = (TextView) findViewById(R.id.text_kb_streamed);

		// list
		listText = (ListView) findViewById(R.id.listfile);

		listText.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		array = new ArrayList<MediaList>();
		arrayAdapter = new ListMediaAdapter(this, R.layout.custommedialist,
				array);
		// set adapter cho list biet de lay noi dung cua mang arraywork
		listText.setAdapter(arrayAdapter);

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

					Toast.makeText(VietnameseTTSMini2440Activity.this,
							e.getMessage(), Toast.LENGTH_SHORT).show();

				}

			}
		} else if (resultCode == RESULT_OK
				&& requestCode == REQUEST_CODE_INPUT_ZOOM) {
			if (data.hasExtra("textInputReturn")) {
				String temp = data.getExtras().getString("textInputReturn");
				inputText.setText(temp);
				cbText.setChecked(false);
			}
		}

	}

	/**
	 * Method which updates the SeekBar primary progress by current song playing
	 * position
	 */
	private void primarySeekBarProgressUpdater() {
		seekBar.setProgress(audioStreamer.getMediaPlayer().getCurrentPosition());
		if (audioStreamer.getMediaPlayer().isPlaying()) {
			Runnable notification = new Runnable() {
				@Override
				public void run() {
					primarySeekBarProgressUpdater();
				}
			};
			handler.postDelayed(notification, 100);
		}
	}

	// Tạo Option Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, DELETE_WORK, 0, "Xóa")
				.setIcon(android.R.drawable.ic_delete);
		menu.add(0, ABOUT, 0, "About").setIcon(
				android.R.drawable.ic_menu_info_details);
		return true;
	}

	// Xử lý sự kiện khi các option trong Option Menu được lựa chọn
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_WORK: {
			deleteCheckedWork();
			break;
		}
		case ABOUT: {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("About");
			builder.setMessage("Tác giả:" + "\n" + "Nguyễn Trung Dũng" + "\n"
					+ "Trà nước:" + "\n" + "Phí Tùng Lâm");
			builder.setPositiveButton("Đóng",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.show();
			break;
		}
		}
		return true;
	}

	private void deleteCheckedWork() {
		if (array.size() > 0) {
			for (int i = 0; i < array.size();) {

				if (array.get(i).isChecked()) {
					MediaList deleteMedia = array.get(i);
					File deleteFile = new File(deleteMedia.getMediaPath());
					deleteFile.delete();
					array.remove(i);
					arrayAdapter.notifyDataSetChanged();

				} else {
					i++;
				}
			}
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}

	private void updateText(final String text) {

		final Runnable update = new Runnable() {
			public void run() {
				// do whatever you want to change here, like:
				status.setText(text);
			}
		};
		final Handler mHandler = new Handler();
		mHandler.post(update);
		// mHandler.postDelayed(update, 15 * 1000);

	}

	
	
	

}