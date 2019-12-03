package responses;

import data.MeetingRoom;

import java.util.List;

public class MeetingRoomsResponse extends ServerResponse {
    private List<MeetingRoom> rooms;

    public List<MeetingRoom> getRooms() {
        return rooms;
    }

    public void setRooms(List<MeetingRoom> rooms) {
        this.rooms = rooms;
    }
}
