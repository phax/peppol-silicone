package at.peppol.webgui.app.components;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressIndicator;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.FinishedEvent;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class OrderUploadWindow extends VerticalLayout implements Receiver {
  private Window subwindow;
  private File file;
  private Label status = new Label("Please select a file to upload");
  private ProgressIndicator pi = new ProgressIndicator();
  private HorizontalLayout progressLayout = new HorizontalLayout();
  private Upload upload = new Upload(null, this);

  public OrderUploadWindow() {
    this.setSpacing(true);
    subwindow = new Window("Upload Order");
    subwindow.addComponent(status);
    subwindow.addComponent(progressLayout);
    subwindow.addComponent (upload);  
    subwindow.addStyleName ("upload-popup");
    subwindow.setModal(true);

    upload.setImmediate(true);
    upload.setButtonCaption("Select local order");

    progressLayout.setSpacing(true);
    progressLayout.setVisible(false);
    progressLayout.addComponent(pi);
    progressLayout.setComponentAlignment(pi, Alignment.MIDDLE_LEFT);

    final Button cancelProcessing = new Button("Cancel");
    cancelProcessing.addListener(new Button.ClickListener() {
        @Override
        public void buttonClick (com.vaadin.ui.Button.ClickEvent event) {
          upload.interruptUpload();
        }
    });
    cancelProcessing.setStyleName("small");
    progressLayout.addComponent(cancelProcessing);
    upload.addListener(new Upload.StartedListener() {

      @Override
      public void uploadStarted (StartedEvent event) {
        // This method gets called immediately after upload is started
        upload.setVisible(false);
        progressLayout.setVisible(true);
        pi.setValue(0f);
        pi.setPollingInterval(500);
        status.setValue("Uploading file \"" + event.getFilename() + "\"");
      }
    });

    upload.addListener(new Upload.ProgressListener() {
      public void updateProgress(long readBytes, long contentLength) {
          // This method gets called several times during the update
          pi.setValue(new Float(readBytes / (float) contentLength));
      }
    });

    upload.addListener(new Upload.SucceededListener() {
      @Override
      public void uploadSucceeded (SucceededEvent event) {
        // This method gets called when the upload finished successfully
        status.setValue("Uploading file \"" + event.getFilename()  + "\" succeeded");
      }
    });

    upload.addListener(new Upload.FailedListener() {
      @Override
      public void uploadFailed (FailedEvent event) {
        status.setValue("Uploading interrupted");
      }
    });

    upload.addListener(new Upload.FinishedListener() {
      @Override
      public void uploadFinished (FinishedEvent event) {
        // This method gets called always when the upload finished, either succeeding or failing
        progressLayout.setVisible(false);
        upload.setVisible(true);
        upload.setCaption("Select another file");        
      }
    });
    
  }

  public Window getWindow(){
    return this.subwindow;
  }
  
  @Override
  public OutputStream receiveUpload (String filename, String mimeType) {
    
    FileOutputStream fos = null; // Output stream to write to
    file = new File("/tmp/" + filename);
    try {
      // Open the file for writing.
      fos = new FileOutputStream(file);
    } catch (final java.io.FileNotFoundException e) {
      // Error while opening the file. Not reported here.
      e.printStackTrace();
      return null;
    }
    
    return fos;
    
    /* API CALLBACK (bug: user not logged in
    UploadedResource ur = UploadManager.getInstance ().createManagedResource (filename);
    if(ur.isSuccess ()){
      System.out.println("success");
    }
    else{
      System.out.println("fail");
    }
    
    return null;
    //return UploadManager.getInstance ().createManagedResource (filename).createOutputStream ();
    */    
    
  }
  

  
}
