package com.arquisoft.fichas.domain.fichaperfil.rules.impl;

import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;
import com.arquisoft.fichas.domain.fichaperfil.exception.FichaTituloDuplicadoException;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
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
class FichaPerfilTituloDisponibleRuleImplTest {

    @Mock
    private FichaPerfilOutputPort puerto;

    @InjectMocks
    private FichaPerfilTituloDisponibleRuleImpl regla;

    @Test
    void debeLanzarExcepcion_cuandoOtraFichaTieneElMismoTitulo() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        var modificacion = ModificacionFichaPerfilDomain.crear(fichaId, "Titulo duplicado", UUID.randomUUID());
        when(puerto.existeTituloEnOtraFicha(fichaId, "Titulo duplicado")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(modificacion))
                .isInstanceOf(FichaTituloDuplicadoException.class);
    }

    @Test
    void debePasar_cuandoNingunaOtraFichaTieneEseTitulo() {
        // Arrange — incluye el caso de reenviar el mismo título de la propia ficha
        UUID fichaId = UUID.randomUUID();
        var modificacion = ModificacionFichaPerfilDomain.crear(fichaId, "Titulo sin cambios", UUID.randomUUID());
        when(puerto.existeTituloEnOtraFicha(fichaId, "Titulo sin cambios")).thenReturn(false);

        // Act & Assert
        assertThatCode(() -> regla.validar(modificacion)).doesNotThrowAnyException();
    }
}
