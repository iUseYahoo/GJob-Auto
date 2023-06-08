package data;

import gearth.extensions.parsers.HGender;

public class SSOffice extends OfficeData {
    public SSOffice(int roomId, int groupId, String mottoTag) {
        super(roomId, groupId, mottoTag);

        this.appendFigure(HGender.Male, "ca-1804-1408.sh-290-64.hd-180-1.lg-270-1408.ch-230-64.wa-2007-0");
        this.appendFigure(HGender.Female, "ca-1805-64.sh-907-64.hd-600-1.lg-715-64.hr-515-33.ch-630-1408");
    }
}
