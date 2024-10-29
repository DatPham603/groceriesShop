package com.project.shopapp.Controllers;

import com.project.shopapp.DTO.OrderDetailDTO;
import com.project.shopapp.Exception.DataNotFoundException;
import com.project.shopapp.Services.IOrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/order_detail")
@RequiredArgsConstructor
public class OrderDetailController {
    private final IOrderDetailService orderDetailService;
    @PostMapping
    public ResponseEntity<?> createOrderDetail(@RequestBody OrderDetailDTO newOrderDetail) throws DataNotFoundException {
        return ResponseEntity.ok(orderDetailService.createOrderDetail(newOrderDetail));
    }
    @GetMapping("{id}") // lấy ra orderDetail từ 1 cái id
    public ResponseEntity<?> getOrderDetail(@PathVariable("id") Long id) throws DataNotFoundException {
            return ResponseEntity.ok().body(orderDetailService.getOrderDetailById(id));
    }
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable("orderId") Long orderId) throws DataNotFoundException {
        return ResponseEntity.ok().body(orderDetailService.getAllOrderDetail(orderId));
    }
    @PutMapping("{id}")
    public ResponseEntity<?> updateOrderDetail(@PathVariable("id") Long orderDetailId,
                                               @RequestBody OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        return ResponseEntity.ok().body(orderDetailService.updateOrderDetail(orderDetailId,orderDetailDTO));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetailById(@PathVariable("id") Long orderDetailId){
        orderDetailService.deleteOrderDetail(orderDetailId);
        return ResponseEntity.ok().body("Delete order detail with id = " + orderDetailId + "sucessfull");
    }
}
