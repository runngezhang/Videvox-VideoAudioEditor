package vidivoxAudio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import vidivoxManagers.AudioPlayerAdapter;
import vidivoxManagers.ExportProgressMonitor;
import vidivoxWorkers.FfmpegWorker;

/**
 * 
 * @author jay
 *
 */
public class AudioFile implements Audio {
	private String _name = null;
	private String _filePath = null;
	private Long _startTime = null; // where inserted in millis
	private Double _duration = null; // where inserted in seconds
	private double _volume = 1.0;
	private FfmpegWorker ffmpeg = null;
	private AudioPlayerAdapter _audioPlayer = new AudioPlayerAdapter();

	public AudioFile(String filePath, String name) {
		_name = name;
		_filePath = filePath;
	}

	@Override
	public String toString() {
		return _name;
	}

	@Override
	public String createProjectString() {
		String projectString;
		if (_duration != null) {
			projectString = "AUDIOFILE*" + _name + "*" + _startTime + "*" + _duration + "*" + _volume + "\n";
		} else {
			projectString = "AUDIOFILE*" + _name + "*" + _startTime + "*null*" + _volume + "\n";
		}
		return projectString;
	}

	/**
	 * Sets all of the parameters for the object that may be altered after
	 * editing
	 * @param startTime
	 * @param duration
	 * @param volume
	 */
	public void setParameters(Long startTime, Double duration, Double volume) {
		_startTime = startTime;
		_duration = duration;
		_volume = volume;
		if (duration == null) {
			setDuration();
		} else {
			_duration = duration;
		}
		_audioPlayer.update(_filePath, (long) Math.round(duration * 1000), (int) Math.round(volume * 100));

	}

	@Override
	public String getType() {
		return "AUDIOFILE";
	}
	
	@Override
	public AudioPlayerAdapter getAudioPlayer() {
		return _audioPlayer;
	}

	@Override
	public String getName() {
		return _name;
	}

	@Override
	public String getFilePath() {
		return _filePath;
	}
	
	@Override
	public long getStartTime() {
		return _startTime;
	}
	
	/**
	 * Gets the set duration of the audio file
	 * @return
	 */
	public Double getDuration() {
		return _duration;
	}

	/**
	 * Gets the set volume of the audio file
	 * @return
	 */
	public double getVolume() {
		return _volume;
	}

	@Override
	public FfmpegWorker addToVideo(String videoPath, long time, ExportProgressMonitor monitor) {
		ffmpeg = new FfmpegWorker(videoPath, time, monitor, _filePath, _startTime, _duration, _volume);
		ffmpeg.execute();
		return ffmpeg;
	}

	@Override
	public boolean equals(Object other) {
		if (other != null) {
			Audio otherAudio = (Audio) other;
			return (otherAudio == this);
		}
		return false;
	}

	/**
	 * Sets the duration of the audio commentary to the length of the audio
	 */
	public void setDuration() {
		try { // have to use bash to get total audio length as
				// mediaPlayer.getLength() returned wrong length
			ProcessBuilder build = new ProcessBuilder("/bin/bash", "-c",
					"ffmpeg -i " + _filePath + " 2>&1 | grep Duration");
			build.redirectErrorStream(true);
			Process process = build.start();
			BufferedReader bf = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = bf.readLine()) != null) {
				if (line.contains("Duration")) {
					String durationString = line.substring(line.indexOf("Duration: ") + 10, line.indexOf(",") - 1);
					String[] times = durationString.split(":");
					long totalMillis = 0;
					totalMillis += TimeUnit.HOURS.toMillis(Long.parseLong(times[0]));
					totalMillis += TimeUnit.MINUTES.toMillis(Long.parseLong(times[1]));
					totalMillis += (long) Math.round((Double.parseDouble(times[2]) * 1000));
					_duration = ((double) totalMillis) / 1000;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
