package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.UsuarioSesionProperties;
import com.arquisoft.seguridad.infrastructure.config.cors.OrigenesPermitidos;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak.KeycloakFuenteMapeoRolesProperties;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis.MapeoRolesProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import com.arquisoft.shared.message.key.seguridad.ResolucionSesionKey;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ValidadorConfiguracionSeguridadPoliticaTest {

    private static final String ISSUER = "https://sso.test/realms/arquisoft";
    private static final String CABECERAS = "Authorization,X-XSRF-TOKEN,X-Proveedor-Identidad";

    private static AutenticacionProperties.Emisor emisor(String id, String issuer, boolean blacklist,
                                                         List<String> audiencias, ModoAutorizacion modo) {
        return new AutenticacionProperties.Emisor(id, true, issuer, "", audiencias, modo, blacklist,
                new ClaimsIdentidad("sub", "email", "u", "n", "roles"));
    }

    private static PepProperties pep() {
        return new PepProperties("https://pep.test", "app", "lab", "cred", false, Duration.ofSeconds(1),
                Duration.ofSeconds(3), 0, Duration.ofMillis(200));
    }

    private static SesionProperties sesion(boolean habilitada) {
        return new SesionProperties("sesion-pdp", habilitada, "SECURITY_BASELINE_SESSION",
                habilitada ? "https://pdp.test" : "", false, Duration.ofSeconds(1), Duration.ofSeconds(3),
                new SesionProperties.Cache(Duration.ofSeconds(60), 100));
    }

    private static UsuarioSesionProperties usuarios(boolean inseguro) {
        return new UsuarioSesionProperties("https://usuarios.test", inseguro, Duration.ofSeconds(1), Duration.ofSeconds(3),
                Duration.ofMinutes(5), "https://usuarios.test/token", "arquisoft-seguridad-usuarios", "secreto",
                "/api/usuarios/por-correo", "legado");
    }

    private static ValidadorConfiguracionSeguridad validador(List<AutenticacionProperties.Emisor> emisores,
                                                             SesionProperties sesion, UsuarioSesionProperties usuarios,
                                                             String perfil) {
        var entorno = new MockEnvironment();
        if (perfil != null) {
            entorno.setActiveProfiles(perfil);
        }
        var validador = new ValidadorConfiguracionSeguridad(mock(AppLogger.class), new AutenticacionProperties(emisores),
                pep(), sesion, usuarios, new ProveedoresAutenticacion(List.of()),
                new OrigenesPermitidos("http://localhost:3000"),
                new MapeoRolesProperties(Duration.ofMinutes(5), Duration.ofMinutes(15), Duration.ofSeconds(30),
                        Duration.ofSeconds(60)),
                new KeycloakFuenteMapeoRolesProperties("http://kc", "arquisoft", "arquisoft-api", "secreto",
                        "cffeb896-738c-4000-80cf-8cf99e05233f", Duration.ofSeconds(2), Duration.ofSeconds(3)),
                mock(RutasPublicas.class), entorno);
        ReflectionTestUtils.setField(validador, "seguridadHabilitada", true);
        ReflectionTestUtils.setField(validador, "modoRuta", "template");
        ReflectionTestUtils.setField(validador, "permiteCredencialesCors", true);
        ReflectionTestUtils.setField(validador, "cabecerasPermitidasCors", CABECERAS);
        return validador;
    }

    @Test
    void debeFallar_cuandoMismoIssuerConBlacklistDistinta() {
        // Arrange
        var validador = validador(List.of(
                emisor("legado", ISSUER, true, List.of("api"), ModoAutorizacion.PEP),
                emisor("principal", ISSUER, false, List.of("api"), ModoAutorizacion.PEP)), sesion(false), usuarios(false), null);

        // Act
        var problemas = validador.problemas();

        // Assert
        assertThat(problemas).containsExactly(
                Mensajes.formatear(AutorizacionKey.ERROR_EMISORES_POLITICA_DISTINTA, "legado,principal", ISSUER));
    }

    @Test
    void debeFallar_cuandoMismoIssuerConAudienciasOModoDistintos() {
        // Arrange
        var audiencias = validador(List.of(
                emisor("legado", ISSUER, false, List.of("api"), ModoAutorizacion.PEP),
                emisor("principal", ISSUER, false, List.of(), ModoAutorizacion.PEP)), sesion(false), usuarios(false), null);
        var modo = validador(List.of(
                emisor("legado", ISSUER, false, List.of("api"), ModoAutorizacion.ROLE_TABLE),
                emisor("principal", ISSUER, false, List.of("api"), ModoAutorizacion.PEP)), sesion(false), usuarios(false), null);

        // Act / Assert
        assertThat(audiencias.problemas()).hasSize(1);
        assertThat(modo.problemas()).contains(
                Mensajes.formatear(AutorizacionKey.ERROR_EMISORES_POLITICA_DISTINTA, "legado,principal", ISSUER));
    }

    @Test
    void debeAceptar_cuandoMismoIssuerConPoliticaIdenticaOIssuersDistintos() {
        // Arrange
        var identica = validador(List.of(
                emisor("legado", ISSUER, true, List.of("api", "web"), ModoAutorizacion.PEP),
                emisor("principal", ISSUER, true, List.of("web", "api"), ModoAutorizacion.PEP)), sesion(false), usuarios(false), null);
        var distintos = validador(List.of(
                emisor("legado", ISSUER, true, List.of("api"), ModoAutorizacion.PEP),
                emisor("principal", "https://otro.test", false, List.of(), ModoAutorizacion.PEP)), sesion(false), usuarios(false), null);

        // Act / Assert
        assertThat(identica.problemas()).isEmpty();
        assertThat(distintos.problemas()).isEmpty();
    }

    @Test
    void debeFallar_enProdConHttpInseguroDeUsuarios() {
        // Arrange
        var emisores = List.of(
                emisor("principal", "https://a.test", false, List.of(), ModoAutorizacion.PEP),
                emisor("legado", "https://b.test", true, List.of("api"), ModoAutorizacion.PEP));
        var validador = validador(emisores, sesion(true), usuarios(true), "prod");

        // Act
        var problemas = validador.problemas();

        // Assert
        assertThat(problemas).contains(Mensajes.obtener(ResolucionSesionKey.ERROR_USUARIOS_HTTP_EN_PRODUCCION));
    }

    @Test
    void debeAceptar_cuandoHttpInseguroDeUsuariosFueraDeProdOConSesionApagada() {
        // Arrange
        var emisores = List.of(
                emisor("principal", "https://a.test", false, List.of(), ModoAutorizacion.PEP),
                emisor("legado", "https://b.test", true, List.of("api"), ModoAutorizacion.PEP));
        var enDev = validador(emisores, sesion(true), usuarios(true), null);
        var apagada = validador(emisores, sesion(false), usuarios(true), "prod");

        // Act / Assert
        assertThat(enDev.problemas()).doesNotContain(Mensajes.obtener(ResolucionSesionKey.ERROR_USUARIOS_HTTP_EN_PRODUCCION));
        assertThat(apagada.problemas()).doesNotContain(Mensajes.obtener(ResolucionSesionKey.ERROR_USUARIOS_HTTP_EN_PRODUCCION));
    }
}
