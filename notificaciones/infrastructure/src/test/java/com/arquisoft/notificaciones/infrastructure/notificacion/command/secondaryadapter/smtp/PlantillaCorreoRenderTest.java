package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.DestinatarioNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionProperties;
import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;
import org.springframework.core.io.DefaultResourceLoader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlantillaCorreoRenderTest {

    private final PlantillaCorreoRender render =
            new PlantillaCorreoRender(fuenteCon("classpath:plantillas/correo-base.html"));

    private static FicheroFuentePlantillaCorreo fuenteCon(String ubicacion) {
        var properties = new NotificacionProperties();
        properties.setPlantilla(ubicacion);
        return new FicheroFuentePlantillaCorreo(new DefaultResourceLoader(), properties);
    }

    private MensajeNotificacion mensajeCon(String asunto, String cuerpo, String pie) {
        return MensajeNotificacion.textoPlano(
                new DestinatarioNotificacion("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                asunto, cuerpo, pie);
    }

    @Test
    void debeSustituirLosTresHuecos_cuandoSeEnvuelveUnMensaje() {
        // Act
        String html = render.envolver(
                mensajeCon("Se te asigno la ficha", "Hola Ana", "Correo automatico"));

        // Assert
        assertThat(html)
                .contains("Se te asigno la ficha")
                .contains("Hola Ana")
                .contains("Correo automatico")
                .doesNotContain("{{");
    }

    @Test
    void debeConservarElPorcentajeDelCss_cuandoSeEnvuelveUnMensaje() {
        // Act
        String html = render.envolver(mensajeCon("Asunto", "Cuerpo", "Pie"));

        // Assert
        assertThat(html).contains("width=\"100%\"");
    }

    @Test
    void debeEscaparElMarcado_cuandoUnValorTraeCaracteresDeHtml() {
        // Act
        String html = render.envolver(
                mensajeCon("Proyecto <script>alert(1)</script>", "Cuerpo & mas", "Pie"));

        // Assert
        assertThat(html)
                .contains("&lt;script&gt;")
                .contains("Cuerpo &amp; mas")
                .doesNotContain("<script>");
    }

    @Test
    void debeDejarElHuecoVacio_cuandoElValorEstaEnBlanco() {
        // Act
        String html = render.envolver(mensajeCon("Asunto", "Cuerpo", "   "));

        // Assert
        assertThat(html).doesNotContain("{{pie}}");
    }

    @Test
    void debeFallarAlArrancar_cuandoLaPlantillaNoTraeUnHueco() {
        // Arrange
        FuentePlantillaCorreo sinCuerpo = () -> "<p>{{titulo}}</p><p>{{pie}}</p>";

        // Act & Assert
        assertThatThrownBy(() -> new PlantillaCorreoRender(sinCuerpo).verificarHuecos())
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class)
                .hasMessageContaining("{{cuerpo}}");
    }

    @Test
    void debeFallar_cuandoLaUbicacionConfiguradaNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> fuenteCon("classpath:plantillas/no-existe.html"))
                .isInstanceOf(PlantillaCorreoNoDisponibleException.class)
                .hasMessageContaining("no-existe.html");
    }
}
