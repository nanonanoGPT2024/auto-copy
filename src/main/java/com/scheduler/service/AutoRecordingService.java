package com.scheduler.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.scheduler.utils.CsvUtils;
import com.scheduler.utils.SingleThreadTaskQueue;

@Service
public class AutoRecordingService {
  @Value("${app.folder.storage.data_recording}")
  private String sourcePath;

  @Value("${app.folder.storage.data_recording_new}")
  private String destinationPath;

  @Value("${app.folder.storage.file}")
  private String pathFile;

  final SingleThreadTaskQueue taskQueue = new SingleThreadTaskQueue();

  public String importData() {
    Runnable task = () -> {
      try {
        startCopy();
      } catch (Exception e) {
        // Silent handling
      }
    };
    taskQueue.submitTask(task);
    return String.valueOf(taskQueue.getQueueSize());
  }

  public void startCopy() {
    LocalDateTime startTime = LocalDateTime.now();
    String formattedStartTime = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    try {
      System.out.println("=== PROSES COPY FILE DIMULAI ===");
      System.out.println("Start Time: " + formattedStartTime);

      // Load data CSV langsung menggunakan CsvUtils tanpa optimasi tambahan
      List<String> rawPhoneData = CsvUtils.readCsvFile(pathFile);

      // Hitung duplikasi sebelum konversi ke HashSet
      Set<String> uniquePhoneNumbers = new HashSet<>(rawPhoneData);

      File folderTahun = new File(sourcePath);
      File[] listOfTahun = folderTahun.listFiles();

      if (listOfTahun != null) {
        for (File fileTahun : listOfTahun) {
          if (fileTahun.getName().equals("2025")) {
            processYearFolder(fileTahun, uniquePhoneNumbers);
          }
        }
      }

      LocalDateTime endTime = LocalDateTime.now();
      String formattedEndTime = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      System.out.println("End Time: " + formattedEndTime);
      System.out.println("=== PROSES COPY FILE SELESAI ===");

    } catch (Exception e) {
      LocalDateTime endTime = LocalDateTime.now();
      String formattedEndTime = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

      System.out.println("End Time (Error): " + formattedEndTime);
      System.out.println("=== PROSES COPY FILE GAGAL ===");
    }
  }

  private void processYearFolder(File yearFolder, Set<String> phoneNumbers) {
    String baseDestCopy = destinationPath + "/" + yearFolder.getName();
    File[] months = yearFolder.listFiles();

    if (months == null)
      return;

    // Process months in parallel untuk performa maksimal
    List<CompletableFuture<Void>> futures = new ArrayList<>();

    for (File monthFolder : months) {
      String monthName = monthFolder.getName();
      if (isValidMonth(monthName)) {
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
          processMonthFolder(monthFolder, baseDestCopy, phoneNumbers);
        });
        futures.add(future);
      }
    }

    // Wait for all months to complete
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
  }

  private boolean isValidMonth(String monthName) {
    // Hanya proses bulan 01-08
    return monthName.compareTo("01") >= 0 && monthName.compareTo("08") <= 0;
  }

  private void processMonthFolder(File monthFolder, String baseDestCopy, Set<String> phoneNumbers) {
    String monthDestCopy = baseDestCopy + "/" + monthFolder.getName();
    File[] days = monthFolder.listFiles();

    if (days == null)
      return;

    for (File dayFolder : days) {
      if (dayFolder.isDirectory()) {
        processDayFolder(dayFolder, monthDestCopy, phoneNumbers);
      }
    }
  }

  private void processDayFolder(File dayFolder, String monthDestCopy, Set<String> phoneNumbers) {
    String dayDestCopy = monthDestCopy + "/" + dayFolder.getName();
    File[] files = dayFolder.listFiles();

    if (files == null)
      return;

    // Pre-create destination directory
    File destDir = new File(dayDestCopy);
    if (!destDir.exists()) {
      destDir.mkdirs();
    }

    // Process files dengan optimasi lookup O(1)
    for (File file : files) {
      if (file.isFile() && shouldCopyFile(file.getName(), phoneNumbers)) {
        copyFileToDestination(file, dayFolder, dayDestCopy);
      }
    }
  }

  private boolean shouldCopyFile(String fileName, Set<String> phoneNumbers) {
    // Check if any phone number from the set is contained in the filename
    for (String phoneNumber : phoneNumbers) {
      if (fileName.contains(phoneNumber)) {
        return true;
      }
    }
    return false;
  }

  private void copyFileToDestination(File sourceFile, File sourceDir, String destDir) {
    try {
      Files.copy(
          sourceDir.toPath().resolve(sourceFile.getName()),
          Paths.get(destDir).resolve(sourceFile.getName()),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      // Silent handling
    }
  }
}
