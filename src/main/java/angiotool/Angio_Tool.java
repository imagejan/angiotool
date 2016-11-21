package angiotool;

import ij.plugin.PlugIn;

import angiotool.gui.AngioToolGUI;

/**
 * This is an ImageJ plugin that serves to start AngioTool
 *
 * @author Jan Eglinger
 */
public class Angio_Tool implements PlugIn {
	
	public static AngioToolGUI allantoisGUI;

	 /**
	 * This method gets called by ImageJ / Fiji.
	 *
	 * @param arg can be specified in plugins.config
	 * @see ij.plugin.PlugIn#run(java.lang.String)
	 */
	@Override
	public void run(String arg) {
		allantoisGUI = new AngioToolGUI ();
        	allantoisGUI.setVisible(true);
        	allantoisGUI.setLocation(10,10);
	}
}
