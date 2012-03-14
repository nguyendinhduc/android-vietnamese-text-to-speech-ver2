package ktmt.k52.viettts;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.impl.client.DefaultHttpClient;

import ktmt.k52.viettts.MediaList.ListMediaAdapter;
import ktmt.k52.viettts.MediaList.MediaList;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Lớp này sử dụng để stream file audio từ link có sẵn và bật ra loa
 * <p>
 * Quy trình sẽ là :Từ link có sẵn,ta download file audio về file buffer.
 * Khi buffer đủ lượng nhất định,copy buffer sang file khác và play file đó
 * đến khi chạy hết file copy,tiếp tục copy file buffer đã download về sang file khác
 * và tua đến đoạn file copy trước đã chạy
 * Khi đã play hết cả file,copy file đầy đủ ra thẻ nhớ và xóa hết các file buffer còn lại 
 * 
 * @author DungNT
 */
public class StreamMedia {

	/**
	 * Dữ liệu buffer sẽ là 40kb
	 */
	private static final int INTIAL_KB_BUFFER = 32 * 10 / 8;// assume
															// 32kbps*10secss/8bits
															// per byte
	/**
	 * Status để hiển thị quá trình download file
	 * 
	 * @see TextView
	 */
	private TextView textStreamed;
	
	/**
	 * Điều khiển nút Submit,khi đang request đến server ->nút này phải không ấn được
	 * và ngược lại khi đã xong thì sẽ ấn lại được
	 * 
	 * @see ImageButton
	 */
	private ImageButton btSumit;
	
	/**
	 * Điều khiển nút Disconnect,khi đang request đến server ->nút này phải ấn được
	 * và ngược lại khi đã xong thì sẽ không ấn được
	 * 
	 * @see ImageButton
	 */
	private ImageButton btDisconnect;
	/**
	 * Update các file nhạc đã down về và đưa ra danh sách,hỗ trợ cho 
	 * việc đưa danh sách các file nhạc đã down ra list
	 * 
	 * @see ArrayList
	 * @see MediaList
	 */
	private ArrayList<MediaList> arrayWork;
	/**
	 * Hỗ trợ cho việc tạo list
	 * 
	 * @see ListMediaAdapter
	 */
	private ListMediaAdapter arrayAdapter;
	/**
	 *Kích thước file audio cần down
	 */
	private int downloadFileSizeinKB;

	/**
	 * số kb đã download được của file audio
	 */
	private int totalKbRead = 0;

	/**
	 * Xử lý các thread sao cho không bị xung đột
	 * @see Handler
	 */
	private final Handler handler = new Handler();

	/**
	 * lớp dùng để chạy các file audio trên android
	 * @see MediaPlayer
	 */
	private MediaPlayer mediaPlayer;

	/**
	 * File buffer tạm khi down file audio trên server về
	 * @see File
	 */
	private File downloadingMediaFile;
	
	/**
	 * File trên thẻ nhớ,sau khi file buffer tạm đã down xong thì copy
	 * sang file này để người sử dụng tiện theo dõi
	 * @see File
	 */
	private File fileLocation;

	/**
	 * Có lỗi xảy ra trong quá trình tải file hay không
	 */
	private boolean isInterrupted;
	/**
	 * 
	 */
	private Context context;
	
	/**
	 * Hỗ trợ việc tạo file tạm
	 */
	private int counter = 0;

	/**
	 * hàm khởi tạo,chứa các tham số từ main UI gọi đến
	 * @param context 
	 * @param textStreamed status để cập nhật tiến trình down file
	 * @param listArrayWork dùng để cập nhật file mới được down và đưa vào list
	 * @param listArrayAdapter hỗ trợ cho listArrayWork
	 * @param mybtSubmit điều khiển nút submit
	 * @param mybtDisconnect điều khiển nút disconnect
	 * @see Context
	 * @see TextView
	 * @see MediaList
	 * @see ListMediaAdapter
	 * @see ImageButton
	 */
	public StreamMedia(Context context, TextView textStreamed,
			ArrayList<MediaList> listArrayWork,
			ListMediaAdapter listArrayAdapter, ImageButton mybtSubmit,ImageButton mybtDisconnect) {
		this.context = context;
		this.textStreamed = textStreamed;
		this.arrayWork = listArrayWork;
		this.arrayAdapter = listArrayAdapter;
		this.btSumit = mybtSubmit;
		this.btDisconnect = mybtDisconnect;

	}

