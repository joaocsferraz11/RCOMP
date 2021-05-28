import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class CentroDistribuicao {

    static InetAddress serverIP;
    static Socket sock;

    public static void main(String args[]) throws Exception {
        if(args.length!=1) {
            System.out.println("Server IPv4/IPv6 address or DNS name is required as argument");
            System.exit(1); }

        try { serverIP = InetAddress.getByName(args[0]); }
        catch(UnknownHostException ex) {
            System.out.println("Invalid server specified: " + args[0]);
            System.exit(1); }

        try { sock = new Socket(serverIP, 32507); }
        catch(IOException ex) {
            System.out.println("Failed to establish TCP connection");
            System.exit(1); }

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream sOut = new DataOutputStream(sock.getOutputStream());
        DataInputStream sIn = new DataInputStream(sock.getInputStream());

        Scanner scan = new Scanner(System.in);
        String nomeFich = "";
        while(!nomeFich.equals("0")) {
            System.out.println("Please enter the desired file name: (0 to finish) \n");
            nomeFich = scan.nextLine();
            if (nomeFich.equals("0")) {
                break;
            }
            File file = new File(nomeFich);
            // Get the size of the file
            long length = file.length();
            String frase = "";
            List<String> listStrings = new ArrayList<>();

            BufferedReader r = new BufferedReader(new FileReader(nomeFich));
            int ch;
            int count = 0;
            char character;

            while ((ch = r.read()) != -1) {
                character = (char) ch;
                frase = frase + character;
                count++;
                if (count % 252 == 0) {
                    System.out.println(frase);
                    listStrings.add(frase);
                    frase = "";

                }

                if (length == count && count % 252 != 0) {
                    System.out.println(frase);
                    listStrings.add(frase);
                }
            }

            while (!listStrings.isEmpty() && listStrings.size()>1) {

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );


                Charset charset = StandardCharsets.UTF_16;
                byte[] byteArrrayCode = {0, (byte) 255, (byte) 255};
                outputStream.write( byteArrrayCode);
                byte[] byteArrray =  listStrings.get(0).getBytes(charset);
                outputStream.write( byteArrray);
                byte[] message = outputStream.toByteArray( );
                sOut.write(message);
                listStrings.remove(0);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );


            Charset charset = StandardCharsets.UTF_16;
            byte[] byteArrrayCode = {0, (byte) 254, (byte) listStrings.get(0).length()};
            outputStream.write( byteArrrayCode);
            byte[] byteArrray =  listStrings.get(0).getBytes(charset);
            outputStream.write( byteArrray);
            byte[] message = outputStream.toByteArray( );
            sOut.writeInt(message.length);
            sOut.write(message);
            listStrings.remove(0);

            System.out.print("File sent");
        }

        sock.close();
    }
}



