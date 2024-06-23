INSERT IGNORE INTO role (role_id,name) VALUES (1,'User');
INSERT IGNORE INTO role (role_id,name) VALUES (2,'Deliverer');
INSERT IGNORE INTO role (role_id,name) VALUES (3,'Seller');

INSERT IGNORE INTO category (category_id,name,picture) VALUES (1,'Mlečni proizvodi','melcni_proizvodi.jpg');
INSERT IGNORE INTO category (category_id,name,picture) VALUES (2,'Voće i povrće','voce_i_povrce.jpg');
INSERT IGNORE INTO category (category_id,name,picture) VALUES (3,'Mesne prerađevine','mesne_prerađevine.jpg');
INSERT IGNORE INTO category (category_id,name,picture) VALUES (4,'Sveže meso','sveze_meso.jpg');
INSERT IGNORE INTO category (category_id,name,picture) VALUES (5,'Žitarice','zitarice.jpg');
INSERT IGNORE INTO category (category_id,name,picture) VALUES (6,'Napici','napici.jpg');
INSERT IGNORE INTO category (category_id,name,picture) VALUES (7,'Biljna ulja','biljna_ulja.jpg');
INSERT IGNORE INTO category (category_id,name,picture) VALUES (8,'Namazi','namazi.jpg');

INSERT IGNORE INTO measurement (measurement_id,name) VALUES (1,'KOMAD');
INSERT IGNORE INTO measurement (measurement_id,name) VALUES (2,'LITAR');
INSERT IGNORE INTO measurement (measurement_id,name) VALUES (3,'KG');

INSERT IGNORE INTO deliverer_offer_status (offer_status_id, name) VALUES (1, 'PONUDA_POSLATA');
INSERT IGNORE INTO deliverer_offer_status (offer_status_id, name) VALUES (2, 'PRIHVACENA_PONUDA');
INSERT IGNORE INTO deliverer_offer_status (offer_status_id, name) VALUES (3, 'ODBIJENA_PONUDA');
INSERT IGNORE INTO deliverer_offer_status (offer_status_id, name) VALUES (4, 'POSILJKA_DOSTAVLJENA');

INSERT IGNORE INTO users (user_id,name,surname,username,password,email,picture,role_id) VALUES (1,'Bogdan','Lukic','Boki037','$2a$10$QnURZMX.ibbhUxlcMmNvcu4W9RYCmxEDzpM3/Ius8XI77sgIUtjoW','bogdan037@gmail.com','aaaa',1);
INSERT IGNORE INTO users (user_id,name,surname,username,password,email,picture,role_id) VALUES (2,'Pera','Peric','Peki034','$2a$10$A6W9fL2l8W9q0yeo6AjT0eml3Rol7zuyVpqRawFaZdhm94xSDo2Rm','pekan037@gmail.com','bbbb',2);
INSERT IGNORE INTO users (user_id,name,surname,username,password,email,picture,role_id) VALUES (3,'Aleksandra','Jankovic','aleksaleks','$2a$10$UltGnOoE5oP925RUxQ7UHuTAcBYWsQCJbIr3EFKmwLxTUC2AhDn.u','saska.jankovic19@gmail.com','tttt',3);
INSERT IGNORE INTO users (user_id,name,surname,username,password,email,picture,role_id) VALUES (4,'Mateja','Kovacevic','matejakovacevic','$2a$10$xit5sVgqsm66qkhDI8Eygea.Va0KXSxEWbMwEmOvtp411ygz53wa2','mateja.kovacevic@gmail.com','sffds',3);
INSERT IGNORE INTO users (user_id,name,surname,username,password,email,picture,role_id) VALUES (5,'Miroslav','Pekic','pekic','$2a$10$r9vW.lOOXweKo4mgOFl7P.FL7IQ4hwGb16HTDWdTjZJu2llXyYYfC','miroslav.pekic80@gmail.com','bbbb',3);
INSERT IGNORE INTO users (user_id,name,surname,username,password,email,picture,role_id) VALUES (6,'Nemanja','Vidic','vidicnemanja','$2a$10$1BN8HbVhM1ECgyUmpAuDVuCtIPLm64zlyjT6ep/cREdKUC.BEYBj6','vidic.nemanja20@gmail.com','tttt',1);
INSERT IGNORE INTO users (user_id,name,surname,username,password,email,picture,role_id) VALUES (7,'Igor','Nikodijevic','nikodijevici','$2a$10$d7.XTG9OY7CT7CJCB9b4g.UO0hQleGv/oy2FQDpgXW8jreunTAh0m','nikodijevicikg@gmail.com','tttt',3);

