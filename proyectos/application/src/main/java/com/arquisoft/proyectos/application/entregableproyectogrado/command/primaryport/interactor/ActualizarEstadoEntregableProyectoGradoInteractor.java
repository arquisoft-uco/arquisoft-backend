package com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.interactor;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.model.ActualizarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.result.ActualizacionEstadoEntregableProyectoGradoResult;
import com.arquisoft.shared.interactor.Interactor;

public interface ActualizarEstadoEntregableProyectoGradoInteractor extends
        Interactor<ActualizarEstadoEntregableProyectoGradoCommand,
                ActualizacionEstadoEntregableProyectoGradoResult> {
}
