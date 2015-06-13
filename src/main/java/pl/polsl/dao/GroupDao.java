package pl.polsl.dao;


import org.springframework.data.repository.CrudRepository;
import pl.polsl.entity.Groups;

public interface GroupDao extends CrudRepository<Groups, Long> {

}
