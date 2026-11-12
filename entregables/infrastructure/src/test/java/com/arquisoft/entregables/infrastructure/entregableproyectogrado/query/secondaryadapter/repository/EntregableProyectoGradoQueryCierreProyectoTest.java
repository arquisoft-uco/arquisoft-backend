package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.mapper.ConsultarEntregablesProyectoGradoAsesorMapper;
import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoAsesorQuery;
import com.arquisoft.entregables.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.entity.EstadoEntregableProyectoGradoJpaEntity;
import com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.exception.FiltroInvalidoException;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class EntregableProyectoGradoQueryCierreProyectoTest {

    private static final Instant FECHA_BASE = Instant.parse("2026-11-10T10:00:00Z");

    @Autowired
    private EntregableProyectoGradoQueryRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private EntregableProyectoGradoQueryOutputAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new EntregableProyectoGradoQueryOutputAdapter(repository, new EntregableProyectoGradoJpaSpecification());
        var em = entityManager.getEntityManager();
        em.createNativeQuery("CREATE TABLE IF NOT EXISTS estado_entregable (id VARCHAR(60) PRIMARY KEY, "
                + "nombre VARCHAR(60), descripcion VARCHAR(300))").executeUpdate();
        em.createNativeQuery("DELETE FROM estado_entregable").executeUpdate();
        sembrarEstado("APROBADO", "Aprobado");
        sembrarEstado("CERRADO", "Cerrado");
        sembrarEstado("CANCELADO", "Cancelado");
    }

    private void sembrarEstado(String id, String nombre) {
        entityManager.getEntityManager()
                .createNativeQuery("INSERT INTO estado_entregable (id, nombre, descripcion) VALUES (?1, ?2, 'd')")
                .setParameter(1, id).setParameter(2, nombre)
                .executeUpdate();
    }

    private UUID sembrarAsesor() {
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(AsesorJpaEntity.builder()
                .id(id).identificador("ID-Ana").nombre("Ana").email("ana@uco.edu.co")
                .ocurridoEn(Instant.parse("2026-01-01T00:00:00Z")).build());
        return id;
    }

    private UUID sembrarEntregable(String estadoProyecto, UUID asesor, String estadoEntregable) {
        var proyecto = UtilUUID.generarNuevoUUID();
        entityManager.persist(ProyectoGradoJpaEntity.builder()
                .id(proyecto).nombre("Proyecto " + estadoProyecto).estadoProyectoId(estadoProyecto).build());
        var id = UtilUUID.generarNuevoUUID();
        entityManager.persist(new EntregableProyectoGradoJpaEntity(id, proyecto, 1, asesor));
        entityManager.persist(new EstadoEntregableProyectoGradoJpaEntity(
                UtilUUID.generarNuevoUUID(), id, estadoEntregable, FECHA_BASE));
        return id;
    }

    private void confirmar() {
        entityManager.flush();
        entityManager.clear();
    }

    private static EntregableProyectoGradoCriteria criteriaDelAsesor(UUID asesor, NodoFiltro raizCliente) {
        return ConsultarEntregablesProyectoGradoAsesorMapper.toCriteria(
                ConsultarEntregablesProyectoGradoAsesorQuery.crear(
                        asesor, ConsultaCriteriaQuery.crear(0, 10, List.of(), raizCliente)));
    }

    @Test
    void debeMarcarProyectoFinalizadoYCancelado_segunElEstadoDelProyecto() {
        // Arrange
        var asesor = sembrarAsesor();
        var enProceso = sembrarEntregable("EN_PROCESO", asesor, "APROBADO");
        var atrasado = sembrarEntregable("ATRASADO", asesor, "APROBADO");
        var finalizado = sembrarEntregable("FINALIZADO", asesor, "CERRADO");
        var cancelado = sembrarEntregable("CANCELADO", asesor, "CANCELADO");
        confirmar();
        var criteria = EntregableProyectoGradoCriteria.builder().pagina(0).tamanio(10).build();

        // Act
        var resultado = adapter.consultar(criteria);

        // Assert
        var porId = resultado.getContent().stream().collect(Collectors.toMap(e -> e.id(), e -> e));
        assertThat(porId).hasSize(4);
        assertThat(porId.get(enProceso).proyectoFinalizado()).isFalse();
        assertThat(porId.get(enProceso).proyectoCancelado()).isFalse();
        assertThat(porId.get(atrasado).proyectoFinalizado()).isFalse();
        assertThat(porId.get(atrasado).proyectoCancelado()).isFalse();
        assertThat(porId.get(finalizado).proyectoFinalizado()).isTrue();
        assertThat(porId.get(finalizado).proyectoCancelado()).isFalse();
        assertThat(porId.get(cancelado).proyectoFinalizado()).isFalse();
        assertThat(porId.get(cancelado).proyectoCancelado()).isTrue();
    }

    @Test
    void debeOcultarFinalizadosYCancelados_porDefecto() {
        // Arrange
        var asesor = sembrarAsesor();
        var enProceso = sembrarEntregable("EN_PROCESO", asesor, "APROBADO");
        var atrasado = sembrarEntregable("ATRASADO", asesor, "APROBADO");
        sembrarEntregable("FINALIZADO", asesor, "CERRADO");
        sembrarEntregable("CANCELADO", asesor, "CANCELADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteriaDelAsesor(asesor, null));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactlyInAnyOrder(enProceso, atrasado);
    }

    @Test
    void debeFiltrarSoloFinalizados_cuandoProyectoFinalizadoEsTrue() {
        // Arrange
        var asesor = sembrarAsesor();
        sembrarEntregable("EN_PROCESO", asesor, "APROBADO");
        var finalizado = sembrarEntregable("FINALIZADO", asesor, "CERRADO");
        sembrarEntregable("CANCELADO", asesor, "CANCELADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteriaDelAsesor(asesor,
                NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.ES, "true")));

        // Assert
        assertThat(resultado.getContent()).extracting("id").containsExactly(finalizado);
    }

    @Test
    void debeNoRetornarCancelados_cuandoElClienteFiltraProyectoCanceladoEnTrue() {
        // Arrange
        var asesor = sembrarAsesor();
        sembrarEntregable("EN_PROCESO", asesor, "APROBADO");
        sembrarEntregable("CANCELADO", asesor, "CANCELADO");
        confirmar();

        // Act
        var resultado = adapter.consultar(criteriaDelAsesor(asesor,
                NodoFiltro.predicado("proyectoCancelado", FiltroOperador.ES, "true")));

        // Assert
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoOperadorNoAplicaAProyectoFinalizado() {
        // Arrange
        var criteriaInvalido = EntregableProyectoGradoCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.CONTIENE, "true")).build();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(criteriaInvalido))
                .isInstanceOf(FiltroInvalidoException.class);
    }

    @Test
    void debeLanzarFiltroInvalidoException_cuandoValorNoEsBooleano() {
        // Arrange
        var criteriaInvalido = EntregableProyectoGradoCriteria.builder().pagina(0).tamanio(10)
                .raiz(NodoFiltro.predicado("proyectoFinalizado", FiltroOperador.ES, "quizas")).build();

        // Act & Assert
        assertThatThrownBy(() -> adapter.consultar(criteriaInvalido))
                .isInstanceOf(FiltroInvalidoException.class);
    }
}
