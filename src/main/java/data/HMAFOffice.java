package data;

import gearth.extensions.parsers.HGender;

public class HMAFOffice extends OfficeData {
    public HMAFOffice(int roomId, int groupId, String mottoTag) {
        super(roomId, groupId, mottoTag);

        this.appendFigure(HGender.Male, "sh-300-64.ch-225-88.lg-285-88.hd-180-1.wa-2009-64");
        this.appendFigure(HGender.Female, "sh-735-64.hd-600-1.lg-720-88.hr-515-33.ch-880-88.wa-2009-64");
    }
}
