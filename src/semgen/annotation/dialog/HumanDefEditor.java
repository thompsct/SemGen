package semgen.annotation.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import semgen.resource.SemGenFont;

public class HumanDefEditor extends JDialog implements PropertyChangeListener {

	private static final long serialVersionUID = -4040704987589247388L;
	private JOptionPane optionPane;
	private JTextArea defarea = new JTextArea();
	public String ftd;

	public HumanDefEditor(String desig, String ft) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		ftd = ft;

		setPreferredSize(new Dimension(430, 250));
		setMaximumSize(getPreferredSize());
		setMinimumSize(getPreferredSize());
		setLocationRelativeTo(getParent());
		setTitle("Enter free-text description");
		setResizable(false);

		JLabel codewordlabel = new JLabel(desig);
		codewordlabel.setFont(SemGenFont.defaultBold());

		defarea.setForeground(Color.blue);
		defarea.setLineWrap(true);
		defarea.setWrapStyleWord(true);

		JScrollPane areascroller = new JScrollPane(defarea);

		Object[] array = { codewordlabel, areascroller };

		optionPane = new JOptionPane(array, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null);
		optionPane.addPropertyChangeListener(this);
		
		Object[] options  = new Object[] { "OK", "Cancel" };
		optionPane.setOptions(options);
		optionPane.setInitialValue(options[0]);

		setContentPane(optionPane);
		defarea.setText(ftd);
		defarea.requestFocusInWindow();
	}
	
	public String getFreeTextAnnotation() {
		return ftd;
	}

	public final void propertyChange(PropertyChangeEvent e) {
		String propertyfired = e.getPropertyName();
		if (propertyfired.equals("value")) {
			String value = optionPane.getValue().toString();
			if (value.equals("OK")) {
				ftd = defarea.getText();
			}
			dispose();
		}
	}
}