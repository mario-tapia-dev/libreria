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

import com.api.libreria.services.UsuarioService;
import com.api.libreria.models.UsuarioModel;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioModel> obtenerUsuarios() {
        return this.usuarioService.obtenerUsuarios();
    }

    
    @PostMapping
    public ResponseEntity<?> guardarUsuario(@RequestBody UsuarioModel usuario) {
        try {
            UsuarioModel guardado = usuarioService.guardarUsuario(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    @GetMapping(path = "/{id}")
    public Optional<UsuarioModel> obtenerPorId(@PathVariable("id") Long id) {
        return this.usuarioService.obtenerPorId(id);
    }

    
    @PutMapping(path = "/{id}")
    public ResponseEntity<?> actualizarUsuario( @RequestBody UsuarioModel request, @PathVariable("id") Long id ) {

        try {
            return ResponseEntity.ok(this.usuarioService.actualizarUsuario(request, id));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
    }

    
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> eliminarPorId(@PathVariable("id") Long id) {
        try {
            if (usuarioService.eliminarUsuario(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
        }
    }
}
