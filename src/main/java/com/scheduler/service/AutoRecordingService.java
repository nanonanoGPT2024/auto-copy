package com.scheduler.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Mulai: " + now);
        startCopy();
        LocalDateTime end = LocalDateTime.now();
        System.out.println("end: " + end);
      } catch (Exception e) {

      }
    };
    taskQueue.submitTask(task);
    return String.valueOf(taskQueue.getQueueSize());
  }

  public void startCopy() {

    File folderTahun = new File(sourcePath);

    String DestCopy = destinationPath;

    File[] listOfTahun = folderTahun.listFiles();

    List<String> ListHp = new ArrayList<>();
    try {
      ListHp = CsvUtils.readCsvFile(pathFile);
      System.out.println("total data csv : " + ListHp.size());
    } catch (Exception e) {

      System.out.println("error : " + e.getCause());
    }

    // looping folder tahun
    if (listOfTahun != null) {
      for (File fileTahun : listOfTahun) {

        if (fileTahun.getName().equals("2025")) {
          String baseDestCopy = DestCopy + "/" + fileTahun.getName();
          File folderBulan = new File(fileTahun.getAbsolutePath());
          File[] listOfBulan = folderBulan.listFiles();

          // looping folder bulan
          if (listOfBulan != null) {

            for (File fileBulan : listOfBulan) {
              String bulanName = fileBulan.getName();
              // Hanya proses bulan 01-06
              if (bulanName.compareTo("01") >= 0 && bulanName.compareTo("08") <= 0) {
                String bulanDestCopy = baseDestCopy + "/" + fileBulan.getName();

                File folderTanggal = new File(fileBulan.getAbsolutePath());
                File[] listOfTanggal = folderTanggal.listFiles();
                if (listOfTanggal != null) {

                  // looping folder tanggal
                  for (File fileTanggal : listOfTanggal) {
                    String currentDestCopy = bulanDestCopy + "/" + fileTanggal.getName();
                    File listOfFile = new File(fileTanggal.getAbsolutePath());
                    File[] folderFile = listOfFile.listFiles();

                    // Buat path direktori (tanpa nama file)
                    File newPath = new File(currentDestCopy);

                    // looping file
                    if (folderFile != null) {
                      for (File file : folderFile) {
                        for (String noHp : ListHp) {
                          if (file.isFile() && file.getName().contains(noHp)) {
                            if (!newPath.exists()) {
                              newPath.mkdirs();
                            }
                            try {
                              // Copy ke direktori dengan nama file asli
                              Files.copy(
                                  Paths.get(fileTanggal.getAbsolutePath()).resolve(file.getName()),
                                  Paths.get(currentDestCopy).resolve(file.getName()),
                                  StandardCopyOption.REPLACE_EXISTING);
                            } catch (Exception e) {
                              System.out.println(e.getMessage());
                            }

                            break;
                          }
                        }
                      }
                    }

                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
