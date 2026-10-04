package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.impl;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ItemCuantitativoJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.ItemCuantitativoJuradoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ItemCuantitativoJuradoExisteFinderImpl implements ItemCuantitativoJuradoExisteFinder {

    private final ItemCuantitativoJuradoOutputPort itemCuantitativoJuradoOutputPort;

    @Override
    public Boolean obtener(UUID itemCuantitativoJurado) {
        return itemCuantitativoJuradoOutputPort.existePorId(itemCuantitativoJurado);
    }
}
