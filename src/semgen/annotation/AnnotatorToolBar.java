package semgen.annotation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import semgen.GlobalActions;
import semgen.SemGenSettings;
import semgen.annotation.workbench.AnnotatorWorkbench;
import semgen.encoding.Encoder;
import semgen.utilities.SemGenIcon;
import semgen.utilities.uicomponent.DropDownCheckList;
import semgen.utilities.uicomponent.SemGenTabToolbar;
import semsim.model.physical.PhysicalEntity;
import semsim.model.physical.PhysicalModelComponent;
import semsim.model.physical.PhysicalProcess;
import semsim.model.physical.object.CompositePhysicalEntity;

public class AnnotatorToolBar extends SemGenTabToolbar implements ActionListener {
	private static final long serialVersionUID = 1L;

	private AnnotatorWorkbench workbench;
	private SemGenToolbarButton annotateitemchangesourcemodelcode = new SemGenToolbarButton(SemGenIcon.setsourceicon);
	private SemGenToolbarButton annotateitemcopy = new SemGenToolbarButton(SemGenIcon.importicon);
	private SemGenToolbarButton annotateitemexportcsv = new SemGenToolbarButton(SemGenIcon.exporticon);
	private SemGenToolbarButton annotateitemshowmarkers;
	private JButton annotateitemshowimports = new JButton("Show imports");
	private SemGenToolbarButton annotateitemaddrefterm= new SemGenToolbarButton(SemGenIcon.createicon);
	private SemGenToolbarButton annotateitemaddcompterm= new SemGenToolbarButton(SemGenIcon.createcompenticon);
	private SemGenToolbarButton annotateitemremoverefterm = new SemGenToolbarButton(SemGenIcon.eraseicon);
	public SemGenToolbarButton annotateitemreplacerefterm = new SemGenToolbarButton(SemGenIcon.replaceicon);
	private SemGenToolbarButton annotateitemtreeview = new SemGenToolbarButton(SemGenIcon.treeicon);
	private SemGenToolbarButton extractorbutton = new SemGenToolbarButton(SemGenIcon.extractoricon);
	private SemGenToolbarButton coderbutton = new SemGenToolbarButton(SemGenIcon.codericon);
	
	private DropDownCheckList sortselector = new DropDownCheckList(" Sort Options");
	private String sortbytype = new String("By Type");
	private String sortbycompletion = new String("By Composite Completeness");
	GlobalActions globalactions;

	public AnnotatorToolBar(GlobalActions gacts, AnnotatorWorkbench wkbnch, SemGenSettings sets) {
		super(sets);
		workbench = wkbnch;
		globalactions = gacts;

		annotateitemshowimports.addActionListener(this);
		annotateitemshowimports.setToolTipText("Make imported codewords and submodels visible");
		
		annotateitemshowmarkers = new SemGenToolbarButton(displayIcontoUse());
		annotateitemshowmarkers.addActionListener(this);
		annotateitemshowmarkers.setToolTipText("Display markers that indicate a codeword's property type");
		
		annotateitemtreeview.addActionListener(this);
		annotateitemtreeview.setToolTipText("Display codewords and submodels within the submodel tree");
		
		sortselector.addItem(sortbytype, "Sort codewords by physical property type", settings.organizeByPropertyType());
		sortselector.addItem(sortbycompletion, "Sort by the completeness of the composite term", settings.organizeByCompositeCompleteness());
		
		annotateitemcopy.addActionListener(this);
		annotateitemcopy.setToolTipText("Annotate codewords using data from identical codewords in another model");
		
		annotateitemchangesourcemodelcode.addActionListener(this);
		annotateitemchangesourcemodelcode.setToolTipText("Link the SemSim model with its computational code");
		
		annotateitemexportcsv.setToolTipText("Create a .csv file that tabulates model codeword annotations for use in spreadsheets, manuscript preparation, etc.");
		annotateitemexportcsv.addActionListener(this);

		annotateitemaddrefterm.addActionListener(this);
		annotateitemaddrefterm.setToolTipText("Add a reference ontology term to use for annotating this model");
				
		annotateitemremoverefterm.addActionListener(this);
		annotateitemremoverefterm.setToolTipText("Remove a physical entity or process term from the model");

		annotateitemreplacerefterm.setToolTipText("Replace a reference ontology term with another");
		annotateitemreplacerefterm.addActionListener(this);
		
		annotateitemaddcompterm.addActionListener(this);
		annotateitemaddcompterm.setToolTipText("Add an arbitrary composite to use for annotating this model");
		
		extractorbutton.setToolTipText("Open this model in Extractor");
		extractorbutton.addActionListener(this);

		coderbutton.setToolTipText("Encode this model for simulation");
		coderbutton.addActionListener(this);
		
		add(annotateitemtreeview);
		add(annotateitemshowmarkers);
		add(annotateitemshowimports);
		add(sortselector);
		addSeparator();
		
		add(annotateitemchangesourcemodelcode);
		add(annotateitemcopy);
		add(annotateitemexportcsv);
		addSeparator();
		
		add(new ToolBarLabel("Reference Terms:"));
		add(annotateitemaddrefterm);
		add(annotateitemaddcompterm);
		add(annotateitemremoverefterm);
		add(annotateitemreplacerefterm);
		
		addSeparator();
		
		add(extractorbutton);
		add(coderbutton);

		sortselector.addItemListener(new SortSelectionListener(settings));
	}
	
