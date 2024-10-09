package edu.lesson.cmr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class Play {

  private int userId;
  private int artistId;
  private int numberOfPlays;

  public static Play of(int userId, int artistId, int numberOfPlays) {
    return Play.builder()
        .userId(userId)
        .artistId(artistId)
        .numberOfPlays(numberOfPlays)
        .build();
  }
}