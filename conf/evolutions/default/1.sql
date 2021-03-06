# --- !Ups



CREATE TABLE "Photos" (
  "idPhotos" INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
  "path" VARCHAR NOT NULL
);

CREATE TABLE "Categories" (
  "idCategories" INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
  "name" VARCHAR NOT NULL
);

CREATE TABLE "Delivery" (
  "idDelivery" INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
  "type" VARCHAR NOT NULL,
  "price" INTEGER NOT NULL
);



CREATE TABLE "Products" (
  "idProducts" INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
  "name" VARCHAR NOT NULL,
  "description" TEXT NOT NULL,
  "idCategories" INTEGER NOT NULL,
  "price" INTEGER NOT NULL,
  "idDelivery" INTEGER NOT NULL,
  "idPhotos" INTEGER NOT NULL,
FOREIGN KEY(idCategories) references Categories(idCategories) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY(idDelivery) references Delivery(idDelivery) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY(idPhotos) references Photos(idPhotos) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "Baskets" (
  "idBaskets" INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
  "idUsers" VARCHAR NOT NULL,
  "idProducts" INTEGER NOT NULL,
FOREIGN KEY(idUsers) references Users(id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY(idProducts) references Products(idProducts) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "Orders" (
  "idOrders" INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
  "date" DATE NOT NULL,
  "idUsers" VARCHAR NOT NULL,
  "idProducts" INTEGER NOT NULL,
FOREIGN KEY(idUsers) references Users(id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY(idProducts) references Products(idProducts) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "Payments" (
  "idPayments" INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
  "status" VARCHAR NOT NULL,
  "date" DATE NOT NULL,
  "idUsers" VARCHAR NOT NULL,
  "value" INTEGER NOT NULL,
FOREIGN KEY(idUsers) references Users(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE TABLE "Favourites" (
  "idFavourites" INTEGER NOT NULL UNIQUE PRIMARY KEY AUTOINCREMENT,
  "idUsers" VARCHAR NOT NULL,
  "idProducts" INTEGER NOT NULL,
FOREIGN KEY(idUsers) references Users(id) ON UPDATE CASCADE ON DELETE CASCADE,
FOREIGN KEY(idProducts) references Products(idProducts) ON UPDATE CASCADE ON DELETE CASCADE
);

# --- !Downs

DROP TABLE "Photos"
DROP TABLE "Categories"
DROP TABLE "Delivery"
DROP TABLE "Products"
DROP TABLE "Baskets"
DROP TABLE "Orders"
DROP TABLE "Payments"
DROP TABLE "Favourites"
