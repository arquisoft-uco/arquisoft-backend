package com.arquisoft.seguridad.infrastructure.config.security;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedoresAutenticacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.AutenticacionProperties;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ClaimsIdentidad;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ModoAutorizacion;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.SesionProperties;
import com.arquisoft.seguridad.infrastructure.config.cors.OrigenesPermitidos;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak.KeycloakFuenteMapeoRolesProperties;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.redis.MapeoRolesProperties;
import com.arquisoft.shared.exception.InfrastructureException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.SeguridadCodes;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import com.arquisoft.seguridad.infrastructure.config.autenticacion.ProveedorAutenticacion;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    private static final String ORIGEN_PRUEBA = "http://localhost:3000";

    private static SesionProperties sesionApagada() {
        return new SesionProperties("sesion-pdp", false, "pep", "SECURITY_BASELINE_SESSION", "", false,
                Duration.ofSeconds(1), Duration.ofSeconds(3), new SesionProperties.Cache(Duration.ofSeconds(60), 10000));
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
                pep, sesionApagada(), new ProveedoresAutenticacion(List.of()), new OrigenesPermitidos(ORIGEN_PRUEBA),
                expansion, keycloak, mock(RutasPublicas.class), entorno);
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
                pep("cred", false, "https://pep.test"), sesionApagada(), new ProveedoresAutenticacion(List.of()),
                new OrigenesPermitidos(ORIGEN_PRUEBA), expansionValida(), keycloakValido(),
                mock(RutasPublicas.class), new MockEnvironment());

        // Act
        validador.registrarConfiguracionAplicada();

        // Assert
        verify(logger).warn(AutorizacionKey.LOG_AUDIENCIA_SIN_VALIDAR, "sin");
        verify(logger, never()).warn(AutorizacionKey.LOG_AUDIENCIA_SIN_VALIDAR, "con");
    }

    private static final String CABECERAS_CANAL = "Authorization,Content-Type,X-Correlation-Id,X-XSRF-TOKEN,X-Proveedor-Identidad";

    private static SesionProperties sesion(boolean habilitada, String url, boolean inseguro, String authorization) {
        return new SesionProperties("sesion-pdp", habilitada, authorization, "SECURITY_BASELINE_SESSION", url, inseguro,
                Duration.ofSeconds(1), Duration.ofSeconds(3), new SesionProperties.Cache(Duration.ofSeconds(60), 100));
    }

    private static ProveedorAutenticacion proveedor(String id) {
        var proveedor = mock(ProveedorAutenticacion.class);
        when(proveedor.id()).thenReturn(id);
        return proveedor;
    }

    private static ValidadorConfiguracionSeguridad validadorConSesion(SesionProperties sesion, PepProperties pep,
                                                                      List<ProveedorAutenticacion> proveedores,
                                                                      String origenes, boolean credenciales,
                                                                      String cabeceras, String perfil) {
        var entorno = new MockEnvironment();
        if (perfil != null) {
            entorno.setActiveProfiles(perfil);
        }
        var validador = new ValidadorConfiguracionSeguridad(mock(AppLogger.class),
                new AutenticacionProperties(List.of(emisor("principal", true, ModoAutorizacion.PEP))), pep, sesion,
                new ProveedoresAutenticacion(proveedores), new OrigenesPermitidos(origenes), expansionValida(),
                keycloakValido(), mock(RutasPublicas.class), entorno);
        ReflectionTestUtils.setField(validador, "seguridadHabilitada", true);
        ReflectionTestUtils.setField(validador, "modoRuta", "template");
        ReflectionTestUtils.setField(validador, "permiteCredencialesCors", credenciales);
        ReflectionTestUtils.setField(validador, "cabecerasPermitidasCors", cabeceras);
        return validador;
    }

    private static ValidadorConfiguracionSeguridad validadorCanal(SesionProperties sesion) {
        return validadorConSesion(sesion, pep("cred", false, "https://pep.test"), List.of(proveedor("sesion-pdp")),
                ORIGEN_PRUEBA, true, CABECERAS_CANAL, null);
    }

    @Test
    void debeFallar_cuandoIdDeProveedorRepetidoOIgualAEmisorOABearer() {
        // Arrange
        var pepValido = pep("cred", false, "https://pep.test");
        var repetido = validadorConSesion(sesion(false, "", false, "pep"), pepValido,
                List.of(proveedor("a"), proveedor("a")), ORIGEN_PRUEBA, true, CABECERAS_CANAL, null);
        var igualAEmisor = validadorConSesion(sesion(false, "", false, "pep"), pepValido,
                List.of(proveedor("principal")), ORIGEN_PRUEBA, true, CABECERAS_CANAL, null);
        var igualABearer = validadorConSesion(sesion(false, "", false, "pep"), pepValido,
                List.of(proveedor("Bearer")), ORIGEN_PRUEBA, true, CABECERAS_CANAL, null);

        // Act / Assert
        assertThat(repetido.problemas()).hasSize(1).first().asString().contains("proveedor");
        assertThat(igualAEmisor.problemas()).hasSize(1).first().asString().contains("proveedor");
        assertThat(igualABearer.problemas()).hasSize(1).first().asString().contains("proveedor");
        assertThatThrownBy(repetido::validar).isInstanceOf(InfrastructureException.class);
    }

    @Test
    void debeFallar_cuandoSesionHabilitadaSinUrl() {
        // Arrange
        var validador = validadorCanal(sesion(true, "", false, "pep"));

        // Act / Assert
        assertThat(validador.problemas()).hasSize(1).first().asString().contains("pdp-url");
    }

    @Test
    void debeFallar_cuandoSesionNoEsPep() {
        // Arrange
        var validador = validadorCanal(sesion(true, "https://pdp.test", false, "role-table"));

        // Act / Assert
        assertThat(validador.problemas()).hasSize(1).first().asString().contains("authorization");
    }

    @Test
    void debeFallar_cuandoPepSinConfigurarConSesionHabilitada() {
        // Arrange
        var validador = validadorConSesion(sesion(true, "https://pdp.test", false, "pep"), pep("", false, ""),
                List.of(proveedor("sesion-pdp")), ORIGEN_PRUEBA, true, CABECERAS_CANAL, null);

        // Act / Assert
        assertThat(validador.problemas()).isNotEmpty();
    }

    @Test
    void debeFallar_cuandoSesionSinAllowCredentialsOOrigenesInvalidos() {
        // Arrange
        var configuracion = sesion(true, "https://pdp.test", false, "pep");
        var pepValido = pep("cred", false, "https://pep.test");
        var sinCredenciales = validadorConSesion(configuracion, pepValido, List.of(proveedor("sesion-pdp")),
                ORIGEN_PRUEBA, false, CABECERAS_CANAL, null);
        var comodin = validadorConSesion(configuracion, pepValido, List.of(proveedor("sesion-pdp")),
                "*", true, CABECERAS_CANAL, null);
        var vacios = validadorConSesion(configuracion, pepValido, List.of(proveedor("sesion-pdp")),
                " , ", true, CABECERAS_CANAL, null);

        // Act / Assert
        assertThat(sinCredenciales.problemas()).hasSize(1).first().asString().contains("allow-credentials");
        assertThat(comodin.problemas()).hasSize(1).first().asString().contains("allowed-origins");
        assertThat(vacios.problemas()).hasSize(1).first().asString().contains("allowed-origins");
    }

    @Test
    void debeFallar_cuandoAllowedHeadersSinLasCabecerasDelCanal() {
        // Arrange
        var configuracion = sesion(true, "https://pdp.test", false, "pep");
        var pepValido = pep("cred", false, "https://pep.test");
        var sinXsrf = validadorConSesion(configuracion, pepValido, List.of(proveedor("sesion-pdp")),
                ORIGEN_PRUEBA, true, "Authorization,X-Proveedor-Identidad", null);
        var sinSelector = validadorConSesion(configuracion, pepValido, List.of(proveedor("sesion-pdp")),
                ORIGEN_PRUEBA, true, "Authorization, x-xsrf-token", null);

        // Act / Assert
        assertThat(sinXsrf.problemas()).hasSize(1).first().asString().contains("X-XSRF-TOKEN");
        assertThat(sinSelector.problemas()).hasSize(1).first().asString().contains("X-Proveedor-Identidad");
    }

    @Test
    void debeFallar_enProdConHttpInseguroDeSesion() {
        // Arrange
        var validador = validadorConSesion(sesion(true, "http://pdp.test", true, "pep"),
                pep("cred", false, "https://pep.test"), List.of(proveedor("sesion-pdp")), ORIGEN_PRUEBA, true,
                CABECERAS_CANAL, "prod");

        // Act / Assert
        assertThat(validador.problemas()).hasSize(1).first().asString().contains("session.allow-insecure-http");
    }

    @Test
    void debeAceptar_cuandoSesionDeshabilitadaOBienConfigurada() {
        // Arrange
        var deshabilitada = validadorConSesion(sesion(false, "", false, "pep"), pep("cred", false, "https://pep.test"),
                List.of(proveedor("sesion-pdp")), ORIGEN_PRUEBA, false, "Authorization", null);
        var bienConfigurada = validadorCanal(sesion(true, "https://pdp.test", false, "pep"));

        // Act / Assert
        assertThat(deshabilitada.problemas()).isEmpty();
        assertThat(bienConfigurada.problemas()).isEmpty();
        bienConfigurada.validar();
    }
}
