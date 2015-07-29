import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Image;

class Encrypt$ImagePaint
  extends Canvas
{
  Image Img;
  Cursor new_cursor = new Cursor(12);
  
  public Encrypt$ImagePaint(Encrypt paramEncrypt, Image paramImage)
  {
    this.Img = paramImage;
    if (paramEncrypt.OnlyStatus != 1) {
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
    if (this.this$0.NotVisible == 0) {
      paramGraphics.drawImage(this.Img, 0, 0, this);
    }
  }
}
