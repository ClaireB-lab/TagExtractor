import javax.swing.SwingUtilities;

/// Claire Battelle
/// October 26, 2026
/// Runs the TagExtractorFrame class
public class TagExtractorRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TagExtractorFrame frame = new TagExtractorFrame();
            frame.setVisible(true);
        });
    }
}