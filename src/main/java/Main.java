import data.ExampleOffice;
import data.OfficeData;
import gearth.extensions.Extension;
import gearth.extensions.ExtensionInfo;
import gearth.extensions.parsers.HDirection;
import gearth.extensions.parsers.HEntity;
import gearth.extensions.parsers.HGender;
import gearth.protocol.HPacket;
import gearth.protocol.HMessage;
import java.lang.String;
import java.util.Map;

@ExtensionInfo(
        Title = "GJob-Auto",
        Description = "Automatically changes you into the Badge, Uniform and motto of the agency or military you enter.",
        Version = "1.0",
        Author = "floppidity"
)

public class Main extends Extension {
    private String _lastRoom = "";
    private int _userId;
    private int roomid;
    private String _figure;
    private HGender gender;
    private boolean interceptNext = false;
    private HEntity user = null;
    private String _motto;
    private Map<Integer, OfficeData> _offices;

    public Main(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        new Main(args).run();
    }

    private void onGetGuestRoom(HMessage hmsg) {
        // Assign the packet to a variable.
        HPacket packet = hmsg.getPacket();
        // Read the int from the packet.
        roomid = packet.readInteger();

        if (!isAuthorisedRoom(roomid) || !isValidGender(gender)) {
            return;
        }

        OfficeData officeData = this._offices.get(roomid);

        if (officeData == null) {
            return;
        }

        sendToServer(new HPacket("{out:JoinHabboGroup}{i:" + officeData.getGroupId() +"}"));
        sendToServer(new HPacket("{out:ChangeMotto}{s:" + officeData.getMottoTag() + "}"));
        sendToServer(new HPacket("{out:UpdateFigureData}{s: " + gender.toString() +"}{s:" + officeData.getFigureByGender(gender) + "}"));
    }

    public void SilentMessage(String message) {
        // Method / Function to send a message to the user without it being shown in the chat.
        sendToServer(new HPacket("{in:Chat}{i:-1}{s:\"${" + message + "}\"}{i:0}{i:23}{i:0}{i:-1}"));
    }

    public void onCloseConnection(HMessage hmsg) {
        // So if the user leaves the target agency / military room do the following
        // Check if the _lastRoom value is the same as the Rooms.AGENCY.getRoomId() room id value

        if (this._userId == 0) {
            SilentMessage("userId is default. Please enter a room. (onCloseConnection)");
        }

        if (roomid == Rooms.HIA.getRoomId()) {
            sendToServer(new HPacket("{out:KickMember}{i:577170}{i:" + this._userId + "}{b:false}"));
        } else if (roomid == Rooms.HMAF.getRoomId()) {
            sendToServer(new HPacket("{out:KickMember}{i:589944}{i:" + this._userId + "}{b:false}"));
        } else if (roomid == Rooms.SS.getRoomId()) {
            sendToServer(new HPacket("{out:KickMember}{i:538923}{i:" + this._userId + "}{b:false}"));
        }

        sendToServer(new HPacket("{out:ChangeMotto}{s:\"" + this._motto + "\"}"));
        if (gender == HGender.Male) {
            sendToServer(new HPacket("{out:UpdateFigureData}{s:\"M\"}{s:\"" + this._figure + "\"}"));
        } else if (gender == HGender.Female) {
            sendToServer(new HPacket("{out:UpdateFigureData}{s:\"F\"}{s:\"" + this._figure + "\"}"));
        } else {
            SilentMessage("Gender other than Male or Female detected. (onCloseConnection)");
        }
    }

    public void onUsers(HMessage hmsg) {
        // Check if interceptNext is true
        // If true then get and store; userid and figure
        // store the user entity
        if (interceptNext) {
            // Set interceptNext to false so that the onUsers method will not be called again.
            interceptNext = false;
            // Get the user entity from the packet and store it
            user = HEntity.parse(hmsg.getPacket())[0];
            // get and store the gender
            gender = user.getGender();
            // assign the packet to a variable
            HPacket packet = hmsg.getPacket();
            // store the userid
            this._userId = packet.readInteger();
            packet.readString();
            // store the motto
            this._motto = packet.readString();
            // store the figure (outfit)
            this._figure = packet.readString();
        }

        // Old code:
        // Assign the packet to a variable.
//        HPacket packet = hmsg.getPacket();
        // Read the integer from the packet and assign it to the _userId variable.
//        this._userId = packet.readInteger();
        // Read the "name" from the packet but dont store it
//        packet.readString();
        // Read the "figure" from the packet (Outfit) and assign it to the _figure variable.
//        this._figure = packet.readString();
    }

    public void onItems(HMessage msg) {
        // Change interceptNext to true so that the onUsers method will be called next.
        interceptNext = true;
    }

    private boolean isAuthorisedRoom(int roomId) {
        return roomId == Rooms.HIA.getRoomId() || roomId == Rooms.HMAF.getRoomId() || roomId == Rooms.SS.getRoomId();
    }

    private boolean isValidGender(HGender gender) {
        return gender == HGender.Male || gender == HGender.Female;
    }

    @Override
    protected void initExtension() {
        // Intercept the room the user enters, and get the room id.
        intercept(HMessage.Direction.TOSERVER, "GetGuestRoom", this::onGetGuestRoom);
        // Intercept when the user leaves the room, and remove the main badge, motto and uniform.
        intercept(HMessage.Direction.TOSERVER, "CloseConnection", this::onCloseConnection);
        // Intercept the Items packet before we intercept the Users packet (useful for later)
        intercept(HMessage.Direction.TOCLIENT, "Items", this::onItems);
        // Intercept when the user enters the room, getting their userid and figure.
        intercept(HMessage.Direction.TOCLIENT, "Users", this::onUsers);

        this._offices.put(Rooms.HIA.getRoomId(), new ExampleOffice(Rooms.HIA.getRoomId(), 577170, "[HIA] Recruit"));
        this._offices.put(Rooms.HMAF.getRoomId(), new ExampleOffice(Rooms.HMAF.getRoomId(), 589944, "[BA] Recruit"));
        this._offices.put(Rooms.SS.getRoomId(), new ExampleOffice(Rooms.SS.getRoomId(), 538923, "[SS] Recruit"));
    }
}