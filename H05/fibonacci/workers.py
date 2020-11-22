import json

def lambda_handler(event, context):
    f = int(event['f'])
    N = int(event['N'])
    arrays = []
    for i in range(f):
        arrays.append([])
        for j in range(1, N+1, f):
            if i+j <= N:
                arrays[i].append(j+i)
    return {
        'arrays': arrays
    }