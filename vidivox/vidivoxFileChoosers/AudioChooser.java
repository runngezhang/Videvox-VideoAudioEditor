package vidivoxFileChoosers;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Custom JFileChooser which filters for mp3 files
 * @author jay
 *
 */
public class AudioChooser extends JFileChooser {
	
	final protected String AUDIO_TYPE = ".mp3";
	
	public AudioChooser() { 
		this.setCurrentDirectory(new File(System.getProperty("user.home")));
		this.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				if (f.getName().endsWith(AUDIO_TYPE)) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return AUDIO_TYPE;
			}
		});
	}
}
