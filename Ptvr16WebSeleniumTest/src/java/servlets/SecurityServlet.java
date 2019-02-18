/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import entity.Book;
import entity.Reader;
import entity.User;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import session.BookFacade;
import session.ReaderFacade;
import session.UserFacade;
/**
 *
 * @author Melnikov
 */
@WebServlet(name = "SecurityServlet", urlPatterns = {
    "/showLogin",
    "/login",
    "/logout",
 
    "/showRegistration",
    "/registration",

    
    

})
public class SecurityServlet extends HttpServlet {
    @EJB BookFacade bookFacade;
    @EJB UserFacade userFacade;
    @EJB ReaderFacade readerFacade;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        User regUser = null;
        HttpSession session = request.getSession(false);
        String path = request.getServletPath();
        if(path != null)
            switch (path) {
                case "/showLogin":
                    request.getRequestDispatcher("/showLogin.jsp").forward(request, response);
                    break;
                case "/login":
                    String login = request.getParameter("login");
                    String password = request.getParameter("password");
                    regUser = userFacade.findByLogin(login);
                    if(regUser == null){
                       request.setAttribute("info", "Нет такого пользователя");
                       request.getRequestDispatcher("/index.jsp").forward(request, response); 
                    }
                    String encriptPass = setEncriptPass(password,"");
                    
                    if(encriptPass != null && !encriptPass.equals(regUser.getPassword())){
                       request.setAttribute("info", "Нет такого пользователя");
                       request.getRequestDispatcher("/index.jsp").forward(request, response); 
                    }
                    session = request.getSession(true);
                    session.setAttribute("regUser", regUser);
                    request.setAttribute("info", "Привет "+regUser.getReader().getName()+", Вы вошли");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                    break;
                case "/logout":
                    request.setAttribute("info", "Вы вышли");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                    break;
                case "/showRegistration":
                    request.getRequestDispatcher("/showRegistration.jsp").forward(request, response);
                    break; 
                case "/registration":
                    String name = request.getParameter("name");
                    String surname = request.getParameter("surname");
                    String email = request.getParameter("email");
                    login = request.getParameter("login");
                    String password1 = request.getParameter("password1");
                    String password2 = request.getParameter("password2");
                    if(!password1.equals(password2)){
                        request.setAttribute("info", "Нет такого пользователя");
                        request.getRequestDispatcher("/showRegistration.jsp").forward(request, response);
                    }
                    encriptPass = setEncriptPass(password1, "");
                    Reader reader = new Reader(email, name, surname);
                    readerFacade.create(reader);
                    User user = new User(login, encriptPass, true, reader);
                    userFacade.create(user);
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                    break;
             
            }
    }
    public String setEncriptPass(String password,String salts){
        password = salts+password;
        MessageDigest m;
        try { 
            m = MessageDigest.getInstance("SHA-256");
            m.update(password.getBytes(),0,password.length());
            return new BigInteger(1,m.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ManagerController.class.getName())
               .log(Level.SEVERE, 
                   "Не поддерживается алгоритм хеширования", ex);
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
