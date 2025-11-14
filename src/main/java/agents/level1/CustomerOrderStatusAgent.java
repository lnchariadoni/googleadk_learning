package agents.level1;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.FunctionTool;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.CustomerOrderTools;
import utils.LLMConstants;

public class CustomerOrderStatusAgent {
  public static final BaseAgent ROOT_AGENT = initializeAgent();
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomerOrderStatusAgent.class);

  public static BaseAgent initializeAgent() {
    final String instructions =
//            """
//            You are a helpful order service agent who can answer questions about customer orders. When a user asks for the status of an
//            order, call the `retrieveOrderById` function. Add an engaging and polite touch to your responses.
//            """;
        """
            You are a helpful order service agent who can answer questions about customer orders. When a user asks for the status of an
            order, call the `retrieveOrderById` function. You respond when users specifically asks for order status or order details.
            Do not be overly polite. But curt and precise. Do not engage in any other conversation.         
            """;

    return LlmAgent
        .builder()
        .name("order-status-assistant")
        .description("Query the status of customer orders status.")
        .model(LLMConstants.CURRENT_MODEL)
        .instruction(instructions)
        .tools(FunctionTool.create(CustomerOrderTools.class, "retrieveOrderById"))
        .build();
  }

  public static void main(String[] args) {
    System.out.println("Welcome to the Customer order status agent!");
    InMemoryRunner runner = new InMemoryRunner(ROOT_AGENT);
    Session session = runner
        .sessionService()
        .createSession(ROOT_AGENT.name(), "demo_session_user")
        .blockingGet();

    try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
      while (true) {
        System.out.print("You: ");
        String userInput = scanner.nextLine().trim();

        if (LLMConstants.EXIT_VARIANTS.contains(userInput.toLowerCase())) {
          System.out.println("Goodbye!");
          break;
        }

        if (!userInput.isEmpty()) {
          Content userInputContent = Content.fromParts(Part.fromText(userInput));
          Flowable<Event> events = runner.runAsync(session.userId(), session.id(), userInputContent);

          events.blockingForEach(CustomerOrderStatusAgent::printEvent);
        }
      }
    }
  }

  private static void printEvent(Event event) {
    System.out.println("In CustomerOrderStatusAgent(response)>" + event.stringifyContent());
  }
}
