package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController {

    @GetMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String message = (String) request.getAttribute("javax.servlet.error.message");
        String exceptionType = (String) request.getAttribute("javax.servlet.error.exception_type");
        
        if (statusCode == null) {
            statusCode = 500;
        }
        if (message == null) {
            message = "Error desconocido";
        }
        if (exceptionType == null) {
            exceptionType = "Error";
        }
        
        model.addAttribute("status", statusCode);
        model.addAttribute("error", exceptionType);
        model.addAttribute("message", message);
        
        return "error";
    }
}
