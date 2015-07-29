import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

public class PageCtrl
  extends Applet
{
  byte TypeMsg;
  byte SubType;
  Socket client = null;
  OutputStream sockwrite = null;
  byte[] writeBuff = new byte[3];
  
  public void init()
  {
    System.out.println("PageCtrl.class version 1.02");
    usePageParams();
  }
  
  public void start()
  {
    int serverport = 55001;
    try
    {
      InetAddress address = InetAddress.getByName(getCodeBase().getHost());
      try
      {
        if (this.client == null) {
          this.client = new Socket(address, serverport);
        }
        try
        {
          this.client.setSoTimeout(10000);
        }
        catch (SocketException e)
        {
          System.out.println("SocketException in timer setting");
        }
        try
        {
          this.sockwrite = this.client.getOutputStream();
        }
        catch (IOException e)
        {
          System.out.println("IOException in OutputStream creation");
          return;
        }
        try
        {
          this.writeBuff[0] = this.TypeMsg;
          this.writeBuff[1] = this.SubType;
          this.writeBuff[2] = 1;
          this.sockwrite.write(this.writeBuff);
        }
        catch (IOException e)
        {
          System.out.println("IOException in OutputStream.write");
        }
      }
      catch (IOException e)
      {
        System.out.println("IOException in socket creation");
        return;
      }
    }
    catch (UnknownHostException e)
    {
      System.out.println("UnknownHostException inetaddress");
      return;
    }
  }
  
  public void stop()
  {
    try
    {
      this.writeBuff[0] = this.TypeMsg;
      this.writeBuff[1] = this.SubType;
      this.writeBuff[2] = 0;
      this.sockwrite.write(this.writeBuff);
    }
    catch (IOException e)
    {
      System.out.println("IOException in OutputStream.write");
    }
    try
    {
      if (this.sockwrite != null) {
        this.sockwrite.close();
      }
    }
    catch (IOException e)
    {
      System.out.println("IOException Stream.close");
    }
    try
    {
      if (this.client != null) {
        this.client.close();
      }
    }
    catch (IOException e)
    {
      System.out.println("IOException Socket.close");
    }
  }
  
  private void usePageParams()
  {
    String backgroundValue = getParameter("background");
    String typeValue = getParameter("type");
    String subtypeValue = getParameter("subtype");
    if (backgroundValue == null) {
      backgroundValue = "00214a";
    }
    if (typeValue == null) {
      typeValue = "1";
    }
    if (subtypeValue == null) {
      subtypeValue = "1";
    }
    setBackground(stringToColor(backgroundValue));
    switch (Byte.parseByte(typeValue))
    {
    case 1: 
      this.TypeMsg = 10;
      break;
    case 2: 
      this.TypeMsg = 11;
      break;
    }
    this.SubType = Byte.parseByte(subtypeValue);
  }
  
  private Color stringToColor(String paramValue)
  {
    int red = Integer.decode("0x" + paramValue.substring(0, 2)).intValue();
    int green = Integer.decode("0x" + paramValue.substring(2, 4)).intValue();
    int blue = Integer.decode("0x" + paramValue.substring(4, 6)).intValue();
    
    return new Color(red, green, blue);
  }
}
