package studyassistant.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.*;
import studyassistant.model.LLMResponse;
import studyassistant.model.ModelObserver;
import studyassistant.model.QuerySession;
import studyassistant.model.ResponseStatus;

/**
 * a single reusable response-view class and instantiate one View object per LLM, parameterized by the LLM name.
 */

public class LLMResponseView extends JPanel implements ModelObserver {
	
	private final String modelName;
	private final QuerySession model;
	private JLabel modelLabel;
	private JLabel statusAndTime;
	private JTextArea responseArea;
	
	/**
	 * Creates and constructs a response view for the specified LLM and registers it with the model as an observer.
	 * 
	 * @param modelName the name of the LLM associated with this view
	 * @param model the shared QuerySession model
	 */
	public LLMResponseView(String modelName, QuerySession model)
	{
		this.modelName = modelName;
		this.model = model;
		
		model.addObserver(this); //to register the view to the model
		
		setLayout(new BorderLayout());
		
		modelLabel = new JLabel(modelName);
		statusAndTime = new JLabel("Waiting");
		
		responseArea = new JTextArea(8,25);
		responseArea.setEditable(false);
		responseArea.setLineWrap(true);
		responseArea.setWrapStyleWord(true);
		responseArea.setMargin(new Insets(4, 6, 4, 6));
		
		JPanel panel = new JPanel(new GridLayout(2,1));
		panel.add(modelLabel);
		panel.add(statusAndTime);
		
		JScrollPane scroll = new JScrollPane(responseArea);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		add(panel, BorderLayout.NORTH);
		add(scroll, BorderLayout.CENTER);
	}
	
	/**
	 * Updates this view when the model notifies its observers.
	 * gets the response associated with the model name and refresh the status, time, and response text area.
	 */
	@Override
	public void modelChanged() {
    	// read the current response for modelName
    	// from the shared Model and refresh this view
		
		LLMResponse response = model.getResponse(modelName);
	
		statusAndTime.setText(response.getStatus().toString());
		
		if(response.getStatus() == ResponseStatus.SUCCESS)
		{
			statusAndTime.setText(response.getStatus() + " - " + response.getResponseTimeMs() + " ms");
		}
		
		responseArea.setText(response.getText());
		responseArea.setCaretPosition(0);
	}
}
