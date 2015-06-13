package pl.polsl.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pl.polsl.entity.Subscriber;

import javax.persistence.NamedNativeQuery;
import java.util.List;

public interface SubscriberDao extends CrudRepository<Subscriber, Long> {

    @Query("SELECT s FROM Subscriber s LEFT JOIN s.groupsList gl WHERE gl.id = :groupId ")
    List<Subscriber> listGroupSubscribers(@Param("groupId") Long groupId);

}
