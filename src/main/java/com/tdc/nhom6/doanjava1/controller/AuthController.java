package com.tdc.nhom6.doanjava1.controller;

import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.service.AccountService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

	@Autowired
	private AccountService accountService;

	// ── Đăng nhập ────────────────────────────────────────────────────
	@GetMapping("/login")
	public String loginForm(HttpSession session) {
		if (session.getAttribute("currentUser") != null)
			return "redirect:/books";
		return "auth/login";
	}

	@PostMapping("/login")
	public String login(@RequestParam("username") String username, @RequestParam("password") String password,
			HttpSession session, RedirectAttributes ra) {
		try {
			Account account = accountService.login(username, password);
			session.setAttribute("currentUser", account);

			// Redirect về trang người dùng định vào trước khi bị chặn
			String target = (String) session.getAttribute("redirectAfterLogin");
			session.removeAttribute("redirectAfterLogin");
			return (target != null && !target.contains("/login")) ? "redirect:" + target : "redirect:/books";
		} catch (RuntimeException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/login";
		}
	}

	// ── Đăng ký ──────────────────────────────────────────────────────
	@GetMapping("/register")
	public String registerForm() {
		return "auth/register";
	}

	@PostMapping("/register")
	public String register(@RequestParam("username") String username, @RequestParam("password") String password,
			@RequestParam("fullName") String fullName, @RequestParam(value = "email", required = false) String email,
			RedirectAttributes ra) {
		try {
			accountService.register(username, password, fullName, email);
			ra.addFlashAttribute("success", "Đăng ký thành công! Hãy đăng nhập.");
			return "redirect:/login";
		} catch (RuntimeException e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/register";
		}
	}

	// ── Đăng xuất ────────────────────────────────────────────────────
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}

	// ── Hồ sơ ────────────────────────────────────────────────────────
	@GetMapping("/profile")
	public String profile(HttpSession session, Model model) {
		model.addAttribute("account", session.getAttribute("currentUser"));
		return "auth/profile";
	}
}
