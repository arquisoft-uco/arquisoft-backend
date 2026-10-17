package com.arquisoft.fichas.application.observacionitem.command.secondaryport.mapper;

import com.arquisoft.fichas.application.observacionitem.command.secondaryport.entity.ContextoObservacionItemEntity;
import com.arquisoft.fichas.domain.estadorevision.EstadoRevision;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ContextoObservacionItemMapperTest {

    @Test
    void debeConvertirElEstadoACatalogoYCopiarLosIds_cuandoMapeaLaEntity() {
        // Arrange
        var entity = new ContextoObservacionItemEntity(
                UtilUUID.generarNuevoUUID(), "CERRADA", UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());

        // Act
        var contexto = ContextoObservacionItemMapper.toDomain(entity);

        // Assert
        assertThat(contexto.revisionItem()).isEqualTo(entity.revisionItem());
        assertThat(contexto.estadoRevision()).isEqualTo(EstadoRevision.CERRADA);
        assertThat(contexto.fichaPerfil()).isEqualTo(entity.fichaPerfil());
        assertThat(contexto.asesorFicha()).isEqualTo(entity.asesorFicha());
    }
}
