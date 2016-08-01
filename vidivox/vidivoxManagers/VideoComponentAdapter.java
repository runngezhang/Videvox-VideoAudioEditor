package vidivoxManagers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;

public class VideoComponentAdapter {
	private final EmbeddedMediaPlayerComponent _mediaPlayerComponent;
	private String _vlcVersion;
	private String _videoPath = "";
	SwingWorker<Void, Integer> _ffmpeg;
	private int _volume = 100;
	boolean isMuted = false;

	/**
	 * Adapter class for the Vlc component. All non GUI video functions are
	 * handled by this class. The component is passed to the GUI thread by a
	 * getter.
	 *
	 * @throws FileNotFoundException
	 *             When a vlc installation is not found.
	 */
	public VideoComponentAdapter() throws FileNotFoundException {
		boolean vlcIsInstalled = new NativeDiscovery().discover();
		if (vlcIsInstalled) {
			_mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
			_vlcVersion = LibVlc.INSTANCE.libvlc_get_version();
		} else {
			_mediaPlayerComponent = null;
			throw new FileNotFoundException("VLC IS NOT INSTALLED");
		}
	}

	/**
	 * Given to the GUI thread to use the components.
	 *
	 * @return the _mediaPlayer
	 */
	public EmbeddedMediaPlayerComponent getComponent() {
		return _mediaPlayerComponent;
	}

	/**
	 * Sets the path of the video file to be loaded into the media player
	 * 
	 * @param path
	 */
	public void setMedia(String path) {
		_videoPath = path;
		_mediaPlayerComponent.getMediaPlayer().startMedia("file:///" + _videoPath);
		_mediaPlayerComponent.getMediaPlayer().pause();
	}
	
	/**
	 * Stops the mediaplayer from playing the selected video
	 */
	public void close() {
		_mediaPlayerComponent.getMediaPlayer().stop();
	}
	
	/**
	 * gets the set video
	 * @return
	 */
	public String getMedia() {
		return _videoPath;
	}

	/**
	 * Sets volume of player to zero
	 */
	public void mute() {
		isMuted = true;
		_mediaPlayerComponent.getMediaPlayer().setVolume(0);
	}

	/**
	 * Sets volume of player to _volume
	 */
	public void unMute() {
		isMuted = false;
		_mediaPlayerComponent.getMediaPlayer().setVolume(_volume);
	}

	/**
	 * Returns whether the player is muted
	 * 
	 * @return boolean
	 */
	public boolean isMuted() {
		return isMuted;
	}

	/**
	 * Sets the volume _volume of the player to the parameter
	 * 
	 * @param volume
	 */
	public void setVolume(int volume) {
		_volume = volume;
		_mediaPlayerComponent.getMediaPlayer().setVolume(_volume);
	}

	/**
	 * Gets the volume _volume of the player
	 * 
	 * @return
	 */
	public int getVolume() {
		return _volume;
	}

	/**
	 * Gets the time in milliseconds of the current position in the video
	 * 
	 * @return long time
	 */
	public long getTime() {
		return _mediaPlayerComponent.getMediaPlayer().getTime();
	}

	public void setTime(long millis) {
		_mediaPlayerComponent.getMediaPlayer().setTime(millis);
	}

	/**
	 * Gets the total time in milliseconds of the current position in the video
	 * 
	 * @return long totalTime
	 */
	public long getLength() {
		return _mediaPlayerComponent.getMediaPlayer().getLength();
	}

	/**
	 * Sets the media file to play at regular speed
	 */
	public void play() {
		if (!_mediaPlayerComponent.getMediaPlayer().isPlaying()) {
			_mediaPlayerComponent.getMediaPlayer().play();
		}
	}
	
	public boolean isPlaying() {
		return _mediaPlayerComponent.getMediaPlayer().isPlaying();
	}

	/**
	 * Pauses the media file
	 */
	public void pause() {
		if (_mediaPlayerComponent.getMediaPlayer().isPlaying()) {
			_mediaPlayerComponent.getMediaPlayer().pause();
		}
	}

	/**
	 * Gets the current position (in the form of a percentage form 0 - 100) in
	 * the media file
	 * 
	 * @return PercentageDone
	 */
	public int getPosition() {
		return (int) (_mediaPlayerComponent.getMediaPlayer().getPosition() * 100);
	}

	/**
	 * Sets the current position (in the form of a percentage form 0 - 100) in
	 * the media file
	 * 
	 * @param position
	 */
	public void setPosition(int position) {
		_mediaPlayerComponent.getMediaPlayer().setPosition(((float) position) / 100);
	}
}
