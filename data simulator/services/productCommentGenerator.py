import random
from models import productComment

class ProductCommentGenerator:
    def __init__(self):
        fileComments = open('src/product_comments.txt', mode='r', encoding='utf-8-sig')
        lines = fileComments.readlines()
        self.grades = lines[0::2]
        self.comments = lines[1::2]
        

    def generateRandomProductComment(self, product_id, simulated_users):
        newProductComment = productComment.ProductComment()
        commentIdNumber = random.randint(0, len(self.grades) - 1)
        
        newProductComment.product_id = product_id
        newProductComment.user_id = random.choice(simulated_users)
        newProductComment.grade = self.grades[commentIdNumber]
        newProductComment.comment = self.comments[commentIdNumber] 
        
        return newProductComment
    
    
    