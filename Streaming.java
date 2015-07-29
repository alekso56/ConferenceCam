import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Label;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

public class Streaming
  extends Applet
  implements Runnable
{
  Label StateLabel;
  String AudioPortStr;
  String VideoPortStr;
  String IpAddressStr;
  byte language;
  Socket client = null;
  OutputStream sockwrite = null;
  InputStream sockread = null;
  Thread thr = null;
  
  public void init()
  {
    System.out.println("Streaming.class version 1.3");
    usePageParams();
    initForm();
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
    this.thr = new Thread(this);
    this.thr.start();
  }
  
  public void stop()
  {
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
    if (this.thr != null)
    {
      this.thr.stop();
      this.thr = null;
    }
  }
  
  private void usePageParams()
  {
    String backgroundValue = getParameter("background");
    String languageValue = getParameter("language");
    if (backgroundValue == null) {
      backgroundValue = "00214a";
    }
    if (languageValue == null) {
      languageValue = "1";
    }
    setBackground(stringToColor(backgroundValue));
    setForeground(stringToColor("ffff00"));
    this.language = Byte.parseByte(languageValue);
    if (this.language == 1)
    {
      this.AudioPortStr = "Porta Audio:";
      this.VideoPortStr = "Porta Video:";
      this.IpAddressStr = "Indirizzo IP:";
    }
    else
    {
      this.AudioPortStr = "Audio Port:";
      this.VideoPortStr = "Video Port:";
      this.IpAddressStr = "IP Address:";
    }
  }
  
  private Color stringToColor(String paramValue)
  {
    int red = Integer.decode("0x" + paramValue.substring(0, 2)).intValue();
    int green = Integer.decode("0x" + paramValue.substring(2, 4)).intValue();
    int blue = Integer.decode("0x" + paramValue.substring(4, 6)).intValue();
    
    return new Color(red, green, blue);
  }
  
  void initForm()
  {
    setLayout(null);
    this.StateLabel = new Label("");
    add(this.StateLabel);
    this.StateLabel.setBounds(0, 0, 500, 20);
    Font old = this.StateLabel.getFont();
    Font newfont = new Font(old.getName(), old.getStyle(), old.getSize() * 5 / 4);
    this.StateLabel.setFont(newfont);
  }
  
  public void run()
  {
    byte[] writeBuffCfg = new byte[1];
    
    byte[] readBuff = new byte[28];
    
    String IpAddress = "";
    String stateStr = "";
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
      this.sockread = this.client.getInputStream();
    }
    catch (IOException e)
    {
      System.out.println("IOException in InputStream creation");
      return;
    }
    for (;;)
    {
      boolean timeout = false;
      try
      {
        writeBuffCfg[0] = 12;
        this.sockwrite.write(writeBuffCfg);
      }
      catch (IOException e)
      {
        System.out.println("IOException in OutputStream.write");
      }
      try
      {
        this.sockread.read(readBuff);
      }
      catch (IOException e)
      {
        timeout = true;
      }
      long VideoPort;
      long AudioPort;
      byte Status;
      if (timeout)
      {
        VideoPort = 556L;
        AudioPort = 554L;
        IpAddress = "0.0.0.0";
        Status = 0;
        stateStr = "";
      }
      else
      {
        AudioPort = readBuff[0] << 24 | readBuff[1] << 16 | readBuff[2] << 8 | readBuff[3];
        VideoPort = readBuff[4] << 24 | readBuff[5] << 16 | readBuff[6] << 8 | readBuff[7];
        String strappo = new String(readBuff, 8, 16);
        Integer lungh = new Integer(strappo.indexOf(0));
        IpAddress = strappo.substring(0, lungh.intValue());
        Status = readBuff[24];
        if (Status == 0)
        {
          if (this.language == 1) {
            stateStr = "Attenzione! Streaming non attivo.";
          } else {
            stateStr = "Warning! Streaming is stopped.";
          }
        }
        else {
          stateStr = this.IpAddressStr + " " + IpAddress + ", " + this.AudioPortStr + " " + AudioPort + ", " + this.VideoPortStr + " " + VideoPort;
        }
      }
      this.StateLabel.setText(stateStr);
      try
      {
        Thread.sleep(10000L);
      }
      catch (InterruptedException e)
      {
        System.out.println("InterruptedException in Thread.sleep");
      }
    }
  }
}
