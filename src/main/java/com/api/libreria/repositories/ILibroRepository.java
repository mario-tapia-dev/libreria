
package com.api.libreria.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.libreria.models.LibroModel;

public interface ILibroRepository extends JpaRepository<LibroModel, Long> {

    List<LibroModel> findByAutorContainingIgnoreCase(String autor);

    List<LibroModel> findByTituloContainingIgnoreCase(String titulo);

    boolean existsByIsbn(Long isbn);
}
