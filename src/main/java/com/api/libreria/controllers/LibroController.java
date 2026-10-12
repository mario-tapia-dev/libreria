package com.api.libreria.controllers;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.libreria.services.LibroService;
import com.api.libreria.models.LibroModel;

@RestController
@RequestMapping("/libros")
public class LibroController {
    
    @Autowired
    private LibroService libroService;

    @GetMapping
    public ArrayList<LibroModel> obtenerLibros() {
        return this.libroService.obtenerLibros();
    }

    @PostMapping
    public LibroModel guardarLibro(@RequestBody LibroModel libro) {
        return this.libroService.guardarLibro(libro);
    }

    @GetMapping(path = "/{isbn}")
    public Optional<LibroModel> obtenerPorId(@PathVariable("isbn") Long isbn) {
        return this.libroService.obtenerPorId(isbn);
    }

    @PutMapping(path = "/{isbn}")
    public LibroModel actualizarLibro(@RequestBody LibroModel request, @PathVariable("isbn") Long isbn) {
        return this.libroService.actualizarLibro(request, isbn);
    }

    @DeleteMapping(path = "/{isbn}")
    public String eliminarPorId(@PathVariable("isbn") Long isbn) {
        boolean ok = this.libroService.eliminarLibro(isbn);

        if (ok) {
            return "Se eliminó el ibro con ISBN: " + isbn;
        } else {
            return "No se pudo eliminar el libro con ISBN: " + isbn;
        }
    }

    @GetMapping(path = "/autor/{autor}")
    public ArrayList<LibroModel> buscarPorAutor(@PathVariable("autor") String autor) {
        return this.libroService.buscarPorAutor(autor);
    }

    @GetMapping(path = "/titulo/{titulo}")
    public ArrayList<LibroModel> buscarPorTitulo(@PathVariable("titulo") String titulo) {
        return this.libroService.buscarPorTitulo(titulo);
    }
}
