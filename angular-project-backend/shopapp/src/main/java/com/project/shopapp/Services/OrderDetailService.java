package com.project.shopapp.Services;

import com.project.shopapp.DTO.OrderDetailDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Models.Order;
import com.project.shopapp.Models.OrderDetail;
import com.project.shopapp.Models.Product;
import com.project.shopapp.Models.User;
import com.project.shopapp.Repositories.OrderDetailRepository;
import com.project.shopapp.Repositories.OrderRepository;
import com.project.shopapp.Repositories.ProductRepository;
import com.project.shopapp.Repositories.UserRepository;
import com.project.shopapp.Responses.OrderDetailResponse;
import com.project.shopapp.Responses.OrderResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailService implements IOrderDetailService{
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Cant found order with id = " + orderDetailDTO.getOrderId()));
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Cant found product with id = " + orderDetailDTO.getOrderId()));
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrder(order);
        orderDetail.setProduct(product);
        orderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        orderDetail.setPrice(product.getPrice());
        Float totalMoney = product.getPrice() * orderDetail.getNumberOfProducts();
        orderDetail.setTotalMoney(totalMoney);
        return OrderDetailResponse.fromOrderDetail(orderDetail);
    }

    @Override
    public List<OrderDetailResponse> getAllOrderDetail(Long orderId) throws DataNotFoundException {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order != null){
            return orderDetailRepository.findByOrderId(orderId).stream().map(OrderDetailResponse::fromOrderDetail).toList();
        }else {
            throw new DataNotFoundException("Can't found order with id = " + orderId);
        }
    }

    @Override
    public OrderDetailResponse getOrderDetailById(Long orderDetailId) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailRepository.findById(orderDetailId)
                .orElseThrow(() -> new DataNotFoundException("Can't found order detail with id = " + orderDetailId));
        return OrderDetailResponse.fromOrderDetail(orderDetail);
    }

    @Override
    public OrderDetailResponse updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        OrderDetail orderDetailExisting = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Can't found order detail with id = " + id));
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Can't found product with id = " + orderDetailDTO.getProductId()));
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Can't found order with id = " + orderDetailDTO.getOrderId()));
        orderDetailExisting.setProduct(product);
        orderDetailExisting.setOrder(order);
        orderDetailExisting.setPrice(product.getPrice());
        orderDetailExisting.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        Float totalMoney = product.getPrice() * orderDetailExisting.getNumberOfProducts();
        orderDetailExisting.setTotalMoney(totalMoney);
        orderDetailExisting.setColor(orderDetailDTO.getColor());
        orderDetailRepository.save(orderDetailExisting);
        return OrderDetailResponse.fromOrderDetail(orderDetailExisting);
    }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.findById(id).ifPresent(orderDetailRepository::delete);
    }
}
