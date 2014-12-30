package semsim.model.physical;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public abstract class PhysicalProcess extends PhysicalModelComponent{
	private LinkedHashMap<PhysicalEntity, Integer> sources = new LinkedHashMap<PhysicalEntity, Integer>();
	private LinkedHashMap<PhysicalEntity, Integer> sinks = new LinkedHashMap<PhysicalEntity, Integer>();
	private LinkedHashMap<PhysicalEntity, Integer> mediators = new LinkedHashMap<PhysicalEntity, Integer>();	
	
	public void addSource(PhysicalEntity entity, Integer stoichiometry){
		sources.put(entity, stoichiometry);
	}
	
	public void addSink(PhysicalEntity entity, Integer stoichiometry){
		sinks.put(entity, stoichiometry);
	}
	
	public void addMediator(PhysicalEntity entity, Integer stoichiometry){
		mediators.put(entity, stoichiometry);
	}
	
	public Set<PhysicalEntity> getSourcePhysicalEntities(){
		Set<PhysicalEntity> ents = new HashSet<PhysicalEntity>();
		for(PhysicalEntity sp : getSources().keySet()){
			ents.add(sp);
		}
		return ents;
	}
	
	public Set<PhysicalEntity> getSinkPhysicalEntities(){
		Set<PhysicalEntity> ents = new HashSet<PhysicalEntity>();
		for(PhysicalEntity sp : getSinks().keySet()){
			ents.add(sp);
		}
		return ents;
	}
	
	public Set<PhysicalEntity> getMediatorPhysicalEntities(){
		Set<PhysicalEntity> ents = new HashSet<PhysicalEntity>();
		for(PhysicalEntity sp : getMediators().keySet()){
			ents.add(sp);
		}
		return ents;
	}

	public void setSources(LinkedHashMap<PhysicalEntity, Integer> sources) {
		this.sources = sources;
	}

	public LinkedHashMap<PhysicalEntity, Integer> getSources() {
		return sources;
	}

	public void setSinks(LinkedHashMap<PhysicalEntity, Integer> sinks) {
		this.sinks = sinks;
	}

	public LinkedHashMap<PhysicalEntity, Integer> getSinks() {
		return sinks;
	}

	public void setMediators(LinkedHashMap<PhysicalEntity, Integer> mediators) {
		this.mediators = mediators;
	}

	public LinkedHashMap<PhysicalEntity, Integer> getMediators() {
		return mediators;
	}
	
	public Integer getStoichiometry(PhysicalEntity entity) {
		return getParticipants().get(entity);
	}
	
	public Integer getSourceStoichiometry(PhysicalEntity entity) {
		return sources.get(entity);
	}
	
	public Integer getSinkStoichiometry(PhysicalEntity entity) {
		return sinks.get(entity);
	}
	
	public Integer getMediatorStoichiometry(PhysicalEntity entity) {
		return mediators.get(entity);
	}
	
	public void setStoichiometry(PhysicalEntity entity, Integer stoich) {
		getParticipants().put(entity,stoich);
	}
	
	public LinkedHashMap<PhysicalEntity, Integer>getParticipants(){
		LinkedHashMap<PhysicalEntity, Integer> allps = new LinkedHashMap<PhysicalEntity, Integer>();
		allps.putAll(getSources());
		allps.putAll(getSinks());
		allps.putAll(getMediators());
		return allps;
	}
	
	// Get all sources, sinks and mediators as PhysicalEntities
	public Set<PhysicalEntity> getParticipantsAsPhysicalEntities(){
		Set<PhysicalEntity> allpents = new HashSet<PhysicalEntity>();
		allpents.addAll(getSourcePhysicalEntities());
		allpents.addAll(getSinkPhysicalEntities());
		allpents.addAll(getMediatorPhysicalEntities());
		return allpents;
	}

	@Override
	public String getComponentTypeasString() {
		return "process";
	}
}
