package edu.lesson.cmr.repository;

import edu.lesson.cmr.model.Play;
import java.util.Collection;
import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PlayCachedCsvRepository {

  private final List<Play> plays;
  private final int numberOfUsers;

  public PlayCachedCsvRepository(String csvPathStr) {
    log.info("Create repository: csvPathStr={}", csvPathStr);
    var plays = plays(csvPathStr);
    this.plays = plays;
    this.numberOfUsers = numberOfUsers(plays);
  }

  @SneakyThrows
  private static List<Play> plays(String csvPathStr) {
    return CsvReader.csvLines(csvPathStr).stream()
        // userId,artistId,numberOfPlays
        .map(csvRow -> csvRow.split(","))
        .filter(parts -> parts.length == 3)
        .map(parts -> Play.of(
            Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2])))
        .toList();
  }

  private static int numberOfUsers(List<Play> plays) {
    return plays.stream()
        .map(Play::getUserId)
        .mapToInt(value -> value)
        .max()
        .orElse(0);
  }

  public int countNumberOfUsers() {
    return numberOfUsers;
  }

  public Collection<Play> findAllPlays() {
    return plays;
  }
}