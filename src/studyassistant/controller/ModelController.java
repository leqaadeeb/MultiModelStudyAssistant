package studyassistant.controller;

import java.awt.BorderLayout;
import studyassistant.model.QuerySession;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.util.List;
import studyassistant.strategy.LLMStrategy;
import java.util.ArrayList;
/**
 *  Controller contains the user-input GUI components, such as the question-entry area and the Ask Models button, together with their event listeners.
 *  The Controller manages user input and coordinates each user-initiated LLM request. 
 *  When the user selects Ask Models, the Controller must: 
 *  obtain and validate the question;
 *  call a Model mutator to begin a new query and establish the initial response state;
 *  invoke each LLMStrategy;
 *  allow the requests to execute concurrently;
 *  process each completed result independently; and
 *  pass each response or failure to the Model through a Model mutator.
 */

// @TODO
public class ModelController {
    private QuerySession session;
    private JTextArea questionInput;
    private JButton askModelsButton;
    private List<LLMStrategy> strategies; 


    public ModelController(QuerySession session, List<LLMStrategy> strategies){
        this.session = session;
        this.strategies = strategies;
        questionInput = new JTextArea(3, 40);
        askModelsButton = new JButton("Ask Models");
        askModelsButton.addActionListener(event -> submitQuestion());
    }

    private void submitQuestion(){
        String question = questionInput.getText().trim();

        if (question.isEmpty()){
            JOptionPane.showMessageDialog(null, "Enter a study question: ");
            return;
        }

        ArrayList<String> modelNames = new ArrayList<>();

        for(LLMStrategy strategy : strategies){
            modelNames.add(strategy.getDisplayName());
        }

        session.startNewQuery(question, modelNames);

        for(LLMStrategy strategy : strategies){
            requestAnswer(strategy, question);
        }
    }

    private void requestAnswer(LLMStrategy strategy, String question){
        
    }

}
