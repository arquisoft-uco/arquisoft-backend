package com.arquisoft.seguridad.infrastructure.config.autenticacion;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioSesionPropertiesTest {

    private static UsuarioSesionProperties propiedades(String url, boolean inseguro, String tokenUrl, String cliente,
                                                       String secreto, Duration ttl) {
        return new UsuarioSesionProperties(url, inseguro, Duration.ofSeconds(1), Duration.ofSeconds(3), ttl, tokenUrl,
                cliente, secreto, "/api/usuarios/por-correo", "legado");
    }

    @Test
    void debeAceptar_cuandoTodoEstaConfigurado() {
        // Act
        var problemas = propiedades("https://usuarios.test", false, "https://kc.test/token", "c", "s",
                Duration.ofMinutes(5)).problemas();

        // Assert
        assertThat(problemas).isEmpty();
    }

    @Test
    void debeRechazar_cuandoFaltaUrlOCredencial() {
        // Act
        var problemas = propiedades("", false, "", "", "", Duration.ofMinutes(5)).problemas();

        // Assert
        assertThat(problemas).hasSize(4);
    }

    @Test
    void debeRechazarHttp_cuandoNoSePermiteInseguro() {
        // Act
        var estricto = propiedades("http://usuarios.test", false, "https://kc.test/token", "c", "s",
                Duration.ofMinutes(5)).problemas();
        var laxo = propiedades("http://usuarios.test", true, "https://kc.test/token", "c", "s",
                Duration.ofMinutes(5)).problemas();

        // Assert
        assertThat(estricto).hasSize(1);
        assertThat(laxo).isEmpty();
    }

    @Test
    void debeRechazarTtlNegativo_yPermitirCero() {
        // Act
        var negativo = propiedades("https://u.test", false, "https://kc.test/t", "c", "s", Duration.ofSeconds(-1));
        var cero = propiedades("https://u.test", false, "https://kc.test/t", "c", "s", Duration.ZERO);

        // Assert
        assertThat(negativo.problemas()).hasSize(1);
        assertThat(cero.problemas()).isEmpty();
        assertThat(cero.cacheHabilitada()).isFalse();
    }

    @Test
    void debeOcultarElSecreto_cuandoSeImprime() {
        // Act
        var texto = propiedades("https://u.test", false, "https://kc.test/t", "c", "super-secreto",
                Duration.ofMinutes(5)).toString();

        // Assert
        assertThat(texto).doesNotContain("super-secreto");
    }

    @Test
    void debeProblema_cuandoProveedorVacio() {
        // Arrange
        var sinProveedor = new UsuarioSesionProperties("https://u.test", false, Duration.ofSeconds(1),
                Duration.ofSeconds(3), Duration.ofMinutes(5), "https://kc.test/t", "c", "s", "/api/usuarios/por-correo", " ");

        // Act / Assert
        assertThat(sinProveedor.problemas()).hasSize(1).first().asString().contains("usuarios.proveedor");
    }

    @Test
    void debeUsarLegado_porDefecto() {
        // Act / Assert
        assertThat(UsuarioSesionProperties.PROVEEDOR_POR_DEFECTO).isEqualTo("legado");
        assertThat(propiedades("https://u.test", false, "https://kc.test/t", "c", "s", Duration.ofMinutes(5)).proveedor())
                .isEqualTo("legado");
    }
}
