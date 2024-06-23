import mysql.connector
from config import database_config
from src import products as Products
from services import followerGenerator
import time

class Connection:
    def __init__(self):
        self.connection = None
        self.__getConnection()

    def __getConnection(self):
        if self.connection is None:
            self.connection = mysql.connector.connect(
                host=database_config["host"],
                user=database_config["user"],
                password=database_config["password"],
                database=database_config["database"]
            )
    
    def addUser(self, name, surname, username, password, email, picture, role_id):
        if self.connection is None:
            self.__getConnection()
        
        cursor = self.connection.cursor()

        try:
            cursor.execute("INSERT INTO users (name, surname, username, password, email, picture, role_id) VALUES (%s, %s, %s, %s, %s, %s, %s);",
               (name, surname, username, password, email, picture, str(role_id)))

            self.connection.commit()
        except mysql.connector.Error as err:
            print(f"Greška prilikom izvršavanja upita: {err}")

    def deleteAllDataWithoutStaticData(self):
        
        if self.connection is None:
            self.__getConnection()
        
        cursor = self.connection.cursor()

        try:
            queries = [
            "DELETE FROM deliverer_offers",
            "DELETE FROM delivery_rating",
            "DELETE FROM working_time",
            "DELETE FROM likes;", 
            "DELETE FROM follow;",
            "DELETE FROM post_comments;",
            "DELETE FROM product_comment;", 
            "DELETE FROM posts;",
            "DELETE FROM purchase_order;",
            "DELETE FROM orders;",
            "DELETE FROM product_comment;",
            "DELETE FROM products;",
            "DELETE FROM sellers;",
            "DELETE FROM drivers_licenses;",
            "DELETE FROM deliverers;",
            "DELETE FROM users;"
            ]

            for query in queries:
                cursor.execute(query)

            self.connection.commit()
        except mysql.connector.Error as err:
            print(f"Greška prilikom izvršavanja upita: {err}")

    def addStaticData(self):

        if self.connection is None:
            self.__getConnection()
        
        cursor = self.connection.cursor()

        try:
            queries = [
                "DELETE FROM role;",
                "DELETE FROM driving_categories;",
                "DELETE FROM category;",
                "DELETE FROM measurement;",
                "DELETE FROM order_status;",
                "DELETE FROM shipping_methods",
                "DELETE FROM payment_methods",
                
                "INSERT INTO role (role_id,name) VALUES (1,'User');",
                "INSERT INTO role (role_id,name) VALUES (2,'Deliverer');",
                "INSERT INTO role (role_id,name) VALUES (3,'Seller');",

                "INSERT INTO driving_categories (driving_category_id, category_name) values (1, 'CAR');",
                "INSERT INTO driving_categories (driving_category_id, category_name) values (2, 'MOTORCYCLE');",
                "INSERT INTO driving_categories (driving_category_id, category_name) values (3, 'VAN');",
                "INSERT INTO driving_categories (driving_category_id, category_name) values (4, 'TRUCK');",

                "INSERT INTO category (category_id,name,picture) VALUES (1,'Mlečni proizvodi','melcni_proizvodi.jpg');",
                "INSERT INTO category (category_id,name,picture) VALUES (2,'Voće i povrće','voce_i_povrce.jpg');",
                "INSERT INTO category (category_id,name,picture) VALUES (3,'Mesne prerađevine','mesne_prerađevine.jpg');",
                "INSERT INTO category (category_id,name,picture) VALUES (4,'Sveže meso','sveze_meso.jpg');",
                "INSERT INTO category (category_id,name,picture) VALUES (5,'Žitarice','zitarice.jpg');",
                "INSERT INTO category (category_id,name,picture) VALUES (6,'Napici','napici.jpg');",
                "INSERT INTO category (category_id,name,picture) VALUES (7,'Biljna ulja','biljna_ulja.jpg');",
                "INSERT INTO category (category_id,name,picture) VALUES (8,'Namazi','namazi.jpg');",

                "INSERT INTO measurement (measurement_id,name) VALUES (1,'KOMAD');",
                "INSERT INTO measurement (measurement_id,name) VALUES (2,'LITAR');",
                "INSERT INTO measurement (measurement_id,name) VALUES (3,'KG');",

                "INSERT INTO order_status (order_status_id, name) values (1, 'U PRIPREMI');",
                "INSERT INTO order_status (order_status_id, name) values (2, 'POSLATO');",
                "INSERT INTO order_status (order_status_id, name) values (3, 'DOSTAVLJENO');",
                "INSERT INTO order_status (order_status_id, name) values (4, 'U POTRAZI ZA DOSTAVLJACEM');",

                "INSERT INTO shipping_methods (shipping_id, shipping_method_name) values (1, 'DOSTAVLJAC')",
                "INSERT INTO shipping_methods (shipping_id, shipping_method_name) values (2, 'KURIRSKA SLUZBA')",
                "INSERT INTO shipping_methods (shipping_id, shipping_method_name) values (3, 'LICNO PREUZIMANJE')",
                
                "INSERT INTO payment_methods (payment_id, payment_method_name) values (1, 'PLACANJE UNAPRED')",
                "INSERT INTO payment_methods (payment_id, payment_method_name) values (2, 'PLACANJE POUZECEM')",
                "INSERT INTO payment_methods (payment_id, payment_method_name) values (3, 'LICNO PLACANJE')"
            ]

            for query in queries:
                cursor.execute(query)

            self.connection.commit()
        except mysql.connector.Error as err:
            print(f"Greška prilikom izvršavanja upita: {err}")

    def addNewProduct(self, seller_id):
         # product[0]-category_id ; product[1]-product_name:measurment_id ; product[2]-description
        product = Products.getRandomProduct()
        category_id     = int(product[0])
        product_name    = product[1].split(":")[0]
        measurement_id  = int(product[1].split(":")[1])
        measurement_value = ""
        if(len(product[1].split(":")) == 3):
            measurement_value = product[1].split(":")[2]
        description     = product[2]

        if self.connection is None:
            self.__getConnection()
        
        cursor = self.connection.cursor()
        
        sql = "INSERT INTO products (product_name, category_id, seller_id, measurement_id, picture, price, description, measurement_value) VALUES (%s, %s, %s, %s, %s, %s, %s, %s)"
        values = (product_name, category_id, seller_id, measurement_id, "", 100, description, measurement_value)

        try:
            cursor.execute(sql,values)
            self.connection.commit()
        except mysql.connector.Error as err:
            print(f"Greška prilikom izvršavanja upita: {err}")
            
    def addNewFollower(self, sellers_ids, users_ids):
        NUM_OF_FOLLOWS = 300
        for i in range (0, NUM_OF_FOLLOWS):
            follower = followerGenerator.SimulateFollowers(sellers_ids, users_ids)
        
            seller_id = follower.seller_id
            user_id = follower.user_id
        
            if self.connection is None:
                self.__getConnection()
            
            cursor = self.connection.cursor()
        
            sql = "INSERT INTO follow (seller_id, user_id) VALUES (%s, %s)"
            values = (seller_id, user_id)
        
            try:
                cursor.execute(sql, values)
                self.connection.commit()
                print("Novi follow")
            except mysql.connector.Error as err:
                print("Greska prilikom izvrsavanja upita: ", err)
                
