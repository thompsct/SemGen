package semgen.stage;

import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.DialogParams;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;
import com.teamdev.jxbrowser.chromium.DefaultLoadHandler;

import semgen.GlobalActions;
import semgen.SemGen;
import semgen.SemGenSettings;
import semgen.stage.StageWorkbench.StageEvent;
import semgen.stage.stagetasks.ProjectTask;
import semgen.stage.stagetasks.StageTask.StageTaskEvent;
import semgen.stage.stagetasks.extractor.ModelExtractionGroup;
import semgen.utilities.BrowserLauncher;
import semgen.utilities.SemGenIcon;
import semgen.utilities.uicomponent.SemGenTab;
import semgen.visualizations.SemGenCommunicatingWebBrowser;

import javax.naming.InvalidNameException;
import javax.swing.*;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class StageTab extends SemGenTab implements Observer {
	private static final long serialVersionUID = 1L;
	// Stage workbench
	private StageWorkbench _workbench;
	private SemGenCommunicatingWebBrowser browser;

	public StageTab(SemGenSettings sets, GlobalActions globalacts, StageWorkbench bench) {
		super("Project", SemGenIcon.stageicon, "Interface for facilitating SemGen tasks", sets, globalacts);

		_workbench = bench;
		bench.addObserver(this);
		
	}

	@Override
	public void loadTab() {
		setOpaque(false);
		setLayout(new BorderLayout());
		// Create the browser
		
		try {
			ArrayList<String> browserprefs = new ArrayList<String>();
			if (SemGen.debug) {
				browserprefs.add("--remote-debugging-port=9222"); 
			}

			BrowserPreferences.setChromiumSwitches(browserprefs.toArray(new String[]{}));
			browser = new SemGenCommunicatingWebBrowser();
			
			if (SemGen.debug) {
				String remoteDebuggingURL = browser.getRemoteDebuggingURL(); 
				System.out.println(remoteDebuggingURL);
				BrowserLauncher.openURL(remoteDebuggingURL);
			}
			
			browser.setPostloadinstructions(new WorkbenchInitializer());
			
			final BrowserView browserView = new BrowserView(browser);

			// Show JS alerts in java dialogs
			browser.setDialogHandler(new DefaultDialogHandler(browserView) {
			    @Override
			    public void onAlert(DialogParams params) {
			        String title = "SemGen Browser Alert";
			        String message = params.getMessage();
			        JOptionPane.showMessageDialog(browserView, message, title,
			                JOptionPane.PLAIN_MESSAGE);
			    }
			    
			});

			// Disable Backspace and Shift+Backspace navigation
			browser.setLoadHandler(new DefaultLoadHandler() {
				@Override
				public boolean canNavigateOnBackspace() {
					return false;
				}
			});
			this.add(browserView, BorderLayout.CENTER);

		} catch (InvalidNameException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isModelLoaded() {
		return !_workbench.isProjectEmpty();
	}
	
	@Override
	public boolean isSaved() {
		return true;
	}

	@Override
	public void requestSave() {
		_workbench.saveModel(0);
	}

	@Override
	public void requestSaveAs() {
		_workbench.saveModelAs(0);
	}
	
	@Override
	public void requestExport() {
		_workbench.exportModel(0);
	}
	
	@Override
	public void requestEditModelLevelMetadata() {
	}

	public boolean closeTab() {

		//TODO: freeze visualizations until end of function
		ProjectTask projtask = _workbench.getProjectTask();
		
		if( ! _workbench.isProjectEmpty()){
			
			// Prompt to save any unsaved extractions
			for(int m=0; m<projtask.extractnodeworkbenchmap.size(); m++){
				ModelExtractionGroup meg = projtask.extractnodeworkbenchmap.get(m);
				
				if(meg!=null){
					if(meg.sourcemodelindex!=null){
						int sourcemodelindx = meg.sourcemodelindex.intValue();
						
						for(int j=0; j<meg.extractionnodes.size();j++){
							boolean cancelled = projtask.closeModels(sourcemodelindx,j);
							if(cancelled) return false;
						}
					}
				}
			}
		}
		browser.dispose(); // close the tab
		return true;
	}
	
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg == StageEvent.CHANGETASK) {
			try {
				browser.changeTask(_workbench.getCommandSenderInterface(), _workbench.getCommandReceiver(), _workbench.getActiveStageState());
				_workbench.setCommandSender(browser.getCommandSenderGenerator());
			} catch (InvalidNameException e) {
				e.printStackTrace();
			}
		}
		if (arg == StageTaskEvent.CLOSETASK) {
			try {
				browser.closeTask(_workbench.getActiveTaskIndex());
			} catch (InvalidNameException e) {
				e.printStackTrace();
			}
		}
	}
	
	public class WorkbenchInitializer implements Runnable {
		public void run() {
			_workbench.initialize();
		}
	}
}
