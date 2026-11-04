package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias;

import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.DecisionAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.RolClienteOtorgadoOutputPort;
import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.equivalencias.EquivalenciasProperties.Entrada;
import com.arquisoft.seguridad.infrastructure.autorizacion.exception.ProveedorAutorizacionNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.PlainJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TablaEquivalenciasDecisionAccesoOutputAdapterTest {

    @Mock
    private RolClienteOtorgadoOutputPort rolClienteOtorgadoOutputPort;

    private TablaEquivalenciasDecisionAccesoOutputAdapter adaptador;

    private static String token(List<String> rolesRealm) {
        var claims = new JWTClaimsSet.Builder()
                .claim("realm_access", Map.of("roles", rolesRealm))
                .build();
        return new PlainJWT(claims).serialize();
    }

    private static SolicitudAcceso solicitud(String metodo, String recurso, List<String> rolesRealm) {
        return new SolicitudAcceso(metodo, recurso, token(rolesRealm), "legado", "c");
    }

    @BeforeEach
    void preparar() {
        var emisor = new AutenticacionProperties.Emisor("legado", true, "https://legado", "", List.of(),
                ModoAutorizacion.ROLE_TABLE, true, new ClaimsIdentidad("sub", "email", "preferred_username", "name",
                "realm_access.roles"));
        var equivalencias = new EquivalenciasProperties(List.of(
                new Entrada("GET", "/api/fichas/{id}", "fichas:ficha:view"),
                new Entrada("DELETE", "/api/fichas/{id}", "fichas:ficha:delete")));
        adaptador = new TablaEquivalenciasDecisionAccesoOutputAdapter(equivalencias,
                new AutenticacionProperties(List.of(emisor)), rolClienteOtorgadoOutputPort);
    }

    @Test
    void debePermitir_cuandoElPuertoConfirmaQueUnRolDelTokenOtorgaElClientRole() {
        // Arrange
        var roles = List.of("asesor", "estudiante");
        when(rolClienteOtorgadoOutputPort.rolClienteOtorgadoPor(eq("fichas:ficha:view"), any())).thenReturn(true);

        // Act
        var decision = adaptador.decidir(solicitud("get", "/api/fichas/{id}", roles));

        // Assert
        assertThat(decision).isInstanceOf(DecisionAcceso.Permitida.class);
        verify(rolClienteOtorgadoOutputPort).rolClienteOtorgadoPor("fichas:ficha:view", roles);
    }

    @Test
    void debeDenegar_cuandoElPuertoNoLoConfirma() {
        // Arrange
        when(rolClienteOtorgadoOutputPort.rolClienteOtorgadoPor(anyString(), any())).thenReturn(false);

        // Act
        var decision = adaptador.decidir(solicitud("DELETE", "/api/fichas/{id}", List.of("estudiante")));

        // Assert
        assertThat(decision).isInstanceOf(DecisionAcceso.Denegada.class);
        verify(rolClienteOtorgadoOutputPort).rolClienteOtorgadoPor("fichas:ficha:delete", List.of("estudiante"));
    }

    @Test
    void debeDenegarSinConsultarElPuerto_cuandoLaRutaNoEstaEnLaTabla() {
        // Act / Assert
        assertThat(adaptador.decidir(solicitud("GET", "/api/desconocido", List.of("asesor"))))
                .isInstanceOf(DecisionAcceso.Denegada.class);
        assertThat(adaptador.decidir(solicitud("PUT", "/api/fichas/{id}", List.of("asesor"))))
                .isInstanceOf(DecisionAcceso.Denegada.class);
        assertThat(adaptador.decidir(solicitud("GET", "/api/fichas/123", List.of("asesor"))))
                .isInstanceOf(DecisionAcceso.Denegada.class);
        verifyNoInteractions(rolClienteOtorgadoOutputPort);
    }

    @Test
    void debeDenegarSinConsultarElPuerto_cuandoElTokenNoTraeRoles() {
        // Act
        var decision = adaptador.decidir(solicitud("GET", "/api/fichas/{id}", List.of()));

        // Assert
        assertThat(decision).isInstanceOf(DecisionAcceso.Denegada.class);
        verify(rolClienteOtorgadoOutputPort, never()).rolClienteOtorgadoPor(anyString(), anyCollection());
    }

    @Test
    void debeDenegar_cuandoElTokenEsIlegibleOElEmisorNoExiste() {
        // Arrange
        var ilegible = new SolicitudAcceso("GET", "/api/fichas/{id}", "no-es-jwt", "legado", "c");
        var otroEmisor = new SolicitudAcceso("GET", "/api/fichas/{id}", token(List.of("asesor")), "x", "c");

        // Act / Assert
        assertThat(adaptador.decidir(ilegible)).isInstanceOf(DecisionAcceso.Denegada.class);
        assertThat(adaptador.decidir(otroEmisor)).isInstanceOf(DecisionAcceso.Denegada.class);
        verifyNoInteractions(rolClienteOtorgadoOutputPort);
    }

    @Test
    void debePropagar503_cuandoElPuertoLanza() {
        // Arrange
        when(rolClienteOtorgadoOutputPort.rolClienteOtorgadoPor(anyString(), any()))
                .thenThrow(new ProveedorAutorizacionNoDisponibleException("sin mapeo"));
        var solicitud = solicitud("GET", "/api/fichas/{id}", List.of("asesor"));

        // Act / Assert
        assertThatThrownBy(() -> adaptador.decidir(solicitud))
                .isInstanceOf(ProveedorAutorizacionNoDisponibleException.class);
    }
}
