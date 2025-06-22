import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

public class PacManPanel extends JPanel implements ActionListener, KeyListener {

    class Block {
        int x, y, width, height;
        Image img;
        int startX, startY;
        char dir = 'U';
        int velX = 0, velY = 0;

        Block(Image img, int x, int y, int width, int height) {
            this.img = img;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDir(char d) {
            dir = d;
            int s = tileSize / 4;
            velX = (dir == 'L') ? -s : (dir == 'R') ? s : 0;
            velY = (dir == 'U') ? -s : (dir == 'D') ? s : 0;
        }

        void reset() {
            x = startX;
            y = startY;
            velX = 0;
            velY = 0;
        }

        boolean isCentered() {
            return x % tileSize == 0 && y % tileSize == 0;
        }
    }

    private final int rowCount = 21, columnCount = 19, tileSize = 32;
    private final int boardW = columnCount * tileSize, boardH = rowCount * tileSize;

    private Image wallImg, blueGhost, orangeGhost, pinkGhost, redGhost;
    private Image pacUp, pacDown, pacLeft, pacRight, cherryImg;

    private final String[] map = {
        "XXXXXXXXXXXXXXXXXXX",
        "X        X        X",
        "X XXXX X X XXXX X X",
        "X    X X X X    X X",
        "X XX X XXX X XX X X",
        "X XX X     X XX X X",
        "X    X XXXXX X    X",
        "XXXX X       X XXXX",
        "X                 X",
        "X b   XXXPXXX   o X",
        "X    r         p  X",
        "XXXX X       X XXXX",
        "X    X XXXXX X    X",
        "X XX X     X XX X X",
        "X XX X XXX X XX X X",
        "X    X X X X    X X",
        "X XXXX X X XXXX X X",
        "X        X        X",
        "X XXXXXXXXXXXXXXX X",
        "X                 X",
        "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls = new HashSet<>();
    HashSet<Block> foods = new HashSet<>();
    HashSet<Block> ghosts = new HashSet<>();
    Block pacman;
    Block cherry;

    Timer gameTimer;
    char[] dirs = {'U', 'D', 'L', 'R'};
    Random rand = new Random();

    int score = 0, lives = 3;
    boolean gameOver = false;
    long startTime;

    public PacManPanel() {
        setPreferredSize(new Dimension(boardW, boardH));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        loadResources();
        loadMap();
        resetPositions();

        startTime = System.currentTimeMillis();
        gameTimer = new Timer(40, this);
        gameTimer.start();
    }

    private void loadResources() {
        wallImg = new ImageIcon(getClass().getResource("./pic/wall.png")).getImage();
        blueGhost = new ImageIcon(getClass().getResource("./pic/blueGhost.png")).getImage();
        orangeGhost = new ImageIcon(getClass().getResource("./pic/orangeGhost.png")).getImage();
        pinkGhost = new ImageIcon(getClass().getResource("./pic/pinkGhost.png")).getImage();
        redGhost = new ImageIcon(getClass().getResource("./pic/redGhost.png")).getImage();
        pacUp = new ImageIcon(getClass().getResource("./pic/pacmanUp.png")).getImage();
        pacDown = new ImageIcon(getClass().getResource("./pic/pacmanDown.png")).getImage();
        pacLeft = new ImageIcon(getClass().getResource("./pic/pacmanLeft.png")).getImage();
        pacRight = new ImageIcon(getClass().getResource("./pic/pacmanRight.png")).getImage();
        cherryImg = new ImageIcon(getClass().getResource("./pic/cherry.png")).getImage();
    }

    private void loadMap() {
        walls.clear();
        foods.clear();
        ghosts.clear();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char ch = map[r].charAt(c);
                int x = c * tileSize, y = r * tileSize;

                if (ch == 'X') walls.add(new Block(wallImg, x, y, tileSize, tileSize));
                else if (ch == 'b') ghosts.add(new Block(blueGhost, x, y, tileSize, tileSize));
                else if (ch == 'o') ghosts.add(new Block(orangeGhost, x, y, tileSize, tileSize));
                else if (ch == 'p') ghosts.add(new Block(pinkGhost, x, y, tileSize, tileSize));
                else if (ch == 'r') ghosts.add(new Block(redGhost, x, y, tileSize, tileSize));
                else if (ch == 'P') pacman = new Block(pacRight, x, y, tileSize, tileSize);
                else if (ch == ' ') foods.add(new Block(null, x + 14, y + 14, 4, 4));
            }
        }
        cherry = new Block(cherryImg, tileSize * 9, tileSize * 17, tileSize, tileSize);
        cherry.updateDir(dirs[rand.nextInt(4)]);
    }

