package com.example.parking.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthWebController {

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        @RequestParam(required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Identifiants incorrects ou compte desactive.");
        }
        if (logout != null) {
            model.addAttribute("infoMessage", "Vous etes deconnecte.");
        }
        return "login";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorTitle", "Acces refuse");
        model.addAttribute("errorMessage", "Votre role ne permet pas d'acceder a cette page.");
        model.addAttribute("statusCode", 403);
        return "error/403";
    }
}
