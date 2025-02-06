package zegel.edu.pe.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import zegel.edu.pe.models.Eventos;
import zegel.edu.pe.models.Usuarios;
import zegel.edu.pe.services.CategoriasServices;
import zegel.edu.pe.services.EventosServices;
import zegel.edu.pe.services.UsuariosServices;

@Controller
@RequestMapping("/inicio")
public class VComController {	
	
	@Autowired
	private EventosServices eveS;
	@Autowired
	private UsuariosServices usuS;
	@Autowired
	private CategoriasServices catS;

	@GetMapping("/inicio")
	public String incio(HttpServletRequest request, Model modelo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		modelo.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
		modelo.addAttribute("currentUri", request.getRequestURI());
		return "VCom/index";
	}
	
	@GetMapping("/categorias")
	public String inicioCategorias(HttpServletRequest request, Model modelo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		modelo.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
		modelo.addAttribute("currentUri", request.getRequestURI());
		return "VCom/descripcion_categoria";
	}
	
	@GetMapping("/niveles")
	public String inicioNiveles(HttpServletRequest request, Model modelo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		modelo.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
		modelo.addAttribute("currentUri", request.getRequestURI());
		return "VCom/descripcion_niveles";
	}
	
	@GetMapping("/sedes")
	public String inicioSedes(HttpServletRequest request, Model modelo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		modelo.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
		modelo.addAttribute("currentUri", request.getRequestURI());
		return "VCom/PaginaPrincipal-Sedes";
	}
	
	@GetMapping("/clubes")
	public String inicioClubes(HttpServletRequest request, Model modelo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		modelo.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
		modelo.addAttribute("currentUri", request.getRequestURI());
		return "VCom/inscripcion_club";
	}

//Perfil
	@GetMapping({"/perfil"})
	public String mostrar(HttpServletRequest request, Model modelo) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		modelo.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
		modelo.addAttribute("currentUri", request.getRequestURI());
		
	    String username = authentication.getName();
	    Usuarios usuarioAutenticado = usuS.CorreoUsuario(username);
	    if (usuarioAutenticado == null) {
	        return "redirect:/login";
	    }
	    
	    Integer categoriaId = usuarioAutenticado.getCategorias().getId();
	    List<Eventos> eventosPorCategoria = eveS.getEventosPorCategoria(categoriaId);
	    
	    List<Usuarios> usuariosPorCategoria = usuS.getListUsuariosPorCategoria(
	            usuarioAutenticado.getCategorias().getId());
	    
	    String foto = usuarioAutenticado.getFoto();
	    if (foto == null || foto.isEmpty()) {
	        foto = "default.jpg";
	    }
	    		
		modelo.addAttribute("usuar", usuarioAutenticado);
		modelo.addAttribute("correo", usuarioAutenticado.getCorreo());
		modelo.addAttribute("foto", foto);
		modelo.addAttribute("categoria", usuarioAutenticado.getCategorias().getNombre());
		modelo.addAttribute("nivel", usuarioAutenticado.getNiveles().getNombre());
		modelo.addAttribute("puntaje", usuarioAutenticado.getPuntaje().getPuntaje());
		modelo.addAttribute("eventos", eventosPorCategoria);
		modelo.addAttribute("usuario", usuariosPorCategoria);
		
		
		return "usuarios/perfil";
	}
	
//Método para obtener ID del evento
	@GetMapping("/evento/id/{id}")
	public ResponseEntity<Eventos> getEventoId(@PathVariable("id") int id) { 
		Eventos evento = eveS.getListEventosId(id);
		if (evento != null) {
			return ResponseEntity.ok(evento);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping({"/perfil/orden-merito"})
	public String ordenMerito(@RequestParam(required = false)Integer categoriaId, HttpServletRequest request, Model modelo){
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		modelo.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
		modelo.addAttribute("currentUri", request.getRequestURI());
		
        // Lista de usuarios filtrados según la categoría seleccionada
        List<Usuarios> usuariosPorCategoria = (categoriaId == null)
            ? usuS.getListUsuarios() // Si no se seleccionó categoría, muestra todos los usuarios
            : usuS.getListUsuPorCategoria(categoriaId);
        
        // Ordenar usuarios por puntaje de mayor a menor
        usuariosPorCategoria.sort((u1, u2) -> u2.getPuntaje().getPuntaje() - u1.getPuntaje().getPuntaje());

        // Agregar datos a la vista
        modelo.addAttribute("usuario", usuariosPorCategoria);
        modelo.addAttribute("categorias", catS.getListarCategorias()); // Para llenar el select
		
		return "usuarios/orden-merito";
	}
}
