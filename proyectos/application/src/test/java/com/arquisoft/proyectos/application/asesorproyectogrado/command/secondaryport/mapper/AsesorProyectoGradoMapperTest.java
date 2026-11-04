package com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.mapper;

import com.arquisoft.proyectos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.proyectos.domain.estadoasesor.EstadoAsesor;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorProyectoGradoMapperTest {

    @Test
    void debeConservarTodosLosCampos_cuandoSeHaceElViajeDominioEntidadDominio() {
        // Arrange
        var vinculo = AsesorProyectoGradoDomain.crear(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var entidad = AsesorProyectoGradoMapper.toEntity(vinculo);
        var reconstruido = AsesorProyectoGradoMapper.toDomain(entidad);

        // Assert
        assertThat(entidad.estadoAsesor()).isEqualTo(EstadoAsesor.ASIGNADO.getId());
        assertThat(reconstruido.getId()).isEqualTo(vinculo.getId());
        assertThat(reconstruido.getAsesor()).isEqualTo(vinculo.getAsesor());
        assertThat(reconstruido.getProyectoGrado()).isEqualTo(vinculo.getProyectoGrado());
        assertThat(reconstruido.getEstadoAsesor()).isEqualTo(EstadoAsesor.ASIGNADO);
        assertThat(reconstruido.getFechaAsignado()).isEqualTo(vinculo.getFechaAsignado());
    }
}
