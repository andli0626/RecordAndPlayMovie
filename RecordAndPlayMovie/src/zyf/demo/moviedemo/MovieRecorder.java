package zyf.demo.moviedemo;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceView;

public class MovieRecorder {
	private MediaRecorder mediarecorder;
	boolean isRecording;

	public void startRecording(SurfaceView surfaceView) {
		mediarecorder = new MediaRecorder();
		// 设置麦克风采集声音
		mediarecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// 设置录制视频源为Camera(相机)
		mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
		// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
		mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		// 设置声音编码格式
		mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		// 设置录制的视频编码h263 h264
		mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
		// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
		// mediarecorder.setVideoSize(176, 144);
		// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
		mediarecorder.setVideoFrameRate(20);
		mediarecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
		// 设置视频文件输出的路径
		lastFileName = newFileName();
		mediarecorder.setOutputFile(lastFileName);
		try {
			// 准备录制
			mediarecorder.prepare();
			// 开始录制
			mediarecorder.start();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		isRecording = true;
		timeSize = 0;
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				timeSize++;
			}
		}, 0, 1000);
	}

	Timer timer;
	int timeSize = 0;

	private String lastFileName;

	public void stopRecording() {
		if (mediarecorder != null) {
			// 停止
			mediarecorder.stop();
			mediarecorder.release();
			mediarecorder = null;

			timer.cancel();
			if (null != lastFileName && !"".equals(lastFileName)) {
				// /data/data/zyf.demo.moviedemo/cache/mov_462399099.3gp
				File f = new File(lastFileName);
				// mov_462399099_14s.3gp
				String name = f.getName().substring(0,f.getName().lastIndexOf(".3gp"));
				name += "_" + timeSize + "s.3gp";
				// /data/data/zyf.demo.moviedemo/cache/mov_462399099_14s.3gp
				String newPath = f.getParentFile().getAbsolutePath() + "/"+ name;
//				String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ name;
				if (f.renameTo(new File(newPath))) {
					Log.i("andli","文件重命名成功!");
//					int i = 0;
//					i++;
				}else{
					Log.i("andli","文件重命名失败!");
				}
				
				Log.i("andli","lastFileName= "+lastFileName);
				Log.i("andli","newFilePath = "+newPath);
			}
		}
	}

	public String newFileName() {
		try {
			return File.createTempFile("/mov_", ".3gp").getAbsolutePath();
//			Date date = new Date();
//			SimpleDateFormat df = new SimpleDateFormat("HH-mm-ss");
//			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ df.format(date));
//			return File.createTempFile("/mov_", ".3gp", file).toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void release() {
		if (mediarecorder != null) {
			// 停止
			mediarecorder.stop();
			mediarecorder.release();
			mediarecorder = null;
		}
	}
}
