package studyassistant.model;

import studyassistant.model.ResponseStatus;

/**
 * Represents a single LLM's response state in the MVC model.
 *
 * Interactions:
 * QuerySession owns a map of model names to LLMResponse. Only QuerySession mutators
 * (setResponse, updateResponse, initializeResponses, clearResponses) may change observable
 * state and must call notifyObservers() on changes.
 * 
 * The Controller creates or replaces LLMResponse instances (transitions WAITING -> RUNNING
 * -> SUCCESS/FAILED/TIMEOUT/INVALID_RESPONSE) and passes them to QuerySession.updateResponse.
 * The Controller must not refresh Views directly.
 * 
 * LLM Strategy classes produce results but must not manipulate Swing components or Views;
 * they report results to the Controller which updates the Model.
 * 
 * ModelObserver and View classes (e.g. LLMResponseView) are notified by the Model and refresh
 * based on updated state. Partial results appear because each update goes through the Model.
 * 
 * ResponseStatus defines valid states: WAITING, RUNNING, SUCCESS, FAILED, TIMEOUT, INVALID_RESPONSE.
 * 
 * This class is a data holder (POJO). It has no observer notification logic; all notifications
 * are enforced by QuerySession.
 */
public class LLMResponse {
	private String modelName;
	private String text;
	private ResponseStatus status;
	private long responseTimeMs;
	private String errorMessage;

	/**
	 * Creates a new response in WAITING state for the given model.
	 *
	 * @param modelName the name of the LLM this response belongs to
	 */
	public LLMResponse(String modelName) {
		this.modelName = modelName;
		this.text = null;
		this.status = ResponseStatus.WAITING;
		this.responseTimeMs = 0L;
		this.errorMessage = null;
	}

	/**
	 * Creates a response with explicit values.
	 *
	 * @param modelName      the name of the LLM this response belongs to
	 * @param text           the response text (may be null for non-SUCCESS states)
	 * @param status         the execution status
	 * @param responseTimeMs elapsed time in milliseconds
	 * @param errorMessage   error details (may be null for non-failure states)
	 */
	public LLMResponse(String modelName, String text, ResponseStatus status, long responseTimeMs, String errorMessage) {
		this.modelName = modelName;
		this.text = text;
		this.status = status;
		this.responseTimeMs = responseTimeMs;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the model name this response belongs to
	 */
	public String getModelName() {
		return modelName;
	}

	/**
	 * @return the response text, or null if not available
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the current execution status
	 */
	public ResponseStatus getStatus() {
		return status;
	}

	/**
	 * @return elapsed time in milliseconds
	 */
	public long getResponseTimeMs() {
		return responseTimeMs;
	}

	/**
	 * @return error details, or null if there is no error
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param modelName the model name this response belongs to
	 */
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	/**
	 * @param text the response text
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @param status the execution status
	 */
	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	/**
	 * @param responseTimeMs elapsed time in milliseconds
	 */
	public void setResponseTimeMs(long responseTimeMs) {
		this.responseTimeMs = responseTimeMs;
	}

	/**
	 * @param errorMessage error details
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "LLMResponse{" +
				"modelName='" + modelName + '\'' +
				", status=" + status +
				", responseTimeMs=" + responseTimeMs +
				'}';
	}
}
