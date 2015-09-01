package semsim.model.physical.object;

import java.net.URI;

import semgen.SemGen;
import semsim.SemSimConstants;
import semsim.annotation.ReferenceOntologyAnnotation;
import semsim.annotation.ReferenceTerm;
import semsim.model.SemSimTypes;
import semsim.model.physical.PhysicalEntity;

public class ReferencePhysicalEntity extends PhysicalEntity implements ReferenceTerm{
	
	public ReferencePhysicalEntity(URI uri, String description){
		referenceuri = uri;
		setName(description);
	}
	
	public ReferenceOntologyAnnotation getRefersToReferenceOntologyAnnotation(){
		if(hasRefersToAnnotation()){
			return new ReferenceOntologyAnnotation(SemSimConstants.REFERS_TO_RELATION, referenceuri, getDescription());
		}
		return null;
	}
	
	public URI getReferstoURI() {
		return URI.create(referenceuri.toString());
	}
	
	/**
	 * @return The name of the knowledge base that contains the URI used as the annotation value
	 */
	public String getNamewithOntologyAbreviation() {
		return getName() + " (" + SemGen.semsimlib.getReferenceOntologyAbbreviation(referenceuri) + ")";
	}
	
	@Override
	protected boolean isEquivalent(Object obj) {
		return ((ReferencePhysicalEntity)obj).getReferstoURI().toString().equals(referenceuri.toString());
	}
	
	@Override
	public String getOntologyName() {
		return SemGen.semsimlib.getReferenceOntologyName(referenceuri);
	}

	@Override
	public String getTermID() {
		return SemGen.semsimlib.getReferenceID(referenceuri);
	}
	
	@Override
	public URI getSemSimClassURI() {
		return SemSimConstants.REFERENCE_PHYSICAL_ENTITY_CLASS_URI;
	}
	
	@Override
	public SemSimTypes getSemSimType() {
		return SemSimTypes.REFERENCE_PHYSICAL_ENTITY;
	}
}
