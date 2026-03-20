//frontend/src/components/HomeView.jsx

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProducts } from "../services/productService";
import { useAuth } from "../hooks/useAuth";
import { getCategories } from "../services/categoryService";



export const Slider = ({ slides = [], interval = 10000 }) => {

    const navigate = useNavigate(); 
    const [index, setIndex] = useState(0); 
    const [direction, setDirection] = useState("right"); 

    useEffect(() => { 
        if (!slides || slides.length === 0) return; 

        const id = setInterval(() => { 
            setDirection("right");
            setIndex((i) => (i + 1) % slides.length);  
        }, interval); 

        return () => clearInterval(id); 
    }, [slides, interval]); 


    const prev = () => {
        setDirection("left"); 
        setIndex((i) => (i - 1 + slides.length) % slides.length); 
    };

    const next = () => {
        setDirection("right"); 
        setIndex((i) => (i + 1) % slides.length);  
    }; 

    if (!slides || slides.length === 0) return null;

    const currentSlide = slides[index];

    return (

        <div className="home-slider mb-4 position-relative">
            <div
                className="d-flex justify-content-center align-items-center"
                style={{ height: '420px', overflow: 'hidden' }}
            >
                <img
                    key={index}
                    src={currentSlide.image}
                    alt={`slide-${index}`} 
                    className={`img-fluid slider-img slide-${direction}`}
                />
            </div>

            <div className="slider-overlay">
                <h5 className="mb-1">{currentSlide.title}</h5>
                <p className="mb-2 small">
                    {currentSlide.description}
                </p>
                <button
                    className="btn btn-sm btn-transparent"
                    type="button" 
                    onClick={() => navigate(`/product/${currentSlide.id}`)}
                >
                    Ver detalles
                </button>
            </div>

            <button className="btn btn-dark slider-btn left" onClick={prev} aria-label="prev">
                ❮
            </button>

            <button className="btn btn-dark slider-btn right" onClick={next} aria-label="next">
                ❯
            </button>

        </div>
    );
};



