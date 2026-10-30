package studyassistant.model;

/**
 * interface containing one response View for each LLM.
 */

public interface ModelObserver {
	void modelChanged();
}

