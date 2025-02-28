package br.com.nlw.events.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exceptions.EventNotFoundException;
import br.com.nlw.events.exceptions.SubscriptionConflictException;
import br.com.nlw.events.exceptions.UserIndicatorNotFoundException;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repository.EventRepository;
import br.com.nlw.events.repository.SubscriptionRepository;
import br.com.nlw.events.repository.UserRepository;

@Service
public class SubscriptionService {

  @Autowired
  private EventRepository eventRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SubscriptionRepository subscriptionRepository;

  public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {
    	Subscription subscription = new Subscription();
      Event event = eventRepository.findByPrettyName(eventName);
      if(event == null){
        throw new EventNotFoundException("event "+eventName+ " não existe");
      }
      User newUser = userRepository.findByEmail(user.getEmail());
      if(newUser == null){
        newUser = userRepository.save(user);
      }

      User userIndicator = userRepository.findById(userId).orElse(null);
      if(userIndicator == null){
        throw new UserIndicatorNotFoundException("Usuário "+userId+ " indicador não encontrado");
      }

      subscription.setEvent(event);
      subscription.setSubscribed(newUser);
      subscription.setIndication(userIndicator);

      Subscription existingSubscription = subscriptionRepository.findByEventAndSubscribed(event, newUser);
      if(existingSubscription != null){
        throw new SubscriptionConflictException("Já exist inscrição para o usuário "+newUser.getName()+" no evento "+event.getTitle());
      }

      Subscription newSubscription = subscriptionRepository.save(subscription);
      return new SubscriptionResponse(newSubscription.getSubscriptionNumber(), "http://codecraft.com/events/"+subscription.getEvent().getPrettyName()+"/"+newSubscription.getSubscribed().getId());
      
  }
}
