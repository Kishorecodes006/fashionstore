-- Categories
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    slug VARCHAR(50) UNIQUE NOT NULL
);

-- Users
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(15),
    role ENUM('USER','ADMIN') DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Products
CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    image_url VARCHAR(255),
    category_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Orders
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED') DEFAULT 'PENDING',
    address TEXT NOT NULL,
    payment_mode VARCHAR(20) DEFAULT 'COD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Order Items
CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Cart Items
CREATE TABLE cart_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Sample Categories
INSERT INTO categories (name, slug) VALUES
('Men', 'men'),
('Women', 'women'),
('Kids', 'kids'),
('Accessories', 'accessories');

-- Sample Admin User
INSERT INTO users (name, email, password, phone, role) VALUES
('Admin User', 'admin@fashionstore.com', 'admin123', '9876543210', 'ADMIN');

-- Sample Products
INSERT INTO products (name, description, price, stock, image_url, category_id) VALUES
('Classic White Shirt', 'Premium cotton formal shirt', 899.00, 50, 'https://via.placeholder.com/300x260?text=White+Shirt', 1),
('Blue Denim Jeans', 'Slim fit comfortable jeans', 1299.00, 35, 'https://via.placeholder.com/300x260?text=Denim+Jeans', 1),
('Floral Summer Dress', 'Light and breezy summer dress', 1499.00, 25, 'https://via.placeholder.com/300x260?text=Summer+Dress', 2),
('Women Kurti Set', 'Elegant ethnic wear kurti', 999.00, 40, 'https://via.placeholder.com/300x260?text=Kurti+Set', 2),
('Kids T-Shirt Pack', 'Colorful cotton t-shirts for kids', 599.00, 60, 'https://via.placeholder.com/300x260?text=Kids+TShirt', 3),
('Leather Belt', 'Genuine leather formal belt', 449.00, 80, 'https://via.placeholder.com/300x260?text=Leather+Belt', 4),
('Canvas Tote Bag', 'Eco-friendly stylish tote bag', 699.00, 45, 'https://via.placeholder.com/300x260?text=Tote+Bag', 4),
('Men Polo T-Shirt', 'Casual polo collar t-shirt', 749.00, 55, 'https://via.placeholder.com/300x260?text=Polo+Shirt', 1);