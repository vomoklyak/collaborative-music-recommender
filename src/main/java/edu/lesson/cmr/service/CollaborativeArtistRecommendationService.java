package edu.lesson.cmr.service;

import edu.lesson.cmr.model.Artist;
import edu.lesson.cmr.model.ArtistRecommendationRequest;
import edu.lesson.cmr.model.ArtistRecommendationResponse;
import edu.lesson.cmr.repository.ArtistCachedCsvRepository;
import edu.lesson.cmr.repository.PlayCachedCsvRepository;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ejml.data.DMatrixSparseCSC;
import org.ejml.data.DMatrixSparseTriplet;
import org.ejml.ops.DConvertMatrixStruct;
import org.ejml.simple.SimpleMatrix;
import org.ejml.sparse.csc.CommonOps_DSCC;

@Slf4j
@RequiredArgsConstructor
public class CollaborativeArtistRecommendationService {

  private final PlayCachedCsvRepository playRepository;
  private final ArtistCachedCsvRepository artistRepository;

  public ArtistRecommendationResponse recommend(ArtistRecommendationRequest request) {
    var artist = artistRepository.findArtist(request.getArtistName());
    var artistSimilarityRow = artistSimilarityRow(artist.getId(), request.isUseCosineDistance());
    // top k most similar artists
    return IntStream.range(0, artistSimilarityRow.length)
        .boxed()
        .filter(similarity -> similarity > 0)
        .sorted(Comparator.comparingDouble(
            recommendedArtistId -> -artistSimilarityRow[recommendedArtistId])) // reversed order (max)
        .limit(request.getNumberOfRecommendations())
        .map(artistRepository::findArtist)
        .map(Artist::getName)
        .collect(Collectors.collectingAndThen(
            Collectors.toList(), artistNames -> response(request, artistNames)));
  }

  /**
   * @return Row with index artistId from similarity matrix: At*A (A is user play matrix, At
   * transposed matrix).
   * </br>
   * Cosine distance is used based on useCosineDistance flag:
   * <ul>
   *     <li>false: (Va,Vb)=cos(Va,Vb)*|Va|*|Vb| </li>
   *     <li>true: cos(Va,Vb)=(Va,Vb)/|Va|*|Vb| </li>
   * </ul>
   */
  @SuppressWarnings("ConstantConditions")
  private double[] artistSimilarityRow(int artistId, boolean useCosineDistance) {
    var newMatrix = (DMatrixSparseCSC) null;
    var numberOfRows = playRepository.countNumberOfUsers() + 1;
    var numberOfColumns = artistRepository.countNumberOfArtists() + 1;
    log.info("Create sparse triplets: numberOfTriplets={}", playRepository.findAllPlays().size());
    var sparseTriplets = new DMatrixSparseTriplet(numberOfRows, numberOfColumns, 1);
    playRepository.findAllPlays().forEach(play ->
        sparseTriplets.addItem(play.getUserId(), play.getArtistId(), play.getNumberOfPlays()));
    log.info("Create sparse matrix: numberOfRows={}, numberOfColumns={}",
        numberOfRows, numberOfColumns);
    var userPlaySparseMatrix =
        DConvertMatrixStruct.convert(sparseTriplets, newMatrix);
    log.info("Transpose sparse matrix: numberOfRows={}, numberOfColumns={}",
        numberOfColumns, numberOfRows);
    var transposedUserPlaySparseMatrix =
        CommonOps_DSCC.transpose(userPlaySparseMatrix, newMatrix, null);
    log.info("Multiply sparse matrices: {}x{} * {}x{}",
        numberOfColumns, numberOfRows, numberOfRows, numberOfColumns);
    var similaritySparseMatrix =
        CommonOps_DSCC.mult(transposedUserPlaySparseMatrix, userPlaySparseMatrix, newMatrix);
    log.info("Compute artist similarity row: artistId={}", artistId);
    return artistSimilarityRow(
        artistId, useCosineDistance, userPlaySparseMatrix, similaritySparseMatrix);
  }

  private double[] artistSimilarityRow(int artistId, boolean useCosineDistance,
      DMatrixSparseCSC userPlaySparseMatrix, DMatrixSparseCSC similaritySparseMatrix) {
    // each element in array is (Va,Vb)=cos(Va,Vb)*|Va|*|Vb| (Va artistId vector, Vb recommended artistId vector)
    var artistSimilarityRow =
        SimpleMatrix.wrap(similaritySparseMatrix).getRow(artistId).toArray2()[0];
    if (useCosineDistance) {
      log.info("Compute artist cosine distances");
      var userPlayDenseMatrix = SimpleMatrix.wrap(userPlaySparseMatrix);
      Function<Integer, Double> lengthFun = column ->
          Arrays.stream(userPlayDenseMatrix.getColumn(column).toArray2())
              .mapToDouble(value -> value[0] * value[0]).sum();
      // each element in array cos(Va,Vb)=(Va,Vb)/|Va|*|Vb| (Va artistId vector, Vb recommended artistId vector)
      IntStream.range(0, artistSimilarityRow.length).forEach(recommendedArtistId ->
          artistSimilarityRow[recommendedArtistId] =
              artistSimilarityRow[recommendedArtistId] /
                  Math.sqrt(lengthFun.apply(artistId) * lengthFun.apply(recommendedArtistId)));
    }
    return artistSimilarityRow;
  }

  private ArtistRecommendationResponse response(
      ArtistRecommendationRequest request, List<String> artistNames) {
    return ArtistRecommendationResponse.of(
        request.getArtistName(),
        request.getNumberOfRecommendations(),
        request.isUseCosineDistance(),
        artistNames
    );
  }
}