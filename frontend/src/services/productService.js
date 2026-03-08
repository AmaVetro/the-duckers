//FrontEndDuckers/src/services/productService.js:

const API_BASE = import.meta.env.VITE_API_URL;
const API_URL = `${API_BASE}/products`;



export const getProducts = async () => {

    const response = await fetch(API_URL); 

    if (!response.ok) { 
        throw new Error('Error al obtener los productos');
    }

    const products = await response.json(); 

    return products; 
};




export const getProductById = async (id) => { 
    const response = await fetch(`${API_URL}/${id}`); 

    if (response.status === 404) { 
        return null; 
    }

    if (!response.ok) { 
        throw new Error('Error al obtener el producto');
    }


    const product = await response.json();

    return product; 
};



export const calculateTotal = (items) => { 
    return items.reduce( 
        (accumulator, item) => accumulator + item.product.price * item.quantity,
        0
    );
};