	/**
	 * download file audio về một vị trí tạm thời,đồng thời cập nhật {@link MediaPlayer}
	 * để chạy file này
	 * @param mediaUrl link của file audio
	 * @param mediaName tên của file audio khi đặt trong thẻ nhớ
	 * @throws IOException lỗi xảy ra khi vào ra dữ liệu
	 */
	public void startStreaming(final String mediaUrl, final String mediaName)
			throws IOException {

		
		URL url = new URL(mediaUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.connect();
		int file_size_inkb = urlConnection.getContentLength() / 1024;
		this.downloadFileSizeinKB = file_size_inkb;

		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					
					downloadAudioIncrement(mediaUrl, mediaName);
				} catch (IOException e) {
					Log.e(getClass().getName(),
							"Unable to initialize the MediaPlayer for fileUrl="
									+ mediaUrl, e);
					return;
				}
			}
		};
		new Thread(r).start();
	}

	/**
	 * Download file audio stream về 1 vị trí tạm thời,update status 
	 * và khi đủ buffer size thì copy và chạy file
	 * @param mediaUrl link của file audio
	 * @param mediaName tên của file audio khi đặt trong thẻ nhớ
	 * @throws IOException lỗi xảy ra khi vào ra dữ liệu
	 */
	public void downloadAudioIncrement(String mediaUrl, String mediaName)
			throws IOException {

		URLConnection cn = new URL(mediaUrl).openConnection();
		cn.connect();
		InputStream stream = cn.getInputStream();
		if (stream == null) {
			Log.e(getClass().getName(),
					"Unable to create InputStream for mediaUrl:" + mediaUrl);
		}

		fileLocation = new File(Environment.getExternalStorageDirectory(),
				mediaName);
		downloadingMediaFile = new File(context.getCacheDir(),
				"downloadingMedia.dat");

		
		if (downloadingMediaFile.exists()) {
			downloadingMediaFile.delete();
		}

		FileOutputStream out = new FileOutputStream(downloadingMediaFile);
		byte buf[] = new byte[16384];
		int totalBytesRead = 0;
		do {
			int numread = stream.read(buf);
			if (numread <= 0)
				break;
			out.write(buf, 0, numread);
			totalBytesRead += numread;

			totalKbRead = totalBytesRead / 1000;

			testMediaBuffer();

			fireDataLoadUpdate();
		} while (validateNotInterrupted());
		stream.close();
		if (validateNotInterrupted()) {
			fireDataFullyLoaded();
		}
	}

	/**
	 *
	 */
	private boolean validateNotInterrupted() {
		if (isInterrupted) {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				// mediaPlayer.release();
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Kiểm tra khi nào ta cần truyền dữ liệu đã buffer vào cho {@link MediaPlayer}
	 * Vì có tương tác để các phần tử của main UI nên để tránh xung đột,ta cần
	 * sử dụng {@link Handler}
	 */
	private void testMediaBuffer() {
		Runnable updater = new Runnable() {
			@Override
			public void run() {
				if (mediaPlayer == null) {
					// Chỉ khởi tạo mediaplayer khi đã có đủ dữ liệu buffer,hoặc dữ liệu
					//đã down xong trong trường hợp kích thước dữ liệu < buffer size
					if (totalKbRead >= INTIAL_KB_BUFFER
							|| totalKbRead == downloadFileSizeinKB) {
						try {
							startMediaPlayer();
						} catch (Exception e) {
							Log.e(getClass().getName(),
									"Error copying buffered conent.", e);
						}
					}
				} else if (mediaPlayer.getDuration()
						- mediaPlayer.getCurrentPosition() <= 1000) {
					//NOTE:media player sẽ dừng khi hết buffer,khi đó ta cần đưa thêm dữ liệu
					//đã down về cho mediaplayer chạy
					//ta dừng trước 1s vì thường media player có thể dừng khi vẫn còn vài minisecond
					//dữ liệu
					transferBufferToMediaPlayer();
				}
			}
		};
		handler.post(updater);
	}

	/**
	 * Khởi tạo {@link MediaPlayer} để chạy {@link File}
	 * @param mediaPath File cần chạy,local
	 * @throws IOException lỗi xảy ra khi vào ra dữ liệu
	 */
	public void startMediaPlayer(File mediaPath) throws IOException {

		mediaPlayer = createMediaPlayer(mediaPath);
		// We have pre-loaded enough content and started the MediaPlayer so
		// update the buttons & progress meters.
		mediaPlayer.start();
	}

	/**
	 * Khi dữ liệu đầu tiên down về đã buffer đủ thì ta copy file download đó
	 * ra file buffer khác,sở dĩ như vậy vì khi dữ liệu đang download,ta chạy trực tiếp
	 * thì sẽ gây ra xung đột,vì vậy cần copy ra file khác để chạy
	 * khởi tạo {@link MediaPlayer} để chạy buffer
	 */
	private void startMediaPlayer() {
		try {
			moveFile(downloadingMediaFile, fileLocation);

			Log.e(getClass().getName(),
					"Buffered File path: " + fileLocation.getAbsolutePath());
			Log.e(getClass().getName(),
					"Buffered File length: " + fileLocation.length() + "");

			mediaPlayer = createMediaPlayer(fileLocation);
			mediaPlayer.start();

		} catch (IOException e) {
			Log.e(getClass().getName(), "Error initializing the MediaPlayer.",
					e);
			return;
		}
	}

	
	/**
	 * Khởi tạo {@link MediaPlayer}
	 * đặt dataSource cho  {@link MediaPlayer} là {@link File}
	 * @param mediaPath File cần chạy
	 * @throws IOException lỗi xảy ra khi vào ra dữ liệu
	 */
	private MediaPlayer createMediaPlayer(File mediaFile) throws IOException {
		MediaPlayer mPlayer = new MediaPlayer();
		mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				Log.e(getClass().getName(), "Error in MediaPlayer: (" + what
						+ ") with extra (" + extra + ")");
				return false;
			}
		});

		FileInputStream fis = new FileInputStream(mediaFile);
		mPlayer.setDataSource(fis.getFD());
		mPlayer.prepare();

		return mPlayer;
	}

	/**
	 * Khi file buffer đầu tiên đã được copy và chạy hết,ta cần buffer tiếp tục
	 * để {@link MediaPlayer} có thể chạy tiếp.
	 * Vì có tương tác để các phần tử của main UI nên để tránh xung đột,ta cần
	 * sử dụng {@link Handler}
	 */
	private void transferBufferToMediaPlayer() {
		try {
		
			boolean wasPlaying = mediaPlayer.isPlaying();
			int curPosition = mediaPlayer.getCurrentPosition();

			

			File bufferedFile = new File(context.getCacheDir(), "playingMedia"
					+ (counter++) + ".dat");

			
			moveFile(downloadingMediaFile, bufferedFile);

			
			mediaPlayer.pause();

			
			mediaPlayer = createMediaPlayer(bufferedFile);
			mediaPlayer.seekTo(curPosition);

			
			boolean atEndOfFile = mediaPlayer.getDuration()
					- mediaPlayer.getCurrentPosition() <= 1000;
			if (wasPlaying || atEndOfFile) {
				mediaPlayer.start();
			}
			bufferedFile.delete();
		} catch (Exception e) {
			Log.e(getClass().getName(),
					"Error updating to newly loaded content.", e);
		}
	}

	/**
	 * Cập nhật status khi đang download
	 * Vì chạy song song với việc download nên ta cần chạy dưới 1 thread khác
	 * @see Runnable
	 */
	private void fireDataLoadUpdate() {
		Runnable updater = new Runnable() {
			@Override
			public void run() {
				textStreamed.setText((totalKbRead + " Kb read"));

			}
		};
		handler.post(updater);
	}

	/**
	 * Cập nhật status khi đã download xong file audio
	 * copy file tạm dùng để download audio ra thẻ nhớ và xóa file tạm đó 
	 * Vì chạy song song với việc download nên ta cần chạy dưới 1 thread khác
	 * @see Runnable
	 */
	private void fireDataFullyLoaded() {
		Runnable updater = new Runnable() {
			@Override
			public void run() {
				transferBufferToMediaPlayer();
				
				if (fileLocation.exists()) {
					fileLocation.delete();
				}
				try {
					moveFile(downloadingMediaFile, fileLocation);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				downloadingMediaFile.delete();
				textStreamed
						.setText(("Audio full loaded: " + totalKbRead + " Kb read"));

				btSumit.setEnabled(true);
				btDisconnect.setEnabled(false);
				MediaList medialist = new MediaList(fileLocation.getName(),
						fileLocation.getAbsolutePath());
				arrayWork.add(medialist);
				arrayAdapter.notifyDataSetChanged();
			}
		};
		handler.post(updater);
	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}

	/**
	 * Sử dụng để pause {@link MediaPlayer} 
	 */
	public void pause() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}

	/**
	 * Sử dụng để stop {@link MediaPlayer} 
	 */
	public void stop() {

		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}

	}

	/**
	 * Sử dụng để tiếp tục {@link MediaPlayer} sau khi pause
	 */
	public void resume() {
		if (mediaPlayer != null) {
			mediaPlayer.start();
		}
	}

	/**
	 * Sử dụng để reset {@link MediaPlayer} về từ đầu
	 */
	public void reset() {
		if (mediaPlayer != null) {
			mediaPlayer.seekTo(0);
		}
	}

	/**
	 * Dừng download
	 */
	public void interrupt() {

		isInterrupted = true;
		validateNotInterrupted();
		fileLocation.delete();
		downloadingMediaFile.delete();
		PauseStatusUpdate();
	}

	/**
	 * Cập nhật status khi đang download
	 * Vì chạy song song với việc download nên ta cần chạy dưới 1 thread khác
	 * @see Runnable
	 */
	private void PauseStatusUpdate() {
		Runnable updater = new Runnable() {
			@Override
			public void run() {
				textStreamed.setText("connecting interupt");

			}
		};
		handler.post(updater);
	}
	/**
	 * copy file từ vị trí này đến vị trí khác
	 * @param oldLocation file cần chuyển
	 * @param newLocation file mới
	 * @throws IOException lỗi vào ra dữ liệu
	 * @see File
	 * @see IOException
	 */
	public void moveFile(File oldLocation, File newLocation) throws IOException {

		if (oldLocation.exists()) {
			BufferedInputStream reader = new BufferedInputStream(
					new FileInputStream(oldLocation));
			BufferedOutputStream writer = new BufferedOutputStream(
					new FileOutputStream(newLocation, false));
			try {
				byte[] buff = new byte[8192];
				int numChars;
				while ((numChars = reader.read(buff, 0, buff.length)) != -1) {
					writer.write(buff, 0, numChars);
				}
			} catch (IOException ex) {
				throw new IOException("IOException when transferring "
						+ oldLocation.getPath() + " to "
						+ newLocation.getPath());
			} finally {
				try {
					if (reader != null) {
						writer.close();
						reader.close();
					}
				} catch (IOException ex) {
					Log.e(getClass().getName(),
							"Error closing files when transferring "
									+ oldLocation.getPath() + " to "
									+ newLocation.getPath());
				}
			}
		} else {
			throw new IOException(
					"Old location does not exist when transferring "
							+ oldLocation.getPath() + " to "
							+ newLocation.getPath());
		}
	}
}
