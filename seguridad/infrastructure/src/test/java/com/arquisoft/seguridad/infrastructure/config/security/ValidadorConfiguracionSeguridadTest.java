package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak.KeycloakFuenteMapeoRolesProperties;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis.MapeoRolesProperties;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.SeguridadCodes;
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
                new ClaimsIdentidad("sub", "email", "u", "n", ""));
    }

    private static PepProperties pep(String credencial, boolean inseguro, String url) {
        return new PepProperties(url, "app", "lab", credencial, inseguro, Duration.ofSeconds(1), Duration.ofSeconds(3),
                0, Duration.ofMillis(200));
    }

    private static final String UUID_CLIENTE = "cffeb896-738c-4000-80cf-8cf99e05233f";

    private static MapeoRolesProperties expansionValida() {
        return new MapeoRolesProperties(Duration.ofMinutes(5), Duration.ofMinutes(15), Duration.ofSeconds(30),
                Duration.ofSeconds(60));
    }

    private static KeycloakFuenteMapeoRolesProperties keycloakValido() {
        return new KeycloakFuenteMapeoRolesProperties("http://kc", "arquisoft", "arquisoft-api", "secreto", UUID_CLIENTE,
                Duration.ofSeconds(2), Duration.ofSeconds(3));
    }

    private static AutenticacionProperties.Emisor emisorTabla(String roles) {
        return new AutenticacionProperties.Emisor("legado", true, "https://legado", "", List.of(),
                ModoAutorizacion.ROLE_TABLE, true, new ClaimsIdentidad("sub", "email", "u", "n", roles));
    }

    private static ValidadorConfiguracionSeguridad validador(List<AutenticacionProperties.Emisor> emisores, PepProperties pep,
                                                             String perfil, boolean habilitada, String modoRuta) {
        return validador(emisores, pep, expansionValida(), keycloakValido(), perfil, habilitada, modoRuta);
    }

    private static ValidadorConfiguracionSeguridad validador(List<AutenticacionProperties.Emisor> emisores, PepProperties pep,
                                                             MapeoRolesProperties expansion,
                                                             KeycloakFuenteMapeoRolesProperties keycloak,
                                                             String perfil, boolean habilitada, String modoRuta) {
        var entorno = new MockEnvironment();
        if (perfil != null) {
            entorno.setActiveProfiles(perfil);
        }
        var validador = new ValidadorConfiguracionSeguridad(mock(AppLogger.class), new AutenticacionProperties(emisores),
                pep, expansion, keycloak, mock(RutasPublicas.class), entorno);
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
        assertThatThrownBy(validador::validar).isInstanceOf(InfrastructureException.class)
                .extracting(fallo -> ((InfrastructureException) fallo).getCodigoError())
                .isEqualTo(SeguridadCodes.Autorizacion.CONFIGURACION_INSEGURA);
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
        assertThatThrownBy(modoNone::validar).isInstanceOf(InfrastructureException.class);
    }

    @Test
    void debeAceptar_cuandoRoleTableTieneRolesYExpansionValida() {
        // Arrange
        var validador = validador(List.of(emisorTabla("realm_access.roles")), pep("", false, ""), null, true, "template");

        // Act / Assert
        assertThat(validador.problemas()).isEmpty();
    }

    @Test
    void debeFallar_cuandoRoleTableNoTieneClaimsRoles() {
        // Arrange
        var validador = validador(List.of(emisorTabla("")), pep("", false, ""), null, true, "template");

        // Act / Assert
        assertThat(validador.problemas()).hasSize(1).first().asString().contains("claims.roles");
        assertThatThrownBy(validador::validar).isInstanceOf(InfrastructureException.class)
                .extracting(fallo -> ((InfrastructureException) fallo).getCodigoError())
                .isEqualTo(SeguridadCodes.Autorizacion.CONFIGURACION_INSEGURA);
    }

    @Test
    void debeFallar_cuandoRoleTableNoTieneClientUuid() {
        // Arrange
        var sinUuid = new KeycloakFuenteMapeoRolesProperties("http://kc", "arquisoft", "arquisoft-api", "secreto", "",
                Duration.ofSeconds(2), Duration.ofSeconds(3));
        var validador = validador(List.of(emisorTabla("realm_access.roles")), pep("", false, ""), expansionValida(),
                sinUuid, null, true, "template");

        // Act / Assert
        assertThat(validador.problemas()).hasSize(1).first().asString().contains("client-uuid");
    }

    @Test
    void debeFallar_cuandoRoleTableTieneClientUuidInvalido() {
        // Arrange
        var uuidInvalido = new KeycloakFuenteMapeoRolesProperties("http://kc", "arquisoft", "arquisoft-api", "secreto",
                "arquisoft-api", Duration.ofSeconds(2), Duration.ofSeconds(3));
        var validador = validador(List.of(emisorTabla("realm_access.roles")), pep("", false, ""), expansionValida(),
                uuidInvalido, null, true, "template");

        // Act / Assert
        assertThat(validador.problemas()).hasSize(1).first().asString().contains("UUID");
    }

    @Test
    void debeFallar_cuandoRoleTableTieneIntervalosIncoherentes() {
        // Arrange
        var incoherente = new MapeoRolesProperties(Duration.ofMinutes(5), Duration.ofMinutes(6), Duration.ofSeconds(30),
                Duration.ofSeconds(60));
        var validador = validador(List.of(emisorTabla("realm_access.roles")), pep("", false, ""), incoherente,
                keycloakValido(), null, true, "template");

        // Act / Assert
        assertThat(validador.problemas()).hasSize(1).first().asString().contains("ttl");
    }

    @Test
    void debeFallar_cuandoRoleTableTieneCheckIntervalMayorQueElIntervalo() {
        // Arrange
        var sondeoExcesivo = new MapeoRolesProperties(Duration.ofMinutes(5), Duration.ofMinutes(15),
                Duration.ofMinutes(6), Duration.ofSeconds(60));
        var validador = validador(List.of(emisorTabla("realm_access.roles")), pep("", false, ""), sondeoExcesivo,
                keycloakValido(), null, true, "template");

        // Act / Assert
        assertThat(validador.problemas()).hasSize(1).first().asString().contains("check-interval");
    }

    @Test
    void debeNoExigirExpansion_cuandoSoloHayPep() {
        // Arrange
        var sinDatos = new KeycloakFuenteMapeoRolesProperties("", "", "", "", "", Duration.ofSeconds(2),
                Duration.ofSeconds(3));
        var validador = validador(List.of(emisor("principal", true, ModoAutorizacion.PEP)), pep("cred", false, "https://pep.test"),
                expansionValida(), sinDatos, null, true, "template");

        // Act / Assert
        assertThat(validador.problemas()).isEmpty();
    }

    @Test
    void debeAdvertirPorEmisor_cuandoNoDeclaraAudiencias() {
        // Arrange
        var logger = mock(AppLogger.class);
        var conAudiencia = new AutenticacionProperties.Emisor("con", true, "https://con", "", List.of("api"),
                ModoAutorizacion.PEP, false, new ClaimsIdentidad("sub", "email", "u", "n", ""));
        var validador = new ValidadorConfiguracionSeguridad(logger,
                new AutenticacionProperties(List.of(emisor("sin", true, ModoAutorizacion.PEP), conAudiencia)),
                pep("cred", false, "https://pep.test"), expansionValida(), keycloakValido(),
                mock(RutasPublicas.class), new MockEnvironment());

        // Act
        validador.registrarConfiguracionAplicada();

        // Assert
        verify(logger).warn(AutorizacionKey.LOG_AUDIENCIA_SIN_VALIDAR, "sin");
        verify(logger, never()).warn(AutorizacionKey.LOG_AUDIENCIA_SIN_VALIDAR, "con");
    }
}
