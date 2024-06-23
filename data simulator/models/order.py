import json 

class Order:
    def __init__(self):
        #Buyer details 
        self.buyerId = None
        self.buyerLongitude = None
        self.buyerLatitude = None
        self.address = None
        self.phoneNumber = None
        
        # Seller details
        self.sellerId = None
        
        self.orderStatusId = None
        self.shippingMethodId = None
        self.paymentMethodId = None
        
        self.purchase = []  # List of SimulatorOrderItemsRequest objects
        
    def __str__(self):
        string = ""
        for i in range (0, len(self.purchase)):
            string += self.purchase[i].__str__() + ","
        #Uklanjanje poslednjeg zareza iz stringa 
        string = string[:-1]
        
        return f"""
        {{
            "buyerId": {self.buyerId}, 
            "buyerLongitude": {self.buyerLongitude}, 
            "buyerLatitude": {self.buyerLatitude}, 
            "sellerId": {self.sellerId}, 
            "orderStatusId": {self.orderStatusId}, 
            "paymentMethodId": {self.paymentMethodId},
            "shippingMethodId": {self.shippingMethodId},
            "phoneNumber": "{self.phoneNumber}",
            "address": "{self.address}",
            "purchase": [{string}]
        }}
    """

        
class SimulatorOrderItemsRequest:
    def __init__(self):
        self.productId = None
        self.quantity = None
    
    def __str__(self):
        return f'{{"productId": {self.productId}, "quantity": {self.quantity}}}'
