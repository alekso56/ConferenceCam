import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class Encrypt$LockImgListener
  implements MouseListener
{
  Encrypt$LockImgListener(Encrypt paramEncrypt) {}
  
  public void mouseReleased(MouseEvent paramMouseEvent) {}
  
  public void mouseClicked(MouseEvent paramMouseEvent)
  {
    switch (this.this$0.State)
    {
    case 0: 
      byte b = 1;
      if (this.this$0.OnlyStatus != 1) {
        this.this$0.CallEnableEnc(b);
      }
      break;
    case 1: 
      if (this.this$0.OnlyStatus != 1) {
        this.this$0.CallStartEnc();
      }
      break;
    }
  }
  
  public void mouseEntered(MouseEvent paramMouseEvent) {}
  
  public void mousePressed(MouseEvent paramMouseEvent) {}
  
  public void mouseExited(MouseEvent paramMouseEvent) {}
}
