package com.project.shopapp.Services;

import com.project.shopapp.DTO.OrderDetailDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.OrderDetail;
import com.project.shopapp.Responses.OrderDetailResponse;

import java.util.List;

public interface IOrderDetailService {
    OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException;
    List<OrderDetailResponse> getAllOrderDetail(Long orderId) throws DataNotFoundException;
    OrderDetailResponse getOrderDetailById(Long orderDetailId) throws DataNotFoundException;
    OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException;
    void deleteOrderDetail(Long id);
}
