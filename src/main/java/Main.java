import gearth.extensions.Extension;
import gearth.extensions.ExtensionInfo;
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
    private String _figure;
    public Main(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        new Main(args).run();
    }

    private void onGetGuestRoom(HMessage hmsg) {
        // Assign the packet to a variable.
        HPacket packet = hmsg.getPacket();
        // Read the string from the packet.
        int roomid = packet.readInteger();

        // Check if the roomid is the same as the Rooms.AGENCY.getRoomId() room id value
        if (roomid == Rooms.HIA.getRoomId()) {
            // If it is, send a packet to the server to change the Badge, Uniform and motto.
            // TODO - change the uniform and motto
            sendToServer(new HPacket("{out:JoinHabboGroup}{i:577170}"));
        }
    }

    public void SilentMessage(String message) {
        // Method / Function to send a message to the user without it being shown in the chat.
        sendToServer(new HPacket("{in:Chat}{i:-1}{s:\"${" + message + "}\"}{i:0}{i:23}{i:0}{i:-1}"));
    }

    public void onCloseConnection(HMessage hmsg) {
        // So if the user leaves the target agency / military room do the following
        // Check if the _lastRoom value is the same as the Rooms.AGENCY.getRoomId() room id value
        if (_lastRoom.equals(Rooms.HIA.getRoomId())) {
            // Check if the user id has been set by the onUsers method.
            if (this._userId == 0) {
                // If it has not been set, send a message to the user.
                SilentMessage("userId is default. Please enter a room.");
            } else {
                // If it has been set, send a packet to the server to remove the badge.
                sendToServer(new HPacket("{out:KickMember}{i:577170}{i:" + this._userId + "}{b:false}"));
                // TODO: remove uniform and motto
            }
        }
    }

    public void onUsers(HMessage hmsg) {
        // Assign the packet to a variable.
        HPacket packet = hmsg.getPacket();
        // Read the integer from the packet and assign it to the _userId variable.
        this._userId = packet.readInteger();
        // Read the "name" from the packet but dont store it
        packet.readString();
        // Read the "figure" from the packet (Outfit) and assign it to the _figure variable.
        this._figure = packet.readString();
    }

    @Override
    protected void initExtension() {
        // Intercept the room the user enters, and get the room id.
        intercept(HMessage.Direction.TOSERVER, "GetGuestRoom", this::onGetGuestRoom);
        // Intercept when the user leaves the room, and remove the main badge, motto and uniform.
        intercept(HMessage.Direction.TOSERVER, "CloseConnection", this::onCloseConnection);
        // Intercept when the user enters the room, getting their userid.
        intercept(HMessage.Direction.TOCLIENT, "Users", this::onUsers);
    }
}