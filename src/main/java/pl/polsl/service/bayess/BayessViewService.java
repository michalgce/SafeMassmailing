package pl.polsl.service.bayess;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.List;

@Service
@Scope("session")
public class BayessViewService {

    @Autowired
    protected BayessService bayessService;

    protected UploadedFile file;

    public String handleLearnNewFileUpload(FileUploadEvent fileUploadEvent) {
        file = fileUploadEvent.getFile();

        if (file != null) {

            try {
                final List<String> incomingText = IOUtils.readLines(file.getInputstream());
                final Boolean spam = bayessService.isThatSpam(incomingText);
                if (spam) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("THIS IS SPAM !!!"));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Uff.. Message look safety!"));
                }
            } catch (IOException e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning file was not uploaded", "Size of file is null. Is this ok? "));
            }
        }
        return null;
    }

    public String handleTestFileUpload(FileUploadEvent fileUploadEvent) {
        file = fileUploadEvent.getFile();

        if (file != null) {

            try {
                final List<String> incomingText = IOUtils.readLines(file.getInputstream());
                bayessService.learnNewSpamPattern(incomingText);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("File uploaded successfully"));
            } catch (IOException e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning file was not uploaded", "Size of file is null. Is this ok? "));
            }
        }
        return null;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(final UploadedFile file) {
        this.file = file;
    }
}
