package com.si.mindhealth.controllers;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.si.mindhealth.dtos.response.OriginFeedbackObjectResponseDTO;
import com.si.mindhealth.dtos.response.PageResponseDTO;
import com.si.mindhealth.entities.Feedback;
import com.si.mindhealth.entities.ProcessingLog;
import com.si.mindhealth.entities.enums.TargetType;
import com.si.mindhealth.services.FeedbackService;
import com.si.mindhealth.services.MessageService;
import com.si.mindhealth.services.MoodResultService;
import com.si.mindhealth.services.ProcessingLogService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class FeedbackController {
    private final FeedbackService feedbackService;
    private final MoodResultService moodResultService;
    private final MessageService messageService;
    private final ProcessingLogService processingLogService;

    @GetMapping("/feedbacks")
    public String loadFeedbacks(Model model, @RequestParam Map<String, String> params) {
        PageResponseDTO<Feedback> feedbacks = feedbackService.getList(params);
        model.addAttribute("feedbacks", feedbacks);
        return "feedbackManagement";
    }

    @GetMapping("/feedbacks/{id}")
    public String loadFeedbackDetails(@PathVariable Long id, Model model) {
        Feedback feedback = feedbackService.get(id);

        Object o = null;
        TargetType type = feedback.getTargetType();
        if (type == TargetType.MESSAGE)
            o = messageService.get(feedback.getTargetId());
        else if (type == TargetType.MOOD_ENTRY) {
            o = moodResultService.get(feedback.getTargetId());
        }
        ProcessingLog log = processingLogService.get(type, feedback.getTargetId());

        OriginFeedbackObjectResponseDTO responseDTO = new OriginFeedbackObjectResponseDTO(o, type, log);

        model.addAttribute("feedback", feedback);
        model.addAttribute("originContent", responseDTO);
        return "feedbackDetails";
    }

    @PostMapping("/feedbacks/{id}/read")
    public String markedAsRead(@PathVariable Long id, RedirectAttributes ra) {
        try {
            feedbackService.markedAsRead(id);
            ra.addFlashAttribute("successMessage", "Đánh dấu là đã đọc thành công!");
            return "redirect:/feedbacks/" + id;
        } catch (Exception ex) {
            ra.addFlashAttribute("errorMessage",
                    ex.getMessage() != null ? ex.getMessage() : "Có lỗi xảy ra, vui lòng thử lại.");
            return "redirect:/feedbacks/" + id;
        }
    }
}
