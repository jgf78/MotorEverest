# MotorEverst

A continuación se detallan los ficheros .properties y ficheros de configuración de validaciones.

## Configuracion del proyecto

_Configuracion de propiedades_

Fichero de configuración. Usamos ruta absoluta, sin prefijos ni nada


    <context:property-placeholder location="file:${user.home}/configuracion/motoreverest/motoreverest.properties"/>

Para el rellenado del fichero de configuración se pueden usar valores por defecto ${clave:valor_defecto}

Se puede consultar la configuración de ejemplo en el siguiente fichero de referencia:
[motoreverest.properties](src/main/config/motoreverest.properties)

_Configuracion para Log_

Se utiliza SL4J y su implementación de referencia LogBack. Existe un fichero de ejemplo que se debe copiar en la ruta indicada en el configurador marcado en web.xml

    <context-param>
        <param-name>logbackConfigLocation</param-name>
        <param-value>file:${user.home}/configuracion/motoreverest-logback.xml</param-value>
    </context-param>

    <listener>
        <listener-class>ch.qos.logback.ext.spring.web.LogbackConfigListener</listener-class>
    </listener>

[motoreverest-logback.xml](src/main/config/motoreverest-logback.xml)

_Configuracion para JBoss/WildFly_

Configurar LogBack (jboss-deployment-structure.xml):

    <jboss-deployment-structure>
        <deployment>
            <!-- Exclusions allow you to prevent the server from automatically adding some dependencies     -->
            <exclusions>
                <module name="org.slf4j"/>
                <module name="org.slf4j.impl"/>
            </exclusions>
        </deployment>
    </jboss-deployment-structure>


## Configuracion de las validaciones

_Configuración de las validaciones_

Las validaciones que se aplican a los ficheros dependen del Procedimiento y Tipo de Fichero. Son ficheros .csv (validaciones sintácticas) y ficheros XML (validaciones semánticas).

De momento se contemplan dos procedimientos (Circular 4/2015 e Inventario) con un tipo de fichero por cada procedimiento.


Un ejemplo (validación del fichero 1 del procedimiento de Circular 4/2015):

[2015.1-circular-format.csv](/src/main/config/format/circular/2015.1-circular-format.csv)

[2015.1-circular-business-validation.xml](/src/main/config/business/2015.1-circular-business-validation.xml)
