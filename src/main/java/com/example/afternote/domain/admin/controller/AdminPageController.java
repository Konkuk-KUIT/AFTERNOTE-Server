package com.example.afternote.domain.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping("/verifications")
    public String verificationsPage() {
        return "admin/verifications";
    }

    @GetMapping("/verifications/{id}")
    public String verificationDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("verificationId", id);
        return "admin/verification-detail";
    }
}
