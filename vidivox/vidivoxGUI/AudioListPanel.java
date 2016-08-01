package vidivoxGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import festivalEnums.*;
import vidivoxAudio.Audio;
import vidivoxAudio.AudioComm;
import vidivoxAudio.AudioFile;

/**
 * Creates panel with list of VIDIVOX project audio
 * @author jay
 *
 */
public class AudioListPanel extends JPanel {
	// Initializing directory viewer panel
	private VideoEditorFrame _videoEditor;
	private ProjectManager _projectManager;
	
	protected Audio _audio = null;

	final JButton btnSetProject = new JButton("Select Project");

	final DefaultTableModel projectTableModel = new DefaultTableModel() {
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};

	final JTable projectTable = new JTable(projectTableModel);

	final JScrollPane listScroll = new JScrollPane(projectTable);
	final JPanel listButtonPanel = new JPanel(new BorderLayout());

	final JButton btnEditAudio = new JButton("Edit Audio");
	final JButton btnRemoveAudio = new JButton("Remove Audio");

	AudioListPanel(VideoEditorFrame videoEditor) {
		_videoEditor = videoEditor;
		_projectManager = new ProjectManager(_videoEditor);

		this.setLayout(new BorderLayout());
		final Border listBorder = BorderFactory.createEtchedBorder();
		this.setBorder(listBorder);
		this.setBackground(Color.decode("#3D3D3D"));

		projectTableModel.addColumn("File Name");
		projectTableModel.addColumn("Start Time");
		projectTableModel.addColumn("End Time");

		DefaultTableCellRenderer centeredRenderer = new DefaultTableCellRenderer();
		centeredRenderer.setHorizontalAlignment(JLabel.CENTER);
		projectTable.getColumnModel().getColumn(1).setCellRenderer(centeredRenderer);
		projectTable.getColumnModel().getColumn(2).setCellRenderer(centeredRenderer);

		listButtonPanel.setBackground(Color.decode("#3D3D3D"));

		btnEditAudio.setEnabled(false);
		btnRemoveAudio.setEnabled(false);

		// Setting up listeners for list panel buttons
		btnSetProject.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_projectManager.openProject();
			}
		});

		projectTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (projectTable.getSelectedRow() != -1) {
					btnEditAudio.setEnabled(true);
					btnRemoveAudio.setEnabled(true);
				} else if (btnEditAudio.getText().equals("Edit Audio")) {
					btnEditAudio.setEnabled(false);
					btnRemoveAudio.setEnabled(false);
				} else {
					btnRemoveAudio.setEnabled(false);
				}
			}
		});

		btnEditAudio.addActionListener(new ActionListener() {
			//gets info from object and sets relevant components
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnEditAudio.getText().equals("Edit Audio")) {
					btnEditAudio.setText("Cancel Edit");
					_videoEditor._audioEditorPanel.btnAddAudio.setText("Confirm Edits");
					_audio = (Audio) projectTable.getValueAt(projectTable.getSelectedRow(), 0);
					String audioType = _audio.getType();
					if (audioType.equals("AUDIOFILE")) {
						AudioFile audioFile = (AudioFile) _audio;
						_videoEditor._audioEditorPanel.chkbxFile.setSelected(true);
						_videoEditor._audioEditorPanel.chkbxCommentary.setSelected(false);
						_videoEditor._audioEditorPanel.chkbxDuration.setEnabled(true);
						_videoEditor._audioEditorPanel.txtFilePath.setEnabled(true);
						_videoEditor._audioEditorPanel.btnAudioChooser.setEnabled(true);
						_videoEditor._audioEditorPanel.txtFilePath.setText(_audio.getFilePath());
						_videoEditor._audioEditorPanel.startTimeSetter.txtMin
								.setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(audioFile.getStartTime())));
						_videoEditor._audioEditorPanel.startTimeSetter.txtSec
								.setText(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(audioFile.getStartTime())
										% TimeUnit.MINUTES.toSeconds(1)));
						_videoEditor._audioEditorPanel.startTimeSetter.txtMSec
								.setText(String.valueOf(audioFile.getStartTime() % TimeUnit.SECONDS.toMillis(1)));
						if (audioFile.getDuration() != null) {
							_videoEditor._audioEditorPanel.chkbxDuration.setSelected(true);
							_videoEditor._audioEditorPanel.durationTimeSetter.setEnabled(true);
							_videoEditor._audioEditorPanel.durationTimeSetter.txtMin.setText(String.valueOf(TimeUnit.MILLISECONDS
									.toMinutes((long) Math.round(audioFile.getDuration() * 1000))));
							_videoEditor._audioEditorPanel.durationTimeSetter.txtSec.setText(String.valueOf(TimeUnit.MILLISECONDS
									.toSeconds((long) Math.round(audioFile.getDuration() * 1000))));
							_videoEditor._audioEditorPanel.durationTimeSetter.txtMSec.setText(String.valueOf(
									(long) Math.round(audioFile.getDuration() * 1000) % TimeUnit.SECONDS.toMillis(1)));
						} else {
							_videoEditor._audioEditorPanel.chkbxDuration.setSelected(false);
							_videoEditor._audioEditorPanel.durationTimeSetter.setEnabled(false);
							_videoEditor._audioEditorPanel.audioVolumeSlider.setValue((int) (audioFile.getVolume() * 100));
						}
					} else if (audioType.equals("AUDIOCOMM")) {
						_videoEditor._audioEditorPanel.chkbxFile.setSelected(false);
						_videoEditor._audioEditorPanel.chkbxCommentary.setSelected(true);
						_videoEditor._audioEditorPanel.chkbxDuration.setEnabled(false);
						_videoEditor._audioEditorPanel.txtFilePath.setEnabled(false);
						_videoEditor._audioEditorPanel.btnAudioChooser.setEnabled(false);
						_videoEditor._audioEditorPanel.durationTimeSetter.setEnabled(false);

						AudioComm audioComm = (AudioComm) _audio;
						_videoEditor._audioEditorPanel.startTimeSetter.txtMin
								.setText(String.valueOf(TimeUnit.MILLISECONDS.toMinutes(audioComm.getStartTime())));
						_videoEditor._audioEditorPanel.startTimeSetter.txtSec
								.setText(String.valueOf(TimeUnit.MILLISECONDS.toSeconds(audioComm.getStartTime())
										% TimeUnit.MINUTES.toSeconds(1)));
						_videoEditor._audioEditorPanel.startTimeSetter.txtMSec
								.setText(String.valueOf(audioComm.getStartTime() % TimeUnit.SECONDS.toMillis(1)));
						_videoEditor._audioEditorPanel.audioVolumeSlider.setValue((int) (audioComm.getVolume() * 100));
						_videoEditor._commentatorPanel.cmbxVoices.setSelectedItem(audioComm.getVoice());
						if (audioComm.getPitch().equals(FestivalPitch.HAPPY)) {
							_videoEditor._commentatorPanel.cmbxEmotions.setSelectedItem("HAPPY");
							_videoEditor._commentatorPanel.cmbxPitches.setEnabled(false);
							_videoEditor._commentatorPanel.cmbxSpeeds.setEnabled(false);
						} else if (audioComm.getPitch().equals(FestivalPitch.SAD)) {
							_videoEditor._commentatorPanel.cmbxEmotions.setSelectedItem("SAD");
							_videoEditor._commentatorPanel.cmbxPitches.setEnabled(false);
							_videoEditor._commentatorPanel.cmbxSpeeds.setEnabled(false);
						} else {
							_videoEditor._commentatorPanel.cmbxEmotions.setSelectedItem("NEUTRAL");
							_videoEditor._commentatorPanel.cmbxPitches.setEnabled(true);
							_videoEditor._commentatorPanel.cmbxSpeeds.setEnabled(true);
							_videoEditor._commentatorPanel.cmbxPitches.setSelectedItem(audioComm.getPitch());
							_videoEditor._commentatorPanel.cmbxSpeeds.setSelectedItem(audioComm.getSpeed());
						}
						_videoEditor._commentatorPanel.commentatorTextEditor.setText(audioComm.getCommText());
					}
					_videoEditor._audioList.remove(_audio);
					DefaultTableModel model = (DefaultTableModel) projectTable.getModel();
					int row = projectTable.getSelectedRow();
					_videoEditor._audioTableInput = new Object[] { model.getValueAt(row, 0), model.getValueAt(row, 1),
							model.getValueAt(row, 2) };
					model.removeRow(row);
				} else {
					btnEditAudio.setText("Edit Audio");
					btnEditAudio.setEnabled(false);
					_videoEditor._audioEditorPanel.btnAddAudio.setText("Add Audio");
					_videoEditor._audioList.add(_audio);
					DefaultTableModel model = (DefaultTableModel) projectTable.getModel();
					model.addRow(_videoEditor._audioTableInput);
				}
			}
		});

		btnRemoveAudio.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel model = (DefaultTableModel) projectTable.getModel();
				int row = projectTable.getSelectedRow();
				model.removeRow(row);
				_videoEditor._audioList.remove(row);
				// because remove(obj) and indexOf(obj) threw out of bounds.
				try {
					BufferedWriter bw = new BufferedWriter(
							new FileWriter(_videoEditor._projectDir + _videoEditor.PROJECT_CONFIG, false));
					bw.write("VIDEO*" + _videoEditor._videoPlayer._videoManager.getMedia() + "\n");
					for (Audio audio : _videoEditor._audioList) {
						bw.write(audio.createProjectString());
					}
					bw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}

			}
		});

		this.add(btnSetProject, BorderLayout.NORTH);
		this.add(listScroll, BorderLayout.CENTER);
		this.add(listButtonPanel, BorderLayout.SOUTH);
		listButtonPanel.add(btnEditAudio, BorderLayout.CENTER);
		listButtonPanel.add(btnRemoveAudio, BorderLayout.EAST);
	}
}
