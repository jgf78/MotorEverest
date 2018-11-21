/*
 * nombres de tipos de fichero para EVEREST
 */
-- PROCEDIMIENTO
SET IDENTITY_INSERT [SedeSchema].[Procedimientos] ON;

INSERT INTO [SedeSchema].[Procedimientos] ([idProcedimiento], [nombre], [nombreCorto], [descripcion], [descripcionCorta],
        [visiblePortalSede], [habilitado], [interactivo], [codigoSIA], [fechaAlta], [fechaBaja], [orden], [idUnidadTramitadora],
        [codigoTipoAlmacenamiento], [idAlmacenamientoBase], [metaDatos],
        [credencialesAlmacenamientoKey], [credencialesAlmacenamientoSecret], [mascaraAlmacenamiento]) VALUES
    (5, 'Circular 4/2015', 'CIRCULAR_4_2015', NULL, NULL, 'True', 'True', 'False', 'SIA-EVEREST1', getdate(), NULL, 5, 7, 'S3',
    'EVEREST.s3.bucket', NULL,
    'EVEREST.s3.credencialesKey', 'EVEREST.s3.credencialesSecret',
    'procedimiento.nombreCorto+''/''+empresa.nombreCorto+''/'''),
    (6, 'Inventario distribución RD 1048', 'DISTRIBUCION_RD_1048', NULL, NULL, 'True', 'True', 'False', 'SIA-EVEREST2', getdate(), NULL, 5, 7, 'S3',
    'EVEREST.s3.bucket', NULL,
    'EVEREST.s3.credencialesKey', 'EVEREST.s3.credencialesSecret',
    'procedimiento.nombreCorto+''/''+empresa.nombreCorto+''/'''),
    (7, 'Transporte RD 1047', 'TRANSPORTE_RD_1047', NULL, NULL, 'True', 'True', 'False', 'SIA-EVEREST3', getdate(), NULL, 5, 7, 'S3',
    'EVEREST.s3.bucket', NULL,
    'EVEREST.s3.credencialesKey', 'EVEREST.s3.credencialesSecret',
    'procedimiento.nombreCorto+''/''+empresa.nombreCorto+''/''')
SET IDENTITY_INSERT [SedeSchema].[Procedimientos] OFF;

