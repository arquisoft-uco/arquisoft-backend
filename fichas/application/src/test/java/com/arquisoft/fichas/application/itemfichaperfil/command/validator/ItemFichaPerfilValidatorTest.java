package com.arquisoft.fichas.application.itemfichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.PropietarioFichaCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.port.out.FichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemFichaNoPropiaException;
import com.arquisoft.fichas.application.itemfichaperfil.exception.ItemTipoDuplicadoException;
import com.arquisoft.fichas.domain.itemfichaperfil.port.out.ItemFichaPerfilOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemFichaPerfilValidatorTest {

    private static final String TIPO_ITEM = "OBJETIVO_GENERAL";

    @Mock
    private FichaPerfilQueryOutputPort fichaPerfilQueryOutputPort;

    @Mock
    private ItemFichaPerfilOutputPort itemFichaPerfilOutputPort;

    @InjectMocks
    private ItemFichaPerfilValidator validator;

    @Test
    void debeLanzarExcepcion_cuandoFichaNoEsDelEstudiante() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(
                new PropietarioFichaCriteria(ficha, estudiante))).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarFichaPropia(ficha, estudiante))
                .isInstanceOf(ItemFichaNoPropiaException.class)
                .hasMessageContaining(ficha.toString());
    }

    @Test
    void debePasar_cuandoFichaEsDelEstudiante() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        UUID estudiante = UUID.randomUUID();
        when(fichaPerfilQueryOutputPort.esEstudiantePropietario(
                new PropietarioFichaCriteria(ficha, estudiante))).thenReturn(true);

        // Act & Assert
        assertThatCode(() -> validator.validarFichaPropia(ficha, estudiante)).doesNotThrowAnyException();
    }

    @Test
    void debeLanzarExcepcion_cuandoTipoDeItemYaExisteEnLaFicha() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.existePorFichaYTipoItem(ficha, TIPO_ITEM)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> validator.validarTipoNoDuplicado(ficha, TIPO_ITEM))
                .isInstanceOf(ItemTipoDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoTipoDeItemNoExisteEnLaFicha() {
        // Arrange
        UUID ficha = UUID.randomUUID();
        when(itemFichaPerfilOutputPort.existePorFichaYTipoItem(ficha, TIPO_ITEM)).thenReturn(false);

        // Act & Assert
        assertThatCode(() -> validator.validarTipoNoDuplicado(ficha, TIPO_ITEM)).doesNotThrowAnyException();
    }
}
