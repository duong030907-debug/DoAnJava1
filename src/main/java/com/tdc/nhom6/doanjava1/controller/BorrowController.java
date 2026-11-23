package com.tdc.nhom6.doanjava1.controller;
import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.entity.BorrowRecord;
import com.tdc.nhom6.doanjava1.service.BorrowService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/borrow")
public class BorrowController {

    @Autowired private BorrowService borrowService;

    @GetMapping("/active")
    public String active(HttpSession session, Model model) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null) return "redirect:/login";
        model.addAttribute("account", current);
        model.addAttribute("records",
            current.canManage() ? borrowService.findActive() : borrowService.findActiveByAccount(current.getId()));
        return "borrow/active";
    }

    @GetMapping("/history")
    public String history(HttpSession session, Model model) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null) return "redirect:/login";
        model.addAttribute("account", current);
        model.addAttribute("records", borrowService.findByAccount(current.getId()));
        return "borrow/history";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") Long id, HttpSession session, Model model) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null) return "redirect:/login";
        BorrowRecord r = borrowService.findById(id);
        if (r == null || (!current.canManage() && !r.getAccount().getId().equals(current.getId()))) {
            return "redirect:/borrow/history";
        }
        model.addAttribute("account", current);
        model.addAttribute("record", r);
        return "borrow/detail";
    }

    @GetMapping("/overdue")
    public String overdue(HttpSession session, Model model) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null || !current.canManage()) return "redirect:/books";
        model.addAttribute("account", current);
        model.addAttribute("records", borrowService.findOverdue());
        return "borrow/overdue";
    }

    @PostMapping("/request")
    public String requestBorrow(@RequestParam("bookId") Long bookId,
                                @RequestParam(value = "notes", required = false) String notes,
                                HttpSession session, RedirectAttributes ra) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null) return "redirect:/login";
        try {
            borrowService.requestBorrow(current.getId(), bookId, notes);
            ra.addFlashAttribute("success", "Đã mượn sách thành công! Hạn trả: 14 ngày.");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/books";
    }

    @PostMapping("/return/{id}")
    public String returnBook(@PathVariable("id") Long id, HttpSession session, RedirectAttributes ra) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null || !current.canManage()) return "redirect:/login";
        try {
            BorrowRecord r = borrowService.returnBook(id);
            if (r.getFineAmount().signum() > 0) {
                ra.addFlashAttribute("success", String.format("Đã trả sách. Tiền phạt: %,.0f VND", r.getFineAmount()));
            } else {
                ra.addFlashAttribute("success", "Đã trả sách đúng hạn. Không có tiền phạt.");
            }
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/borrow/active";
    }

    @PostMapping("/pay-fine/{id}")
    public String payFine(@PathVariable("id") Long id, HttpSession session, RedirectAttributes ra) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null) return "redirect:/login";
        BorrowRecord r = borrowService.findById(id);
        if (r == null || (!current.canManage() && !r.getAccount().getId().equals(current.getId()))) {
            return "redirect:/borrow/history";
        }
        try {
            borrowService.payFine(id);
            ra.addFlashAttribute("success", "Đã thanh toán tiền phạt!");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/borrow/history";
    }
}
