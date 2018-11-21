package es.cnmc.everest.dao;

import es.cnmc.everest.exception.EverestRuntimeException;
import es.cnmc.everest.model.ItemFile;
import es.cnmc.everest.model.TipoFicheroConst;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dromera on 17/05/2016.
 * TODO: clase temporal hasta que haya base de datos
 */
@Component
public class ItemFileDAO {

    @Autowired
    private TipoFicheroConst tipoFicheroConst;

    @Autowired
    private PropertyDAO propertyDAO;

    /**
     * Devuelve los ficheros que se se esperan dentro del ZIP (los que son obligatorios y los que hay que validar)
     * Pre: si no viene codDistribuidoraCompleto... es que es de transporte
     *  @param codDistribuidoraCompleto Con el R1-
     */
    public List<ItemFile> findByTipoFichero(Integer idTipoFichero, String codDistribuidoraCompleto) throws ConfigurationException {
        String codDistribuidora = null;
        Boolean auditoriaRequired = null;
        if (codDistribuidoraCompleto != null) {
            codDistribuidora = codDistribuidoraCompleto.toUpperCase().replace("R1-", "");
            List<String> codDistribuidoraRequiredAuditoria = propertyDAO.getPropertyList("empresas.auditoria.obligadas");
            auditoriaRequired = codDistribuidoraRequiredAuditoria.contains(codDistribuidora);
        }

        List<ItemFile> itemFileList;
        if (idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_INVENTARIO))
        {
            itemFileList = populateDistribucionInventario(auditoriaRequired);
        }
        else if (idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_AUDITORIA))
        {
            itemFileList = populateDistribucionAuditoria(auditoriaRequired);
        }
        else if (idTipoFichero.equals(tipoFicheroConst.DISTRIBUCION_PLANINVERSION))
        {
            itemFileList = populateDistribucionPlanInversion();
        }
        else if (idTipoFichero.equals(tipoFicheroConst.CIRCULAR_CIRCULAR))
        {
            itemFileList = populateCircularCircular(auditoriaRequired, codDistribuidora);
        }
        else if (idTipoFichero.equals(tipoFicheroConst.TRANSPORTE_INVENTARIO))
        {
            itemFileList = populateTransporteInventario();
        }
        else
        {
            // TRANSPORTE_AUDITORIA
            // TRANSPORTE_PLANINVERSION ¿?
            itemFileList = new ArrayList<>();
        }
        return itemFileList;
    }


    private  List<ItemFile> populateDistribucionInventario(boolean auditoriaRequired) {
        ItemFile itemFile;
        List<ItemFile> itemFileList = new ArrayList<>();

        String[] inventarioFiles = new String[]{"1","2","3","4","5","6","7","8"};
        for (String fileId : inventarioFiles) {
            itemFile = new ItemFile();
            itemFile.setName("INVENTARIO_R1-XXX_" + fileId + ".txt");
            itemFile.setFound(false);
            itemFile.setId(fileId);
            itemFile.setPath(".");
            itemFile.setRequired(true);
            itemFile.setValidate(true);
            itemFile.setTypeFile(ItemFile.TypeFile.FILE);
            itemFileList.add(itemFile);
        }

        // transmisiones
        itemFile = new ItemFile();
        itemFile.setName("TRANSMISIONES_R1-XXX.txt");
        itemFile.setFound(false);
        itemFile.setId("9");
        itemFile.setPath(".");
        itemFile.setRequired(true);
        itemFile.setValidate(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        // modificaciones
        itemFile = new ItemFile();
        itemFile.setName("MODIFICACIONES_R1-XXX.txt");
        itemFile.setFound(false);
        itemFile.setId("modificaciones");
        itemFile.setPath(".");
        itemFile.setRequired(true);
        itemFile.setValidate(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        // declaración responsable
        itemFile = new ItemFile();
        itemFile.setName("DEC_RESP_R1-XXX.pdf");
        itemFile.setFound(false);
        itemFile.setId("10");
        itemFile.setPath(".");
        itemFile.setRequired(!auditoriaRequired);
        itemFile.setValidate(false);
        itemFile.setDeclResponsable(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        // auditoría
        itemFile = new ItemFile();
        itemFile.setName("AUDITORIA_R1-XXX.xlsx (ó .xls)");
        itemFile.setFound(false);
        itemFile.setId("11");
        itemFile.setPath(".");
        itemFile.setRequired(auditoriaRequired);
        itemFile.setValidate(false);
        itemFile.setAuditoria(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        return itemFileList;
    }

    private  List<ItemFile> populateCircularCircular(boolean auditoriaRequired, String codDistribuidora) {
        assert(codDistribuidora != null);
        ItemFile itemFile;
        List<ItemFile> itemFileList = new ArrayList<>();
        // "9" no es un fichero csv
        String[] circularFiles = new String[]{"0", "1", "1bis", "2", "2A", "2B", "3", "4", "5", "6", "7", "9", "10", "11", "12", "12bis", "13", "13bis", "13C", "14", "15", "16", "17", "18", "19bis", "20", "26", "26bis", "26C", "26D", "26E", "26F", "26G", "28", "28bis", "29", "29bis", "29C", "29D", "30", "30bis", "31"};
        for (String fileId : circularFiles) {
            itemFile = new ItemFile();
            itemFile.setName("CIR4_2015_" + fileId + "_R1-XXX_2017.txt");
            itemFile.setFound(false);
            itemFile.setId(fileId);
            itemFile.setPath(".");
            itemFile.setRequired(false);
            itemFile.setValidate(!fileId.equals("9"));
            itemFile.setTypeFile(ItemFile.TypeFile.FILE);
            itemFileList.add(itemFile);
        }
        if (codDistribuidora.equals("000")) {
            setRequired(itemFileList, new String[]{"29", "29bis", "29C", "29D", "30", "30bis"});
        } else if (codDistribuidora.equals("001") ||
                codDistribuidora.equals("002") ||
                codDistribuidora.equals("003") ||
                codDistribuidora.equals("005") ||
                codDistribuidora.equals("008") ||
                codDistribuidora.equals("299")) { // >100.0000 clientes
            setRequired(itemFileList, new String[]{"0", "1", "1bis", "2", "2A", "2B", "3", "4", "5", "6", "7", "9", "10", "11", "12", "12bis", "13", "13bis", "13C", "14", "15", "16", "17", "18", "19bis", "20", "26", "26bis", "26C", "26D", "26E", "26F", "26G", "28", "28bis", "31"});
        } else { // <100.000 clientes
            setRequired(itemFileList, new String[]{"0", "1", "1bis", "2", "2A", "2B", "3", "9", "10", "11", "12", "12bis", "13", "13bis", "13C", "14", "15", "16", "17", "19bis", "20", "26", "26bis", "26C", "26D", "28", "28bis", "31"});
        }

        itemFile = new ItemFile();
        itemFile.setName("impuestos_R1-XXX.txt");
        itemFile.setFound(false);
        itemFile.setId("impuestos");
        itemFile.setPath(".");
        itemFile.setRequired(!codDistribuidora.equals("000"));
        itemFile.setValidate(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        // declaración responsable
        itemFile = new ItemFile();
        itemFile.setName("DEC_RESP_R1-XXX.pdf");
        itemFile.setFound(false);
        itemFile.setId("decresp");
        itemFile.setPath(".");
        itemFile.setRequired(!auditoriaRequired);
        itemFile.setValidate(false);
        itemFile.setDeclResponsable(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        itemFile = new ItemFile();
        itemFile.setName("R1-XXX_AUDITORIA.xlsx (ó .xls)");
        itemFile.setFound(false);
        itemFile.setId("auditoria");
        itemFile.setPath("./CIRA");
        itemFile.setRequired(auditoriaRequired);
        itemFile.setValidate(false);
        itemFile.setAuditoria(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        itemFile = new ItemFile();
        itemFile.setName("CIRA");
        itemFile.setFound(false);
        itemFile.setId("cira");
        itemFile.setPath(".");
        itemFile.setRequired(auditoriaRequired);
        itemFile.setValidate(false);
        itemFile.setAuditoria(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FOLDER);
        itemFileList.add(itemFile);

        itemFile = new ItemFile();
        itemFile.setName("CIIA");
        itemFile.setFound(false);
        itemFile.setId("ciia");
        itemFile.setPath(".");
        itemFile.setRequired(auditoriaRequired);
        itemFile.setValidate(false);
        itemFile.setAuditoria(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FOLDER);
        itemFileList.add(itemFile);

        return itemFileList;
    }

    private  List<ItemFile> populateDistribucionAuditoria(boolean auditoriaRequired) {
        ItemFile itemFile;
        List<ItemFile> itemFileList = new ArrayList<>();
        String[] auditFiles = new String[]{"1","2","3","4","5","6","7","8"};
        for (String fileId : auditFiles) {
            itemFile = new ItemFile();
            itemFile.setName("AUDIT_R1-XXX_" + fileId + ".txt");
            itemFile.setFound(false);
            itemFile.setId(fileId);
            itemFile.setPath(".");
            itemFile.setRequired(true);
            itemFile.setValidate(true);
            itemFile.setTypeFile(ItemFile.TypeFile.FILE);
            itemFileList.add(itemFile);
        }

        // cuadros resumen
        itemFile = new ItemFile();
        itemFile.setName("CUADROS_RESUMEN_R1-XXX.xlsx (ó .xls)");
        itemFile.setFound(false);
        itemFile.setId("cuadro");
        itemFile.setPath(".");
        itemFile.setRequired(true);
        itemFile.setValidate(false);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        // seguimiento
        itemFile = new ItemFile();
        itemFile.setName("SEGUIMIENTO_R1-XXX.txt");
        itemFile.setFound(false);
        itemFile.setId("seguimiento");
        itemFile.setPath(".");
        itemFile.setRequired(true);
        itemFile.setValidate(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        // actas
        itemFile = new ItemFile();
        itemFile.setName("ACTAS.zip");
        itemFile.setFound(false);
        itemFile.setId("actas");
        itemFile.setPath(".");
        itemFile.setRequired(false);
        itemFile.setValidate(false);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        // declaración responsable
        itemFile = new ItemFile();
        itemFile.setName("DEC_RESP_R1-XXX.pdf");
        itemFile.setFound(false);
        itemFile.setId("decresp");
        itemFile.setPath(".");
        itemFile.setRequired(!auditoriaRequired);
        itemFile.setValidate(false);
        itemFile.setDeclResponsable(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        // auditoría
        itemFile = new ItemFile();
        itemFile.setName("AUDITORIA_R1-XXX.xlsx (ó .xls)");
        itemFile.setFound(false);
        itemFile.setId("auditoria");
        itemFile.setPath(".");
        itemFile.setRequired(auditoriaRequired);
        itemFile.setValidate(false);
        itemFile.setAuditoria(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        // auditoría
        itemFile = new ItemFile();
        itemFile.setName("CIIA.zip");
        itemFile.setFound(false);
        itemFile.setId("ciia");
        itemFile.setPath(".");
        itemFile.setRequired(auditoriaRequired);
        itemFile.setValidate(false);
        itemFile.setAuditoria(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        return itemFileList;
    }

    private  List<ItemFile> populateTransporteInventario() {
        ItemFile itemFile;
        List<ItemFile> itemFileList = new ArrayList<>();

        itemFile = new ItemFile();
        itemFile.setName("TRINVaaaaeee.xml ");
        itemFile.setFound(false);
        itemFile.setId("TRINV");
        itemFile.setPath(".");
        itemFile.setRequired(true);
        itemFile.setValidate(false);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        itemFile = new ItemFile();
        itemFile.setName("TRBAJaaaaeee.xml ");
        itemFile.setFound(false);
        itemFile.setId("TRBAJ");
        itemFile.setPath(".");
        itemFile.setRequired(false);
        itemFile.setValidate(false);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        return itemFileList;
    }

    private List<ItemFile> populateDistribucionPlanInversion() {
        ItemFile itemFile;
        List<ItemFile> itemFileList = new ArrayList<>();
        String[] planFiles = new String[]{"1","2","3","4","5","6","7","8"};
        for (String fileId : planFiles) {
            itemFile = new ItemFile();
            itemFile.setName("PI_R1-XXX_" + fileId + ".txt");
            itemFile.setFound(false);
            itemFile.setId(fileId);
            itemFile.setPath(".");
            itemFile.setRequired(true);
            itemFile.setValidate(true);
            itemFile.setTypeFile(ItemFile.TypeFile.FILE);
            itemFileList.add(itemFile);
        }
        itemFile = new ItemFile();
        itemFile.setName("PI_RESUMEN_R1-XXX.txt");
        itemFile.setFound(false);
        itemFile.setId("resumen");
        itemFile.setPath(".");
        itemFile.setRequired(true);
        itemFile.setValidate(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        itemFile = new ItemFile();
        itemFile.setName("PI_RESUMEN_CCAA_R1-XXX.txt");
        itemFile.setFound(false);
        itemFile.setId("resumenccaa");
        itemFile.setPath(".");
        itemFile.setRequired(true);
        itemFile.setValidate(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        itemFile = new ItemFile();
        itemFile.setName("PI_PROYECTOS_R1-XXX.txt");
        itemFile.setFound(false);
        itemFile.setId("proyectos");
        itemFile.setPath(".");
        itemFile.setRequired(true);
        itemFile.setValidate(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        itemFile = new ItemFile();
        itemFile.setName("PI_MACRO_R1-XXX.txt");
        itemFile.setFound(false);
        itemFile.setId("macro");
        itemFile.setPath(".");
        itemFile.setRequired(true);
        itemFile.setValidate(true);
        itemFile.setTypeFile(ItemFile.TypeFile.FILE);
        itemFileList.add(itemFile);

        return itemFileList;
    }

    private void setRequired(List<ItemFile> itemFileList, String[] requiredIdList) {
        boolean checkFound;
        for (String idRequired : requiredIdList) {
            checkFound = false;
            for (ItemFile itemFile: itemFileList) {
                if (itemFile.getId().equalsIgnoreCase(idRequired)) {
                    itemFile.setRequired(true);
                    checkFound = true;
                    break;
                }
            }
            if (!checkFound) {
                throw new EverestRuntimeException("Id de fichero no encontrado: " + idRequired);
            }
        }

    }
}
