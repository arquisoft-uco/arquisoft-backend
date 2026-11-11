package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.entity.EstadoEntregableProyectoGradoAccesoEntity;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoEntregableProyectoGradoAccesoMapperTest {

    @Test
    void debeReconstruirElDomain_cuandoSeConvierteDesdeEntity() {
        // Arrange
        var fecha = UtilFecha.generarInstanteActual();
        var entity = new EstadoEntregableProyectoGradoAccesoEntity(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "RECHAZADO", fecha);

        // Act
        var domain = EstadoEntregableProyectoGradoAccesoMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(entity.id());
        assertThat(domain.getEntregableProyectoGrado()).isEqualTo(entity.entregableProyectoGrado());
        assertThat(domain.getEstadoEntregable()).isEqualTo(EstadoEntregable.RECHAZADO);
        assertThat(domain.getFechaActualizacion()).isEqualTo(fecha);
    }
}
