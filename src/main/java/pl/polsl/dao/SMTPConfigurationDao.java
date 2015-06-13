package pl.polsl.dao;


import org.springframework.data.repository.CrudRepository;
import pl.polsl.entity.SMTPConfiguration;

public interface SMTPConfigurationDao extends CrudRepository<SMTPConfiguration, Long> {

}
