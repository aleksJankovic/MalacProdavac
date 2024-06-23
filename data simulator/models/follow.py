class Follow:
    def __init__(self):
        self.seller_id = None
        self.user_id = None
        
    def __str__(self):
        return f"""
        {
            "seller_id" : "{self.seller_id}", 
            "user_id" : "{self.user_id}"
        }
    """