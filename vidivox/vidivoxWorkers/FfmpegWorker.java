package vidivoxWorkers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingWorker;

import vidivoxManagers.ExportProgressMonitor;

/**
 * Creates an ffmpeg command string based on provided input and spawns an ffmpeg
 * process which creates a temporary video file which is then copied to the
 * input video and deleted
 * 
 * @author jay
 *
 */
public class FfmpegWorker extends SwingWorker<Void, Integer> {
	private long _totalTime = 0;
	private String _exportVideo = "";
	private String _tempFile = System.getProperty("user.home") + "/.vidivox_temp_merged_output.avi";
	private String _command = "";
	private ExportProgressMonitor _monitor = null;

	public FfmpegWorker(String exportVideo, long totalTime, ExportProgressMonitor monitor, String audioPath,
			Long startTime, Double duration, Double volume) {
		_totalTime = totalTime;
		_exportVideo = exportVideo;
		_monitor = monitor;
		//constructs a random file to reduce likelihood of overwrite concerns
		_tempFile = System.getProperty("user.home") + "/." + UUID.randomUUID().toString() + ".avi";

		_command = _command + "ffmpeg -y -i \'" + _exportVideo + "\' -i \'" + audioPath + "\' -filter_complex \"";
		// If a duration is set, use the atrim command
		if (duration != null) {
			_command = _command + "[1:a]atrim=end=" + duration + "[aud1];";
			_command = _command + "[aud1]volume=" + volume + "[aud2];";
		} else {
			_command = _command + "[1:a]volume=" + volume + "[aud2];";
		}
		// if a start time is set, use the adelay command
		if (startTime != 0) {
			_command = _command + "[aud2]adelay=" + startTime + "|" + startTime + "|" + startTime + "[aud3];";
			_command = _command + "[aud3][0:a]amix=inputs=2\" \'" + _tempFile + "\'";
		} else {
			_command = _command + "[aud2][0:a]amix=inputs=2\" \'" + _tempFile + "\'";
		}
	}

	@Override
	protected Void doInBackground() throws Exception {
		ProcessBuilder build = new ProcessBuilder("/bin/bash", "-c", _command);
		build.redirectErrorStream(true);
		Process process = build.start();
		BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		while ((line = bf.readLine()) != null) {
			if (line.contains("time=")) {
				String ffmpegTime = line.substring((line.indexOf("time=") + 5), (line.indexOf(" bitrate=") - 3));
				String[] hhmmss = ffmpegTime.split(":");
				long currentTime = 0;
				currentTime += TimeUnit.HOURS.toMillis(Integer.parseInt(hhmmss[0]));
				currentTime += TimeUnit.MINUTES.toMillis(Integer.parseInt(hhmmss[1]));
				currentTime += TimeUnit.SECONDS.toMillis(Integer.parseInt(hhmmss[2]));
				// publishes the time given by ffmpeg
				publish((int) ((100 * currentTime) / _totalTime));
			}

		}
		process.waitFor();
		try {
			Files.copy(Paths.get(_tempFile), Paths.get(_exportVideo), StandardCopyOption.REPLACE_EXISTING);
			Files.delete(Paths.get(_tempFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void process(List<Integer> chunks) {
		for (int i : chunks) {
			// sets the monitor progress to the current progress in the ffmpeg
			// operation
			_monitor.publishEditProgress(i);
		}
	}

	@Override
	protected void done() {
		//resets the monitor progress
		_monitor.publishEditProgress(0);
	}
}
