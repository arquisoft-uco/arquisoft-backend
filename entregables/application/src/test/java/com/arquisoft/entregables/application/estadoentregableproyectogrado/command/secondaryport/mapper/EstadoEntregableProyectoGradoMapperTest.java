package com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.mapper;

import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoEntregableProyectoGradoMapperTest {

    @Test
    void debeConservarCamposEnIdaYVuelta_cuandoSeConvierte() {
        // Arrange
        var estado = EstadoEntregableProyectoGradoDomain.crearConEstado(
                UtilUUID.generarNuevoUUID(), EstadoEntregable.EN_EVALUACION);

        // Act
        var entity = EstadoEntregableProyectoGradoMapper.toEntity(estado);
        var vuelta = EstadoEntregableProyectoGradoMapper.toDomain(entity);

        // Assert
        assertThat(entity.id()).isEqualTo(estado.getId());
        assertThat(entity.entregableProyectoGrado()).isEqualTo(estado.getEntregableProyectoGrado());
        assertThat(entity.estadoEntregable()).isEqualTo("EN_EVALUACION");
        assertThat(entity.fechaActualizacion()).isEqualTo(estado.getFechaActualizacion());
        assertThat(vuelta.getId()).isEqualTo(estado.getId());
        assertThat(vuelta.getEntregableProyectoGrado()).isEqualTo(estado.getEntregableProyectoGrado());
        assertThat(vuelta.getEstadoEntregable()).isEqualTo(EstadoEntregable.EN_EVALUACION);
        assertThat(vuelta.getFechaActualizacion()).isEqualTo(estado.getFechaActualizacion());
    }
}
