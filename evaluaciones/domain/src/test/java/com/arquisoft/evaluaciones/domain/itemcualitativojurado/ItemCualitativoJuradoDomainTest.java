package com.arquisoft.evaluaciones.domain.itemcualitativojurado;

import com.arquisoft.evaluaciones.domain.itemcualitativojurado.event.ItemCualitativoJuradoRegistradoEvent;
import com.arquisoft.shared.message.constant.EvaluacionesCodes;
import com.arquisoft.shared.message.constant.EvaluacionesFields;
import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ItemCualitativoJuradoDomainTest {

    @Test
    void debeCrearItemYPublicarEvento_cuandoDatosValidos() {
        // Arrange
        String nombre = "  Claridad conceptual  ";
        String descripcion = "  Evalúa la claridad de la exposición  ";

        // Act
        ItemCualitativoJuradoDomain item =
                ItemCualitativoJuradoDomain.crear(nombre, descripcion);
        var eventos = item.extraerEventosSinPublicar();

        // Assert
        assertThat(item.getId()).isNotNull();
        assertThat(item.getNombre()).isEqualTo("Claridad conceptual");
        assertThat(item.getDescripcion()).isEqualTo("Evalúa la claridad de la exposición");
        assertThat(eventos).singleElement().isInstanceOfSatisfying(
                ItemCualitativoJuradoRegistradoEvent.class,
                evento -> {
                    assertThat(evento.getItemCualitativoJuradoId()).isEqualTo(item.getId());
                    assertThat(evento.getNombre()).isEqualTo(item.getNombre());
                    assertThat(evento.getDescripcion()).isEqualTo(item.getDescripcion());
                    assertThat(evento.getTemaEvento())
                            .isEqualTo(ItemCualitativoJuradoRegistradoEvent.EVENT_TOPIC);
                    assertThat(evento.getTipoEvento())
                            .isEqualTo(ItemCualitativoJuradoRegistradoEvent.EVENT_TYPE);
                    assertThat(evento.getIdEvento()).isNotBlank();
                    assertThat(evento.getOcurridoEn()).isNotNull();
                });
        assertThat(item.extraerEventosSinPublicar()).isEmpty();
    }

    @Test
    void debeAcumularErrores_cuandoCamposEstanEnBlanco() {
        // Arrange
        String nombre = "   ";
        String descripcion = null;

        // Act & Assert
        assertThatThrownBy(() -> ItemCualitativoJuradoDomain.crear(nombre, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception -> {
                    assertThat(exception.getValidationResult().getErrores())
                            .extracting(
                                    error -> error.campo(),
                                    error -> error.codigoError())
                            .containsExactlyInAnyOrder(
                                    org.assertj.core.groups.Tuple.tuple(
                                            EvaluacionesFields.ItemCualitativoJurado.NOMBRE,
                                            EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_REQUERIDO),
                                    org.assertj.core.groups.Tuple.tuple(
                                            EvaluacionesFields.ItemCualitativoJurado.DESCRIPCION,
                                            EvaluacionesCodes.ItemCualitativoJurado.DESCRIPCION_REQUERIDA));
                });
    }

    @Test
    void debeAcumularErrores_cuandoCamposSuperanLongitudMaxima() {
        // Arrange
        String nombre = "n".repeat(101);
        String descripcion = "d".repeat(301);

        // Act & Assert
        assertThatThrownBy(() -> ItemCualitativoJuradoDomain.crear(nombre, descripcion))
                .isInstanceOfSatisfying(DomainValidationException.class, exception -> {
                    assertThat(exception.getValidationResult().getErrores())
                            .extracting(error -> error.codigoError())
                            .containsExactlyInAnyOrder(
                                    EvaluacionesCodes.ItemCualitativoJurado.NOMBRE_DEMASIADO_LARGO,
                                    EvaluacionesCodes.ItemCualitativoJurado.DESCRIPCION_DEMASIADO_LARGA);
                });
    }

    @Test
    void debeReconstruirSinValidarNiPublicar_cuandoSeCargaDesdePersistencia() {
        // Arrange
        UUID id = UUID.randomUUID();
        String nombrePersistido = "";
        String descripcionPersistida = null;

        // Act
        ItemCualitativoJuradoDomain item = ItemCualitativoJuradoDomain.reconstruir(
                id, nombrePersistido, descripcionPersistida);

        // Assert
        assertThat(item.getId()).isEqualTo(id);
        assertThat(item.getNombre()).isEqualTo(nombrePersistido);
        assertThat(item.getDescripcion()).isNull();
        assertThat(item.extraerEventosSinPublicar()).isEmpty();
    }
}
