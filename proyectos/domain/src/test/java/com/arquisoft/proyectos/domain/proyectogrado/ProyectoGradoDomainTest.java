package com.arquisoft.proyectos.domain.proyectogrado;

import com.arquisoft.proyectos.domain.estadoproyectogrado.EstadoProyectoGrado;
import com.arquisoft.shared.message.constant.ProyectosCodes;
import com.arquisoft.shared.message.constant.ProyectosFields;
import com.arquisoft.shared.message.constant.ProyectosLimits;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProyectoGradoDomainTest {

    @Test
    void debeCrearEnProcesoConTituloRecortado_cuandoDatosValidos() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var coordinador = UUID.randomUUID();

        // Act
        var proyecto = ProyectoGradoDomain.crear(fichaPerfil, "  Sistema de gestion academica  ", coordinador);

        // Assert
        assertThat(proyecto.getId()).isNotNull();
        assertThat(proyecto.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(proyecto.getTituloProyecto()).isEqualTo("Sistema de gestion academica");
        assertThat(proyecto.getCoordinador()).isEqualTo(coordinador);
        assertThat(proyecto.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.EN_PROCESO);
        assertThat(proyecto.esVacio()).isFalse();
    }

    @Test
    void debeAceptarTituloEnElLimite_cuandoMideExactamenteElMaximoTrasRecortar() {
        // Arrange
        var titulo = " " + "a".repeat(ProyectosLimits.ProyectoGrado.TITULO_MAX) + " ";

        // Act
        var proyecto = ProyectoGradoDomain.crear(UUID.randomUUID(), titulo, UUID.randomUUID());

        // Assert
        assertThat(proyecto.getTituloProyecto()).hasSize(ProyectosLimits.ProyectoGrado.TITULO_MAX);
    }

    @Test
    void debeAcumularTodosLosErrores_cuandoFichaNulaTituloLargoYCoordinadorNulo() {
        // Arrange
        var tituloLargo = "a".repeat(ProyectosLimits.ProyectoGrado.TITULO_MAX + 1);

        // Act & Assert
        assertThatThrownBy(() -> ProyectoGradoDomain.crear(null, tituloLargo, null))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> {
                    var errores = ((DomainValidationException) ex).getValidationResult().getErrores();
                    assertThat(errores).hasSize(3);
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.ProyectoGrado.FICHA_PERFIL);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.ProyectoGrado.FICHA_PERFIL_ID_REQUERIDO);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.ProyectoGrado.TITULO_PROYECTO);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.ProyectoGrado.TITULO_LONGITUD_MAXIMA);
                    });
                    assertThat(errores).anySatisfy(error -> {
                        assertThat(error.campo()).isEqualTo(ProyectosFields.ProyectoGrado.COORDINADOR);
                        assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.ProyectoGrado.COORDINADOR_ID_REQUERIDO);
                    });
                });
    }

    @Test
    void debeRechazarTitulo_cuandoSoloTieneEspacios() {
        // Act & Assert
        assertThatThrownBy(() -> ProyectoGradoDomain.crear(UUID.randomUUID(), "   ", UUID.randomUUID()))
                .isInstanceOf(DomainValidationException.class)
                .satisfies(ex -> assertThat(((DomainValidationException) ex).getValidationResult().getErrores())
                        .singleElement()
                        .satisfies(error -> assertThat(error.codigoError())
                                .isEqualTo(ProyectosCodes.ProyectoGrado.TITULO_REQUERIDO)));
    }

    @Test
    void debeReconstruirSinValidar_cuandoReconstruirEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        var fichaPerfil = UUID.randomUUID();
        var coordinador = UUID.randomUUID();

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(id, fichaPerfil, "Titulo", coordinador,
                EstadoProyectoGrado.FINALIZADO);

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(proyecto.getTituloProyecto()).isEqualTo("Titulo");
        assertThat(proyecto.getCoordinador()).isEqualTo(coordinador);
        assertThat(proyecto.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.FINALIZADO);
        assertThat(proyecto.esVacio()).isFalse();
        assertThat(ProyectoGradoDomain.VACIO.esVacio()).isTrue();
    }
}
