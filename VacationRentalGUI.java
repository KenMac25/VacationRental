import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class VacationRentalGUI extends JFrame 
{
    // vacay data
    private String[] vacayDestinations = 
    {
        "Disney",
        "Savannah, Georgia",
        "North Georgia Mountain Resort",
        "San Francisco Mountain Resort",
        "Dubai Desert Resort"
    };
    private String[] vacayDescriptions = 
    {
        "Enjoy Mickey and pals with the family in the happiest place on earth.",
        "Bustling riverside entertainment, shopping, and historic charm.",
        "Cozy cabin with breathtaking wilderness views in North Georgia.",
        "Escape city life and breathe fresh California mountain air.",
        "Serene luxury among the soft dunes of the Dubai desert."
    };
    private String[] vacayImagePaths = 
    { 
        "Images/Disney.jpg",
        "Images/Savannah.jpg",
        "Images/Mountains2.jpg",
        "Images/Mountains.jpg",
        "Images/Dunes.jpg"
    };
    private double[] bedroomRates  = {100, 120, 150, 175, 200};
    private double[] bathroomRates = { 50,  60,  70,  80,  90};
    private double[] baseFees      = {100, 150, 200, 250, 300};

    private JComboBox<String> comboVacayDestination;
    private JLabel labelImage;
    private JTextArea textAreaDescription;
    private JSpinner spinnerBedrooms, spinnerBathrooms, spinnerGuests, spinnerNights, spinnerPets;
    private JCheckBox checkboxPets;
    private JTextArea textAreaBill;
    private Clip  clipBackgroundMusic;

    private ImageIcon[] iconsVacay = new ImageIcon[5];

    public VacationRentalGUI()
        throws UnsupportedAudioFileException,
               IOException,
               LineUnavailableException
    {
        super("Chill and Real Vacays");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        // gradient background panel
        JPanel background = new JPanel() 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(35, 35, 55),
                    0, getHeight(), new Color(55, 35, 55)
                );
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setLayout(new BorderLayout(10, 10));
        setContentPane(background);

        // vacay selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setOpaque(false);
        topPanel.add(new JLabel("Choose your chill getaway:"));
        comboVacayDestination = new JComboBox<>(vacayDestinations);
        topPanel.add(comboVacayDestination);
        background.add(topPanel, BorderLayout.NORTH);

        labelImage = new JLabel();
        labelImage.setHorizontalAlignment(JLabel.CENTER);

        textAreaDescription = new JTextArea(3, 25);
        textAreaDescription.setLineWrap(true);
        textAreaDescription.setWrapStyleWord(true);
        textAreaDescription.setEditable(false);
        textAreaDescription.setOpaque(true);
        textAreaDescription.setBackground(new Color(40, 40, 50, 220));

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        leftPanel.add(labelImage, BorderLayout.CENTER);
        leftPanel.add(textAreaDescription, BorderLayout.SOUTH);
        background.add(leftPanel, BorderLayout.WEST);

        // scale images
        for (int i = 0; i < vacayImagePaths.length; i++) 
        {
            ImageIcon icon = new ImageIcon(vacayImagePaths[i]);
            Image img = icon.getImage()
                            .getScaledInstance(350, 225, Image.SCALE_SMOOTH);
            iconsVacay[i] = new ImageIcon(img);
        }
        // initial vacay
        labelImage.setIcon(iconsVacay[0]);
        textAreaDescription.setText(vacayDescriptions[0]);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        formPanel.setOpaque(false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        spinnerBedrooms = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        spinnerBathrooms = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
        spinnerGuests = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        spinnerNights = new JSpinner(new SpinnerNumberModel(1, 1, 30, 1));
        checkboxPets = new JCheckBox("Traveling with pets?");
        spinnerPets = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        spinnerPets.setEnabled(false);

        // pet count only when checkbox checked
        checkboxPets.addItemListener(e -> 
        {
            boolean sel = checkboxPets.isSelected();
            spinnerPets.setEnabled(sel);
            if (!sel) spinnerPets.setValue(0);
        });

        formPanel.add(new JLabel("Bedrooms needed:")); formPanel.add(spinnerBedrooms);
        formPanel.add(new JLabel("Bathrooms needed:")); formPanel.add(spinnerBathrooms);
        formPanel.add(new JLabel("Number of travelers:")); formPanel.add(spinnerGuests);
        formPanel.add(new JLabel("How many nights:")); formPanel.add(spinnerNights);
        formPanel.add(checkboxPets); formPanel.add(spinnerPets);

        JButton btnGenerateBill = new JButton("Generate Chill Bill");
        formPanel.add(new JLabel());
        formPanel.add(btnGenerateBill);
        background.add(formPanel, BorderLayout.EAST);

        // bill
        textAreaBill = new JTextArea(10, 60);
        textAreaBill.setEditable(false);
        textAreaBill.setOpaque(true);
        textAreaBill.setBackground(new Color(40, 40, 50, 220));
        background.add(new JScrollPane(textAreaBill), BorderLayout.SOUTH);

        comboVacayDestination.addActionListener(e -> updateDisplay());
        btnGenerateBill.addActionListener(e -> calculateBill());

        loadAndPlayMusic("Music/Background.wav");

        setVisible(true);
    }

    private void updateDisplay() 
    {
        int idx = comboVacayDestination.getSelectedIndex();
        labelImage.setIcon(iconsVacay[idx]);
        textAreaDescription.setText(vacayDescriptions[idx]);
    }

    private void calculateBill() 
    {
        int idx = comboVacayDestination.getSelectedIndex();
        int beds = (Integer) spinnerBedrooms.getValue();
        int baths = (Integer) spinnerBathrooms.getValue();
        int guests = (Integer) spinnerGuests.getValue();
        int nights = (Integer) spinnerNights.getValue();
        int pets = checkboxPets.isSelected() ? (Integer) spinnerPets.getValue() : 0;

        int capacity = beds * 2 + 2;
        if (guests > capacity) 
        {
            JOptionPane.showMessageDialog
            (
                this,
                "Sorry, Too many guests! Max allowed: " + capacity,
                "Capacity Exceeded",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        double costBeds    = bedroomRates[idx]  * beds  * nights;
        double costBaths   = bathroomRates[idx] * baths * nights;
        double costBase    = baseFees[idx];
        double costPets    = pets * 15.0 * nights;
        double cleaningFee = 100.0;
        double subtotal    = costBeds + costBaths + costBase + costPets;
        double serviceFee  = subtotal * 0.05;
        double taxes       = subtotal * 0.10;
        double total       = subtotal + cleaningFee + serviceFee + taxes;

        DecimalFormat fmt = new DecimalFormat("#,##0.00");
        String bill = "";
        bill += "Chill Rental Bill \n";
        bill += "Destination: " + vacayDestinations[idx] + "\n";
        bill += "Nights: " + nights + "\n\n";
        bill += "Bedrooms ("+beds+") $"+fmt.format(bedroomRates[idx])+" per night\n";
        bill += "Bathrooms("+baths+") $"+fmt.format(bathroomRates[idx])+" per night\n";
        bill += "Base Fee: $"+fmt.format(costBase)+"\n";
        if (pets > 0) 
        {
            bill += "Pet Fee ("+pets+") $15 per night: $"+fmt.format(costPets)+"\n";
        }
        bill += "Cleaning Fee: $"+fmt.format(cleaningFee)+"\n";
        bill += "Service (5%): $"+fmt.format(serviceFee)+"\n";
        bill += "Taxes (10%): $"+fmt.format(taxes)+"\n";
        bill += "=============================== \n";
        bill += "TOTAL: $"+fmt.format(total)+"\n";

        textAreaBill.setText(bill);
    }

    private void loadAndPlayMusic(String path)
        throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        AudioInputStream ais = AudioSystem.getAudioInputStream(new File(path));
        clipBackgroundMusic = AudioSystem.getClip();
        clipBackgroundMusic.open(ais);
        clipBackgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public static void main(String[] args) 
    {
        // Nimbus L&F + custom palette
        try 
        {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
            UIManager.put("control", new Color(30, 30, 30));
            UIManager.put("nimbusBase", new Color(40, 40, 50));
            UIManager.put("nimbusBlueGrey", new Color(50, 50, 60));
            UIManager.put("text", new Color(220, 220, 220));
            UIManager.put("nimbusFocus", new Color(100, 100, 120));
            UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 16));
            UIManager.put("TextArea.font", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("ComboBox.font", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("Spinner.font", new Font("SansSerif", Font.PLAIN, 14));
            UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        } 
        catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> 
        {
            try 
            {
                new VacationRentalGUI();
            } 
            catch (Exception ex) 
            {
                ex.printStackTrace();
            }
        });
    }
}