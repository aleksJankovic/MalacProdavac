import requests
from config import java_backend_config
from models.user import User 
import json

base_url = "http://" + java_backend_config["host"] + ":" + java_backend_config["port"] + "/"

def RegisterNewUser(user: User):
    url = base_url + "registration"
    json_user = json.loads(user.__str__())
    response = requests.post(url, json=json_user)
    if response.status_code == 200:
        print(response.json())
        return response.json()
    else:
        print(f"Neuspešan zahtev, status kod: {response.status_code}")

def AddNewProduct(product):
    url = base_url + "simulator/add-product"
    json_product = json.loads(product.__str__())
    response = requests.post(url, json=json_product)
    if response.status_code == 200:
        print(response.json())
        return response.json()
    else:
        print(f"Neuspešan zahtev, status kod: {response.status_code}")
        
def CreateNewOrder(order):
    url = base_url + "simulator/make-order"
    json_order = json.loads(order.__str__())
    
    response = requests.post(url, json=json_order)
    print(response.text)
    return response
        
def CreateNewPost(post):
    url = base_url + "simulator/add-post"
    param = {"sellerId": post.sellerId, "text": post.text}
    
    response = requests.post(url, params=param)
    if response.status_code == 200:
        print("[USPESNO]: Post je uspesno kreiran!")
        return response
    else:
        print(response.text)
        
def CreateNewLike(like):
    url = base_url + "simulator/like-post"
    param = {"postId": like.post_id, "userId": like.user_id}
    
    response = requests.post(url, params=param)
    print(response.text)
    return response
        
def CreateProductComment(productComment):
    url = base_url + "simulator/comment-product"
    param = {"productId": productComment.product_id,
             "userId": productComment.user_id, 
             "grade": productComment.grade,
             "comment": productComment.comment}
    
    response = requests.post(url, params=param)
    print(response.text)
    return response

def CreatePostComment(postComment):
    url = base_url + "simulator/comment-post"
    param = {"postId": postComment.post_id, 
             "userId": postComment.user_id,
             "comment": postComment.comment}
    
    response = requests.post(url, params=param)
    print(response.text)
    return response

def createWorkingTime(sellerId, workingTime):
    url = base_url + "simulator/seller/set-working-time"
    param = {"sellerId": sellerId}
    json_workingTime = json.loads(workingTime.__str__())
    
    response = requests.post(url, params=param, json=json_workingTime)
    print(response.text)
    return response

    
    