package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilAggregate;
import com.arquisoft.fichas.domain.fichaperfil.model.CambioAsesorFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaExisteRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CambiarAsesorFichaValidatorTest {

    @Mock
    private AsesorFichaExisteRule asesorFichaExisteRule;

    @Mock
    private EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

    @Mock
    private AsesorFichaDiferenteRule asesorFichaDiferenteRule;

    @InjectMocks
    private CambiarAsesorFichaValidator validator;

    @Test
    void debeDelegarEnLasReglas_cuandoValida() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorActualId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        FichaPerfilAggregate ficha = FichaPerfilAggregate.cambiarAsesorFicha(fichaId, nuevoAsesorId);

        // Act
        validator.validar(ficha, asesorActualId);

        // Assert
        verify(asesorFichaExisteRule).validar(nuevoAsesorId);
        verify(estadoFichaPerfilEnTerminalRule).validar(fichaId);
        verify(asesorFichaDiferenteRule).validar(new CambioAsesorFichaCriteria(nuevoAsesorId, asesorActualId));
    }
}
