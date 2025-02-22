CREATE TABLE product (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL
);

INSERT INTO product (name, price) VALUES ('Laptop', 999.99);
INSERT INTO product (name, price) VALUES ('Phone', 699.99);