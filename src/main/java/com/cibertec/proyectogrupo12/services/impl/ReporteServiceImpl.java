package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.CuentaPorCobrar;
import com.cibertec.proyectogrupo12.entities.Egreso;
import com.cibertec.proyectogrupo12.entities.Recibo;
import com.cibertec.proyectogrupo12.entities.enums.TipoRecibo;
import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.repositories.CuentaPorCobrarRepository;
import com.cibertec.proyectogrupo12.repositories.EgresoRepository;
import com.cibertec.proyectogrupo12.repositories.ReciboRepository;
import com.cibertec.proyectogrupo12.services.ReporteService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReporteServiceImpl implements ReporteService {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ReciboRepository reciboRepository;
    private final EgresoRepository egresoRepository;
    private final CuentaPorCobrarRepository cuentaPorCobrarRepository;

    @Override
    public byte[] generarReporteMovimientosDiarios(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);

        List<Recibo> recibos = reciboRepository.findByFechaBetween(inicio, fin);
        List<Egreso> egresos = egresoRepository.findByFechaBetween(fecha, fecha);

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hojaRecibos = crearHoja(libro, "Ingresos y bancos",
                    "Correlativo", "Tipo", "Fecha", "Monto", "Cuenta", "Banco", "Usuario", "Detalle");
            int fila = 1;
            for (Recibo r : recibos) {
                escribirFila(hojaRecibos, fila++,
                        r.getCorrelativo(), r.getTipo().name(), r.getFecha().format(FORMATO_FECHA_HORA),
                        r.getMonto(), idOTexto(r.getCuenta() != null ? r.getCuenta().getId() : null),
                        r.getBanco() != null ? r.getBanco().getNombre() : "-",
                        nombreCompleto(r.getUsuario() != null ? r.getUsuario().getNombres() : null,
                                r.getUsuario() != null ? r.getUsuario().getApellidos() : null),
                        detalleRecibo(r));
            }

            Sheet hojaEgresos = crearHoja(libro, "Egresos",
                    "Numero documento", "Proveedor", "Fecha", "Importe", "Estado", "Motivo");
            fila = 1;
            for (Egreso e : egresos) {
                escribirFila(hojaEgresos, fila++,
                        e.getNumeroDocumento(), e.getProveedor(), e.getFecha().format(FORMATO_FECHA),
                        e.getImporte(), e.getEstado().name(), e.getMotivo());
            }
            return aBytes(libro);
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar el reporte: " + e.getMessage());
        }
    }

    @Override
    public byte[] generarReporteBancos(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        List<Recibo> recibos = reciboRepository.findByTipoAndFechaBetween(TipoRecibo.BANCO, inicio, fin);

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = crearHoja(libro, "Recibos bancarios",
                    "Correlativo", "Banco", "Cuenta", "Monto", "Fecha deposito", "Usuario");
            int fila = 1;
            for (Recibo r : recibos) {
                escribirFila(hoja, fila++,
                        r.getCorrelativo(),
                        r.getBanco() != null ? r.getBanco().getNombre() : "-",
                        idOTexto(r.getCuenta() != null ? r.getCuenta().getId() : null),
                        r.getMonto(),
                        r.getFechaDeposito() != null ? r.getFechaDeposito().format(FORMATO_FECHA) : "-",
                        nombreCompleto(r.getUsuario() != null ? r.getUsuario().getNombres() : null,
                                r.getUsuario() != null ? r.getUsuario().getApellidos() : null));
            }
            return aBytes(libro);
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar el reporte: " + e.getMessage());
        }
    }

    @Override
    public byte[] generarReporteTotales(YearMonth mes) {
        LocalDateTime inicio = mes.atDay(1).atStartOfDay();
        LocalDateTime fin = mes.atEndOfMonth().atTime(LocalTime.MAX);
        List<Recibo> recibos = reciboRepository.findByFechaBetween(inicio, fin);
        List<Egreso> egresos = egresoRepository.findByFechaBetween(mes.atDay(1), mes.atEndOfMonth());

        BigDecimal totalIngresos = sumar(recibos, TipoRecibo.INGRESO);
        BigDecimal totalBanco = sumar(recibos, TipoRecibo.BANCO);
        BigDecimal totalEgresos = egresos.stream().map(Egreso::getImporte).reduce(BigDecimal.ZERO, BigDecimal::add);

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = crearHoja(libro, "Totales " + mes, "Concepto", "Total");
            escribirFila(hoja, 1, "Ingresos (cobranza)", totalIngresos);
            escribirFila(hoja, 2, "Canjes bancarios", totalBanco);
            escribirFila(hoja, 3, "Egresos", totalEgresos);
            escribirFila(hoja, 4, "Neto (ingresos + banco - egresos)",
                    totalIngresos.add(totalBanco).subtract(totalEgresos));
            return aBytes(libro);
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar el reporte: " + e.getMessage());
        }
    }

    @Override
    public byte[] generarReporteMensual(YearMonth mes) {
        LocalDateTime inicio = mes.atDay(1).atStartOfDay();
        LocalDateTime fin = mes.atEndOfMonth().atTime(LocalTime.MAX);
        List<Recibo> recibos = reciboRepository.findByFechaBetween(inicio, fin);

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = crearHoja(libro, "Recibos " + mes,
                    "Correlativo", "Tipo", "Fecha", "Monto", "Cuenta", "Banco", "Usuario", "Detalle");
            int fila = 1;
            for (Recibo r : recibos) {
                escribirFila(hoja, fila++,
                        r.getCorrelativo(), r.getTipo().name(), r.getFecha().format(FORMATO_FECHA_HORA),
                        r.getMonto(), idOTexto(r.getCuenta() != null ? r.getCuenta().getId() : null),
                        r.getBanco() != null ? r.getBanco().getNombre() : "-",
                        nombreCompleto(r.getUsuario() != null ? r.getUsuario().getNombres() : null,
                                r.getUsuario() != null ? r.getUsuario().getApellidos() : null),
                        detalleRecibo(r));
            }
            return aBytes(libro);
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar el reporte: " + e.getMessage());
        }
    }

    @Override
    public byte[] generarReporteSocios(YearMonth mes) {
        List<CuentaPorCobrar> cuentas = cuentaPorCobrarRepository
                .findBySocioIsNotNullAndFechaGeneracionBetween(mes.atDay(1), mes.atEndOfMonth());

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = crearHoja(libro, "Socios " + mes,
                    "Socio", "Codigo", "Servicio", "Periodo", "Monto", "Estado", "Fecha generacion", "Fecha pago");
            int fila = 1;
            for (CuentaPorCobrar c : cuentas) {
                escribirFila(hoja, fila++,
                        nombreCompleto(c.getSocio().getNombres(), c.getSocio().getApellidos()),
                        c.getSocio().getCodigo(),
                        c.getServicio().getNombre(),
                        c.getPeriodo(),
                        c.getMonto(),
                        c.getEstado().name(),
                        c.getFechaGeneracion().format(FORMATO_FECHA),
                        c.getFechaPago() != null ? c.getFechaPago().format(FORMATO_FECHA) : "-");
            }
            return aBytes(libro);
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar el reporte: " + e.getMessage());
        }
    }

    @Override
    public byte[] generarReporteNoSocios(YearMonth mes) {
        List<CuentaPorCobrar> cuentas = cuentaPorCobrarRepository
                .findByPuestoIsNotNullAndFechaGeneracionBetween(mes.atDay(1), mes.atEndOfMonth());

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = crearHoja(libro, "No socios " + mes,
                    "Puesto", "Servicio", "Periodo", "Monto", "Estado", "Fecha generacion", "Fecha pago");
            int fila = 1;
            for (CuentaPorCobrar c : cuentas) {
                escribirFila(hoja, fila++,
                        c.getPuesto().getNumero(),
                        c.getServicio().getNombre(),
                        c.getPeriodo(),
                        c.getMonto(),
                        c.getEstado().name(),
                        c.getFechaGeneracion().format(FORMATO_FECHA),
                        c.getFechaPago() != null ? c.getFechaPago().format(FORMATO_FECHA) : "-");
            }
            return aBytes(libro);
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar el reporte: " + e.getMessage());
        }
    }

    @Override
    public byte[] generarReporteEgresos(YearMonth mes) {
        List<Egreso> egresos = egresoRepository.findByFechaBetween(mes.atDay(1), mes.atEndOfMonth());

        try (Workbook libro = new XSSFWorkbook()) {
            Sheet hoja = crearHoja(libro, "Egresos " + mes,
                    "Numero documento", "Proveedor", "Fecha", "Importe", "Estado", "Motivo", "Usuario");
            int fila = 1;
            for (Egreso e : egresos) {
                escribirFila(hoja, fila++,
                        e.getNumeroDocumento(), e.getProveedor(), e.getFecha().format(FORMATO_FECHA),
                        e.getImporte(), e.getEstado().name(), e.getMotivo(),
                        nombreCompleto(e.getUsuario() != null ? e.getUsuario().getNombres() : null,
                                e.getUsuario() != null ? e.getUsuario().getApellidos() : null));
            }
            return aBytes(libro);
        } catch (IOException e) {
            throw new BusinessException("No se pudo generar el reporte: " + e.getMessage());
        }
    }


    private Sheet crearHoja(Workbook libro, String nombre, String... encabezados) {
        Sheet hoja = libro.createSheet(nombreHojaValido(nombre));
        CellStyle estiloEncabezado = libro.createCellStyle();
        Font fuente = libro.createFont();
        fuente.setBold(true);
        estiloEncabezado.setFont(fuente);

        Row filaEncabezado = hoja.createRow(0);
        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEncabezado.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEncabezado);
            hoja.setColumnWidth(i, 22 * 256);
        }
        return hoja;
    }

    private void escribirFila(Sheet hoja, int numeroFila, Object... valores) {
        Row fila = hoja.createRow(numeroFila);
        for (int i = 0; i < valores.length; i++) {
            Cell celda = fila.createCell(i);
            Object valor = valores[i];
            if (valor == null) {
                celda.setCellValue("-");
            } else if (valor instanceof BigDecimal bd) {
                celda.setCellValue(bd.doubleValue());
            } else if (valor instanceof Number n) {
                celda.setCellValue(n.doubleValue());
            } else {
                celda.setCellValue(String.valueOf(valor));
            }
        }
    }

    private byte[] aBytes(Workbook libro) throws IOException {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            libro.write(salida);
            return salida.toByteArray();
        }
    }

    private String nombreHojaValido(String nombre) {
        String limpio = nombre.replaceAll("[\\\\/*\\[\\]:?]", "-");
        return limpio.length() > 31 ? limpio.substring(0, 31) : limpio;
    }

    private String nombreCompleto(String nombres, String apellidos) {
        if (nombres == null && apellidos == null) {
            return "-";
        }
        return (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
    }

    private String idOTexto(Long id) {
        return id != null ? String.valueOf(id) : "-";
    }

    private String detalleRecibo(Recibo r) {
        return switch (r.getTipo()) {
            case INGRESO -> r.getCuenta() != null
                    ? "Pago de cuenta #" + r.getCuenta().getId()
                    : (r.getConcepto() != null ? r.getConcepto() : "Ingreso externo");
            case BANCO -> "Canje bancario";
            case EGRESO -> "Egreso";
        };
    }

    private BigDecimal sumar(List<Recibo> recibos, TipoRecibo tipo) {
        return recibos.stream()
                .filter(r -> r.getTipo() == tipo)
                .map(Recibo::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
