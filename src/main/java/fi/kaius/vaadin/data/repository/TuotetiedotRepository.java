package fi.kaius.vaadin.data.repository;

import fi.kaius.vaadin.data.entity.Tuotetiedot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TuotetiedotRepository extends JpaRepository<Tuotetiedot, Long> {
}