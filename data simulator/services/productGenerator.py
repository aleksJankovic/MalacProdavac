from models.product import Product
from src.products import getRandomProduct

def SimulateProducts(random_seller):
    product_information = getRandomProduct()
    measurement_value = ""
    if(len(product_information[1].split(":")) == 3):
        measurement_value = product_information[1].split(":")[2]
        
    product = Product()
    product.product_id         = None
    product.description        = product_information[2]
    product.measurment_value   = measurement_value
    product.picture            = None
    product.price              = product_information[3]
    product.product_name       = product_information[1].split(":")[0]
    product.category_id        = int(product_information[0])
    product.measurment_id      = int(product_information[1].split(":")[1])
    product.seller_id          = random_seller

    return product
        