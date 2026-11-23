package com.tdc.nhom6.doanjava1.controller;
import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.entity.DigitalDocument;
import com.tdc.nhom6.doanjava1.service.DocumentService;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    @Autowired private DocumentService documentService;

    @GetMapping
    public String list(HttpSession session, Model model) {
        model.addAttribute("account",   session.getAttribute("currentUser"));
        model.addAttribute("documents", documentService.findAll());
        return "documents/list";
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        DigitalDocument doc = documentService.findById(id);
        if (doc == null) { response.setStatus(404); return; }
        documentService.incrementDownload(id);
        String ct = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";
        response.setContentType(ct);
        boolean isPdf = ct.contains("pdf");
        String disposition = isPdf ? "inline" : "attachment";
        String encodedName = URLEncoder.encode(doc.getFileName(), StandardCharsets.UTF_8).replace("+", "%20");
        response.setHeader("Content-Disposition", disposition + "; filename*=UTF-8''" + encodedName);
        response.getOutputStream().write(doc.getFileData());
    }

    @GetMapping("/upload")
    public String uploadForm(HttpSession session, Model model) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null) return "redirect:/login";
        model.addAttribute("account", current);
        return "documents/upload";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("title") String title,
                         @RequestParam(value = "author", required = false) String author,
                         @RequestParam(value = "description", required = false) String description,
                         @RequestParam(value = "documentType", required = false) String documentType,
                         HttpSession session, RedirectAttributes ra) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null) return "redirect:/login";
        try {
            if (file.isEmpty()) { ra.addFlashAttribute("error", "Vui lòng chọn file"); return "redirect:/documents/upload"; }
            documentService.upload(file, title, author, description, documentType, current.getId());
            ra.addFlashAttribute("success", "Đã upload tài liệu '" + title + "'!");
        } catch (Exception e) { ra.addFlashAttribute("error", "Lỗi: " + e.getMessage()); }
        return "redirect:/documents";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, HttpSession session, RedirectAttributes ra) {
        Account current = (Account) session.getAttribute("currentUser");
        if (current == null || !current.canManage()) return "redirect:/documents";
        documentService.delete(id);
        ra.addFlashAttribute("success", "Đã xóa tài liệu!");
        return "redirect:/documents";
    }
}
