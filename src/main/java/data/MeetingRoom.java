package data;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "rooms")
public class MeetingRoom {

    @DatabaseField(id = true, canBeNull = false)  private String name;
    @DatabaseField private String description;
    @DatabaseField private int chairsCount;
    //private List<Order> orders;
    @DatabaseField private String earliestTime;
    @DatabaseField private boolean projector;
    @DatabaseField private boolean board;

    public MeetingRoom() {
        //for OrmLite
    }

    public MeetingRoom(String name, String description, int chairsCount, boolean projector, boolean board, String earliestTime) {
        this.name = name;
        this.description = description;
        this.chairsCount = chairsCount;
        this.projector = projector;
        this.board = board;
        this.earliestTime = earliestTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getChairsCount() {
        return chairsCount;
    }

    public void setChairsCount(int chairsCount) {
        this.chairsCount = chairsCount;
    }

    //public List<Order> getOrders() {
    //    return orders;
    //}

    //public void setOrders(List<Order> orders) {
    //    this.orders = orders;
    //}

    public boolean hasProjector() {
        return projector;
    }

    public void setProjector(boolean projector) {
        this.projector = projector;
    }

    public boolean hasBoard() {
        return board;
    }

    public void setBoard(boolean board) {
        this.board = board;
    }

    public String getEarliestTime() {
        return earliestTime;
    }

    public void setEarliestTime(String earliestTime) {
        this.earliestTime = earliestTime;
    }

}
