package com.api.libreria.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public PrestamoModel registrarPrestamo(Long libroIsbn, Long socioId) {
        LibroModel libro = libroRepository.findById(libroIsbn)
                .orElseThrow(() -> new IllegalArgumentException("No existe un libro con ISBN: " + libroIsbn));
        UsuarioModel socio = usuarioRepository.findById(socioId)
                .orElseThrow(() -> new IllegalArgumentException("No existe un usuario con ID: " + socioId));

        PrestamoModel prestamo = new PrestamoModel();
        prestamo.setLibro(libro);
        prestamo.setSocio(socio);
        return prestamoRepository.save(prestamo);
    }

    public PrestamoModel registrarDevolucion(Long id) {
        PrestamoModel prestamo = prestamoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe un préstamo con ID: " + id));
        prestamo.setFechaDevolucion(LocalDateTime.now());
        return prestamoRepository.save(prestamo);
    }

    public boolean eliminarPrestamo(Long id) {
        try {
            prestamoRepository.deleteById(id);
            return true;
        } catch (Exception err) {
            return false;
        }
    }
}
