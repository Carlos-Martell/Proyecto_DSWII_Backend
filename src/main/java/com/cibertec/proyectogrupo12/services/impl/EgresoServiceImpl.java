package com.cibertec.proyectogrupo12.services.impl;

import com.cibertec.proyectogrupo12.entities.Egreso;
import com.cibertec.proyectogrupo12.entities.Usuario;
import com.cibertec.proyectogrupo12.entities.enums.EstadoEgreso;
import com.cibertec.proyectogrupo12.exceptions.BusinessException;
import com.cibertec.proyectogrupo12.exceptions.ResourceNotFoundException;
import com.cibertec.proyectogrupo12.models.EgresoCargaMasivaResultado;
import com.cibertec.proyectogrupo12.models.EgresoDTO;
import com.cibertec.proyectogrupo12.repositories.EgresoRepository;
import com.cibertec.proyectogrupo12.repositories.UsuarioRepository;
import com.cibertec.proyectogrupo12.services.EgresoService;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class EgresoServiceImpl implements EgresoService {

    private final EgresoRepository egresoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    @Override
    public EgresoDTO registrar(EgresoDTO dto, Long usuarioId) {
        Usuario usuario = obtenerUsuario(usuarioId);
        Egreso egreso = objectMapper.convertValue(dto, Egreso.class);
        egreso.setId(null);
        egreso.setEstado(EstadoEgreso.REGISTRADO);
        egreso.setUsuario(usuario);
        return toDTO(egresoRepository.save(egreso));
    }

    @Override
    public EgresoCargaMasivaResultado registrarMasivo(InputStream contenido, String nombreArchivo, Long usuarioId) {
        obtenerUsuario(usuarioId);

        String nombre = nombreArchivo == null ? "" : nombreArchivo.toLowerCase(Locale.ROOT);
        List<String[]> filas;
        if (nombre.endsWith(".xlsx") || nombre.endsWith(".xls")) {
            filas = leerFilasExcel(contenido);
        } else if (nombre.endsWith(".csv")) {
            filas = leerFilasCsv(contenido);
        } else {
            throw new BusinessException("Formato de archivo no soportado; use .xlsx o .csv");
        }

        if (!filas.isEmpty()) {
            filas.remove(0);
        }

        int registrados = 0;
        List<String> errores = new ArrayList<>();

        for (int i = 0; i < filas.size(); i++) {
            int numeroFila = i + 2;
            try {
                EgresoDTO dto = mapearFilaAEgresoDTO(filas.get(i));
                registrar(dto, usuarioId);
                registrados++;
            } catch (Exception e) {
                errores.add("Fila " + numeroFila + ": " + e.getMessage());
            }
        }

        return EgresoCargaMasivaResultado.builder()
                .totalFilas(filas.size())
                .registrados(registrados)
                .errores(errores)
                .build();
    }

    private EgresoDTO mapearFilaAEgresoDTO(String[] fila) {
        if (fila.length < 4) {
            throw new BusinessException("La fila debe tener al menos numeroDocumento, proveedor, fecha e importe");
        }
        String numeroDocumento = valorCelda(fila, 0);
        String proveedor = valorCelda(fila, 1);
        String fechaTexto = valorCelda(fila, 2);
        String importeTexto = valorCelda(fila, 3);
        String documentoAsociado = valorCelda(fila, 4);
        String motivo = valorCelda(fila, 5);

        if (numeroDocumento.isBlank() || proveedor.isBlank() || fechaTexto.isBlank() || importeTexto.isBlank()) {
            throw new BusinessException("numeroDocumento, proveedor, fecha e importe son obligatorios");
        }

        LocalDate fecha;
        try {
            fecha = LocalDate.parse(fechaTexto.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException("Fecha invalida (use el formato yyyy-MM-dd): " + fechaTexto);
        }

        BigDecimal importe;
        try {
            importe = new BigDecimal(importeTexto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new BusinessException("Importe invalido: " + importeTexto);
        }

        return EgresoDTO.builder()
                .numeroDocumento(numeroDocumento.trim())
                .proveedor(proveedor.trim())
                .fecha(fecha)
                .importe(importe)
                .documentoAsociado(documentoAsociado.isBlank() ? null : documentoAsociado.trim())
                .motivo(motivo.isBlank() ? null : motivo.trim())
                .build();
    }

    private String valorCelda(String[] fila, int indice) {
        return indice < fila.length && fila[indice] != null ? fila[indice] : "";
    }

    private List<String[]> leerFilasExcel(InputStream contenido) {
        List<String[]> filas = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(contenido)) {
            Sheet hoja = workbook.getSheetAt(0);
            for (Row fila : hoja) {
                int columnas = Math.max(fila.getLastCellNum(), 6);
                String[] valores = new String[columnas];
                for (int c = 0; c < columnas; c++) {
                    valores[c] = valorCeldaExcel(fila.getCell(c));
                }
                filas.add(valores);
            }
        } catch (IOException e) {
            throw new BusinessException("No se pudo leer el archivo Excel: " + e.getMessage());
        }
        return filas;
    }

    private String valorCeldaExcel(Cell celda) {
        if (celda == null) {
            return "";
        }
        if (celda.getCellType() == CellType.NUMERIC) {
            double valor = celda.getNumericCellValue();
            if (valor == Math.floor(valor)) {
                return String.valueOf((long) valor);
            }
            return String.valueOf(valor);
        }
        return celda.toString().trim();
    }

    private List<String[]> leerFilasCsv(InputStream contenido) {
        List<String[]> filas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(contenido, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                filas.add(linea.split(",", -1));
            }
        } catch (IOException e) {
            throw new BusinessException("No se pudo leer el archivo CSV: " + e.getMessage());
        }
        return filas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EgresoDTO> listarPorMes(LocalDate fechaDelMes) {
        YearMonth mes = YearMonth.from(fechaDelMes);
        return egresoRepository.findByFechaBetween(mes.atDay(1), mes.atEndOfMonth())
                .stream().map(this::toDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EgresoDTO buscarPorId(Long id) {
        return toDTO(obtenerEntidad(id));
    }

    @Override
    public EgresoDTO anular(Long id) {
        Egreso egreso = obtenerEntidad(id);
        if (egreso.getEstado() == EstadoEgreso.ANULADO) {
            throw new BusinessException("El egreso ya se encuentra anulado");
        }
        egreso.setEstado(EstadoEgreso.ANULADO);
        return toDTO(egresoRepository.save(egreso));
    }

    @Override
    public EgresoDTO procesar(Long id) {
        Egreso egreso = obtenerEntidad(id);
        if (egreso.getEstado() != EstadoEgreso.REGISTRADO) {
            throw new BusinessException("Solo se puede procesar un egreso registrado");
        }
        egreso.setEstado(EstadoEgreso.PROCESADO);
        return toDTO(egresoRepository.save(egreso));
    }

    private Egreso obtenerEntidad(Long id) {
        return egresoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Egreso no encontrado: " + id));
    }

    private Usuario obtenerUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    private EgresoDTO toDTO(Egreso egreso) {
        EgresoDTO dto = objectMapper.convertValue(egreso, EgresoDTO.class);
        dto.setUsuarioId(egreso.getUsuario() != null ? egreso.getUsuario().getId() : null);
        return dto;
    }
}
