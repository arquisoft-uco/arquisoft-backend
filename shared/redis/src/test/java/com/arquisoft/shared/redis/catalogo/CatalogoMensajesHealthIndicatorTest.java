package com.arquisoft.shared.redis.catalogo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoMensajesHealthIndicatorTest {

    private static final String DETALLE_CLAVES_EN_CACHE = "clavesEnCache";

    @Mock
    private CatalogoMensajesRedis catalogo;

    @InjectMocks
    private CatalogoMensajesHealthIndicator indicador;

    @Test
    @DisplayName("reporta UP cuando el catálogo resuelve contra Redis")
    void debeReportarUp_cuandoElCatalogoEstaSano() {
        // Arrange
        when(catalogo.estaDegradado()).thenReturn(false);
        when(catalogo.clavesEnCache()).thenReturn(221);

        // Act
        Health salud = indicador.health();

        // Assert
        assertThat(salud.getStatus()).isEqualTo(Status.UP);
    }

    @Test
    @DisplayName("reporta DOWN cuando el catálogo sirve desde la caché")
    void debeReportarDown_cuandoElCatalogoEstaDegradado() {
        // Arrange
        when(catalogo.estaDegradado()).thenReturn(true);
        when(catalogo.clavesEnCache()).thenReturn(221);

        // Act
        Health salud = indicador.health();

        // Assert
        assertThat(salud.getStatus())
                .as("la aplicación responde, pero con textos que pueden estar obsoletos: es una "
                        + "degradación que un operador tiene que ver")
                .isEqualTo(Status.DOWN);
    }

    @Test
    @DisplayName("publica cuántas claves tiene la caché, sano o degradado")
    void debePublicarElTamanioDeLaCache_cuandoSeConsultaLaSalud() {
        // Arrange
        when(catalogo.estaDegradado()).thenReturn(true);
        when(catalogo.clavesEnCache()).thenReturn(200);

        // Act
        Health salud = indicador.health();

        // Assert
        assertThat(salud.getDetails())
                .as("distingue una caché completa de una a medio poblar, que es la diferencia "
                        + "entre degradar bien y devolver respaldos")
                .containsEntry(DETALLE_CLAVES_EN_CACHE, 200);
    }
}
