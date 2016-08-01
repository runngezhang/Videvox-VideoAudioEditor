package vidivoxGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import festivalEnums.*;
import vidivoxAudio.Audio;
import vidivoxAudio.AudioComm;
import vidivoxAudio.AudioFile;
import vidivoxFileChoosers.AudioChooser;
import vidivoxManagers.CommentatorManager;

/**
 * Creates panel to add/edit VIDIVOX video audio
 * @author jay
 *
 */
public class AudioEditorPanel extends JPanel {
	VideoEditorFrame _videoEditor;
	ArrayList<Audio> _audioList;

	final protected CommentatorManager _commentatorManager = new CommentatorManager();
	final protected AudioChooser selectAudioChooser = new AudioChooser();

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

	// Initializing time selection panel
	final JPanel timePanel = new JPanel(new BorderLayout());
	final JPanel timeSetterPanel = new JPanel(new BorderLayout());
	final TimeSetterPanel startTimeSetter;
	final TimeSetterPanel durationTimeSetter;

	final JPanel timeSliderPanel = new JPanel(new BorderLayout());
	final TitledBorder sliderPanelBorder = BorderFactory.createTitledBorder("Added audio volume");

	final JSlider audioVolumeSlider = new JSlider(0, 200, 100);

	final JLabel lblAudioVolume = new JLabel("100");

	// Initializing file selection panel
	final JPanel filePanel = new JPanel(new BorderLayout());
	final JTextField txtFilePath = new JTextField();

	final JButton btnAudioChooser = new JButton("Select Mp3...");

	// Initializing audio input selection panel
	final JPanel audioSouthPanel = new JPanel(new BorderLayout());

	final JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	final JLabel lblInput = new JLabel("Audio Source:  ");
	final JCheckBox chkbxCommentary = new JCheckBox("Commentary");
	final JCheckBox chkbxFile = new JCheckBox("File");
	final JCheckBox chkbxDuration = new JCheckBox("Duration");

	final JPanel confirmationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
	final JButton btnAddAudio = new JButton("Add Audio");

