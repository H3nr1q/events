package br.com.nlw.events.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.model.Event;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;

public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> {
  public Subscription findByEventAndSubscribed(Event event, User user);

  @Query(value = "select"
          +" count(sub.subscription_number) as quantidade, "
          +" sub.indication_user_id, "
          +"  user.user_name "
          +" from tbl_subscription as sub "
          +" inner join tbl_user as user on sub.indication_user_id = user.user_id "
          +" where sub.indication_user_id is not null "
          +" and sub.event_id = :eventId "
          +" group by sub.indication_user_id "
          +" order by quantidade desc ", nativeQuery = true)
  public List<SubscriptionRankingItem> generatedRanking(@Param("eventId") Integer eventId);
}
