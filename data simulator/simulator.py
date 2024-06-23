from connection                         import Connection
from services.userGenerator             import UserGenerator
from api                                import RegisterNewUser, AddNewProduct, CreateNewOrder, CreateNewPost, CreateNewLike, CreateProductComment, CreatePostComment, createWorkingTime
import random
from services.productGenerator          import SimulateProducts
from services.orderGenerator            import generateRandomOrder
from services.postGenerator             import generateRandomPost
from services.postLikeGenerator         import generateRandomLikes
from services.productCommentGenerator   import ProductCommentGenerator
from services.postCommentGenerator      import PostCommentGenerator
from services.workingTimeGenerator      import generateRandomWorkingTime

NUM_OF_USERS        = 30
NUM_OF_DELIVERERS   = 10
NUM_OF_SELLERS      = 15

def simulateUsers():
    # ROLE: user
    simulated_users = []
    for i in range(1,(NUM_OF_USERS+1)):
        new_user = user_generator.generateRandomUser(i, "user")
        # db_connection.addUser(new_user.name, new_user.surname, new_user.username, new_user.password, new_user.email, new_user.picture, 1)
        response = RegisterNewUser(new_user)
        simulated_users.append(response["data"]["id"])

    # ROLE: deliverer
    for i in range(1,(NUM_OF_DELIVERERS+1)):
        new_user = user_generator.generateRandomUser(i, "deliverer")
        # db_connection.addUser(new_user.name, new_user.surname, new_user.username, new_user.password, new_user.email, new_user.picture, 1)
        response = RegisterNewUser(new_user)
        simulated_users.append(response["data"]["id"])

    # ROLE: seller
    simulated_sellers = []
    for i in range(1,(NUM_OF_SELLERS+1)):
        new_user = user_generator.generateRandomUser(i, "seller")
        # db_connection.addUser(new_user.name, new_user.surname, new_user.username, new_user.password, new_user.email, new_user.picture, 1)
        response = RegisterNewUser(new_user)
        simulated_sellers.append(response["data"]["id"])
        simulated_users.append(response["data"]["id"])
    return simulated_sellers, simulated_users

NUM_OF_PRODUCTS = 327
def simulateProducts(list_sellers_id):
    simulated_products = []
    len_list_sellers_id = len(list_sellers_id)
    for i in range(0, (NUM_OF_PRODUCTS + 1)):
        random_seller       = random.randrange(0, len_list_sellers_id)
        random_seller_id    = list_sellers_id[random_seller]
        # db_connection.addNewProduct(list_sellers_id[random_seller])
        new_product = SimulateProducts(random_seller_id)
        response = AddNewProduct(new_product)
        simulated_products.append({
            "seller_id": new_product.seller_id, 
            "product_id": response["data"]
        })
    return simulated_products

def simulateOrders(simulated_sellers, simulated_users, simulated_products):
    simulated_orders = []
    for i in range(1, len(simulated_sellers)):
        numberOfOrders =  random.randint(5, 12)
        for j in range(numberOfOrders):
            order = generateRandomOrder(simulated_sellers[i], simulated_users, simulated_products)
            response = CreateNewOrder(order)
            
        
        
def simulatePosts(simulated_sellers):
    #Citanje podataka iz tekstualnog izvora
    filePosts = open('src/post.txt', mode='r', encoding='utf-8-sig')
    posts = filePosts.readlines()
    posts = [post.strip() for post in posts]
    filePosts.close()
    
    simulated_posts = []
    for i in range(len(simulated_sellers)):
        seller_id = simulated_sellers[i]  #Ovom prodavcu
        available_posts = posts.copy()
        numberOfPost = random.randint(2, 10)  #Dodeli random broj postova
        for j in range(numberOfPost):
            post, available_posts = generateRandomPost(seller_id, available_posts)
            response = CreateNewPost(post)
            simulated_posts.append(response.text)
            #Ukoliko je broj objava veci od broja dostupnim objava za izbor
            if len(available_posts) == 0:
                break
    return simulated_posts
            
def simulateLikesOnPost(simulated_users, simulated_posts):
    for i in range (len(simulated_posts)):
        #Za svaku objavu generisi random broj lajkova
        numberOfLikes = random.randint(3, len(simulated_users))
        for j in range (numberOfLikes):
            like = generateRandomLikes(simulated_posts[i], simulated_users)
            response = CreateNewLike(like)
            
def simulateCommentsOnProducts(simulated_products, simulated_users):
    product_ids = [product["product_id"] for product in simulated_products]
    for i in range(len(product_ids)):
        product_id = product_ids[i]
        #Za svaki proizvod, generisi random broj komentara
        numberOfComments = random.randint(0, len(simulated_users) // 2)
        for j in range (numberOfComments):
            comment = product_comment_generator.generateRandomProductComment(product_id, simulated_users)
            response = CreateProductComment(comment)
            
def simulateCommentsOnPosts(simulated_posts, simulated_users):
    for i in range(len(simulated_posts)):
        post_id = simulated_posts[i]
        #Za svaku objavu, generisi random broj komentara
        numberOfComments = random.randint(0, len(simulated_users) // 2)
        for j in range(numberOfComments):
            comment = post_comment_generator.generateRandomPostComment(post_id, simulated_users)
            response = CreatePostComment(comment)
            
def simulateSellersWorkingTime(simulated_sellers):
    for i in range(len(simulated_sellers)):
        sellerId = simulated_sellers[i]
        workingTime = generateRandomWorkingTime()
        response = createWorkingTime(sellerId, workingTime)

        
# SIMULATOR
# =====================================================
db_connection = Connection()
user_generator = UserGenerator()
product_comment_generator = ProductCommentGenerator()
post_comment_generator = PostCommentGenerator()

# !!!!!!! WARNING !!!!!!! 
db_connection.deleteAllDataWithoutStaticData()
db_connection.addStaticData()

simulated_sellers, simulated_users = simulateUsers()
simulated_products = simulateProducts(simulated_sellers)
db_connection.addNewFollower(simulated_sellers, simulated_users)
simulateOrders(simulated_sellers, simulated_users, simulated_products)
simulated_posts = simulatePosts(simulated_sellers)
simulateLikesOnPost(simulated_users, simulated_posts)
simulateCommentsOnProducts(simulated_products, simulated_users)
simulateCommentsOnPosts(simulated_posts, simulated_users)
simulateSellersWorkingTime(simulated_sellers)