SET IDENTITY_INSERT [LoaderSchema].[TipoFicheros] ON;
INSERT INTO [LoaderSchema].[TipoFicheros] ([idTipoFichero], [idProcedimiento], [nombrecorto], [descripcion], [entrada], [mascaraNombreFichero],
        [version], [retencionMeses], [codigoTipoContenedor], [codigoFormatoFichero], [codigoTipoAlmacenamiento], [uriServicioValidacion],
        [nombreColaDestino], [validezEnlaceMinutos]) VALUES
    (34, 5, 'CIRCULAR_4_2015', 'Fichero zip con ficheros para la remisión de ficheros de Everest', 1, 'ficheroGenerado+''.''+(((ficheroOriginal==null || tipo.codigotipoContenedor==null )?tipo.codigoFormatoFichero: (ficheroOriginal.endsWith(tipo.codigotipoContenedor)? tipo.codigotipoContenedor: tipo.codigoFormatoFichero)))', NULL, NULL, 'zip', 'zip', 'S3', NULL, 'EVERESTVERTICAL.queue.entrada', NULL),
    (35, 5, 'ERRORES_CIRCULAR_4_2015', 'ERRORES para CIRCULAR 4-2015 EVEREST', 0, NULL, NULL, NULL, NULL, 'txt', 'S3', NULL, NULL, NULL),
    (36, 6, 'INVENTARIO_DISTRIBUCION', 'Fichero zip con ficheros para la remisión de ficheros de Everest', 1, 'ficheroGenerado+''.''+(((ficheroOriginal==null || tipo.codigotipoContenedor==null )?tipo.codigoFormatoFichero: (ficheroOriginal.endsWith(tipo.codigotipoContenedor)? tipo.codigotipoContenedor: tipo.codigoFormatoFichero)))', NULL, NULL, 'zip', 'zip', 'S3', NULL, 'EVEREST.queue.entrada', NULL),
    (37, 6, 'AUDITORIA_INVERSIONES', 'Fichero zip con ficheros para la remisión de ficheros de Everest', 1, 'ficheroGenerado+''.''+(((ficheroOriginal==null || tipo.codigotipoContenedor==null )?tipo.codigoFormatoFichero: (ficheroOriginal.endsWith(tipo.codigotipoContenedor)? tipo.codigotipoContenedor: tipo.codigoFormatoFichero)))', NULL, NULL, 'zip', 'zip', 'S3', NULL, 'EVERESTVERTICAL.queue.entrada', NULL),
    (38, 6, 'PLANES_DE_INVERSION', 'Fichero zip con ficheros para la remisión de ficheros de Everest', 1, 'ficheroGenerado+''.''+(((ficheroOriginal==null || tipo.codigotipoContenedor==null )?tipo.codigoFormatoFichero: (ficheroOriginal.endsWith(tipo.codigotipoContenedor)? tipo.codigotipoContenedor: tipo.codigoFormatoFichero)))', NULL, NULL, 'zip', 'zip', 'S3', NULL, 'EVERESTVERTICAL.queue.entrada', NULL),
    (39, 6, 'ERRORES_DISTRIBUCION_RD_1048', 'ERRORES para DISTRIBUCION_RD_1048 EVEREST', 0, NULL, NULL, NULL, NULL, 'txt', 'S3', NULL, NULL, NULL),
    (40, 7, 'INVENTARIO_TRANSPORTE', 'Fichero zip con ficheros para la remisión de ficheros de Everest', 1, 'ficheroGenerado+''.''+(((ficheroOriginal==null || tipo.codigotipoContenedor==null )?tipo.codigoFormatoFichero: (ficheroOriginal.endsWith(tipo.codigotipoContenedor)? tipo.codigotipoContenedor: tipo.codigoFormatoFichero)))', NULL, NULL, 'zip', 'zip', 'S3', NULL, 'EVERESTVERTICAL.queue.entrada', NULL),
    (41, 7, 'AUDITORIA_TRANSPORTE', 'Fichero zip con ficheros para la remisión de ficheros de Everest', 1, 'ficheroGenerado+''.''+(((ficheroOriginal==null || tipo.codigotipoContenedor==null )?tipo.codigoFormatoFichero: (ficheroOriginal.endsWith(tipo.codigotipoContenedor)? tipo.codigotipoContenedor: tipo.codigoFormatoFichero)))', NULL, NULL, 'zip', 'zip', 'S3', NULL, 'EVERESTVERTICAL.queue.entrada', NULL),
    (42, 7, 'ERRORES_TRANSPORTE_RD_1047', 'ERRORES para TRANSPORTE_RD_1047 EVEREST', 0, NULL, NULL, NULL, NULL, 'txt', 'S3', NULL, NULL, NULL)

SET IDENTITY_INSERT [LoaderSchema].[TipoFicheros] OFF;
/*
		ficheroGenerado+'.'+(((ficheroOriginal==null || tipo.codigotipoContenedor==null )?tipo.codigoFormatoFichero: (ficheroOriginal.endsWith(tipo.codigotipoContenedor)? tipo.codigotipoContenedor: tipo.codigoFormatoFichero)))

*/

/* Configuracion S3 */
INSERT INTO [SedeSchema].[AplicacionConfiguraciones] ([idAplicacion],[clave],[descripcion],[entorno],[valor]) VALUES
     (1,'EVEREST.s3.bucket', 'Nombre del bucket (unico en S3) para EVEREST', 'DESARROLLO', 'DESA_AP_EVEREST'),
     (1,'EVEREST.s3.credencialesKey', 'Credenciales (Key) de S3 para EVEREST', 'DESARROLLO', 'AP_S3_OwerDESA@cnmc.age'),
     (1,'EVEREST.s3.credencialesSecret', 'Credenciales (Secret) de S3 para EVEREST', 'DESARROLLO', 'ebf18dca908782b6558cf528747ad63ec49bde0f4c61329ff95fb5eda96bcf580f8cfba36c01a6be3dce9615d2f0b44af1ac360862412d6b1444c829a218b0b1'),
     (1,'EVEREST.s3.bucket', 'Nombre del bucket (unico en S3) para EVEREST', 'PREPRODUCCION', 'PRE_AP_EVEREST'),
     (1,'EVEREST.s3.credencialesKey', 'Credenciales (Key) de S3 para EVEREST', 'PREPRODUCCION', 'AP_S3_OwerPRE@cnmc.age'),
     (1,'EVEREST.s3.credencialesSecret', 'Credenciales (Secret) de S3 para EVEREST', 'PREPRODUCCION', '985222fb55f0f189473f21323f0afead2aaef1771d9c3c44fe8d1e1388160f0958ff19241c38c166d9e79bbb546c3eb9de6bb5980fe0a2052fcc2bd3d29e1418'),
     (1,'EVEREST.s3.bucket', 'Nombre del bucket (unico en S3) para EVEREST', 'PRODUCCION', 'PRO_AP_EVEREST'),
     (1,'EVEREST.s3.credencialesKey', 'Credenciales (Key) de S3 para EVEREST', 'PRODUCCION', 'AP_S3Everest_Owpro@cnmc.age'),
     (1,'EVEREST.s3.credencialesSecret', 'Credenciales (Secret) de S3 para EVEREST', 'PRODUCCION', '4763fcbefe9c606b652d824ad6ce7391355b930c8b39e963dd36b56609ce98ecf1f183cc24a78bb479aed591fbd0037464280286aeb9201da128f334cdd66842');
