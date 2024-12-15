package com.example.demo.contollers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {
    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication){
        if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))){
            return "redirect:/admin";
        }
        else if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("INSTRUCTOR"))){
            return "redirect:/instructor";
        }
        else if(authentication.getAuthorities().contains(new SimpleGrantedAuthority("STUDENT"))){
            return "redirect:/student";
        }
        else{
            return "redirect:/api/hello";
        }
    }

}
