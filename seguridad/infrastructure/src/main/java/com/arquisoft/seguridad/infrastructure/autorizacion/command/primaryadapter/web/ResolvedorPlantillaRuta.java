package com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web;

import com.arquisoft.shared.util.UtilObjeto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.PathContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public class ResolvedorPlantillaRuta {

    private static final String MODO_PLANTILLA = "template";

    private final RequestMappingHandlerMapping handlerMapping;
    private final String modoRuta;
    private final String prefijoRuta;
    private volatile List<Mapeo> mapeos;

    public ResolvedorPlantillaRuta(
            @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
            @Value("${security.authorization.resource.path-mode:template}") String modoRuta,
            @Value("${security.authorization.resource.path-prefix:${server.servlet.context-path:}}")
            String prefijoRuta) {
        this.handlerMapping = handlerMapping;
        this.modoRuta = modoRuta;
        this.prefijoRuta = prefijoRuta;
    }

    public String identificarRecurso(HttpServletRequest request) {
        var rutaRelativa = request.getRequestURI().substring(request.getContextPath().length());
        var ruta = MODO_PLANTILLA.equalsIgnoreCase(modoRuta)
                ? resolver(request.getMethod(), rutaRelativa).orElse(rutaRelativa)
                : rutaRelativa;
        return prefijoRuta + ruta;
    }

    public Optional<String> resolver(String metodo, String rutaRelativa) {
        var ruta = PathContainer.parsePath(rutaRelativa);
        return mapeos().stream()
                .filter(mapeo -> mapeo.admite(metodo))
                .filter(mapeo -> mapeo.patron().matches(ruta))
                .map(Mapeo::patron)
                .min(PathPattern.SPECIFICITY_COMPARATOR)
                .map(PathPattern::getPatternString);
    }

    public List<Recurso> recursos() {
        return mapeos().stream()
                .flatMap(mapeo -> (mapeo.metodos().isEmpty()
                        ? Set.of(RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
                                RequestMethod.PATCH, RequestMethod.DELETE)
                        : mapeo.metodos()).stream()
                        .map(metodo -> new Recurso(metodo.name(), mapeo.patron().getPatternString())))
                .toList();
    }

    private List<Mapeo> mapeos() {
        var actuales = mapeos;
        if (UtilObjeto.esNulo(actuales)) {
            synchronized (this) {
                actuales = mapeos;
                if (UtilObjeto.esNulo(actuales)) {
                    actuales = construir();
                    mapeos = actuales;
                }
            }
        }
        return actuales;
    }

    private List<Mapeo> construir() {
        var resultado = new ArrayList<Mapeo>();
        for (var info : handlerMapping.getHandlerMethods().keySet()) {
            var condicion = info.getPathPatternsCondition();
            if (UtilObjeto.esNulo(condicion)) {
                continue;
            }
            for (var patron : condicion.getPatterns()) {
                resultado.add(new Mapeo(patron, info.getMethodsCondition().getMethods()));
            }
        }
        return List.copyOf(resultado);
    }

    public record Recurso(String metodo, String plantilla) {}

    private record Mapeo(PathPattern patron, Set<RequestMethod> metodos) {

        boolean admite(String metodo) {
            return metodos.isEmpty() || metodos.stream().anyMatch(permitido -> permitido.name().equalsIgnoreCase(metodo));
        }
    }
}
