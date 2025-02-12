package be.kdg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GuestPagesController {
    @GetMapping("/")
    public String homepage() {
        return "guest/homepage";
    }

    @GetMapping("/faq")
    public String faq() {
        return "guest/faq";
    }

    @GetMapping("/about")
    public String about() {
        return "guest/about";
    }

    @GetMapping("/purchase")
    public String purchase() {
        return "guest/purchase";
    }

    @GetMapping("/contact")
    public String contact() {
        return "guest/contact";
    }
}
