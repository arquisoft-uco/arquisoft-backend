package com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.interactor.impl;

import com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.interactor.AgregarRepresentanteComiteCurriculumInteractor;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.mapper.AgregarRepresentanteComiteCurriculumMapper;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.model.AgregarRepresentanteComiteCurriculumCommand;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.usecase.AgregarRepresentanteComiteCurriculumUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarRepresentanteComiteCurriculumInteractorImpl implements AgregarRepresentanteComiteCurriculumInteractor {

    private final AgregarRepresentanteComiteCurriculumUseCase useCase;

    @Override
    @Transactional(transactionManager = "usuariosTransactionManager")
    public UUID ejecutar(AgregarRepresentanteComiteCurriculumCommand command) {
        return useCase.ejecutar(AgregarRepresentanteComiteCurriculumMapper.toDomain(command));
    }
}
