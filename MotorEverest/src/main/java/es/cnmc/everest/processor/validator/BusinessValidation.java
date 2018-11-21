
package es.cnmc.everest.processor.validator;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="validation" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="condition">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="column" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="operator" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="values" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="check" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="column" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="columnIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                             &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                             &lt;element name="operator" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="values" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="default-value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "validation"
})
@XmlRootElement(name = "businessValidation")
public class BusinessValidation {

    protected List<BusinessValidation.Validation> validation;

    /**
     * Gets the value of the validation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the validation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValidation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BusinessValidation.Validation }
     * 
     * 
     */
    public List<BusinessValidation.Validation> getValidation() {
        if (validation == null) {
            validation = new ArrayList<BusinessValidation.Validation>();
        }
        return this.validation;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="condition">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="column" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *                   &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *                   &lt;element name="operator" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="values" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="check" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="column" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="columnIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *                   &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *                   &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
     *                   &lt;element name="operator" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="values" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="default-value" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "condition",
        "check"
    })
    public static class Validation {

        @XmlElement(required = true)
        protected BusinessValidation.Validation.Condition condition;
        protected BusinessValidation.Validation.Check check;

        /**
         * Obtiene el valor de la propiedad condition.
         * 
         * @return
         *     possible object is
         *     {@link BusinessValidation.Validation.Condition }
         *     
         */
        public BusinessValidation.Validation.Condition getCondition() {
            return condition;
        }

        /**
         * Define el valor de la propiedad condition.
         * 
         * @param value
         *     allowed object is
         *     {@link BusinessValidation.Validation.Condition }
         *     
         */
        public void setCondition(BusinessValidation.Validation.Condition value) {
            this.condition = value;
        }

        /**
         * Obtiene el valor de la propiedad check.
         * 
         * @return
         *     possible object is
         *     {@link BusinessValidation.Validation.Check }
         *     
         */
        public BusinessValidation.Validation.Check getCheck() {
            return check;
        }

        /**
         * Define el valor de la propiedad check.
         * 
         * @param value
         *     allowed object is
         *     {@link BusinessValidation.Validation.Check }
         *     
         */
        public void setCheck(BusinessValidation.Validation.Check value) {
            this.check = value;
        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="column" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="columnIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
         *         &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
         *         &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
         *         &lt;element name="operator" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="values" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="default-value" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "column",
            "columnIndex",
            "position",
            "length",
            "operator",
            "values",
            "defaultValue"
        })
        public static class Check {

            @XmlElement(required = true)
            protected String column;
            protected int columnIndex;
            protected Integer position;
            protected Integer length;
            @XmlElement(required = true, nillable = true)
            protected String operator;
            @XmlElement(required = true)
            protected String values;
            @XmlElement(name = "default-value", required = true, nillable = true)
            protected String defaultValue;

            /**
             * Obtiene el valor de la propiedad column.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getColumn() {
                return column;
            }

            /**
             * Define el valor de la propiedad column.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setColumn(String value) {
                this.column = value;
            }

            /**
             * Obtiene el valor de la propiedad columnIndex.
             * 
             */
            public int getColumnIndex() {
                return columnIndex;
            }

            /**
             * Define el valor de la propiedad columnIndex.
             * 
             */
            public void setColumnIndex(int value) {
                this.columnIndex = value;
            }

            /**
             * Obtiene el valor de la propiedad position.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getPosition() {
                return position;
            }

            /**
             * Define el valor de la propiedad position.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setPosition(Integer value) {
                this.position = value;
            }

            /**
             * Obtiene el valor de la propiedad length.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getLength() {
                return length;
            }

            /**
             * Define el valor de la propiedad length.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setLength(Integer value) {
                this.length = value;
            }

            /**
             * Obtiene el valor de la propiedad operator.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOperator() {
                return operator;
            }

            /**
             * Define el valor de la propiedad operator.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOperator(String value) {
                this.operator = value;
            }

            /**
             * Obtiene el valor de la propiedad values.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValues() {
                return values;
            }

            /**
             * Define el valor de la propiedad values.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValues(String value) {
                this.values = value;
            }

            /**
             * Obtiene el valor de la propiedad defaultValue.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getDefaultValue() {
                return defaultValue;
            }

            /**
             * Define el valor de la propiedad defaultValue.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setDefaultValue(String value) {
                this.defaultValue = value;
            }

        }


        /**
         * <p>Clase Java para anonymous complex type.
         * 
         * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="column" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="position" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
         *         &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
         *         &lt;element name="operator" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="values" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "column",
            "position",
            "length",
            "operator",
            "values"
        })
        public static class Condition {

            @XmlElement(required = true)
            protected String column;
            protected Integer position;
            protected Integer length;
            @XmlElement(required = true)
            protected String operator;
            @XmlElement(required = true)
            protected String values;

            /**
             * Obtiene el valor de la propiedad column.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getColumn() {
                return column;
            }

            /**
             * Define el valor de la propiedad column.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setColumn(String value) {
                this.column = value;
            }

            /**
             * Obtiene el valor de la propiedad position.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getPosition() {
                return position;
            }

            /**
             * Define el valor de la propiedad position.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setPosition(Integer value) {
                this.position = value;
            }

            /**
             * Obtiene el valor de la propiedad length.
             * 
             * @return
             *     possible object is
             *     {@link Integer }
             *     
             */
            public Integer getLength() {
                return length;
            }

            /**
             * Define el valor de la propiedad length.
             * 
             * @param value
             *     allowed object is
             *     {@link Integer }
             *     
             */
            public void setLength(Integer value) {
                this.length = value;
            }

            /**
             * Obtiene el valor de la propiedad operator.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOperator() {
                return operator;
            }

            /**
             * Define el valor de la propiedad operator.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOperator(String value) {
                this.operator = value;
            }

            /**
             * Obtiene el valor de la propiedad values.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getValues() {
                return values;
            }

            /**
             * Define el valor de la propiedad values.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setValues(String value) {
                this.values = value;
            }

        }

    }

}
