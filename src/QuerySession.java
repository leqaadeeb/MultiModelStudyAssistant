import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
		notifyObservers();
	}

	public Map<String, LLMResponse> getResponses() {
		return responses;
	}

	public LLMResponse getResponse(String modelName) {
		if (modelName == null) {
			return null;
		}
		return responses.get(modelName);
	}

	public void setResponse(String modelName, LLMResponse response) {
		if (modelName == null) {
			return;
		}
		responses.put(modelName, response);
		notifyObservers();
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
