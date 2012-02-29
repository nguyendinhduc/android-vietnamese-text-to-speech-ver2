package ktmt.k52.viettts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import ktmt.k52.viettts.FileChooser.FileChooser;
import ktmt.k52.viettts.MediaList.ListMediaAdapter;
import ktmt.k52.viettts.MediaList.MediaList;

import org.apache.http.ParseException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
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
	private StreamMedia audioStreamer;

	public static String fileChooserPath;

	// list
	// Các hằng dùng cho tạo Option Menu
	private static final int DELETE_WORK = Menu.FIRST;
	private static final int ABOUT = Menu.FIRST + 2;
	ArrayList<MediaList> array;
	ListMediaAdapter arrayAdapter;

	//
	private MediaPlayer mp;
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
			String temp = inputText.getText().toString();

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					/*
					 * String response = HttpHelp.postPageIsolar(temp);
					 * status.setText("Requesting to isolar..");
					 * 
					 * String audioUrl = HttpHelp.getIsolarAudioUrl(response);
					 * status.setText("Getting audio url..");
					 * 
					 * String mediaName = mediaName(audioUrl);
					 */

					// isolor die,test zing
					String audioUrl = "http://dl3.mp3.zdn.vn/tUtYXLhuDGwX1aWn6/1635fe1e50e39d1eb6ed1416357d5e37/4f4c5f50/2011/12/16/d/a/da048ce2f81d6013f287e17919d537a0.mp3?filename=canon%20in%20d-%20nhac%20chuong%20-%20Johann%20Pachelbel.mp3";
					String mediaName = "test.mp3";
					audioStreamer = new StreamMedia(
							VietnameseTTSMini2440Activity.this, status, array,
							arrayAdapter, btSubmit);
					audioStreamer.startStreaming(audioUrl, mediaName);

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		btPlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (audioStreamer != null) {
					audioStreamer.resume();
				}
				if (mp != null) {
					mp.start();
					primarySeekBarProgressUpdater();
				}

			}
		});

		btPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (audioStreamer != null) {
					audioStreamer.pause();
				}
				if (mp != null) {
					mp.pause();
					primarySeekBarProgressUpdater();
				}

			}
		});

		btStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (audioStreamer != null) {
					audioStreamer.stop();
				}
				if (mp != null) {
					mp.stop();
					primarySeekBarProgressUpdater();
				}

			}
		});

		btReset.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (audioStreamer != null) {
					audioStreamer.reset();
				}
				if (mp != null) {
					mp.seekTo(0);
					primarySeekBarProgressUpdater();
					mp.start();
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

					} else {
						btChoose.setEnabled(true);
						inputText.setClickable(false);
						inputText.setFocusable(false);
					}

				}

			}
		});

		// list
		
		listText.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int position, long id) {
				MediaList temp = arrayAdapter.getItem(position);
				Toast.makeText(VietnameseTTSMini2440Activity.this,
						temp.getMediaPath(), Toast.LENGTH_SHORT).show();
				audioPlayer(temp.getMediaPath());

				seekBar.setEnabled(true);
				seekBar.setMax(mp.getDuration());
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
							mp.seekTo(progress);
						}

					}
				});
				primarySeekBarProgressUpdater();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		listText.setSelection(0);
		listText.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {

				MediaList temp = arrayAdapter.getItem(position);
				Toast.makeText(VietnameseTTSMini2440Activity.this,
						temp.getMediaPath(), Toast.LENGTH_SHORT).show();
				audioPlayer(temp.getMediaPath());

				seekBar.setEnabled(true);
				seekBar.setMax(mp.getDuration());
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
							mp.seekTo(progress);
						}

					}
				});

				primarySeekBarProgressUpdater();
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
		btClear = (ImageButton) findViewById(R.id.clearInput);
		btPause = (ImageButton) findViewById(R.id.pause);
		btReset = (ImageButton) findViewById(R.id.reset);

		seekBar = (SeekBar) findViewById(R.id.seek_bar);
		seekBar.setEnabled(false);
		cbText = (CheckBox) findViewById(R.id.Get_text);
		cbText.setChecked(true);
		status = (TextView) findViewById(R.id.text_kb_streamed);

		// list
		listText = (ListView) findViewById(R.id.listfile);
		array = new ArrayList<MediaList>();
		arrayAdapter = new ListMediaAdapter(this, R.layout.custommedialist,
				array);
		// set adapter cho list biet de lay noi dung cua mang arraywork
		listText.setAdapter(arrayAdapter);

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

	public void audioPlayer(String path) {
		// set up MediaPlayer
		if (mp != null) {
			mp.reset();
		} else {
			mp = new MediaPlayer();
		}

		try {

			mp.setDataSource(path);

		} catch (IllegalArgumentException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		} catch (IllegalStateException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		try {

			mp.prepare();

		} catch (IllegalStateException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

		mp.start();

	}

	/**
	 * Method which updates the SeekBar primary progress by current song playing
	 * position
	 */
	private void primarySeekBarProgressUpdater() {
		seekBar.setProgress(mp.getCurrentPosition());
		if (mp.isPlaying()) {
			Runnable notification = new Runnable() {
				public void run() {
					primarySeekBarProgressUpdater();
				}
			};
			handler.postDelayed(notification, 1000);
		}
	}
	

	// Tạo Option Menu
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, DELETE_WORK, 0, "Xóa").setIcon(
				android.R.drawable.ic_delete);
		menu.add(0, ABOUT, 0, "About").setIcon(
				android.R.drawable.ic_menu_info_details);
		return true;
	}
	// Xử lý sự kiện khi các option trong Option Menu được lựa chọn
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
						+ "Trà n:" + "\n" + "Phí Tùng Lâm");
				builder.setPositiveButton("đóng",
						new DialogInterface.OnClickListener() {
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
				for (int i = 0; i < array.size(); i++) {
					if (i > array.size()) {
						break;
					}
					if (array.get(i).isChecked()) {
						MediaList deleteMedia = array.get(i);
						File deleteFile = new File(deleteMedia.getMediaPath());
						deleteFile.delete();
						array.remove(i);
						arrayAdapter.notifyDataSetChanged();
						continue;
					}
				}
			}
		}

}