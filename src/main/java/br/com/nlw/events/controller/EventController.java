package br.com.nlw.events.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.nlw.events.model.Event;
import br.com.nlw.events.services.EventService;

@RestController
public class EventController {
  @Autowired
  private EventService eventService;

  @PostMapping("/events")
  public Event addNewEvent(@RequestBody Event newEvent) {
    return eventService.AddNewEvent(newEvent);
  }

  @GetMapping("/events")
  public List<Event> getAllEvents() {
    return eventService.getAllEvents();
  }

  @GetMapping("/events/{prettyName}")
  public ResponseEntity<Event> getEventByPrettyName(@PathVariable String prettyName) {
    Event newEvent = eventService.getByPrettyName(prettyName);
    if(newEvent != null) {
      return ResponseEntity.ok().body(newEvent );
    }
    return ResponseEntity.notFound().build();
  }
}
