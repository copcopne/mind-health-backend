package com.si.mindhealth.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.si.mindhealth.dtos.request.UserRequestDTO;
import com.si.mindhealth.dtos.response.UserResponseDTO;
import com.si.mindhealth.exceptions.AuthException;
import com.si.mindhealth.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public String manageUser() {
        return "userManagement";
    }

    @GetMapping("/profile")
    public String editProfile(Model model, Principal principal) {
        UserResponseDTO response = new UserResponseDTO(
                userService.getVerifiedUserByUsername(principal.getName()));
        model.addAttribute("profileForm", response);
        return "editProfile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @Valid @ModelAttribute("profileForm") UserRequestDTO request,
            BindingResult br,
            RedirectAttributes ra,
            Principal principal,
            Model model) {

        if (br.hasErrors()) {
            return "editProfile";
        }

        try {
            userService.updateUser(request, principal);
            ra.addFlashAttribute("successMessage", "Cập nhật thành công!");
            return "redirect:/users/profile";
        } catch (Exception ex) {
            br.reject("update.failed", ex.getMessage());
            return "editProfile";
        }
    }

    @GetMapping("/profile/edit-password")
    public String editPassword(Model model) {
        if (!model.containsAttribute("profileForm")) {
            model.addAttribute("profileForm", new UserRequestDTO());
        }
        return "editPassword";
    }

    @PostMapping("/profile/edit-password")
    public String updatePassword(
            @Valid @ModelAttribute("profileForm") UserRequestDTO request,
            BindingResult br,
            RedirectAttributes ra,
            Principal principal,
            Model model) {

        if (request.getPassword() == null || request.getConfirm() == null
                || !request.getPassword().equals(request.getConfirm())) {
            br.rejectValue("confirm", "password.mismatch", "Mật khẩu xác nhận không khớp");
            return "editPassword";
        }

        if (br.hasErrors()) {
            return "editPassword";
        }

        try {
            userService.updateUser(request, principal);
            ra.addFlashAttribute("successMessage", "Đổi mật khẩu thành công!");
            return "redirect:/users/profile/edit-password";
        } catch (AuthException ex) {
            br.rejectValue("oldPassword", "invalid.oldPassword",
                    ex.getMessage() != null ? ex.getMessage() : "Mật khẩu hiện tại không đúng");
            return "editPassword";
        } catch (Exception ex) {
            br.reject("changePassword.failed", "Có lỗi xảy ra. Vui lòng thử lại.");
            return "editPassword";
        }
    }
}
