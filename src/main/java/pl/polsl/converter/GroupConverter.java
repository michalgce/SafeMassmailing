package pl.polsl.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.polsl.entity.Groups;
import pl.polsl.service.GroupService;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@Service
@FacesConverter("groupConverter")
public class GroupConverter implements Converter {

    @Autowired
    protected GroupService groupService;

    @Override
    public Object getAsObject(final FacesContext context, final UIComponent component, final String value) {
        if (value != null && value.trim().length() > 0) {
           try {
               return groupService.getGroupses().stream().filter(group -> group.getId().equals(Long.valueOf(value))).findFirst().get();
           } catch(NumberFormatException e) {
               throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid group."));
           }
        }

        return null;
    }

    @Override
    public String getAsString(final FacesContext context, final UIComponent component, final Object value) {
        if (value != null) {
            return String.valueOf(((Groups) value).getId());
        } else {
            return null;
        }
    }
}
