import gearth.extensions.Extension;
import gearth.extensions.ExtensionInfo;
import gearth.extensions.parsers.HDirection;
import gearth.extensions.parsers.HEntity;
import gearth.extensions.parsers.HGender;
import gearth.protocol.HPacket;
import gearth.protocol.HMessage;
import java.lang.String;

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

        if (roomid == Rooms.HIA.getRoomId()) {
            sendToServer(new HPacket("{out:JoinHabboGroup}{i:577170}")); // Badge
            sendToServer(new HPacket("{out:ChangeMotto}{s:\"[HIA] Recruit\"}")); // Motto

            // Outfit
            if (gender == HGender.Male) {
                sendToServer(new HPacket("{out:UpdateFigureData}{s:\"M\"}{s:\"ca-1813-0.sh-300-64.ch-225-73.lg-285-64.hd-180-1\"}"));
            } else if (gender == HGender.Female) {
                sendToServer(new HPacket("{out:UpdateFigureData}{s:\"F\"}{s:\"ca-1813-0.sh-907-64.hd-600-1.lg-715-64.hr-515-33.ch-880-73\"}"));
            } else {
                SilentMessage("Gender other than Male or Female detected. (onGetGuestRoom, HIA)");
            }
        } else if (roomid == Rooms.HMAF.getRoomId()) {
            sendToServer(new HPacket("{out:JoinHabboGroup}{i:589944}"));
            sendToServer(new HPacket("{out:ChangeMotto}{s:\"[BA] Recruit\"}"));

            // Outfit
            if (gender == HGender.Male) {
                sendToServer(new HPacket("{out:UpdateFigureData}{s:\"M\"}{s:\"sh-300-64.ch-225-88.lg-285-88.hd-180-1.wa-2009-64\"}"));
            } else if (gender == HGender.Female) {
                sendToServer(new HPacket("{out:UpdateFigureData}{s:\"F\"}{s:\"sh-735-64.hd-600-1.lg-720-88.hr-515-33.ch-880-88.wa-2009-64\"}"));
            } else {
                SilentMessage("Gender other than Male or Female detected. (onGetGuestRoom, HMAF)");
            }
        }
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

            if (gender == HGender.Male) {
                sendToServer(new HPacket("{out:UpdateFigureData}{s:\"M\"}{s:\"" + this._figure + "\"}"));
            } else if (gender == HGender.Female) {
                sendToServer(new HPacket("{out:UpdateFigureData}{s:\"F\"}{s:\"" + this._figure + "\"}"));
            } else {
                SilentMessage("Gender other than Male or Female detected. (onCloseConnection)");
            }

        } else if (roomid == Rooms.HMAF.getRoomId()) {
            sendToServer(new HPacket("{out:KickMember}{i:577170}{i:" + this._userId + "}{b:false}"));

            if (gender == HGender.Male) {
                sendToServer(new HPacket("{out:UpdateFigureData}{s:\"M\"}{s:\"" + this._figure + "\"}"));
            } else if (gender == HGender.Female) {
                sendToServer(new HPacket("{out:UpdateFigureData}{s:\"F\"}{s:\"" + this._figure + "\"}"));
            } else {
                SilentMessage("Gender other than Male or Female detected. (onCloseConnection)");
            }
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
    }
}