const CategoryCard = ({ title, description, image, categoryId }) => {

    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/catalog?category=${categoryId}`);
    };

    return (
        <div className="card h-100 category-card" onClick={handleClick} style={{ cursor: "pointer" }}>
            <img
                src={image}
                className="card-img-top"
                alt={title}
                style={{ height: '160px', objectFit: 'cover' }}
            />
            <div className="card-body">
                <h5 className="card-title">{title}</h5>
                <p className="card-text">{description}</p>
            </div>
        </div>
    );
};



export const HomeView = () => {

    const { user, isLogged } = useAuth(); 
    const displayName = user?.name || user?.username || "";

    const navigate = useNavigate();

    const categoryImages = {
        controllers: "/images/categories/controllers.png",
        monitors: "/images/categories/monitors.png",
        mousepads: "/images/categories/mousepads.png",
        keyboards: "/images/categories/keyboards.png",
        mice: "/images/categories/mice.png",
        headsets: "/images/categories/headsets.png"
    };

    const sliderImages = {
        "gaming-mouse-razer-deathadder-essential": "/images/slider/razerdeathader.png",
        "mousepad-logitech-g240": "/images/slider/Logitech G240 Cloth Mouse Pad.png",
        "controller-8bitdo-pro-2-wired": "/images/slider/8bitdo.png",
        "gaming-monitor-aoc-24g2": "/images/slider/ACO 24 G2 Monitor.png"
    };

    const sliderProductIds = [
        "gaming-mouse-razer-deathadder-essential",
        "mousepad-logitech-g240",
        "controller-8bitdo-pro-2-wired",
        "gaming-monitor-aoc-24g2"
    ];

    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [showColdStart, setShowColdStart] = useState(false);
    const [hasLoadedOnce, setHasLoadedOnce] = useState(false);
    const [isBackendReady, setIsBackendReady] = useState(false);



    const loadCategories = async () => {

        try {

            const cats = await getCategories();

            setCategories(cats);

        } catch (error) {

            console.error("Error loading categories", error);

        }

    };



    useEffect(() => {

        let coldStartTimer;

        const loadProducts = async () => {

            try {

                // SOLO en la primera carga del sitio
                if (!hasLoadedOnce) {

                    coldStartTimer = setTimeout(() => {
                        setShowColdStart(true);
                    }, 2500); // 2.5 segundos

                }

                const prods = await getProducts();

                setProducts(prods);
                setHasLoadedOnce(true);

            } catch (error) {

                console.error('Error cargando productos en HomeView', error);

            } finally {

                clearTimeout(coldStartTimer);

                // Si estaba mostrando cold start → mostramos check verde
                if (showColdStart) {

                    setIsBackendReady(true);

                    setTimeout(() => {
                        setShowColdStart(false);
                        setIsBackendReady(false);
                    }, 1000); // duración del "check visible"

                } else {
                    setShowColdStart(false);
                }

                setIsLoading(false);

            }

        };

        loadProducts();
        loadCategories();

        return () => {
            clearTimeout(coldStartTimer);
        };

    }, []);




    const slides = sliderProductIds
        .map((id) => products.find((p) => p.id === id))
        .filter(Boolean)
        .map((p) => ({
            id: p.id,
            image: sliderImages[p.id],
            title: p.name,
            description: p.description
        }));

    return (
        <div className="d-flex flex-column" style={{ minHeight: '100%' }}>

            <div className="flex-grow-1 pb-5">

                {isLogged && displayName && (
                    <h3>Bienvenido, {displayName}</h3>
                )}

                {isLoading && !showColdStart && (
                    <div className="alert alert-info my-2">
                        Cargando productos...
                    </div>
                )}

                {showColdStart && (
                    <div className="d-flex justify-content-center my-4">
                        <div
                            className="d-flex align-items-center gap-3 px-4 py-3 rounded shadow-sm"
                            style={{
                                backgroundColor: "#2a2a2a",
                                border: "1px solid #444",
                                maxWidth: "420px"
                            }}
                        >

                            {!isBackendReady ? (
                                <div
                                    className="spinner-border text-light"
                                    role="status"
                                    style={{ width: "1.5rem", height: "1.5rem" }}
                                >
                                    <span className="visually-hidden">Loading...</span>
                                </div>
                            ) : (
                                <div
                                    style={{
                                        width: "1.5rem",
                                        height: "1.5rem",
                                        borderRadius: "50%",
                                        backgroundColor: "#28a745",
                                        display: "flex",
                                        alignItems: "center",
                                        justifyContent: "center",
                                        color: "white",
                                        fontSize: "1rem",
                                        fontWeight: "bold"
                                    }}
                                >
                                    ✓
                                </div>
                            )}

                            <div style={{ fontSize: "14px", color: "#e0e0e0" }}>
                                {!isBackendReady ? (
                                    <>
                                        Inicializando servidor...<br />
                                        <span style={{ fontSize: "12px", color: "#aaa" }}>
                                            Esto puede tardar unos segundos la primera vez
                                        </span>
                                    </>
                                ) : (
                                    <>
                                        Servidor listo<br />
                                        <span style={{ fontSize: "12px", color: "#aaa" }}>
                                            Cargando contenido...
                                        </span>
                                    </>
                                )}
                            </div>

                        </div>
                    </div>
                )}
                <div className="text-center mb-3">
                    <div className="home-hero-text">
                        Conviértete en un gamer especializado.
                    </div>
                </div>
                <Slider slides={slides} interval={11000} />


                <div className="row g-3 mt-3">

                    {categories.map((category) => (

                        <div className="col-md-4" key={category.id}>

                            <CategoryCard
                                title={category.name}
                                description={category.description}
                                image={categoryImages[category.id]}
                                categoryId={category.id}
                            />

                        </div>

                    ))}

                </div>



                <div className="text-center mt-4">

                    <button
                        className="btn btn-outline-primary me-2"
                        onClick={() => navigate('/catalog')}
                    >
                        Ver Catálogo
                    </button>

                    <button
                        className="btn btn-outline-secondary"
                        onClick={() => navigate('/cart')}
                    >
                        Ir al Carrito
                    </button>

                </div>

            </div>

        </div>
    );
};