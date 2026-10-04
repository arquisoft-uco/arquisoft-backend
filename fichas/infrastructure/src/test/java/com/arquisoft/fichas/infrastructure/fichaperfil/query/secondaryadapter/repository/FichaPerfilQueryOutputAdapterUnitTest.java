package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.shared.query.FiltroConector;
import com.arquisoft.shared.query.FiltroOperador;
import com.arquisoft.shared.query.NodoFiltro;
import com.arquisoft.shared.query.SortOrder;
import com.arquisoft.shared.query.pagination.SortDirection;
import com.arquisoft.shared.util.UtilUUID;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FichaPerfilQueryOutputAdapterUnitTest {

    @Mock
    private FichaPerfilQueryRepository repository;

    @Mock
    private Root<FichaPerfilJpaQueryEntity> root;

    @Mock
    private CriteriaQuery<?> query;

    @Mock
    private CriteriaBuilder cb;

    @Mock
    private Path<String> rutaEstado;

    @Captor
    private ArgumentCaptor<Specification<FichaPerfilJpaQueryEntity>> specCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableCaptor;

    private FichaPerfilQueryOutputAdapter adapter() {
        return new FichaPerfilQueryOutputAdapter(repository, new FichaPerfilJpaSpecification());
    }

    private FichaPerfilJpaQueryEntity entidad(String titulo, String estadoId, String estadoNombre, Instant fecha) {
        return FichaPerfilJpaQueryEntity.builder()
                .id(UtilUUID.generarNuevoUUID())
                .tituloProyecto(titulo)
                .asesorId(UtilUUID.generarNuevoUUID())
                .asesorIdentificador("A-1")
                .asesorNombre("Ana")
                .asesorEmail("ana@uco.edu.co")
                .estadoId(estadoId)
                .estadoNombre(estadoNombre)
                .estadoFechaActualizacion(fecha)
                .build();
    }

    @Test
    void debeMapearCadaEntidadYArmarPaginado_cuandoHayResultados() {
        // Arrange
        var fecha = Instant.parse("2026-01-01T10:00:00Z");
        var entidades = List.of(
                entidad("T1", "EN_CONSTRUCCION", "En construccion", fecha),
                entidad("T2", "APROBADA", "Aprobada", fecha.plusSeconds(60)),
                entidad("T3", "RECHAZADA", "Rechazada", fecha.plusSeconds(120)));
        var criteria = FichaPerfilCriteria.builder().pagina(1).tamanio(3).build();
        when(repository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(entidades, PageRequest.of(1, 3), 7));

        // Act
        var resultado = adapter().consultarTodas(criteria);

        // Assert
        assertThat(resultado.getContent()).hasSize(3);
        for (var i = 0; i < entidades.size(); i++) {
            var origen = entidades.get(i);
            var modelo = resultado.getContent().get(i);
            assertThat(modelo.id()).isEqualTo(origen.getId());
            assertThat(modelo.tituloProyecto()).isEqualTo(origen.getTituloProyecto());
            assertThat(modelo.asesorFicha().id()).isEqualTo(origen.getAsesorId());
            assertThat(modelo.asesorFicha().email()).isEqualTo("ana@uco.edu.co");
            assertThat(modelo.estado().id()).isEqualTo(origen.getEstadoId());
            assertThat(modelo.estado().nombre()).isEqualTo(origen.getEstadoNombre());
            assertThat(modelo.estado().fechaActualizacion()).isEqualTo(origen.getEstadoFechaActualizacion());
        }
        assertThat(resultado.getPage()).isEqualTo(1);
        assertThat(resultado.getSize()).isEqualTo(3);
        assertThat(resultado.getTotalElements()).isEqualTo(7);
        assertThat(resultado.getTotalPages()).isEqualTo(3);
    }

    @Test
    void debeTraducirOrdenYPaginacion_cuandoElCriteriaTraeOrdenamiento() {
        // Arrange
        var criteria = FichaPerfilCriteria.builder()
                .pagina(2).tamanio(5)
                .ordenamiento(List.of(
                        SortOrder.of("asesorNombre", SortDirection.DESC),
                        SortOrder.of("tituloProyecto", SortDirection.ASC)))
                .build();
        Page<FichaPerfilJpaQueryEntity> vacia = Page.empty();
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(vacia);

        // Act
        var resultado = adapter().consultarTodas(criteria);

        // Assert
        verify(repository).findAll(specCaptor.capture(), pageableCaptor.capture());
        var pageable = pageableCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().toList()).containsExactly(
                Sort.Order.desc("asesorNombre"), Sort.Order.asc("tituloProyecto"));
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void debePedirPaginaSinOrden_cuandoElCriteriaNoTraeOrdenamiento() {
        // Arrange
        var criteria = FichaPerfilCriteria.builder().pagina(0).tamanio(10).build();
        Page<FichaPerfilJpaQueryEntity> vacia = Page.empty();
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(vacia);

        // Act
        adapter().consultarTodas(criteria);

        // Assert
        verify(repository).findAll(specCaptor.capture(), pageableCaptor.capture());
        assertThat(pageableCaptor.getValue().getSort().isUnsorted()).isTrue();
        assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(10);
    }

    @ParameterizedTest
    @CsvSource({
            "tituloProyecto,CONTIENE,abc,tituloProyecto",
            "asesorNombre,EMPIEZA_CON,ana,asesorNombre",
            "asesorEmail,TERMINA_CON,@uco.edu.co,asesorEmail",
            "estadoFicha,ES,APROBADA,estadoId",
            "asesorId,ES,6f1c2f6e-5a54-4d5f-9d2f-0f3c3a1f9b11,asesorId"
    })
    void debeFiltrarPorAtributoDeLaVista_cuandoElCriteriaTraeUnPredicado(
            String campo, FiltroOperador operador, String valor, String atributo) {
        // Arrange
        var criteria = FichaPerfilCriteria.builder()
                .raiz(NodoFiltro.predicado(campo, operador, valor)).build();
        Page<FichaPerfilJpaQueryEntity> vacia = Page.empty();
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(vacia);

        // Act
        adapter().consultarTodas(criteria);
        verify(repository).findAll(specCaptor.capture(), any(Pageable.class));
        specCaptor.getValue().toPredicate(root, query, cb);

        // Assert
        verify(root).get(atributo);
    }

    @Test
    void debeFiltrarPorEstadoYTitulo_cuandoElFiltroEsUnGrupoConMultivalor() {
        // Arrange
        var criteria = FichaPerfilCriteria.builder()
                .raiz(NodoFiltro.grupo(FiltroConector.AND, List.of(
                        NodoFiltro.predicadoMultivalor("estadoFicha", FiltroOperador.IN,
                                List.of("EN_CONSTRUCCION", "APROBADA")),
                        NodoFiltro.predicado("tituloProyecto", FiltroOperador.CONTIENE, "x"))))
                .build();
        Page<FichaPerfilJpaQueryEntity> vacia = Page.empty();
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(vacia);
        when(root.<String>get("estadoId")).thenReturn(rutaEstado);

        // Act
        adapter().consultarTodas(criteria);
        verify(repository).findAll(specCaptor.capture(), any(Pageable.class));
        specCaptor.getValue().toPredicate(root, query, cb);

        // Assert
        verify(rutaEstado).in(List.of("EN_CONSTRUCCION", "APROBADA"));
        verify(root).get("tituloProyecto");
    }

    @Test
    void debeDelegarExistencia_cuandoSePreguntaPorId() {
        // Arrange
        var existente = UtilUUID.generarNuevoUUID();
        var ausente = UtilUUID.generarNuevoUUID();
        when(repository.existsById(existente)).thenReturn(true);
        when(repository.existsById(ausente)).thenReturn(false);

        // Act
        var existe = adapter().existePorId(existente);
        var noExiste = adapter().existePorId(ausente);

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
