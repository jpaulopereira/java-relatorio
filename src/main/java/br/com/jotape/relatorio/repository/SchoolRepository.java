package br.com.jotape.relatorio.repository;

import br.com.jotape.relatorio.model.School;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SchoolRepository extends JpaRepository<School, String> {

}
