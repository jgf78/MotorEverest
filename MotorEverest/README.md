# MotorEverest

* Nombre: MotorEverest
* Versión: 2.1.3
* Copyright: Comisión Nacional de los Mercados y la Competencia (CNMC)
* Última actualización: 14 de mayo de 2018

## 1. Introducción

MotorEverest tiene un listener en la cola de mensajes ActiveMQ de la CNMC para informarse de una solicitud de aporte de fichero. Desencadena la descarga de un fichero del repositorio S3, lo valida sintácticamente y semánticamente y si pasa las validaciones lo copia en el sistema de ficheros de EVEREST.

En el caso en que no pase las validaciones, genera un fichero .err en formato json con el detalle de los errores y lo sube al repositorio S3.

Se envía un JMS a la cola de de eventos de la Sede informando del resultado de la validación.


## 2. Entorno tecnológico

* Capa de presentación: Spring MVC
* Capa de servicio: Spring
* Repositorio de ficheros: VIPR S3 Amazon Services (AWS)
* Integrador de servicios: Spring JMS

## 3. Configuración

El proyecto se configura mediante los archivos de propiedades en la siguiente ruta:

    <HOME_DIRECTORY>/configuracion/motoreverest.properties
    <HOME_DIRECTORY>/configuracion/motoreverest-database.properties

Además, se require de un fichero de configuración del sistema de logs:

    <HOME_DIRECTORY>/configuracion/motoreverest-logback.xml
