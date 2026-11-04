package com.arquisoft.shared.web.identidad;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IdentidadActualArgumentResolverTest {

    @SuppressWarnings("unused")
    void metodo(IdentidadActual identidad, String otro) {
    }

    private MethodParameter parametro(int indice) throws NoSuchMethodException {
        return new MethodParameter(getClass().getDeclaredMethod("metodo", IdentidadActual.class, String.class), indice);
    }

    @Test
    void debeResolverIdentidad_cuandoHayProveedor() throws Exception {
        // Arrange
        var identidad = new IdentidadActual(UtilUUID.generarNuevoUUID(), "a@uco.edu.co", "a", List.of("R"), "jti", null);
        var resolver = new IdentidadActualArgumentResolver(() -> identidad);

        // Act
        var resuelta = resolver.resolveArgument(parametro(0), null, null, null);

        // Assert
        assertThat(resuelta).isSameAs(identidad);
    }

    @Test
    void debeSoportarSoloParametrosIdentidadActual_cuandoSeConsultaElTipo() throws Exception {
        // Arrange
        var resolver = new IdentidadActualArgumentResolver(() -> null);

        // Act / Assert
        assertThat(resolver.supportsParameter(parametro(0))).isTrue();
        assertThat(resolver.supportsParameter(parametro(1))).isFalse();
    }

    @Test
    void debePropagarAutenticacionRequerida_cuandoNoHayIdentidad() throws Exception {
        // Arrange
        var resolver = new IdentidadActualArgumentResolver(() -> {
            throw new AuthenticationCredentialsNotFoundException("sin identidad");
        });
        var parametro = parametro(0);

        // Act / Assert
        assertThatThrownBy(() -> resolver.resolveArgument(parametro, null, null, null))
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class);
    }
}
