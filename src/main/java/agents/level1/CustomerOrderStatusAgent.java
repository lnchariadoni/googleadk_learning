package agents.level1;

import agents.level0.ScienceTeacherAgent;
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
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.CustomerOrderTools;

// TODO remove this
/*
How to run:
    export GOOGLE_API_KEY=YOUR_KEY

    mvn clean compile exec:java -DmainClass="agents.level1.CustomerOrderStatusAgent"

    mvn clean compile exec:java \
    -Dexec.mainClass="com.google.adk.web.AdkWebServer" \
    -Dexec.args="--adk.agents.source-dir=target --server.port=8000"

References:
- https://www.youtube.com/watch?v=VM3b3csBeUc&list=PLWVjTNKbh-LmnsxminYNE5UM0eKH4oy_c
- https://www.youtube.com/watch?v=44C8u0CDtSo
- https://www.youtube.com/watch?v=P4VFL9nIaIA
 */

public class CustomerOrderStatusAgent {
  private static final Logger logger = LoggerFactory.getLogger(ScienceTeacherAgent.class);

  public static final String LLM_MODEL_GEMINI_2_5_PRO= "gemini-2.5-pro";
  public static final String LLM_MODEL_GEMINI_2_5_FLASH = "gemini-2.5-flash"; // working and tested
  public static final String LLM_MODEL_GEMINI_2_5_FLASH_LITE = "gemini-2.5-flash-lite"; // working and tested
  public static final String LLM_MODEL_GEMINI_2_0_FLASH = "gemini-2.0-flash";
  public static final String LLM_MODEL_GEMINI_2_0_FLASH_LITE = "gemini-2.0-flash-lite";
  public static final String LLM_MODEL_GEMINI_1_5_FLASH = "gemini-1.5-flash";

  public static final List<String> EXIT_VARIANTS = List.of("exit", "quit", "bye", "goodbye");

  public static final BaseAgent ROOT_AGENT = initializeAgent();

  public static BaseAgent initializeAgent() {
    String instructions =
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
        .model(LLM_MODEL_GEMINI_2_5_FLASH_LITE)
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

    try(Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
      while (true) {
        System.out.print("You: ");
        String userInput = scanner.nextLine().trim();

        if (EXIT_VARIANTS.contains(userInput.toLowerCase())) {
          System.out.println("Goodbye!");
          break;
        }

        if(!userInput.isEmpty()) {
          Content userInputContent = Content.fromParts(Part.fromText(userInput));
          Flowable<Event> events =
              runner.runAsync(session.userId(), session.id(), userInputContent);

          events.blockingForEach(CustomerOrderStatusAgent::printEvent);
        }
      }
    }
  }

  private static void printEvent(Event event) {
    System.out.println(event.stringifyContent());
  }
}
