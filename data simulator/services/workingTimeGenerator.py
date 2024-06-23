from models import workingTime
import random

times = ["09-15H", "09-16H", "10-20H", "14-16H", "16-20H", "13-20H", "09-20H", "10-14H", "ZATVORENO"]

def generateRandomWorkingTime():
    new_workingTime = workingTime.WorkingTime()
    
    duringWeekTime = random.choice(times)
    weekendTime = random.choice(times)
    
    new_workingTime.monday = new_workingTime.tuesday = new_workingTime.wednesday = new_workingTime.thursday = new_workingTime.friday = duringWeekTime
    new_workingTime.saturday = new_workingTime.sunday = weekendTime
    
    return new_workingTime
    
    