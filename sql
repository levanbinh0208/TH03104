CREATE DATABASE PhoneStore;
GO

USE PhoneStore;
GO

CREATE TABLE Users (
    UserId INT IDENTITY(1,1) PRIMARY KEY,
    FullName NVARCHAR(100) NOT NULL,
    UserName VARCHAR(100) UNIQUE NOT NULL,
    Email NVARCHAR(150) UNIQUE NOT NULL,
    PasswordHash NVARCHAR(255) NOT NULL,
    Phone NVARCHAR(20),
    Address NVARCHAR(255),
    RoleName NVARCHAR(20) DEFAULT 'CUSTOMER',
    AvatarUrl NVARCHAR(255) NULL,
    CreatedAt DATETIME DEFAULT GETDATE()
);

CREATE TABLE Products (
    ProductId INT IDENTITY(1,1) PRIMARY KEY,
    ProductName NVARCHAR(200) NOT NULL,
    Description NVARCHAR(MAX),
    Price DECIMAL(18,2) NOT NULL,
    Quantity INT NOT NULL DEFAULT 0,
    ImageUrl NVARCHAR(500),
    CategoryId INT,
    IsFeatured BIT DEFAULT 0,
    IsDeleted BIT NOT NULL DEFAULT 0,
    CreatedAt DATETIME DEFAULT GETDATE(),

    CONSTRAINT FK_Product_Category
        FOREIGN KEY (CategoryId)
        REFERENCES Categories(CategoryId)
);

CREATE TABLE CartItems (
    CartItemId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT NOT NULL,
    ProductId INT NOT NULL,
    Quantity INT NOT NULL DEFAULT 1,

    CONSTRAINT FK_CartItem_User
        FOREIGN KEY (UserId)
        REFERENCES Users(UserId),

    CONSTRAINT FK_CartItem_Product
        FOREIGN KEY (ProductId)
        REFERENCES Products(ProductId),

    CONSTRAINT UQ_CartItem_User_Product
        UNIQUE (UserId, ProductId)
);

CREATE TABLE Orders (
    OrderId INT IDENTITY(1,1) PRIMARY KEY,
    UserId INT NOT NULL,
    TotalAmount DECIMAL(18,2) NOT NULL,
    Status NVARCHAR(30) DEFAULT 'PENDING',
    OrderDate DATETIME DEFAULT GETDATE(),
    ReceiverName NVARCHAR(100),
    Phone NVARCHAR(20),
    ShippingAddress NVARCHAR(255),
    PaymentMethod NVARCHAR(20),

    CONSTRAINT FK_Order_User
        FOREIGN KEY (UserId)
        REFERENCES Users(UserId)
);

CREATE TABLE OrderDetails (
    OrderDetailId INT IDENTITY(1,1) PRIMARY KEY,
    OrderId INT NOT NULL,
    ProductId INT NOT NULL,
    Quantity INT NOT NULL,
    UnitPrice DECIMAL(18,2) NOT NULL,

    CONSTRAINT FK_OrderDetail_Order
        FOREIGN KEY (OrderId)
        REFERENCES Orders(OrderId),

    CONSTRAINT FK_OrderDetail_Product
        FOREIGN KEY (ProductId)
        REFERENCES Products(ProductId)
);

CREATE TABLE password_reset_tokens (
    id BIGINT PRIMARY KEY IDENTITY(1,1),
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date DATETIME2 NOT NULL,

    CONSTRAINT fk_password_reset_tokens_user
        FOREIGN KEY (user_id)
        REFERENCES Users(UserId)
        ON DELETE CASCADE
);