package com.tdc.nhom6.doanjava1.dao.impl;
import com.tdc.nhom6.doanjava1.dao.DigitalDocumentDao;
import com.tdc.nhom6.doanjava1.entity.DigitalDocument;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class DigitalDocumentDaoImpl implements DigitalDocumentDao {

    @PersistenceContext
    private EntityManager em;

    @Override public DigitalDocument save(DigitalDocument d)  { return em.merge(d); }
    @Override public DigitalDocument findById(Long id)        { return em.find(DigitalDocument.class, id); }
    @Override public boolean existsById(Long id)              { return findById(id) != null; }

    @Override
    public List<DigitalDocument> findAll() {
        return em.createQuery("SELECT d FROM DigitalDocument d ORDER BY d.uploadedAt DESC", DigitalDocument.class).getResultList();
    }

    @Override
    public void delete(DigitalDocument d) {
        DigitalDocument managed = em.contains(d) ? d : em.merge(d);
        em.remove(managed);
    }
}
