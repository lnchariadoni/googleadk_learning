package tools;

import model.OrderDetails;
import com.google.adk.tools.Annotations.Schema;
import java.util.Map;
import java.util.Optional;

public class CustomerOrderTools {
  private static final Map<String, OrderDetails> ORDER_DATABASE = Map.of(
      "1001", new OrderDetails("1001", "2025-01-02T10:15:30Z", "Shipped", "Left warehouse"),
      "1002", new OrderDetails("1002", "2025-01-05T09:00:00Z", "Processing", "Awaiting fulfillment"),
      "1003", new OrderDetails("1003", "2024-12-20T12:00:00Z", "Delivered", "Delivered to front door"),
      "1004", new OrderDetails("1004", "2024-11-01T08:30:00Z", "Cancelled", "Cancelled per request")
  );

  private CustomerOrderTools() {
    // Prevent instantiation
  }

  @Schema(
      description = "Retrieve the order details of a customer order by its ID. The details include order ID, order date, description and current status.",
      name = "retrieveOrderById"
  )
  public static Map<String, OrderDetails> retrieveOrderById(
      @Schema(name = "orderId", description = "The unique identifier of the customer order.") String orderId) {
    OrderDetails value = Optional.ofNullable(ORDER_DATABASE.get(orderId))
        .orElse(new OrderDetails(null, "","", "Provided order `%s` not found in the database.".formatted(orderId)));

    return Map.of("orderDetails", value);
  }



}
