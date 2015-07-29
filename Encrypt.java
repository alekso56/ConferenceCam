import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

public class Encrypt
  extends Applet
  implements Runnable
{
  Encrypt.ImagePaint Lock;
  Image Lockyellow;
  Image Lockgreen;
  Image Lockred;
  Image Lockdisabled;
  byte State;
  byte NotVisible;
  byte OnlyStatus;
  Thread thr;
  byte language;
  Socket client;
  OutputStream sockwrite;
  InputStream sockread;
  
  public Encrypt()
  {
    this.State = 0;
    this.NotVisible = 0;
    this.OnlyStatus = 0;
    
    this.thr = null;
    
    this.client = null;
    this.sockwrite = null;
    this.sockread = null;
  }
  
  public void init()
  {
    System.out.println("Encrypt.class version 1.03");
    usePageParams();
    
    this.Lockyellow = getImage(getDocumentBase(), "lockyell.gif");
    this.Lockgreen = getImage(getDocumentBase(), "lockgreen.gif");
    this.Lockred = getImage(getDocumentBase(), "lockred.gif");
    this.Lockdisabled = getImage(getDocumentBase(), "lockdis.gif");
    
    this.Lock = new Encrypt.ImagePaint(this.Lockyellow);
    
    initForm();
  }
  
  private void usePageParams()
  {
    String str1 = getParameter("background");
    String str2 = getParameter("foreground");
    String str3 = getParameter("language");
    String str4 = getParameter("status");
    if (str1 == null) {
      str1 = "00214a";
    }
    if (str2 == null) {
      str2 = "ffffff";
    }
    if (str3 == null) {
      str3 = "1";
    }
    if (str4 == null) {
      str4 = "0";
    }
    setBackground(stringToColor(str1));
    setForeground(stringToColor(str2));
    this.language = Byte.parseByte(str3);
    this.OnlyStatus = Byte.parseByte(str4);
  }
  
  public void start()
  {
    int i = 55001;
    try
    {
      InetAddress localInetAddress = InetAddress.getByName(getCodeBase().getHost());
      try
      {
        if (this.client == null) {
          this.client = new Socket(localInetAddress, i);
        }
        try
        {
          this.client.setSoTimeout(10000);
        }
        catch (SocketException localSocketException)
        {
          System.out.println("SocketException in timer setting");
        }
      }
      catch (IOException localIOException)
      {
        System.out.println("IOException in socket creation");
        return;
      }
    }
    catch (UnknownHostException localUnknownHostException)
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
      if (this.sockread != null) {
        this.sockread.close();
      }
    }
    catch (IOException localIOException1)
    {
      System.out.println("IOException Stream.close");
    }
    try
    {
      if (this.client != null) {
        this.client.close();
      }
    }
    catch (IOException localIOException2)
    {
      System.out.println("IOException Socket.close");
    }
    if (this.thr != null)
    {
      this.thr.stop();
      this.thr = null;
    }
  }
  
  private Color stringToColor(String paramString)
  {
    int i = Integer.decode("0x" + paramString.substring(0, 2)).intValue();
    int j = Integer.decode("0x" + paramString.substring(2, 4)).intValue();
    int k = Integer.decode("0x" + paramString.substring(4, 6)).intValue();
    
    return new Color(i, j, k);
  }
  
  void initForm()
  {
    setLayout(null);
    
    AddControls();
    
    Font localFont1 = getFont();
    Font localFont2 = new Font(localFont1.getName(), localFont1.getStyle(), localFont1.getSize() + 2);
    setFont(localFont2);
  }
  
  void AddControls()
  {
    this.Lock.setBounds(0, 0, 50, 50);
    add(this.Lock);
    this.Lock.addMouseListener(new Encrypt.LockImgListener());
  }
  
  public void run()
  {
    byte[] arrayOfByte1 = new byte[2];
    byte[] arrayOfByte2 = new byte[1];
    int i = 0;
    try
    {
      this.sockwrite = this.client.getOutputStream();
    }
    catch (IOException localIOException1)
    {
      System.out.println("IOException in OutputStream creation");
      return;
    }
    try
    {
      this.sockread = this.client.getInputStream();
    }
    catch (IOException localIOException2)
    {
      System.out.println("IOException in InputStream creation");
      return;
    }
    for (;;)
    {
      try
      {
        arrayOfByte1[0] = 22;
        arrayOfByte1[1] = 0;
        this.sockwrite.write(arrayOfByte1);
      }
      catch (IOException localIOException3)
      {
        System.out.println("IOException in OutputStream.write");
      }
      try
      {
        i = 0;
        this.sockread.read(arrayOfByte2);
      }
      catch (IOException localIOException4)
      {
        i = 1;
        System.out.println("IOException in OutputStream.read : timeout");
      }
      if (i == 0)
      {
        this.State = arrayOfByte2[0];
        switch (this.State)
        {
        case 0: 
          this.Lock.SetImage(this.Lockdisabled);
          break;
        case 1: 
          this.Lock.SetImage(this.Lockyellow);
          break;
        case 2: 
          this.Lock.SetImage(this.Lockgreen);
          break;
        case 3: 
          this.Lock.SetImage(this.Lockred);
          break;
        default: 
          this.NotVisible = 1;
        }
      }
      try
      {
        Thread.sleep(5000L);
      }
      catch (InterruptedException localInterruptedException)
      {
        System.out.println("InterruptedException in Thread.sleep");
      }
    }
  }
  
  class ImagePaint
    extends Canvas
  {
    Image Img;
    Cursor new_cursor = new Cursor(12);
    
    public ImagePaint(Image paramImage)
    {
      this.Img = paramImage;
      if (Encrypt.this.OnlyStatus != 1) {
        setCursor(this.new_cursor);
      }
    }
    
    public void SetImage(Image paramImage)
    {
      this.Img = paramImage;
      repaint();
    }
    
    public void paint(Graphics paramGraphics)
    {
      if (Encrypt.this.NotVisible == 0) {
        paramGraphics.drawImage(this.Img, 0, 0, this);
      }
    }
  }
  
  class LockImgListener
    implements MouseListener
  {
    LockImgListener() {}
    
    public void mouseReleased(MouseEvent paramMouseEvent) {}
    
    public void mouseClicked(MouseEvent paramMouseEvent)
    {
      switch (Encrypt.this.State)
      {
      case 0: 
        byte b = 1;
        if (Encrypt.this.OnlyStatus != 1) {
          Encrypt.this.CallEnableEnc(b);
        }
        break;
      case 1: 
        if (Encrypt.this.OnlyStatus != 1) {
          Encrypt.this.CallStartEnc();
        }
        break;
      }
    }
    
    public void mouseEntered(MouseEvent paramMouseEvent) {}
    
    public void mousePressed(MouseEvent paramMouseEvent) {}
    
    public void mouseExited(MouseEvent paramMouseEvent) {}
  }
  
  public void CallStartEnc()
  {
    byte[] arrayOfByte = new byte[1];
    try
    {
      if (this.sockwrite != null)
      {
        arrayOfByte[0] = 23;
        this.sockwrite.write(arrayOfByte);
      }
    }
    catch (IOException localIOException)
    {
      System.out.println("IOException in OutputStream.write");
    }
  }
  
  public void CallEnableEnc(byte paramByte)
  {
    byte[] arrayOfByte = new byte[2];
    try
    {
      if (this.sockwrite != null)
      {
        arrayOfByte[0] = 24;
        arrayOfByte[1] = paramByte;
        this.sockwrite.write(arrayOfByte);
      }
    }
    catch (IOException localIOException)
    {
      System.out.println("IOException in OutputStream.write");
    }
  }
}
