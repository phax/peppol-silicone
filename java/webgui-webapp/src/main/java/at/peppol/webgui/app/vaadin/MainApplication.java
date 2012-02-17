package at.peppol.webgui.app.vaadin;

import com.vaadin.Application;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MainApplication extends Application {
  private final Button newContact = new Button ("Add contact");
  private final Button search = new Button ("Search");
  private final Button share = new Button ("Share");
  private final Button help = new Button ("Help");
  private final HorizontalSplitPanel horizontalSplit = new HorizontalSplitPanel ();

  private void buildMainLayout () {
    setMainWindow (new Window ("Address Book Demo application"));
    final VerticalLayout layout = new VerticalLayout ();
    layout.setSizeFull ();

    layout.addComponent (createToolbar ());
    layout.addComponent (horizontalSplit);

    /* Allocate all available extra space to the horizontal split panel */
    layout.setExpandRatio (horizontalSplit, 1);
    /*
     * Set the initial split position so we can have a 200 pixel menu to the
     * left
     */
    horizontalSplit.setSplitPosition (200, Sizeable.UNITS_PIXELS);

    getMainWindow ().setContent (layout);

  }

  public HorizontalLayout createToolbar () {

    final HorizontalLayout lo = new HorizontalLayout ();
    lo.addComponent (newContact);
    lo.addComponent (search);
    lo.addComponent (share);
    lo.addComponent (help);

    return lo;

  }

  @Override
  public void init () {
    buildMainLayout ();
  }
}
