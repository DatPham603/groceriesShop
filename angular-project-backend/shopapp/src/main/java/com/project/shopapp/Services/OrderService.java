package com.project.shopapp.Services;

import com.project.shopapp.DTO.CartItemDTO;
import com.project.shopapp.DTO.OrderDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.*;
import com.project.shopapp.Repositories.OrderDetailRepository;
import com.project.shopapp.Repositories.OrderRepository;
import com.project.shopapp.Repositories.ProductRepository;
import com.project.shopapp.Repositories.UserRepository;
import com.project.shopapp.Responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ModelMapper modelMapper;
    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFoundException {
       User user = userRepository.findById(orderDTO.getUserId())
                    .orElseThrow(() -> new DataNotFoundException("Can't find user witd id =" +orderDTO.getUserId()));
        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper -> { // thiết lập quy tắc ánh xạ từ OrderDto sang order
            mapper.skip(Order::setId); // Bỏ qua ánh xạ cho trường id của Order
        });
        Order order = new Order();
        modelMapper.map(orderDTO ,order);
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        LocalDate shippingDate = orderDTO.getShippingDate() == null ? LocalDate.now() : orderDTO.getShippingDate();
        if(shippingDate.isBefore(LocalDate.now())){
            throw new DataNotFoundException("Shipping date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()){
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            Product product = productRepository.findById(cartItemDTO.getProductId()).orElseThrow();
            orderDetail.setProduct(product);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setNumberOfProducts(cartItemDTO.getQuantity());
            orderDetails.add(orderDetail);
        }
        OrderResponse orderResponse = new OrderResponse();
        List<CartItemDTO> cartItemDTOS = new ArrayList<>();
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()){
            cartItemDTOS.add(cartItemDTO);
            orderResponse.setCartItems(cartItemDTOS);
        }
        modelMapper.map(order,orderResponse);
        orderDetailRepository.saveAll(orderDetails);
        return orderResponse;
    }

    @Override
    public OrderResponse getOrder(Long id) throws DataNotFoundException {
        Order orderExisting = orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Cant found order with id = " + id));
        OrderResponse orderResponse = new OrderResponse();
        modelMapper.map(orderExisting, orderResponse);
        return orderResponse;
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException {
        Order orderExisting =  orderRepository.findById(id).orElseThrow(() -> new DataNotFoundException("Can't not found order with id =" + id));
        User userExisting =  userRepository.findById(orderDTO.getUserId()).orElseThrow(() -> new DataNotFoundException("Can't not found user with id =" + orderDTO.getUserId()));
        modelMapper.typeMap(OrderDTO.class, Order.class).addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, orderExisting);
        orderExisting.setUser(userExisting);
        orderRepository.save(orderExisting);
        OrderResponse orderResponse = new OrderResponse();
        modelMapper.map(orderExisting,orderResponse);
        return orderResponse;
    }
    @Override
    public void deleteOrderById(Long id) throws DataNotFoundException {
        Order orderExisting =  orderRepository.findById(id).orElse(null);
        if(orderExisting != null){
            orderExisting.setActive(false);
            orderRepository.save(orderExisting);
        }else {
            throw new DataNotFoundException("Can't found order with id = " + id);
        }
    }
    @Override
    public List<OrderResponse> getAllOrders(Long userId) throws DataNotFoundException {
        userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Can't find user with id =" + userId));
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> orderResponses = new ArrayList<>();
        for(Order order : orders){
            OrderResponse orderResponse = new OrderResponse();
            modelMapper.map(order,orderResponse);
            orderResponses.add(orderResponse);
        }
        return orderResponses;
    }
}
