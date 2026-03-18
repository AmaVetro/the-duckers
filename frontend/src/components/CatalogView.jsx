//frontend/src/components/CatalogView.jsx:

import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { getProducts } from "../services/productService";
import { ProductCardView } from "./ProductCardView";
import { getCategories } from "../services/categoryService";



export const CatalogView = ({ handler }) => {

    const location = useLocation();
    const navigate = useNavigate();
    const params = new URLSearchParams(location.search);
    const categoryFilter = params.get("category");



    const [products, setProducts] = useState([]);
    const [queryText, setQueryText] = useState("");
    const [minPrice, setMinPrice] = useState("");
    const [maxPrice, setMaxPrice] = useState("");
    const [category, setCategory] = useState("");
    const [categories, setCategories] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    



    useEffect(() => {

        const loadData = async () => {

            try {

                setIsLoading(true);

                // sync category state with URL
                if (categoryFilter) {
                    setCategory(categoryFilter);
                }

                const prods = await getProducts({
                    text: "",
                    category: categoryFilter || "",
                    minPrice: "",
                    maxPrice: ""
                });

                setProducts(prods);

            } catch (error) {

                console.error("Error loading products", error);

            } finally {

                setIsLoading(false);

            }

        };

        const loadCategories = async () => {

            try {

                const cats = await getCategories();

                setCategories(cats);

            } catch (error) {

                console.error("Error loading categories", error);

            }

        };

        loadData();
        loadCategories();

    }, [categoryFilter]);



    const onSubmit = async (e) => {

        e.preventDefault();

        try {

            setIsLoading(true);

            const filteredProducts = await getProducts({
                text: queryText,
                category,
                minPrice,
                maxPrice
            });

            setProducts(filteredProducts);

        } catch (error) {

            console.error("Error filtering products", error);

        } finally {

            setIsLoading(false);

        }

    };



    return (
        <>
            <div className="d-flex align-items-center justify-content-between mb-3">
                <h3 className="m-0">Catálogo</h3>
                <form
                    className="d-flex"
                    role="search"
                    onSubmit={onSubmit}
                    style={{ flex: 1, maxWidth: "600px", marginLeft: "20px" }}
                >
                    <input
                        className="form-control me-2 rounded-pill px-4"
                        type="search"
                        placeholder="🔍 Busca productos o marcas..."
                        aria-label="Buscar"
                        value={queryText}
                        onChange={(e) => setQueryText(e.target.value)}
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


            {/* Price Filters */}
            <div className="row mb-3 align-items-end">
                <div className="col-md-3">
                    <input
                        type="number"
                        className="form-control"
                        placeholder="Precio mínimo"
                        value={minPrice}
                        onChange={(e) => setMinPrice(e.target.value)}
                    />
                </div>
                <div className="col-md-3">
                    <input
                        type="number"
                        className="form-control"
                        placeholder="Precio máximo"
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                    />
                </div>
                <div className="col-md-1">

                    <button
                        className="btn btn-primary"
                        style={{ height: "38px", padding: "0 16px" }}
                        onClick={onSubmit}
                    >
                        Aplicar
                    </button>
                </div>
            </div>



            {/* Category Filter */}
            <div className="row mb-3">
                <div className="col-md-3">
                    <select
                        className="form-select"
                        value={category}
                        onChange={(e) => {
                            const newCategory = e.target.value;

                            setCategory(newCategory);

                            // sync URL with state
                            const params = new URLSearchParams(location.search);

                            if (newCategory) {
                                params.set("category", newCategory);
                            } else {
                                params.delete("category");
                            }

                            navigate(`/catalog?${params.toString()}`);
                        }}
                    >
                        <option value="">
                            Todas las categorías
                        </option>
                        {categories.map((cat) => (
                            <option key={cat.id} value={cat.id}>
                                {cat.name}
                            </option>
                        ))}
                    </select>
                </div>
            </div>


            <div className="row">
                {products.map((prod) => (
                        <div className="col-4 my-2" key={prod.id}>
                            <ProductCardView
                                handler={handler}
                                id={prod.id}
                                name={prod.name}
                                description={prod.description}
                                price={prod.price}
                                image={prod.images?.[0]}  
                                stock={prod.stock}
                            />
                        </div>
                ))}
            </div>
        </>
    );
};
