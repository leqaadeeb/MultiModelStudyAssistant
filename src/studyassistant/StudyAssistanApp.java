package studyassistant;

import studyassistant.strategy.QwenStrategy;

import javax.swing.*;

public class StudyAssistanApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {})
		ModelControler controler = new ModelControler(session, strategies);
		controler.getPanel();

	}
}
