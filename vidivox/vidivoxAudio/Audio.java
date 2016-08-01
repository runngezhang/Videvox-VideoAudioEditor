package vidivoxAudio;

import vidivoxManagers.AudioPlayerAdapter;
import vidivoxManagers.ExportProgressMonitor;
import vidivoxWorkers.FfmpegWorker;

/**
 * Interface to be implemented by Audio file and commentary objects
 * 
 * @author jay
 *
 */
public interface Audio {
	/**
	 * Returns the filepath of the audio object
	 * 
	 * @return String filePath
	 */
	public String getFilePath();

	/**
	 * Returns the name of the audio object
	 * 
	 * @return String name
	 */
	public String getName();

	/**
	 * Instructs the audio object to create an ffmpegWorker and add itself to a
	 * given video filepath
	 * 
	 * @param videoPath
	 * @param time
	 * @param monitor
	 * @return
	 */
	public FfmpegWorker addToVideo(String videoPath, long time, ExportProgressMonitor monitor);

	/**
	 * Gets the type of the audio object
	 * 
	 * @return
	 */
	public String getType();

	/**
	 * Gets the start time of the audio object in millis
	 * 
	 * @return long startTime
	 */
	public long getStartTime();

	/**
	 * Returns a string representing the audio object to be written to the
	 * project.vdvx file
	 * 
	 * @return String projectString
	 */
	public String createProjectString();
	
	/**
	 * Gets the AudioPlayerAdapter associated with the audio object
	 * @return
	 */
	public AudioPlayerAdapter getAudioPlayer();
}
