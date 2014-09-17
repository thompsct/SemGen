package semgen.annotation.dialog.selectordialog;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;

import org.semanticweb.owlapi.model.OWLException;

import semgen.annotation.AnnotatorTab;
import semgen.annotation.codewordpane.CodewordButton;
import semgen.annotation.dialog.referencedialog.ReferenceClassFinderPanel;
import semgen.resource.uicomponents.SemGenScrollPane;
import semsim.SemSimConstants;
import semsim.model.annotation.Annotation;
import semsim.model.annotation.ReferenceOntologyAnnotation;
import semsim.model.physical.PhysicalModelComponent;
import semsim.writing.CaseInsensitiveComparator;

public class AnnotationComponentReplacer extends SemSimComponentSelectorDialog implements
		PropertyChangeListener {

	private static final long serialVersionUID = 5155214590420468140L;
	public ReferenceClassFinderPanel refclasspanel;
	public JOptionPane optionPane;
	public Hashtable<String, String> oldclsnamesanduris = new Hashtable<String, String>();
	public JList<String> list = new JList<String>();

	public AnnotationComponentReplacer() throws OWLException {
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		refreshListData();
		SemGenScrollPane scpn = new SemGenScrollPane(list);
		
		refclasspanel = new ReferenceClassFinderPanel(SemSimConstants.ALL_SEARCHABLE_ONTOLOGIES);

		Object[] dialogarray = { scpn, refclasspanel };

		optionPane = new JOptionPane(dialogarray, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION, null);
		optionPane.addPropertyChangeListener(this);
		Object[] options = new Object[] { "Replace all", "Close" };
		optionPane.setOptions(options);
		optionPane.setInitialValue(options[0]);

		setContentPane(optionPane);

		setTitle("Select a term to replace from the top list, then a term to replace it");
		setModalityType(ModalityType.APPLICATION_MODAL);
		pack();
		setVisible(true);
	}
	

	public void propertyChange(PropertyChangeEvent arg0) {
		String propertyfired = arg0.getPropertyName();
		if (propertyfired.equals("value")) {
			String value = optionPane.getValue().toString();
			if (value.equals("Replace all")) {
				String oldclass = (String) list.getSelectedValue();
				
				String newclassuri = (String) refclasspanel.resultsanduris.get(refclasspanel.resultslistright.getSelectedValue());
				String oldclassuri = (String) oldclsnamesanduris.get(oldclass);
				
				for(PhysicalModelComponent pmc : semsimmodel.getPhysicalModelComponents()){
					for(Annotation anno : pmc.getAnnotations()){
						if(anno instanceof ReferenceOntologyAnnotation){
							ReferenceOntologyAnnotation refann = (ReferenceOntologyAnnotation)anno;
							if(refann.getReferenceURI().toString().equals(oldclassuri)){
								refann.setValueDescription((String) refclasspanel.resultslistright.getSelectedValue());
								refann.setReferenceURI(URI.create(newclassuri));
								refann.setReferenceOntologyAbbreviation(URI.create(newclassuri));
							}
						}
					}
				}
				
				JOptionPane.showMessageDialog(this, "Finished replacement");
				refreshListData();
				optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
			} else if (value.equals("Close")) {
				dispose();
			}
		}
	}
	
	public void refreshListData() {
		Set<String> refclassnames = new HashSet<String>();
		for(PhysicalModelComponent pmc : semsimmodel.getPhysicalModelComponents()){
			if(pmc.hasRefersToAnnotation()){
				ReferenceOntologyAnnotation ann = pmc.getFirstRefersToReferenceOntologyAnnotation();
				String label = ann.getValueDescription();
				URI uri = ann.getReferenceURI();
				label = label + " (" + ann.getOntologyAbbreviation() + ")";
				oldclsnamesanduris.put(label, uri.toString());
				refclassnames.add(label);
			}
		}

		String[] array = refclassnames.toArray(new String[] {});
		Arrays.sort(array, new CaseInsensitiveComparator());
		list.setListData(array);
	}
}
