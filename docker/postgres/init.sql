CREATE TABLE IF NOT EXISTS products (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DOUBLE PRECISION NOT NULL CHECK (precio > 0),
    stock INT NOT NULL CHECK (stock >= 0),
    categoria VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE
);
