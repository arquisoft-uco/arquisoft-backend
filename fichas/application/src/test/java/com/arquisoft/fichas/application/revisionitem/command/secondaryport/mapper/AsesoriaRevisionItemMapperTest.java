package com.arquisoft.fichas.application.revisionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.application.revisionitem.command.secondaryport.entity.AsesoriaRevisionItemEntity;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AsesoriaRevisionItemMapperTest {

    @Test
    void debeConvertirElEstadoAEnum_cuandoMapeaLaEntityADomain() {
        // Arrange
        var fichaPerfil = UtilUUID.generarNuevoUUID();
        var asesorFicha = UtilUUID.generarNuevoUUID();
        var entity = new AsesoriaRevisionItemEntity(fichaPerfil, asesorFicha, "CERRADA");

        // Act
        var asesoria = AsesoriaRevisionItemMapper.toDomain(entity);

        // Assert
        assertThat(asesoria.fichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(asesoria.asesorFicha()).isEqualTo(asesorFicha);
        assertThat(asesoria.estadoRevision()).isEqualTo(EstadoRevision.CERRADA);
    }
}
