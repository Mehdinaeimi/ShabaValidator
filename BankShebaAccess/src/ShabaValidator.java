import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.ConsoleHandler;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class ShabaValidator {
    private static final Logger LOGGER = Logger.getLogger(ShabaValidator.class.getName());

    static {
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter() {
            @Override
            public String format(LogRecord record) {
                ZoneId zoneId = ZoneId.of("Asia/Tehran");
                ZonedDateTime zdt = ZonedDateTime.now(zoneId);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.US);
                String persianDate = zdt.format(formatter);
                return String.format("[%s] %s%n", persianDate, record.getMessage());
            }
        });
        LOGGER.addHandler(consoleHandler);

        FileHandler fileHandler = null;
        try {
            fileHandler = new FileHandler("Log.log", true);
            fileHandler.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
                    ZoneId zoneId = ZoneId.of("Asia/Tehran");
                    ZonedDateTime zdt = ZonedDateTime.now(zoneId);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss", Locale.US);
                    String persianDate = zdt.format(formatter);
                    return String.format("[%s] %s%n", persianDate, record.getMessage());
                }
            });
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            LOGGER.severe("Error creating file handler: " + e.getMessage());
        }
    }
    private static final String INVALID_SHABA_FILE = "invalid_shabas.txt";
    private static final String VALID_SHABA_FILE = "valid_shabas.txt";

    public static void main(String[] args) {
        List<String> validShabas = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            validShabas = new ArrayList<>();
            List<String> invalidShabas = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (isValidShaba(line)) {
                    validShabas.add(line);
                    extractBankCodeAndAccountNumber(line);
                } else {
                    invalidShabas.add(line);
                }
            }
            writeToFile(INVALID_SHABA_FILE, invalidShabas);
            writeToFile(VALID_SHABA_FILE, validShabas);
        } catch (IOException e) {
            LOGGER.severe("Error reading or writing file: " + e.getMessage());
        }

        JFrame frame = new JFrame("Valid Shaba's");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("CC - کد کشور");
        model.addColumn("نام بانک");
        model.addColumn("CD - شناسه بانک");
        model.addColumn("BBAN - شماره شبا");


        for (String shaba : validShabas) {
            String CC = "IR - ایران";
            String bankCode = shaba.substring(2, 5);
            String accountNumber = shaba.substring(5);
            String bankName = getBankName(bankCode);
            model.addRow(new Object[]{CC,bankName, bankCode, accountNumber});
        }

        JButton closeButton = new JButton("بستن صفحه");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        frame.getContentPane().add(closeButton, BorderLayout.SOUTH);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        table.getColumnModel().getColumn(0).setPreferredWidth(1);
        table.getColumnModel().getColumn(1).setPreferredWidth(1);
        table.getColumnModel().getColumn(2).setPreferredWidth(1);
        table.getTableHeader().setBackground(Color.lightGray);
        frame.getContentPane().add(scrollPane);
        frame.setSize(450, 300);
        frame.setVisible(true);
    }

    private static String getBankName(String bankCode) {
        switch (bankCode) {
            case "020":
                return "بانک توسعه صادرات";
            case "055":
                return "بانک اقتصاد نوین";
            case "054":
                return "بانک پارسیان";
            case "057":
                return "بانک پاسارگاد";
            case "021":
                return "پست بانک ایران";
            case "018":
                return "بانک تجارت";
            case "051":
                return "موسسه اعتباری توسعه";
            case "013":
                return "بانک رفاه";
            case "056":
                return "بانک سامان";
            case "015":
                return "بانک سپه";
            case "058":
                return "بانک سرمایه";
            case "019":
                return "بانک صادرات ایران";
            case "011":
                return "بانک صنعت و معدن";
            case "053":
                return "بانک کارآفرین";
            case "016":
                return "بانک کشاورزی";
            case "010":
                return "بانک مرکزی";
            case "014":
                return "بانک مسکن";
            case "012":
                return "بانک ملت";
            case "017":
                return "بانک ملی";
            default:
                return "بانک نا مشخص";
        }
    }

    private static void extractBankCodeAndAccountNumber(String shaba) {
        String bankCode = shaba.substring(2, 5);
        String accountNumber = shaba.substring(5);
        String bankName = getBankName(bankCode);
        System.out.println(bankName + ", CD: " + bankCode + ", BBAN: " + accountNumber);
    }

    private static boolean isValidShaba(String shaba) {
        String bankCode = shaba.substring(2, 5);
        return shaba.length() == 26 && shaba.startsWith("IR") && bankCode.startsWith("0") && shaba.matches("[A-Z0-9]+");
    }

    private static void writeToFile(String fileName, List<String> shabas) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (String shaba : shabas) {
                writer.write(shaba + "\n");
            }
        } catch (IOException e) {
            LOGGER.severe("Error writing to file: " + e.getMessage());
        }
    }
    private static class PersianDateConverter {
        public static String convert(Date date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            return dateFormat.format(date);
        }
    }
}