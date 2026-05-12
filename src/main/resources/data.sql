-- 1. INSERTAR ROLES BÁSICOS
-- Es fundamental que empiecen con ROLE_ para que coincidan con SecurityConfig
INSERT INTO rol (nombre) VALUES ('ROLE_ADMIN');
INSERT INTO rol (nombre) VALUES ('ROLE_USER');

-- 2. INSERTAR ESTADOS DE EQUIPO
INSERT INTO estado_equipo (nombre) VALUES ('OPERATIVO');
INSERT INTO estado_equipo (nombre) VALUES ('EN_MANTENIMIENTO');
INSERT INTO estado_equipo (nombre) VALUES ('DADO_BAJA');
INSERT INTO estado_equipo (nombre) VALUES ('FUERA_DE_SERVICIO');

-- 3. INSERTAR TIPOS DE EQUIPO
INSERT INTO tipo_equipo (nombre) VALUES ('Vehículo');
INSERT INTO tipo_equipo (nombre) VALUES ('Herramienta Manual');
INSERT INTO tipo_equipo (nombre) VALUES ('Equipo de Protección Personal');
INSERT INTO tipo_equipo (nombre) VALUES ('Equipo de Rescate');

-- 4. INSERTAR USUARIO ADMINISTRADOR INICIAL
-- La contraseña es 'admin123' encriptada con BCrypt
-- Importante: El ID del rol ADMIN suele ser 1
INSERT INTO usuario (username, password, activo, rol_id) 
VALUES ('admin', '$2a$10$Xptf.dFhL.MuxlHq8AAsmOAtXJpYInlAnGvI/G0OtoV2sTzXp3YyG', true, 1);

-- 5. INSERTAR USOS DE EMERGENCIA (OPCIONAL)
INSERT INTO uso_emergencia (nombre) VALUES ('Incendio Estructural');
INSERT INTO uso_emergencia (nombre) VALUES ('Rescate Vehicular');
INSERT INTO uso_emergencia (nombre) VALUES ('Incendio Forestal');