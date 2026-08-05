package com.arquisoft.fichas.application.fichaperfil.command.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;
import com.arquisoft.shared.message.FichasFields;
import com.arquisoft.shared.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarAsesorFichaMapperTest {

    @Test
    void debeMapearADomain_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        var command = new CambiarAsesorFichaCommand(fichaId, nuevoAsesorId);

        // Act
        FichaPerfilDomain ficha = CambiarAsesorFichaMapper.toDomain(command);

        // Assert
        assertThat(ficha.getId()).isEqualTo(fichaId);
        assertThat(ficha.getAsesorFicha()).isEqualTo(nuevoAsesorId);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoNuevoAsesorEsNulo() {
        // Arrange
        var command = new CambiarAsesorFichaCommand(UUID.randomUUID(), null);

        // Act & Assert
        assertThatThrownBy(() -> CambiarAsesorFichaMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.ASESOR_FICHA);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoFichaPerfilEsNula() {
        // Arrange
        var command = new CambiarAsesorFichaCommand(null, UUID.randomUUID());

        // Act & Assert
        assertThatThrownBy(() -> CambiarAsesorFichaMapper.toDomain(command))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.ID);
    }
}
