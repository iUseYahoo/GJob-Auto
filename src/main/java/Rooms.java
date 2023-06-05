public enum Rooms {
    // Code: https://stackoverflow.com/questions/1067352/can-i-set-enum-start-value-in-java
    HMAF(78630689),
    HIA(76377765);

    private final int roomId;

    Rooms(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }
}