package com.tdc.nhom6.doanjava1.dao;
import com.tdc.nhom6.doanjava1.entity.DigitalDocument;
import java.util.List;
public interface DigitalDocumentDao {
    DigitalDocument save(DigitalDocument doc);
    DigitalDocument findById(Long id);
    List<DigitalDocument> findAll();
    void delete(DigitalDocument doc);
    boolean existsById(Long id);
}
