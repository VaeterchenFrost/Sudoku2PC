package winapp.tip;

import java.awt.Color;
import java.awt.SystemColor;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
class TipTextPanel extends JPanel {

	private TipTextArea textArea;

	TipTextPanel(String text) {
		super();
		this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		textArea = new TipTextArea(text, 0);
		this.add(textArea);
		// setzeHintergrund(SystemColor.window);
		setzeHintergrund(SystemColor.control);
	}

	void setzeHintergrund(Color color) {
		this.setBackground(color);
		textArea.setBackground(color);
	}

	void zoomText(int delta) {
		textArea.zoomText(delta);
	}

	boolean istZoomTextMoeglich(int richtung) {
		return textArea.istZoomTextMoeglich(richtung, this.getWidth());
	}

	void setzeFontGroesse() {
		textArea.setzeFontGroesse();
	}
}
