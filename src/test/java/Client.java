import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket=new Socket("127.0.0.1",80);
        BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Scanner sc=new Scanner(System.in);
        sc.useDelimiter(System.lineSeparator());
        InputStream in=socket.getInputStream();

        String line="1";
        while (true){
            line=sc.next();
            writer.write( line.replace("*","\r\n"));
            writer.flush();
            if(line.isEmpty())
                break;
        }
        writer.close();
        byte[] bs=new byte[1024];
        in.read(bs);
        System.out.println(new String(bs));
        socket.close();
    }
}