GO

-- ROLES
INSERT INTO [SedeSchema].[Roles] ([nombre], [descripcion], [idProcedimiento],
    [fechaAlta], [fechaBaja], [codigoTipo], [distinguisedNameDirectorioActivo]) VALUES
    ('Operadoras electricidad', 'Rol de empresa operadora eléctrica para EVEREST', 5, getdate(), NULL, 'EMPRESA', NULL),
    ('Cargador EVEREST', 'Rol de contacto cargador EVEREST', 5, getdate(), NULL, 'CONTACTO', NULL),
    ('Operadoras electricidad', 'Rol de empresa operadora eléctrica para EVEREST', 6, getdate(), NULL, 'EMPRESA', NULL),
    ('Cargador EVEREST', 'Rol de contacto cargador EVEREST', 6, getdate(), NULL, 'CONTACTO', NULL),
    ('Operadoras electricidad', 'Rol de empresa operadora eléctrica para EVEREST', 7, getdate(), NULL, 'EMPRESA', NULL),
    ('Cargador EVEREST', 'Rol de contacto cargador EVEREST', 7, getdate(), NULL, 'CONTACTO', NULL);

--Roles backoffice
    INSERT INTO [SedeSchema].[Roles]
           ([idProcedimiento],[nombre],[descripcion],[codigoTipo],[distinguisedNameDirectorioActivo])
     VALUES
           (5,'Usuario EVEREST 4/2015','Rol de usuario para la gestion de Circular 4/2015','INTERNO','CN=AP_SedeElectronica_backoffice_usuarios_EVEREST,OU=SedeElectronica,OU=Aplicaciones,OU=CNMC,DC=cnmc,DC=age'),
		 (6,'Usuario EVEREST RD 1048','Rol de usuario para la gestion de Inventario distribución RD 1048','INTERNO','CN=AP_SedeElectronica_backoffice_usuarios_EVEREST,OU=SedeElectronica,OU=Aplicaciones,OU=CNMC,DC=cnmc,DC=age'),
		 (7,'Usuario EVEREST RD 1047','Rol de usuario para la gestion de Transporte RD 1047','INTERNO','CN=AP_SedeElectronica_backoffice_usuarios_EVEREST,OU=SedeElectronica,OU=Aplicaciones,OU=CNMC,DC=cnmc,DC=age'),
		 (5,'Editor EVEREST 4/2015','Rol de usuario para la gestion de Circular 4/2015','INTERNO','CN=AP_SedeElectronica_backoffice_editores_EVEREST,OU=SedeElectronica,OU=Aplicaciones,OU=CNMC,DC=cnmc,DC=age'),
		 (6,'Editor EVEREST RD 1048','Rol de usuario para la gestion de Inventario distribución RD 1048','INTERNO','CN=AP_SedeElectronica_backoffice_editores_EVEREST,OU=SedeElectronica,OU=Aplicaciones,OU=CNMC,DC=cnmc,DC=age'),
		 (7,'Editor EVEREST RD 1047','Rol de usuario para la gestion de Transporte RD 1047','INTERNO','CN=AP_SedeElectronica_backoffice_editores_EVEREST,OU=SedeElectronica,OU=Aplicaciones,OU=CNMC,DC=cnmc,DC=age')

GO