INSERT IGNORE INTO sellers (seller_id, pib, address, latitude, longitude)
VALUES (3, '103920327', 'Crkvinska 2', 43.944544, 20.872520);

INSERT IGNORE INTO sellers (seller_id, pib, address, latitude, longitude)
VALUES (4, '159554583', 'Tanaska Rajica 12', 44.653996, 20.263756);

INSERT IGNORE INTO sellers (seller_id, pib, address, latitude, longitude)
VALUES (5, '856235485', 'Filipa Visnjica 55', 44.277296, 19.914113);

INSERT IGNORE INTO sellers (seller_id, pib, address, latitude, longitude)
VALUES (7, '951485315', 'Ljubomira Jovanovica 70', 43.641911, 20.915729);
INSERT IGNORE INTO deliverers (deliverer_id,location,latitude,longitude) VALUES (2,'Kragujevac',20.90,44.02);

INSERT IGNORE INTO order_status (order_status_id, name) VALUES (1, 'U PRIPREMI');
INSERT IGNORE INTO order_status (order_status_id, name) VALUES (2, 'POSLATO');
INSERT IGNORE INTO order_status (order_status_id, name) VALUES (3, 'DOSTAVLJENO');
INSERT IGNORE INTO order_status (order_status_id, name) VALUES (4, 'U POTRAZI ZA DOSTAVLJACEM');

INSERT IGNORE INTO shipping_methods (shipping_id, shipping_method_name) VALUES (1, 'DOSTAVLJAC');
INSERT IGNORE INTO shipping_methods (shipping_id, shipping_method_name) VALUES (2, 'KURIRSKA SLUZBA');
INSERT IGNORE INTO shipping_methods (shipping_id, shipping_method_name) VALUES (3, 'LICNO PREUZIMANJE');

INSERT IGNORE INTO payment_methods (payment_id, payment_method_name) VALUES (1, 'PLACANJE UNAPRED');
INSERT IGNORE INTO payment_methods (payment_id, payment_method_name) VALUES (2, 'PLACANJE POUZECEM');
INSERT IGNORE INTO payment_methods (payment_id, payment_method_name) VALUES (3, 'LICNO PLACANJE');

INSERT IGNORE INTO products(product_id,product_name, category_id, seller_id, measurement_id, picture, price, description,available) values (1,'Jabuke', 2, 3, 4, 'fedf', 60, 'Naša organska jabuka je savršen spoj ukusa i zdravlja, bez pesticida i hemijskih đubriva.',true);
INSERT IGNORE INTO products(product_id,product_name, category_id, seller_id, measurement_id, picture, price, description,available) values (2,'Kruske', 2, 3, 4, 'fedf', 125, 'Naše organske kruške su sočne i prirodno uzgajane, pružajući osvežavajući ukus bez upotrebe pesticida i hemijskih đubriva.',true);
INSERT IGNORE INTO products(product_id,product_name, category_id, seller_id, measurement_id, picture, price, description,available) values (3,'Maline', 2, 3, 4, 'fedf', 390, 'Sveže organske maline - prirodno uzgojene, ukusne i bez štetnih hemikalija',true);
INSERT IGNORE INTO products(product_id,product_name, category_id, seller_id, measurement_id, picture, price, description,available) values (4,'Svinjski but', 4, 4, 4, 'fedf', 1200, 'Svinjski but - vrhunski komad mesa sa bogatim ukusom i mekoćom koji će zadovoljiti svačije gurmanluk.',true);
INSERT IGNORE INTO products(product_id,product_name, category_id, seller_id, measurement_id, picture, price, description,available) values (5,'Jagnjeci kotleti', 4, 4, 4, 'fedf', 2420, 'Jagnjeći kotleti - delikatesno jelo s neodoljivim ukusom i sočnošću koji će vas oduševiti.',true);
INSERT IGNORE INTO products(product_id,product_name, category_id, seller_id, measurement_id, picture, price, description,available) values (6,'Guscja jaja', 1, 5, 3, 'fedf', 20, 'Gusčja jaja - prirodna poslastica s bogatim i intenzivnim ukusom, savršena za različite kulinarske kreacije.',true);

