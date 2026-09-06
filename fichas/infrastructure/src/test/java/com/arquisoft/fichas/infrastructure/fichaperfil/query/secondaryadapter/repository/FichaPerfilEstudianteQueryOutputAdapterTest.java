package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.secondaryport.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaPerfilEstudianteQueryOutputAdapterTest {

    @Mock
    private FichaPerfilEstudianteQueryRepository fichaPerfilEstudianteQueryRepository;

    @Mock
    private EstudianteFichaPerfilQueryOutputPort estudianteFichaPerfilQueryOutputPort;

    @InjectMocks
    private FichaPerfilEstudianteQueryOutputAdapter adapter;

    @Test
    void debeRetornarFichaCompuesta_cuandoEstudianteEstaVinculado() {
        // Arrange
        var fichaId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var criteria = new FichaPerfilEstudianteCriteria(fichaId, estudianteId);
        var vinculacion = new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), fichaId, estudianteId, "Estudiante Uno", "e1@uco.edu.co");
        var cabecera = FichaPerfilEstudianteJpaQueryEntity.builder()
                .id(fichaId)
                .tituloProyecto("Titulo")
                .asesorId(UUID.randomUUID())
                .asesorIdentificador("A1")
                .asesorNombre("Asesor")
                .asesorEmail("asesor@uco.edu.co")
                .estadoId("FORMULACION")
                .estadoNombre("Formulacion")
                .build();
        when(estudianteFichaPerfilQueryOutputPort.consultarPorFicha(fichaId)).thenReturn(List.of(vinculacion));
        when(fichaPerfilEstudianteQueryRepository.findById(fichaId)).thenReturn(Optional.of(cabecera));

        // Act
        var resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(fichaId);
        assertThat(resultado.get().asesorFicha().nombre()).isEqualTo("Asesor");
        assertThat(resultado.get().estado().id()).isEqualTo("FORMULACION");
        assertThat(resultado.get().estudiantes()).containsExactly(vinculacion);
    }

    @Test
    void debeRetornarVacio_cuandoEstudianteNoEstaVinculado() {
        // Arrange
        var fichaId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var criteria = new FichaPerfilEstudianteCriteria(fichaId, estudianteId);
        var otroEstudiante = new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), fichaId, UUID.randomUUID(), "Otro", "otro@uco.edu.co");
        when(estudianteFichaPerfilQueryOutputPort.consultarPorFicha(fichaId)).thenReturn(List.of(otroEstudiante));

        // Act
        var resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
        verify(fichaPerfilEstudianteQueryRepository, never()).findById(any(UUID.class));
    }

    @Test
    void debeRetornarVacio_cuandoFichaNoExiste() {
        // Arrange
        var fichaId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var criteria = new FichaPerfilEstudianteCriteria(fichaId, estudianteId);
        var vinculacion = new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), fichaId, estudianteId, "Estudiante Uno", "e1@uco.edu.co");
        when(estudianteFichaPerfilQueryOutputPort.consultarPorFicha(fichaId)).thenReturn(List.of(vinculacion));
        when(fichaPerfilEstudianteQueryRepository.findById(fichaId)).thenReturn(Optional.empty());

        // Act
        var resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado).isEmpty();
    }

    @Test
    void debeIncluirTodosLosEstudiantesVinculados_incluyendoAlSolicitante() {
        // Arrange
        var fichaId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var criteria = new FichaPerfilEstudianteCriteria(fichaId, estudianteId);
        var solicitante = new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), fichaId, estudianteId, "Solicitante", "sol@uco.edu.co");
        var companero = new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), fichaId, UUID.randomUUID(), "Companero", "comp@uco.edu.co");
        var cabecera = FichaPerfilEstudianteJpaQueryEntity.builder()
                .id(fichaId)
                .tituloProyecto("Titulo")
                .asesorId(UUID.randomUUID())
                .asesorIdentificador("A1")
                .asesorNombre("Asesor")
                .asesorEmail("asesor@uco.edu.co")
                .estadoId("FORMULACION")
                .estadoNombre("Formulacion")
                .build();
        when(estudianteFichaPerfilQueryOutputPort.consultarPorFicha(fichaId))
                .thenReturn(List.of(solicitante, companero));
        when(fichaPerfilEstudianteQueryRepository.findById(fichaId)).thenReturn(Optional.of(cabecera));

        // Act
        var resultado = adapter.consultar(criteria);

        // Assert
        assertThat(resultado).isPresent();
        assertThat(resultado.get().estudiantes()).containsExactlyInAnyOrder(solicitante, companero);
    }
}
