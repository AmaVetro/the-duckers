//FrontEndDuckers/src/components/HomeView.jsx:

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getProducts } from "../services/productService";
import { useAuth } from "../hooks/useAuth";  




export const Slider = ({ slides = [], interval = []}) => {



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
            onClick={() => navigate(`/product/${currentSlide.id}`)}    //(3)
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




const CategoryCard = ({ title, description, image }) => (
    <div className="card h-100">
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




export const HomeView = () => {


    const { user, isLogged } = useAuth(); 
    const displayName = user?.name || user?.username || "";


    const navigate = useNavigate();
    const [products, setProducts] = useState([]);
    const [isLoading, setIsLoading] = useState(true);


    useEffect(() => {
        const loadProducts = async () => { 
            try {                   
                const prods = await getProducts();
                setProducts(prods);
            } catch (error) {
                console.error('Error cargando productos en HomeView', error);
            } finally {  
                setIsLoading(false);
            }
        };
        loadProducts();
    }, []);




    const featuredIds = [6, 7, 8, 9];
    const slides = products 
        .filter((p) => featuredIds.includes(p.id))
        .map((p) => ({
            id: p.id,
            image: p.image,
            title: p.name,
            description: p.shortDescription, 
    }));                





    const getProductImageById = (id) => {
        const product = products.find((p) => p.id === id);
        return product ? product.image : '/src/img/placeholder.jpg';
    };
    const categoryImages = {
        notebooks: getProductImageById(3),
        monitores: getProductImageById(4),
        desktop: getProductImageById(5),
    };



    return (
        <div className="d-flex flex-column" style={{ minHeight: '100%' }}>
            <div className="flex-grow-1 pb-5">
                {isLogged && displayName && (
                <h3>Bienvenido, {displayName}</h3>
                )}
                {isLoading && (
                <div className="alert alert-info my-2">Cargando productos...</div>
                )}

                
                <Slider slides={slides} interval={11000} />



                <div className="row g-3 mt-3">
                    <div className="col-md-4">
                        <CategoryCard
                        title="VR Sets"
                        description="Dispositivos de realidad virtual diseñados para máxima inmersión y precisión en cada movimiento."
                        image={categoryImages.notebooks}
                        />
                    </div>
                    
                    <div className="col-md-4">
                        <CategoryCard
                        title="Teclados"
                        description="Teclados diseñados para confort y precisión, ideales para escritura, gaming y largas jornadas de uso."
                        image={categoryImages.monitores}
                        />
                    </div>

                    <div className="col-md-4">
                        <CategoryCard
                        title="Sillas Gamer"
                        description="Sillas ergonómicas pensadas para brindar soporte y comodidad durante horas de juego o trabajo."
                        image={categoryImages.desktop}
                        />
                    </div>
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