-- PERMISOS
INSERT INTO [SedeSchema].[Permisos_Roles] ([idPermiso],[idRol])
SELECT 1, idRol FROM [SedeSchema].[Roles] WHERE nombre = 'Cargador EVEREST';

--idPlantillaExito
--idPlantillaError
--idPlantillaParcial
INSERT INTO [SedeSchema].[AplicacionConfiguraciones] ([idAplicacion],[clave],[descripcion],[entorno],[valor]) VALUES
     (1,'EVEREST.queue.entrada', 'Cola de entrada para EVEREST', 'DESARROLLO', 'entrada.everest.desa'),
     (1,'EVEREST.queue.entrada', 'Cola de entrada para EVEREST', 'PREPRODUCCION', 'entrada.everest.pre'),
     	(1,'EVEREST.queue.entrada', 'Cola de entrada para EVEREST', 'PRODUCCION', 'entrada.everest.pro'),

     (1,'EVERESTVERTICAL.queue.entrada', 'Cola de entrada para EVEREST-VERTICAL', 'DESARROLLO', 'entrada.vertical.desa'),
     (1,'EVERESTVERTICAL.queue.entrada', 'Cola de entrada para EVEREST-VERTICAL', 'PREPRODUCCION', 'entrada.vertical.pre'),
     (1,'EVERESTVERTICAL.queue.entrada', 'Cola de entrada para EVEREST-VERTICAL', 'PRODUCCION', 'entrada.vertical.pro'),

     (1,'procedimiento.5.envioActivado', '', 'DESARROLLO', 'true'),
     (1,'procedimiento.5.servicioNotificaciones', '', 'DESARROLLO', 'notificacionServiceJMS'),
     (1,'procedimiento.5.idTipoFicheroErrores', '', 'DESARROLLO', '35'),
     (1,'procedimiento.5.envioActivado', '', 'PREPRODUCCION', 'true'),
     (1,'procedimiento.5.servicioNotificaciones', '', 'PREPRODUCCION', 'notificacionServiceJMS'),
     (1,'procedimiento.5.idTipoFicheroErrores', '', 'PREPRODUCCION', '35'),
     (1,'procedimiento.5.envioActivado', '', 'PRODUCCION', 'true'),
     (1,'procedimiento.5.servicioNotificaciones', '', 'PRODUCCION', 'notificacionServiceJMS'),
     (1,'procedimiento.5.idTipoFicheroErrores', '', 'PRODUCCION', '35'),

     (1,'procedimiento.6.envioActivado', '', 'DESARROLLO', 'true'),
     (1,'procedimiento.6.servicioNotificaciones', '', 'DESARROLLO', 'notificacionServiceJMS'),
     (1,'procedimiento.6.idTipoFicheroErrores', '', 'DESARROLLO', '35'),
     (1,'procedimiento.6.envioActivado', '', 'PREPRODUCCION', 'true'),
     (1,'procedimiento.6.servicioNotificaciones', '', 'PREPRODUCCION', 'notificacionServiceJMS'),
     (1,'procedimiento.6.idTipoFicheroErrores', '', 'PREPRODUCCION', '35'),
     (1,'procedimiento.6.envioActivado', '', 'PRODUCCION', 'true'),
     (1,'procedimiento.6.servicioNotificaciones', '', 'PRODUCCION', 'notificacionServiceJMS'),
     (1,'procedimiento.6.idTipoFicheroErrores', '', 'PRODUCCION', '35'),

     (1,'procedimiento.7.envioActivado', '', 'DESARROLLO', 'true'),
     (1,'procedimiento.7.servicioNotificaciones', '', 'DESARROLLO', 'notificacionServiceJMS'),
     (1,'procedimiento.7.idTipoFicheroErrores', '', 'DESARROLLO', '35'),
     (1,'procedimiento.7.envioActivado', '', 'PREPRODUCCION', 'true'),
     (1,'procedimiento.7.servicioNotificaciones', '', 'PREPRODUCCION', 'notificacionServiceJMS'),
     (1,'procedimiento.7.idTipoFicheroErrores', '', 'PREPRODUCCION', '35')
     (1,'procedimiento.7.envioActivado', '', 'PRODUCCION', 'true'),
     (1,'procedimiento.7.servicioNotificaciones', '', 'PRODUCCION', 'notificacionServiceJMS'),
     (1,'procedimiento.7.idTipoFicheroErrores', '', 'PRODUCCION', '35');


