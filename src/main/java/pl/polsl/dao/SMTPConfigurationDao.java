package pl.polsl.dao;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import pl.polsl.entity.SMTPConfiguration;
import pl.polsl.entity.Subscriber;

import java.util.List;

public interface SMTPConfigurationDao extends CrudRepository<SMTPConfiguration, Long> {

    @Query("SELECT sc FROM smtp_configuration sc WHERE sc.active = true ")
    SMTPConfiguration getActive();
}
