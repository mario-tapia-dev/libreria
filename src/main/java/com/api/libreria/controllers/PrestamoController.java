package com.api.libreria.controllers;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.api.libreria.models.PrestamoModel;
import com.api.libreria.services.PrestamoService;

@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    @Autowired
    private PrestamoService prestamoService;

    @GetMapping
    public ArrayList<PrestamoModel> obtenerPrestamos() {
        return this.prestamoService.obtenerPrestamos();
    }

    
    @PostMapping
    public ResponseEntity<?> registrarPrestamo(@RequestParam Long libroIsbn, @RequestParam Long socioId) {

        try {
            return ResponseEntity.ok(this.prestamoService.registrarPrestamo(libroIsbn, socioId));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping(path = "/{id}")
    public Optional<PrestamoModel> obtenerPorId(@PathVariable("id") Long id) {
        return this.prestamoService.obtenerPorId(id);
    }

    
    @PutMapping(path = "/{id}/devolucion")
    public ResponseEntity<?> registrarDevolucion(@PathVariable("id") Long id) {

        try {
            return ResponseEntity.ok(this.prestamoService.registrarDevolucion(id));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    @DeleteMapping(path = "/{id}")
    public String eliminarPorId(@PathVariable("id") Long id) {
        boolean ok = this.prestamoService.eliminarPrestamo(id);
        return ok ? "Se eliminó el préstamo con ID: " + id : "No se pudo eliminar el préstamo con ID: " + id;
    }
}
