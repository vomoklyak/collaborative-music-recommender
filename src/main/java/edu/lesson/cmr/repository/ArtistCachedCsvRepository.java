package edu.lesson.cmr.repository;


import edu.lesson.cmr.exception.ResourceNotFoundException;
import edu.lesson.cmr.model.Artist;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArtistCachedCsvRepository {

  private final Map<Integer, Artist> idToArtist;
  private final Map<String, Artist> nameToArtist;
  private final int numberOfArtists;

  public ArtistCachedCsvRepository(String csvPathStr) {
    log.info("Create repository: csvPathStr={}", csvPathStr);
    var idToArtist = idToArtist(csvPathStr);
    this.idToArtist = idToArtist;
    this.nameToArtist = nameToArtist(idToArtist);
    this.numberOfArtists = numberOfArtists(idToArtist);
  }

  @SneakyThrows
  private static Map<Integer, Artist> idToArtist(String csvPathStr) {
    Map<Integer, Artist> idToArtist = new HashMap<>();
    CsvReader.csvLines(csvPathStr).stream()
        // artistId,artistName
        .map(csvRow -> csvRow.split(","))
        .filter(parts -> parts.length == 2)
        .forEach(parts -> idToArtist.put(
            Integer.parseInt(parts[0]), Artist.of(Integer.parseInt(parts[0]), parts[1].trim())));
    return idToArtist;
  }

  private static Map<String, Artist> nameToArtist(Map<Integer, Artist> idToArtist) {
    Map<String, Artist> nameToArtist = new HashMap<>();
    idToArtist.values().forEach(
        artist -> nameToArtist.put(artist.getName(), artist));
    return nameToArtist;
  }

  private static int numberOfArtists(Map<Integer, Artist> idToArtist) {
    return idToArtist.values().stream()
        .map(Artist::getId)
        .mapToInt(value -> value)
        .max()
        .orElse(0);
  }

  public int countNumberOfArtists() {
    return numberOfArtists;
  }

  public Artist findArtist(int id) {
    var artist = idToArtist.get(id);
    if (artist == null) {
      throw new ResourceNotFoundException(String.format("Artist not found: id=%s", id));
    }
    return artist;
  }

  public Artist findArtist(String name) {
    var artist = nameToArtist.get(name);
    if (artist == null) {
      throw new ResourceNotFoundException(String.format("Artist not found: name=%s", name));
    }
    return artist;
  }
}