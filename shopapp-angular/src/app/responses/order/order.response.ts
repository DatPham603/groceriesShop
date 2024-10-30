import { OrderDetail } from "src/app/models/order.detail";
export interface OrderResponse {
    id: number;
    user_id: number;
    full_name: string;
    email: string;
    phone_number: string;
    address: string;
    note: string;
    total_money: number;
    shipping_method: string;
    shipping_address: string;
    payment_method: string;
    order_details: OrderDetail[]; // Đảm bảo có một interface OrderDetail tương ứng
  }
  
  