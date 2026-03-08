//FrontEndDuckers/src/components/CatalogView.jsx:

import { useEffect, useState } from "react";
import { getProducts } from "../services/productService";
import { ProductCardView } from "./ProductCardView";



export const CatalogView = ({ handler }) => {

    const [products, setProducts] = useState([]);
    const [query, setQuery] = useState("");
    const [isLoading, setIsLoading] = useState(true);



    useEffect(() => {
        const loadProducts = async () => {
            try {
                const prods = await getProducts(); 
                setProducts(prods); 
            } catch (error) {
                console.error('Error cargando productos', error); 
            } finally {
                setIsLoading(false); 
            }
        };
        loadProducts();
    }, []);



    const onSubmit = (e) => {
        e.preventDefault();
    };



    return (
        <>
            <div className="d-flex align-items-center justify-content-between mb-3">
                <h3 className="m-0">Catálogo</h3>
                <form
                    className="d-flex"
                    role="search"
                    onSubmit={(e) => {e.preventDefault(); alert("Demo");}}
                    style={{ flex: 1, maxWidth: "600px", marginLeft: "20px" }}
                >
                    <input
                        className="form-control form-control-lg me-2 rounded-pill px-4"
                        type="search"
                        placeholder="🔍 Buscar productos, marcas o categorías..."
                        aria-label="Buscar"
                        value={query}
                        onChange={(e) => setQuery(e.target.value)}
                    />
                    <button className="btn btn-primary rounded-pill px-4" type="submit">
                        Buscar
                    </button>
                </form>
            </div>

            {isLoading && (
                <div className="alert alert-info">Cargando productos...</div>
            )}



            {/* error al usuario */}
            {!isLoading && products.length === 0 && (
                <div className="alert alert-warning">
                    No se encontraron productos disponibles.
                </div>
            )}



            <div className="row">
                {products.map((prod) => (
                    <div className="col-4 my-2" key={prod.id}>
                        <ProductCardView
                            handler={handler}
                            id={prod.id}
                            name={prod.name}
                            description={prod.description}
                            price={prod.price}
                            image={prod.image}  
                        />
                    </div>
                ))}
            </div>
        </>
    );
};
