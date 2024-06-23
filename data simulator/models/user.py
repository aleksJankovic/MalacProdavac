from unidecode import unidecode

class User:
    def __init__(self):
        self.user_id            = None
        self.email              = None
        self.name               = None
        #password = Batmobile123        =       $2a$10$8sr3.9oYAvVnbWoYt/GFwOmMwSWw6PNppYw9oroU5y9FAYwrm8RLq
        self.password           = "Batmobile123"
        self.picture            = None
        self.surname            = None
        self.username           = None
        # user, deliverer, seller 
        self.role_id            = None      

        # roles: deliverer & seller
        self.latitude           = 0
        self.longitude          = 0
        # role: deliverer (id=2)
        self.driving_category   = []
        # role: seller
        self.pib                = ""
        self.accountNumber      = ""

    def __str__(self):

        return f"""
    {{
        "name": "{self.name}",
        "surname": "{self.surname}",
        "username": "{self.username}",
        "password": "{self.password}",
        "email": "{self.email}",
        "picture": "",
        "role": "{self.role_id}",
        "latitude":{self.latitude},
        "longitude":{self.longitude},
        "licenceCategories":{self.driving_category},
        "pib":"{self.pib}", 
        "accountNumber":"{self.accountNumber}"
    }}
"""