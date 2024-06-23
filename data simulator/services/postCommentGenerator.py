import random
from models import postComment

class PostCommentGenerator:
    def __init__(self):
        fileComments = open('src/post_comments.txt', mode='r', encoding='utf-8-sig')
        lines = fileComments.readlines()
        self.comments = [comment.strip() for comment in lines]
        
    def generateRandomPostComment(self, post_id, simulated_users):
        newPostComment = postComment.PostComment()
        
        newPostComment.post_id = post_id
        newPostComment.user_id = random.choice(simulated_users)
        newPostComment.comment = random.choice(self.comments)   
             
        return newPostComment
        