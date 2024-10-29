CREATE DATABASE shopapp;
USE shopapp;

CREATE TABLE users(
    id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(10) NOT NULL ,
    address VARCHAR(200) DEFAULT '',
    password VARCHAR(100) NOT NULL DEFAULT '',
    created_at DATETIME,
    updated_at DATETIME,
    is_active TINYINT(1) DEFAULT 1,
    date_of_birth DATE,
    facebook_account_id INT DEFAULT 0,
    google_account_id INT DEFAULT 0
);
ALTER TABLE users ADD COLUMN role_id INT;
CREATE TABLE roles(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(20) NOT NULL
);
ALTER TABLE users ADD FOREIGN KEY (role_id) REFERENCES roles(id);

CREATE TABLE tokens(
    id INT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) UNIQUE NOT NULL,
    token_type VARCHAR(50) NOT NULL,
    expiration_date DATETIME,
    revoked TINYINT(1) NOT NULL,
    expired TINYINT(1) NOT NULL,
    user_id INT ,
    FOREIGN KEY (user_id) REFERENCES users(id) 
);
CREATE TABLE social_accounts(
    id INT PRIMARY KEY AUTO_INCREMENT,
    provider VARCHAR(20) NOT NULL COMMENT 'Tên nhà social network',
    provider_id VARCHAR(50) NOT NULL,
    email VARCHAR(150) NOT NULL COMMENT 'Email tài khoản',
    name VARCHAR(100) NOT NULL COMMENT 'Tên người dùng',
    user_id INT ,
    FOREIGN KEY (user_id) REFERENCES users(id) 
);
CREATE TABLE categories(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR( 100) NOT NULL DEFAULT '' COMMENT 'Tên danh mục, vd : đồ điện tử etc...',
);
CREATE TABLE products(
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(350) COMMENT 'tên sản phẩm',
    price FLOAT NOT NULL CHECK(price >= 0), 
    thumbnail VARCHAR(300) DEFAULT '',
    description LONGTEXT DEFAULT '',
    updated_at TIMESTAMP, -- lưu trữ ngày tháng năm giờ phút giây theo múi UTC, DATETIME cũng thế nhưng không theo UTC
    created_at TIMESTAMP,
    categories_id INT,
    FOREIGN KEY (categories_id) REFERENCES categories(id)
);
CREATE TABLE product_images(
    id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    CONSTRAINT fk_product_image_product_id
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    image_url VARCHAR(300),
);
CREATE TABLE orders(
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    full_name VARCHAR(100) DEFAULT '',
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(100) DEFAULT '',
    address VARCHAR(200) NOT NULL,
    note VARCHAR(200) DEFAULT '',
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20), 
    total_money FLOAT CHECK(total_money >= 0)
);
ALTER TABLE orders ADD COLUMN shipping_method VARCHAR(100);
ALTER TABLE orders ADD COLUMN shipping_address VARCHAR(200);
ALTER TABLE orders ADD COLUMN shipping_date DATE;
ALTER TABLE orders ADD COLUMN tracking_number VARCHAR(100);
ALTER TABLE orders ADD COLUMN payment_method VARCHAR(100);
-- xóa một 1 đơn hàng => xóa mềm => thêm trường active
ALTER TABLE orders ADD COLUMN active TINYINT(1);
--chỉ có status nhận 1 trong các giá trị do ng quản trị cung cấp
ALTER TABLE orders
MODIFY COLUMN status ENUM('pending','processing','shipped','delivered','cancelled') 
COMMENT 'Trạng thái đơn';

CREATE TABLE orders_details(
    id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    products_id INT, 
    FOREIGN KEY (products_id) REFERENCES products(id),
    price FLOAT CHECK(price >= 0),
    number_of_products INT CHECK(number_of_products > 0),
    total_money FLOAT CHECK(price >= 0),
    color VARCHAR(20) DEFAULT ''
);