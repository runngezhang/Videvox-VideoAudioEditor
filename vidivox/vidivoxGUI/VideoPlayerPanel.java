package vidivoxGUI;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

import vidivoxAudio.Audio;
import vidivoxManagers.VideoComponentAdapter;

/**
 * Creates a panel to display and play video
 * @author jay
 *
 */
public class VideoPlayerPanel extends JPanel {
	VideoEditorFrame _videoEditor;
	protected VideoComponentAdapter _videoManager;

			// Initializing video hotbar
			final JPanel hotBarPanel = new JPanel(new BorderLayout());

			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

			// Initializing progress panel
			final JPanel progressPanel = new JPanel(new BorderLayout());
			final JSlider progressSlider = new JSlider(0, 100, 0);

			final JLabel lblCurrentTime = new JLabel("   00:00 ");
			final JLabel lblTotalTime = new JLabel(" 00:00   ");

			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

			// Initializing button panel
			final JPanel buttonPanel = new JPanel(new BorderLayout());
			// Initializing play button
			final ImageIcon play = new ImageIcon(getClass().getResource("images/Play.png"));
			final ImageIcon playHover = new ImageIcon(getClass().getResource("images/PlayHover.png"));
			final ImageIcon pause = new ImageIcon(getClass().getResource("images/pause.png"));
			final ImageIcon pauseHover = new ImageIcon(getClass().getResource("images/pauseHover.png"));
			final JButton btnPlayPause = new JButton(play);


			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

			// Initializing show/hide buttons
			final JPanel viewPanel = new JPanel(new BorderLayout());
			final JPanel viewButtonPanel = new JPanel();

			final JButton btnCommentator = new JButton("Show Commentator");
			final JButton btnAudioEditor = new JButton("Show Audio Editor");
			final JButton btnDirViewer = new JButton("☰");

			// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

			// Initializing volume panel
			final JPanel volumePanel = new JPanel(new BorderLayout());
			// Initialzing the volume button and all its dynamic components
			final ImageIcon noVolume = new ImageIcon(getClass().getResource("images/noVolume.png"));
			final ImageIcon noVolumeHover = new ImageIcon(getClass().getResource("images/noVolumeHover.png"));

			final ImageIcon quarterVolume = new ImageIcon(getClass().getResource("images/quarterVolume.png"));
			final ImageIcon quarterVolumeHover = new ImageIcon(getClass().getResource("images/quarterVolumeHover.png"));

			final ImageIcon threeQuarterVolume = new ImageIcon(getClass().getResource("images/threeQuarterVolume.png"));
			final ImageIcon threeQuarterVolumeHover = new ImageIcon(getClass().getResource("images/threeQuarterVolumeHover.png"));

			final ImageIcon fullVolume = new ImageIcon(getClass().getResource("images/fullVolume.png"));
			final ImageIcon fullVolumeHover = new ImageIcon(getClass().getResource("images/fullVolumeHover.png"));

			final ImageIcon muteIcon = new ImageIcon(getClass().getResource("images/mute.png"));
			final ImageIcon muteHoverIcon = new ImageIcon(getClass().getResource("images/muteHover.png"));

			final JButton btnMute = new JButton(threeQuarterVolume);

			final JSlider volumeSlider = new JSlider(0, 200, 100);
			
