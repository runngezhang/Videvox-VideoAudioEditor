package vidivoxGUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;
import javax.swing.border.Border;

import festivalEnums.*;
import vidivoxFileChoosers.AudioChooser;
import vidivoxManagers.CommentatorManager;
import vidivoxWorkers.FestivalWorker;

/**
 * Creates commentator panel for the selection of festival settings and
 * provision of GUI for festival text-to-speech system use
 * 
 * @author jay
 *
 */
public class CommentatorPanel extends JPanel {
	// ===================================================================//
	// Initializing commentator panel
	final protected CommentatorManager _commentatorManager = new CommentatorManager();
	protected FestivalWorker _festival;
	final protected AudioChooser selectAudioChooser = new AudioChooser();

	final JEditorPane commentatorTextEditor = new JEditorPane() {
		public boolean getScrollableTracksViewportWidth() {
			return true;
		}
	};
	final Border commentaryTextBorder = BorderFactory.createEtchedBorder();

	final private Timer timer = new Timer(50, null);

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

	final JPanel commentatorButtonPanel = new JPanel();

	final JButton btnSave = new JButton("Save...");
	final JButton btnTest = new JButton("Test");
	final JButton btnStop = new JButton("Stop");
	final JButton btnClear = new JButton("Clear");

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

	final JPanel commentatorOptionPanel = new JPanel();

	FestivalVoice[] festVoices = { FestivalVoice.KAL, FestivalVoice.KED, FestivalVoice.RAB, FestivalVoice.DON };
	final JComboBox<?> cmbxVoices = new JComboBox<Object>(festVoices);

	String[] festEmotions = { "NEUTRAL", "HAPPY", "SAD" };
	final JComboBox<?> cmbxEmotions = new JComboBox<Object>(festEmotions);

	FestivalPitch[] festPitches = { FestivalPitch.HIGH, FestivalPitch.NORMAL, FestivalPitch.LOW };
	final JComboBox<?> cmbxPitches = new JComboBox<Object>(festPitches);

	FestivalSpeed[] festSpeeds = { FestivalSpeed.FAST, FestivalSpeed.NORMAL, FestivalSpeed.SLOW };
	final JComboBox<?> cmbxSpeeds = new JComboBox<Object>(festSpeeds);

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

	final JPanel festivalInfoPanel = new JPanel(new BorderLayout());
	final JLabel lblWordCount = new JLabel("Wordcount: 8");

	// ===================================================================//

