package com.api.libreria.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.libreria.services.LibroService;
import com.api.libreria.services.PrestamoService;
import com.api.libreria.services.UsuarioService;

@Controller
public class PrestamoVistaController {

    private final PrestamoService prestamoService;
    private final LibroService libroService;
    private final UsuarioService usuarioService;

    public PrestamoVistaController(PrestamoService prestamoService, LibroService libroService, UsuarioService usuarioService) {
        this.prestamoService = prestamoService;
        this.libroService = libroService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/vista/prestamos")
    public String mostrarPrestamos(Model model) {
        model.addAttribute("prestamos", prestamoService.obtenerPrestamos());
        model.addAttribute("libros", libroService.obtenerLibros());
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
        return "prestamos";
    }

    @PostMapping("/vista/prestamos/guardar")
    public String registrarPrestamo(@RequestParam Long libroIsbn, @RequestParam Long socioId, RedirectAttributes atributos) {
        try {
            prestamoService.registrarPrestamo(libroIsbn, socioId);
            atributos.addFlashAttribute("mensaje", "Préstamo registrado correctamente.");
        } catch (IllegalArgumentException exception) {
            atributos.addFlashAttribute("error", exception.getMessage());
        }
        return "redirect:/vista/prestamos";
    }

    @PostMapping("/vista/prestamos/{id}/devolucion")
    public String registrarDevolucion(@PathVariable Long id, RedirectAttributes atributos) {
        prestamoService.registrarDevolucion(id);
        atributos.addFlashAttribute("mensaje", "Devolución registrada.");
        return "redirect:/vista/prestamos";
    }

    @PostMapping("/vista/prestamos/{id}/eliminar")
    public String eliminarPrestamo(@PathVariable Long id, RedirectAttributes atributos) {
        prestamoService.eliminarPrestamo(id);
        atributos.addFlashAttribute("mensaje", "Préstamo eliminado.");
        return "redirect:/vista/prestamos";
    }
}
