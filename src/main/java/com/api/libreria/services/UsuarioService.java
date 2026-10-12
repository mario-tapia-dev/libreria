package com.api.libreria.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.libreria.models.UsuarioModel;
import com.api.libreria.repositories.IUsuarioRepository;

@Service 
public class UsuarioService {
    
    @Autowired
    IUsuarioRepository usuarioRepository;
    
    public ArrayList<UsuarioModel> obtenerUsuarios() {
        return (ArrayList<UsuarioModel>) usuarioRepository.findAll();
    }

    public UsuarioModel guardarUsuario(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    public Optional<UsuarioModel> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public UsuarioModel actualizarUsuario(UsuarioModel request, Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow( () -> new IllegalArgumentException("No existe un usuario con ID: " + id));
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setContrasena(request.getContrasena());
        usuario.setRol(request.getRol());
        usuario.setEstado(request.getEstado());
        usuario.setFechaCreacion(request.getFechaCreacion());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setTelefono(request.getTelefono());
        usuario.setDireccion(request.getDireccion());

        return usuarioRepository.save(usuario);
    }

    public boolean eliminarUsuario(Long id) {
        try {
            usuarioRepository.deleteById(id);
            return true;
        } catch (Exception err) {
            return false;
        }
    }
}
