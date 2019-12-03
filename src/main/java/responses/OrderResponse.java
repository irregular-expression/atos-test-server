package responses;

import data.Order;

import java.util.List;

public class OrderResponse extends ServerResponse {
    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    private List<Order> orders;
}
