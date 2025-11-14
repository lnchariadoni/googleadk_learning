package agents.level2;

import agents.level0.ScienceTeacherAgent;
import agents.level1.CustomerOrderStatusAgent;
import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.AgentTool;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import utils.LLMConstants;

public class AggregateRootAgent {
  public static final BaseAgent ROOT_AGENT = initializeRootAgent();
  public static BaseAgent initializeRootAgent() {
    final String instructions = """
        You are a helpful assistant that combines the capabilities of multiple agents to provide comprehensive responses to user queries.
        When a user asks a question, determine which of your tools (agents) is best suited to answer the question.
        Use the Customer Order Status Agent for questions related to customer orders and their statuses.
        Use the Science Teacher Agent for questions related to science topics, explanations, and educational content.
        Always respond using the appropriate tool for the user's query.
        """;

    return LlmAgent.builder()
        .name("aggregate-root-agent")
        .description("An aggregate root agent that combines multiple agents.")
        .model(LLMConstants.CURRENT_MODEL)
        .instruction(instructions)
        .tools(
            AgentTool.create(CustomerOrderStatusAgent.ROOT_AGENT),
            AgentTool.create(ScienceTeacherAgent.ROOT_AGENT))
        .build();
  }

  public static void main(String[] args) {
    System.out.println("Aggregate Root Agent initialized successfully.");
    InMemoryRunner runner = new InMemoryRunner(AggregateRootAgent.ROOT_AGENT, "aggregate_root_app");

    Session session = runner.sessionService()
        .createSession(runner.appName(), "demo_session_user")
        .blockingGet();

    try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
      while (true) {
        System.out.print("You: ");
        String userInput = scanner.nextLine().trim();

        if (LLMConstants.EXIT_VARIANTS.contains(userInput.toLowerCase())) {
          System.out.println("Goodbye!");
          break;
        }

        Content userInputContent = Content.fromParts(Part.fromText(userInput));
        Flowable<Event> events = runner.runAsync(session.userId(), session.id(), userInputContent);

        events
            .blockingForEach(event ->
                System.out.println("In AggregateRootAgent(response)>" + event.stringifyContent()));
      }
    }
  }
}
