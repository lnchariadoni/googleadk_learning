import io.reactivex.rxjava3.core.Observable;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ThrowAwary {

  @Test
  void observableTest() {
    List<String> numbers = new ArrayList<>(List.of("one", "two", "three"));

    Observable<List<String>> observable = Observable.just(numbers);

//    observable.map(String::toUpperCase).subscribe(e -> System.out.println(e));
    observable.subscribe(e -> System.out.println(e));

    try { Thread.sleep(10000); } catch(Exception e) {}
    numbers.add("four");
    Assertions.assertTrue(true);
  }

  @Test
  void testing() {
    List<String> EXIT_VARIANTS = List.of("exit", "quit", "bye", "goodbye");
    String userInput  = "bye";
    var result = EXIT_VARIANTS.stream().map(variant ->
    {
      System.out.println("Checking variant: " + variant);
      return variant.toLowerCase().equals(userInput);
    })
        .filter(conditionCheck -> conditionCheck)
        .peek(System.out::println)
        .findAny().orElse(false);

    System.out.println(result);
//    variant -> variant.toLowerCase().equals(userInput)).findAny().orElse(false)
  }
}
