/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app;

import ap.peppol.webgui.security.user.IUser;
import ap.peppol.webgui.security.user.User;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.themes.Reindeer;
import javax.ws.rs.HEAD;

/**
 *
 * @author Jerouris
 */
public class MainWindow extends Window {
    
    private CssLayout topBarCSSLayout = new CssLayout();
    private HorizontalLayout topBarLayout = new HorizontalLayout();
    private HorizontalLayout middleContentLayout = new HorizontalLayout();
    private HorizontalLayout footerLayout = new HorizontalLayout();
    private HorizontalLayout topBarLayoutLeft;
    private HorizontalLayout topBarLayoutRight;
    
    public MainWindow()
    {
        super("PAWG Main");
        initUI();
    }

    private void initUI() {
        VerticalLayout root = new VerticalLayout();
        root.setMargin(false);
        setContent(root);
       // createTopBar();
       // Changed with menuBar -- under testing
        createMenuBar();
        
        
        // ------ START: Left NavBar -------
        CssLayout leftNavBar = new CssLayout();
        leftNavBar.setStyleName("sidebar-menu");
        leftNavBar.setSizeFull();
        leftNavBar.setWidth("220px");
        
        //User theUser = (User) getApplication().getUser();
        Label homeLbl = new Label("HOME");
        homeLbl.addStyleName("blue");
        leftNavBar.addComponent(homeLbl);
        
        leftNavBar.addComponent(new Label("INBOX"));
        NativeButton catalogueBtn = new NativeButton("Catalogue");
        catalogueBtn.setStyleName("selected");
        leftNavBar.addComponent(catalogueBtn);
        leftNavBar.addComponent(new NativeButton("Orders"));
        leftNavBar.addComponent(new NativeButton("Invoices"));
        
        leftNavBar.addComponent(new Label("DRAFTS"));
        leftNavBar.addComponent(new NativeButton("Catalogue"));
        leftNavBar.addComponent(new NativeButton("Orders"));
        leftNavBar.addComponent(new NativeButton("Invoices"));
        
        leftNavBar.addComponent(new Label("OUTBOX"));
        leftNavBar.addComponent(new NativeButton("Catalogue"));
        leftNavBar.addComponent(new NativeButton("Orders"));
        leftNavBar.addComponent(new NativeButton("Invoices"));
        
        leftNavBar.addComponent(new Label("SETTINGS"));
        leftNavBar.addComponent(new NativeButton("My Profile"));
        leftNavBar.addComponent(new NativeButton("Customers"));
        leftNavBar.addComponent(new NativeButton("Suppliers"));
        
        Embedded peppolLogoImg = new Embedded(null,
                                 new ExternalResource("img/peppol_logo.png"));

        peppolLogoImg.setStyleName("logo");
        leftNavBar.addComponent(peppolLogoImg);
        
        middleContentLayout.addComponent(leftNavBar);
        // ------ START: Main Content -------
        VerticalLayout mainContentLayout = new VerticalLayout();
        mainContentLayout.addStyleName("margin");
        VerticalLayout topmain = new VerticalLayout();
        topmain.setWidth("100%");
        Label bigPAWGLabel = new Label("PEPPOL Post Award Web GUI");
        bigPAWGLabel.setStyleName("huge");
        topmain.addComponent(bigPAWGLabel);
        Label blahContent = new Label("This is a mockup of the GUI that is going"
                                    + " to be the PAWG. It is created by the Greek"
                                    + " and Austrian teams as a fine replacement "
                                    + " of the Demo Client");
        blahContent.setWidth("80%");
        blahContent.addStyleName("big");
        topmain.addComponent(blahContent);
        Button learnMoreBtn = new Button("Learn More >>");
        learnMoreBtn.addStyleName("tall default");
        topmain.addComponent(learnMoreBtn);
        
        mainContentLayout.addComponent(topmain);
        // ------ END: Main Content ---------
        mainContentLayout.setHeight("100%");
        mainContentLayout.setSizeFull();
        
        mainContentLayout.setSpacing(true);
        mainContentLayout.setWidth("100%");
        middleContentLayout.addComponent(mainContentLayout);
        middleContentLayout.setExpandRatio(mainContentLayout, 1);
        middleContentLayout.setWidth("100%");
        middleContentLayout.setHeight("100%");
        // -------- 
        addComponent(middleContentLayout);
        addComponent(footerLayout);
    }

    private void createTopBar() {
       
        topBarCSSLayout.setStyleName("toolbar");
        topBarCSSLayout.setSizeFull();
        topBarLayout.setMargin(false, true, false, true);
        topBarLayout.setSizeFull();
        topBarLayoutLeft  = new HorizontalLayout();
        topBarLayoutRight = new HorizontalLayout();
        
        Label pawgLabel = new Label("PAWG");
        pawgLabel.setStyleName("h1");
        pawgLabel.setSizeUndefined();
        topBarLayoutLeft.addComponent(pawgLabel);
        
        HorizontalLayout segBtns = createTopBarButtons();
        topBarLayoutLeft.addComponent(segBtns);
        
       // IUser user = (IUser) PawgApp.getInstance().getUser();
        Label loggedInLabel = new Label("Test User");
        loggedInLabel.setSizeUndefined();
        topBarLayoutRight.addComponent(loggedInLabel);
        topBarLayoutRight.setComponentAlignment(loggedInLabel, Alignment.MIDDLE_RIGHT);
        topBarLayoutLeft.setComponentAlignment(segBtns, Alignment.MIDDLE_CENTER);
        topBarLayoutLeft.setSpacing(true);
        
        topBarLayout.addComponent(topBarLayoutLeft);
        topBarLayout.addComponent(topBarLayoutRight);
        topBarLayout.setComponentAlignment(topBarLayoutRight, Alignment.MIDDLE_RIGHT);
        
        topBarLayout.setExpandRatio(topBarLayoutLeft, 1);
        topBarLayout.setExpandRatio(topBarLayoutRight, 1);
        
        topBarCSSLayout.addComponent(topBarLayout);
        addComponent(topBarCSSLayout);
       
    }
    
