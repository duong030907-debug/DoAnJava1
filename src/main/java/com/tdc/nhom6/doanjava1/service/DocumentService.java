package com.tdc.nhom6.doanjava1.service;
import com.tdc.nhom6.doanjava1.entity.DigitalDocument;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
public interface DocumentService {
    DigitalDocument upload(MultipartFile file, String title, String author, String description, String documentType, Long uploaderId) throws IOException;
    List<DigitalDocument> findAll();
    DigitalDocument findById(Long id);
    void delete(Long id);
    void incrementDownload(Long id);
}
