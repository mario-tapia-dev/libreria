package com.api.libreria.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.libreria.models.UsuarioModel;
import com.api.libreria.services.UsuarioService;

@Controller
public class UsuarioVistaController {
    
    private final UsuarioService usuarioService;

    public UsuarioVistaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping({ "/", "/vista/usuarios" })
    public String mostrarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new UsuarioModel());
        }
        return "usuarios";
    }

    @PostMapping("/vista/usuarios/guardar")
    public String guardarUsuarios(@ModelAttribute UsuarioModel usuario, RedirectAttributes atributos) {
        usuarioService.guardarUsuario(usuario);
        atributos.addFlashAttribute("mensaje", "Usuario registrado correctamente.");
        return "redirect:/vista/usuarios";
    }

    @PostMapping("/vista/usuarios/{id}/actualizar")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute UsuarioModel usuario,
            RedirectAttributes atributos) {
        try {
            usuarioService.actualizarUsuario(usuario, id);
            atributos.addFlashAttribute("mensaje", "Usuario actualizado correctamente.");
        } catch (IllegalArgumentException exception) {
            atributos.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/vista/usuarios";
    }

    @PostMapping("/vista/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes atributos) {
        if (usuarioService.eliminarUsuario(id)) {
            atributos.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
        } else {
            atributos.addFlashAttribute("error", "No se pudo eliminar el usuario.");
        }
        return "redirect:/usuarios/libros";
    }

}
