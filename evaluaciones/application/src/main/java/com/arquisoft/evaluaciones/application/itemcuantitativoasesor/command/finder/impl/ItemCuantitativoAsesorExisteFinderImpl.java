package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ItemCuantitativoAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.ItemCuantitativoAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCuantitativoAsesorExisteFinderImpl implements ItemCuantitativoAsesorExisteFinder {

    private final ItemCuantitativoAsesorOutputPort itemCuantitativoAsesorOutputPort;

    @Override
    public Boolean obtener(UUID itemCuantitativoAsesor) {
        return itemCuantitativoAsesorOutputPort.existePorId(itemCuantitativoAsesor);
    }
}
