import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Ebbe Vang
 * https://github.com/flexlab-ruc/TelloDroneJavaConnect
 */
public class TelloDrone {

    private final int udpPort = 8889;
    private DatagramSocket socket;
    private InetAddress IPAddress;
    private boolean isConnected = false;
    private boolean logToConsole = true;

    public TelloDrone() {
        log("Initializing Drone");
    }

    public boolean connect() {
        try {
            log("Connecting to drone");
            IPAddress = InetAddress.getByName("192.168.10.1");
            socket = new DatagramSocket(udpPort);
            sendMessage("command");
            if (ok()) {
                isConnected = true;
                log("Succesfully connected to the drone");
                return true;

            }
            log("Cannot connect to the drone");
            return false;
        } catch (Exception e) {

            return false;
        }
    }

    private boolean ok() {
        return receiveMessage().equals("ok\u0000\u0000\u0000");
    }

    private boolean sendMessage(String command) {
        try {
            byte[] sendData = command.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, udpPort);
            socket.send(sendPacket);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendCommand(String command) {
        sendMessage(command);
        if (ok()) {
            log("command \"" + command + "\" accepted");
            return true;
        } else {
            log("command \"" + command + "\" failed");
            return false;
        }
    }

    private String receiveMessage() {
        byte[] receiveData = new byte[5];
        DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
        try {
            socket.receive(packet);
        } catch (IOException e) {
            return "communication error";
        }
        return new String(packet.getData());
    }

    private void log(String message) {
        if (logToConsole) {
            System.out.print(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
            System.out.print("\t");
            System.out.println(message);

        }
    }

    public void setLogToConsole(boolean logToConsole) {
        this.logToConsole = logToConsole;
    }

    public boolean isLogToConsole() {
        return logToConsole;
    }
}
