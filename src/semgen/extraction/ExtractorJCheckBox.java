package semgen.extraction;

<<<<<<< HEAD
=======

>>>>>>> 2eb394907b98577f1b916408cf22a2de6952b22d
import java.util.HashSet;
import java.util.Set;
import javax.swing.JCheckBox;

import semsim.model.computational.DataStructure;
import semsim.model.physical.PhysicalModelComponent;

<<<<<<< HEAD
public class ExtractorJCheckBox extends JCheckBox {

=======

public class ExtractorJCheckBox extends JCheckBox {
	/**
	 * 
	 */
>>>>>>> 2eb394907b98577f1b916408cf22a2de6952b22d
	private static final long serialVersionUID = -3088569216874092127L;
	// data structures are the codewords, not the URIs
	public Set<DataStructure> associateddatastructures = new HashSet<DataStructure>();
	public PhysicalModelComponent pmc;

	public ExtractorJCheckBox(String name, Set<DataStructure> assocdatastr) {
		super(name);
		associateddatastructures.addAll(assocdatastr);
		this.setToolTipText(concatCodewordsForToolTip(associateddatastructures));
	}
	
	public ExtractorJCheckBox(String name, PhysicalModelComponent pmc, Set<DataStructure> assocdatastr){
		super(name);
		associateddatastructures.addAll(assocdatastr);
		this.setToolTipText(concatCodewordsForToolTip(associateddatastructures));
		this.pmc = pmc;
	}
	
	public static String concatCodewordsForToolTip(Set<DataStructure> associateddatastructures){
		String tooltip = "";
		for (DataStructure ds : associateddatastructures) {
			tooltip = tooltip + ds.getName() + ", ";
		}
		if(!tooltip.equals("")){
			tooltip = tooltip.substring(0, tooltip.lastIndexOf(","));
		}
		return tooltip;
	}
}
