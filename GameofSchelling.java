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
  private static int agent = 2000;   //エージェントの総数
  private static int rgj = 0;
  private static int bgj = 0;
  private static double theta = 1 / 3;  //閾値の計算

  ArrayList<Ag> ag = new ArrayList<Ag>(); //エージェントを格納するリストクラス
  ArrayList<ArrayList<Integer>> pi = new ArrayList<ArrayList<Integer>>(); //格子のデータ

  BiFunction<Integer, Integer, Integer> division = (n, g) -> {return n / g;};
  Function<Integer, Integer> randInt = (d) -> {return random.nextInt(d-1)+1;};  //乱数生成

  //メイン・メソッド
  public static void main(String[] args) {
    GameofSchelling gameOfSchelling = new GameofSchelling();
  }

  //メイン・クラスのコンストラクタ
  public GameofSchelling() {
    //Windowの呼び出し
    setSize(windowX+30, windowY+90);
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

    //格子空間の属性値を初期化
    for(int i = 0; i < division.apply(windowX, gridSize)+1; i++) {
      pi.add(new ArrayList<Integer>());
      for(int j = 0; j < division.apply(windowY, gridSize)+1; j++) {
        pi.get(i).add(0);
      }
    }

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
      int gj = random.nextInt(2)+1;
      if(gj == 1) rgj++;
      if(gj == 2) bgj++;
      ag.add(new Ag(gj, ix, iy));
    }

    //属性値の格納
    for(Ag agj : ag) pi.get(agj.x).set(agj.y, agj.gj);

  }

  //エージェントの色
  public void colorList(int gj) {
    if(gj == 1) graphicsBuff.setColor(Color.red);
    if(gj == 2) graphicsBuff.setColor(Color.blue);
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

    //x軸の値セット
    public void setterX(int x) {
      this.x = x;
    }

    //y軸の値セット
    public void setterY(int y) {
      this.y = y;
    }

  }

  //シェリングのゲームのアルゴリズム
  public void schellingAlgorithm(int x, int y, int gj) {
    //エージェントの属性値に合わせて合計属性値を決定する
    int agj = 0, wgj = 0, sk = 0;
    if (gj == 1)  agj = rgj;
    else          agj = bgj;

    //8近傍の属性値の合計を計算する
    if(pi.get(x).get(y-1) == gj && (y-1) < 0) wgj += pi.get(x).get(y-1);
    if(pi.get(x+1).get(y-1) == gj && (x+1) > division.apply(windowX, gridSize) && (y-1) < 0) wgj += pi.get(x+1).get(y-1);
    if(pi.get(x+1).get(y) == gj && (x+1) > division.apply(windowX, gridSize)) wgj += pi.get(x+1).get(y);
    if(pi.get(x+1).get(y+1) == gj && (x+1) > division.apply(windowX, gridSize) && (y+1) > division.apply(windowY, gridSize)) wgj += pi.get(x+1).get(y+1);
    if(pi.get(x).get(y+1) == gj && (y+1) > division.apply(windowY, gridSize)) wgj += pi.get(x).get(y+1);
    if(pi.get(x-1).get(y+1) == gj && (x-1) < 0 && (y+1) > division.apply(windowY, gridSize)) wgj += pi.get(x-1).get(y+1);
    if(pi.get(x-1).get(y) == gj && (x-1) < 0) wgj += pi.get(x-1).get(y);
    if(pi.get(x-1).get(y-1) == gj && (x-1) < 0 && (y-1) < 0) wgj += pi.get(x-1).get(y-1);

    //周辺近傍の属性値gjを計算し「好ましさ」skを計算する
    sk = division.apply(wgj, agj);

    //skが閾値以下か以上かで行動するかしないかを決定する
    if(theta < sk) {

    } else {
      
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
      graphicsBuff.fillRect(0, 0, windowX+20, windowY+30);

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
