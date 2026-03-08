//FrontEndDuckers/src/CartApp.jsx:

import { Navbar } from "./components/Navbar"; 
import { useItemsCart } from "./hooks/useItemsCart"
import { CartRoutes } from "./routes/CartRoutes";
import { Footer } from "./components/Footer";

export const CartApp = () => {


    const { cartItems, handlerAddProductCart, handlerDeleteProductCart} = useItemsCart();
    
    return ( 
        <>
            <div className="d-flex flex-column min-vh-100">
                <Navbar/> 
                <main className="flex-grow-1"> 
                    <div className="container mt-4 mb-0"> 
                        <CartRoutes
                            cartItems={cartItems} 
                            handlerAddProductCart={handlerAddProductCart}
                            handlerDeleteProductCart={handlerDeleteProductCart}
                        />
                    </div>
                </main>
                <Footer />
            </div>
        </>
    )
}

