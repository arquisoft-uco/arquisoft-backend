package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.readmodel.EstudianteFichaPerfilReadModel;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.secondaryport.EstudianteFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
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

    private static FichaPerfilEstudianteJpaQueryEntity cabecera(UUID id, String titulo) {
        return FichaPerfilEstudianteJpaQueryEntity.builder()
                .id(id)
                .tituloProyecto(titulo)
                .asesorId(UUID.randomUUID())
                .asesorIdentificador("A1")
                .asesorNombre("Asesor")
                .asesorEmail("asesor@uco.edu.co")
                .estadoId("FORMULACION")
                .estadoNombre("Formulacion")
                .build();
    }

    private static EstudianteFichaPerfilReadModel vinculo(UUID ficha, UUID estudiante, String nombre) {
        return new EstudianteFichaPerfilReadModel(
                UUID.randomUUID(), ficha, estudiante, nombre, nombre + "@uco.edu.co", true);
    }

    @Test
    void debeRetornarFichaCompuesta_cuandoEstudiantePerteneceAUnaFicha() {
        // Arrange
        var fichaId = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var criteria = new FichaPerfilEstudianteCriteria(estudianteId);
        var propio = vinculo(fichaId, estudianteId, "propio");
        var companero = vinculo(fichaId, UUID.randomUUID(), "companero");
        when(estudianteFichaPerfilQueryOutputPort.consultarVigentesDeFichasDelEstudiante(estudianteId))
                .thenReturn(List.of(propio, companero));
        when(fichaPerfilEstudianteQueryRepository.findByIdInOrderByTituloProyectoAsc(anyCollection()))
                .thenReturn(List.of(cabecera(fichaId, "Titulo")));

        // Act
        var resultado = adapter.consultarPorEstudiante(criteria);

        // Assert
        assertThat(resultado).singleElement().satisfies(rm -> {
            assertThat(rm.id()).isEqualTo(fichaId);
            assertThat(rm.tituloProyecto()).isEqualTo("Titulo");
            assertThat(rm.asesorFicha().nombre()).isEqualTo("Asesor");
            assertThat(rm.estado().id()).isEqualTo("FORMULACION");
            assertThat(rm.estudiantes()).containsExactlyInAnyOrder(propio, companero);
        });
    }

    @Test
    void debeRepartirACadaFichaSoloSusEstudiantes_cuandoPerteneceAVariasFichas() {
        // Arrange
        var fichaUno = UUID.randomUUID();
        var fichaDos = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        var criteria = new FichaPerfilEstudianteCriteria(estudianteId);
        var propioUno = vinculo(fichaUno, estudianteId, "propio1");
        var companeroUno = vinculo(fichaUno, UUID.randomUUID(), "companero1");
        var propioDos = vinculo(fichaDos, estudianteId, "propio2");
        when(estudianteFichaPerfilQueryOutputPort.consultarVigentesDeFichasDelEstudiante(estudianteId))
                .thenReturn(List.of(propioUno, companeroUno, propioDos));
        when(fichaPerfilEstudianteQueryRepository.findByIdInOrderByTituloProyectoAsc(anyCollection()))
                .thenReturn(List.of(cabecera(fichaUno, "Alfa"), cabecera(fichaDos, "Beta")));

        // Act
        var resultado = adapter.consultarPorEstudiante(criteria);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).tituloProyecto()).isEqualTo("Alfa");
        assertThat(resultado.get(0).estudiantes()).containsExactlyInAnyOrder(propioUno, companeroUno);
        assertThat(resultado.get(1).tituloProyecto()).isEqualTo("Beta");
        assertThat(resultado.get(1).estudiantes()).containsExactly(propioDos);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<UUID>> captor = ArgumentCaptor.forClass(Collection.class);
        verify(fichaPerfilEstudianteQueryRepository).findByIdInOrderByTituloProyectoAsc(captor.capture());
        assertThat(captor.getValue()).containsExactlyInAnyOrder(fichaUno, fichaDos);
    }

    @Test
    void debeRetornarListaVaciaSinConsultarCabeceras_cuandoNoHayVinculos() {
        // Arrange
        var estudianteId = UUID.randomUUID();
        when(estudianteFichaPerfilQueryOutputPort.consultarVigentesDeFichasDelEstudiante(estudianteId))
                .thenReturn(List.of());

        // Act
        var resultado = adapter.consultarPorEstudiante(new FichaPerfilEstudianteCriteria(estudianteId));

        // Assert
        assertThat(resultado).isEmpty();
        verify(fichaPerfilEstudianteQueryRepository, never()).findByIdInOrderByTituloProyectoAsc(anyCollection());
    }

    @Test
    void debeOmitirLaFicha_cuandoNoTieneCabecera() {
        // Arrange
        var fichaConCabecera = UUID.randomUUID();
        var fichaSinCabecera = UUID.randomUUID();
        var estudianteId = UUID.randomUUID();
        when(estudianteFichaPerfilQueryOutputPort.consultarVigentesDeFichasDelEstudiante(estudianteId))
                .thenReturn(List.of(vinculo(fichaConCabecera, estudianteId, "a"),
                        vinculo(fichaSinCabecera, estudianteId, "b")));
        when(fichaPerfilEstudianteQueryRepository.findByIdInOrderByTituloProyectoAsc(anyCollection()))
                .thenReturn(List.of(cabecera(fichaConCabecera, "Con cabecera")));

        // Act
        var resultado = adapter.consultarPorEstudiante(new FichaPerfilEstudianteCriteria(estudianteId));

        // Assert
        assertThat(resultado)
                .extracting(rm -> rm.id())
                .containsExactly(fichaConCabecera);
    }
}
