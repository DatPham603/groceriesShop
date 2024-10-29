package com.project.shopapp.Services;

import com.project.shopapp.DTO.OrderDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.Order;
import com.project.shopapp.Responses.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderDTO orderDTO) throws DataNotFoundException;
    OrderResponse getOrder(Long id) throws DataNotFoundException;
    OrderResponse updateOrder(Long id, OrderDTO orderDTO) throws DataNotFoundException;
    void deleteOrderById(Long id) throws DataNotFoundException;
    List<OrderResponse> getAllOrders(Long userId) throws DataNotFoundException;
}
