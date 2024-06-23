import random
from models import like

def generateRandomLikes(post_id, users_ids):
    generatedLike = like.Like()
    generatedLike.post_id = post_id
    generatedLike.user_id = random.choice(users_ids)
    
    return generatedLike
    
        