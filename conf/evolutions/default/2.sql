INSERT INTO "Categories"("name") VALUES("Laptops");
INSERT INTO "Categories"("name") VALUES("Keyboards");
INSERT INTO "Categories"("name") VALUES("Mouses");
INSERT INTO "Categories"("name") VALUES("Disks");

INSERT INTO "Users"("password", "email") VALUES("Password", "darek@gmail.com");
INSERT INTO "Users"("password", "email") VALUES("Password", "jan@gmail.com");
INSERT INTO "Users"("password", "email") VALUES("Password", "jacek@gmail.com");
INSERT INTO "Users"("password", "email") VALUES("Password", "anna@gmail.com");


INSERT INTO "Products"("name", "description", "idCategories", "price") VALUES("Lenovo Y700", "Laptop for gamers", 1, 5500);
INSERT INTO "Products"("name", "description", "idCategories", "price") VALUES("Sandisk 500 GB SSD", "Speed hard disk", 4, 700);
INSERT INTO "Products"("name", "description", "idCategories", "price") VALUES("Genesis G300", "Little keyboards for gamers", 2, 250);
INSERT INTO "Products"("name", "description", "idCategories", "price") VALUES("ROG SICA 520", "Smart mouse for gamers", 2, 190);


# --- !Downs

DELETE FROM "Categories" WHERE name="Laptops";
DELETE FROM "Categories" WHERE name="Keyboards";
DELETE FROM "Categories" WHERE name="Mouses";
DELETE FROM "Categories" WHERE name="Disks";

DELETE FROM "Users" WHERE email="darek@gmail.com";
DELETE FROM "Users" WHERE email="jan@gmail.com";
DELETE FROM "Users" WHERE email="jacek@gmail.com";
DELETE FROM "Users" WHERE email="anna@gmail.com";

DELETE FROM "Products" WHERE name="Lenovo Y700";
DELETE FROM "Products" WHERE name="Sandisk 500 GB SSD";
DELETE FROM "Products" WHERE name="Genesis G300";
DELETE FROM "Products" WHERE name="ROG SICA 520";

