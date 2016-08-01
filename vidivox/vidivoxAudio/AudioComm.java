package vidivoxAudio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import festivalEnums.*;
import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;
import vidivoxManagers.AudioPlayerAdapter;
import vidivoxManagers.ExportProgressMonitor;
import vidivoxWorkers.FfmpegWorker;

/**
 * Class to contain information pertaining to audio added from the commentator
 * 
 * @author jay
 *
 */
public class AudioComm implements Audio {
	private String _name = null;
	private String _filePath = null;
	private String _commText = "";
	private long _startTime = 0;
	private double _volume = 1.0;
	private long _duration = 0;
	private FestivalVoice _voice = FestivalVoice.KAL;
	private FestivalPitch _pitch = FestivalPitch.NORMAL;
	private FestivalSpeed _speed = FestivalSpeed.NORMAL;
	private FfmpegWorker ffmpeg = null;
	private AudioPlayerAdapter _audioPlayer = new AudioPlayerAdapter();

	public AudioComm(String filePath, String name) {
		_name = name;
		_filePath = filePath;
	}

	@Override
	public String toString() {
		return _name;
	}

	/**
	 * Returns a representation of the object in the form of a String to be
	 * printed to the project.vdvx file
	 * 
	 * @Returns String projectString
	 */
	public String createProjectString() {
		String projectString = "AUDIOCOMM*" + _name + "*" + _startTime + "*" + _volume + "*" + _voice.toString() + "*"
				+ _pitch.toString() + "*" + _speed.toString() + "*" + _commText + "\n";
		return projectString;
	}

	/**
	 * Sets all of the parameters for the object that may be altered after
	 * editing
	 * 
	 * @param startTime
	 * @param volume
	 * @param voice
	 * @param pitch
	 * @param speed
	 * @param commText
	 */
	public void setParameters(long startTime, double volume, FestivalVoice voice, FestivalPitch pitch,
			FestivalSpeed speed, String commText) {
		_startTime = startTime;
		_volume = volume;
		if (voice != null) {
			_voice = voice;
		}
		if (pitch != null) {
			_pitch = pitch;
		}
		if (speed != null) {
			_speed = speed;
		}
		_commText = commText;
		setDuration();
		_audioPlayer.update(_filePath, _duration, (int) Math.round(volume * 100));
	}

	@Override
	public String getType() {
		return "AUDIOCOMM";
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

	/**
	 * Gets the text synthesized by the audio object
	 * 
	 * @return String text
	 */
	public String getCommText() {
		return _commText;
	}

	@Override
	public long getStartTime() {
		return _startTime;
	}

	/**
	 * Returns the length of the audio in millis
	 * 
	 * @return long duration
	 */
	public long getDuration() {
		return _duration;
	}

	/**
	 * Returns the set volume of the object from 0.0 to 2.0
	 * 
	 * @return double volume
	 */
	public double getVolume() {
		return _volume;
	}

	/**
	 * Returns the set voice of the audio commentary
	 * 
	 * @return
	 */
	public FestivalVoice getVoice() {
		return _voice;
	}

	/**
	 * Returns the set pitch of the audio commentary
	 * 
	 * @return
	 */
	public FestivalPitch getPitch() {
		return _pitch;
	}

	/**
	 * Returns the set speed of the audio commentary
	 * 
	 * @return
	 */
	public FestivalSpeed getSpeed() {
		return _speed;
	}

	@Override
	public FfmpegWorker addToVideo(String videoPath, long time, ExportProgressMonitor monitor) {
		ffmpeg = new FfmpegWorker(videoPath, time, monitor, _filePath, _startTime, null, _volume);
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
		try {
			/*
			 * have to use bash to get total audio length as
			 * mediaPlayer.getLength() returned wrong length
			 */
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
					_duration = totalMillis;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
