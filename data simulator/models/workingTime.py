class WorkingTime:
    def __init__(self):
        self.monday = None
        self.tuesday = None
        self.wednesday = None
        self.thursday = None
        self.friday = None
        self.saturday = None
        self.sunday = None
        
    def __str__(self):
        return f"""
        {{
            "monday": "{self.monday}", 
            "tuesday": "{self.tuesday}", 
            "wednesday": "{self.wednesday}", 
            "thursday": "{self.thursday}", 
            "friday": "{self.friday}",
            "saturday": "{self.saturday}",
            "sunday": "{self.sunday}"
        }}
    """
        