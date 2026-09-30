package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilEstudianteQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarFichasPerfilEstudianteUseCaseImplTest {

    @Mock
    private FichaPerfilEstudianteQueryOutputPort fichaPerfilEstudianteQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarFichasPerfilEstudianteUseCaseImpl useCase;

    @Test
    void debeDelegarEnPuertoYRetornarListaSinTransformar_cuandoHayFichas() {
        // Arrange
        var estudiante = UUID.randomUUID();
        var criteria = new FichaPerfilEstudianteCriteria(estudiante);
        var readModel = new FichaPerfilEstudianteReadModel(
                UUID.randomUUID(), "Titulo",
                new AsesorFichaReadModel(UUID.randomUUID(), "id", "Nombre", "correo@uco.edu.co"),
                new EstadoFichaPerfilReadModel("FORMULACION", "Formulacion", Instant.now()),
                List.of());
        var fichas = List.of(readModel, readModel);
        when(fichaPerfilEstudianteQueryOutputPort.consultarPorEstudiante(criteria)).thenReturn(fichas);

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isSameAs(fichas);
        verify(logger).debug(FichaPerfilKey.LOG_CONSULTANDO_ESTUDIANTE, estudiante);
        verify(logger).debug(FichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, 2);
    }

    @Test
    void debeRetornarListaVacia_cuandoPuertoNoEncuentraFichas() {
        // Arrange
        var criteria = new FichaPerfilEstudianteCriteria(UUID.randomUUID());
        when(fichaPerfilEstudianteQueryOutputPort.consultarPorEstudiante(criteria)).thenReturn(List.of());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(logger).debug(FichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, 0);
    }
}
