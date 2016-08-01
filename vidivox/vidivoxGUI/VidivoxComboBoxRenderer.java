package vidivoxGUI;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

/**
 * Custom renderer to use for commentator combo boxes (allows display of
 * non-selectable titles)
 * 
 * @author jay
 *
 */
public class VidivoxComboBoxRenderer extends BasicComboBoxRenderer {
	private String title;

	/**
	 * Set the text to display when no item has been selected.
	 */
	public VidivoxComboBoxRenderer(String title) {
		this.title = title;
		setHorizontalAlignment(LEFT);
	}

	/**
	 * Custom rendering to display the prompt text when no item is selected
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		if (value == null) {
			setText("  " + title);
		} else {
			setText("  " + value);
		}

		return this;
	}

}