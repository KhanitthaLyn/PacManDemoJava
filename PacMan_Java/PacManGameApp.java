import javax.swing.JFrame;

public class PacManGameApp {
    public static void main(String[] args) {
        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;

        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize + 50; // add space for status bar

        JFrame frame = new JFrame("Pac Man Demo");
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PacManPanel gamePanel = new PacManPanel();
        frame.add(gamePanel);
        frame.pack();

        gamePanel.requestFocus();
        frame.setVisible(true);
    }
}
