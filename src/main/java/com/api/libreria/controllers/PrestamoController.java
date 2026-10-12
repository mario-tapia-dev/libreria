package com.api.libreria.controllers;

import java.util.ArrayList;
import java.util.Optional;

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
    public PrestamoModel registrarPrestamo(@RequestParam Long libroIsbn, @RequestParam Long socioId) {
        return this.prestamoService.registrarPrestamo(libroIsbn, socioId);
    }

    @GetMapping(path = "/{id}")
    public Optional<PrestamoModel> obtenerPorId(@PathVariable("id") Long id) {
        return this.prestamoService.obtenerPorId(id);
    }

    @PutMapping(path = "/{id}/devolucion")
    public PrestamoModel registrarDevolucion(@PathVariable("id") Long id) {
        return this.prestamoService.registrarDevolucion(id);
    }

    @DeleteMapping(path = "/{id}")
    public String eliminarPorId(@PathVariable("id") Long id) {
        boolean ok = this.prestamoService.eliminarPrestamo(id);
        return ok ? "Se eliminó el préstamo con ID: " + id : "No se pudo eliminar el préstamo con ID: " + id;
    }
}
