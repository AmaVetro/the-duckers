package com.theduckers.backend.service;

import com.theduckers.backend.entity.ShoppingCart;
import com.theduckers.backend.entity.ShoppingCartItem;
import com.theduckers.backend.entity.mongo.ProductDocument;
import com.theduckers.backend.exception.BadRequestException;
import com.theduckers.backend.repository.ShoppingCartItemRepository;
import com.theduckers.backend.repository.ShoppingCartRepository;
import com.theduckers.backend.repository.mongo.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



//test/service/CartServiceTest:



@ExtendWith(MockitoExtension.class)
class CartServiceTest {

        @Mock
        private ShoppingCartRepository shoppingCartRepository;

        @Mock
        private ShoppingCartItemRepository shoppingCartItemRepository;

        @Mock
        private ProductRepository productRepository;

        @InjectMocks
        private CartService cartService;

        private static final Long USER_ID = 1L;
        private static final String PRODUCT_ID = "keyboard-001";

        // -------- helpers --------

        private void setCartId(ShoppingCart cart, Long id) {
                try {
                var field = ShoppingCart.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(cart, id);
                } catch (Exception e) {
                throw new RuntimeException(e);
                }
        }

        // -------- tests --------

        @Test
        void getOrCreateActiveCart_createsCartWhenNoneExists() {
                when(shoppingCartRepository.findAllByUserIdAndStatus(USER_ID, "ACTIVE"))
                        .thenReturn(List.of());

                when(shoppingCartRepository.save(any()))
                        .thenAnswer(invocation -> invocation.getArgument(0));

                ShoppingCart cart = cartService.getOrCreateActiveCart(USER_ID);

                assertThat(cart).isNotNull();
                assertThat(cart.getUserId()).isEqualTo(USER_ID);
                assertThat(cart.getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        void addItem_createsNewItemWhenNotExists() {
                ShoppingCart cart = new ShoppingCart(USER_ID);
                setCartId(cart, 1L);

                ProductDocument product = mock(ProductDocument.class);
                when(product.getId()).thenReturn(PRODUCT_ID);
                when(product.getName()).thenReturn("Mechanical Keyboard");
                when(product.getPrice()).thenReturn(49990L);

                when(shoppingCartRepository.findAllByUserIdAndStatus(USER_ID, "ACTIVE"))
                        .thenReturn(List.of(cart));

                when(productRepository.findById(PRODUCT_ID))
                        .thenReturn(Optional.of(product));

                when(shoppingCartItemRepository.findByCartIdAndProductId(any(), any()))
                        .thenReturn(Optional.empty());

                ShoppingCart result = cartService.addItem(USER_ID, PRODUCT_ID, 2);

                verify(shoppingCartItemRepository).save(any(ShoppingCartItem.class));
                assertThat(result).isSameAs(cart);
        }

        @Test
        void addItem_incrementsQuantityWhenItemExists() {
                ShoppingCart cart = new ShoppingCart(USER_ID);
                setCartId(cart, 1L);

                ShoppingCartItem existingItem =
                        new ShoppingCartItem(cart, PRODUCT_ID, "Mechanical Keyboard", 49990L, 1);

                when(shoppingCartRepository.findAllByUserIdAndStatus(USER_ID, "ACTIVE"))
                        .thenReturn(List.of(cart));

                when(productRepository.findById(PRODUCT_ID))
                        .thenReturn(Optional.of(mock(ProductDocument.class)));

                when(shoppingCartItemRepository.findByCartIdAndProductId(any(), any()))
                        .thenReturn(Optional.of(existingItem));

                cartService.addItem(USER_ID, PRODUCT_ID, 2);

                assertThat(existingItem.getQuantity()).isEqualTo(3);
                verify(shoppingCartItemRepository).save(existingItem);
        }

        @Test
        void addItem_throwsExceptionWhenQuantityInvalid() {
                assertThatThrownBy(() ->
                        cartService.addItem(USER_ID, PRODUCT_ID, 0)
                ).isInstanceOf(BadRequestException.class);
        }

        @Test
        void updateItemQuantity_updatesQuantity() {
                ShoppingCart cart = new ShoppingCart(USER_ID);
                setCartId(cart, 1L);

                ShoppingCartItem item =
                        new ShoppingCartItem(cart, PRODUCT_ID, "Mechanical Keyboard", 49990L, 1);

                when(shoppingCartRepository.findAllByUserIdAndStatus(USER_ID, "ACTIVE"))
                        .thenReturn(List.of(cart));

                when(shoppingCartItemRepository.findById(1L))
                        .thenReturn(Optional.of(item));

                ShoppingCart result = cartService.updateItemQuantity(USER_ID, 1L, 3);

                assertThat(item.getQuantity()).isEqualTo(3);
                verify(shoppingCartItemRepository).save(item);
                assertThat(result).isSameAs(cart);
        }

        @Test
        void removeItem_deletesItem() {
                ShoppingCart cart = new ShoppingCart(USER_ID);
                setCartId(cart, 1L);

                ShoppingCartItem item =
                        new ShoppingCartItem(cart, PRODUCT_ID, "Mechanical Keyboard", 49990L, 1);

                when(shoppingCartRepository.findAllByUserIdAndStatus(USER_ID, "ACTIVE"))
                        .thenReturn(List.of(cart));

                when(shoppingCartItemRepository.findById(1L))
                        .thenReturn(Optional.of(item));

                ShoppingCart result = cartService.removeItem(USER_ID, 1L);

                verify(shoppingCartItemRepository).delete(item);
                assertThat(result).isSameAs(cart);
        }
}
