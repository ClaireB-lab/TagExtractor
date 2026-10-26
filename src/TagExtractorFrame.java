import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

/// Claire Battelle
/// October 26, 2026
/// creates the frame for this class with buttons and display areas
public class TagExtractorFrame extends JFrame {

    private JLabel fileLabel;
    private JTextArea displayArea;
    private JButton loadStopWordsBtn;
    private JButton extractTagsBtn;
    private JButton saveTagsBtn;

    private Set<String> stopWords;
    private Map<String, Integer> tagMap;
    private File currentTextFile;

    /// keeps tags sorted alphabetically
    public TagExtractorFrame() {
        super("Tag / Keyword Extractor");
        stopWords = new HashSet<>();
        tagMap = new TreeMap<>();

        initComponents();
    }

    /// creates top header panel, center display area, bottom controls panel and action listeners
    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        fileLabel = new JLabel("Selected File: None", SwingConstants.CENTER);
        fileLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        topPanel.add(fileLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10),
                BorderFactory.createEtchedBorder()
        ));
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        loadStopWordsBtn = new JButton("1. Load Stop Words");
        extractTagsBtn = new JButton("2. Pick File & Extract Tags");
        saveTagsBtn = new JButton("3. Save Extracted Tags");

        extractTagsBtn.setEnabled(false); // Disabled until stop words are loaded
        saveTagsBtn.setEnabled(false);    // Disabled until tags are extracted

        bottomPanel.add(loadStopWordsBtn);
        bottomPanel.add(extractTagsBtn);
        bottomPanel.add(saveTagsBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        loadStopWordsBtn.addActionListener(e -> loadStopWords());
        extractTagsBtn.addActionListener(e -> processTextFile());
        saveTagsBtn.addActionListener(e -> saveTagsToFile());
    }

    /**
     * Loads stop words from a selected file into the stopWords Set
     */
    private void loadStopWords() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Stop Words File");
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            stopWords.clear();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String word = line.trim().toLowerCase();
                    if (!word.isEmpty()) {
                        stopWords.add(word);
                    }
                }
                JOptionPane.showMessageDialog(this,
                        "Loaded " + stopWords.size() + " stop words successfully.",
                        "Stop Words Loaded", JOptionPane.INFORMATION_MESSAGE);
                extractTagsBtn.setEnabled(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error reading stop words file: " + ex.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Reads text file, cleans non-letters, filters stop words, and tallies frequency
     */
    private void processTextFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Text File for Tag Extraction");
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            currentTextFile = chooser.getSelectedFile();
            fileLabel.setText("Selected File: " + currentTextFile.getName());
            tagMap.clear();

            try (Scanner scanner = new Scanner(currentTextFile)) {
                while (scanner.hasNext()) {
                    String rawWord = scanner.next();
                    String cleanWord = rawWord.replaceAll("[^a-zA-Z]", "").toLowerCase();

                    if (!cleanWord.isEmpty() && !stopWords.contains(cleanWord)) {
                        tagMap.put(cleanWord, tagMap.getOrDefault(cleanWord, 0) + 1);
                    }
                }

                updateDisplayArea();
                saveTagsBtn.setEnabled(true);

            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error opening text file: " + ex.getMessage(),
                        "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Formats and updates the JTextArea with extracted tags and counts
     */
    private void updateDisplayArea() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-25s %s\n", "TAG / KEYWORD", "FREQUENCY"));
        sb.append("----------------------------------------\n");

        for (Map.Entry<String, Integer> entry : tagMap.entrySet()) {
            sb.append(String.format("%-25s %d\n", entry.getKey(), entry.getValue()));
        }

        displayArea.setText(sb.toString());
        displayArea.setCaretPosition(0);
    }

    /**
     * Saves the tag counts displayed in the JTextArea to a file
     */
    private void saveTagsToFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Extracted Tags As");
        int result = chooser.showSaveDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File saveFile = chooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(saveFile)) {
                writer.print(displayArea.getText());
                JOptionPane.showMessageDialog(this,
                        "Tags successfully saved to " + saveFile.getName(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving file: " + ex.getMessage(),
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}