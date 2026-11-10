package com.arquisoft.entregables.infrastructure.paqueteentregable.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.paqueteentregable.query.primaryport.model.RolConsultaPaqueteEntregable;
import com.arquisoft.shared.message.constant.UsuariosRoles;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.web.identidad.IdentidadActual;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DescargarPaqueteEntregableRequestMapperTest {

    @Test
    void debeTraducirRoles_cuandoAsesorYEstudiante() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();
        var identidad = new IdentidadActual(actor, "a@uco.edu.co", "a",
                List.of(UsuariosRoles.ASESOR, UsuariosRoles.ESTUDIANTE, "otro"), null, null);

        // Act
        var query = DescargarPaqueteEntregableRequestMapper.toQuery(entregable, identidad);

        // Assert
        assertThat(query.entregableProyectoGrado()).isEqualTo(entregable);
        assertThat(query.actor()).isEqualTo(actor);
        assertThat(query.roles()).containsExactlyInAnyOrder(
                RolConsultaPaqueteEntregable.ASESOR, RolConsultaPaqueteEntregable.ESTUDIANTE);
    }
}
