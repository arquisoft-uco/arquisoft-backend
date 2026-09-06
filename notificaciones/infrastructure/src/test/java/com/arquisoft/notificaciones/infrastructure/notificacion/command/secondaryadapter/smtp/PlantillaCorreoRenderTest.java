package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.DestinatarioNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.infrastructure.notificacion.exception.PlantillaCorreoNoDisponibleException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlantillaCorreoRenderTest {

    // La plantilla desplegada ya no esta en el classpath: vive en plantillas/ y un script la carga
    // en Redis. El test la lee del repositorio para que siga siendo la real la que se prueba.
    private static final Path PLANTILLA_DESPLEGADA =
            Path.of("..", "..", "plantillas", "correo-base.html");

    private final PlantillaCorreoRender render = new PlantillaCorreoRender(plantillaDesplegada());

    static FuentePlantillaCorreo plantillaDesplegada() {
        try {
            String contenido = Files.readString(PLANTILLA_DESPLEGADA, StandardCharsets.UTF_8);
            return () -> contenido;
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "No se encontro " + PLANTILLA_DESPLEGADA.toAbsolutePath(), e);
        }
    }

    private MensajeNotificacion mensajeCon(String asunto, String cuerpo, String pie) {
        return MensajeNotificacion.textoPlano(
                new DestinatarioNotificacion("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                asunto, cuerpo, pie);
    }

    @Test
    void debePasarLaVerificacionDeHuecos_cuandoSeUsaLaPlantillaDesplegada() {
        // Act & Assert
        render.verificarHuecos();
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

    // El titulo del proyecto lo escribe un usuario; nada le impide llamarlo "{{cuerpo}}". escapar()
    // no toca las llaves, asi que con replace() encadenado ese literal insertado en el titulo lo
    // pisaba la sustitucion del cuerpo.
    @Test
    void debeSustituirCadaMarcadorUnaSolaVez_cuandoUnValorContieneOtroMarcador() {
        // Act
        String html = render.envolver(
                mensajeCon("Ficha {{cuerpo}} y {{pie}}", "TEXTO_CUERPO", "TEXTO_PIE"));

        // Assert
        assertThat(html).contains("Ficha {{cuerpo}} y {{pie}}");
        assertThat(apariciones(html, "TEXTO_CUERPO")).isEqualTo(1);
        assertThat(apariciones(html, "TEXTO_PIE")).isEqualTo(1);
    }

    // $ y \ son metacaracteres en la cadena de reemplazo de Matcher#appendReplacement; el valor
    // tiene que salir literal, como salia con String#replace.
    @Test
    void debeConservarLosSignosDeReemplazoDeRegex_cuandoUnValorLosTrae() {
        // Act
        String html = render.envolver(
                mensajeCon("Presupuesto $1000 y $2000", "Ruta C:\\datos $x", "Pie"));

        // Assert
        assertThat(html).contains("Presupuesto $1000 y $2000");
        assertThat(html).contains("Ruta C:\\datos $x");
    }

    private static int apariciones(String texto, String fragmento) {
        return texto.split(java.util.regex.Pattern.quote(fragmento), -1).length - 1;
    }
}
