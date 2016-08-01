package vidivoxGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ToolTipManager;
import vidivoxAudio.Audio;
import vidivoxFileChoosers.ProjectChooser;
import vidivoxFileChoosers.VideoChooser;
import vidivoxManagers.ExportManager;

/**
 * Creates JMenuBar component for VIDIVOX GUI
 * @author jay
 *
 */
public class MenuBar extends JMenuBar {
	private VideoEditorFrame _videoEditor;
	private ProjectManager _projectManager;
	
	final private VideoChooser selectVideoChooser = new VideoChooser();
	final private ProjectChooser selectProjectChooser = new ProjectChooser();
	
	final protected JMenuBar menuBar = new JMenuBar();
	final protected JMenu mnFile = new JMenu("File");
	final protected JMenuItem mntmNew = new JMenuItem("New Project...");
	final protected JMenuItem mntmOpen = new JMenuItem("Open Project...");
	final protected JMenuItem mntmClose = new JMenuItem("Close Project");
	final protected JMenuItem mntmBar = new JMenuItem("~~~~~~~~~~~~");
	final protected JMenuItem mntmExportVideo = new JMenuItem("Export Video...");
	
	public MenuBar(VideoEditorFrame videoEditor) {
		_videoEditor = videoEditor;
		_projectManager = new ProjectManager(_videoEditor);
		
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		
		mntmBar.setEnabled(false);
		mntmBar.setFocusable(false);
		mntmExportVideo.setEnabled(false);
		
		//adds listener to create new projects
		mntmNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int status = selectProjectChooser.showDialog(null, "Set Project");
				if (status == JFileChooser.APPROVE_OPTION) {
					String project = selectProjectChooser.getSelectedFile().toString();
					_projectManager.createProject(project);
				}
			}
		});
		
		//adds listener to open projects
		mntmOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_projectManager.openProject();
			}
		});
		
		//adds listener to close projects
		mntmClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_projectManager.closeProject();
			}
		});
		
		//adds listener to export project as video
		mntmExportVideo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//prompts for save location
				int status = selectVideoChooser.showSaveDialog(null);
				if (status == JFileChooser.APPROVE_OPTION) {
					String exportVideo = selectVideoChooser.getSelectedFile().toString();
					//ensures appropriate extension is included
					if (!exportVideo.endsWith(".avi")) {
						exportVideo += ".avi";
					}
					try {
						//initializes export video
						Files.copy(Paths.get(_videoEditor._videoPlayer._videoManager.getMedia()), Paths.get(exportVideo),
								StandardCopyOption.REPLACE_EXISTING);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					//copies current audio list (so alterations don't interfere with export)
					ArrayList<Audio> exportAudio = new ArrayList<Audio>(_videoEditor._audioList);
					//uses export manager to begin exporting in background
					_videoEditor._exportManager = new ExportManager(exportAudio, exportVideo, _videoEditor._videoPlayer._videoManager.getLength(), _videoEditor._monitor);
					_videoEditor._exportManager.execute();
				}
			}
		});

		this.add(mnFile);
		mnFile.add(mntmNew);
		mnFile.add(mntmOpen);
		mnFile.add(mntmClose);
		mnFile.add(mntmBar);
		mnFile.add(mntmExportVideo);
	}
}
