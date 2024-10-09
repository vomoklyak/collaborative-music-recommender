package edu.lesson.cmr.config;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;

@Getter
@FieldNameConstants
public class ApplicationArguments {

  private final String playCsvPathStr;
  private final String artistCsvPathStr;
  private final String resultPathStr;
  private final String artistName;
  private final int numberOfRecommendations;
  private final boolean useCosineDistance;

  public ApplicationArguments(String[] arguments) {
    var argumentNameToValue = argumentNameToValue(arguments);
    this.playCsvPathStr = argumentNameToValue.getOrDefault(Fields.playCsvPathStr,
        "user_artist_plays.csv");
    this.artistCsvPathStr = argumentNameToValue.getOrDefault(Fields.artistCsvPathStr,
        "artist.csv");
    this.resultPathStr = argumentNameToValue.getOrDefault(Fields.resultPathStr,
        String.format("%s/collaborative_artist_recommender_result.json",
            System.getProperty("java.io.tmpdir")));
    this.artistName = argumentNameToValue.getOrDefault(Fields.artistName,
        "Queen");
    this.numberOfRecommendations = Integer.parseInt(
        argumentNameToValue.getOrDefault(Fields.numberOfRecommendations, "5"));
    this.useCosineDistance = Boolean.parseBoolean(
        argumentNameToValue.getOrDefault(Fields.useCosineDistance, "false"));
  }

  private static Map<String, String> argumentNameToValue(String[] arguments) {
    return Arrays.stream(arguments)
        .distinct()
        .map(argument -> argument.split("="))
        .filter(parts -> parts.length == 2)
        .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
  }
}