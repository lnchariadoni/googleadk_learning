package agents.level0;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.LLMConstants;

public class ScienceTeacherAgent {
  public static final BaseAgent ROOT_AGENT = initializeAgent();
  private static final Logger LOGGER = LoggerFactory.getLogger(ScienceTeacherAgent.class);

  public static BaseAgent initializeAgent() {
    final String instruction = """
        You are a helpful science teacher that explains science concepts to kids and teenagers in simple words and in an engaging fashion.
        Anything other than science related questions should be politely declined. Be crisp and to the point in your answers.
        """;

    return LlmAgent
        .builder()
        .name("science-teacher-assistant")
        .description("scientific research assistant for teachers")
        .model(LLMConstants.CURRENT_MODEL)
        .instruction(instruction)
        .build();
  }

  public static void main(String[] args) {
    System.out.println("Welcome to the Science Teacher Assistant!");
    InMemoryRunner runner = new InMemoryRunner(ROOT_AGENT);
    Session session = runner.sessionService()
        .createSession(runner.appName(), "demo_session_user")
        .blockingGet();

    try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
      while (true) {
        System.out.print("You: ");
        String userInput = scanner.nextLine().trim();

        if (userInput.isEmpty()) {
          System.out.println("Please enter a valid question or type 'exit' or 'quit' to quit.");
          continue;
        }

        // both works
        if (LLMConstants.EXIT_VARIANTS.contains(userInput)) {
          System.out.println("Exiting the Science Teacher Assistant. Goodbye!");
          break;
        }

        Content userInputContent = Content.fromParts(Part.fromText(userInput));
        Flowable<Event> events = runner.runAsync(session.userId(), session.id(), userInputContent);

        events.blockingForEach(ScienceTeacherAgent::printEvent);
      }
    }
  }

  private static void printEvent(Event event) {
    System.out.println("In ScienceTeacherAgent(response)>" + event.stringifyContent());
  }
}
