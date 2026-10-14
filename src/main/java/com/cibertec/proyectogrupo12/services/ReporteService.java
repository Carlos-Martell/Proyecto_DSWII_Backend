package com.cibertec.proyectogrupo12.services;

import java.time.LocalDate;
import java.time.YearMonth;


public interface ReporteService {

    byte[] generarReporteMovimientosDiarios(LocalDate fecha);

    byte[] generarReporteBancos(LocalDate fecha);

    byte[] generarReporteTotales(YearMonth mes);

    byte[] generarReporteMensual(YearMonth mes);

    byte[] generarReporteSocios(YearMonth mes);

    byte[] generarReporteNoSocios(YearMonth mes);

    byte[] generarReporteEgresos(YearMonth mes);
}