INSERT IGNORE INTO orders (seller_id, latitude_buyer, longitude_buyer, order_date, buyer_id, order_status_id,deliverer_id) VALUES (3, 45.5, 41.5, "2023-10-30 18:23:00.000000", 1, 1,2);
INSERT IGNORE INTO orders (seller_id, latitude_buyer, longitude_buyer, order_date, buyer_id, order_status_id) VALUES (4, 45.5, 41.5, "2023-10-24 18:23:00.000000", 1, 2);
INSERT IGNORE INTO orders (seller_id, latitude_buyer, longitude_buyer, order_date, buyer_id, order_status_id) VALUES (5, 45.5, 41.5, "2023-11-04 18:23:00.000000", 1, 2);
INSERT IGNORE INTO orders (seller_id, latitude_buyer, longitude_buyer, order_date, buyer_id, order_status_id) VALUES (3, 45.5, 41.5, "2023-11-01 18:23:00.000000", 1, 3);
INSERT IGNORE INTO orders (seller_id, latitude_buyer, longitude_buyer, order_date, buyer_id, order_status_id) VALUES (4, 45.5, 41.5, "2023-11-03 18:23:00.000000", 1, 3);
INSERT IGNORE INTO orders (seller_id, latitude_buyer, longitude_buyer, order_date, buyer_id, order_status_id) VALUES (5, 45.5, 41.5, "2023-11-01 18:23:00.000000", 6, 2);
INSERT IGNORE INTO orders (seller_id, latitude_buyer, longitude_buyer, order_date, buyer_id, order_status_id) VALUES (3, 45.5, 41.5, "2023-10-27 18:23:00.000000", 1, 2);
INSERT IGNORE INTO orders (seller_id, latitude_buyer, longitude_buyer, order_date, buyer_id, order_status_id) VALUES (4, 45.5, 41.5, "2023-10-15 18:23:00.000000", 6, 1);
INSERT IGNORE INTO orders (seller_id, latitude_buyer, longitude_buyer, order_date, buyer_id, order_status_id) VALUES (4, 45.5, 41.5, "2023-11-03 18:23:00.000000", 67, 1);

INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (1, 1, 2);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (1, 2, 1);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (2, 4, 3);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (3, 4, 2);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (4, 4, 5);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (5, 6, 10);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (6, 4, 2);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (6, 5, 3);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (7, 1, 2);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (7, 3, 3);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (7, 2, 1);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (8, 4, 2);
INSERT IGNORE INTO purchase_order (order_id, product_id, quantity) VALUES (9, 5, 1);

INSERT IGNORE INTO products (product_id, description, picture, price, product_name, category_id, measurement_id, seller_id,available) VALUES (7,'Sveze i ukusno','aaa',200,'Jabuke',2,4,3,true);
INSERT IGNORE INTO products (product_id, description, picture, price, product_name, category_id, measurement_id, seller_id,available) VALUES (8,'Sveze i ukusno','aaa',250,'Jaja',1,3,3,true);
INSERT IGNORE INTO products (product_id, description, picture, price, product_name, category_id, measurement_id, seller_id,available) VALUES (9,'Sveze i ukusno','aaa',290,'Kruske',2,4,3,true);
INSERT IGNORE INTO products (product_id, description, picture, price, product_name, category_id, measurement_id, seller_id,available) VALUES (10,'Sveze i ukusno','aaa',300,'Jagode',2,4,3,true);

