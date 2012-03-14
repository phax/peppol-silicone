/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.peppol.webgui.app;

import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.*;
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

        // -------- START: Top Bar -----------
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
        
        
        Label loggedInLabel = new Label("Logged in as "+PawgApp.getInstance().user);
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
        // -------- END: Top Bar -----------
        
        // ------ START: Left NavBar -------
        CssLayout leftNavBar = new CssLayout();
        leftNavBar.setStyleName("sidebar-menu");
        leftNavBar.setSizeFull();
        leftNavBar.setWidth("220px");
        
        Label homeLbl = new Label("Home");
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
