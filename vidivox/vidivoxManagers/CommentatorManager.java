package vidivoxManagers;

import festivalEnums.*;
import vidivoxWorkers.FestivalWorker;
import vidivoxWorkers.Text2WaveWorker;

/**
 * Manages functionality for Commentator frame; all non-GUI functions are to be
 * handled through this CommentatorManager.
 */
public class CommentatorManager {
	private FestivalVoice _voice = FestivalVoice.KAL;
	private FestivalPitch _pitch = FestivalPitch.NORMAL;
	private FestivalSpeed _speed = FestivalSpeed.NORMAL;

	private FestivalWorker festival = null;

	/**
	 * Sets voice to selected voice input
	 * 
	 * @param voice
	 */
	public void setVoice(FestivalVoice voice) {
		_voice = voice;
	}

	/**
	 * Sets pitch to selected pitch input
	 * 
	 * @param pitch
	 */
	public void setPitch(FestivalPitch pitch) {
		_pitch = pitch;
	}
	
	/**
	 * Sets speed to selected speed input
	 * 
	 * @param speed
	 */
	public void setSpeed(FestivalSpeed speed) {
		_speed = speed;
	}

	/**
	 * Creates mp3 file of synthesized festival voice
	 * 
	 * @param fileName
	 * @param text
	 */
	public void createMp3(String fileName, String text) {
		Text2WaveWorker text2Wave = new Text2WaveWorker(fileName, text, _voice, _pitch, _speed);
		text2Wave.execute();
	}

	/**
	 * Creates and executes a SwingWorker which uses festival to sythensize
	 * text. (WEIRD ERROR: STRING INDEX OUT OF BOUND IF RUN TEST IMMEDIATELY
	 * AFTER LAUNCH MULTIPLE TIMES)
	 * 
	 * @param text
	 */
	public FestivalWorker execute(String text) {
		if (festival != null) {
			if (!festival.isDone()) {
				festival.cancel(true);
			}
		}
		festival = new FestivalWorker(text, _voice, _pitch, _speed);
		festival.execute();
		return festival;
	}
}
