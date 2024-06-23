import random
from unidecode import unidecode
from models.user import User

class UserGenerator:
    def __init__(self):
        self.__load_all_data()

    def __load_all_data(self):
        fileNames       = open('src/name.txt', mode='r', encoding='utf-8-sig')
        fileSurnames    = open('src/surname.txt', mode='r', encoding='utf-8-sig')
        names           = fileNames.readlines()
        surnames        = fileSurnames.readlines()
        self.names      = [name.strip() for name in names]
        self.surnames   = [surname.strip() for surname in surnames]

    def __get_random_coordinates(self):
        min_latitude, max_latitude = 42.0, 45.0
        min_longitude, max_longitude = 19.0, 22.5

        random_latitude = round(random.uniform(min_latitude, max_latitude), 6)
        random_longitude = round(random.uniform(min_longitude, max_longitude), 6)

        return random_latitude, random_longitude
        
    def __get_list_of_random_vehicles(self):
        num_of_vehicles = random.randrange(0,5)
        set_of_vehicles = set()
        for i in range(num_of_vehicles):
            set_of_vehicles.add(random.randrange(1,5))
        return list(set_of_vehicles)
    
    def generateRandomAccountNumber(self):
        firstNumbers = ["120", "340", "250", "217", "320"]
        
        accountNumber = random.choice(firstNumbers) + "-" + str(random.randint(1000000000000, 9999999999999)) + "-" + str(random.randint(10, 100)) + "(S)"
        return accountNumber

    def generateRandomUser(self, user_id, role):
        new_user = User()
        new_user.user_id    = user_id
        new_user.name       = random.choice(self.names)
        new_user.surname    = random.choice(self.surnames)
        new_user.email      = unidecode(new_user.name.lower() + "." + new_user.surname.lower() + "@gmail.com")
        new_user.picture    = ""
        new_user.username   = new_user.name.lower() + "." + new_user.surname.lower()    
        new_user.role_id    = role
        if role == "deliverer":
            new_user.latitude, new_user.longitude   = self.__get_random_coordinates()
            new_user.driving_category               = self.__get_list_of_random_vehicles()
        if role == "seller":
            new_user.latitude, new_user.longitude = self.__get_random_coordinates()
            new_user.pib                          = random.randrange(1000000000, 9999999999)
            new_user.accountNumber = self.generateRandomAccountNumber()
        return new_user
    
    
