package vidivoxWorkers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

import javax.swing.SwingWorker;

import festivalEnums.*;

/**
 * Creates a process to use festival to synthesize and play input speech with
 * input settings, stopping the audio if cancelled
 * 
 * @author jay
 *
 */
public class FestivalWorker extends SwingWorker<Void, Void> {
	Process process;
	FestivalVoice _voice = FestivalVoice.KAL;
	FestivalPitch _pitch = FestivalPitch.NORMAL;
	FestivalSpeed _speed = FestivalSpeed.NORMAL;
	String _text = "";

	public FestivalWorker(String text, FestivalVoice voice, FestivalPitch pitch, FestivalSpeed speed) {
		_text = "(SayText \\\"" + text + "\\\")";
		_voice = voice;
		_pitch = pitch;
		_speed = speed;
	}

	@Override
	protected Void doInBackground() throws Exception {
		 //Says text set in Manager with festival speech synthesizer.
		String cmd = "echo \"" + _voice.getValue() + " " + _pitch.getFestValue() + " " + _speed.getValue() + " " + _text
				+ "\" | festival";
		ProcessBuilder build = new ProcessBuilder("/bin/bash", "-c", cmd);
		process = build.start();
		process.waitFor();
		return null;
	}

	@Override
	protected void done() {
		 //Kills festival speech if cancelled, otherwise does nothing
		if (isCancelled()) {
			Field f;
			try {
				f = process.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				int pid = f.getInt(process);
				String cmd = "pstree -lp | grep " + pid;
				ProcessBuilder build = new ProcessBuilder("/bin/bash", "-c", cmd);
				Process process = build.start();
				InputStream stdout = process.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
				String line = br.readLine();
				String aplay = line.substring(line.indexOf("play("));
				String playPid = aplay.substring(5, aplay.indexOf(")"));
				cmd = "kill -9 " + playPid;
				ProcessBuilder bkill = new ProcessBuilder("/bin/bash", "-c", cmd);
				@SuppressWarnings("unused")
				Process pkill = bkill.start();
			} catch (NoSuchFieldException | SecurityException | IOException | IllegalArgumentException
					| IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
}
