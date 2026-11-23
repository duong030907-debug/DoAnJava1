package com.tdc.nhom6.doanjava1.service.impl;
import com.tdc.nhom6.doanjava1.dao.AccountDao;
import com.tdc.nhom6.doanjava1.dao.DigitalDocumentDao;
import com.tdc.nhom6.doanjava1.entity.Account;
import com.tdc.nhom6.doanjava1.entity.DigitalDocument;
import com.tdc.nhom6.doanjava1.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class DocumentServiceImpl implements DocumentService {

    @Autowired private DigitalDocumentDao docDao;
    @Autowired private AccountDao accountDao;

    @Override
    public DigitalDocument upload(MultipartFile file, String title, String author,
                                  String description, String documentType, Long uploaderId) throws IOException {
        DigitalDocument d = new DigitalDocument();
        d.setTitle(title);
        d.setAuthor(author);
        d.setDescription(description);
        d.setDocumentType(documentType != null ? documentType : "OTHER");
        d.setFileName(file.getOriginalFilename());
        d.setFileData(file.getBytes());
        d.setContentType(file.getContentType());
        d.setFileSize(file.getSize());
        if (uploaderId != null) {
            Account uploader = accountDao.findById(uploaderId);
            d.setUploadedBy(uploader);
        }
        d.setDownloadCount(0L);
        return docDao.save(d);
    }

    @Override public List<DigitalDocument> findAll()     { return docDao.findAll(); }
    @Override public DigitalDocument findById(Long id)   { return docDao.findById(id); }

    @Override
    public void delete(Long id) {
        DigitalDocument d = docDao.findById(id);
        if (d != null) docDao.delete(d);
    }

    @Override
    public void incrementDownload(Long id) {
        DigitalDocument d = docDao.findById(id);
        if (d != null) { d.setDownloadCount(d.getDownloadCount() + 1); docDao.save(d); }
    }
}
