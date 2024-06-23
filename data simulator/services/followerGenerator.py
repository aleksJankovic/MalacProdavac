from models.follow import Follow
import random

def generateRandomFollower(sellers_ids, users_ids):
    seller_id = random.choice(sellers_ids)
    user_id = random.choice(users_ids)
    
    return seller_id, user_id

def SimulateFollowers(sellers_ids, users_ids):
    follow_information = generateRandomFollower(sellers_ids, users_ids)
    
    follow = Follow()
    follow.seller_id = follow_information[0]
    follow.user_id = follow_information[1]
    
    return follow