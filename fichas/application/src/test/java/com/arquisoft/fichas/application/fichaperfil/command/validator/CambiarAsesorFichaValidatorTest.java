package com.arquisoft.fichas.application.fichaperfil.command.validator;

import com.arquisoft.fichas.application.fichaperfil.command.validator.impl.CambiarAsesorFichaValidatorImpl;
import com.arquisoft.fichas.domain.estadofichaperfil.rules.EstadoFichaPerfilEnTerminalRule;
import com.arquisoft.fichas.domain.fichaperfil.CambiarAsesorFichaDomain;
import com.arquisoft.fichas.domain.fichaperfil.model.CambioAsesorFichaCriteria;
import com.arquisoft.fichas.domain.fichaperfil.rules.AsesorFichaDiferenteRule;
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
    private EstadoFichaPerfilEnTerminalRule estadoFichaPerfilEnTerminalRule;

    @Mock
    private AsesorFichaDiferenteRule asesorFichaDiferenteRule;

    @InjectMocks
    private CambiarAsesorFichaValidatorImpl validator;

    @Test
    void debeDelegarEnLasReglas_cuandoValida() {
        // Arrange
        UUID fichaId = UUID.randomUUID();
        UUID asesorActualId = UUID.randomUUID();
        UUID nuevoAsesorId = UUID.randomUUID();
        CambiarAsesorFichaDomain cambio = CambiarAsesorFichaDomain.crear(fichaId, nuevoAsesorId);

        // Act
        validator.validar(cambio, asesorActualId);

        // Assert
        verify(estadoFichaPerfilEnTerminalRule).validar(fichaId);
        verify(asesorFichaDiferenteRule).validar(new CambioAsesorFichaCriteria(nuevoAsesorId, asesorActualId));
    }
}
