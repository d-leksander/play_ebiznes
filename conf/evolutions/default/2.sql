INSERT INTO "Categories"("name") VALUES("Laptops");
INSERT INTO "Categories"("name") VALUES("Keyboards");
INSERT INTO "Categories"("name") VALUES("Mouses");
INSERT INTO "Categories"("name") VALUES("Disks");

INSERT INTO "Delivery"("type", "price") VALUES("DHL", 15);
INSERT INTO "Delivery"("type", "price") VALUES("PACZKA POCZTOWA", 12);
INSERT INTO "Photos"("path") VALUES("https://www.w3schools.com/w3css/img_avatar3.png");



INSERT INTO "Products"("name", "description", "idCategories", "price", "idDelivery", "idPhotos") VALUES("Lenovo Y700", "Laptop for gamers", 1, 5500, 2, 1);
INSERT INTO "Products"("name", "description", "idCategories", "price", "idDelivery", "idPhotos") VALUES("Sandisk 500 GB SSD", "Speed hard disk", 4, 700, 1, 1);
INSERT INTO "Products"("name", "description", "idCategories", "price", "idDelivery", "idPhotos") VALUES("Genesis G300", "Little keyboards for gamers", 2, 250, 1, 1);
INSERT INTO "Products"("name", "description", "idCategories", "price", "idDelivery", "idPhotos") VALUES("ROG SICA 520", "Smart mouse for gamers", 2, 190, 2, 1);


# --- !Downs

DELETE FROM "Categories" WHERE name="Laptops";
DELETE FROM "Categories" WHERE name="Keyboards";
DELETE FROM "Categories" WHERE name="Mouses";
DELETE FROM "Categories" WHERE name="Disks";



DELETE FROM "Delivery" WHERE type="DHL";
DELETE FROM "Delivery" WHERE type="PACZKA POCZTOWA";

DELETE FROM "Photos" WHERE path="https://www.w3schools.com/w3css/img_avatar3.png";


DELETE FROM "Products" WHERE name="Lenovo Y700";
DELETE FROM "Products" WHERE name="Sandisk 500 GB SSD";
DELETE FROM "Products" WHERE name="Genesis G300";
DELETE FROM "Products" WHERE name="ROG SICA 520";

