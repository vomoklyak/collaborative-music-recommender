package edu.lesson.cmr.exception;

public class ResourceNotFoundException extends CollaborativeMusicRecommenderException {

  public ResourceNotFoundException(String message) {
    super(message);
  }
}