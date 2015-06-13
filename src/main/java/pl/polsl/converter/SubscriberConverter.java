package pl.polsl.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.entity.Subscriber;
import pl.polsl.service.SubscriberService;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@Service
@FacesConverter("subscriberConverter")
public class SubscriberConverter implements Converter {

    @Autowired
    protected SubscriberService subscriberService;

    @Override
    public Object getAsObject(final FacesContext context, final UIComponent component, final String value) {
        if (value != null && value.trim().length() > 0) {
            try {
                return subscriberService.getSubscribers()
                        .stream()
                        .filter(subscriber -> subscriber.getId().equals(Long.valueOf(value)))
                        .findFirst().get();
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid group."));
            }
        }

        return null;
    }

    @Override
    public String getAsString(final FacesContext context, final UIComponent component, final Object value) {
        if (value != null) {
            return String.valueOf(((Subscriber) value).getId());
        } else {
            return null;
        }
    }
}
