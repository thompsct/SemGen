package semgen.stage.stagetasks;

import java.util.ArrayList;
import semgen.stage.serialization.DependencyNode;
import semgen.stage.serialization.ExtractionNode;
import semgen.stage.serialization.ModelNode;
import semgen.stage.serialization.PhysioMapNode;
import semgen.stage.serialization.SearchResultSet;
import semgen.stage.serialization.SubModelNode;

public interface ProjectWebBrowserCommandSender extends SemGenWebBrowserCommandSender{
	
	/**
	 * Sends the add model command
	 * @param modelName The {@link ModelNode} to add
	 */
	void addModel(ModelNode modelName);
	
	
	void removeModel(Integer[] modelindex);
	/**
	 * Tell the browser to render the dependencies
	 * 
	 * @param modelName Name of model
	 * @param jsonDependencies Dependencies
	 */
	void showDependencyNetwork(String modelName, DependencyNode[] jsonDependencies);
	
	/**
	 * Tell the browser to render the submodel dependencies
	 * 
	 * @param modelName name of parent model
	 * @param jsonSubmodelNetwork submodel network
	 */
	void showSubmodelNetwork(String modelName, SubModelNode[] jsonSubmodelNetwork);

	/**
	 * Sends search results from multiple sources to JavaScript
	 * @param resultSets Array containing search results from multiple sources
	 */
	void search(SearchResultSet[] resultSets);

	/**
	 * Retrieves model abstract based on Pubmed ID
	 * @param bioModelAbstract String of the model abstract from Pubmed
	 */
	void showModelAbstract(String bioModelAbstract);

	/**
	 * Tell the browser to render the PhysioMap
	 * 
	 * @param modelName name of model
	 * @param jsonPhysioMap PhysioMap
	 */
	void showPhysioMapNetwork(String modelName, PhysioMapNode[] jsonPhysioMap);

	void newExtraction(Integer sourceindex, ExtractionNode newextraction);
			
	void loadExtractions(ArrayList<ArrayList<ExtractionNode>> extractions);

	void modifyExtraction(Integer infoindex, Integer extractionindex, ExtractionNode extraction);

}
