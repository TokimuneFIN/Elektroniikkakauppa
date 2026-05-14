package fi.kaius.vaadin.data.repository;

import fi.kaius.vaadin.data.entity.Kategoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KategoriaRepository extends JpaRepository<Kategoria, Long> {
}