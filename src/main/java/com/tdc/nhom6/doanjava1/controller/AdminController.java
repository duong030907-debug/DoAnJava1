package com.tdc.nhom6.doanjava1.controller;

import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.service.AccountService;
import com.tdc.nhom6.doanjava1.service.BorrowService;
import com.tdc.nhom6.doanjava1.service.StatsService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AccountService accountService;
	@Autowired
	private BorrowService borrowService;
	@Autowired
	private StatsService statsService;

	@GetMapping("/users")
	public String users(HttpSession session, Model model) {
		if (!canManage(session))
			return "redirect:/books";
		model.addAttribute("account", session.getAttribute("currentUser"));
		model.addAttribute("users", accountService.findAll());
		return "admin/users";
	}

	@GetMapping("/users/{id}/history")
	public String userHistory(@PathVariable("id") Long id, HttpSession session, Model model) {
		if (!canManage(session))
			return "redirect:/books";
		model.addAttribute("account", session.getAttribute("currentUser"));
		model.addAttribute("targetUser", accountService.findById(id));
		model.addAttribute("records", borrowService.findByAccount(id));
		return "admin/user-history";
	}

	@PostMapping("/users/{id}/promote")
	public String promote(@PathVariable("id") Long id, HttpSession session, RedirectAttributes ra) {
		if (!isAdmin(session))
			return "redirect:/books";
		try {
			accountService.promote(id);
			ra.addFlashAttribute("success", "Đã thăng cấp!");
		} catch (RuntimeException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/users";
	}

	@PostMapping("/users/{id}/demote")
	public String demote(@PathVariable("id") Long id, HttpSession session, RedirectAttributes ra) {
		if (!isAdmin(session))
			return "redirect:/books";
		try {
			accountService.demote(id);
			ra.addFlashAttribute("success", "Đã hạ cấp!");
		} catch (RuntimeException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/users";
	}

	@PostMapping("/users/{id}/delete")
	public String deleteUser(@PathVariable("id") Long id, HttpSession session, RedirectAttributes ra) {
		if (!isAdmin(session))
			return "redirect:/books";
		try {
			accountService.delete(id);
			ra.addFlashAttribute("success", "Đã vô hiệu hóa tài khoản!");
		} catch (RuntimeException e) {
			ra.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/users";
	}

	@GetMapping("/stats")
	public String stats(HttpSession session, Model model) {
		if (!canManage(session))
			return "redirect:/books";
		model.addAttribute("account", session.getAttribute("currentUser"));
		model.addAttribute("mostBorrowed", statsService.getMostBorrowed(10));
		model.addAttribute("inventory", statsService.getInventory());
		model.addAttribute("monthly", statsService.getMonthlyActivity());
		model.addAttribute("revenue", statsService.getRevenue());
		model.addAttribute("overdueCount", statsService.countOverdue());
		return "admin/stats";
	}

	private boolean isAdmin(HttpSession session) {
		Account a = (Account) session.getAttribute("currentUser");
		return a != null && a.isAdmin();
	}

	private boolean canManage(HttpSession session) {
		Account a = (Account) session.getAttribute("currentUser");
		return a != null && a.canManage();
	}
}
