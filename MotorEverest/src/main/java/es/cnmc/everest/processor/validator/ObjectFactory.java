
package es.cnmc.everest.processor.validator;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the es.cnmc.everest.service.processor.validator package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: es.cnmc.everest.service.processor.validator
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link BusinessValidation }
     * 
     */
    public BusinessValidation createBusinessValidation() {
        return new BusinessValidation();
    }

    /**
     * Create an instance of {@link BusinessValidation.Validation }
     * 
     */
    public BusinessValidation.Validation createBusinessValidationValidation() {
        return new BusinessValidation.Validation();
    }

    /**
     * Create an instance of {@link BusinessValidation.Validation.Condition }
     * 
     */
    public BusinessValidation.Validation.Condition createBusinessValidationValidationCondition() {
        return new BusinessValidation.Validation.Condition();
    }

    /**
     * Create an instance of {@link BusinessValidation.Validation.Check }
     * 
     */
    public BusinessValidation.Validation.Check createBusinessValidationValidationCheck() {
        return new BusinessValidation.Validation.Check();
    }

}
