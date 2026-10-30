package studyassistant.view;

import studyassistant.model.ModelObserver;

/**
 * a single reusable response-view class and instantiate one View object per LLM, parameterized by the LLM name.
 */

public class LLMResponseView implements ModelObserver {
	private final String modelName;
	@Override
	public void modelChanged() {
    	// read the current response for modelName
    	// from the shared Model and refresh this view
	}
}
