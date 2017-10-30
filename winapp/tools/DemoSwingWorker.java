package winapp.tools;

public class DemoSwingWorker {

	public DemoSwingWorker() {
		// TODO Auto-generated constructor stub
	}
	// JDialog dlgProgress = new JDialog(null, "Please wait...", true);//true means that the dialog created is modal
	// JLabel lblStatus = new JLabel("Working..."); // this is just a label in which you can indicate the state of the processing
	//
	// JProgressBar pbProgress = new JProgressBar(0, 100);
	// pbProgress.setIndeterminate(true); //we'll use an indeterminate progress bar
	//
	// dlgProgress.add(BorderLayout.NORTH, lblStatus);
	// dlgProgress.add(BorderLayout.CENTER, dpb);
	// dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // prevent the user from closing the dialog
	// dlgProgress.setSize(300, 90);
	//
	// SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
	// @Override
	// protected Void doInBackground() throws Exception {
	// longProcessingTask();
	// return null;
	// }
	//
	// @Override
	// protected void done() {
	// dlgProgress.dispose();//close the modal dialog
	// }
	// };
	//
	// sw.execute(); // this will start the processing on a separate thread
	// dlgProgress.setVisible(true); //this will block user input as long as the processing task is working
}
