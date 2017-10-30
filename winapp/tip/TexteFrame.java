package winapp.tip;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.SystemColor;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import winapp.tools.MaximaleFensterGroesse;

@SuppressWarnings("serial")
public class TexteFrame extends JFrame { // implements WindowStateListener{
	private TipTextArea textArea;

	TexteFrame(int anzahlTextZeilen, String text, int anhebung) {
		super("Sudoku Tip");
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		this.textArea = new TipTextArea(text, anzahlTextZeilen);
		textArea.setBackground(SystemColor.control);

		JScrollPane scrollingArea = new JScrollPane(textArea);

		// ... Get the content pane, set layout, add to center
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.add(scrollingArea, BorderLayout.CENTER);

		// ... Set window characteristics.
		this.setContentPane(content);

		pack();

		Rectangle rMax = MaximaleFensterGroesse.gibMaxGroesse();
		Dimension dMeine = this.getSize();
		if (dMeine.width > rMax.width / 2) {
			dMeine.width = rMax.width / 2;
		}
		if (dMeine.height > rMax.height / 2) {
			dMeine.height = rMax.height / 2;
		}
		this.setSize(dMeine);
		this.setMinimumSize(dMeine);
		this.setLocation(rMax.width - dMeine.width, rMax.height - dMeine.height - anhebung);
		setAlwaysOnTop(true);
		setVisible(true);
	}

	void zoomText(int delta) {
		textArea.zoomText(delta);
	}

	boolean istZoomTextMoeglich(int richtung) {
		if (richtung < 0) {
			return textArea.istZoomTextMoeglich(richtung, this.getWidth());
		} else {
			return true;
		}
	}

	void zeigen() {
		this.setExtendedState(JFrame.NORMAL);
		setVisible(true);
	}
}
