package vidivoxWorkers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import javax.swing.SwingWorker;

import festivalEnums.*;

/**
 * Spawns a text2wave and ffmpeg process which creates an mp3 file from input
 * text and festival settings
 * 
 * @author jay
 *
 */
public class Text2WaveWorker extends SwingWorker<Void, Void> {
	private String _tempFile = System.getProperty("user.home") + "/.vidivox_festival_temp.wav";

	String _fileName = "";
	String _text = "";

	FestivalVoice _voice = FestivalVoice.KAL;
	FestivalPitch _pitch = FestivalPitch.NORMAL;
	FestivalSpeed _speed = FestivalSpeed.NORMAL;

	public Text2WaveWorker(String fileName, String text, FestivalVoice voice, FestivalPitch pitch,
			FestivalSpeed speed) {
		if (!fileName.endsWith(".mp3")) {
			fileName += ".mp3";
		}
		_fileName = fileName;
		_text = text;
		_voice = voice;
		_pitch = pitch;
		_speed = speed;
		_tempFile = System.getProperty("user.home") + "/." + UUID.randomUUID().toString() + ".wav";
	}

	@Override
	protected Void doInBackground() throws Exception {
		try {
			String cmd = "echo \"" + _text + "\" | text2wave -o \'" + _tempFile + "\' -eval \"" + _voice.getValue()
					+ "\" -eval \"" + _pitch.get2WaveValue() + "\" -eval \"" + _speed.getValue() + "\"";
			System.out.println(cmd);
			ProcessBuilder build = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = build.start();
			process.waitFor();
			cmd = "ffmpeg -y -i \'" + _tempFile + "\' -acodec libmp3lame " + "\'" + _fileName + "\'";
			build = new ProcessBuilder("/bin/bash", "-c", cmd);
			build.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.INHERIT);
			process = build.start();
			process.waitFor();
			Files.delete(Paths.get(_tempFile));
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void done() {
		try {
			Files.delete(Paths.get(_tempFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
