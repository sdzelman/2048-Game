import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Game3072 extends JPanel 
{
  private static final Color BG_COLOR = new Color(0xbbada0);
  private static final String FONT_NAME = "Comic Sans";
  private static final int TILE_SIZE = 64;
  private static final int TILES_MARGIN = 16;

  private Tile[] gametile;
  boolean winner = false;
  boolean loser = false;
  int totalScore = 0;

  public Game3072() 
  {
    setFocusable(true);
    addKeyListener(new KeyAdapter() 
    //sets keys for the movement in the game
    {
      
      public void keyPressed(KeyEvent keyinput) 
      {
        if (keyinput.getKeyCode() == KeyEvent.VK_ENTER) 
        {
          newGame();
        }
        if (!canMove()) 
        {
          loser = true;
        }

        if (!winner && !loser) 
        {
          switch (keyinput.getKeyCode()) 
          {
            case KeyEvent.VK_LEFT:
              left();
              break;
            case KeyEvent.VK_RIGHT:
              right();
              break;
            case KeyEvent.VK_DOWN:
              down();
              break;
            case KeyEvent.VK_UP:
              up();
              break;
          }
        }

        repaint();
      }
    });
    newGame();
  }

  public void newGame() 
  {
    totalScore = 0; //sets user score to 0
    winner = false;
    loser = false;
    gametile = new Tile[4 * 4]; //sets the game board
    
    for (int i = 0; i < gametile.length; i++) 
    {
      gametile[i] = new Tile();
    }
    addTile();
    addTile();
  }

  public void left() 
  {
    boolean needAddTile = false;
    for (int i = 0; i < 4; i++) 
    {
      Tile[] line = getLine(i);
      Tile[] merged = mergeLine(moveLine(line));
      setLine(i, merged);
      if (!needAddTile && !compare(line, merged)) 
      {
        needAddTile = true;
      }
    }

    if (needAddTile) 
    {
      addTile();
    }
  }

  public void right() 
  {
    gametile = rotate(180);
    left();
    gametile = rotate(180);
  }

  public void up() 
  {
    gametile = rotate(270);
    left();
    gametile = rotate(90);
  }

  public void down() 
  {
    gametile = rotate(90);
    left();
    gametile = rotate(270);
  }

  private Tile tileAt(int x, int y) 
  {
    return gametile[x + y * 4];
  }

  private void addTile() 
  {
    List<Tile> list = availableSpace();
    if (!availableSpace().isEmpty()) 
    {
      int index = (int) (Math.random() * list.size()) % list.size();
      Tile emptyTile = list.get(index);
      emptyTile.value = Math.random() < 0.9 ? 3 : 6;
    }
  }

  private List<Tile> availableSpace() 
  {
    final List<Tile> list = new ArrayList<Tile>(16);
    for (Tile t : gametile) 
    {
      if (t.isEmpty()) 
      {
        list.add(t);
      }
    }
    return list;
  }

  private boolean isFull() 
  {
    return availableSpace().size() == 0;
  }

  boolean canMove() 
  {
    if (!isFull()) 
    {
      return true;
    }
    for (int x = 0; x < 3; x++) 
    {
      for (int y = 0; y < 3; y++) 
      {
        Tile t = tileAt(x, y);
        if (t.value == tileAt(x + 1, y).value || t.value == tileAt(x, y + 1).value) 
        {
          return true;
        }
      }
    }
    return false;
  }

  private boolean compare(Tile[] line1, Tile[] line2) 
  {
    if (line1 == line2) 
    {
      return true;
    } 
    
    else if (line1.length != line2.length) 
    {
      return false;
    }

    for (int i = 0; i < line1.length; i++) 
    {
      if (!line1[i].equals(line2[i]))
        {
            return false;
        }
    }
    return true;
  }

  private Tile[] rotate(int angle) 
  {
    Tile[] newTiles = new Tile[4 * 4];
    int offsetX = 3, offsetY = 3;
    
    if (angle == 90) 
    {
      offsetY = 0;
    }
    
    else if (angle == 270) 
    {
      offsetX = 0;
    }

    double rad = Math.toRadians(angle);
    int cos = (int) Math.cos(rad);
    int sin = (int) Math.sin(rad);
    for (int x = 0; x < 4; x++) 
    {
      for (int y = 0; y < 4; y++) 
      {
        int newX = (x * cos) - (y * sin) + offsetX;
        int newY = (x * sin) + (y * cos) + offsetY;
        newTiles[(newX) + (newY) * 4] = tileAt(x, y);
      }
    }
    return newTiles;
  }

  private Tile[] moveLine(Tile[] initLine) 
  {
    LinkedList<Tile> l = new LinkedList<Tile>();
    for (int i = 0; i < 4; i++) 
    {
      if (!initLine[i].isEmpty())
        {
            l.addLast(initLine[i]);
        }
    }
    if (l.size() == 0) 
    {
      return initLine;
    } 
    
    else 
    {
      Tile[] finLine = new Tile[4];
      ensureSize(l, 4);
      for (int i = 0; i < 4; i++) 
      {
        finLine[i] = l.removeFirst();
      }
      return finLine;
    }
  }

  private Tile[] mergeLine(Tile[] initLine) 
  {
    LinkedList<Tile> list = new LinkedList<Tile>();
    for (int i = 0; i < 4 && !initLine[i].isEmpty(); i++) 
    {
      int num = initLine[i].value;
      if (i < 3 && initLine[i].value == initLine[i + 1].value) 
      {
        num *= 2;
        totalScore += num;
        int ourTarget = 3072;
        if (num == ourTarget) 
        {
          winner = true;
        }
        i++;
      }
      list.add(new Tile(num));
    }
    if (list.size() == 0) 
    {
      return initLine;
    }
    
    else
{
      ensureSize(list, 4);
      return list.toArray(new Tile[4]);
    }
  }

  private static void ensureSize(java.util.List<Tile> l, int s) {
    while (l.size() != s) {
      l.add(new Tile());
    }
  }

  private Tile[] getLine(int index) {
    Tile[] result = new Tile[4];
    for (int i = 0; i < 4; i++) {
      result[i] = tileAt(i, index);
    }
    return result;
  }

  private void setLine(int index, Tile[] re) {
    System.arraycopy(re, 0, gametile, index * 4, 4);
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    g.setColor(BG_COLOR);
    g.fillRect(0, 0, this.getSize().width, this.getSize().height);
    for (int y = 0; y < 4; y++) {
      for (int x = 0; x < 4; x++) {
        drawTile(g, gametile[x + y * 4], x, y);
      }
    }
  }

  private void drawTile(Graphics g2, Tile tile, int x, int y) {
    Graphics2D g = ((Graphics2D) g2);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
    int value = tile.value;
    int xOffset = offsetCoors(x);
    int yOffset = offsetCoors(y);
    g.setColor(tile.getBackground());
    g.fillRect(xOffset, yOffset, TILE_SIZE, TILE_SIZE);
    g.setColor(tile.getForeground());
    final int size = value < 100 ? 36 : value < 1000 ? 32 : 24;
    final Font font = new Font(FONT_NAME, Font.BOLD, size);
    g.setFont(font);

    String s = String.valueOf(value);
    final FontMetrics fm = getFontMetrics(font);

    final int w = fm.stringWidth(s);
    final int h = -(int) fm.getLineMetrics(s, g).getBaselineOffsets()[2];

    if (value != 0)
      g.drawString(s, xOffset + (TILE_SIZE - w) / 2, yOffset + TILE_SIZE - (TILE_SIZE - h) / 2 - 2);

    if (winner || loser) {
      g.setColor(new Color(255, 255, 255, 30));
      g.fillRect(0, 0, getWidth(), getHeight());
      g.setColor(new Color(78, 139, 202));
      g.setFont(new Font(FONT_NAME, Font.BOLD, 48));
      if (winner) {
        g.drawString("You won!", 68, 150);
      }
      if (loser) {
        g.drawString("You lost!", 65, 60);
      }
      if (winner || loser) {
        g.setFont(new Font(FONT_NAME, Font.PLAIN, 25));
        g.setColor(new Color(128, 128, 128, 128));
        g.drawString("Press ENTER to restart", 35, getHeight() - 270);
      }
    }
    g.setFont(new Font(FONT_NAME, Font.PLAIN, 18));
    g.drawString("Score: " + totalScore, 200, 355);
  }

  private static int offsetCoors(int arg) {
    return arg * (TILES_MARGIN + TILE_SIZE) + TILES_MARGIN;
  }

  static class Tile {
    int value;
    
    public Tile() {
      this(0);
    }

    public Tile(int num) {
      value = num;
    }

    public boolean isEmpty() {
      return value == 0;
    }

    public Color getForeground() {
      return value < 3072 ? new Color(0x626262) :  new Color(0xd7d7d7);
    }

    public Color getBackground() {
      switch (value) {
        case 3:    return new Color(0xf9ad81);
        case 6:    return new Color(0xfdc68a);
        case 12:    return new Color(0xfff79a);
        case 24:   return new Color(0xc4df9b);
        case 48:   return new Color(0xa2d39c);
        case 96:   return new Color(0x7bcdc8);
        case 192:  return new Color(0x8493ca);
        case 384:  return new Color(0xa187be);
        case 768:  return new Color(0xbc8dbf);
        case 1536: return new Color(0xf6989d);
        case 3072: return new Color(0x000000);
      }
      return new Color(0xcdc1b4);
    }
  }

  public static void main(String[] args) {
    JFrame game = new JFrame();
    game.setTitle("APCS 3072");
    game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    game.setSize(340, 400);
    game.setResizable(false);

    game.add(new Game3072());

    game.setLocationRelativeTo(null);
    game.setVisible(true);
  }
}