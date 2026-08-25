package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.interactor;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RegistrarItemCualitativoJuradoCommand;

import java.util.UUID;

public interface RegistrarItemCualitativoJuradoInteractor {

    UUID ejecutar(RegistrarItemCualitativoJuradoCommand command);
}
