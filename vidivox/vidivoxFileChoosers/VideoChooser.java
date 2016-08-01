package vidivoxFileChoosers;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * Custom JFileChooser which filters for avi files
 * @author jay
 *
 */
public class VideoChooser extends JFileChooser {
	
	final protected String VIDEO_TYPE = ".avi";
	
	public VideoChooser() {
		this.setCurrentDirectory(new File(System.getProperty("user.home")));
		this.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				if (f.getName().endsWith(VIDEO_TYPE)) {
					return true;
				} else {
					return false;
				}
			}

			@Override
			public String getDescription() {
				return VIDEO_TYPE;
			}
		});
	}
}
