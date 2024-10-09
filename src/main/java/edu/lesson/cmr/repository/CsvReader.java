package edu.lesson.cmr.repository;

import edu.lesson.cmr.exception.CollaborativeMusicRecommenderException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class CsvReader {

  @SneakyThrows
  static List<String> csvLines(String csvPathStr) {
    var inputStream = CsvReader.class.getClassLoader().getResourceAsStream(csvPathStr);
    if (inputStream == null) {
      throw new CollaborativeMusicRecommenderException(
          String.format("Csv file not found: %s", csvPathStr));
    }
    // automatically closes wrapped input stream
    try (var bufferedReader = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
      return bufferedReader.lines().toList();
    }
  }
}