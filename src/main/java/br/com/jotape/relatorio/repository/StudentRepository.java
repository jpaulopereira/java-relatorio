package br.com.jotape.relatorio.repository;

import br.com.jotape.relatorio.model.School;
import br.com.jotape.relatorio.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    int countBySchool(School school);

}
