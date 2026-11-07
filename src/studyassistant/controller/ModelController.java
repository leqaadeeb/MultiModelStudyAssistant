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
import java.util.concurrent.CompletableFuture;
import javax.swing.SwingUtilities;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeoutException;
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
        long startTime = System.nanoTime();
        CompletableFuture<LLMResponse> future = strategy.generate(question);
        future.orTimeout(60, TimeUnit.SECONDS);
        future.thenAccept(response -> {
            SwingUtilities.invokeLater(()->{
                if (requestQueryNumber != queryNumber){
                    return;
                }
                if (response == null || response.getText() == null || response.getText().trim().isEmpty()){
                    long elapsedTimeMs = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - startTime);
                    LLMResponse invalidResponse = new LLMResponse(modelName, null, ResponseStatus.INVALID_RESPONSE, elapsedTimeMs, "The model returns an empty or invalid response.");
                    session.updateResponse(modelName, invalidResponse);
                }
                else {
                    session.updateResponse(modelName, response);
                }
            });
        });
        future.exceptionally(error -> {
            Throwable cause = error;
            while (cause instanceof CompletionException && cause.getCause() != null){
                cause = cause.getCause();
            }

            ResponseStatus status;
            if (cause instanceof TimeoutException){
                status = ResponseStatus.TIMEOUT;
            }
            else {
                status = ResponseStatus.FAILED;
            }
            long elapsedTimeMs = TimeUnit.NANOSECONDS.toMillis(
                System.nanoTime() - startTime);

            LLMResponse failureResponse = new LLMResponse(modelName, null, status, elapsedTimeMs, cause.toString());
            SwingUtilities.invokeLater(()->{
                if (requestQueryNumber != queryNumber){
                    return;
                }
                session.updateResponse(modelName, failureResponse);
            });
            return null;
        });

    }
    


    public JPanel getPanel(){
        return panel;
    }

}
