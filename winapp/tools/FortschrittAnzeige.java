package winapp.tools;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import sudoku.Paar;

@SuppressWarnings("serial")
public class FortschrittAnzeige extends JDialog implements sudoku.langerprozess.FortschrittAnzeige, ActionListener {
	private JLabel labelTitel;
	private JLabel labelNote;
	private JProgressBar progressBar;
	private JButton buttonCancel;
	private boolean istAbbruch;

	public FortschrittAnzeige(JFrame elternFenster) {
		super(elternFenster, "Fortschritt", true);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		this.istAbbruch = false;

		this.setLayout(new GridLayout(0, 1));

		labelTitel = new JLabel("Fortschritt...");
		labelTitel.setHorizontalAlignment(SwingConstants.CENTER);
		labelTitel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.add(labelTitel);

		labelNote = new JLabel("Working..."); // this is just a label in which
												// you can indicate the state of
												// the procesing
		labelNote.setHorizontalAlignment(SwingConstants.CENTER);
		labelNote.setOpaque(true);
		labelNote.setBackground(Color.WHITE);
		this.add(labelNote);

		progressBar = new JProgressBar(0, 100);
		this.add(progressBar);

		buttonCancel = new JButton("Abbrechen");
		buttonCancel.addActionListener(this);
		int hoehe1 = buttonCancel.getPreferredSize().height;

		JPanel buttonPane = new JPanel();
		buttonPane.add(buttonCancel);

		this.add(buttonPane);

		this.setUndecorated(true);
		// this.getRootPane().setBorder(BorderFactory.createRaisedBevelBorder());
		this.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLUE.brighter(), hoehe1 / 3, true));

		this.setLocation(getParent().getLocation().x, getParent().getSize().height / 2);
		this.setSize(getParent().getWidth(), hoehe1 * 5);
	}

	public void actionPerformed(ActionEvent e) {
		buttonCancel.setEnabled(false);
		this.istAbbruch = true;
	}

	public void starten(String titel, Paar<Integer, Integer> fortschrittBereich) {
		this.labelTitel.setText(titel);

		if (fortschrittBereich == null) {
			this.progressBar.setIndeterminate(true);
		} else {
			this.progressBar.setMinimum(fortschrittBereich.getKey());
			this.progressBar.setMaximum(fortschrittBereich.getValue());
		}
		this.progressBar.setValue(0);

		// Das soll hier möglich sein:
		// setModal(boolean);
		this.setVisible(true);
	}

	@Override
	public void beenden() {
		this.dispose();
	}

	@Override
	public boolean istAbbruchGefordert() {
		return this.istAbbruch;
	}

	@Override
	public void zeigeFortschritt(int zustand) {
		this.progressBar.setValue(zustand);
	}

	@Override
	public void zeigeInfo(String mitteilung) {
		this.labelNote.setText(mitteilung);
	}

	@Override
	protected void processEvent(AWTEvent e) {
		// System.out.println(e);
		super.processEvent(e);
	}

}
