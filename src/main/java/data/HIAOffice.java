package data;

import gearth.extensions.parsers.HGender;

public class HIAOffice extends OfficeData {
    public HIAOffice(int roomId, int groupId, String mottoTag) {
        super(roomId, groupId, mottoTag);

        this.appendFigure(HGender.Male, "ca-1813-0.sh-300-64.ch-225-73.lg-285-64.hd-180-1");
        this.appendFigure(HGender.Female, "ca-1813-0.sh-907-64.hd-600-1.lg-715-64.hr-515-33.ch-880-73");
    }
}