	public AudioEditorPanel(VideoEditorFrame videoEditor) {
		_videoEditor = videoEditor;
		_audioList = _videoEditor._audioList;
		
		startTimeSetter = new TimeSetterPanel("Set Start Time", _videoEditor);
		durationTimeSetter = new TimeSetterPanel("Set Audio Duration", _videoEditor);

		this.setLayout(new BorderLayout());

		durationTimeSetter.setEnabled(false);

		timeSliderPanel.setBorder(sliderPanelBorder);

		lblAudioVolume.setPreferredSize(new Dimension(30, 0));

		txtFilePath.setEditable(false);
		txtFilePath.setFocusable(false);

		txtFilePath.setEnabled(false);
		btnAudioChooser.setEnabled(false);

		chkbxDuration.setEnabled(false);

		chkbxCommentary.setSelected(true);

		// Setting listeners for audio panel

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Setting up slider for new audio volume
		audioVolumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				lblAudioVolume.setText(String.valueOf(audioVolumeSlider.getValue()));
			}
		});

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Setting up add audio button listeners
		btnAddAudio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (_videoEditor._projectDir.equals("")) {
					JOptionPane.showMessageDialog(null, "Please select a project first", "No Project Selected",
							JOptionPane.ERROR_MESSAGE);
				} else {
					Long startTime = _videoEditor.getMillis(Integer.parseInt("0" + startTimeSetter.txtMin.getText()),
							Integer.parseInt("0" + startTimeSetter.txtSec.getText()),
							Integer.parseInt("0" + startTimeSetter.txtMSec.getText()));
					if (chkbxFile.isSelected()) {
						//gets relevant file fields and creates audio file object
						Long duration = null;
						Double durationSeconds = null;
						String filePath = txtFilePath.getText();
						if (!filePath.equals("")) {
							if (chkbxDuration.isSelected()) {
								duration = _videoEditor.getMillis(
										Integer.parseInt("0" + durationTimeSetter.txtMin.getText()),
										Integer.parseInt("0" + durationTimeSetter.txtSec.getText()),
										Integer.parseInt("0" + durationTimeSetter.txtMSec.getText()));
								durationSeconds = (((double) duration) / 1000);
							}
							try {
								Files.copy(Paths.get(filePath),
										Paths.get(_videoEditor._projectDir + "/"
												+ Paths.get(filePath).getFileName().toString()),
										StandardCopyOption.REPLACE_EXISTING);
								AudioFile audioFile = new AudioFile(
										_videoEditor._projectDir + "/" + Paths.get(filePath).getFileName().toString(),
										Paths.get(filePath).getFileName().toString());
								audioFile.setParameters(startTime, durationSeconds,
										((double) audioVolumeSlider.getValue()) / 100);
								_audioList.add(audioFile);
								Object[] audioTableInput = new Object[] { audioFile,
										_videoEditor.getMmss(audioFile.getStartTime()), _videoEditor.getMmss(
												(long) (audioFile.getDuration() * 1000) + audioFile.getStartTime()) };
								_videoEditor._audioListPanel.projectTableModel.addRow(audioTableInput);
							} catch (IOException e) {
								e.printStackTrace();
							}
						} else {
							JOptionPane.showMessageDialog(null, "Please select a file", "No File Selected",
									JOptionPane.ERROR_MESSAGE);
						}
					} else if (chkbxCommentary.isSelected()) {
						//gets relevant fields and creates audio commentary object
						String fileName = "";
						if (btnAddAudio.getText().equals("Add Audio")) {
							fileName = (String) JOptionPane.showInputDialog(null, "Please enter a unique file name:",
									"Commentary Name", JOptionPane.PLAIN_MESSAGE);
						} else if (btnAddAudio.getText().equals("Confirm Edits") && _videoEditor._audioListPanel._audio != null) {
							fileName = _videoEditor._audioListPanel._audio.getName();
						}
						if (fileName != null) {
							if (!fileName.endsWith(".mp3")) {
								fileName += ".mp3";
							}
							AudioComm audioComm = new AudioComm(_videoEditor._projectDir + "/" + fileName, fileName);
							audioComm.setParameters(startTime, ((double) audioVolumeSlider.getValue()) / 100,
									(FestivalVoice) _videoEditor._commentatorPanel.cmbxVoices.getSelectedItem(),
									(FestivalPitch) _videoEditor._commentatorPanel.cmbxPitches.getSelectedItem(),
									(FestivalSpeed) _videoEditor._commentatorPanel.cmbxSpeeds.getSelectedItem(),
									_videoEditor._commentatorPanel.commentatorTextEditor.getText());
							_audioList.add(audioComm);
							_commentatorManager.createMp3(_videoEditor._projectDir + "/" + fileName,
									_videoEditor._commentatorPanel.commentatorTextEditor.getText());
							Object[] audioTableInput = new Object[] { audioComm,
									_videoEditor.getMmss(audioComm.getStartTime()),
									_videoEditor.getMmss(audioComm.getDuration() + audioComm.getStartTime()) };
							_videoEditor._audioListPanel.projectTableModel.addRow(audioTableInput);
						}
					}
					try {
						BufferedWriter bw = new BufferedWriter(
								new FileWriter(_videoEditor._projectDir + _videoEditor.PROJECT_CONFIG, false));
						bw.write("VIDEO*" + _videoEditor._videoPlayer._videoManager.getMedia() + "\n");
						for (Audio audio : _audioList) {
							bw.write(audio.createProjectString());
						}
						bw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (btnAddAudio.getText().equals("Confirm Edits")) {
						btnAddAudio.setText("Add Audio");
						_videoEditor._audioListPanel.btnEditAudio.setText("Edit Audio");
					}
				}
			}
		});

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Setting up audio input check box and add button listeners
		//sets/enables relevant checkboxes
		chkbxCommentary.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (chkbxCommentary.isSelected()) {
					chkbxFile.setSelected(false);
					chkbxDuration.setEnabled(false);
					txtFilePath.setEnabled(false);
					btnAudioChooser.setEnabled(false);
					durationTimeSetter.setEnabled(false);
				} else {
					chkbxCommentary.setSelected(true);
				}
			}
		});

		chkbxFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (chkbxFile.isSelected()) {
					chkbxCommentary.setSelected(false);
					chkbxDuration.setEnabled(true);
					txtFilePath.setEnabled(true);
					btnAudioChooser.setEnabled(true);
					if (chkbxDuration.isSelected()) {
						durationTimeSetter.setEnabled(true);
					}
				} else {
					chkbxFile.setSelected(true);
				}
			}
		});

		chkbxDuration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chkbxDuration.isSelected()) {
					durationTimeSetter.btnTimeSetter.setEnabled(true);
					durationTimeSetter.btnSliderSetter.setEnabled(true);
					durationTimeSetter.txtMin.setEnabled(true);
					durationTimeSetter.txtSec.setEnabled(true);
					durationTimeSetter.txtMSec.setEnabled(true);
				} else {
					durationTimeSetter.btnTimeSetter.setEnabled(false);
					durationTimeSetter.btnSliderSetter.setEnabled(false);
					durationTimeSetter.txtMin.setEnabled(false);
					durationTimeSetter.txtSec.setEnabled(false);
					durationTimeSetter.txtMSec.setEnabled(false);
				}
			}
		});

		btnAudioChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				int status = selectAudioChooser.showOpenDialog(null);
				if (status == JFileChooser.APPROVE_OPTION) {
					txtFilePath.setText(selectAudioChooser.getSelectedFile().getAbsolutePath());
				}
			}
		});

		this.add(timePanel, BorderLayout.NORTH);
		timePanel.add(timeSetterPanel, BorderLayout.WEST);
		timeSetterPanel.add(startTimeSetter, BorderLayout.WEST);
		timeSetterPanel.add(durationTimeSetter, BorderLayout.EAST);

		timePanel.add(timeSliderPanel, BorderLayout.CENTER);
		timeSliderPanel.add(audioVolumeSlider, BorderLayout.CENTER);
		timeSliderPanel.add(lblAudioVolume, BorderLayout.EAST);

		this.add(filePanel, BorderLayout.CENTER);
		filePanel.add(txtFilePath, BorderLayout.CENTER);
		filePanel.add(btnAudioChooser, BorderLayout.EAST);

		this.add(audioSouthPanel, BorderLayout.SOUTH);
		audioSouthPanel.add(inputPanel, BorderLayout.WEST);
		inputPanel.add(lblInput);
		inputPanel.add(chkbxCommentary);
		inputPanel.add(chkbxFile);
		inputPanel.add(new JLabel("|"));
		inputPanel.add(chkbxDuration);

		audioSouthPanel.add(confirmationPanel, BorderLayout.EAST);
		confirmationPanel.add(btnAddAudio);

	}
}
