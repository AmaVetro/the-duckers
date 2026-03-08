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


describe("useItemsCart", () => {
  it("agrega un producto nuevo con cantidad 1", () => {
    const { result } = renderHook(() => useItemsCart());

    const prod = {
      id: 1,
      name: "Auriculares Force 2",
      description: "Auriculares gamer con micrófono",
      price: 18990,
      image: "/src/img/1.JPG",
    };

    act(() => {
      result.current.handlerAddProductCart(prod);
    });

    const { cartItems } = result.current;
    expect(cartItems.length).toBe(1);
    expect(cartItems[0].product).toEqual(prod);
    expect(cartItems[0].quantity).toBe(1);
  });
});
