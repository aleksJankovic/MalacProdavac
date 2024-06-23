class Product:
    def __init__(self):
        self.product_id         = None
        self.description        = None
        self.measurment_value   = None
        self.picture            = None
        self.price              = None
        self.product_name       = None
        self.category_id        = None
        self.measurment_id      = None
        self.seller_id          = None

    def __str__(self):
        return f"""
    {{
        "product_name":"{self.product_name}",
        "category_id": {self.category_id},
        "seller_id": {self.seller_id},
        "measurement_id":{self.measurment_id},
        "picture":"",
        "price":{self.price},
        "description":"{self.description}"
    }}
    """

