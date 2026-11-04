package com.arquisoft.mapas_ruta.domain.proyectogrado;

import com.arquisoft.mapas_ruta.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoGradoDomainTest {

    @Test
    void debeReconstruirConTodosLosCampos_cuandoVienenDeLaBase() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var coordinador = UtilUUID.generarNuevoUUID();
        var fichaPerfil = UtilUUID.generarNuevoUUID();

        // Act
        var proyecto = ProyectoGradoDomain.reconstruir(
                id, EstadoProyectoGrado.EN_PROCESO, coordinador, fichaPerfil, "Titulo");

        // Assert
        assertThat(proyecto.getId()).isEqualTo(id);
        assertThat(proyecto.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.EN_PROCESO);
        assertThat(proyecto.getCoordinador()).isEqualTo(coordinador);
        assertThat(proyecto.getFichaPerfil()).isEqualTo(fichaPerfil);
        assertThat(proyecto.getTituloProyecto()).isEqualTo("Titulo");
        assertThat(proyecto.esVacio()).isFalse();
    }

    @Test
    void debeIdentificarElCentinela_cuandoSeConsultaEsVacio() {
        // Act & Assert
        assertThat(ProyectoGradoDomain.VACIO.esVacio()).isTrue();
        assertThat(ProyectoGradoDomain.VACIO.getEstadoProyectoGrado()).isEqualTo(EstadoProyectoGrado.VACIO);
        assertThat(UtilUUID.esPorDefecto(ProyectoGradoDomain.VACIO.getId())).isTrue();
    }
}
