package com.api.libreria.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
    public List<LibroModel> obtenerLibros() {
        return this.libroService.obtenerLibros();
    }

    
    @PostMapping
    public ResponseEntity<?> guardarLibro(@RequestBody LibroModel libro) {
        try {
            LibroModel guardado = libroService.guardarLibro(libro);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping(path = "/{isbn}")
    public Optional<LibroModel> obtenerPorId(@PathVariable("isbn") Long isbn) {
        return this.libroService.obtenerPorId(isbn);
    }

    
    @PutMapping(path = "/{isbn}")
    public ResponseEntity<?> actualizarLibro( @RequestBody LibroModel request, @PathVariable("isbn") Long isbn ) {

        try {
            return ResponseEntity.ok(this.libroService.actualizarLibro(request, isbn));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }
   
    @DeleteMapping(path = "/{isbn}")
    public ResponseEntity<?> eliminarPorId(@PathVariable("isbn") Long isbn) {
        try {
            if (libroService.eliminarLibro(isbn)) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }

    @GetMapping(path = "/autor/{autor}")
    public List<LibroModel> buscarPorAutor(@PathVariable("autor") String autor) {
        return this.libroService.buscarPorAutor(autor);
    }

    @GetMapping(path = "/titulo/{titulo}")
    public List<LibroModel> buscarPorTitulo(@PathVariable("titulo") String titulo) {
        return this.libroService.buscarPorTitulo(titulo);
    }
}
