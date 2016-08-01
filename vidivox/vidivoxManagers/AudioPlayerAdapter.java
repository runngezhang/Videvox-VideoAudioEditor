package vidivoxManagers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import uk.co.caprica.vlcj.component.AudioMediaPlayerComponent;

/**
 * An adapter to handle the functions of playing audio in the preview
 * 
 * @author jay
 *
 */
public class AudioPlayerAdapter {
	final private AudioMediaPlayerComponent _audioPlayerComponent = new AudioMediaPlayerComponent();
	private Long _duration = null;
	private int _volume = 100;

	private Timer _timer = new Timer(100, null);

	public AudioPlayerAdapter() {
		setTimer();
	}

	public AudioPlayerAdapter(String filePath, Long duration, int volume) {
		update(filePath, duration, volume);
		setTimer();
	}

	/**
	 * Sets up timer action listener
	 */
	public void setTimer() {
		_timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_audioPlayerComponent.getMediaPlayer().getTime() > _duration) {
					pause();
				}
			}
		});
	}

	/**
	 * sets the audio time to input time, checks if audio should play and set time
	 * @param time
	 * @param override
	 */
	public void setTime(long time, boolean override) {
		if (time > 0 && time < _duration && (!_audioPlayerComponent.getMediaPlayer().isPlaying() || override)) {
			_audioPlayerComponent.getMediaPlayer().setTime(time);
			play();
			_timer.start();
		} else if (time < 0 || time > _duration) {
			pause();
		}
	}
	
	/**
	 * Pauses audio, stops timer
	 */
	public void pause() {
		if (_audioPlayerComponent.getMediaPlayer().isPlaying()) {
			_audioPlayerComponent.getMediaPlayer().pause();
			_timer.stop();
		}
	}
	
	/**
	 * Plays audio, starts timer
	 */
	public void play() {
		if (!_audioPlayerComponent.getMediaPlayer().isPlaying()) {
			_audioPlayerComponent.getMediaPlayer().play();
			_timer.start();
		}
	}

	/**
	 * Sets volume of player to zero
	 */
	public void mute() {
		_audioPlayerComponent.getMediaPlayer().setVolume(0);
	}

	/**
	 * Sets volume of player to _volume
	 */
	public void unMute() {
		_audioPlayerComponent.getMediaPlayer().setVolume(_volume);
	}
	
	/**
	 * updates parameters of audio player
	 * @param filePath
	 * @param duration
	 * @param volume
	 */
	public void update(String filePath, Long duration, int volume) {
		_audioPlayerComponent.getMediaPlayer().startMedia("file:///" + filePath);
		pause();
		_volume = volume;
		_audioPlayerComponent.getMediaPlayer().setVolume(_volume);
		_duration = duration;
	}
}
