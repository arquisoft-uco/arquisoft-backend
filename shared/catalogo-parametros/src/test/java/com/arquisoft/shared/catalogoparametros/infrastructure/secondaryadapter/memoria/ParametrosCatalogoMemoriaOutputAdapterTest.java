package com.arquisoft.shared.catalogoparametros.infrastructure.secondaryadapter.memoria;

import com.arquisoft.shared.catalogoparametros.application.command.secondaryport.entity.ParametrosCatalogoEntity;
import com.arquisoft.shared.catalogoparametros.infrastructure.config.CatalogoParametrosProperties;
import com.arquisoft.shared.catalogoparametros.infrastructure.exception.ParametroCatalogoNoDisponibleException;
import com.arquisoft.shared.message.constant.AppCodes;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ParametrosCatalogoMemoriaOutputAdapterTest {

    private static CatalogoParametrosProperties propiedades(Map<String, String> defaults) {
        return new CatalogoParametrosProperties(true, "https://catalogo.test", "https://keycloak.test/token", "id", "secreto",
                Duration.ofSeconds(1), Duration.ofSeconds(3), false, Duration.ofMinutes(5), defaults);
    }

    @Test
    void debeDevolverValorDeLaCopia() {
        // Arrange
        var adaptador = new ParametrosCatalogoMemoriaOutputAdapter(propiedades(Map.of("PARAM_X", "defecto")));
        adaptador.reemplazar(new ParametrosCatalogoEntity(Map.of("PARAM_X", "vigente")));

        // Act
        var valor = adaptador.valor("PARAM_X");

        // Assert
        assertThat(valor).isEqualTo("vigente");
    }

    @Test
    void debeUsarDefault_cuandoNoEstaEnLaCopia() {
        // Arrange
        var adaptador = new ParametrosCatalogoMemoriaOutputAdapter(propiedades(Map.of("PARAM_X", "defecto")));

        // Act
        var valor = adaptador.valor("PARAM_X");

        // Assert
        assertThat(valor).isEqualTo("defecto");
    }

    @Test
    void debeLanzar503_cuandoNoHayCopiaNiDefault() {
        // Arrange
        var adaptador = new ParametrosCatalogoMemoriaOutputAdapter(propiedades(Map.of()));

        // Act / Assert
        assertThatThrownBy(() -> adaptador.valor("PARAM_X"))
                .isInstanceOfSatisfying(ParametroCatalogoNoDisponibleException.class, e -> {
                    assertThat(e.getCodigoError()).isEqualTo(AppCodes.CatalogoParametros.NO_DISPONIBLE);
                    assertThat(e.getError().getMessage()).doesNotContain("PARAM_X");
                    assertThat(e.getMessage()).contains("PARAM_X");
                });
    }

    @Test
    void debeSustituirLaCopiaCompleta_alReemplazar() {
        // Arrange
        var adaptador = new ParametrosCatalogoMemoriaOutputAdapter(propiedades(Map.of()));
        adaptador.reemplazar(new ParametrosCatalogoEntity(Map.of("A", "1")));
        adaptador.reemplazar(new ParametrosCatalogoEntity(Map.of("B", "2")));

        // Act / Assert
        assertThat(adaptador.valor("B")).isEqualTo("2");
        assertThatThrownBy(() -> adaptador.valor("A")).isInstanceOf(ParametroCatalogoNoDisponibleException.class);
    }

    @Test
    void debeRecordarSuspension() {
        // Arrange
        var adaptador = new ParametrosCatalogoMemoriaOutputAdapter(propiedades(Map.of()));

        // Act
        var antes = adaptador.suspendido();
        adaptador.suspender();

        // Assert
        assertThat(antes).isFalse();
        assertThat(adaptador.suspendido()).isTrue();
    }
}