			public VideoPlayerPanel(VideoEditorFrame videoEditor) {
				try {
					_videoManager = new VideoComponentAdapter();
				} catch (FileNotFoundException E) {
					System.err.println(E.getMessage());
					JOptionPane.showMessageDialog(this, E.getMessage(), "VLC NOT FOUND", JOptionPane.ERROR_MESSAGE);
				}
				
				_videoEditor = videoEditor;
				
				this.setLayout(new BorderLayout());
				
				viewButtonPanel.setLayout(new BoxLayout(viewButtonPanel, BoxLayout.X_AXIS));
				btnPlayPause.setRolloverIcon(playHover);
				btnPlayPause.setContentAreaFilled(false);
				
				btnMute.setRolloverIcon(threeQuarterVolumeHover);
				btnMute.setContentAreaFilled(false);
				
				// Setting listener for user input to progress bar
				progressSlider.addMouseMotionListener(new MouseAdapter() {
					@Override
					public void mouseDragged(MouseEvent e) {
						//audio muted to prevent garbled audio while dragging
						_videoManager.mute();
						if (_videoManager.isPlaying()) {
							for (Audio audio : _videoEditor._audioList) {
								audio.getAudioPlayer().mute();
							}
						}
						int progress = progressSlider.getValue();
						_videoManager.setPosition(progress);
						if (_videoManager.isPlaying()) {
							for (Audio audio : _videoEditor._audioList) {
								audio.getAudioPlayer().setTime(_videoManager.getTime() - audio.getStartTime(), true);
								audio.getAudioPlayer().unMute();
							}
						}
						_videoManager.unMute();
					}
				});

				progressSlider.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						//sets progress to where clicked
						BasicSliderUI ui = (BasicSliderUI) progressSlider.getUI();
						Point p = e.getPoint();
						int value = ui.valueForXPosition(p.x);
						_videoManager.setPosition(value);
						if (_videoManager.isPlaying()) {
							for (Audio audio : _videoEditor._audioList) {
								audio.getAudioPlayer().setTime(_videoManager.getTime() - audio.getStartTime(), true);
							}
						}
					}
				});

				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

				// Setting listeners for video manipulation buttons
				btnPlayPause.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (_videoEditor._projectDir.equals("")) {
							JOptionPane.showMessageDialog(null, "Please select a project first", "No Project Selected",
									JOptionPane.ERROR_MESSAGE);
						} else {
							if (btnPlayPause.getIcon().equals(play)) {
								_videoManager.play();
								btnPlayPause.setIcon(pause);
								btnPlayPause.setRolloverIcon(pauseHover);
							} else {
								_videoManager.pause();
								btnPlayPause.setIcon(play);
								btnPlayPause.setRolloverIcon(playHover);
								for (Audio audio : _videoEditor._audioList) {
									audio.getAudioPlayer().pause();
								}
							}
						}
					}
				});

				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

				// Setting listeners for panel show/hide buttons
				btnCommentator.addActionListener(new ActionListener() {
					//show/hide commentator
					@Override
					public void actionPerformed(ActionEvent ae) {
						if (btnCommentator.getText().equals("Hide Commentator")) {
							_videoEditor._commentatorTabbedPane.setVisible(false);
							btnCommentator.setText("Show Commentator");
						} else {
							_videoEditor._commentatorTabbedPane.setVisible(true);
							btnCommentator.setText("Hide Commentator");
						}
					}
				});

				btnAudioEditor.addActionListener(new ActionListener() {
					//show/hide editor
					@Override
					public void actionPerformed(ActionEvent ae) {
						if (btnAudioEditor.getText().equals("Hide Audio Editor")) {
							_videoEditor._audioEditorTabbedPane.setVisible(false);
							btnAudioEditor.setText("Show Audio Editor");
						} else {
							_videoEditor._audioEditorTabbedPane.setVisible(true);
							btnAudioEditor.setText("Hide Audio Editor");
						}
					}
				});

				btnDirViewer.addActionListener(new ActionListener() {
					//show/hide audio list
					@Override
					public void actionPerformed(ActionEvent ae) {
						if (_videoEditor._audioListPanel.isVisible()) {
							_videoEditor._audioListPanel.setVisible(false);
						} else {
							_videoEditor._audioListPanel.setVisible(true);
						}
					}
				});

				// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

				// Setting listeners for volume components
				btnMute.addActionListener(new ActionListener() {
					//sets volume icon on mute/unmute and mutes/unmutes
					@Override
					public void actionPerformed(ActionEvent ae) {
						if (_videoManager.isMuted()) {
							_videoManager.unMute();
							int volume = _videoManager.getVolume();
							if (volume == 0) {
								btnMute.setIcon(noVolume);
								btnMute.setRolloverIcon(noVolumeHover);
							} else if (volume <= (200 / 3)) {
								btnMute.setIcon(quarterVolume);
								btnMute.setRolloverIcon(quarterVolumeHover);
							} else if (volume <= (400 / 3)) {
								btnMute.setIcon(threeQuarterVolume);
								btnMute.setRolloverIcon(threeQuarterVolumeHover);
							} else {
								btnMute.setIcon(fullVolume);
							}
						} else {
							_videoManager.mute();
							btnMute.setIcon(muteIcon);
							btnMute.setRolloverIcon(muteHoverIcon);
						}
					}
				});

				volumeSlider.addChangeListener(new ChangeListener() {
					//changes volume and sets appropriate volume icons
					@Override
					public void stateChanged(ChangeEvent e) {
						int volume = volumeSlider.getValue();
						_videoManager.setVolume(volumeSlider.getValue());
						volume = _videoManager.getVolume();
						if (volume == 0) {
							btnMute.setIcon(noVolume);
							btnMute.setRolloverIcon(noVolumeHover);
						} else if (volume <= (200 / 3)) {
							btnMute.setIcon(quarterVolume);
							btnMute.setRolloverIcon(quarterVolumeHover);
						} else if (volume <= (400 / 3)) {
							btnMute.setIcon(threeQuarterVolume);
							btnMute.setRolloverIcon(threeQuarterVolumeHover);
						} else {
							btnMute.setIcon(fullVolume);
							btnMute.setRolloverIcon(fullVolumeHover);
						}
					}
				});
				
				this.add(_videoManager.getComponent(), BorderLayout.CENTER);
				this.add(hotBarPanel, BorderLayout.SOUTH);

				hotBarPanel.add(progressPanel, BorderLayout.NORTH);
				progressPanel.add(progressSlider, BorderLayout.CENTER);
				progressPanel.add(lblCurrentTime, BorderLayout.WEST);
				progressPanel.add(lblTotalTime, BorderLayout.EAST);

				hotBarPanel.add(buttonPanel, BorderLayout.WEST);
				buttonPanel.add(btnPlayPause, BorderLayout.CENTER);

				hotBarPanel.add(viewPanel, BorderLayout.CENTER);
				viewPanel.add(viewButtonPanel, BorderLayout.EAST);
				viewButtonPanel.add(btnAudioEditor);
				viewButtonPanel.add(btnCommentator);

				hotBarPanel.add(volumePanel, BorderLayout.EAST);
				volumePanel.add(btnMute, BorderLayout.WEST);
				volumePanel.add(volumeSlider, BorderLayout.CENTER);
				volumePanel.add(btnDirViewer, BorderLayout.EAST);

			}
			
			public void setMedia(String filePath) {
				_videoManager.setMedia(filePath);
				long millis = _videoManager.getLength();
				String time = _videoEditor.getMmss(millis);
				lblTotalTime.setText(time);
			}
}
