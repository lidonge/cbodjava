package free.servpp.cbodjava.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebUiController {

    @GetMapping("/webui")
    public String webui() {
        return "redirect:/webui/";
    }

    @GetMapping("/webui/")
    public String webuiIndex() {
        return "redirect:/webui/index.html";
    }
}
