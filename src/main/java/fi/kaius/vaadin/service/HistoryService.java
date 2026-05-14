package fi.kaius.vaadin.service;

import fi.kaius.vaadin.data.entity.Tuote;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Service
public class HistoryService {

    @PersistenceContext
    private final EntityManager entityManager;

    public HistoryService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<Object[]> haeKaikkiTuoteMuutokset() {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.createQuery()
                .forRevisionsOfEntity(Tuote.class, false, true)
                .addOrder(AuditEntity.revisionNumber().desc())
                .getResultList();
    }

    @Transactional(readOnly = true)
    public Tuote haeVersio(Long tuoteId, Number revisionNumber) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        return auditReader.find(Tuote.class, tuoteId, revisionNumber);
    }
}