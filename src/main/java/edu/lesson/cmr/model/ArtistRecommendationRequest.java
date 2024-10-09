package edu.lesson.cmr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class ArtistRecommendationRequest {

  private String artistName;
  private int numberOfRecommendations;
  private boolean useCosineDistance;
}
