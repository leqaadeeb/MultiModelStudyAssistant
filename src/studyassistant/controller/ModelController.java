package studyassistant.controller;

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

}
