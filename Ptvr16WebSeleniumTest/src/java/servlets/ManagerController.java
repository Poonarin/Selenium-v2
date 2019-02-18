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


@WebServlet(name = "Controller", urlPatterns = {
    "/showAddNewBook",
    "/addBook",
    "/deleteBook",
    "/deleteUser",
    
    

})
public class ManagerController extends HttpServlet {
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
              
                case "/showAddNewBook":
                    request.getRequestDispatcher("/showAddNewBook.jsp").forward(request, response);
                    break;
                case "/deleteBook":
                  if(session == null){
                        request.getRequestDispatcher("/showLogin").forward(request, response);
                       break;
                    }
                    regUser = (User) session.getAttribute("regUser");
                    if(regUser == null){
                        request.getRequestDispatcher("/showLogin").forward(request, response);
                        break;
                    }
                    Book book = bookFacade.findByIsbn("TestIsbn");
                    bookFacade.remove(book);
                    request.setAttribute("info", "тестовая книга удалена");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                    break;    
                case "/deleteUser":
                    if(session == null){
                        request.getRequestDispatcher("/showLogin").forward(request, response);
                        break;
                    }
                    regUser = (User) session.getAttribute("regUser");
                    if(regUser == null){
                        request.getRequestDispatcher("/showLogin").forward(request, response);
                        break;
                    }
                    User user = userFacade.findByLogin("TestLogin");
                    Reader reader = user.getReader();
                    userFacade.remove(user);
                    readerFacade.remove(reader);
                    request.setAttribute("info", "тестовый пользователь удален");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                    break;  
                case "/addBook":
                    if(session == null){
                        request.getRequestDispatcher("/showLogin").forward(request, response);
                        break;
                    }
                    regUser = (User) session.getAttribute("regUser");
                    if(regUser == null){
                        request.getRequestDispatcher("/showLogin").forward(request, response);
                        break;
                    }
                    String name = request.getParameter("name");
                    String author = request.getParameter("author");
                    String isbn = request.getParameter("isbn");
                    String count = request.getParameter("count");
                    book = new Book(isbn, name, author, Integer.parseInt(count));
                    bookFacade.create(book);
                    request.setAttribute("info", "Новая книга добавлена");
                    request.getRequestDispatcher("/index.jsp").forward(request, response);
                    break;
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
