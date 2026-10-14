
package com.api.libreria.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.libreria.models.LibroModel;
import com.api.libreria.models.PrestamoModel;
import com.api.libreria.models.UsuarioModel;
import com.api.libreria.repositories.IPrestamoRepository;
import com.api.libreria.repositories.ILibroRepository;
import com.api.libreria.repositories.IUsuarioRepository;

@Service
public class PrestamoService {

    @Autowired
    IPrestamoRepository prestamoRepository;

    @Autowired
    ILibroRepository libroRepository;

    @Autowired
    IUsuarioRepository usuarioRepository;

    public ArrayList<PrestamoModel> obtenerPrestamos() {
        return (ArrayList<PrestamoModel>) prestamoRepository.findAll();
    }

    public Optional<PrestamoModel> obtenerPorId(Long id) {
        return prestamoRepository.findById(id);
    }

    @Transactional
    public PrestamoModel registrarPrestamo(Long libroIsbn, Long socioId) {

        LibroModel libro = libroRepository.findById(libroIsbn)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un libro con ISBN: " + libroIsbn));

        UsuarioModel socio = usuarioRepository.findById(socioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un usuario con ID: " + socioId));

        if (libro.getNumeroEjemplares() <= 0) {
            throw new IllegalArgumentException(
                    "No hay ejemplares disponibles de este libro.");
        }

        libro.setNumeroEjemplares(libro.getNumeroEjemplares() - 1);
        libroRepository.save(libro);

        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setLibro(libro);
        prestamo.setSocio(socio);

        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public PrestamoModel registrarDevolucion(Long id) {

        PrestamoModel prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un préstamo con ID: " + id));

        if (prestamo.getFechaDevolucion() != null) {
            throw new IllegalArgumentException(
                    "Este préstamo ya fue devuelto.");
        }

        LibroModel libro = prestamo.getLibro();

        libro.setNumeroEjemplares(libro.getNumeroEjemplares() + 1);
        libroRepository.save(libro);

        prestamo.setFechaDevolucion(LocalDateTime.now());

        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public boolean eliminarPrestamo(Long id) {

        Optional<PrestamoModel> resultado = prestamoRepository.findById(id);

        if (resultado.isEmpty()) {
            return false;
        }

        PrestamoModel prestamo = resultado.get();

        // Si el préstamo sigue activo, recuperamos el ejemplar.
        if (prestamo.getFechaDevolucion() == null) {
            LibroModel libro = prestamo.getLibro();

            libro.setNumeroEjemplares(libro.getNumeroEjemplares() + 1);
            libroRepository.save(libro);
        }

        prestamoRepository.delete(prestamo);

        return true;
    }
}
