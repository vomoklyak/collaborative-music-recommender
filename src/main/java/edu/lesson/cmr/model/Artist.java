package edu.lesson.cmr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
public class Artist {

  private int id;
  private String name;

  public static Artist of(int id, String name) {
    return Artist.builder()
        .id(id)
        .name(name)
        .build();
  }
}
