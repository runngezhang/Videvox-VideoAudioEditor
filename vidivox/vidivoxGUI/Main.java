package vidivoxGUI;

import java.awt.EventQueue;

import javax.swing.UIManager;

/**
 * Initializes the gui and UI
 * @author jay
 *
 */
public class Main {
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
					UIManager.put("Slider.paintValue", false);
					VideoEditorFrame window = new VideoEditorFrame();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
