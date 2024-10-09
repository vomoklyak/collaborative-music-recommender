package edu.lesson.cmr;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.lesson.cmr.exception.CollaborativeMusicRecommenderException;
import edu.lesson.cmr.exception.ResourceNotFoundException;
import edu.lesson.cmr.model.ArtistRecommendationResponse;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CollaborativeMusicRecommenderIT {

  @ParameterizedTest
  @MethodSource("successfulArguments")
  void shouldRecommend(
      String[] arguments,
      ArtistRecommendationResponse expectedResponse) {
    // When
    CollaborativeMusicRecommender.main(arguments);

    // Then
    Assertions.assertThat(actualResponse()).isEqualTo(expectedResponse);
  }

  private static Stream<Arguments> successfulArguments() {
    return Stream.of(
        args(
            new String[]{
                "artistName=Eric Clapton",
                "numberOfRecommendations=10"
            },
            ArtistRecommendationResponse.of(
                "Eric Clapton",
                10,
                false,
                List.of(
                    "Glee Cast", "Pink Floyd", "Led Zeppelin", "The Beatles", "Pearl Jam",
                    "Darren Criss", "The Who", "Duran Duran", "Roger Waters", "Jethro Tull"
                ))
        ),
        args(
            new String[]{
                "artistName=Eric Clapton",
                "numberOfRecommendations=10",
                "useCosineDistance=true"
            },
            ArtistRecommendationResponse.of(
                "Eric Clapton",
                10,
                true,
                List.of(
                    "Eric Clapton", "Edgar Broughton Band", "Warren Haynes", "Humble Pie",
                    "Syd Barrett",
                    "Jethro Tull", "Grand Funk Railroad", "Roger Waters", "Gov't Mule",
                    "Barao Vermelho"
                ))
        ),
        args(
            new String[]{
                "artistName=Eric Clapton",
                "numberOfRecommendations=5"
            },
            ArtistRecommendationResponse.of(
                "Eric Clapton",
                5,
                false,
                List.of("Glee Cast", "Pink Floyd", "Led Zeppelin", "The Beatles", "Pearl Jam"))
        )
    );
  }

  @ParameterizedTest
  @MethodSource("errorArguments")
  void shouldRecommendCaseError(
      String[] arguments,
      Class<? extends CollaborativeMusicRecommenderException> expectedExceptionClass
  ) {
    // When
    final ThrowingCallable result = () -> CollaborativeMusicRecommender.main(arguments);

    // Then
    Assertions.assertThatThrownBy(result)
        .isInstanceOf(expectedExceptionClass);
  }

  private static Stream<Arguments> errorArguments() {
    return Stream.of(
        args(
            new String[]{"artistName=Non Existent artistName"},
            ResourceNotFoundException.class
        ),
        args(
            new String[]{"playCsvPathStr=Non Existent playCsvPathStr"},
            CollaborativeMusicRecommenderException.class
        ),
        args(
            new String[]{"artistCsvPathStr=Non Existent artistCsvPathStr"},
            CollaborativeMusicRecommenderException.class
        )
    );
  }

  private static Arguments args(Object... args) {
    return Arguments.of(args);
  }

  @Test
  void shouldRecommendCaseDefaults() {
    final var arguments = new String[]{};
    final var expectedResponse = ArtistRecommendationResponse.of(
        "Queen",
        5,
        false,
        List.of("Queen", "Enigma", "Duran Duran", "The Beatles", "Pink Floyd")
    );

    // When
    CollaborativeMusicRecommender.main(arguments);

    // Then
    Assertions.assertThat(actualResponse()).isEqualTo(expectedResponse);
  }

  @SneakyThrows
  private static ArtistRecommendationResponse actualResponse() {
    return new ObjectMapper().readValue(
        Path.of("/tmp/collaborative_artist_recommender_result.json").toFile(),
        ArtistRecommendationResponse.class
    );
  }
}