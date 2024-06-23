import random
from models import post 

def generateRandomPost(seller_id, posts):
    #Definisanje objave 
    generatedPost = post.Post()
    generatedPost.sellerId = seller_id
    
    post_index = 0
    if len(posts) > 0:
        post_index = random.randint(0, len(posts) - 1)
    generatedPost.text = posts[post_index]
    posts.pop(post_index)
    
    return generatedPost, posts
    
    
