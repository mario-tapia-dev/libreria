
package com.api.libreria.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.libreria.models.LibroModel;
import com.api.libreria.repositories.ILibroRepository;
import com.api.libreria.repositories.IPrestamoRepository;

@Service
public class LibroService {

    private final ILibroRepository libroRepository;
    private final IPrestamoRepository prestamoRepository;

    public LibroService(ILibroRepository libroRepository,
            IPrestamoRepository prestamoRepository) {
        this.libroRepository = libroRepository;
        this.prestamoRepository = prestamoRepository;
    }

    public List<LibroModel> obtenerLibros() {
        return libroRepository.findAll();
    }

    public LibroModel guardarLibro(LibroModel libro) {
        validarLibro(libro);
        return libroRepository.save(libro);
    }

    public Optional<LibroModel> obtenerPorId(Long isbn) {
        return libroRepository.findById(isbn);
    }

    @Transactional
    public LibroModel actualizarLibro(LibroModel request, Long isbn) {
        LibroModel libro = libroRepository.findById(isbn)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un libro con ISBN: " + isbn));

        validarLibro(request);

        libro.setTitulo(request.getTitulo().trim());
        libro.setAutor(request.getAutor().trim());
        libro.setAñoPublicacion(request.getAñoPublicacion().trim());
        libro.setNumeroEjemplares(request.getNumeroEjemplares());

        return libroRepository.save(libro);
    }

    @Transactional
    public boolean eliminarLibro(Long isbn) {
        if (!libroRepository.existsById(isbn)) {
            return false;
        }

        if (prestamoRepository.existsByLibro_Isbn(isbn)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el libro porque tiene "
                    + "préstamos registrados.");
        }

        libroRepository.deleteById(isbn);
        return true;
    }

    public List<LibroModel> buscarPorAutor(String autor) {
        if (autor == null || autor.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes indicar el autor que quieres buscar.");
        }

        return libroRepository.findByAutorContainingIgnoreCase(
                autor.trim());
    }

    public List<LibroModel> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException(
                    "Debes indicar el título que quieres buscar.");
        }

        return libroRepository.findByTituloContainingIgnoreCase(
                titulo.trim());
    }

    private void validarLibro(LibroModel libro) {
        if (libro == null) {
            throw new IllegalArgumentException(
                    "Los datos del libro son obligatorios.");
        }

        if (libro.getTitulo() == null || libro.getTitulo().isBlank()) {
            throw new IllegalArgumentException(
                    "El título es obligatorio.");
        }

        if (libro.getAutor() == null || libro.getAutor().isBlank()) {
            throw new IllegalArgumentException(
                    "El autor es obligatorio.");
        }

        if (libro.getAñoPublicacion() == null
                || !libro.getAñoPublicacion().trim()
                        .matches("\\d{4}")) {
            throw new IllegalArgumentException(
                    "El año de publicación debe tener cuatro dígitos.");
        }

        int año = Integer.parseInt(libro.getAñoPublicacion().trim());

        if (año < 1 || año > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException(
                    "El año de publicación no es válido.");
        }

        if (libro.getNumeroEjemplares() < 0) {
            throw new IllegalArgumentException(
                    "Los ejemplares disponibles no pueden ser negativos.");
        }
    }
}
