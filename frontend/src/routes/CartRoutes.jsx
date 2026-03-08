//FrontEndDuckers/src/routes/CartRoutes.jsx:

import { Navigate, Route, Routes } from "react-router-dom"
import { CartView } from "../components/CartView"
import { CatalogView } from "../components/CatalogView"
import { HomeView } from '../components/HomeView';
import { AccountView } from '../components/AccountView';
import { LoginView } from '../components/LoginView';
import { useAuth } from "../hooks/useAuth";
import { CheckoutView } from "../components/CheckoutView";
import { ProductDetailView } from "../components/ProductDetailView";



export const CartRoutes = ({ handlerAddProductCart, handlerDeleteProductCart, cartItems }) => {
    
    const { isLogged } = useAuth();
    
    return (
        <div className="d-flex flex-column min-vh-100">
        <Routes>
            <Route path="home" element={<HomeView />} />
            <Route path="catalog" element={<CatalogView handler={handlerAddProductCart} />}/>
            <Route path="cart"
            element={(
                cartItems?.length <= 0 ?
                <div className="alert alert-warning">No hay productos en el carrito de compras!</div>
                :
                (
                    <div className="my-4">
                        <CartView items={cartItems} handlerDelete={handlerDeleteProductCart} />
                    </div>
                )
            )}
            />
            
            <Route path="product/:id" element={ 
                <ProductDetailView handler={handlerAddProductCart} />
            } />

            <Route path="account" element={isLogged ? <AccountView /> : <Navigate to="/login" />} />
            <Route path="login" element={<LoginView />} />
            <Route path="/" element={<Navigate to={'/home'} />} />
            <Route path="checkout" element={<CheckoutView />} />
        </Routes>
        </div>
    );
};