    private boolean isWall(int x, int y) {
        Rectangle rect = new Rectangle(x, y, tileSize, tileSize);
        for (Block w : walls) if (rect.intersects(new Rectangle(w.x, w.y, w.width, w.height))) return true;
        return false;
    }

    private List<Character> getValidDirs(Block b) {
        List<Character> valid = new ArrayList<>();
        for (char d : dirs) {
            int nx = b.x + ((d == 'L') ? -tileSize : (d == 'R') ? tileSize : 0);
            int ny = b.y + ((d == 'U') ? -tileSize : (d == 'D') ? tileSize : 0);
            if (!isWall(nx, ny)) valid.add(d);
        }
        return valid;
    }

    private void moveGhost(Block g) {
        if (g.isCentered()) {
            List<Character> valid = getValidDirs(g);
            if (!valid.isEmpty()) g.updateDir(valid.get(rand.nextInt(valid.size())));
        }
        g.x += g.velX;
        g.y += g.velY;
    }

    private void moveCherry() {
        int cherrySpeed = tileSize / 8; // slower
        cherry.x += cherry.dirX * cherrySpeed;
        cherry.y += cherry.dirY * cherrySpeed;

        if (cherry.isCentered()) {
            List<Character> valid = getValidDirs(cherry);
            if (!valid.isEmpty()) cherry.updateDir(valid.get(rand.nextInt(valid.size())));
        }
        cherry.x += cherry.velX / 2; 
        cherry.y += cherry.velY / 2;

                if (collision(pacman, cherry)) {
            score += 50;
            cherry.x = rand.nextInt(columnCount - 4) * tileSize + tileSize;
            cherry.y = rand.nextInt(rowCount - 4) * tileSize + tileSize;
            cherry.dirX = rand.nextBoolean() ? 1 : -1;
            cherry.dirY = rand.nextBoolean() ? 1 : -1;
    }

    private void move() {
        pacman.x += pacman.velX;
        pacman.y += pacman.velY;
        for (Block w : walls) {
            if (collision(pacman, w)) {
                pacman.x -= pacman.velX;
                pacman.y -= pacman.velY;
                break;
            }
        }

        for (Block g : ghosts) {
            if (collision(g, pacman)) {
                if (--lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
                return;
            }
            moveGhost(g);
        }

        moveCherry();

        Block eaten = null;
        for (Block f : foods) {
            if (collision(pacman, f)) {
                eaten = f;
                score += 10;
            }
        }
        if (eaten != null) foods.remove(eaten);

        if (collision(pacman, cherry)) {
            score += 50;
        }

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    private boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private void resetPositions() {
        pacman.reset();
        for (Block g : ghosts) g.reset();
        cherry.x = tileSize * 9;
        cherry.y = tileSize * 17;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(pacman.img, pacman.x, pacman.y, pacman.width, pacman.height, null);
        for (Block ghost : ghosts) g.drawImage(ghost.img, ghost.x, ghost.y, ghost.width, ghost.height, null);
        for (Block wall : walls) g.drawImage(wall.img, wall.x, wall.y, wall.width, wall.height, null);
        g.setColor(Color.WHITE);
        for (Block f : foods) g.fillRect(f.x, f.y, f.width, f.height);
        g.drawImage(cherry.img, cherry.x, cherry.y, cherry.width, cherry.height, null);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        long elapsedSec = (System.currentTimeMillis() - startTime) / 1000;
        if (gameOver) g.drawString("Game Over! Score: " + score, tileSize, tileSize);
        else g.drawString("x" + lives + " Score: " + score + "  Time: " + elapsedSec + "s", tileSize, tileSize);
    }

    @Override public void actionPerformed(ActionEvent e) {
        move(); repaint(); if (gameOver) gameTimer.stop();
    }
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap(); resetPositions(); lives = 3; score = 0;
            gameOver = false; startTime = System.currentTimeMillis();
            gameTimer.start();
        }
        if (e.getKeyCode() == KeyEvent.VK_UP) pacman.updateDir('U');
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) pacman.updateDir('D');
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) pacman.updateDir('L');
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) pacman.updateDir('R');
        if (pacman.dir == 'U') pacman.img = pacUp;
        else if (pacman.dir == 'D') pacman.img = pacDown;
        else if (pacman.dir == 'L') pacman.img = pacLeft;
        else if (pacman.dir == 'R') pacman.img = pacRight;
    }
}