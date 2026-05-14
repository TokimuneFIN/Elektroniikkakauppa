package fi.kaius.vaadin.data.repository;

import fi.kaius.vaadin.data.entity.Ominaisuus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OminaisuusRepository extends JpaRepository<Ominaisuus, Long> {
}