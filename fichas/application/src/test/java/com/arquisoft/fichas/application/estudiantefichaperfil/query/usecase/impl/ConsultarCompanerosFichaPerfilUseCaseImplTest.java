package com.arquisoft.fichas.application.estudiantefichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCompaneroCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.secondaryport.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.ClaveMensaje;
import com.arquisoft.shared.message.key.fichas.EstudianteFichaPerfilKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarCompanerosFichaPerfilUseCaseImplTest {

    @Mock
    private EstudianteFichaPerfilQueryOutputPort estudianteFichaPerfilQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarCompanerosFichaPerfilUseCaseImpl useCase;

    @Test
    void debeRetornarCompaneros_cuandoElOutputPortLosDevuelve() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var criteria = new EstudianteFichaPerfilCompaneroCriteria(fichaPerfil, estudiante);
        var esperado = List.of(new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), fichaPerfil, UUID.randomUUID(), "Ana Ruiz", "ana.ruiz@uco.edu.co"));
        when(estudianteFichaPerfilQueryOutputPort.consultarCompanerosPorFichaYEstudiante(fichaPerfil, estudiante))
                .thenReturn(esperado);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void debeRetornarListaVacia_cuandoNoHayCompaneros() {
        // Arrange
        var criteria = new EstudianteFichaPerfilCompaneroCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(estudianteFichaPerfilQueryOutputPort.consultarCompanerosPorFichaYEstudiante(any(), any()))
                .thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(eq(EstudianteFichaPerfilKey.LOG_CONSULTA_COMPANEROS_COMPLETADA), eq(0));
    }

    @Test
    void debeInvocarOutputPortConFichaYEstudianteEnOrden_yRegistrarDebugSinInfo() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var criteria = new EstudianteFichaPerfilCompaneroCriteria(fichaPerfil, estudiante);
        when(estudianteFichaPerfilQueryOutputPort.consultarCompanerosPorFichaYEstudiante(any(), any()))
                .thenReturn(List.of());

        // Act
        useCase.ejecutar(criteria);

        // Assert
        verify(estudianteFichaPerfilQueryOutputPort)
                .consultarCompanerosPorFichaYEstudiante(fichaPerfil, estudiante);
        verify(logger).debug(eq(EstudianteFichaPerfilKey.LOG_CONSULTANDO_COMPANEROS), eq(fichaPerfil), eq(estudiante));
        verify(logger, never()).info(any(ClaveMensaje.class), any());
    }
}
