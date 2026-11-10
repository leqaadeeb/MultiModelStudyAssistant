package studyassistant.controller;
import studyassistant.model.LLMResponse;
import studyassistant.model.ResponseStatus;
import java.awt.BorderLayout;
import studyassistant.model.QuerySession;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.util.List;
import studyassistant.strategy.LLMStrategy;
import java.util.ArrayList;
import javax.swing.JPanel;
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
    private JPanel panel = new JPanel(new BorderLayout());
    private int queryNumber = 0;
    private final ResponseProcessor responseProcessor = new ResponseProcessor();
    


    public ModelController(QuerySession session, List<LLMStrategy> strategies){
        this.session = session;
        this.strategies = strategies;
        questionInput = new JTextArea(3, 40);
        askModelsButton = new JButton("Ask Models");
        askModelsButton.addActionListener(event -> submitQuestion());
        panel.add(questionInput, BorderLayout.CENTER);
        panel.add(askModelsButton, BorderLayout.SOUTH);
    }

    private void submitQuestion(){
        String question = questionInput.getText().trim();

        if (question.isEmpty()){
            JOptionPane.showMessageDialog(null, "Enter a study question: ");
            return;
        }

        queryNumber += 1;
        ArrayList<String> modelNames = new ArrayList<>();

        for(LLMStrategy strategy : strategies){
            modelNames.add(strategy.getDisplayName());
        }

        session.startNewQuery(question, modelNames);

        for(LLMStrategy strategy : strategies){
            requestAnswer(strategy, question, queryNumber);
        }
    }

    private void requestAnswer(LLMStrategy strategy, String question, int requestQueryNumber){
        String modelName = strategy.getDisplayName();
        LLMResponse runningResponse = new LLMResponse(modelName, null, ResponseStatus.RUNNING, 0L, null);
        session.updateResponse(modelName, runningResponse);
        responseProcessor.process(strategy, question, response -> {
            if (requestQueryNumber != queryNumber){
                return;
            }
            session.updateResponse(modelName, response);
        });
    }
    


    public JPanel getPanel(){
        return panel;
    }

}
