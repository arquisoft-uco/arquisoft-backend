package com.arquisoft.usuarios.application.representantecomitecurriculum.command.usecase.impl;

import com.arquisoft.shared.events.EventPublisher;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.fichas.RepresentanteComiteKey;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.finder.RepresentanteComiteCurriculumExisteFinder;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.finder.UsuarioExisteFinder;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.RepresentanteComiteCurriculumOutputPort;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.secondaryport.mapper.RepresentanteComiteCurriculumMapper;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.usecase.AgregarRepresentanteComiteCurriculumUseCase;
import com.arquisoft.usuarios.application.representantecomitecurriculum.command.validator.AgregarRepresentanteComiteCurriculumValidator;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.RepresentanteComiteCurriculumDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarRepresentanteComiteCurriculumUseCaseImpl implements AgregarRepresentanteComiteCurriculumUseCase {

    private final AgregarRepresentanteComiteCurriculumValidator agregarRepresentanteComiteCurriculumValidatorvalidator;
    private final UsuarioExisteFinder usuarioExisteFinder;
    private final RepresentanteComiteCurriculumExisteFinder representanteComiteExisteFinder;
    private final RepresentanteComiteCurriculumOutputPort representanteComiteCurriculumOutputPortoutputPort;
    private final EventPublisher eventPublisher;

    @Override
    public UUID ejecutar(RepresentanteComiteCurriculumDomain representante) {
        var usuarioExiste = usuarioExisteFinder.obtener(representante.getUsuario());
        var yaEsRepresentante = representanteComiteExisteFinder.obtener(representante.getUsuario());

        agregarRepresentanteComiteCurriculumValidatorvalidator.validar(representante.getUsuario(), usuarioExiste, yaEsRepresentante);

        var entity = RepresentanteComiteCurriculumMapper.toEntity(representante);

        representanteComiteCurriculumOutputPortoutputPort.agregarRepresentante(entity);


        representante.extraerEventosSinPublicar().forEach(eventPublisher::publish);

        return representante.getUsuario();
    }
}
