import com.google.gson.Gson;
import data.MeetingRoom;
import data.Order;
import data.User;
import responses.*;

import java.io.IOException;
import java.sql.SQLException;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        get("/hello", (req, res) -> "App is still running!");

        //Authorization, no session required - only login and password validation
        get("/auth", (request, response) -> {
            String login = request.queryMap().get("login").value();
            String password = request.queryMap().get("password").value();
            response.status(200);
            response.type("application/json");
            return new Gson().toJson(auth(login, password));
        });


        get("/rooms", (request, response) -> {
            response.status(200);
            response.type("application/json");
            return new Gson().toJson(getRooms());
        });

        get("/orders", (request, response) -> {
            String roomName = request.queryMap().get("roomName").value();
            response.status(200);
            response.type("application/json");
            return new Gson().toJson(getOrders(roomName));
        });

        post("/reserveRoom", (request, response) -> {
            Order order = (Order) new Gson().fromJson(request.body(), Order.class);
            response.status(200);
            response.type("application/json");
            return new Gson().toJson(addOrder(order));
        });

        get("/createUser", (request, response) -> {
            String login = request.queryMap().get("login").value();
            String password = request.queryMap().get("password").value();
            String name = request.queryMap().get("name").value();
            response.status(200);
            response.type("application/json");
            return new Gson().toJson(createUser(new User(login, password, name)));
        });

        get("/createRoom", (request, response) -> {
            String name = request.queryMap().get("name").value();
            String description = request.queryMap().get("description").value();
            int chairsCount = request.queryMap().get("chairsCount").integerValue();
            boolean projector = request.queryMap().get("projector").booleanValue();
            boolean board = request.queryMap().get("board").booleanValue();
            response.status(200);
            response.type("application/json");
            return new Gson().toJson(createRoom(new MeetingRoom(name, description, chairsCount, projector, board, null)));
        });

    }

    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }

    private static AuthorizationResponse auth(String login, String password) throws IOException {
        TestServerDB database = null;
        AuthorizationResponse res = new AuthorizationResponse();
        try {
            database = TestServerDB.getInstance();
            database.init();
            res = database.validateLoginAndPassword(login, password);
            return res;
        } catch (SQLException e) {
            res.setSuccess(false);
            res.setError(500);
            return res;
        } finally {
            database.close();
        }
    }

    private static ServerResponse createUser(User user) throws IOException {
        TestServerDB database = null;
        ServerResponse res = new ServerResponse();
        try {
            database = TestServerDB.getInstance();
            database.init();
            database.createOrUpdateUser(user);
            User r = database.getUserById(user.getLogin());
            if (r != null) {
                res.setSuccess(true);
                return res;
            } else {
                res.setError(500);
                return res;
            }
        } catch (SQLException e) {
            res.setError(500);
            return res;
        } finally {
            database.close();
        }
    }

    private static ServerResponse createRoom(MeetingRoom room) throws IOException {
        TestServerDB database = null;
        ServerResponse res = new ServerResponse();
        try {
            database = TestServerDB.getInstance();
            database.init();
            database.createOrUpdateRoom(room);
            MeetingRoom r = database.getRoomById(room.getName());
            if (r != null) {
                res.setSuccess(true);
                return res;
            } else {
                res.setError(500);
                return res;
            }
        } catch (SQLException e) {
            res.setError(500);
            return res;
        } finally {
            database.close();
        }
    }


    private static MeetingRoomsResponse getRooms() throws IOException {
        TestServerDB database = null;
        MeetingRoomsResponse res = new MeetingRoomsResponse();
        try {
            database = TestServerDB.getInstance();
            database.init();
            res.setRooms(database.getAllRooms());
            res.setSuccess(true);
            return res;
        } catch (SQLException e) {
            res.setSuccess(false);
            res.setError(500);
            return res;
        } finally {
            database.close();
        }
    }

    private static OrderResponse getOrders(String roomName) throws IOException {
        TestServerDB database = null;
        OrderResponse res = new OrderResponse();
        try {
            database = TestServerDB.getInstance();
            database.init();
            res.setOrders(database.getAllOrdersForRoom(roomName));
            res.setSuccess(true);
            return res;
        } catch (SQLException e) {
            res.setSuccess(false);
            res.setError(500);
            return res;
        } finally {
            database.close();
        }
    }

    private static OrderCreateResponse addOrder(Order order) throws IOException {
        TestServerDB database = null;
        OrderCreateResponse r = new OrderCreateResponse();
        try {
            database = TestServerDB.getInstance();
            database.init();
            database.createOrUpdateOrder(order);
            Order o = database.getOrderById(order.getId());
            if (o != null) {
                r.setOrderId(o.getId());
                r.setSuccess(true);
                return r;
            } else {
                r.setSuccess(false);
                r.setError(103);
                return r;
            }
        } catch (SQLException e) {
            r.setSuccess(false);
            r.setError(500);
            return r;
        } finally {
            database.close();
        }
    }
}
