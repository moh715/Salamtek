package com.example.salamtek1;

import java.io.FileWriter;
import java.io.IOException;

class FileHandler {
    // Static method to save accident report to a .txt file
    public static void saveReportToFile(Accident accident) {
        String filename = "Accident_Report_" + accident.getAccidentId() + ".txt";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("=== SALAMTEK SYSTEM REPORT ===\n");
            writer.write(accident.generateReportDetails());
            writer.write("\n==============================\n");
            System.out.println(">> Report saved to file: " + filename);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
