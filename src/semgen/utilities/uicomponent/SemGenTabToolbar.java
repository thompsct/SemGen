package semgen.utilities.uicomponent;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import semgen.SemGenSettings;
import semgen.utilities.SemGenFont;

public class SemGenTabToolbar extends JToolBar {
	private static final long serialVersionUID = 1L;
	protected SemGenSettings settings;
	
	public SemGenTabToolbar(SemGenSettings sets) {
		settings = sets;
		setFloatable(false);
		setOpaque(true);
		setAlignmentY(JPanel.TOP_ALIGNMENT);
	}
	
	public SemGenTabToolbar(int orient) {
		super(orient);
		setFloatable(false);
		setOpaque(true);
		setAlignmentY(JPanel.TOP_ALIGNMENT);
	}
	
	protected class ToolBarLabel extends JLabel {
		private static final long serialVersionUID = 1L;

		public ToolBarLabel(String caption) {
			super(caption);
			setFont(SemGenFont.defaultBold());
		}
	}
	
	protected class SemGenToolbarButton extends JButton {
		private static final long serialVersionUID = 1L;

		public SemGenToolbarButton(ImageIcon icon) {
			super(icon);
			
			setSize(new Dimension(30, 30));
			setRolloverEnabled(true);
			setPreferredSize(new Dimension(30, 30));
			setAlignmentY(JButton.TOP_ALIGNMENT);
			addMouseListener(new btnbehavior(this));
		}
		
		private class btnbehavior extends MouseAdapter {
			AbstractButton button;
			public btnbehavior(AbstractButton btn) {
				button = btn;
			}
			public void mouseEntered(MouseEvent e) {
				mouseGraphics(button, true);
			}

			public void mouseExited(MouseEvent e) {
				mouseGraphics(button, false);
			}
		}
	}
	protected class SemGenToolbarRadioButton extends JRadioButton {
		private static final long serialVersionUID = 1L;

		public SemGenToolbarRadioButton(ImageIcon icon) {
			super(icon);
			setSize(new Dimension(30, 30));
			setRolloverEnabled(true);
			setPreferredSize(new Dimension(30, 30));
			setAlignmentY(JButton.TOP_ALIGNMENT);
			addMouseListener(new btnbehavior(this));
			setHorizontalAlignment(SwingConstants.CENTER);
			setVerticalTextPosition(SwingConstants.CENTER);
			setBorderPainted(true);
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		}
		
		public void toggleSelectionGraphic() {
			radioMouseGraphics(this, isSelected());
		}
		
		private class btnbehavior extends MouseAdapter {
			AbstractButton button;
			public btnbehavior(AbstractButton btn) {
				button = btn;
			}
			public void mouseEntered(MouseEvent e) {
				if (!isSelected()) {
					radioMouseGraphics(button, true);
				}
			}

			public void mouseExited(MouseEvent e) {
				if (!isSelected()) {
					radioMouseGraphics(button, false);
				}
			}
		}
	}
	
	private void mouseGraphics(AbstractButton button, boolean dostuff) {
		button.setBorderPainted(dostuff);
	}
	
	private void radioMouseGraphics(AbstractButton button, boolean dostuff) {
		if (dostuff) {
			button.setBorder(BorderFactory.createLineBorder(Color.red, 2));
		}
		else button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
	}
}
