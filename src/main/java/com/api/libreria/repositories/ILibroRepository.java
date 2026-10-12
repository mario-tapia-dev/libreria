package com.api.libreria.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import com.api.libreria.models.LibroModel;

public interface ILibroRepository extends JpaRepository<LibroModel, Long> {
    ArrayList<LibroModel> findByAutor(String autor);
    ArrayList<LibroModel> findByTitulo(String titulo);

}
