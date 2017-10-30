package winapp.toolbar;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;

import javax.swing.JFrame;

import sudoku.kern.info.InfoSudoku;

@SuppressWarnings("serial")
public class DateiDialog extends FileDialog {

	public class DateiFilter implements FilenameFilter {
		@Override
		public boolean accept(File file, String name) {
			if (name == null) {
				return false;
			}
			if (name.isEmpty()) {
				return false;
			}
			if (!name.endsWith(InfoSudoku.dateiErweiterung)) {
				return false;
			}

			if (!file.exists()) {
				return false;
			}
			if (!file.isFile()) {
				return false;
			}
			if (!file.canRead()) {
				return false;
			}

			return false;
		}
	}

	public DateiDialog(JFrame frame, boolean istSpeichern) {
		super(frame, istSpeichern ? "Sudoku speichern" : "Sudoku laden",
				istSpeichern ? FileDialog.SAVE : FileDialog.LOAD);

		this.setDirectory("C:\\Benutzer");
		this.setFilenameFilter(new DateiFilter());
	}

}
