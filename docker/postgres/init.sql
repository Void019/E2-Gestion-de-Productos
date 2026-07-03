CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DOUBLE PRECISION NOT NULL CHECK (precio > 0),
    stock INT NOT NULL CHECK (stock >= 0),
    categoria VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

INSERT INTO products (nombre, descripcion, precio, stock, categoria, activo)
SELECT 'Notebook Lenovo IdeaPad 3', 'Notebook 15.6 pulgadas con 8GB RAM y SSD de 512GB', 549990, 12, 'Computacion', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE nombre = 'Notebook Lenovo IdeaPad 3'
);

INSERT INTO products (nombre, descripcion, precio, stock, categoria, activo)
SELECT 'Mouse Logitech M280', 'Mouse inalambrico ergonomico para uso diario', 19990, 45, 'Accesorios', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE nombre = 'Mouse Logitech M280'
);

INSERT INTO products (nombre, descripcion, precio, stock, categoria, activo)
SELECT 'Teclado Redragon Kumara', 'Teclado mecanico compacto con switches blue', 42990, 20, 'Accesorios', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE nombre = 'Teclado Redragon Kumara'
);

INSERT INTO products (nombre, descripcion, precio, stock, categoria, activo)
SELECT 'Monitor Samsung 24', 'Monitor Full HD de 24 pulgadas con panel IPS', 139990, 9, 'Monitores', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE nombre = 'Monitor Samsung 24'
);

INSERT INTO products (nombre, descripcion, precio, stock, categoria, activo)
SELECT 'Audifonos JBL Tune 510BT', 'Audifonos bluetooth con bateria de larga duracion', 34990, 30, 'Audio', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM products WHERE nombre = 'Audifonos JBL Tune 510BT'
);