    private void createMenuBar()    
    {
       
        topBarLayout.setMargin(false, false, false, false);
        topBarLayout.setSizeFull();
      //  topBarLayout.setStyleName("v-menubar");
        topBarLayoutLeft  = new HorizontalLayout();
        topBarLayoutRight = new HorizontalLayout();
        
//        Label pawgLabel = new Label("PAWG",Label.CONTENT_XHTML);
//        pawgLabel.setStyleName("v-menubar");
//        pawgLabel.addStyleName("v-label-big");
//        pawgLabel.setSizeFull();
//        topBarLayoutLeft.addComponent(pawgLabel);

        MenuBar lMenuBar = new MenuBar();
        lMenuBar.setHtmlContentAllowed(true);
        final MenuBar.MenuItem PAWGLabel = lMenuBar.addItem("<b>PAWG<b>", null);
        final MenuBar.MenuItem docItem = lMenuBar.addItem("Document", null);
        final MenuBar.MenuItem prefsItem = lMenuBar.addItem("Preferences", null);
        final MenuBar.MenuItem aboutItem = lMenuBar.addItem("About", null);
        lMenuBar.setSizeFull();
        
        final MenuBar.MenuItem invItem = docItem.addItem("Invoice", null);
        final MenuBar.MenuItem orderItem = docItem.addItem("Order", null);
        
        final MenuBar.MenuItem invCreateItem = invItem.addItem("New",null);
        final MenuBar.MenuItem invViewItem = invItem.addItem("View",null);
        
        final MenuBar.MenuItem ordCreateItem = orderItem.addItem("New",null);
        final MenuBar.MenuItem ordViewItem = orderItem.addItem("View",null);
        
        topBarLayoutLeft.addComponent(lMenuBar);
        Label loggedInLabel = new Label("Test User");

       //        loggedInLabel.addStyleName("v-menubar");
      // loggedInLabel.addStyleName("v-menubar-menuitem");
      //  loggedInLabel.setSizeUndefined();
      //  topBarLayoutRight.setStyleName("v-menubar");;
      //  topBarLayoutRight.setSizeUndefined();
      //  topBarLayoutRight.addComponent(loggedInLabel);
     //   topBarLayoutRight.setComponentAlignment(loggedInLabel, Alignment.MIDDLE_RIGHT);
        topBarLayoutLeft.setComponentAlignment(lMenuBar, Alignment.MIDDLE_CENTER);
        topBarLayoutLeft.setSpacing(false);
        topBarLayoutLeft.setSizeFull();
        topBarLayoutRight.setSizeUndefined();
        
        MenuBar rMenuBar = new MenuBar();
        rMenuBar.setHtmlContentAllowed(true);
        final MenuBar.MenuItem userLabel = rMenuBar.addItem("<b>User<b>", null);
        final MenuBar.MenuItem logoutLabel = userLabel.addItem("Logout", new MenuBar.Command() {

            @Override
            public void menuSelected(MenuItem selectedItem) {
                getApplication().close();
                
            }
        });
        topBarLayoutRight.addComponent(rMenuBar);
        topBarLayout.addComponent(topBarLayoutLeft);
        topBarLayout.addComponent(topBarLayoutRight);
        topBarLayout.setComponentAlignment(topBarLayoutRight, Alignment.MIDDLE_RIGHT);
      topBarLayout.setExpandRatio(topBarLayoutLeft, 1);
      //topBarLayout.setExpandRatio(topBarLayoutRight, 1);
        addComponent(topBarLayout);
        
    }
    
    private HorizontalLayout createTopBarButtons() {
        
        HorizontalLayout topBarBtns = new HorizontalLayout();
        
        Button homeBtn = new Button("Home");
        homeBtn.addStyleName("first");
        homeBtn.addStyleName("down");

        Button aboutBtn = new Button("About");
        Button contactBtn = new Button("Contact");
        contactBtn.addStyleName("last");
       
        topBarBtns.setStyleName("segment");
        topBarBtns.addStyleName("tall");
        topBarBtns.addComponent(homeBtn);
        topBarBtns.addComponent(aboutBtn);
        topBarBtns.addComponent(contactBtn);
        
        return topBarBtns;
    }
    
    private String getLoremIpsum() {
        
        String lorem = "Sed ultrices, est dapibus aliquet interdum, sapien "
                + "sapien elementum diam, sed tempus odio mi vitae felis. Donec"
                + " et tortor ipsum. Pellentesque est est, feugiat tincidunt "
                + "volutpat sed, facilisis ac est. Vestibulum convallis orci vel"
                + " justo hendrerit ac vulputate urna ornare. Pellentesque semper"
                + " consectetur tortor, eu egestas enim fringilla volutpat. "
                + "Donec blandit congue tellus, at faucibus erat luctus rutrum."
                + " Phasellus rhoncus turpis ut orci ornare vehicula. Etiam sem"
                + " neque, dictum ac commodo nec, molestie non enim. Aliquam "
                + "egestas sem eget sapien pellentesque vel scelerisque risus"
                + " semper. Vivamus ac nisi turpis, sit amet sagittis elit.";
        
        return lorem;
    } 
}
