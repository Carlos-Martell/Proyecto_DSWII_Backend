package com.cibertec.proyectogrupo12.controllers;

import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.services.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;


@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ReporteService reporteService;

    @GetMapping("/movimientos-diarios")
    public ResponseEntity<byte[]> movimientosDiarios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return archivo(reporteService.generarReporteMovimientosDiarios(fecha),
                "movimientos-diarios-" + fecha + ".xlsx");
    }

    @GetMapping("/bancos")
    public ResponseEntity<byte[]> bancos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return archivo(reporteService.generarReporteBancos(fecha), "bancos-" + fecha + ".xlsx");
    }

    @GetMapping("/totales")
    public ResponseEntity<byte[]> totales(@RequestParam String mes) {
        YearMonth periodo = parsearMes(mes);
        return archivo(reporteService.generarReporteTotales(periodo), "totales-" + periodo + ".xlsx");
    }

    @GetMapping("/mensual")
    public ResponseEntity<byte[]> mensual(@RequestParam String mes) {
        YearMonth periodo = parsearMes(mes);
        return archivo(reporteService.generarReporteMensual(periodo), "mensual-" + periodo + ".xlsx");
    }

    @GetMapping("/socios")
    public ResponseEntity<byte[]> socios(@RequestParam String mes) {
        YearMonth periodo = parsearMes(mes);
        return archivo(reporteService.generarReporteSocios(periodo), "socios-" + periodo + ".xlsx");
    }

    @GetMapping("/no-socios")
    public ResponseEntity<byte[]> noSocios(@RequestParam String mes) {
        YearMonth periodo = parsearMes(mes);
        return archivo(reporteService.generarReporteNoSocios(periodo), "no-socios-" + periodo + ".xlsx");
    }

    @GetMapping("/egresos")
    public ResponseEntity<byte[]> egresos(@RequestParam String mes) {
        YearMonth periodo = parsearMes(mes);
        return archivo(reporteService.generarReporteEgresos(periodo), "egresos-" + periodo + ".xlsx");
    }

    private YearMonth parsearMes(String mes) {
        try {
            return YearMonth.parse(mes);
        } catch (DateTimeParseException e) {
            throw new BusinessException("El mes debe tener el formato yyyy-MM, por ejemplo 2026-08");
        }
    }

    private ResponseEntity<byte[]> archivo(byte[] contenido, String nombreArchivo) {
        return ResponseEntity.ok()
                .contentType(XLSX)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(nombreArchivo).build().toString())
                .body(contenido);
    }
}
