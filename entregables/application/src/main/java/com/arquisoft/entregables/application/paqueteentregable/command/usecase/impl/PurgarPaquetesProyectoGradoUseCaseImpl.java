package com.arquisoft.entregables.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaquetesProyectoGradoFinder;
import com.arquisoft.entregables.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.entregables.application.paqueteentregable.command.result.mapper.PurgaPaquetesProyectoGradoResultMapper;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.ArmadoPaqueteOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.usecase.PurgarPaquetesProyectoGradoUseCase;
import com.arquisoft.entregables.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurgarPaquetesProyectoGradoUseCaseImpl implements PurgarPaquetesProyectoGradoUseCase {

    private final PaquetesProyectoGradoFinder paquetesProyectoGradoFinder;
    private final ArmadoPaqueteOutputPort armadoPaqueteOutputPort;

    @Override
    public PurgaPaquetesProyectoGradoResult ejecutar(PurgaPaquetesProyectoGradoDomain purga) {
        var paquetes = paquetesProyectoGradoFinder.obtener(purga.getProyectoGrado());
        paquetes.forEach(paquete -> armadoPaqueteOutputPort.eliminar(paquete.getBucket(), paquete.getClaveObjeto()));
        return PurgaPaquetesProyectoGradoResultMapper.toResult(purga, paquetes.size());
    }
}
