
package com.api.libreria.services;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.libreria.models.UsuarioModel;
import com.api.libreria.repositories.IPrestamoRepository;
import com.api.libreria.repositories.IUsuarioRepository;

@Service
public class UsuarioService {

    private final IUsuarioRepository usuarioRepository;
    private final IPrestamoRepository prestamoRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UsuarioService(IUsuarioRepository usuarioRepository, IPrestamoRepository prestamoRepository) {
        this.usuarioRepository = usuarioRepository; 
        this.prestamoRepository = prestamoRepository;
    }

    public List<UsuarioModel> obtenerUsuarios() {
        return usuarioRepository.findAll();
    }

    public UsuarioModel guardarUsuario(UsuarioModel usuario) {
        validarUsuario(usuario, true);

        if (usuarioRepository.existsByCorreoIgnoreCase(
                usuario.getCorreo().trim())) {
            throw new IllegalArgumentException(
                    "Ya existe un usuario con ese correo.");
        }

        usuario.setNombre(usuario.getNombre().trim());
        usuario.setCorreo(usuario.getCorreo().trim());
        usuario.setRol(usuario.getRol().trim());
        usuario.setEstado(usuario.getEstado().trim());
        usuario.setContrasena(
                passwordEncoder.encode(usuario.getContrasena()));

        return usuarioRepository.save(usuario);
    }

    public Optional<UsuarioModel> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Transactional
    public UsuarioModel actualizarUsuario(UsuarioModel request, Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un usuario con ID: " + id));

        validarUsuario(request, false);

        String correo = request.getCorreo().trim();

        if (usuarioRepository.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new IllegalArgumentException(
                    "Ya existe otro usuario con ese correo.");
        }

        usuario.setNombre(request.getNombre().trim());
        usuario.setCorreo(correo);
        usuario.setRol(request.getRol().trim());
        usuario.setEstado(request.getEstado().trim());

        // Solo cambia la contraseña si se envió una nueva.
        if (request.getContrasena() != null
                && !request.getContrasena().isBlank()) {

            validarContrasena(request.getContrasena());
            usuario.setContrasena(
                    passwordEncoder.encode(request.getContrasena()));
        }

        // Los campos que no llegan en el formulario se conservan.
        if (request.getFechaNacimiento() != null
                && !request.getFechaNacimiento().isBlank()) {
            usuario.setFechaNacimiento(request.getFechaNacimiento().trim());
        }

        if (request.getTelefono() != null
                && !request.getTelefono().isBlank()) {
            usuario.setTelefono(request.getTelefono().trim());
        }

        if (request.getDireccion() != null
                && !request.getDireccion().isBlank()) {
            usuario.setDireccion(request.getDireccion().trim());
        }

        return usuarioRepository.save(usuario);
    }

    
    @Transactional
    public boolean eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }

        if (prestamoRepository.existsBySocio_Id(id)) {
            throw new IllegalArgumentException("No se puede eliminar el usuario porque tiene " + "préstamos registrados.");
        }

        usuarioRepository.deleteById(id);
        return true;
    }

    private void validarUsuario(UsuarioModel usuario,
            boolean requiereContrasena) {

        if (usuario == null) {
            throw new IllegalArgumentException(
                    "Los datos del usuario son obligatorios.");
        }

        if (usuario.getNombre() == null
                || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre es obligatorio.");
        }

        if (usuario.getCorreo() == null
                || !usuario.getCorreo().trim().matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException(
                    "Debes proporcionar un correo válido.");
        }

        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }

        if (usuario.getEstado() == null
                || usuario.getEstado().isBlank()) {
            throw new IllegalArgumentException(
                    "El estado es obligatorio.");
        }

        if (requiereContrasena
                && (usuario.getContrasena() == null
                || usuario.getContrasena().isBlank())) {
            throw new IllegalArgumentException(
                    "La contraseña es obligatoria.");
        }

        if (usuario.getContrasena() != null
                && !usuario.getContrasena().isBlank()) {
            validarContrasena(usuario.getContrasena());
        }
    }

    private void validarContrasena(String contrasena) {
        int bytes = contrasena.getBytes(StandardCharsets.UTF_8).length;

        if (contrasena.length() < 8) {
            throw new IllegalArgumentException(
                    "La contraseña debe tener al menos 8 caracteres.");
        }

        if (bytes > 72) {
            throw new IllegalArgumentException(
                    "La contraseña es demasiado larga; utiliza como máximo "
                    + "72 bytes en UTF-8.");
        }
    }
}
