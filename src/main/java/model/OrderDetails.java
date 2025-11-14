package model;

import com.google.adk.tools.Annotations.Schema;

public record OrderDetails(
    @Schema(name = "orderId", description = "The unique identifier of the customer order") String orderId,
    @Schema(name = "orderDate", description = "ISO 8601 order creation date/time") String orderDate,
    @Schema(name = "status", description = "Current order status") String status,
    @Schema(name = "description", description = "Optional notes or description for the order") String description) {
}