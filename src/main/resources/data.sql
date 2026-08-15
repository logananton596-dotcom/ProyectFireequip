-- 1. INSERTAR ROLES BÁSICOS
-- Es fundamental que empiecen con ROLE_ para que coincidan con SecurityConfig
INSERT INTO rol (nombre) VALUES ('ROLE_ADMIN');
INSERT INTO rol (nombre) VALUES ('ROLE_USER');
--INSERT INTO rol (nombre) VALUES ('ROLE_USER');

-- 2. INSERTAR ESTADOS DE EQUIPO
INSERT INTO estado_equipo (nombre) VALUES ('OPERATIVO');
INSERT INTO estado_equipo (nombre) VALUES ('EN_MANTENIMIENTO');
INSERT INTO estado_equipo (nombre) VALUES ('DADO_BAJA');
INSERT INTO estado_equipo (nombre) VALUES ('FUERA_DE_SERVICIO');

--INSERT INTO bombero (estado) VALUES ('ACTIVO');
--INSERT INTO bombero (estado) VALUES ('RESERVA ');
--INSERT INTO bombero (estado) VALUES ('BAJA');


-- 3. INSERTAR TIPOS DE EQUIPO
INSERT INTO tipo_equipo (nombre) VALUES ('Vehículo');
INSERT INTO tipo_equipo (nombre) VALUES ('Herramienta Manual');
INSERT INTO tipo_equipo (nombre) VALUES ('Equipo de Protección Personal');
INSERT INTO tipo_equipo (nombre) VALUES ('Equipo de Rescate');

-- 4. INSERTAR USUARIO ADMINISTRADOR INICIAL
-- La contraseña es 'admin123' encriptada con BCrypt
-- Importante: El ID del rol ADMIN suele ser 1

--INSERT INTO usuario (username, password, activo, rol_id) 
--VALUES ('admin', '$2a$10$7R4M8Hw0iQzX1vS9yE3uOe1aK3vV7Hn6X5t9u7h3y6wR93O2l1vG.', true, 1);

-- 5. INSERTAR USOS DE EMERGENCIA (OPCIONAL)
INSERT INTO uso_emergencia (nombre) VALUES ('Incendio Estructural');
INSERT INTO uso_emergencia (nombre) VALUES ('Rescate Vehicular');
INSERT INTO uso_emergencia (nombre) VALUES ('Incendio Forestal');

--insercion de datos

--INSERT INTO area (nombre, encargado, telefono) VALUES 
--('Estación Central - Sistemas', 'Capitán Juan Pérez', '+51987654321'),
--('Logística y Almacén', 'Teniente Ana Gómez', '+51987654322'),
--('Compañía de Rescate', 'Subteniente Luis Merlo', NULL),
--('Compañía B - Ataque', 'Oficial Carlos Torres', '+51987654324'),
--('Mantenimiento de Equipos', 'Sargento Raúl Silva', NULL);


--INSERT INTO equipo (autorizado_por, codigo_interno, created_at, fecha_baja, fecha_compra, marca, modelo, motivo_baja, nombre, numero_serie, ubicacion_actual, updated_at, vida_util_anios, area_id, estado_id, tipo_id) VALUES
--('Comandante Silva', 'BOM-001', NOW(), NULL, '2026-01-14', 'Scott Safety', 'Air-Pak X3 Pro', NULL, 'Equipo de Respiración Autónoma (ERA)', 'SCOTT-9921-X', 'Pañol de Equipos - Estación 1', NOW(), 10, 1, 1, 3),
--('Oficial Martínez', 'BOM-002', NOW(), '2026-02-20', '2016-05-10', 'Innotex', 'Energy Turnout', 'Caducidad de tejido estructural', 'Traje Estructural (Chaqueta y Jardonera)', 'INNO-4421-T', 'Almacén de Desecho', NOW(), 10, 2, 3, 3),
--('Comandante Silva', 'BOM-003', NOW(), NULL, '2026-01-30', 'Holmatro', 'Pentheon PCU50', NULL, 'Cizalla Hidráulica de Rescate', 'HOLM-7732-C', 'Unidad de Rescate R-1', NOW(), 15, 3, 1, 4),
--('Oficial Torres', 'BOM-004', NOW(), NULL, '2024-11-10', 'MSA', 'Gallet F1XF', NULL, 'Casco Estructural de Bombero', 'MSA-F1XF-883', 'Casillero 14 - Compañía B', NOW(), 8, 4, 1, 3),
--('Comandante Silva', 'BOM-005', NOW(), NULL, '2026-02-28', 'Akron Brass', 'Turbojet 1720', NULL, 'Pitón / Lanza de Caudal Variable', 'AKRON-1720-P', 'Camión de Ataque B-1', NOW(), 12, 1, 1, 2),
--('Oficial Martínez', 'BOM-006', NOW(), '2026-01-05', '2023-08-15', 'Key Hose', 'Combat Ready 1.75"', 'Corte severo por escombros', 'Manguera de Ataque 1.75 pulg (30m)', 'KEY-175-092', 'Taller de Reparaciones', NOW(), 5, 5, 3, 2),
--('Oficial Torres', 'BOM-007', NOW(), NULL, '2026-04-15', 'FLIR', 'K33', NULL, 'Cámara Térmica de Rescate', 'FLIR-K33-7712', 'Unidad de Rescate R-1', NOW(), 7, 3, 1, 4),
--('Comandante Silva', 'BOM-008', NOW(), NULL, '2025-01-08', 'Hale', 'Prima P35', NULL, 'Bomba Contra Incendios Estacionaria', 'HALE-P35-990', 'Sala de Máquinas - Estación 2', NOW(), 20, 1, 1, 4),
--('Oficial Martínez', 'BOM-009', NOW(), NULL, '2026-04-28', 'Streamlight', 'Vantage LED', NULL, 'Linterna de Casco Recargable', 'STREAM-VANT-44', 'Casillero 22 - Compañía A', NOW(), 4, 4, 1, 3),
--('Oficial Torres', 'BOM-010', NOW(), '2026-03-15', '2022-03-10', 'STIHL', 'MS 462 C-M', 'Daño crítico en motor por uso extremo', 'Motosierra de Ventilación y Rescate', 'STIHL-MS462-8', 'Almacén Central', NOW(), 6, 2, 3, 4);
