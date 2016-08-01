package vidivoxGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.border.Border;

import vidivoxAudio.Audio;
import vidivoxFileChoosers.AudioChooser;
import vidivoxFileChoosers.ProjectChooser;
import vidivoxFileChoosers.VideoChooser;
import vidivoxManagers.CommentatorManager;
import vidivoxManagers.ExportManager;
import vidivoxManagers.ExportProgressMonitor;

/**
 * Creates the VIDIVOX main frame and initializes panels
 * @author jay
 *
 */
public class VideoEditorFrame extends JFrame {

	final protected CommentatorManager _commentatorManager = new CommentatorManager();
	protected ExportManager _exportManager = new ExportManager();
	final protected ExportProgressMonitor _monitor = new ExportProgressMonitor();

	protected ArrayList<Audio> _audioList = new ArrayList<Audio>();
	protected Object[] _audioTableInput = null;

	final protected String AUDIO_TYPE = ".mp3";
	final protected String VIDEO_TYPE = ".avi";
	final protected String PROJECT_CONFIG = "/.project.vdvx.txt";

	protected String _projectDir = "";

	protected Timer timer = new Timer(50, null);

	final protected VideoChooser selectVideoChooser = new VideoChooser();
	final protected ProjectChooser selectProjectChooser = new ProjectChooser();
	final protected AudioChooser selectAudioChooser = new AudioChooser();

	final protected MenuBar _menuBar = new MenuBar(this);

	final protected VideoPlayerPanel _videoPlayer = new VideoPlayerPanel(this);

	final protected AudioListPanel _audioListPanel = new AudioListPanel(this);

	final protected JTabbedPane _commentatorTabbedPane = new JTabbedPane();
	final protected CommentatorPanel _commentatorPanel = new CommentatorPanel();

	final protected JTabbedPane _audioEditorTabbedPane = new JTabbedPane();
	final protected AudioEditorPanel _audioEditorPanel = new AudioEditorPanel(this);

	public VideoEditorFrame() {

		// ===================================================================//

		// Initializing components

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Initializing frame
		this.setTitle("Vidivox");
		this.setBounds(100, 100, 1080, 550);
		this.setMinimumSize(new Dimension(1080, 550));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Initializing editing panel
		final JPanel frameSouthPanel = new JPanel(new BorderLayout());

		final JPanel editorPanel = new JPanel(new BorderLayout());
		final Border editorBorder = BorderFactory.createEtchedBorder();
		editorPanel.setBorder(editorBorder);
		editorPanel.setBackground(Color.decode("#3D3D3D"));
		
		_audioEditorTabbedPane.setFocusable(false);
		_audioEditorTabbedPane.setVisible(false);
		_commentatorTabbedPane.setFocusable(false);
		_commentatorTabbedPane.setVisible(false);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Initializing footer panel
		final JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		final JLabel lblProgress = new JLabel("No export in progress.  ");
		final JProgressBar editorProgress = new JProgressBar();

		// ===================================================================//

		// Setting up components

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Adding listener for timer
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				// get vid progress
				_videoPlayer.progressSlider.setValue(_videoPlayer._videoManager.getPosition());
				long currentTimeMillis = _videoPlayer._videoManager.getTime();
				String ms = getMmss(currentTimeMillis);
				_videoPlayer.lblCurrentTime.setText("   " + ms);
				//inform audio objects of current time
				if (_videoPlayer._videoManager.isPlaying()) {
					for (Audio audio : _audioList) {
						audio.getAudioPlayer().setTime(_videoPlayer._videoManager.getTime() - audio.getStartTime(), false);
					}
				} else {
					_videoPlayer.btnPlayPause.setIcon(_videoPlayer.play);
					_videoPlayer.btnPlayPause.setRolloverIcon(_videoPlayer.playHover);
					for (Audio audio : _audioList) {
						audio.getAudioPlayer().pause();
					}
				}
				// check editing progress
				if (_monitor.isDone()) {
					lblProgress.setText("No export in progress. ");
					editorProgress.setValue(0);
				} else {
					lblProgress.setText("Exporting video... ");
					int progress = _monitor.getEditProgress();
					editorProgress.setValue(progress);
				}
			}
		});
		timer.start();

		// ===================================================================//

		// Adding components to frame
		this.getContentPane().add(_menuBar, BorderLayout.NORTH);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Adding video components
		this.getContentPane().add(_videoPlayer, BorderLayout.CENTER);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Adding list components
		this.getContentPane().add(_audioListPanel, BorderLayout.EAST);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Adding editor components
		this.getContentPane().add(frameSouthPanel, BorderLayout.SOUTH);
		frameSouthPanel.add(editorPanel, BorderLayout.CENTER);
		// Adding audio editor components
		editorPanel.add(_audioEditorTabbedPane, BorderLayout.NORTH);
		_audioEditorTabbedPane.add(_audioEditorPanel, "Audio Editor");

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Adding commentator components
		editorPanel.add(_commentatorTabbedPane, BorderLayout.CENTER);
		_commentatorTabbedPane.add(_commentatorPanel, "Commentator");

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~//

		// Adding footer panel
		frameSouthPanel.add(footerPanel, BorderLayout.SOUTH);
		footerPanel.add(lblProgress);
		footerPanel.add(editorProgress);
	}

	protected long getMillis(int min, int sec, int msec) {
		long totalMillis = 0;
		totalMillis += TimeUnit.MINUTES.toMillis(min);
		totalMillis += TimeUnit.SECONDS.toMillis(sec);
		totalMillis += msec;
		return totalMillis;
	}

	protected String getMmss(long millis) {
		String ms = (_videoPlayer._videoManager.getLength() > TimeUnit.HOURS.toMillis(1))
				? String.format("%03d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
						TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1))
				: String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis),
						TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
		return ms;
	}

	protected void setFromMmssmm(int min, int sec, int msec) {
		long totalMillis = getMillis(min, sec, msec);
		_videoPlayer._videoManager.setTime(totalMillis);
	}
}
