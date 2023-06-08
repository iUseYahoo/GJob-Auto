package data;

import gearth.extensions.parsers.HGender;

import java.util.HashMap;
import java.util.Map;

public class OfficeData {
    private int roomId;
    private int groupId;
    private String mottoTag;
    private Map<HGender, String> figures = new HashMap<>();

    public OfficeData(int roomId, int groupId, String mottoTag) {
        this.roomId = roomId;
        this.groupId = groupId;
        this.mottoTag = mottoTag;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getMottoTag() {
        return mottoTag;
    }

    public String getFigureByGender(HGender gender) {
        return figures.getOrDefault(gender, "");
    }

    public void appendFigure(HGender gender, String figure) {
        this.figures.putIfAbsent(gender, figure);
    }
}
