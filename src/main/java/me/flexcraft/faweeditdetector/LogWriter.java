package me.flexcraft.faweeditdetector;

import java.io.File;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogWriter {

    private static final DateTimeFormatter TIME =
            DateTimeFormatter.ofPattern("HH:mm:ss");

    public static void write(File dataFolder, String line) {
        try {
            File logFile = new File(
                    new File(dataFolder, "logs"),
                    LocalDate.now() + ".log"
            );

            FileWriter fw = new FileWriter(logFile, true);
            fw.write("[" + LocalDateTime.now().format(TIME) + "] " + line + "\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
