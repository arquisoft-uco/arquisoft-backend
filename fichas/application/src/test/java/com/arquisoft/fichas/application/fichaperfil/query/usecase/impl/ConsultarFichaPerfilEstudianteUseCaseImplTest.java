package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorFichaReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilEstudianteQueryOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarFichaPerfilEstudianteUseCaseImplTest {

    @Mock
    private FichaPerfilEstudianteQueryOutputPort fichaPerfilEstudianteQueryOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ConsultarFichaPerfilEstudianteUseCaseImpl useCase;

    @Test
    void debeDelegarEnPuerto_conCriteria() {
        // Arrange
        var criteria = new FichaPerfilEstudianteCriteria(UUID.randomUUID(), UUID.randomUUID());
        var readModel = new FichaPerfilEstudianteReadModel(
                UUID.randomUUID(), "Titulo",
                new AsesorFichaReadModel(UUID.randomUUID(), "id", "Nombre", "correo@uco.edu.co"),
                new EstadoFichaPerfilReadModel("FORMULACION", "Formulacion", Instant.now()),
                List.of());
        when(fichaPerfilEstudianteQueryOutputPort.consultar(criteria)).thenReturn(Optional.of(readModel));

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).containsSame(readModel);
    }

    @Test
    void debeRetornarVacio_cuandoPuertoNoEncuentra() {
        // Arrange
        var criteria = new FichaPerfilEstudianteCriteria(UUID.randomUUID(), UUID.randomUUID());
        when(fichaPerfilEstudianteQueryOutputPort.consultar(any(FichaPerfilEstudianteCriteria.class)))
                .thenReturn(Optional.empty());

        // Act
        var resultado = useCase.ejecutar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }
}
