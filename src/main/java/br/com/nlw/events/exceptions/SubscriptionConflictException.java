package br.com.nlw.events.exceptions;

public class SubscriptionConflictException extends RuntimeException{
  public SubscriptionConflictException(String message){
    super(message);
  }
}
