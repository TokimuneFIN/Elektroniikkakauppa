package fi.kaius.vaadin.data.repository;

import fi.kaius.vaadin.data.entity.Tuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TuoteRepository extends JpaRepository<Tuote, Long>, JpaSpecificationExecutor<Tuote> {
}