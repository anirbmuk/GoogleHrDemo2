package blog.anirbanm.googlehr.view.bean;

import java.io.IOException;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import javax.security.auth.Subject;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.adf.view.rich.component.rich.input.RichInputText;

import weblogic.security.URLCallbackHandler;
import weblogic.security.services.Authentication;

import weblogic.servlet.security.ServletAuthentication;

public class ApplicationLogin {
    
    private RichInputText _username;
    private RichInputText _password;
    
    public ApplicationLogin() {
        super();
    }
    
    public void doLogin(final ActionEvent actionEvent) {
        final FacesContext ctx = FacesContext.getCurrentInstance();
        final ExternalContext ectx = ctx.getExternalContext();
        final HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
        final HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
        
        final byte[] pwd = ((String)getPassword().getValue()).getBytes();
        try {
            Subject mysubject = Authentication.login(new URLCallbackHandler(((String) getUsername().getValue()), pwd));
            ServletAuthentication.runAs(mysubject, request);
            ServletAuthentication.generateNewSessionID(request);
            sendForward(request, response, "/adfAuthentication?success_url=/faces/home.jspx");
        } catch (FailedLoginException e) {
            e.printStackTrace();
        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
    
    public void doLogout(ActionEvent actionEvent) {
        final FacesContext fctx = FacesContext.getCurrentInstance();
        final ExternalContext ectx = fctx.getExternalContext();
        final HttpServletRequest request = (HttpServletRequest) ectx.getRequest();
        final HttpServletResponse response = (HttpServletResponse) ectx.getResponse();
        sendForward(request, response, "/adfAuthentication?logout=true&end_url=/faces/login.jspx");
    }
    
    private void sendForward(HttpServletRequest request, HttpServletResponse response, String forwardUrl) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        RequestDispatcher dispatcher = request.getRequestDispatcher(forwardUrl);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ctx.responseComplete();
    }

    public void setUsername(RichInputText _username) {
        this._username = _username;
    }

    public RichInputText getUsername() {
        return _username;
    }

    public void setPassword(RichInputText _password) {
        this._password = _password;
    }

    public RichInputText getPassword() {
        return _password;
    }
}
