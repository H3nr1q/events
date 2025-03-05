package br.com.nlw.events.services;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
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
    Event event = eventRepository.findByPrettyName(eventName);
    if (event == null) {
      throw new EventNotFoundException("event " + eventName + " não existe");
    }
    User newUser = userRepository.findByEmail(user.getEmail());
    if (newUser == null) {
      newUser = userRepository.save(user);
    }

    User userIndicator = null;
    if (userId != null) {
      userIndicator = userRepository.findById(userId).orElse(null);
      if (userIndicator == null) {
        throw new UserIndicatorNotFoundException("Usuário " + userId + " indicador não encontrado");
      }
    }

    Subscription subscription = new Subscription();
    subscription.setEvent(event);
    subscription.setSubscribed(newUser);
    subscription.setIndication(userIndicator);

    Subscription existingSubscription = subscriptionRepository.findByEventAndSubscribed(event, newUser);
    if (existingSubscription != null) {
      throw new SubscriptionConflictException(
          "Já exist inscrição para o usuário " + newUser.getName() + " no evento " + event.getTitle());
    }

    Subscription newSubscription = subscriptionRepository.save(subscription);
    return new SubscriptionResponse(newSubscription.getSubscriptionNumber(), "http://codecraft.com/events/"
        + subscription.getEvent().getPrettyName() + "/" + newSubscription.getSubscribed().getId());

  }
	
	public List<SubscriptionRankingItem> getCompleteRanking(String prettyName){
		Event event = eventRepository.findByPrettyName(prettyName);
		if(event == null){
			throw new EventNotFoundException("Ranking do evento "+prettyName+" não exise");
		}
		return subscriptionRepository.generatedRanking(event.getEventId());
	}

	public SubscriptionRankingByUser getRankingByUser(String prettyName, Integer userId){
		List<SubscriptionRankingItem> rankingItems = getCompleteRanking(prettyName);
		
		SubscriptionRankingItem item = rankingItems.stream().filter(i -> i.userId().equals(userId)).findFirst().orElse(null);
		if(item == null){
			throw new UserIndicatorNotFoundException("Não há inscrições com indicação do usuário "+userId);
		}
		Integer position = IntStream.range(0, rankingItems.size())
											.filter(pos -> rankingItems.get(pos).userId().equals(userId))
											.findFirst().getAsInt();
		return new SubscriptionRankingByUser(item, position+1);
	}

}
