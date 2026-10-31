package studyassistant.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import studyassistant.model.LLMResponse;
import studyassistant.model.ModelObserver;
import studyassistant.model.ResponseStatus;

/**
 * model for representing the current user interaction
 */
public class QuerySession {
	private String question;
	private Map<String, LLMResponse> responses;
	private final List<ModelObserver> observers;

	public QuerySession() {
		this.question = null;
		this.responses = new HashMap<String, LLMResponse>();
		this.observers = new ArrayList<ModelObserver>();
	}

	public QuerySession(String question) {
		this.question = question;
		this.responses = new HashMap<String, LLMResponse>();
		this.observers = new ArrayList<ModelObserver>();
	}


	/**
	 * Start a new query.
	 * Uses fresh WAITING responses.
	 * After running, notifies observers.
	 *
	 * @param question The question the user has.
	 * @param modelNames The names of models the user wants to use.
	 */
	public void startNewQuery(String question, List<String> modelNames){
		// First, validate the parameters
		if(question == null || question.trim().isEmpty()){
			throw new IllegalArgumentException("Question cannot be blank");
		}
		if(modelNames == null || modelNames.isEmpty()){
			throw new IllegalArgumentException("At least one model is required");
		}

		for (String modelName : modelNames){
			if(modelName == null || modelName.trim().isEmpty()){
				throw new IllegalArgumentException("Model names cannot be blank");
			}
		}


		this.question = question.trim();
		responses.clear();

		for (String modelName : modelNames) {
			responses.put(modelName, new LLMResponse(modelName));
		}

		notifyObservers();
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
		notifyObservers();
	}

	public Map<String, LLMResponse> getResponses() {
		return Collections.unmodifiableMap(responses);
	}

	public LLMResponse getResponse(String modelName) {
		if (modelName == null) {
			return null;
		}
		return responses.get(modelName);
	}

	public void setResponse(String modelName, LLMResponse response) {
		updateResponse(modelName, response);
	}

	public void updateResponse(String modelName, LLMResponse response) {
		if (modelName == null) {
			return;
		}
		responses.put(modelName, response);
		notifyObservers();
	}

	public void initializeResponses(List<String> modelNames) {
		if (modelNames == null) {
			return;
		}
		for (String modelName : modelNames) {
			if (modelName != null && !responses.containsKey(modelName)) {
				responses.put(modelName, new LLMResponse(modelName, null, ResponseStatus.WAITING, 0, null));
			}
		}
		notifyObservers();
	}

	public void clearResponses() {
		responses.clear();
		notifyObservers();
	}

	public void addObserver(ModelObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void removeObserver(ModelObserver observer) {
		if (observer != null) {
			observers.remove(observer);
		}
	}

	private void notifyObservers() {
		for (ModelObserver observer : new ArrayList<ModelObserver>(observers)) {
			if (observer != null) {
				observer.modelChanged();
			}
		}
	}
}
