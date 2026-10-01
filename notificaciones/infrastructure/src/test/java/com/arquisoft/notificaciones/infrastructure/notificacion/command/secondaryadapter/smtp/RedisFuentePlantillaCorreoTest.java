package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.infrastructure.config.NotificacionProperties;
import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisFuentePlantillaCorreoTest {

    private static final String CLAVE = "plantilla.correo-base";
    private static final String COMPLETA = "<p>{{titulo}}{{cuerpo}}{{pie}}</p>";
    private static final String COMPLETA_V2 = "<h1>{{titulo}}</h1><p>{{cuerpo}}</p><i>{{pie}}</i>";

    @Mock
    private StringRedisTemplate redis;

    @Mock
    private ValueOperations<String, String> operaciones;

    private RedisFuentePlantillaCorreo fuenteQueLee(String... contenidos) {
        when(redis.opsForValue()).thenReturn(operaciones);
        var stub = when(operaciones.get(CLAVE)).thenReturn(contenidos[0]);
        for (int i = 1; i < contenidos.length; i++) {
            stub = stub.thenReturn(contenidos[i]);
        }
        return new RedisFuentePlantillaCorreo(redis, properties());
    }

    private static NotificacionProperties properties() {
        var properties = new NotificacionProperties();
        properties.setPlantilla(CLAVE);
        return properties;
    }

    @Test
    void debeLeerLaPlantilla_cuandoLaClaveEstaEnRedis() {
        // Act
        RedisFuentePlantillaCorreo fuente = fuenteQueLee(COMPLETA);

        // Assert
        assertThat(fuente.obtener()).isEqualTo(COMPLETA);
    }

    @Test
    void debeConservarLosAcentos_cuandoLaPlantillaLosTrae() {
        // Act
        RedisFuentePlantillaCorreo fuente = fuenteQueLee(
                "<p>Asignación de asesoría {{titulo}}{{cuerpo}}{{pie}}</p>");

        // Assert
        assertThat(fuente.obtener()).contains("Asignación de asesoría");
    }

    @Test
    void debeSeguirSirviendoLaPlantilla_cuandoRedisCaeTrasElArranque() {
        // Arrange
        RedisFuentePlantillaCorreo fuente = fuenteQueLee(COMPLETA);

        // Act & Assert
        assertThat(fuente.obtener()).isEqualTo(COMPLETA);
        assertThat(fuente.obtener()).isEqualTo(COMPLETA);
    }

    @Test
    void debeFallar_cuandoLaClaveNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> fuenteQueLee((String) null))
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class)
                .hasMessageContaining(CLAVE);
    }

    @Test
    void debeFallar_cuandoLaPlantillaEstaEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> fuenteQueLee("   "))
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class)
                .hasMessageContaining(CLAVE);
    }

    @Test
    void debeFallarAlArrancar_cuandoLaPlantillaNoTraeUnHueco() {
        // Act & Assert
        assertThatThrownBy(() -> fuenteQueLee("<p>{{titulo}}{{pie}}</p>"))
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class)
                .hasMessageContaining(HuecosPlantillaCorreo.CUERPO);
    }

    @Test
    void debeFallar_cuandoRedisNoRespondeAlArrancar() {
        // Arrange
        when(redis.opsForValue()).thenReturn(operaciones);
        when(operaciones.get(CLAVE)).thenThrow(new QueryTimeoutException("sin conexion"));

        // Act & Assert
        assertThatThrownBy(() -> new RedisFuentePlantillaCorreo(redis, properties()))
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class)
                .hasMessageContaining(CLAVE);
    }

    @Test
    void debePublicarLaNuevaVersion_cuandoLaRecargaTraeUnaPlantillaDistinta() {
        // Arrange
        RedisFuentePlantillaCorreo fuente = fuenteQueLee(COMPLETA, COMPLETA_V2);

        // Act
        boolean cambio = fuente.recargar();

        // Assert
        assertThat(cambio).isTrue();
        assertThat(fuente.obtener()).isEqualTo(COMPLETA_V2);
    }

    @Test
    void debeInformarQueNoHuboCambio_cuandoLaRecargaTraeLaMismaPlantilla() {
        // Arrange
        RedisFuentePlantillaCorreo fuente = fuenteQueLee(COMPLETA, COMPLETA);

        // Act
        boolean cambio = fuente.recargar();

        // Assert
        assertThat(cambio).isFalse();
        assertThat(fuente.obtener()).isEqualTo(COMPLETA);
    }

    @Test
    void debeConservarLaPlantillaAnterior_cuandoLaRecargaTraeUnaSinHuecos() {
        // Arrange
        RedisFuentePlantillaCorreo fuente = fuenteQueLee(COMPLETA, "<p>{{titulo}}{{pie}}</p>");

        // Act & Assert
        assertThatThrownBy(fuente::recargar)
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class)
                .hasMessageContaining(HuecosPlantillaCorreo.CUERPO);
        assertThat(fuente.obtener()).isEqualTo(COMPLETA);
    }

    @Test
    void debeConservarLaPlantillaAnterior_cuandoRedisCaeDuranteLaRecarga() {
        // Arrange
        when(redis.opsForValue()).thenReturn(operaciones);
        when(operaciones.get(CLAVE))
                .thenReturn(COMPLETA)
                .thenThrow(new QueryTimeoutException("sin conexion"));
        var fuente = new RedisFuentePlantillaCorreo(redis, properties());

        // Act & Assert
        assertThatThrownBy(fuente::recargar)
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class);
        assertThat(fuente.obtener()).isEqualTo(COMPLETA);
    }

    @Test
    void debeConservarLaPlantillaAnterior_cuandoLaRecargaLaEncuentraBorrada() {
        // Arrange
        RedisFuentePlantillaCorreo fuente = fuenteQueLee(COMPLETA, null);

        // Act & Assert
        assertThatThrownBy(fuente::recargar)
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class);
        assertThat(fuente.obtener()).isEqualTo(COMPLETA);
    }
}
