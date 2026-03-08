import { describe, it, expect, beforeAll, beforeEach } from "vitest";
import { renderHook, act } from "@testing-library/react";

let useItemsCart;


beforeAll(async () => {
  const store = {};
  global.sessionStorage = {
    getItem: (k) => (k in store ? store[k] : null),
    setItem: (k, v) => { store[k] = String(v); },
    removeItem: (k) => { delete store[k]; },
    clear: () => { for (const k in store) delete store[k]; },
  };


  ({ useItemsCart } = await import("../useItemsCart"));
});

beforeEach(() => {
  sessionStorage.clear();
});

// TEST
describe("useItemsCart - eliminar producto", () => {
  it("elimina un producto del carrito por su id", () => {
    const { result } = renderHook(() => useItemsCart());

    // Mock
    const prod = {
      id: 10,
      name: "Silla Gamer Comfy Throne 3",
      description: "Modo ergonómico ajustable",
      price: 84990,
      image: "/src/img/5.JPG",
    };

    act(() => {
      result.current.handlerAddProductCart(prod);
    });

    expect(result.current.cartItems.length).toBe(1);

    act(() => {
      result.current.handlerDeleteProductCart(10);
    });

    expect(result.current.cartItems.length).toBe(0);
  });
});
