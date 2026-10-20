package com.arquisoft.seguridad.infrastructure.autorizacion.command.primaryadapter.web;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResolvedorPlantillaRutaTest {

    private static RequestMappingInfo info(RequestMethod metodo, String ruta) {
        var configuracion = new RequestMappingInfo.BuilderConfiguration();
        configuracion.setPatternParser(PathPatternParser.defaultInstance);
        return RequestMappingInfo.paths(ruta).methods(metodo).options(configuracion).build();
    }

    private static ResolvedorPlantillaRuta resolvedor(String modo, String prefijo, RequestMappingInfo... infos) {
        var handlerMapping = mock(RequestMappingHandlerMapping.class);
        var mapa = new HashMap<RequestMappingInfo, HandlerMethod>();
        for (var info : infos) {
            mapa.put(info, null);
        }
        when(handlerMapping.getHandlerMethods()).thenReturn(mapa);
        return new ResolvedorPlantillaRuta(handlerMapping, modo, prefijo);
    }

    private static MockHttpServletRequest peticion(String metodo, String uri) {
        var request = new MockHttpServletRequest(metodo, uri);
        request.setContextPath("/api");
        return request;
    }

    @Test
    void debeResolverPlantillaConPrefijo_cuandoHayVariableYModoTemplate() {
        // Arrange
        var resolvedor = resolvedor("template", "/api", info(RequestMethod.GET, "/fichas-perfil/{fichaPerfilId}/items"));

        // Act
        var recurso = resolvedor.identificarRecurso(peticion("GET", "/api/fichas-perfil/abc/items"));

        // Assert
        assertThat(recurso).isEqualTo("/api/fichas-perfil/{fichaPerfilId}/items");
    }

    @Test
    void debeElegirLaMasEspecifica_cuandoHayDosCoincidencias() {
        // Arrange
        var resolvedor = resolvedor("template", "/api",
                info(RequestMethod.GET, "/fichas-perfil/{id}"), info(RequestMethod.GET, "/fichas-perfil/estados"));

        // Act
        var recurso = resolvedor.resolver("GET", "/fichas-perfil/estados");

        // Assert
        assertThat(recurso).contains("/fichas-perfil/estados");
    }

    @Test
    void debeDistinguirPorMetodo_cuandoLaMismaRutaTieneVariosVerbos() {
        // Arrange
        var resolvedor = resolvedor("template", "/api",
                info(RequestMethod.GET, "/x/{id}"), info(RequestMethod.DELETE, "/x/{borrarId}"));

        // Act / Assert
        assertThat(resolvedor.resolver("GET", "/x/1")).contains("/x/{id}");
        assertThat(resolvedor.resolver("DELETE", "/x/1")).contains("/x/{borrarId}");
        assertThat(resolvedor.resolver("PUT", "/x/1")).isEmpty();
    }

    @Test
    void debeUsarRutaCrudaConPrefijo_cuandoModoRawOSinMapeo() {
        // Arrange
        var raw = resolvedor("raw", "/api", info(RequestMethod.GET, "/x/{id}"));
        var sinMapeo = resolvedor("template", "/api", info(RequestMethod.GET, "/x/{id}"));

        // Act / Assert
        assertThat(raw.identificarRecurso(peticion("GET", "/api/x/1"))).isEqualTo("/api/x/1");
        assertThat(sinMapeo.identificarRecurso(peticion("GET", "/api/otra/ruta"))).isEqualTo("/api/otra/ruta");
    }

    @Test
    void debeListarLosRecursos_cuandoSePideElInventario() {
        // Arrange
        var resolvedor = resolvedor("template", "/api",
                info(RequestMethod.GET, "/x/{id}"), info(RequestMethod.POST, "/x"));

        // Act
        var recursos = resolvedor.recursos();

        // Assert
        assertThat(recursos).containsExactlyInAnyOrder(
                new ResolvedorPlantillaRuta.Recurso("GET", "/x/{id}"),
                new ResolvedorPlantillaRuta.Recurso("POST", "/x"));
    }
}
