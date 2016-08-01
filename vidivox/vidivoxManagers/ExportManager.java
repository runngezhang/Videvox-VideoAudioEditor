package vidivoxManagers;

import java.util.ArrayList;

import javax.swing.SwingWorker;

import vidivoxAudio.Audio;
import vidivoxWorkers.FfmpegWorker;

/**
 * Swing worker to manage adding all of the audio to a video file
 * @author jay
 */
public class ExportManager extends SwingWorker<Void, Void> {
	private ArrayList<Audio> _audioList = null;
	private String _videoPath = "";
	private long _totalTime = 0;
	private ExportProgressMonitor _monitor = null;
	
	public ExportManager() {
		this.cancel(true);
	}

	public ExportManager(ArrayList<Audio> audioList, String videoPath, long totalTime, ExportProgressMonitor monitor) {
		_audioList = audioList;
		_videoPath = videoPath;
		_totalTime = totalTime;
		_monitor = monitor;
	}

	@Override
	public Void doInBackground() {
		_monitor.setDone(false);
		//iterates through all files and adds them to the input video
		for (int i = 0; i < _audioList.size(); i++) {
			FfmpegWorker ffmpeg = _audioList.get(i).addToVideo(_videoPath, _totalTime, _monitor);
			while (!(ffmpeg.isDone())) {
				// wait for previous audio to finish being added
			}
		}
		return null;
	}
	
	@Override
	public void done() {
		if(_monitor != null) {
			_monitor.setDone(true);
		}
	}
}
