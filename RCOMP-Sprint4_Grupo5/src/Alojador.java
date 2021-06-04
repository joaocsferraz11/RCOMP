import java.io.*;
import java.net.*;

public class Alojador {

    static ServerSocket sock;

    public static void main(String args[]) throws Exception {
        Socket cliSock;

        try { sock = new ServerSocket(32507); }
        catch(IOException ex) {
            System.out.println("Failed to open server socket");
            System.exit(1);
        }

        while(true) {
            cliSock=sock.accept();
            new Thread(new AlojadorThread(cliSock)).start();
        }
    }
}



class AlojadorThread implements Runnable {
    private Socket s;
    private DataOutputStream sOut;
    private DataInputStream sIn;

    public AlojadorThread(Socket cli_s) { s=cli_s;}

    public void run() {
        long f,i,num,sum;
        InetAddress clientIP;

        clientIP=s.getInetAddress();
        System.out.println("New client connection from " + clientIP.getHostAddress() +
                ", port number " + s.getPort());
        try {
            sOut = new DataOutputStream(s.getOutputStream());
            sIn = new DataInputStream(s.getInputStream());
            byte [] message = new byte[0];
            byte [] newMessage = new byte[0];
            byte [] code;


                do {
                    int length = sIn.readInt();
                    if(length>0){
                        newMessage = new byte[length];
                        sIn.readFully(message,0,message.length);
                    /*code = newMessage.substring(0,3);
                    StringBuffer text = new StringBuffer(newMessage);
                    text.replace(0,3,"");
                    newMessage = text.toString();*/
                        String string =  new String (message);
                        System.out.println(string);

                    }

                }
                while(!newMessage.equals("0"));

            System.out.println("Client " + clientIP.getHostAddress() + ", port number: " + s.getPort() +
                    " disconnected");
            s.close();
        }
        catch(IOException ex) { System.out.println("IOException"); }
    }
}