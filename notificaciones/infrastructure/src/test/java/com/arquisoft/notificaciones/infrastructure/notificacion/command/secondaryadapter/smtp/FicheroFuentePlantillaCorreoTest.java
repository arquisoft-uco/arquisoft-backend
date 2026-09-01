package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.infrastructure.config.NotificacionProperties;
import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FicheroFuentePlantillaCorreoTest {

    @TempDir
    private Path directorio;

    private static FicheroFuentePlantillaCorreo fuenteCon(String ubicacion) {
        var properties = new NotificacionProperties();
        properties.setPlantilla(ubicacion);
        return new FicheroFuentePlantillaCorreo(new DefaultResourceLoader(), properties);
    }

    @Test
    void debeLeerLaPlantilla_cuandoLaUbicacionEsUnFicheroExterno() throws IOException {
        // Arrange
        Path plantilla = directorio.resolve("correo.html");
        Files.writeString(plantilla, "<p>{{cuerpo}}</p>", StandardCharsets.UTF_8);

        // Act
        FicheroFuentePlantillaCorreo fuente = fuenteCon("file:" + plantilla);

        // Assert
        assertThat(fuente.obtener()).isEqualTo("<p>{{cuerpo}}</p>");
    }

    @Test
    void debeConservarLosAcentos_cuandoElFicheroEstaEnUtf8() throws IOException {
        // Arrange
        Path plantilla = directorio.resolve("acentos.html");
        Files.writeString(plantilla, "<p>Asignación de asesoría</p>", StandardCharsets.UTF_8);

        // Act
        FicheroFuentePlantillaCorreo fuente = fuenteCon("file:" + plantilla);

        // Assert
        assertThat(fuente.obtener()).contains("Asignación de asesoría");
    }

    @Test
    void debeFallar_cuandoElFicheroEstaVacio() throws IOException {
        // Arrange
        Path plantilla = directorio.resolve("vacia.html");
        Files.writeString(plantilla, "   ", StandardCharsets.UTF_8);

        // Act & Assert
        assertThatThrownBy(() -> fuenteCon("file:" + plantilla))
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class)
                .hasMessageContaining("vacia.html");
    }

    @Test
    void debeFallar_cuandoLaUbicacionApuntaAUnDirectorio() {
        // Act & Assert
        assertThatThrownBy(() -> fuenteCon("file:" + directorio))
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class);
    }
}
