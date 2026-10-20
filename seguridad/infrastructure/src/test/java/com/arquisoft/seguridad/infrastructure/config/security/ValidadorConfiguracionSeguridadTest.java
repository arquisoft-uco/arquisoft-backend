package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ValidadorConfiguracionSeguridadTest {

    private static AutenticacionProperties.Emisor emisor(String id, boolean habilitado, ModoAutorizacion modo) {
        return new AutenticacionProperties.Emisor(id, habilitado, "https://" + id, "", List.of(), modo, false,
                new ClaimsIdentidad("sub", "email", "u", "n", "", ""));
    }

    private static PepProperties pep(String credencial, boolean inseguro, String url) {
        return new PepProperties(url, "app", "lab", credencial, inseguro, Duration.ofSeconds(1), Duration.ofSeconds(3),
                0, Duration.ofMillis(200));
    }

    private static ValidadorConfiguracionSeguridad validador(List<AutenticacionProperties.Emisor> emisores, PepProperties pep,
                                                             String perfil, boolean habilitada, String modoRuta) {
        var entorno = new MockEnvironment();
        if (perfil != null) {
            entorno.setActiveProfiles(perfil);
        }
        var validador = new ValidadorConfiguracionSeguridad(mock(AppLogger.class), new AutenticacionProperties(emisores),
                pep, mock(RutasPublicas.class), entorno);
        ReflectionTestUtils.setField(validador, "seguridadHabilitada", habilitada);
        ReflectionTestUtils.setField(validador, "modoRuta", modoRuta);
        return validador;
    }

    @Test
    void debeAceptar_cuandoLaConfiguracionEsValida() {
        // Arrange
        var validador = validador(List.of(emisor("principal", true, ModoAutorizacion.PEP)),
                pep("cred", false, "https://pep.test"), "prod", true, "template");

        // Act / Assert
        assertThat(validador.problemas()).isEmpty();
        validador.validar();
    }

    @Test
    void debeFallar_cuandoNingunEmisorHabilitado() {
        // Arrange
        var validador = validador(List.of(emisor("principal", false, ModoAutorizacion.PEP)),
                pep("cred", false, "https://pep.test"), null, true, "template");

        // Act / Assert
        assertThatThrownBy(validador::validar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeFallar_cuandoPepSinCredencialOIdsDuplicadosOTablaConModoRaw() {
        // Arrange
        var sinCredencial = validador(List.of(emisor("principal", true, ModoAutorizacion.PEP)),
                pep("", false, "https://pep.test"), null, true, "template");
        var duplicados = validador(List.of(emisor("a", true, ModoAutorizacion.NONE), emisor("a", true, ModoAutorizacion.NONE)),
                pep("", false, ""), null, true, "template");
        var tablaRaw = validador(List.of(emisor("legado", true, ModoAutorizacion.ROLE_TABLE)),
                pep("", false, ""), null, true, "raw");

        // Act / Assert
        assertThat(sinCredencial.problemas()).isNotEmpty();
        assertThat(duplicados.problemas()).isNotEmpty();
        assertThat(tablaRaw.problemas()).isNotEmpty();
    }

    @Test
    void debeFallar_enProdConModoNoneOSeguridadDeshabilitadaOHttpInseguro() {
        // Arrange
        var modoNone = validador(List.of(emisor("principal", true, ModoAutorizacion.NONE)),
                pep("", false, ""), "prod", true, "template");
        var sinSeguridad = validador(List.of(emisor("principal", true, ModoAutorizacion.NONE)),
                pep("", false, ""), "prod", false, "template");
        var httpInseguro = validador(List.of(emisor("principal", true, ModoAutorizacion.PEP)),
                pep("cred", true, "http://pep.test"), "prod", true, "template");

        // Act / Assert
        assertThat(modoNone.problemas()).hasSize(1);
        assertThat(sinSeguridad.problemas()).hasSize(2);
        assertThat(httpInseguro.problemas()).hasSize(1);
        assertThatThrownBy(modoNone::validar).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void debeAdvertirPorEmisor_cuandoNoDeclaraAudiencias() {
        // Arrange
        var logger = mock(AppLogger.class);
        var conAudiencia = new AutenticacionProperties.Emisor("con", true, "https://con", "", List.of("api"),
                ModoAutorizacion.PEP, false, new ClaimsIdentidad("sub", "email", "u", "n", "", ""));
        var validador = new ValidadorConfiguracionSeguridad(logger,
                new AutenticacionProperties(List.of(emisor("sin", true, ModoAutorizacion.PEP), conAudiencia)),
                pep("cred", false, "https://pep.test"), mock(RutasPublicas.class), new MockEnvironment());

        // Act
        validador.registrarConfiguracionAplicada();

        // Assert
        verify(logger).warn(AutorizacionKey.LOG_AUDIENCIA_SIN_VALIDAR, "sin");
        verify(logger, never()).warn(AutorizacionKey.LOG_AUDIENCIA_SIN_VALIDAR, "con");
    }
}
