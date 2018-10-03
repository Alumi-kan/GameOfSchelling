import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.lang.*;
import java.util.function.*;
import java.util.Collections;

public class GameofSchelling extends JFrame {

  //Eclipse等のIDE以外の場合は必要です
  private static final long serialVersionUID = 1234L;

  //いろいろなものの設定
  JPanel model = new Model();
  JPanel button = new JPanel();
  JButton start = new JButton("START");

  Graphics graphicsBuff;
  Image imageBuff;

  Thread thread = null;

  Random random = new Random();

  private static int windowX = 650;
  private static int windowY = 650;
  private static int gridSize = 10;
  private static int agent = 6000;   //エージェントの総数

  ArrayList<Ag> ag = new ArrayList<Ag>(); //エージェントを格納するリストクラス

  BiFunction<Integer, Integer, Integer> division = (n, g) -> {return n / g;};
  Function<Integer, Integer> randInt = (d) -> {return random.nextInt(d-1)+1;};  //乱数生成

  //メイン・メソッド
  public static void main(String[] args) {
    GameofSchelling gameOfSchelling = new GameofSchelling();
  }

  //メイン・クラスのコンストラクタ
  public GameofSchelling() {
    //Windowの呼び出し
    setSize(windowX+40, windowY+100);
    setTitle("シェリングのゲーム");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);
    setVisible(true);

    //バッファリング
    imageBuff = createImage(windowX+20, windowY+30);
    graphicsBuff = imageBuff.getGraphics();

    //コンテナの生成
    Container container = new Container();
    container = getContentPane();
    container.add(model, BorderLayout.CENTER);
    container.add(button, BorderLayout.SOUTH);

    //空間モデルのレイアウト
    model.setSize(windowX, windowY);
    model.setBackground(Color.white);
    model.setLayout(null);

    //buttonエリアのレイアウト
    button.setBorder(BorderFactory.createLineBorder(Color.black));
    button.setBackground(Color.white);
    button.add(start);

    //スレッドを起動するラムダ式
    start.addActionListener(event -> {
      if(event.getSource().equals(start)) {
        new Thread( () -> {
          while(true) {
            //例外処理
            try {thread.sleep(20);} catch(InterruptedException e) {e.printStackTrace();}

            //スレッド処理
            System.out.println("( '∀`)");
          }
        }).start();
      }
    });

    //エージェントの配置
    ArrayList<Integer> x = new ArrayList<Integer>();
    ArrayList<Integer> y = new ArrayList<Integer>();
    for(int i = 1; i <= division.apply(windowX, gridSize); i++) x.add(i);
    for(int i = 1; i <= division.apply(windowY, gridSize); i++) y.add(i);

    Collections.shuffle(x);
    Collections.shuffle(y);
    for (int i = 0; i < agent; i++) {
      Integer ix = x.get(random.nextInt(division.apply(windowX, gridSize)));
      Integer iy = y.get(random.nextInt(division.apply(windowY, gridSize)));
      ag.add(new Ag(random.nextInt(2), ix, iy));
    }

  }

  //エージェントの色
  public void colorList(int gj) {
    if(gj == 0) graphicsBuff.setColor(Color.red);
    else        graphicsBuff.setColor(Color.blue);
  }

  //エージェントのモデリング
  class Ag {

    //属性値i
    public int gj;

    //位置
    public int x;
    public int y;

    //Agのコンストラクタ
    public Ag(int i, int x, int y) {
      this.gj = i;
      this.x = x;
      this.y = y;
    }

    //エージェントの描画
    public void draw() {
      colorList(this.gj);
      graphicsBuff.fillRect(this.x*gridSize, this.y*gridSize, gridSize, gridSize);
    }

  }

  //空間のモデリング
  class Model extends JPanel {

    //Eclipse等のIDE以外の場合は必要です
    private static final long serialVersionUID = 1234L;

    //空間モデルのコンストラクタ
    public void init() {
      this.setSize(windowX, windowY);
    }

    //描画メソッド
    public void paint(Graphics graphics) {
      //描画
      super.paint(graphics);
      graphicsBuff.setColor(Color.white);
      graphicsBuff.fillRect(0, 0, windowX+20, windowY+20);

      for(Ag a : ag) a.draw();  //エージェントの描画
      gridDraw();               //グリッドの描画

      graphics.drawImage(imageBuff, 0, 0, this);
    }

    //格子の描画
    public void gridDraw() {
      graphicsBuff.setColor(Color.gray);
      for(int x = 1; x <= division.apply(windowX, gridSize); x++) {
        for(int y = 1; y <= division.apply(windowY, gridSize); y++) {
          graphicsBuff.drawRect(x*gridSize, y*gridSize, gridSize, gridSize);
        }
      }
    }

  }

}
