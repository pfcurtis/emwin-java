
package com.terrapin.emwin.test;

import java.io.*;
import java.net.*;
import java.text.ParseException;
import com.terrapin.emwin.object.PacketException;

public class SocketTest {

    private static DataInputStream dis;
    public static void main(String[] args) throws IOException, PacketException, ParseException {



     try
     {
         Socket s = new Socket("www.opennoaaport.net", 2211);
         dis = new DataInputStream(s.getInputStream());

     }
     catch (UnknownHostException e)
     {
         System.out.println("Unknown host: localhost");
         System.exit(1);
     }
     catch  (IOException e)
     {
         System.out.println("No I/O");
         System.exit(1);
     }

     //Receive data from ROS SerialtoNetwork server

     try 
     {
         while(true)
         {
            //in.readInt();
            byte[] cbuf = new byte[257];
            dis.readFully(cbuf);        

            String lstr = new String(cbuf);
            System.out.print(lstr);

            System.out.println("");
         }            
    } 
    catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
}
