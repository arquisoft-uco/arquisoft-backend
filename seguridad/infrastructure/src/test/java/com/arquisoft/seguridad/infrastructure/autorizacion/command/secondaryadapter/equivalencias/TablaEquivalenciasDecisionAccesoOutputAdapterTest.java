package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.EquivalenciasProperties.Entrada;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TablaEquivalenciasDecisionAccesoOutputAdapterTest {

    private TablaEquivalenciasDecisionAccesoOutputAdapter adaptador;

    private static String token(List<String> permisos) {
        var claims = new JWTClaimsSet.Builder()
                .claim("resource_access", Map.of("arquisoft-api", Map.of("roles", permisos)))
                .build();
        return new PlainJWT(claims).serialize();
    }

    private static SolicitudAcceso solicitud(String metodo, String recurso, List<String> permisos) {
        return new SolicitudAcceso(metodo, recurso, token(permisos), "legado", "c");
    }

    @BeforeEach
    void preparar() {
        var emisor = new AutenticacionProperties.Emisor("legado", true, "https://legado", "", List.of(),
                ModoAutorizacion.ROLE_TABLE, true, new ClaimsIdentidad("sub", "email", "preferred_username", "name",
                "", "resource_access.arquisoft-api.roles"));
        var equivalencias = new EquivalenciasProperties(List.of(
                new Entrada("GET", "/api/fichas/{id}", "fichas:ficha:view"),
                new Entrada("DELETE", "/api/fichas/{id}", "fichas:ficha:delete")));
        adaptador = new TablaEquivalenciasDecisionAccesoOutputAdapter(equivalencias,
                new AutenticacionProperties(List.of(emisor)));
    }

    @Test
    void debePermitir_cuandoElTokenTieneElRol() {
        // Act
        var decision = adaptador.decidir(solicitud("get", "/api/fichas/{id}", List.of("fichas:ficha:view")));

        // Assert
        assertThat(decision).isInstanceOf(DecisionAcceso.Permitida.class);
    }

    @Test
    void debeDenegar_cuandoFaltaElRolORecursoNoEstaEnLaTablaOElMetodoNoCoincide() {
        // Act / Assert
        assertThat(adaptador.decidir(solicitud("GET", "/api/fichas/{id}", List.of("otro:rol"))))
                .isInstanceOf(DecisionAcceso.Denegada.class);
        assertThat(adaptador.decidir(solicitud("GET", "/api/desconocido", List.of("fichas:ficha:view"))))
                .isInstanceOf(DecisionAcceso.Denegada.class);
        assertThat(adaptador.decidir(solicitud("DELETE", "/api/fichas/{id}", List.of("fichas:ficha:view"))))
                .isInstanceOf(DecisionAcceso.Denegada.class);
        assertThat(adaptador.decidir(solicitud("GET", "/api/fichas/123", List.of("fichas:ficha:view"))))
                .isInstanceOf(DecisionAcceso.Denegada.class);
    }

    @Test
    void debeDenegar_cuandoElTokenEsIlegibleOElEmisorNoExiste() {
        // Arrange
        var ilegible = new SolicitudAcceso("GET", "/api/fichas/{id}", "no-es-jwt", "legado", "c");
        var otroEmisor = new SolicitudAcceso("GET", "/api/fichas/{id}", token(List.of("fichas:ficha:view")), "x", "c");

        // Act / Assert
        assertThat(adaptador.decidir(ilegible)).isInstanceOf(DecisionAcceso.Denegada.class);
        assertThat(adaptador.decidir(otroEmisor)).isInstanceOf(DecisionAcceso.Denegada.class);
    }
}
