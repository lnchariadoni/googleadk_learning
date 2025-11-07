package agents;

import com.google.adk.agents.BaseAgent;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

// TODO remove this
/*
How to run:
    export GOOGLE_API_KEY=YOUR_KEY

    mvn compile exec:java -DmainClass="agents.ScienceTeacherAgent"

    mvn compile exec:java \
    -Dexec.mainClass="com.google.adk.web.AdkWebServer" \
    -Dexec.args="--adk.agents.source-dir=target --server.port=8000"

References:
- https://www.youtube.com/watch?v=VM3b3csBeUc&list=PLWVjTNKbh-LmnsxminYNE5UM0eKH4oy_c
- https://www.youtube.com/watch?v=44C8u0CDtSo
- https://www.youtube.com/watch?v=P4VFL9nIaIA
 */
public class ScienceTeacherAgent {
  public static final String LLM_MODEL_GEMINI_2_5_PRO= "gemini-2.5-pro";
  public static final String LLM_MODEL_GEMINI_2_5_FLASH = "gemini-2.5-flash"; // working and tested
  public static final String LLM_MODEL_GEMINI_2_0_FLASH = "gemini-2.0-flash";
  public static final String LLM_MODEL_GEMINI_1_5_FLASH = "gemini-1.5-flash";

  public static final List<String> EXIT_VARIANTS = List.of("exit", "quit", "bye", "goodbye");

  public static final BaseAgent ROOT_AGENT = initializeAgent();

  public static BaseAgent initializeAgent() {
    return LlmAgent
        .builder()
        .name("science-teacher-assistant")
        .description("scientific research assistant for teachers")
        .model(LLM_MODEL_GEMINI_2_5_FLASH)
        .instruction("""
            You are a helpful science teacher that explains science concepts to kids and teenagers in simple words and in an engaging fashion.
            """)
        .build();
  }

  public static void main(String[] args) {
    System.out.println("Welcome to the Science Teacher Assistant!");
    InMemoryRunner runner = new InMemoryRunner(ROOT_AGENT);
    Session session = runner.sessionService()
        .createSession(runner.appName(), "demo_session_user")
        .blockingGet();

    try(Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
      while (true) {
        System.out.print("You: ");
        String userInput = scanner.nextLine().trim();

        if(userInput.isEmpty()) {
          System.out.println("Please enter a valid question or type 'exit' or 'quit' to quit.");
          continue;
        }


        if(EXIT_VARIANTS.contains(userInput)) {
          System.out.println("Exiting the Science Teacher Assistant. Goodbye!");
          break;
        }

        Content userInputContent = Content.fromParts(Part.fromText(userInput));
        Flowable<Event> events = runner.runAsync(session.userId(), session.id(), userInputContent);

        System.out.println("\nAgent:");
        events.blockingForEach(ScienceTeacherAgent::printEvent);
      }
    }
  }

  private static void printEvent(Event event) {
    System.out.println(event.stringifyContent());
  }

}
