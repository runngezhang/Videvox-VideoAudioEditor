package vidivoxGUI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Creates a pamel for setting time
 * @author jay
 *
 */
public class TimeSetterPanel extends JPanel {
	VideoEditorFrame _videoEditor;

	final JButton btnTimeSetter = new JButton("▼");
	final JTextField txtMin = new JTextField("000");
	final JTextField txtSec = new JTextField("00");
	final JTextField txtMSec = new JTextField("000");
	final JButton btnSliderSetter = new JButton("▲");

	public TimeSetterPanel(String title, VideoEditorFrame videoEditor) {
		_videoEditor = videoEditor;
		final TitledBorder timeBorder = BorderFactory.createTitledBorder(title);

		this.setLayout(new FlowLayout());
		this.setBorder(timeBorder);
		this.setToolTipText("Time of audio in minutes, seconds, milliseconds");
		
		//sets width of text fields to show full text
		txtMin.setColumns(2);
		txtMin.setHorizontalAlignment(JTextField.CENTER);
		txtSec.setColumns(2);
		txtSec.setHorizontalAlignment(JTextField.CENTER);
		txtMSec.setColumns(2);
		txtMSec.setHorizontalAlignment(JTextField.CENTER);

		txtMin.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char input = e.getKeyChar();
				if (!Character.isDigit(input)
						//prevents invalid text (too many digits, non-numbers)
						|| txtMin.getText().length() >= 3 && !(txtMin.getHighlighter().getHighlights().length > 0)) {
					e.consume();
				}
			}
		});

		txtSec.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char input = e.getKeyChar();
				if (!Character.isDigit(input)
						//prevents invalid text (too many digits, non-numbers)
						|| txtSec.getText().length() >= 3 && !(txtSec.getHighlighter().getHighlights().length > 0)) {
				} else if (txtSec.getText().length() == 1 && Integer.parseInt(txtSec.getText()) > 5) {
					e.consume();
				}
			}
		});

		txtMSec.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				char input = e.getKeyChar();
				if (!Character.isDigit(input)
						//prevents invalid text (too many digits, non-numbers)
						|| txtMSec.getText().length() >= 3 && !(txtMSec.getHighlighter().getHighlights().length > 0)) {
					e.consume();
				}
			}
		});

		btnSliderSetter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//sets video time to time in text boxes
				_videoEditor.setFromMmssmm(Integer.parseInt(txtMin.getText()), Integer.parseInt(txtSec.getText()),
						Integer.parseInt(txtMSec.getText()));
			}
		});

		btnTimeSetter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//sets time in time setter to video time
				long totalMillis = _videoEditor._videoPlayer._videoManager.getTime();
				if (totalMillis >= 0) {
					setTime(totalMillis);
				}
			}
		});

		this.add(btnTimeSetter);
		this.add(txtMin);
		this.add(new JLabel(":"));
		this.add(txtSec);
		this.add(new JLabel(":"));
		this.add(txtMSec);
		this.add(btnSliderSetter);
	}

	public void setTime(long totalMillis) {
		long minutes = TimeUnit.MILLISECONDS.toMinutes(totalMillis);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(totalMillis) % TimeUnit.MINUTES.toSeconds(1);
		long millis = totalMillis % TimeUnit.SECONDS.toMillis(1);
		txtMin.setText(String.format("%03d", minutes));
		txtSec.setText(String.format("%02d", seconds));
		txtMSec.setText(String.format("%03d", millis));
	}

	public void setEnabled(boolean enable) {
		btnTimeSetter.setEnabled(enable);
		txtMin.setEnabled(enable);
		txtSec.setEnabled(enable);
		txtMSec.setEnabled(enable);
		btnSliderSetter.setEnabled(enable);
	}
}
