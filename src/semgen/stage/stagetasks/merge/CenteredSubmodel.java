package semgen.stage.stagetasks.merge;

import java.util.HashMap;

import semsim.model.collection.Submodel;
import semsim.model.computational.Computation;
import semsim.model.computational.datastructures.DataStructure;

public class CenteredSubmodel extends Submodel {

	private DataStructure focusds;
	
	public CenteredSubmodel(DataStructure ds, String name) {
		super(name);
		focusds = getConnections(ds);
	}
	
	public CenteredSubmodel(CenteredSubmodel submodel) {
		super(submodel);
		
		this.focusds = submodel.focusds;
	}
	
	public CenteredSubmodel(CenteredSubmodel submodel, String name) {
		super(name);
		focusds = submodel.getFocusDataStructure();
		this.addSubmodel(submodel);
	}
	
	
	private DataStructure getConnections(DataStructure ds) {
		HashMap<DataStructure, DataStructure> dsmap = new HashMap<DataStructure, DataStructure>();
		DataStructure dscopy = copyDataStructure(ds, getName());
		
		dsmap.put(ds, dscopy);

		for (DataStructure compds : ds.getComputationInputs()) {
			if (!dsmap.containsKey(compds))
				dsmap.put(compds, copyDataStructure(compds, getName()));
		}
		
		for (DataStructure compds : ds.getComputationOutputs()) {
			if (!dsmap.containsKey(compds))
				dsmap.put(compds, copyDataStructure(compds, getName()));
		}
		
		for (DataStructure compds : ds.getUsedToCompute()) {
			if (!dsmap.containsKey(compds)) 
				dsmap.put(compds, copyDataStructure(compds, getName()));
		}
		
		for (DataStructure sourceds : dsmap.keySet()) {
			DataStructure replacer = dsmap.get(sourceds);
			
			addDataStructure(replacer);
			replaceDataStructures(dsmap, sourceds);
		}
		return dscopy;
	}

	private DataStructure copyDataStructure(DataStructure dstocopy, String name) {
		DataStructure copy = dstocopy.copy();
		copy.setName(name + "." + copy.getName());
		copy.getUsedToCompute().clear();
		copy.setComputation(new Computation(copy));
		
		return copy;
	}
	
	//Replace inputs and outputs for a DataStructure with their copies
	private void replaceDataStructures(HashMap<DataStructure, DataStructure> dsmap, DataStructure original) {
		DataStructure copy = dsmap.get(original);
		
		for (DataStructure input : original.getComputationInputs()) {
			DataStructure match = dsmap.get(input);
			if (match!=null) {
				copy.getComputation().addInput(match);
			}
		}
		
		for (DataStructure output : original.getComputationOutputs()) {
			DataStructure match = dsmap.get(output);
			if (match!=null) {
				copy.getComputation().addOutput(match);
			}
		}
		for (DataStructure output : original.getUsedToCompute()) {
			DataStructure match = dsmap.get(output);
			if (match!=null) {
				copy.addUsedToCompute(match);
			}
		}
	}

	public void addUsedtoComputetoFocus(DataStructure dstoadd, String parentname) {
		Submodel computesubmodel = new Submodel(parentname);
		for (DataStructure ds : dstoadd.getUsedToCompute()) {
			focusds.addUsedToCompute(ds);
			ds.replaceDataStructureReference(focusds, dstoadd);
			computesubmodel.addDataStructure(ds);
			
		}
		
		this.addSubmodel(computesubmodel);
	}
	
	public DataStructure getFocusDataStructure() {
		return focusds;
	}
	
}
