# Collaborative-Music-Recommender

## Introduction

Repository contains Java implementations for Collaborative-Music-Recommender (CMR).
https://www.youtube.com/watch?v=HLa836Zf39A

## Build and Test

**To build project**:<br>
`mvn clean install`<br>
**To run test suite** (Junit based: *Test, *IT):<br>
`mvn clean test`<br>

## Run

**To run CMR**:

* enter project root:<br>
  `collaborative-music-recommender`
* build project:<br>
  `mvn clean install`
* execute command:<br>
  `java -jar ./target/collaborative-music-recommender-1.0-SNAPSHOT-jar-with-dependencies.jar artistName=Queen`
* use arguments:<br>
  `playCsvPathStr=/custom/play.csv` set custom play list path<br>
  `artistCsvPathStr=/custom/artist.csv` set custom artist list path<br>
  `resultPathStr=/custom/result.json` set custom result path<br> 
  `artistName=Wolfgang Amadeus Mozart` set custom artist name (see artist.csv)<br>
  `numberOfRecommendations=10` set custom number of recommendations<br>
  `useCosineDistance=true` set cosine distance (cosine distance ignores popular results and find more similar results)<br>