import random 
from models import order

LATITUDE_LOWER_LIMIT = 41.85
LATITUDE_UPPER_LIMIT = 46.18

LONGITUDE_LOWER_LIMIT = 18.83
LONGITUDE_UPPER_LIMIT = 23

ORDER_STATUS_IDS = [1, 2, 3, 4]
probabilities = [0.3, 0.3, 0.3, 0.1] 

ORDER_SHIPPING_AND_PAYMENT_METHODS = [1, 2, 3]

PHONE_NUMBERS = [
    "0651122334", "0694455667", "0617788990", "0643344556", "0631122334",
    "0607788990", "0659900112", "0693344556", "0611122334", "0647788990",
    "0633344556", "0609900112", "0654455667", "0697788990", "0613344556",
    "0641122334", "0637788990", "0603344556", "0657788990", "0691122334",
    "0614455667", "0647788990", "0633344556", "0601122334", "0657788990",
    "0699900112", "0613344556", "0641122334", "0637788990", "0603344556",
    "0659900112", "0691122334", "0617788990", "0643344556", "0631122334",
    "0607788990", "0654455667", "0697788990", "0613344556", "0641122334",
    "0637788990", "0603344556", "0659900112", "0691122334", "0614455667",
    "0647788990", "0633344556", "0601122334", "0657788990", "0699900112"
]

ADDRESSES = [
    "Kragujevac, Radoja Domanovica",
    "Kragujevac, Zorana Đinđića",
    "Kragujevac, Ljubomira Jovanovića",
    "Kragujevac, Resavska",
    "Kragujevac, Ramaćka",
    "Kragujevac, Milovana Glišića",
    "Kragujevac, Bulevar Kraljice Marije",
    "Kragujevac, Tanaska Rajića",
    "Kragujevac, Filipa Višnjića",
    "Kragujevac, Belička",
    "Kragujevac, Grada Sirena",
    "Beograd, Bohinjska",
    "Beograd, Branka Đonovića",
    "Beograd, Branka Miljkovića",
    "Beograd, Branka Šotre",
    "Beograd, Bregalnička",
    "Beograd, Veljka Petrovića",
    "Beograd, Vizantijska",
    "Beograd, Vlašićka",
    "Beograd, Generala Vasića",
    "Beograd, Grigora Viteza",
    "Beograd, Divčibarska",
    "Beograd, Diplomatska Kolonija",
    "Zemun, Đure Đakovića",
    "Beograd, Đure Jakšića",
    "Beograd, Egejska",
    "Beograd, Žarka Zrenjanina",
    "Zemun, Zagorska",
    "Beograd, Jedranska",
    "Beograd, Kačićeva",
    "Beograd, Knez Mihajlova",
    "Beograd, Kneza Miloša",
    "Beograd, Lopudska",
    "Beograd, Ljutice Bogdana",
    "Beograd, Majska",
    "Novi Sad, Beogradski kej",
    "Novi Sad, Branka Bajića",
    "Novi Sad, Temerinska",
    "Novi Sad, Futoški put",
    "Novi Sad, Rumenački put",
    "Novi Sad, Novosadski put",
    "Novi Sad, Jevrejska",
    "Novi Sad, Gimnazijska",
    "Novi Sad, Bulevar kralja Petra I",
    "Novi Sad, Braće Popovića",
    "Novi Sad, Branka Ćopića",
    "Novi Sad, Zlatne grede",
    "Novi Sad, Jovana Subotića",
    "Niš, 13.maj",
    "Niš, 1300 Kaplara",
    "Niš, 25.maj",
    "Niš, Avalska",
    "Niš, Apatinska",
    "Niš, Bore Đorđevića - Kokana",
    "Niš, Bosanska",
    "Niš, Vojvode Vuka",
    "Niš, D.Tucovića",
    "Niš, Davidova",
    "Niš, Episkopska",
    "Niš, Zetska",
    "Niš, Janka Veselinovića",
    "Niš, Koče Kapetana",
    "Niš, Ljutice Bogdana",
    "Niš, Milunke Savić",
    "Niš, Olivere Jocić",
    "Subotica, 15.aprila",
    "Subotica, 300.Nova",
    "Subotica, Alekse Kokića",
    "Subotica, Bore Stankovića",
    "Subotica, Braće Bradić",
    "Subotica, Gornja",
    "Subotica, Dimitrija Tucovića",
    "Subotica, Žarka Vasiljevića",
    "Subotica, Kajmakčalanska",
    "Subotica, Lepenička",
    "Subotica, Matka Vukovića"
]


def generateRandomOrder(seller_id, users_ids, simulated_products):
    new_order = order.Order()
    #Buyer details
    new_order.buyerId = random.choice(users_ids)
    new_order.buyerLongitude = round(random.uniform(LONGITUDE_LOWER_LIMIT, LONGITUDE_UPPER_LIMIT), 2)
    new_order.buyerLatitude = round(random.uniform(LATITUDE_LOWER_LIMIT, LATITUDE_UPPER_LIMIT), 2)
    
    #Seller details
    new_order.sellerId = seller_id
    
    #Order details
    new_order.orderStatusId = random.choices(ORDER_STATUS_IDS, probabilities)[0]
    
    shipping_and_payment_method_id = random.choice(ORDER_SHIPPING_AND_PAYMENT_METHODS)
    new_order.shippingMethodId = shipping_and_payment_method_id
    new_order.paymentMethodId = shipping_and_payment_method_id
    
    new_order.phoneNumber = random.choice(PHONE_NUMBERS)
    new_order.address = random.choice(ADDRESSES) + " " + str(random.randint(1, 50)) + " (S)"
    
    #Order items
    new_order.purchase = generateRandomPurchase(simulated_products, new_order.sellerId)
    
    return new_order
    
def generateRandomPurchase(simulated_products, seller_id):
    products = filterSimulatedProductsBySellerId(simulated_products, seller_id)
    
    purchase = []
    for i in range(random.randint(1, len(products))):
        orderItem = order.SimulatorOrderItemsRequest()
        orderItem.productId = random.choice(products)
        orderItem.quantity = random.randint(1, 3)
        purchase.append(orderItem)
        
    return purchase
        
def filterSimulatedProductsBySellerId(simulated_products, seller_id):
    return [
        product["product_id"]
        for product in simulated_products
        if product["seller_id"] == seller_id
    ]

    

    