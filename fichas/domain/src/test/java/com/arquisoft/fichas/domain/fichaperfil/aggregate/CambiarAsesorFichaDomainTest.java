package com.arquisoft.fichas.domain.fichaperfil.aggregate;

import com.arquisoft.shared.exception.DomainValidationException;
import com.arquisoft.shared.message.constant.FichasFields;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarAsesorFichaDomainTest {

    @Test
    void debeConstruirElCambio_cuandoDatosValidos() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();

        // Act
        CambiarAsesorFichaDomain cambio = CambiarAsesorFichaDomain.crear(fichaId, nuevoAsesorId);

        // Assert — el título no entra en esta transacción; lo aporta la ficha reconstruida
        assertThat(cambio.getFichaPerfil()).isEqualTo(fichaId);
        assertThat(cambio.getNuevoAsesorFicha()).isEqualTo(nuevoAsesorId);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoNuevoAsesorEsNulo() {
        // Arrange
        UUID fichaId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarAsesorFichaDomain.crear(fichaId, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.ASESOR_FICHA);
    }

    @Test
    void debeLanzarDomainValidationException_cuandoFichaPerfilEsNula() {
        // Arrange
        UUID nuevoAsesorId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> CambiarAsesorFichaDomain.crear(null, nuevoAsesorId))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.ID);
    }

    @Test
    void debeAcumularAmbosErrores_cuandoTodosLosCamposSonNulos() {
        // Act & Assert — una sola pasada tiene que reportar los dos campos, no sólo el primero
        assertThatThrownBy(() -> CambiarAsesorFichaDomain.crear(null, null))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining(FichasFields.FichaPerfil.ID)
                .hasMessageContaining(FichasFields.FichaPerfil.ASESOR_FICHA);
    }
}
