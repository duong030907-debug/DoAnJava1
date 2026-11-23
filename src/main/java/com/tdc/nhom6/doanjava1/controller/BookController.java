package com.tdc.nhom6.doanjava1.controller;
import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.entity.Book;
import com.tdc.nhom6.doanjava1.service.BookService;
import com.tdc.nhom6.doanjava1.service.BorrowService;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired private BookService bookService;
    @Autowired private BorrowService borrowService;

    // ── Danh sách / Tìm kiếm ────────────────────────────────────────────
    @GetMapping
    public String list(@RequestParam(value = "keyword", required = false) String keyword,
                       @RequestParam(value = "category", required = false) String category,
                       @RequestParam(value = "availableOnly", required = false, defaultValue = "false") boolean availableOnly,
                       HttpSession session, Model model) {
        Account current = (Account) session.getAttribute("currentUser");
        List<Book> books;
        if (keyword != null && !keyword.isBlank())     books = bookService.search(keyword);
        else if (category != null && !category.isBlank()) books = bookService.findByCategory(category);
        else if (availableOnly)                            books = bookService.findAvailable();
        else                                               books = bookService.findAll();
        model.addAttribute("account",       current);
        model.addAttribute("books",         books);
        model.addAttribute("keyword",       keyword);
        model.addAttribute("selectedCat",   category);
        model.addAttribute("availableOnly", availableOnly);
        // ID các sách người dùng đang mượn — để hiện badge "Đang mượn" trên từng thẻ sách.
        // Khách chưa đăng nhập nhận tập rỗng nên JSP không cần kiểm tra null.
        Set<Long> borrowedIds = (current != null)
                ? borrowService.findActiveBookIds(current.getId())
                : new HashSet<>();
        model.addAttribute("borrowedIds", borrowedIds);
        return "books/list";
    }

    // ── Ảnh bìa ─────────────────────────────────────────────────────────
    @GetMapping("/{id}/cover")
    public void getCover(@PathVariable("id") Long id,
                         @RequestParam(value = "download", required = false, defaultValue = "false") boolean download,
                         HttpServletResponse response) throws IOException {
        Book book = bookService.findById(id);
        if (book == null || book.getCoverImage() == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); return;
        }
        String mime = book.getCoverType() != null ? book.getCoverType() : "image/jpeg";
        response.setContentType(mime);
        if (download) {
            String ext = mime.contains("png") ? "png" : "jpg";
            response.setHeader("Content-Disposition", "attachment; filename=cover_" + id + "." + ext);
        } else {
            response.setHeader("Content-Disposition", "inline");
        }
        response.getOutputStream().write(book.getCoverImage());
    }

    // ── Form thêm mới ───────────────────────────────────────────────────
    @GetMapping("/add")
    public String addForm(HttpSession session, Model model) {
        if (!canManage(session)) return "redirect:/books";
        model.addAttribute("account", session.getAttribute("currentUser"));
        model.addAttribute("book", new Book());
        return "books/form";
    }

    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book, HttpSession session, RedirectAttributes ra) {
        if (!canManage(session)) return "redirect:/books";
        try {
            bookService.save(book);
            ra.addFlashAttribute("success", "Đã thêm sách '" + book.getTitle() + "'!");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/books";
    }

    // ── Form sửa ────────────────────────────────────────────────────────
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, HttpSession session, Model model) {
        if (!canManage(session)) return "redirect:/books";
        Book book = bookService.findById(id);
        if (book == null) return "redirect:/books";
        model.addAttribute("account", session.getAttribute("currentUser"));
        model.addAttribute("book", book);
        return "books/form";
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable("id") Long id, @ModelAttribute Book updated,
                           HttpSession session, RedirectAttributes ra) {
        if (!canManage(session)) return "redirect:/books";
        try {
            bookService.update(id, updated);
            ra.addFlashAttribute("success", "Đã cập nhật sách!");
        } catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/books";
    }

    // ── Upload ảnh bìa ───────────────────────────────────────────────────
    @PostMapping("/{id}/cover")
    public String uploadCover(@PathVariable("id") Long id,
                              @RequestParam("file") MultipartFile file,
                              HttpSession session, RedirectAttributes ra) {
        if (!canManage(session)) return "redirect:/books";
        try {
            if (file.isEmpty()) { ra.addFlashAttribute("error", "Vui lòng chọn file ảnh"); return "redirect:/books/edit/" + id; }
            String ct = file.getContentType();
            if (ct == null || !ct.startsWith("image/")) { ra.addFlashAttribute("error", "Chỉ chấp nhận file ảnh"); return "redirect:/books/edit/" + id; }
            bookService.uploadCover(id, file);
            ra.addFlashAttribute("success", "Đã cập nhật ảnh bìa!");
        } catch (Exception e) { ra.addFlashAttribute("error", "Lỗi upload: " + e.getMessage()); }
        return "redirect:/books/edit/" + id;
    }

    // ── Xóa ─────────────────────────────────────────────────────────────
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession session, RedirectAttributes ra) {
        if (!canManage(session)) return "redirect:/books";
        try { bookService.delete(id); ra.addFlashAttribute("success", "Đã xóa sách!"); }
        catch (RuntimeException e) { ra.addFlashAttribute("error", e.getMessage()); }
        return "redirect:/books";
    }

    private boolean canManage(HttpSession session) {
        Account a = (Account) session.getAttribute("currentUser");
        return a != null && a.canManage();
    }
}
