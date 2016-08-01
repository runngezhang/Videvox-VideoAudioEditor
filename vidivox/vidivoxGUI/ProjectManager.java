package vidivoxGUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import festivalEnums.FestivalPitch;
import festivalEnums.FestivalSpeed;
import festivalEnums.FestivalVoice;
import vidivoxAudio.Audio;
import vidivoxAudio.AudioComm;
import vidivoxAudio.AudioFile;
import vidivoxFileChoosers.ProjectChooser;
import vidivoxFileChoosers.VideoChooser;

/**
 * A manager to handle opening, creating, and closing projects
 * 
 * @author jay
 *
 */
public class ProjectManager {
	VideoEditorFrame _videoEditor;

	String PROJECT_CONFIG;
	String _projectDir;

	ArrayList<Audio> _audioList;
	ProjectChooser selectProjectChooser = new ProjectChooser();
	VideoChooser selectVideoChooser = new VideoChooser();

	public ProjectManager(VideoEditorFrame videoEditor) {
		_videoEditor = videoEditor;
		PROJECT_CONFIG = videoEditor.PROJECT_CONFIG;
		_projectDir = _videoEditor._projectDir;
		_audioList = _videoEditor._audioList;
	}

	/**
	 * Creates a new project and dependent files
	 * 
	 * @param project
	 * @return
	 */
	protected boolean createProject(String project) {
		boolean valid = true;
		int status;
		if (new File(project + PROJECT_CONFIG).exists()) {
			status = JOptionPane.showConfirmDialog(_videoEditor,
					"This folder is already a project.\n  Do you wish to overwrite the project contents?",
					"Overwrite Project", JOptionPane.YES_NO_OPTION);
			if (status != JOptionPane.YES_OPTION) {
				valid = false;
			}
		}
		if (valid) {
			JOptionPane.showMessageDialog(_videoEditor, "Please select a video to edit");
			status = selectVideoChooser.showDialog(_videoEditor, "Set Video");
			if (status == JFileChooser.APPROVE_OPTION) {
				setProject(project);
				_videoEditor._menuBar.mntmExportVideo.setEnabled(true);
				String videoPath = selectVideoChooser.getSelectedFile().toString();
				String videoName = Paths.get(videoPath).getFileName().toString();
				try {
					Files.copy(Paths.get(videoPath), Paths.get(_projectDir + "/" + videoName),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					PrintWriter writer = new PrintWriter(_projectDir + PROJECT_CONFIG, "UTF-8");
					writer.println("VIDEO*" + _projectDir + "/" + videoName);
					writer.close();
				} catch (FileNotFoundException | UnsupportedEncodingException e2) {
					e2.printStackTrace();
				}
				_videoEditor._videoPlayer.setMedia(_projectDir + "/" + videoName);
				return true;
			}
		}
		return false;
	}

	/**
	 * Opens a project and gets relevant objects, sets up list. If selected
	 * folder is not a project, creates a project on user prompt
	 */
	public void openProject() {
		int status = selectProjectChooser.showOpenDialog(null);
		if (status == JFileChooser.APPROVE_OPTION) {
			if (!_projectDir.equals("")) {
				setProject("");
				_audioList = new ArrayList<Audio>();
				DefaultTableModel model = (DefaultTableModel) _videoEditor._audioListPanel.projectTable.getModel();
				int rows = model.getRowCount();
				for (int i = rows - 1; i >= 0; i--) {
					model.removeRow(i);
				}
			}
			String project = selectProjectChooser.getSelectedFile().toString();
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(project + PROJECT_CONFIG)));
				setProject(project);
				_videoEditor._menuBar.mntmExportVideo.setEnabled(true);
				String line = null;
				while ((line = br.readLine()) != null) {
					String[] objectParameters = line.split("\\*");
					if (objectParameters[0].equals("AUDIOCOMM")) {
						AudioComm audioComm = new AudioComm(_projectDir + "/" + objectParameters[1],
								objectParameters[1]);
						audioComm.setParameters(Long.parseLong(objectParameters[2]),
								Double.parseDouble(objectParameters[3]), FestivalVoice.asString(objectParameters[4]),
								FestivalPitch.asString(objectParameters[5]),
								FestivalSpeed.asString(objectParameters[6]), objectParameters[7]);
						_audioList.add(audioComm);
						Object[] audioTableInput = new Object[] { audioComm,
								_videoEditor.getMmss(audioComm.getStartTime()),
								_videoEditor.getMmss(audioComm.getDuration() + audioComm.getStartTime()) };
						_videoEditor._audioListPanel.projectTableModel.addRow(audioTableInput);
					} else if (objectParameters[0].equals("AUDIOFILE")) {
						AudioFile audioFile = new AudioFile(_videoEditor._projectDir + "/" + objectParameters[1],
								objectParameters[1]);
						if (objectParameters[3].equals("null")) {
							audioFile.setParameters(Long.parseLong(objectParameters[2]), null,
									Double.parseDouble(objectParameters[4]));
						} else {
							audioFile.setParameters(Long.parseLong(objectParameters[2]),
									Double.parseDouble(objectParameters[3]), Double.parseDouble(objectParameters[4]));
						}
						_audioList.add(audioFile);
						Object[] audioTableInput = null;
						if (!objectParameters[3].equals("null")) {
							audioTableInput = new Object[] { _audioList.get(_audioList.size() - 1),
									_videoEditor.getMmss(Long.parseLong(objectParameters[2])),
									_videoEditor
											.getMmss((Long) Math.round(Double.parseDouble(objectParameters[3]) * 1000)
													+ Long.parseLong(objectParameters[2])) };
						} else {
							audioTableInput = new Object[] {
									_videoEditor._audioList.get(_videoEditor._audioList.size() - 1),
									_videoEditor.getMmss(Long.parseLong(objectParameters[2])), "-" };
						}
						_videoEditor._audioListPanel.projectTableModel.addRow(audioTableInput);
					} else if (objectParameters[0].equals("VIDEO")) {
						_videoEditor._videoPlayer.setMedia(objectParameters[1]);
					}
				}
			} catch (IOException e1) {
				status = JOptionPane.showConfirmDialog(null,
						"This folder is not a project.\n  Would you like to create a new project?",
						"Create New Project", JOptionPane.YES_NO_OPTION);
				if (status == JOptionPane.YES_OPTION) {
					_projectDir = project;
					createProject(_projectDir);
				}
			}
		}
	}

	/**
	 * Closes a project: removes audio from list and table, closes video
	 */
	public void closeProject() {
		_videoEditor._menuBar.mntmExportVideo.setEnabled(false);
		_videoEditor._videoPlayer._videoManager.close();
		_videoEditor._videoPlayer.btnPlayPause.setIcon(_videoEditor._videoPlayer.play);
		_videoEditor._videoPlayer.btnPlayPause.setRolloverIcon(_videoEditor._videoPlayer.playHover);
		setProject("");
		_videoEditor._audioList = new ArrayList<Audio>();
		DefaultTableModel model = (DefaultTableModel) _videoEditor._audioListPanel.projectTable.getModel();
		int rows = model.getRowCount();
		for (int i = rows - 1; i >= 0; i--) {
			model.removeRow(i);
		}
	}

	private void setProject(String project) {
		_projectDir = project;
		_videoEditor._projectDir = project;
	}
}
