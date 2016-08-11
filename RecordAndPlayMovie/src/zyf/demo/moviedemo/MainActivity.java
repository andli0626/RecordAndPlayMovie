package zyf.demo.moviedemo;

import java.io.File;
import java.io.FileFilter;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	//private static final String LOG_TAG = MainActivity.class.getSimpleName();

	MoviePlayer mPlayer;
	MovieRecorder mRecorder;

	Button btnRecoder;

	ListView 		mListView;
	SurfaceView 	surfaceView;
	SurfaceHolder 	surfaceHolder;
	MyAdatpter 		listAdp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		// 设置横屏显示
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 选择支持半透明模式,在有surfaceview的activity中使用。
		getWindow().setFormat(PixelFormat.TRANSLUCENT);

		setContentView(R.layout.activity_main);

		btnRecoder = (Button) findViewById(R.id.btnRecoder);
		btnRecoder.setOnClickListener(mRecordingClick);

		surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(surfaceHolderCallback); // holder加入回调接口
		// setType必须设置，要不出错.
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		mPlayer   = new MoviePlayer();
		mRecorder = new MovieRecorder();

		mListView = (ListView) findViewById(R.id.listView1);
		listAdp = new MyAdatpter();
		mListView.setAdapter(listAdp);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						File f = listAdp.fileArray[arg2];
						mPlayer.play(f.getAbsolutePath(), surfaceView);
				}
		});

		refreshViewByRecordingState();
	}

	protected void onDestroy() {
		if (mRecorder != null) {
			mRecorder.release();
		}
		if (mPlayer != null) {
			mPlayer.release();
		}
		super.onDestroy();
	};

	Callback surfaceHolderCallback = new Callback() {
		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			surfaceView = null;
			surfaceHolder = null;
			mRecorder = null;
		}

		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			surfaceHolder = arg0;
		}

		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,int arg3) {
			surfaceHolder = arg0;
		}
	};

	OnClickListener mRecordingClick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (!mRecorder.isRecording) {
				mRecorder.startRecording(surfaceView);
				mRecorder.isRecording = true;
				btnRecoder.setText("录制中，单击停止");
			} else {
				mRecorder.stopRecording();
				mRecorder.isRecording = false;
				btnRecoder.setText("录制录音，单击开始");
				refreshViewByRecordingState();
			}
		}
	};

	/* 刷新状态 */
	protected void refreshViewByRecordingState() {
		if (mRecorder.isRecording) {
			mRecorder.isRecording = true;
			btnRecoder.setText("录制中，单击停止");
		} else {
			mRecorder.isRecording = false;
			btnRecoder.setText("准备录制，单击开始");
		}
		listAdp.query();
		listAdp.notifyDataSetChanged();
	}

	class MyAdatpter extends BaseAdapter {
		File[] fileArray;

		public MyAdatpter() {
			super();
			query();
		}

		public void query() {
//			String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
			String mFileName = getApplicationContext().getCacheDir().getAbsolutePath();
			Log.i("andli","文件路径= "+mFileName);
			File f = new File(mFileName);
			File[] found = f.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					if (pathname.getName().startsWith("mov_")) {
						return true;
					}
					return false;
				}
			});

			fileArray = new File[found.length];
			for (int i = 0; i < found.length; i++) {
				fileArray[i] = found[found.length - i - 1];
			}
		}

		@Override
		public int getCount() {
			return fileArray == null ? 0 : fileArray.length;
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			View v = getLayoutInflater().inflate(R.layout.item, null);
			TextView txtText = (TextView) v.findViewById(R.id.txtText);
			TextView txtSize = (TextView) v.findViewById(R.id.txtSize);

			File f = fileArray[arg0];
			txtText.setText(f.getName());
			txtSize.setText(f.length() / 1024 + " kb");

			return v;
		}

	}
}
