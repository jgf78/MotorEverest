package es.cnmc.everest.processor;

import es.cnmc.everest.Constants;
import es.cnmc.everest.exception.EverestRuntimeException;
import es.cnmc.everest.processor.model.FormatFileLine;
import es.cnmc.everest.processor.constraint.CheckDocument;
import es.cnmc.everest.processor.constraint.FloatPrecisionScale;
import es.cnmc.everest.processor.constraint.ParseDateMultiple;
import es.cnmc.everest.processor.validator.BusinessValidation;
import org.supercsv.cellprocessor.*;
import org.supercsv.cellprocessor.constraint.ForbidSubStr;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.StrMinMax;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.DoubleCellProcessor;
import org.supercsv.cellprocessor.ift.LongCellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.exception.SuperCsvCellProcessorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dromera on 01/09/2016.
 */
public class EverestProcessorContainer {

    private List<SuperCsvCellProcessorException> commonSuppressedExceptions;
    private CellProcessor[] processors;
    private List<Object> syntaxValidations;
    private List<BusinessValidation.Validation> businessValidations;



    public void initializeProcessors() {
        commonSuppressedExceptions = new ArrayList<>();
        processors = new CellProcessor[syntaxValidations.size()];

        List<String> forbiddenStrList = Arrays.asList(".");
        CheckDocument.TypeDocument[] typeDocumentsList = new CheckDocument.TypeDocument[]{CheckDocument.TypeDocument.CIF, CheckDocument.TypeDocument.NIF, CheckDocument.TypeDocument.NIE};

        int k=0;
        for (Object obj : syntaxValidations){
            FormatFileLine inv = (FormatFileLine)obj;

            CellProcessor businessCellProcessor = BusinessProcessorFactory.getProcessor(businessValidations,inv);

            StringCellProcessor cellProcessor = null;
            switch (inv.getType()){
                case "Integer": {
                    if (businessCellProcessor != null) {
                        cellProcessor = new StrMinMax(Integer.parseInt(inv.getMin()), Integer.parseInt(inv.getMax()), new ParseInt((LongCellProcessor)businessCellProcessor));
                    } else {
                        cellProcessor = new StrMinMax(Integer.parseInt(inv.getMin()), Integer.parseInt(inv.getMax()), new ParseInt());
                    }
                    break;
                }
                case "Date": {
                    cellProcessor = new StrRegEx(Constants.DDMMYYYY_DATE_REGEX, new ParseDate(Constants.DDMMYYYY_DATE_FORMAT, false)); break;
                }
                case "Date4Date8": {
                    cellProcessor = new StrRegEx(Constants.YYYY_DDMMYYYY_REGEX, new ParseDateMultiple(new String[]{Constants.DDMMYYYY_DATE_FORMAT, Constants.YYYY_DATE_FORMAT}, false));
                    break;
                }
//                case "Date4": {
//                    cellProcessor = new StrRegEx(Constants.YYYY_DATE_REGEX, new ParseDate(Constants.YYYY_DATE_FORMAT, false)); break;}
                case "Float": {
                    if (businessCellProcessor != null) {
                        cellProcessor = new ForbidSubStr(forbiddenStrList, new StrReplace(",", ".", new StrMinMax(Integer.parseInt(inv.getMin()), Integer.parseInt(inv.getMax()), new ParseDouble((DoubleCellProcessor) businessCellProcessor))));
                    } else {
                        cellProcessor = new ForbidSubStr(forbiddenStrList, new StrReplace(",", ".", new StrMinMax(Integer.parseInt(inv.getMin()), Integer.parseInt(inv.getMax()), new ParseDouble())));
                    }
                    break;
                }
                case "FloatFixed":
                    if (businessCellProcessor != null) {
                        cellProcessor = new ForbidSubStr(forbiddenStrList, new StrReplace(",", ".", new FloatPrecisionScale(Integer.parseInt(inv.getLength()), Integer.parseInt(inv.getMin()), "1".equals(inv.getMax()), new ParseDouble((DoubleCellProcessor) businessCellProcessor))));
                    } else {
                        cellProcessor = new ForbidSubStr(forbiddenStrList, new StrReplace(",", ".", new FloatPrecisionScale(Integer.parseInt(inv.getLength()), Integer.parseInt(inv.getMin()), "1".equals(inv.getMax()),new ParseDouble())));
                    }
                    break;
                case "String": {
                    if (businessCellProcessor != null) {
                        cellProcessor = new StrMinMax(Integer.parseInt(inv.getMin()), Integer.parseInt(inv.getMax()), businessCellProcessor);
                    } else {
                        cellProcessor = new StrMinMax(Integer.parseInt(inv.getMin()), Integer.parseInt(inv.getMax()));
                    }
                    break;
                }
                case "Document": {
                    if (businessCellProcessor != null) {
                        cellProcessor = new CheckDocument(typeDocumentsList, businessCellProcessor);
                    } else {
                        cellProcessor = new CheckDocument(typeDocumentsList);
                    }
                    break;
                }
                default: {
                    throw new EverestRuntimeException(String.format("Type '%s' unknown", inv.getType()));
                }
            }

            InventarioProcessor inventarioProcessor;
            if (inv.isNullable()){
                inventarioProcessor = new InventarioProcessor(new Optional(new Trim(cellProcessor)));
            } else {
                inventarioProcessor = new InventarioProcessor(new NotNull(new Trim(cellProcessor)));
            }
            inventarioProcessor.setCommonSuppressedExceptions(commonSuppressedExceptions);
            processors[k++] = inventarioProcessor;
        }
    }


    public String[] getSyntaxValidationNames(){
        String[] res = new String[syntaxValidations.size()];
        for (int i = 0; i < syntaxValidations.size(); i++) {
            FormatFileLine e = (FormatFileLine)syntaxValidations.get(i);
            res[i]=e.getName();
        }

        return res;

    }

    public List<SuperCsvCellProcessorException> getCommonSuppressedExceptions() {
        return commonSuppressedExceptions;
    }

    public void setCommonSuppressedExceptions(List<SuperCsvCellProcessorException> commonSuppressedExceptions) {
        this.commonSuppressedExceptions = commonSuppressedExceptions;
    }

    public CellProcessor[] getProcessors() {
        return processors;
    }

    public void setProcessors(CellProcessor[] processors) {
        this.processors = processors;
    }

    public List<Object> getSyntaxValidations() {
        return syntaxValidations;
    }

    public void setSyntaxValidations(List<Object> syntaxValidations) {
        this.syntaxValidations = syntaxValidations;
    }

    public List<BusinessValidation.Validation> getBusinessValidations() {
        return businessValidations;
    }

    public void setBusinessValidations(List<BusinessValidation.Validation> businessValidations) {
        this.businessValidations = businessValidations;
    }
}