	public void enableSort(boolean enable) {
		sortselector.setEnabled(enable);
	}
	
	private ImageIcon displayIcontoUse() {
		if (settings.useDisplayMarkers()) return SemGenIcon.onicon;
		return SemGenIcon.officon;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		if (o == annotateitemshowmarkers){
			settings.toggleDisplayMarkers();

			annotateitemshowmarkers.setIcon(displayIcontoUse());
		}
	
		if(o == annotateitemshowimports){
			// Set visbility of imported codewords and submodels
			settings.toggleShowImports();

		}
		
		if(o == annotateitemtreeview){
			settings.toggleTreeView();
		}
		if (o == extractorbutton) {
			try {
				if(workbench.unsavedChanges()){
					globalactions.NewExtractorTab(workbench.getFile());
				}
			} catch (Exception e1) {
				e1.printStackTrace();}
		}
		
		if (o == annotateitemchangesourcemodelcode) {
				workbench.changeModelSourceFile();
		}

		if(o == annotateitemexportcsv){
				workbench.exportCSV(); 
		}
		
		if (o == annotateitemcopy) {
			workbench.importModelAnnotations();
		}
		
		if (o == annotateitemaddrefterm) {

			} 
		
		if (o == annotateitemremoverefterm) {
				Set<PhysicalModelComponent> pmcs = new HashSet<PhysicalModelComponent>();
				for(PhysicalModelComponent pmc : workbench.getSemSimModel().getPhysicalModelComponents()){
					if(!(pmc instanceof CompositePhysicalEntity) && (pmc instanceof PhysicalEntity || pmc instanceof PhysicalProcess))
						pmcs.add(pmc);
				}
			}
		
		if (o == annotateitemreplacerefterm) {

		}
		
		if (o == annotateitemaddcompterm) {

		}
		
		if (o == coderbutton) {
			String filenamesuggestion = null;
			if(workbench.getSourceModelLocation()!=null) filenamesuggestion = workbench.getSourceModelLocation().substring(0, workbench.getSourceModelLocation().lastIndexOf("."));
			if(workbench.unsavedChanges()){
				new Encoder(workbench.getSemSimModel(), filenamesuggestion);
			} 
		}
	}

	class SortSelectionListener implements ItemListener {
		SemGenSettings settings;
		public SortSelectionListener(SemGenSettings sets) {
			settings =sets;
		}
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			String obj = sortselector.getLastSelectedItem();
			if (obj == sortbytype) {
				settings.toggleByPropertyType();
		    }
			if (obj == sortbycompletion) {
				settings.toggleCompositeCompleteness();
		    }
		}
	}
	
}
