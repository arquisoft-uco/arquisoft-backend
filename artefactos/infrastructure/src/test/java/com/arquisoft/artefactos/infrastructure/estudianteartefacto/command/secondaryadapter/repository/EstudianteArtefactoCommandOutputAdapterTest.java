package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DataJpaTest
class EstudianteArtefactoCommandOutputAdapterTest {

    @Autowired
    private EstudianteArtefactoCommandRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeRetornarVerdadero_cuandoElEstudianteEstaVinculadoAlArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(EstudianteArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).artefacto(artefacto).estudiante(estudiante).build());
        entityManager.flush();
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository, mock(AppLogger.class));

        // Act & Assert
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, estudiante)).isTrue();
    }

    @Test
    void debeRetornarFalso_cuandoElVinculoEsDeOtroEstudianteOArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        entityManager.persist(EstudianteArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).artefacto(artefacto).estudiante(estudiante).build());
        entityManager.flush();
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository, mock(AppLogger.class));

        // Act & Assert
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, UtilUUID.generarNuevoUUID())).isFalse();
        assertThat(adapter.existePorArtefactoYEstudiante(UtilUUID.generarNuevoUUID(), estudiante)).isFalse();
    }

    @Test
    void debeGuardarTodosYLogear_cuandoSeVinculanVariosEstudiantes() {
        // Arrange
        var logger = mock(AppLogger.class);
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository, logger);
        var artefacto = UtilUUID.generarNuevoUUID();
        var primero = new EstudianteArtefactoEntity(
                UtilUUID.generarNuevoUUID(), artefacto, UtilUUID.generarNuevoUUID());
        var segundo = new EstudianteArtefactoEntity(
                UtilUUID.generarNuevoUUID(), artefacto, UtilUUID.generarNuevoUUID());

        // Act
        adapter.vincularTodos(List.of(primero, segundo));
        entityManager.flush();
        entityManager.clear();

        // Assert
        var guardado = entityManager.find(EstudianteArtefactoJpaEntity.class, segundo.id());
        assertThat(guardado.getArtefacto()).isEqualTo(artefacto);
        assertThat(guardado.getEstudiante()).isEqualTo(segundo.estudiante());
        assertThat(entityManager.find(EstudianteArtefactoJpaEntity.class, primero.id())).isNotNull();
        verify(logger).debug(EstudianteArtefactoKey.LOG_GUARDADOS, 2);
    }

    @Test
    void debeObtenerSoloLasParejasPedidas_cuandoHayOtrosVinculos() {
        // Arrange
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository, mock(AppLogger.class));
        var artefacto = UtilUUID.generarNuevoUUID();
        var otroArtefacto = UtilUUID.generarNuevoUUID();
        var pedido = UtilUUID.generarNuevoUUID();
        var noPedido = UtilUUID.generarNuevoUUID();
        sembrar(artefacto, pedido);
        sembrar(artefacto, noPedido);
        sembrar(otroArtefacto, pedido);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerVinculados(List.of(artefacto), List.of(pedido, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).singleElement().satisfies(vinculo -> {
            assertThat(vinculo.artefacto()).isEqualTo(artefacto);
            assertThat(vinculo.estudiante()).isEqualTo(pedido);
        });
    }

    @Test
    void debeBorrarSoloLosVinculosDelEstudianteEnArtefactosDelProyecto_cuandoSeDesvincula() {
        // Arrange
        var logger = mock(AppLogger.class);
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository, logger);
        var proyecto = UtilUUID.generarNuevoUUID();
        var primerArtefacto = sembrarArtefacto(proyecto);
        var segundoArtefacto = sembrarArtefacto(proyecto);
        var artefactoAjeno = sembrarArtefacto(UtilUUID.generarNuevoUUID());
        var desvinculado = UtilUUID.generarNuevoUUID();
        var otroEstudiante = UtilUUID.generarNuevoUUID();
        sembrar(primerArtefacto, desvinculado);
        sembrar(segundoArtefacto, desvinculado);
        sembrar(artefactoAjeno, desvinculado);
        sembrar(primerArtefacto, otroEstudiante);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.eliminarPorProyectoGradoYEstudiante(proyecto, desvinculado);
        entityManager.flush();
        entityManager.clear();

        // Assert
        var restantes = adapter.obtenerVinculados(
                List.of(primerArtefacto, segundoArtefacto, artefactoAjeno), List.of(desvinculado, otroEstudiante));
        assertThat(restantes).extracting(EstudianteArtefactoEntity::artefacto, EstudianteArtefactoEntity::estudiante)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple(artefactoAjeno, desvinculado),
                        org.assertj.core.groups.Tuple.tuple(primerArtefacto, otroEstudiante));
        verify(logger).debug(EstudianteArtefactoKey.LOG_DESVINCULADOS, proyecto, desvinculado);
    }

    private UUID sembrarArtefacto(UUID proyecto) {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(ArtefactoJpaEntity.builder()
                .id(id)
                .proyectoGrado(proyecto)
                .versionRepositorioArtefacto(UtilUUID.generarNuevoUUID())
                .asesor(UtilUUID.generarNuevoUUID())
                .build());
        return id;
    }

    private void sembrar(UUID artefacto, UUID estudiante) {
        entityManager.persist(EstudianteArtefactoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID()).artefacto(artefacto).estudiante(estudiante).build());
    }
}
