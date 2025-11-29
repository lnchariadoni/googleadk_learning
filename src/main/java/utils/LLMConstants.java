package utils;

import java.util.List;

public class LLMConstants {
  public static final String LLM_MODEL_GEMINI_2_5_PRO = "gemini-2.5-pro";
  public static final String LLM_MODEL_GEMINI_2_5_FLASH = "gemini-2.5-flash"; // working and tested
  public static final String LLM_MODEL_GEMINI_2_5_FLASH_LITE = "gemini-2.5-flash-lite"; // working and tested
  public static final String LLM_MODEL_GEMINI_2_0_FLASH = "gemini-2.0-flash";
  public static final String LLM_MODEL_GEMINI_2_0_FLASH_LITE = "gemini-2.0-flash-lite";
  public static final String LLM_MODEL_GEMINI_1_5_FLASH = "gemini-1.5-flash";

  public static final String CURRENT_MODEL = LLM_MODEL_GEMINI_2_5_PRO;

  public static final List<String> EXIT_VARIANTS = List.of("exit", "quit", "bye", "goodbye");

  private LLMConstants() {
    // Prevent instantiation
  }
}
