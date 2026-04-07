package es.codeurjc.daw.library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CustomErrorController {

    // Custom handler for all error requests; security forwards here
    @RequestMapping({"/error", "/security-error"})
    public String handleError(HttpServletRequest request, HttpServletResponse response, Model model) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String originalMessage = (String) request.getAttribute("javax.servlet.error.message");

        // Fallback to HTTP response status when the servlet error attribute is missing or invalid
        if (statusCode == null || statusCode == 0) {
            statusCode = response.getStatus();
        }
        if (statusCode == null || statusCode == 0) {
            statusCode = 500;
        }

        String errorTitle;
        String friendlyMessage;

        switch (statusCode) {
            case 401:
                errorTitle = "No autenticado";
                friendlyMessage = "Debes iniciar sesión para acceder a este recurso.";
                break;
            case 403:
                errorTitle = "Acceso denegado";
                friendlyMessage = "No tienes permisos suficientes para acceder a este contenido.";
                break;
            case 404:
                errorTitle = "Página no encontrada";
                friendlyMessage = "La página que buscas no existe o ha sido movida.";
                break;
            case 500:
                errorTitle = "Error interno del servidor";
                friendlyMessage = "Se ha producido un error interno. Inténtalo de nuevo más tarde.";
                break;
            default:
                errorTitle = "Error";
                friendlyMessage = originalMessage != null && !originalMessage.isBlank()
                        ? originalMessage
                        : "Se ha producido un error inesperado.";
                break;
        }

        model.addAttribute("status", statusCode);
        model.addAttribute("error", errorTitle);
        model.addAttribute("message", friendlyMessage);

        return "error";
    }
}
