package at.peppol.webgui.app.components;

import java.io.File;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.peppol.webgui.upload.UploadManager;
import at.peppol.webgui.upload.UploadedResource;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class InvoiceUploadWindow extends VerticalLayout implements
                                                       Upload.Receiver,
                                                       Upload.FailedListener,
                                                       Upload.SucceededListener {
  private static final Logger logger = LoggerFactory.getLogger (InvoiceUploadWindow.class);
  private final Window subwindow;
  private File file;
  private final Label status = new Label ("Please select a file to upload");
  private final ProgressIndicator pi = new ProgressIndicator ();
  private final HorizontalLayout progressLayout = new HorizontalLayout ();
  private final Upload upload = new Upload (null, this);
  private UploadedResource ur;

  public InvoiceUploadWindow () {
    this.setSpacing (true);
    subwindow = new Window ("Upload Invoice");
    subwindow.addComponent (status);
    subwindow.addComponent (progressLayout);
    subwindow.addComponent (upload);
    subwindow.addStyleName ("upload-popup");
    subwindow.setModal (true);

    upload.setImmediate (true);
    upload.setButtonCaption ("Select local invoice");

    progressLayout.setSpacing (true);
    progressLayout.setVisible (false);
    progressLayout.addComponent (pi);
    progressLayout.setComponentAlignment (pi, Alignment.MIDDLE_LEFT);

    final Button cancelProcessing = new Button ("Cancel");
    cancelProcessing.addListener (new Button.ClickListener () {
      @Override
      public void buttonClick (final com.vaadin.ui.Button.ClickEvent event) {
        upload.interruptUpload ();
      }
    });
    cancelProcessing.setStyleName ("small");
    progressLayout.addComponent (cancelProcessing);
    upload.addListener (new Upload.StartedListener () {

      @Override
      public void uploadStarted (final StartedEvent event) {
        // This method gets called immediately after upload is started
        upload.setVisible (false);
        progressLayout.setVisible (true);
        pi.setValue (0f);
        pi.setPollingInterval (500);
        status.setValue ("Uploading file \"" + event.getFilename () + "\"");
      }
    });

    upload.addListener (new Upload.ProgressListener () {
      public void updateProgress (final long readBytes, final long contentLength) {
        // This method gets called several times during the update
        pi.setValue (new Float (readBytes / (float) contentLength));
      }
    });

    upload.addListener (new Upload.SucceededListener () {
      @Override
      public void uploadSucceeded (final SucceededEvent event) {
        // This method gets called when the upload finished successfully
        status.setValue ("Uploading file \"" + event.getFilename () + "\" succeeded");
      }
    });

    upload.addListener (new Upload.FailedListener () {
      @Override
      public void uploadFailed (final FailedEvent event) {
        status.setValue ("Uploading interrupted");
      }
    });

    upload.addListener (new Upload.FinishedListener () {
      @Override
      public void uploadFinished (final FinishedEvent event) {
        // This method gets called always when the upload finished, either
        // succeeding or failing
        progressLayout.setVisible (false);
        upload.setVisible (true);
        upload.setCaption ("Select another file");
      }
    });

  }

  @Override
  public Window getWindow () {
    return this.subwindow;
  }

  @Override
  public OutputStream receiveUpload (final String filename, final String mimeType) {
    ur = UploadManager.getInstance ().createManagedResource (filename);
    return ur.createOutputStream ();
  }

  public void uploadSucceeded (final SucceededEvent event) {
    if (ur != null)
      ur.setSuccess (true);
    else
      logger.warn ("Upload succeeded, but no Upload request present!");
  }

  public void uploadFailed (final FailedEvent event) {
    if (ur != null)
      ur.setSuccess (false);
    else
      logger.warn ("Upload succeeded, but no Upload request present!");
  }
}
