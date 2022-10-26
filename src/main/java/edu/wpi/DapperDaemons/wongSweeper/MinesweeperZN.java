package edu.wpi.DapperDaemons.wongSweeper;
/**
 *
 *
 * <h1>MinesweeperZN</h1>
 *
 * MinesweeperZN is a modern day recreation of Microsoft's Minesweeper program.
 *
 * @author Robert Zink
 * @version 1.01
 * @since 2017-08-24
 */
import edu.wpi.DapperDaemons.backend.SoundPlayer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MinesweeperZN extends Application {

  private final int TILE_SIZE = (int) Screen.getPrimary().getVisualBounds().getHeight() / 30;
  private final int WIDTH = (int) (Screen.getPrimary().getVisualBounds().getWidth() * 0.50);
  private final int HEIGHT = (int) (Screen.getPrimary().getVisualBounds().getHeight() * 0.60);

  private int X_TILES = 15;
  private int Y_TILES = 15;

  private double difficulty = 0.2;

  private Tile[][] grid;
  private BorderPane window;
  private Text bottomText;
  private final Image mine = new Image("mine.png");
  private final Image flag = new Image("flag.png");
  private final Image good = new Image("cool_guy.png");
  private final Image bad = new Image("dead_guy.png");
  private ImageView guy;

  /**
   * The createContent method is used to create the tile grid and display it in the application
   * window.
   *
   * @return A parent node containing the game grid
   */
  private Parent createContent() {
    GridPane root = new GridPane();
    grid = new Tile[X_TILES][Y_TILES];

    for (int y = 0; y < Y_TILES; y++) {
      for (int x = 0; x < X_TILES; x++) {
        Tile tile = new Tile(x, y, Math.random() < difficulty);

        grid[x][y] = tile;
        root.add(tile, x, y);
      }
    }

    for (int y = 0; y < Y_TILES; y++) {
      for (int x = 0; x < X_TILES; x++) {
        Tile tile = grid[x][y];

        if (tile.hasBomb) continue;

        long bombs = getNeighbors(tile).stream().filter(t -> t.hasBomb).count();

        if (bombs > 0) {
          tile.text.setText(String.valueOf(bombs));

          switch ((int) bombs) {
            case 1:
              tile.text.setFill(Color.BLUE);
              break;
            case 2:
              tile.text.setFill(Color.GREEN);
              break;
            case 3:
              tile.text.setFill(Color.RED);
              break;
            case 4:
              tile.text.setFill(Color.PURPLE);
              break;
            case 5:
              tile.text.setFill(Color.MAROON);
              tile.text.setId("manyNeighbors");
              break;
            case 6:
              tile.text.setFill(Color.DARKTURQUOISE);
              tile.text.setId("manyNeighbors");
              break;
            case 7:
              tile.text.setFill(Color.GRAY);
              tile.text.setId("manyNeighbors");
              break;
            case 8:
              tile.text.setFill(Color.BLACK);
              tile.text.setId("manyNeighbors");
              break;
            default:
              break;
          }
        }
      }
    }

    root.setHgap(1);
    root.setVgap(1);
    root.setAlignment(Pos.CENTER);
    root.setPadding(new Insets(20, 20, 20, 20));
    root.setGridLinesVisible(false);

    return root;
  }

  /**
   * The getNeighbors method is called to find the neighbors of a given tile in the game grid.
   *
   * @param tile The tile to find the neighbors for.
   * @return A list of the neighbors to the current tile.
   */
  private List<Tile> getNeighbors(Tile tile) {
    List<Tile> neighbors = new ArrayList<>();

    int[] points =
        new int[] {
          -1, -1,
          -1, 0,
          -1, 1,
          0, -1,
          0, 1,
          1, -1,
          1, 0,
          1, 1
        };

    for (int i = 0; i < points.length; i++) {
      int dx = points[i];
      int dy = points[++i];

      int newX = tile.x + dx;
      int newY = tile.y + dy;

      if (newX >= 0 && newX < X_TILES && newY >= 0 && newY < Y_TILES) {
        neighbors.add(grid[newX][newY]);
      }
    }

    return neighbors;
  }

  @Override
  public void start(Stage primaryStage) throws Exception {}

  /** The tile class defines tiles in the game grid. */
  private class Tile extends StackPane {
    private final int x, y;
    private final boolean hasBomb;
    private boolean flagged = false;
    private boolean isOpen = false;
    private final ImageView tileImage = new ImageView();

    private final Rectangle border = new Rectangle(TILE_SIZE - 2, TILE_SIZE - 2);
    private final Text text = new Text();

    /**
     * The Tile constructor creates a tile in the game grid.
     *
     * @param x The x coordinate of the created tile
     * @param y The y coordinate of the created tile
     * @param hasBomb Whether or not the tile has a mine
     */
    public Tile(int x, int y, boolean hasBomb) {
      this.x = x;
      this.y = y;
      this.hasBomb = hasBomb;

      tileImage.setFitHeight(TILE_SIZE);
      tileImage.setFitWidth(TILE_SIZE);

      if (hasBomb) {
        tileImage.setImage(mine);
      }

      border.setFill(Color.GRAY);
      border.setStroke(Color.BLACK);

      text.setFont(Font.font(18));
      text.setText(hasBomb ? "X" : "");
      text.setVisible(false);

      tileImage.setVisible(false);

      getChildren().addAll(border, text, tileImage);

      setOnMouseClicked(
          event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
              if (!flagged) open();
              checkForWin();
            } else if (event.getButton() == MouseButton.SECONDARY) {
              if (!flagged) {
                if (!isOpen) {
                  flagged = true;
                  border.setFill(Color.AQUAMARINE);
                  tileImage.setImage(flag);
                  tileImage.setVisible(true);
                  checkForWin();
                }
              } else {
                if (!isOpen) {
                  flagged = false;
                  border.setFill(Color.GRAY);
                  tileImage.setVisible(false);

                  if (hasBomb) {
                    tileImage.setImage(mine);
                  }
                }
              }
            }
          });
    }

    /** The open method handles the revealing of tiles in the game grid. */
    public void open() {
      if (isOpen) return;

      if (hasBomb) {
        tileImage.setVisible(true);
        border.setFill(Color.LIGHTSALMON);
        // System.out.println("Game Over"); // Used for debugging
        revealAllBombs();
        bottomText.setText("BOOM! You lose.");
        bottomText.setVisible(true);
        return;
      }

      isOpen = true;
      text.setVisible(true);
      border.setFill(Color.LIGHTGRAY);

      if (text.getText().isEmpty()) {
        getNeighbors(this).forEach(Tile::open);
      }
    }
  }

  /**
   * The revealAllBombs method, invoked on a loss, reveals the location of all the mines in the
   * grid. Additionally, it will set the emoji to a sad face.
   */
  private void revealAllBombs() {
    for (int y = 0; y < Y_TILES; y++) {
      for (int x = 0; x < X_TILES; x++) {
        if (grid[x][y].hasBomb) {
          grid[x][y].tileImage.setImage(mine);
          grid[x][y].tileImage.setVisible(true);
        }
      }
    }

    guy.setImage(bad);
  }

  /**
   * The checkForWin method sets the win text for the game if all mines are flagged and all other
   * tiles are open.
   */
  private void checkForWin() {
    for (int y = 0; y < Y_TILES; y++) {
      for (int x = 0; x < X_TILES; x++) {
        if (grid[x][y].hasBomb && grid[x][y].flagged) continue;
        if (grid[x][y].isOpen) continue;
        else return;
      }
    }

    bottomText.setText("Yay! You win!");
    bottomText.setVisible(true);
  }

  /**
   * The setNumTiles method changes the size of the game grid.
   *
   * @param numTiles The number of tiles in the X and Y coordinates
   */
  private void setNumTiles(int numTiles) {
    X_TILES = numTiles;
    Y_TILES = numTiles;
    resetGrid();
  }

  /**
   * The setDifficulty method changes the difficulty of the game by altering the rate at which mines
   * appear.
   *
   * @param mineRate The rate of mine formation.
   */
  private void setDifficulty(double mineRate) {
    difficulty = mineRate;
    resetGrid();
  }

  /** The resetGrid method resets the game grid with currently defined settings. */
  private void resetGrid() {
    window.setCenter(createContent());
    guy.setImage(good);
    bottomText.setVisible(false);
  }

  /** The getAboutScreen method is used to create a screen giving information about the game. */
  private void getAboutScreen() {
    GridPane aboutGrid = new GridPane();
    aboutGrid.setAlignment(Pos.CENTER);

    VBox aboutBox = new VBox();
    aboutBox.setAlignment(Pos.TOP_LEFT);
    try {
      InputStream in = ClassLoader.getSystemResourceAsStream("wongSweeperREADME.txt");
      BufferedReader aboutReader = new BufferedReader(new InputStreamReader(in));
      List<Text> aboutTextArray = new ArrayList<>();
      String line;
      while ((line = aboutReader.readLine()) != null) {
        aboutTextArray.add(new Text(line));
      }
      for (Text text : aboutTextArray) {
        text.setId("#aboutText");
        aboutBox.getChildren().add(text);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    ScrollPane scrollPane = new ScrollPane(aboutBox);
    scrollPane.setFitToHeight(true);

    BorderPane root = new BorderPane(scrollPane);
    root.setPadding(new Insets(15));
    root.getChildren().add(aboutBox);

    window.setCenter(root);
  }

  /**
   * The start method sets up the application.
   *
   * @param stage The application stage.
   * @throws Exception Exceptions should not be thrown.
   */
  public void begin(Stage stage) throws Exception {
    SoundPlayer player = new SoundPlayer("edu/wpi/DapperDaemons/assets/Smash.wav");
    player.play(0.75F);

    stage.setMinWidth(Screen.getPrimary().getVisualBounds().getWidth() * 0.50);
    stage.setMinHeight(Screen.getPrimary().getVisualBounds().getHeight() * 0.65);

    window = new BorderPane();
    window.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));

    MenuBar menuBar = new MenuBar();
    Menu options = new Menu("Options");
    MenuItem resetMenu = new MenuItem("Reset Grid");
    resetMenu.setOnAction(e -> resetGrid());
    MenuItem setSmallGrid = new MenuItem("Small Grid");
    setSmallGrid.setOnAction(e -> setNumTiles(7));
    MenuItem setMedGrid = new MenuItem("Medium Grid");
    setMedGrid.setOnAction(e -> setNumTiles(11));
    MenuItem setLrgGrid = new MenuItem("Large Grid");
    setLrgGrid.setOnAction(e -> setNumTiles(15));
    MenuItem setEasyDifficulty = new MenuItem("Easy Mode");
    setEasyDifficulty.setOnAction(e -> setDifficulty(0.08));
    MenuItem setNormalDifficulty = new MenuItem("Normal Mode");
    setNormalDifficulty.setOnAction(e -> setDifficulty(0.20));
    MenuItem setHardDifficulty = new MenuItem("Hard Mode");
    setHardDifficulty.setOnAction(e -> setDifficulty(0.35));
    options
        .getItems()
        .addAll(
            resetMenu,
            setSmallGrid,
            setMedGrid,
            setLrgGrid,
            setEasyDifficulty,
            setNormalDifficulty,
            setHardDifficulty);

    Menu aboutMenu = new Menu("About");
    MenuItem aboutItem = new MenuItem("About Wongsweeper");
    aboutItem.setOnAction(e -> getAboutScreen());
    aboutMenu.getItems().addAll(aboutItem);
    menuBar.getMenus().addAll(options, aboutMenu);

    VBox vBox = new VBox(10);
    vBox.setAlignment(Pos.CENTER);
    vBox.setPadding(new Insets(0, 50, 0, 0));

    Text logoText = new Text("Wongsweeper");
    logoText.setFont(Font.font("Courier New", FontWeight.EXTRA_BOLD, 30));
    logoText.setFill(Color.BLACK);
    logoText.setVisible(true);

    Text versionText = new Text("Version 1.01");
    versionText.setFont(Font.font("Courier New", FontPosture.ITALIC, 12));
    versionText.setFill(Color.BLACK);
    versionText.setVisible(true);

    Button reset = new Button("Reset Grid");
    reset.setOnMouseClicked(e -> resetGrid());
    guy = new ImageView(good);
    guy.setFitWidth(200);
    guy.setFitHeight(200);
    guy.setVisible(true);
    vBox.getChildren().addAll(logoText, versionText, reset, guy);

    HBox hBox = new HBox();
    bottomText = new Text();
    bottomText.setVisible(false);
    hBox.getChildren().add(bottomText);

    window.setTop(menuBar);
    window.setRight(vBox);
    window.setCenter(createContent());
    window.setBottom(hBox);

    Scene scene = new Scene(window, WIDTH, HEIGHT);

    scene
        .getStylesheets()
        .add(
            MinesweeperZN.class.getClassLoader().getResource("MinesweeperZN.css").toExternalForm());

    stage.setScene(scene);
    stage.setTitle("Wongsweeper");
    stage.show();

    stage.setOnCloseRequest(e -> player.stop());
  }
}
