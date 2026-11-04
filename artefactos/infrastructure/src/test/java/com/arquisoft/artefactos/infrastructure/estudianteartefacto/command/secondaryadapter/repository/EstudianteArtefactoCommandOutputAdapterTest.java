package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.entity.EstudianteArtefactoEntity;
import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository.EstudianteProyectoGradoCommandRepository;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
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
    private EstudianteProyectoGradoCommandRepository estudianteProyectoGradoRepository;

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
        var resultado = adapter.obtenerVinculados(artefacto, List.of(pedido, UtilUUID.generarNuevoUUID()));

        // Assert
        assertThat(resultado).singleElement().satisfies(vinculo -> {
            assertThat(vinculo.artefacto()).isEqualTo(artefacto);
            assertThat(vinculo.estudiante()).isEqualTo(pedido);
        });
    }

    @Test
    void debeEliminarSoloElVinculoIndicado_yConservarLosDemas() {
        // Arrange
        var logger = mock(AppLogger.class);
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository, logger);
        var artefacto = UtilUUID.generarNuevoUUID();
        var otroArtefacto = UtilUUID.generarNuevoUUID();
        var removido = UtilUUID.generarNuevoUUID();
        var otroEstudiante = UtilUUID.generarNuevoUUID();
        sembrar(artefacto, removido);
        sembrar(artefacto, otroEstudiante);
        sembrar(otroArtefacto, removido);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.eliminarPorArtefactoYEstudiante(artefacto, removido);
        entityManager.flush();
        entityManager.clear();

        // Assert
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, removido)).isFalse();
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, otroEstudiante)).isTrue();
        assertThat(adapter.existePorArtefactoYEstudiante(otroArtefacto, removido)).isTrue();
        verify(logger).debug(EstudianteArtefactoKey.LOG_VINCULO_ELIMINADO, artefacto, removido);
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
        var restantes = new java.util.ArrayList<EstudianteArtefactoEntity>();
        List.of(primerArtefacto, segundoArtefacto, artefactoAjeno).forEach(artefacto ->
                restantes.addAll(adapter.obtenerVinculados(artefacto, List.of(desvinculado, otroEstudiante))));
        assertThat(restantes).extracting(EstudianteArtefactoEntity::artefacto, EstudianteArtefactoEntity::estudiante)
                .containsExactlyInAnyOrder(
                        org.assertj.core.groups.Tuple.tuple(artefactoAjeno, desvinculado),
                        org.assertj.core.groups.Tuple.tuple(primerArtefacto, otroEstudiante));
        verify(logger).debug(EstudianteArtefactoKey.LOG_DESVINCULADOS, proyecto, desvinculado);
    }

    @Test
    void debeConservarLaDestitucionPendiente_cuandoSeDesvinculaEnLaMismaTransaccion() {
        // Arrange
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository, mock(AppLogger.class));
        var proyecto = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        var vinculo = EstudianteProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estudianteId(estudiante)
                .proyectoGradoId(proyecto)
                .ocurridoEn(Instant.parse("2026-10-28T22:45:51Z"))
                .build();
        entityManager.persist(vinculo);
        entityManager.flush();
        entityManager.clear();
        var destituidoEn = Instant.parse("2026-10-28T23:20:26Z");
        estudianteProyectoGradoRepository.save(EstudianteProyectoGradoJpaEntity.builder()
                .id(vinculo.getId())
                .estudianteId(estudiante)
                .proyectoGradoId(proyecto)
                .ocurridoEn(destituidoEn)
                .destituidoEn(destituidoEn)
                .build());

        // Act
        adapter.eliminarPorProyectoGradoYEstudiante(proyecto, estudiante);
        entityManager.clear();

        // Assert
        var persistido = entityManager.find(EstudianteProyectoGradoJpaEntity.class, vinculo.getId());
        assertThat(persistido.getDestituidoEn()).isEqualTo(destituidoEn);
        assertThat(persistido.getOcurridoEn()).isEqualTo(destituidoEn);
    }

    @Test
    void debeConservarSoloLosEnviadosVigentes_cuandoSeEliminaElResto() {
        // Arrange
        var logger = mock(AppLogger.class);
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository, logger);
        var proyecto = UtilUUID.generarNuevoUUID();
        var artefacto = sembrarArtefacto(proyecto);
        var otroArtefacto = sembrarArtefacto(proyecto);
        var enviadoVigente = UtilUUID.generarNuevoUUID();
        var noEnviadoVigente = UtilUUID.generarNuevoUUID();
        var enviadoDestituido = UtilUUID.generarNuevoUUID();
        var enviadoSinPertenencia = UtilUUID.generarNuevoUUID();
        sembrarPertenencia(enviadoVigente, proyecto, null);
        sembrarPertenencia(noEnviadoVigente, proyecto, null);
        sembrarPertenencia(enviadoDestituido, proyecto, UtilFecha.generarInstanteActual());
        sembrar(artefacto, enviadoVigente);
        sembrar(artefacto, noEnviadoVigente);
        sembrar(artefacto, enviadoDestituido);
        sembrar(artefacto, enviadoSinPertenencia);
        sembrar(otroArtefacto, noEnviadoVigente);
        entityManager.flush();
        entityManager.clear();

        // Act
        adapter.eliminarExceptoConservados(
                artefacto, List.of(enviadoVigente, enviadoDestituido, enviadoSinPertenencia));

        // Assert
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, enviadoVigente)).isTrue();
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, noEnviadoVigente)).isFalse();
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, enviadoDestituido)).isFalse();
        assertThat(adapter.existePorArtefactoYEstudiante(artefacto, enviadoSinPertenencia)).isFalse();
        assertThat(adapter.existePorArtefactoYEstudiante(otroArtefacto, noEnviadoVigente)).isTrue();
        verify(logger).debug(EstudianteArtefactoKey.LOG_VINCULOS_DEPURADOS, artefacto);
    }


    @Test
    void debeDevolverLosVigentesVinculados_cuandoHayBajasYOtrosArtefactos() {
        // Arrange
        var adapter = new EstudianteArtefactoCommandOutputAdapter(repository, mock(AppLogger.class));
        var proyecto = UtilUUID.generarNuevoUUID();
        var artefacto = sembrarArtefacto(proyecto);
        var otroArtefacto = sembrarArtefacto(proyecto);
        var vigente = UtilUUID.generarNuevoUUID();
        var destituido = UtilUUID.generarNuevoUUID();
        var sinPertenencia = UtilUUID.generarNuevoUUID();
        var deOtroArtefacto = UtilUUID.generarNuevoUUID();
        sembrarPertenencia(vigente, proyecto, null);
        sembrarPertenencia(destituido, proyecto, UtilFecha.generarInstanteActual());
        sembrarPertenencia(deOtroArtefacto, proyecto, null);
        sembrar(artefacto, vigente);
        sembrar(artefacto, destituido);
        sembrar(artefacto, sinPertenencia);
        sembrar(otroArtefacto, deOtroArtefacto);
        entityManager.flush();
        entityManager.clear();

        // Act
        var resultado = adapter.obtenerVigentesVinculados(artefacto);

        // Assert
        assertThat(resultado).containsExactly(vigente);
        assertThat(adapter.obtenerVigentesVinculados(UtilUUID.generarNuevoUUID())).isEmpty();
    }

    private void sembrarPertenencia(UUID estudiante, UUID proyecto, Instant destituidoEn) {
        entityManager.persist(EstudianteProyectoGradoJpaEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .estudianteId(estudiante)
                .proyectoGradoId(proyecto)
                .ocurridoEn(Instant.now())
                .destituidoEn(destituidoEn)
                .build());
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
