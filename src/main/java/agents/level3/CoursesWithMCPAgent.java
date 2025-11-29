package agents.level3;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.BaseToolset;
import com.google.adk.tools.mcp.McpToolset;
import com.google.adk.tools.mcp.SseServerParameters;
import com.google.adk.tools.mcp.StreamableHttpServerParameters;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.LLMConstants;

public class CoursesWithMCPAgent {
  public static final BaseAgent ROOT_AGENT = initializeRootAgent();
  public static BaseAgent initializeRootAgent() {
    final String instructions = """
        You are a helpful assistant to help recommend courses.
        When users asks a question of knowing on available courses, call this tool `get_all_courses`
        When user wants to know more about a particular course, call this tool `get_course_by_id` with `id` parameter.
        Also when you respond, provide brief context on what tools did you use to get the information. So that there is transparency in your responses.
        """;

    String mcpEndPoint = "http://localhost:8080/mcp";
    BaseToolset mcpToolset;

    StreamableHttpServerParameters coursesHttpMcpParams = StreamableHttpServerParameters
        .builder(mcpEndPoint)
        .timeout(Duration.ofSeconds(60))
        .build();
    mcpToolset = new McpToolset(coursesHttpMcpParams);

    /* This is for SSE based MCP endpoint. Do not use it as it is deprecated.
    SseServerParameters sseServerParameters = SseServerParameters.builder().url(mcpEndPoint).build();
    mcpToolset = new McpToolset(sseServerParameters);
     */

    return LlmAgent.builder()
        .name("courses-with-mcp-agent")
        .description("A course recommendation agent that leverages MCP tools.")
        .model(LLMConstants.CURRENT_MODEL)
        .instruction(instructions)
        .tools(List.of(mcpToolset))
        .build();
  }

  public static void main(String[] args) {
    Logger logger = LoggerFactory.getLogger(CoursesWithMCPAgent.class);

    logger.info("A course recommendation agent initialized successfully.");

    Runtime.getRuntime().addShutdownHook(new Thread(() -> logger.info("Shutting down CoursesWithMCPAgent...")));

    InMemoryRunner runner = new InMemoryRunner(CoursesWithMCPAgent.ROOT_AGENT, "courses_with_mcp_app");

    Session session = runner.sessionService()
        .createSession(runner.appName(), "course_user")
        .blockingGet();

    String inroduction = "Hello. Please introduce yourself";
    runner.runAsync(session.userId(),
            session.id(),
            Content.fromParts(Part.fromText(inroduction)))
        .blockingForEach(event -> System.out.println(event.stringifyContent()));

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
                System.out.println("In CoursesWithMcpAgent(response)>" + event.stringifyContent()));
      }
    }
  }
}
