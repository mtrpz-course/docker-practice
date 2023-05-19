import numpy as np
from fastapi import APIRouter

router = APIRouter()


@router.get('')
def hello_world() -> dict:
    return {'msg': 'Hello, World!'}

@router.get('/matrix')
def matrix() -> dict:
    matrix_a = np.random.rand(10, 10)
    matrix_b = np.random.rand(10, 10)

    product = np.dot(matrix_a, matrix_b)

    result = {
        "matrix_a": matrix_a.tolist(),
        "matrix_b": matrix_b.tolist(),
        "product": product.tolist()
    }

    return result
