package com.si.mindhealth.controllers;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.si.mindhealth.dtos.request.UserRequestDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;
import com.si.mindhealth.dtos.response.UserResponseDTO;
import com.si.mindhealth.entities.User;
import com.si.mindhealth.exceptions.AuthException;
import com.si.mindhealth.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public String manageUser(Model model, @RequestParam Map<String, String> params) {
        PageResponseDTO<User> response = userService.getList(params);
        User user = new User();
        model.addAttribute("newUser", user);
        model.addAttribute("users", response);
        return "userManagement";
    }

    @PostMapping("")
    public String addUser(Model model,
            @Valid @ModelAttribute("newUser") User u,
            BindingResult br,
            RedirectAttributes ra) {
        if (br.hasErrors()) {
            PageResponseDTO<User> response = userService.getList(Map.of());
            model.addAttribute("users", response);
            return "userManagement";
        }

        try {
            userService.addUser(u);
            ra.addFlashAttribute("successMessage", "Thêm người dùng mới thành công!");
            return "redirect:/users";
        } catch (Exception ex) {
            br.reject("add.failed", ex.getMessage() != null ? ex.getMessage() : "Có lỗi xảy ra, vui lòng thử lại.");
            PageResponseDTO<User> response = userService.getList(Map.of());
            model.addAttribute("users", response);
            return "userManagement";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model,
            RedirectAttributes ra) {
        try {
            userService.deleteUserById(id);
            ra.addFlashAttribute("successMessage", "Xóa người dùng thành công!");
            return "redirect:/users";
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage",
                    ex.getMessage() != null ? ex.getMessage() : "Có lỗi xảy ra, vui lòng thử lại.");
            return "redirect:/users";
        }
    }

    @GetMapping("/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("/{id}/edit")
    public String saveEditUser(@PathVariable Long id,
            @Valid @ModelAttribute("user") User user,
            BindingResult br,
            RedirectAttributes ra,
            Model model) {

        if (br.hasErrors()) {
            return "editUser";
        }
        try {
            user.setId(id);
            userService.updateUser(user);
            ra.addFlashAttribute("successMessage", "Cập nhật thành công!");
            return "redirect:/users/" + id + "/edit";
        } catch (Exception ex) {
            br.reject("update.failed", ex.getMessage());
            return "editUser";
        }
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
