package studyassistant;

import studyassistant.client.*;
import studyassistant.model.*;
import studyassistant.strategy.*;
import studyassistant.view.*;
import studyassistant.controller.*;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StudyAssistantApp {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			QuerySession session = new QuerySession();

			OllamaClient client = new OllamaClient();

			LlamaStrategy llama = new LlamaStrategy(client);
			GemmaStrategy gemma = new GemmaStrategy(client);
			QwenStrategy qwen = new QwenStrategy(client);

			List<LLMStrategy> strategies = new ArrayList<>();
			strategies.add(llama);
			strategies.add(gemma);
			strategies.add(qwen);

			List<String> modelNames = new ArrayList<>();

			for(LLMStrategy str : strategies) {
				modelNames.add(str.getDisplayName());
			}
			session.initializeResponses(modelNames);


			LLMResponseView llamaView = new LLMResponseView(llama.getDisplayName(), session);
			LLMResponseView gemmaView = new LLMResponseView(gemma.getDisplayName(), session);
			LLMResponseView qwenView = new LLMResponseView(qwen.getDisplayName(), session);




			ModelController controller = new ModelController(session, strategies);

			JFrame frame = new JFrame("Multi-Model Study Assistant");

			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			frame.add(controller.getPanel(), BorderLayout.NORTH);
			JPanel responsePanel = new JPanel(new GridLayout(1, 3, 10, 0));

			responsePanel.add(llamaView);
			responsePanel.add(gemmaView);
			responsePanel.add(qwenView);

			frame.add(responsePanel, BorderLayout.CENTER);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);


		});

	}
}
