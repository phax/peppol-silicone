package at.peppol.webgui.servlet;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationServlet;
import com.vaadin.ui.Window;

@SuppressWarnings ("serial")
public class WebGuiApplicationServlet extends ApplicationServlet {

  @Override
  protected void writeAjaxPageHtmlVaadinScripts(Window window,
        String themeName, Application application, BufferedWriter page,
        String appUrl, String themeUri, String appId,
        HttpServletRequest request) throws ServletException, IOException {
  
    page.write ("<link rel='stylesheet' href='VAADIN/bootstrap/css/bootstrap.min.css' type='text/css' />");
    page.write ("<script src='VAADIN/js/jquery-1.7.js' type='text/javascript'></script>");
    page.write ("<script src='VAADIN/bootstrap/js/bootstrap.min.js' type='text/javascript'></script>");
    page.write ("<script src='VAADIN/js/custom.js' type='text/javascript'></script>");
    
  
    super.writeAjaxPageHtmlVaadinScripts(window, themeName, application,
        page, appUrl, themeUri, appId, request);
  }


}
