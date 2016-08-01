package vidivoxFileChoosers;

import java.io.File;

import javax.swing.JFileChooser;

/**
 * Custom JFileChooser which filters for folders
 * @author jay
 *
 */
public class ProjectChooser extends JFileChooser {
	
	public ProjectChooser() {
		this.setCurrentDirectory(new File(System.getProperty("user.home")));
		this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	}
}
