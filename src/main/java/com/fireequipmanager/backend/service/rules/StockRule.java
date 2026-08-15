package com.fireequipmanager.backend.service.rules;

import com.fireequipmanager.backend.service.contracts.StockDTO;
import com.fireequipmanager.backend.service.contracts.StockEntity;

public final class StockRule {

    private StockRule() {
    }

    // Completa indicadores de stock
    public static void completarIndicadoresDeStock(
            StockDTO dto,
            StockEntity entity) {

        if (dto == null || entity == null) {
            return;
        }

        Integer stock = entity.getStock() == null
                ? 0
                : entity.getStock();

        Integer minimo = entity.getStockMinimo() == null
                ? 0
                : entity.getStockMinimo();

        dto.setAlertaStock(alerta(stock, minimo));
        dto.setSinStock(sinStock(stock));
        dto.setPorcentajeStock(porcentaje(stock, minimo));
    }

    // Existe alerta de stock
    public static boolean alerta(
            Integer stock,
            Integer minimo) {

        return stock <= minimo;
    }

    // Sin stock
    public static boolean sinStock(Integer stock) {

        return stock == 0;
    }

    // Porcentaje disponible
    public static int porcentaje(
            Integer stock,
            Integer minimo) {

        if (minimo <= 0) {
            return 100;
        }

        return (stock * 100) / minimo;
    }

}