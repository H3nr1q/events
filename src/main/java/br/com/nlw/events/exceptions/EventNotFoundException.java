package br.com.nlw.events.exceptions;

public class EventNotFoundException extends RuntimeException{
  public EventNotFoundException(String message){
    super(message);
  }

}
