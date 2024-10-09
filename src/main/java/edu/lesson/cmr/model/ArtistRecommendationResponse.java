package edu.lesson.cmr.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class ArtistRecommendationResponse {

  private String artistName;
  private int numberOfRecommendations;
  private boolean useCosineDistance;
  private List<String> recommendedArtistNames;

  public static ArtistRecommendationResponse of(
      String artistName,
      int numberOfRecommendations,
      boolean useCosineDistance,
      List<String> recommendedArtistNames) {
    return ArtistRecommendationResponse.builder()
        .artistName(artistName)
        .numberOfRecommendations(numberOfRecommendations)
        .useCosineDistance(useCosineDistance)
        .recommendedArtistNames(recommendedArtistNames)
        .build();
  }
}
