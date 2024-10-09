package edu.lesson.cmr;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.lesson.cmr.config.ApplicationArguments;
import edu.lesson.cmr.model.ArtistRecommendationRequest;
import edu.lesson.cmr.repository.ArtistCachedCsvRepository;
import edu.lesson.cmr.repository.PlayCachedCsvRepository;
import edu.lesson.cmr.service.CollaborativeArtistRecommendationService;
import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CollaborativeMusicRecommender {

  @SneakyThrows
  public static void main(String[] args) {
    var arguments = new ApplicationArguments(args);
    var playRepository = new PlayCachedCsvRepository(arguments.getPlayCsvPathStr());
    var artistRepository = new ArtistCachedCsvRepository(arguments.getArtistCsvPathStr());
    var artistRecommendationService =
        new CollaborativeArtistRecommendationService(playRepository, artistRepository);

    var request = ArtistRecommendationRequest.builder()
        .artistName(arguments.getArtistName())
        .numberOfRecommendations(arguments.getNumberOfRecommendations())
        .useCosineDistance(arguments.isUseCosineDistance())
        .build();
    log.info("Start processing: artistName={}, numberOfRecommendations={}",
        request.getArtistName(), request.getNumberOfRecommendations());
    var response = artistRecommendationService.recommend(request);
    log.info("Write processing result: resultPathStr={}", arguments.getResultPathStr());
    Files.writeString(Paths.get(arguments.getResultPathStr()),
        new ObjectMapper().writeValueAsString(response));
    log.info("Finish processing: artistName={},  recommendedArtistNames={}",
        response.getArtistName(), response.getRecommendedArtistNames());
  }
}