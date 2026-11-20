package com.arquisoft.entregables.application.estudianteproyectogrado.command.finder;

import com.arquisoft.entregables.domain.estudianteproyectogrado.model.ContactoEstudiante;
import com.arquisoft.shared.finder.Finder;

import java.util.List;
import java.util.UUID;

public interface ContactosEstudiantesEntregableFinder extends Finder<UUID, List<ContactoEstudiante>> {
}
