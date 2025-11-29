package agents.level4;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.adk.tools.BaseToolset;
import com.google.adk.tools.mcp.McpToolset;
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

public class DVDRentalsAnalysisAgentUsingMCP {
  public static final Logger LOGGER = LoggerFactory.getLogger(DVDRentalsAnalysisAgentUsingMCP.class);
  public static final BaseAgent ROOT_AGENT = initializeRootAgent();

  public static BaseAgent initializeRootAgent() {
    final String instructions = """
        You are a helpful assistant to help users analyze and get information from a DVD Rentals database.
        Also when you respond, provide brief context on what tools did you use to get the information. So that there is transparency in your responses.
        Use your analytical skills to analyze and provide insights on the DVD Rentals database as per user requests.
        You make use of all the tools available to you to provide answers to user queries.
        You make use of your data analysis skills, data science abilities to provide insights on the DVD Rentals database.
        """;
//        The following tools are available to you:
//        `get_all_actors` - to get the list of all actors.
//        `get_actor_by_id` with `id` parameter.
//        When user wants to search for an actor by name, call this tool `get_actor_by_name` with `name` parameter.
//        When user wants to search for multiple actors by names, call this tool `get_actor_by_names` with `names` parameter. `names` is a list of strings.
//        When users asks a question of knowing on available movies, call this tool `get_all_movies`
//        When user wants to know more about a particular movie, call this tool `get_movie_by_id` with `id` parameter.
//        When user wants to search for movies by title, call this tool `get_movie_by_search_title` with `searchTerm` parameter.
//        When user wants to search for movies by multiple title words, call this tool `get_movie_by_multiple_search_title` with `searchTerms` parameter. `searchTerms` is a list of strings.

//        """;
//        You make use of your existing information but not knowledge to provide answers to user queries.
//        """;

    String mcpEndPoint = "http://localhost:8080/mcp";
    BaseToolset mcpToolset;

    StreamableHttpServerParameters dvdRentalsHttpMcpParams = StreamableHttpServerParameters
        .builder(mcpEndPoint)
        .timeout(Duration.ofSeconds(60))
        .build();
    mcpToolset = new McpToolset(dvdRentalsHttpMcpParams);

    /* This is for SSE based MCP endpoint. Do not use it as it is deprecated.
    SseServerParameters sseServerParameters = SseServerParameters.builder().url(mcpEndPoint).build();
    mcpToolset = new McpToolset(sseServerParameters);
     */

    return LlmAgent.builder()
        .name("dvd-rentals-analysis-agent")
        .description("A DVD rental analysis agent that leverages MCP tools.")
        .model(LLMConstants.CURRENT_MODEL)
        .instruction(instructions)
        .tools(List.of(mcpToolset))
        .build();
  }

  public static void main(String[] args) {
    LOGGER.info("A DVD rental analysis agent initialized successfully.");

    Runtime.getRuntime().addShutdownHook(
        new Thread(() -> LOGGER.info("Shutting down DVDRentalsAnalysisAgentUsingMCP...")));

    InMemoryRunner runner = new InMemoryRunner(DVDRentalsAnalysisAgentUsingMCP.ROOT_AGENT,
        "DVD Rentals Analysis Agent Using MCP");

    Session session = runner
        .sessionService()
        .createSession(runner.appName(), "user")
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
                System.out.println("In DVDRentalsAnalysisAgentUsingMCP(response)>" + event.stringifyContent()));
      }
    }
  }
}
