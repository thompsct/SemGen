package semgen.annotation.componentlistpanes.buttons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;

import semgen.annotation.workbench.CodewordToolDrawer.CodewordCompletion;
import semgen.utilities.SemGenFont;
import semsim.PropertyType;

public abstract class CodewordButton extends AnnotationObjectButton {
	private static final long serialVersionUID = -7555259367118052593L;
	private JLabel compannlabel = new JLabel("_");
	private PropertyMarker propoflabel = new PropertyMarker(Color.white, null);
	
	private Color constitutivecolor = new Color(255, 127, 14, 255);
	private Color entitycolor = new Color(31, 119, 180);
	private Color processcolor = new Color(63, 196, 63);
	
	public CodewordButton(String name, boolean canedit, boolean showmarkers) {
		super(name, canedit);
		indicatorspanel.setPreferredSize(new Dimension(50, 18));
		
		makeIndicator(compannlabel, "C", "Indicates status of codeword's composite annotation");
		compannlabel.addMouseListener(new IndicatorMouseListener(compannlabel));
		namelabel.setFont(SemGenFont.defaultItalic());
		drawButton();
		indicatorssuperpanel.add(propoflabel, BorderLayout.EAST);
	}
	
	public void refreshCompositeAnnotationCode(CodewordCompletion cwc) {
		compannlabel.setText(cwc.getCode());
	}

	public void togglePropertyMarkers(boolean showmarkers) {
		propoflabel.setVisible(showmarkers);
	}
	
	public boolean refreshPropertyOfMarker(PropertyType ptype){
		Color oldcolor = new Color(propoflabel.color.getRGB());
		Color col =  constitutivecolor;; 
		String tooltip;
		if(ptype == PropertyType.PropertyOfPhysicalEntity){
			col = entitycolor;
			tooltip = "<html>Codeword identified as a property of a physical <i>entity</i></html>";
		}
		else if(ptype == PropertyType.PropertyOfPhysicalProcess){
			col = processcolor;
			tooltip = "<html>Codeword identified as a property of a physical <i>process</i></html>";
		}
		else{
			tooltip = "<html>Codeword identified as a property of a <i>constitutive</i> relation.</htm>";
		}
		propoflabel.updateType(col, tooltip);
		indicatorssuperpanel.add(propoflabel, BorderLayout.EAST);
		validate();

		// Indicate whether to re-scroll to this button
		return (oldcolor!=col); 
	}
}
