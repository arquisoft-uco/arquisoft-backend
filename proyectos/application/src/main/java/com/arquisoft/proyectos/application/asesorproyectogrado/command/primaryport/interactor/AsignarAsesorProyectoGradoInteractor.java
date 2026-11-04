package com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.interactor;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.AsignarAsesorProyectoGradoCommand;
import com.arquisoft.shared.interactor.Interactor;

import java.util.UUID;

public interface AsignarAsesorProyectoGradoInteractor
        extends Interactor<AsignarAsesorProyectoGradoCommand, UUID> {}
