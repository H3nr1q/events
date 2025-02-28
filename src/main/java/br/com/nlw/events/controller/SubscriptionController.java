package br.com.nlw.events.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.nlw.events.dto.ErrorMessage;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exceptions.EventNotFoundException;
import br.com.nlw.events.exceptions.SubscriptionConflictException;
import br.com.nlw.events.exceptions.UserIndicatorNotFoundException;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.services.SubscriptionService;

@RestController
public class SubscriptionController {

  @Autowired
  private SubscriptionService subscriptionService;

  @PostMapping({"/subscription/{prettyName}","/subscription/{prettyName}/{userId}"})
  public ResponseEntity<?> createSubscription(@PathVariable String prettyName, 
                                              @RequestBody User subscribed,
                                              @PathVariable(required = false) Integer userId) {
    try {
      SubscriptionResponse newSubscription = subscriptionService.createNewSubscription(prettyName, subscribed, userId);
      if(newSubscription != null){
        return ResponseEntity.ok(newSubscription);
      }
    } catch (EventNotFoundException  ex) {
      return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
    }
    catch (SubscriptionConflictException ex) {
      return ResponseEntity.status(409).body(new ErrorMessage(ex.getMessage()));
    }
    catch(UserIndicatorNotFoundException ex){
      return ResponseEntity.status(404).body(new ErrorMessage(ex.getMessage()));
    }
    return ResponseEntity.badRequest().build();
  }


}