INSERT IGNORE INTO product_comment (product_comment_id,date, grade, text, product_id, user_id) VALUES (1,null,5,'Vrlo dobar proizvod',4,2);
INSERT IGNORE INTO product_comment (product_comment_id,date, grade, text, product_id, user_id) VALUES (2,null,4,'Zadovoljan sam proizvodom. Zadovoljan sam proizvodom',4,1);
INSERT IGNORE INTO product_comment (product_comment_id,date, grade, text, product_id, user_id) VALUES (3,null,3,'Prosli put su bile bolje',5,2);

INSERT IGNORE INTO follow(user_id,seller_id) VALUES (1,3);
INSERT IGNORE INTO follow(user_id,seller_id) VALUES (1,4);
INSERT IGNORE INTO follow(user_id,seller_id) VALUES (1,5);
INSERT IGNORE INTO follow(user_id,seller_id) VALUES (1,7);
INSERT IGNORE INTO follow(user_id,seller_id) VALUES (2,3);
INSERT IGNORE INTO follow(user_id,seller_id) VALUES (4,3);
INSERT IGNORE INTO follow(user_id,seller_id) VALUES (5,3);
INSERT IGNORE INTO follow(user_id,seller_id) VALUES (4,3);

INSERT IGNORE INTO posts(post_id, date_time, text, seller_id) VALUES (1,'2023-10-30 18:23:00.000000','Pozdrav svima! Obaveštavamo vas da ponovo imamo ceo asortiman u ponudi. Vidimo se na dogovorenom mestu uskoro <3!',3);
INSERT IGNORE INTO posts(post_id, date_time, text, seller_id) VALUES (2,'2023-08-30 18:23:00.000000','Dragi naši kupci, obaveštavamo vas da ćemo se sutra videti ponovo na Kalenić pijaci na starom mestu. Pozdrav svima !',3);
INSERT IGNORE INTO posts(post_id, date_time, text, seller_id) VALUES (3,'2023-11-14 18:25:00.000000','treca objava',3);
INSERT IGNORE INTO posts(post_id, date_time, text, seller_id) VALUES (4,'2023-09-13 18:28:00.000000','cetvrta objava',3);
INSERT IGNORE INTO posts(post_id, date_time, text, seller_id) VALUES (5,'2023-05-03 18:29:00.000000','peta objava',4);
INSERT IGNORE INTO posts(post_id, date_time, text, seller_id) VALUES (6,'2023-07-13 18:23:00.000000','sesta objava',4);
INSERT IGNORE INTO posts(post_id, date_time, text, seller_id) VALUES (7,'2023-11-13 18:20:00.000000','sedma objava',3);
INSERT IGNORE INTO posts(post_id, date_time, text, seller_id) VALUES (8,'2023-01-03 18:23:00.000000','osma objava',3);
INSERT IGNORE INTO posts(post_id, date_time, text, seller_id) VALUES (9,'2023-10-30 16:23:00.000000','Pozdrav svima! Obaveštavamo vas da ponovo imamo ceo asortiman u ponudi. Vidimo se na dogovorenom mestu uskoro <3!',3);


INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (1,1,'2023-10-30 18:23:00.000000');
INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (1,2,'2023-11-25 18:23:00.000000');
INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (4,2,'2023-11-24 18:23:00.000000');
INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (1,4,'2023-11-22 18:23:00.000000');
INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (1,6,'2023-11-20 18:23:00.000000');
INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (2,3,'2023-11-21 18:23:00.000000');
INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (2,4,'2023-11-22 18:23:00.000000');
INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (8,1,'2023-11-22 18:23:00.000000');
INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (7,1,'2023-11-25 18:23:00.000000');
INSERT IGNORE INTO likes(post_id, user_id, date_time) VALUES (4,1,'2023-11-23 18:23:00.000000');

INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (1,'2023-11-24 18:23:00.000000','super',1,1);
INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (2,'2023-11-27 18:23:00.000000','steta',2,1);
INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (3,'2023-11-25 18:23:00.000000','odlicno',3,2);
INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (4,'2023-11-25 18:23:00.000000','super',5,2);
INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (5,'2023-11-25 18:23:00.000000','super',4,1);
INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (6,'2023-11-24 18:23:00.000000','vau',3,1);
INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (7,'2023-11-23 18:23:00.000000','super',1,4);
INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (8,'2023-11-25 18:23:00.000000','bas dobre vesti',1,5);
INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (9,'2023-11-25 18:23:00.000000','sjajno',2,4);
INSERT IGNORE INTO post_comments(post_comment_id, date_time, text, post_id, user_id) VALUES (10,'2023-11-23 18:23:00.000000','lepo',8,1);

INSERT IGNORE INTO driving_categories (driving_category_id, category_name) values (1, 'CAR');
INSERT IGNORE INTO driving_categories (driving_category_id, category_name) values (2, 'MOTORCYCLE');
INSERT IGNORE INTO driving_categories (driving_category_id, category_name) values (3, 'VAN');
INSERT IGNORE INTO driving_categories (driving_category_id, category_name) values (4, 'TRUCK');


INSERT IGNORE INTO drivers_licenses (user_id, driving_category_id) VALUES (2, 2);
INSERT IGNORE INTO drivers_licenses (user_id, driving_category_id) VALUES (2, 3);

INSERT IGNORE INTO delivery_rating (id, comment, grade, deliverer_id, customer_id) VALUES (1, "Brza i efikasna dostava.", 5, 38, 12);
INSERT IGNORE INTO delivery_rating (id, comment, grade, deliverer_id, customer_id) VALUES (2, "Brza i efikasna dostava.", 4, 38, 13);
INSERT IGNORE INTO delivery_rating (id, comment, grade, deliverer_id, customer_id) VALUES (3, "Brza i efikasna dostava.", 5, 38, 14);
INSERT IGNORE INTO delivery_rating (id, comment, grade, deliverer_id, customer_id) VALUES (4, "Brza i efikasna dostava.", 3, 38, 15);
INSERT IGNORE INTO delivery_rating (id, comment, grade, deliverer_id, customer_id) VALUES (5, "Brza i efikasna dostava.", 2, 38, 16);
INSERT IGNORE INTO delivery_rating (id, comment, grade, deliverer_id, customer_id) VALUES (6, "Brza i efikasna dostava.", 1, 38, 17);
INSERT IGNORE INTO delivery_rating (id, comment, grade, deliverer_id, customer_id) VALUES (7, "Brza i efikasna dostava.", 5, 38, 18);
INSERT IGNORE INTO delivery_rating (id, comment, grade, deliverer_id, customer_id) VALUES (8, "Brza i efikasna dostava.", 3, 38, 19);
INSERT IGNORE INTO delivery_rating (id, comment, grade, deliverer_id, customer_id) VALUES (9, "Brza i efikasna dostava.", 5, 38, 20);



INSERT IGNORE INTO deliverer_offers(offer_id,order_id,deliverer_id,price,date_time,comment,offer_status_id) VALUES (1,1,2,250,null,null,1);
INSERT IGNORE INTO deliverer_offers(offer_id,order_id,deliverer_id,price,date_time,comment,offer_status_id) VALUES (2,2,2,250,null,null,2);
INSERT IGNORE INTO deliverer_offers(offer_id,order_id,deliverer_id,price,date_time,comment,offer_status_id) VALUES (3,3,2,250,null,null,1);
INSERT IGNORE INTO deliverer_offers(offer_id,order_id,deliverer_id,price,date_time,comment,offer_status_id) VALUES (4,4,2,350,null,null,1);
INSERT IGNORE INTO deliverer_offers(offer_id,order_id,deliverer_id,price,date_time,comment,offer_status_id) VALUES (5,5,2,250,null,null,1);