	public CommentatorPanel() {

		// ===================================================================//
		this.setLayout(new BorderLayout());

		commentatorTextEditor.setBorder(commentaryTextBorder);
		commentatorTextEditor.setText("Enter text to be synthesized for commentary here");

		commentatorButtonPanel.setLayout(new BoxLayout(commentatorButtonPanel, BoxLayout.Y_AXIS));

		btnStop.setEnabled(false);

		// ensures all buttons are the same size
		Dimension d = new Dimension();
		d.setSize(104, 29);
		btnSave.setMaximumSize(d);
		btnTest.setMaximumSize(d);
		btnStop.setMaximumSize(d);
		btnClear.setMaximumSize(d);

		commentatorOptionPanel.setLayout(new BoxLayout(commentatorOptionPanel, BoxLayout.Y_AXIS));

		cmbxVoices.setRenderer(new VidivoxComboBoxRenderer("Voice"));
		cmbxVoices.setSelectedIndex(-1);

		cmbxEmotions.setRenderer(new VidivoxComboBoxRenderer("Emotion"));
		cmbxEmotions.setSelectedIndex(-1);

		cmbxPitches.setRenderer(new VidivoxComboBoxRenderer("Pitch"));
		cmbxPitches.setSelectedIndex(-1);

		cmbxSpeeds.setRenderer(new VidivoxComboBoxRenderer("Speed"));
		cmbxSpeeds.setSelectedIndex(-1);

		// ensures combo boxes are the same size
		d.setSize(104, 29);
		cmbxVoices.setPreferredSize(d);
		cmbxEmotions.setPreferredSize(d);
		cmbxPitches.setPreferredSize(d);
		cmbxSpeeds.setPreferredSize(d);
		// ===================================================================//

		// Setting listeners for commentator panel

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Setting up listeners for commentator text editor
		commentatorTextEditor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// clears initial text for user
				if (commentatorTextEditor.getText().equals("Enter text to be synthesized for commentary here")) {
					commentatorTextEditor.setText("");
					lblWordCount.setText("Wordcount: 0");
					btnTest.setEnabled(false);
					btnSave.setEnabled(false);
				}
			}
		});

		commentatorTextEditor.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// updates the word counter
				int words = commentatorTextEditor.getText().split("\\s+").length;
				lblWordCount.setText("Wordcount: " + words);
				if (commentatorTextEditor.getText().equals("")) {
					btnTest.setEnabled(false);
					btnSave.setEnabled(false);
					lblWordCount.setText("Wordcount: 0");
				} else {
					btnTest.setEnabled(true);
					btnSave.setEnabled(true);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// ensures special character used in project strings not
				// accepted
				if (e.getKeyChar() == '*') {
					e.consume();
				}
			}
		});

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Setting listeners for commentator button panel

		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				//saves commentary as mp3
				int status = selectAudioChooser.showOpenDialog(null);
				if (status == JFileChooser.APPROVE_OPTION) {
					String fileName = selectAudioChooser.getSelectedFile().getAbsolutePath();
					if (!fileName.endsWith(".mp3")) {
						fileName += ".mp3";
					}
					_commentatorManager.createMp3(fileName, commentatorTextEditor.getText());
				}
			}
		});

		btnTest.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				//creates a festival worker to say text
				_festival = _commentatorManager.execute(commentatorTextEditor.getText());
				btnStop.setEnabled(true);
			}
		});

		btnStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				//stops festival speech
				_festival.cancel(true);
				btnStop.setEnabled(false);
			}
		});

		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				//clears text in editor
				commentatorTextEditor.setText("");
				lblWordCount.setText("Wordcount: 0");
				btnTest.setEnabled(false);
				btnSave.setEnabled(false);
			}
		});

		cmbxVoices.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_commentatorManager.setVoice((FestivalVoice) cmbxVoices.getSelectedItem());
			}
		});

		cmbxEmotions.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//sets pitch and speed enums to emotion settings
				if (cmbxEmotions.getSelectedItem().equals("NEUTRAL")) {
					if ((cmbxPitches.getSelectedIndex() != -1)) {
						_commentatorManager.setPitch((FestivalPitch) cmbxPitches.getSelectedItem());
					} else {
						_commentatorManager.setPitch(FestivalPitch.NORMAL);
					}
					if ((cmbxSpeeds.getSelectedIndex() != -1)) {
						_commentatorManager.setSpeed((FestivalSpeed) cmbxSpeeds.getSelectedItem());
					} else {
						_commentatorManager.setSpeed(FestivalSpeed.NORMAL);

					}
					cmbxPitches.setEnabled(true);
					cmbxSpeeds.setEnabled(true);
				} else {
					_commentatorManager.setPitch(FestivalPitch.asString((String) cmbxEmotions.getSelectedItem()));
					_commentatorManager.setSpeed(FestivalSpeed.asString((String) cmbxEmotions.getSelectedItem()));
					cmbxPitches.setEnabled(false);
					cmbxSpeeds.setEnabled(false);
				}
			}
		});

		cmbxPitches.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_commentatorManager.setPitch((FestivalPitch) cmbxPitches.getSelectedItem());
			}
		});

		cmbxSpeeds.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_commentatorManager.setSpeed((FestivalSpeed) cmbxSpeeds.getSelectedItem());
			}
		});

		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (_festival != null) {
					if (_festival.isDone()) {
						btnStop.setEnabled(false);
					}
				}
			}
		});
		timer.start();
		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Adding commentator components
		this.add(new JScrollPane(commentatorTextEditor), BorderLayout.CENTER);
		this.add(commentatorButtonPanel, BorderLayout.WEST);
		commentatorButtonPanel.add(btnSave);
		commentatorButtonPanel.add(btnTest);
		commentatorButtonPanel.add(btnStop);
		commentatorButtonPanel.add(btnClear);
		this.add(commentatorOptionPanel, BorderLayout.EAST);
		commentatorOptionPanel.add(cmbxVoices);
		commentatorOptionPanel.add(cmbxEmotions);
		commentatorOptionPanel.add(cmbxPitches);
		commentatorOptionPanel.add(cmbxSpeeds);
		this.add(festivalInfoPanel, BorderLayout.SOUTH);
		festivalInfoPanel.add(lblWordCount, BorderLayout.WEST);
	}
}
